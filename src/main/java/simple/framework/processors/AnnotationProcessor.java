package simple.framework.processors;

import simple.framework.annotations.Init;
import simple.framework.annotations.Service;
import simple.framework.services.LazyService;
import simple.framework.services.SimpleService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class AnnotationProcessor {
    static Map<String, Object> classes = new HashMap<>();

    public static void main(String[] args) {
        inspectService(SimpleService.class);
        inspectService(LazyService.class);

        loadClass("simple.framework.services.SimpleService");
        loadClass("simple.framework.services.LazyService");
        loadClass("java.lang.String");

        initMethods();
    }

    private static void loadClass(String name) {
        try {
            Class<?> aClass = Class.forName(name);
            if(aClass.isAnnotationPresent(Service.class)){
                Object object = aClass.newInstance();
                classes.put(name, object);
                System.out.println("Class successfully created: " + object.getClass().getSimpleName());
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + name);
        } catch (IllegalAccessException | InstantiationException e) {
            System.err.println("Class can't be created: " + name);
        }
    }

    public static void initMethods(){
        classes.values().forEach(
                object -> {
                    Class<?> aClass = object.getClass();
                    for (Method method : aClass.getDeclaredMethods()) {
                        if(method.isAnnotationPresent(Init.class)) {
                            method.setAccessible(true);
                            boolean lazyLoad = aClass.getAnnotation(Service.class).lazyLoad();
                            if(lazyLoad) {
                                new Thread(() -> {
                                    try {
                                        method.invoke(object);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                invokeInternal(object, method);
                            }
                        }
                    }
                }
        );
    }

    private static void invokeInternal(Object object, Method method) {
        try {
            method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Init annotation = method.getAnnotation(Init.class);
            if(annotation.suppressException()) {
                System.err.println("Exception happened in method: " + method.getName());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static void inspectService(Class<?> service) {
        if(service.isAnnotationPresent(Service.class)) {
            Service ann = service.getAnnotation(Service.class);
            System.out.println(ann.name());
            System.out.println(ann.lazyLoad());
        }
        for (Method method : service.getDeclaredMethods()) {
            System.out.print(method.getName() + " : ");
            if(method.isAnnotationPresent(Init.class)){
                System.out.println("found");
            } else {
                System.out.println("not found");
            }
        }

    }
}
