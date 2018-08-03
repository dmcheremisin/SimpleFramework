package simple.framework.processors;

import simple.framework.annotations.Service;
import simple.framework.services.LazyService;
import simple.framework.services.SimpleService;


public class AnnotationProcessor {
    public static void main(String[] args) {
        inspectService(SimpleService.class);
        inspectService(LazyService.class);
        inspectService(String.class);
        /*
        Super simple service
        false
        Very-very lazy service
        true
         */
    }

    static void inspectService(Class<?> service) {
//        boolean isPresent = service.isAnnotationPresent(Init.class);
//        Init annotation = service.getAnnotation(Init.class);
//        Annotation[] annotations = service.getAnnotations();
//        Annotation[] declaredAnnotations = service.getDeclaredAnnotations();

        if(service.isAnnotationPresent(Service.class)) {
            Service ann = service.getAnnotation(Service.class);
            System.out.println(ann.name());
            System.out.println(ann.lazyLoad());
        }
    }
}
