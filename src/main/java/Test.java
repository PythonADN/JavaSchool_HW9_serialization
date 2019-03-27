import proxy.CacheProxy;
import services.Service_1;
import services.Service_1_Impl;
import services.Service_2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Service_1 service_1 = new Service_1_Impl();
        InvocationHandler handler = new CacheProxy(service_1);
        Service_1 proxyService_1 = (Service_1) Proxy.newProxyInstance(service_1.getClass().getClassLoader(), Service_1_Impl.class.getInterfaces(), handler);

        List<String> list = proxyService_1.run("tt", 10);
        System.out.println(list);
    }
}
