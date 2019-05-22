package com.cx.ext.proxy;

import java.lang.reflect.Method;


public interface MyExtJdkInvocationHandler {
    /**
     * @param proxy  代理类
     * @param method 目标方法
     * @param args   参数
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable;
}
