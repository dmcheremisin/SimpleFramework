package simple.framework.processors;

import simple.framework.annotations.Init;
import simple.framework.annotations.Service;

import java.lang.reflect.Method;
import java.util.Map;


public class ApplicationProcessor {
    public static Map<String, Object> context;

    public static void main(String[] args) {
        ConfigurationProcessor configurationProcessor = new ConfigurationProcessor();
        context = configurationProcessor.getContext();
        initMethods();
    }

    public static void initMethods(){
        context.values().forEach(
                object -> {
                    Class<?> aClass = object.getClass();
                    for (Method method : aClass.getDeclaredMethods()) {
                        if(method.isAnnotationPresent(Init.class)) {
                            method.setAccessible(true);
                            initNormalOrLazy(object, aClass, method);
                        }
                    }
                }
        );
    }

    private static void initNormalOrLazy(Object object, Class<?> aClass, Method method) {
        boolean lazyLoad = aClass.getAnnotation(Service.class).lazyLoad();
        if(lazyLoad) {
            new Thread(() -> invokeInternal(object, method)).start();
        } else {
            invokeInternal(object, method);
        }
    }

    private static void invokeInternal(Object object, Method method) {
        try {
            method.invoke(object);
        } catch (Exception e) {
            Init annotation = method.getAnnotation(Init.class);
            if(annotation.suppressException()) {
                System.err.println("Exception happened in method: " + method.getName());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
