package simple.framework.processors;

import simple.framework.annotations.Init;
import simple.framework.annotations.Service;
import simple.framework.services.LazyService;
import simple.framework.services.SimpleService;

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
    }

    private static void loadClass(String name) {
        try {
            Class<?> aClass = Class.forName(name);
            if(aClass.isAnnotationPresent(Service.class)){
                Object object = aClass.newInstance();
                classes.put(name, object);
                System.out.println("Class successfully created: " + name);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + name);
        } catch (IllegalAccessException | InstantiationException e) {
            System.err.println("Class can't be created: " + name);
        }
    }

    static void inspectService(Class<?> service) {

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
