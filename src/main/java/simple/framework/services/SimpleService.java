package simple.framework.services;

import simple.framework.annotations.Init;
import simple.framework.annotations.Service;

@Service(name = "Super simple service")
public class SimpleService {

    private LazyService lazyService;

    public SimpleService(LazyService lazyService) {
        this.lazyService = lazyService;
    }

    @Init(suppressException = true)
    void initService(){
        System.out.println("Hi! Simple Service is loaded");
        throw new RuntimeException("something went wrong in initService");
    }

    String getName() {
        return "SimpleService";
    }

}
