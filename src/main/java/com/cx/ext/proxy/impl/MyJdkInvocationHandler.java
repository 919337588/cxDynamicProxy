package com.cx.ext.proxy.impl;

import com.cx.ext.proxy.MyExtJdkInvocationHandler;

import java.lang.reflect.Method;


public class MyJdkInvocationHandler implements MyExtJdkInvocationHandler {
    /**
     * 目标对象 被代理的类 真实访问的类的对象
     */
    private Object target;

    public MyJdkInvocationHandler(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("<<<<<<<<<纯手写jdk动态代理日志开始>>>>>>>>>>");
        Object result = method.invoke(target, args);// 使用java的反射执行
        System.out.println("<<<<<<<<纯手写jdk动态代理结束>>>>>>>>>>");
        return result;
    }
}
