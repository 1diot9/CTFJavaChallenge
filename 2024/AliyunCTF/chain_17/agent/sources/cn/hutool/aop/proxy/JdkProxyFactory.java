package cn.hutool.aop.proxy;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.interceptor.JdkInterceptor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/proxy/JdkProxyFactory.class */
public class JdkProxyFactory extends ProxyFactory {
    private static final long serialVersionUID = 1;

    @Override // cn.hutool.aop.proxy.ProxyFactory
    public <T> T proxy(T t, Aspect aspect) {
        return (T) ProxyUtil.newProxyInstance(t.getClass().getClassLoader(), new JdkInterceptor(t, aspect), t.getClass().getInterfaces());
    }
}
