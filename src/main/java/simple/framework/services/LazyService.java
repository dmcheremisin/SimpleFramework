package simple.framework.services;

import simple.framework.annotations.Init;
import simple.framework.annotations.Service;

@Service(name = "Very-very lazy service", lazyLoad = true)
public class LazyService {

    @Init
    void lazyInit() throws Exception {
        System.out.println("I'm initing extra lazily....");
    }

    void printInfo() {
        System.out.println("this is print info method");
    }
}
