import proxy.CacheProxy;
import services.Service_1;
import services.Service_1_Impl;
import services.Service_2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

public class Test {
    public static void main(String[] args) {
//        Service_1 service_1 = new Service_1_Impl();
//        InvocationHandler handler = new CacheProxy(service_1, "C:\\Users\\ADN\\IdeaProjects\\JavaSchool_HW9_serialization\\src\\main\\resources\\");
//        Service_1 proxyService_1 = (Service_1) Proxy.newProxyInstance(service_1.getClass().getClassLoader(), Service_1_Impl.class.getInterfaces(), handler);
//        List<String> list1 = proxyService_1.run("tt", 10);
//        System.out.println(list1);
//        List<String> list2 = proxyService_1.run("tt", 10);
//        System.out.println(list2);

        Service_1 service_1 = CacheProxy.cache(new Service_1_Impl(), "C:\\Users\\ADN\\IdeaProjects\\JavaSchool_HW9_serialization\\src\\main\\resources\\");
        List<String> list_1 = service_1.run("tt", 10);
        List<String> list_2 = service_1.run("tt", 50);
        List<String> list_3 = service_1.run("tt", 50);

        System.out.println(list_3);
    }
}
