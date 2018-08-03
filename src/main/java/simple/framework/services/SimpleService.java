package simple.framework.services;

import simple.framework.annotations.Init;
import simple.framework.annotations.Service;

@Service(name = "Super simple service")
public class SimpleService {
    @Init
    void initService(){
        System.out.println("Hi! Simple Service is loaded");
    }

    String getName() {
        return "SimpleService";
    }

}
