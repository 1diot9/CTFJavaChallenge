package cn.hutool.aop;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.proxy.ProxyFactory;
import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/ProxyUtil.class */
public final class ProxyUtil {
    public static <T> T proxy(T t, Class<? extends Aspect> cls) {
        return (T) ProxyFactory.createProxy(t, cls);
    }

    public static <T> T proxy(T t, Aspect aspect) {
        return (T) ProxyFactory.createProxy(t, aspect);
    }

    public static <T> T newProxyInstance(ClassLoader classLoader, InvocationHandler invocationHandler, Class<?>... clsArr) {
        return (T) Proxy.newProxyInstance(classLoader, clsArr, invocationHandler);
    }

    public static <T> T newProxyInstance(InvocationHandler invocationHandler, Class<?>... clsArr) {
        return (T) newProxyInstance(ClassUtil.getClassLoader(), invocationHandler, clsArr);
    }
}
