package cn.hutool.aop.proxy;

import cn.hutool.aop.aspects.Aspect;
import cn.hutool.aop.interceptor.CglibInterceptor;
import net.sf.cglib.proxy.Enhancer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/aop/proxy/CglibProxyFactory.class */
public class CglibProxyFactory extends ProxyFactory {
    private static final long serialVersionUID = 1;

    @Override // cn.hutool.aop.proxy.ProxyFactory
    public <T> T proxy(T t, Aspect aspect) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t.getClass());
        enhancer.setCallback(new CglibInterceptor(t, aspect));
        return (T) enhancer.create();
    }
}
