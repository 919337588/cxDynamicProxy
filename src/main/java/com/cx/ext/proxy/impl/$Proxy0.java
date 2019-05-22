package com.cx.ext.proxy.impl;

import com.cx.ext.proxy.MyExtJdkInvocationHandler;
import com.cx.service.OrderService;

import java.lang.reflect.Method;


public class $Proxy0 implements OrderService {
    private MyExtJdkInvocationHandler h;

    public $Proxy0(MyExtJdkInvocationHandler h) {
        this.h = h;
    }

    public void order() throws Throwable {
        // 如何获取真实目标方法呢
        Method orderMethod = OrderService.class.getMethod("order", new Class[]{});
        this.h.invoke(this, orderMethod, null);
    }
}
