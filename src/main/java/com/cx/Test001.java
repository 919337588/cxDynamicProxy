package com.cx;

import com.cx.ext.proxy.JavaClassLoader;
import com.cx.ext.proxy.impl.MyJdkInvocationHandler;
import com.cx.service.OrderService;
import com.cx.service.impl.OrderServiceImpl;


public class Test001 {
    public static void main(String[] args) throws Throwable {
//        OrderService  orderService = new $Proxy0(new MyJdkInvocationHandler(new OrderServiceImpl()));
//        orderService.order();
        OrderService orderService = (OrderService) MyProxy.newProxyInstance(new JavaClassLoader(), OrderService.class, new MyJdkInvocationHandler(new OrderServiceImpl()));
        orderService.order();
    }
}
