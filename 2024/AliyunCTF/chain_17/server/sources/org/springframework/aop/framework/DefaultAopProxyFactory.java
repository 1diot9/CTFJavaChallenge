package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import org.springframework.aop.SpringProxy;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/DefaultAopProxyFactory.class */
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {
    public static final DefaultAopProxyFactory INSTANCE = new DefaultAopProxyFactory();
    private static final long serialVersionUID = 7930414337282325166L;

    @Override // org.springframework.aop.framework.AopProxyFactory
    public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
        if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
            Class<?> targetClass = config.getTargetClass();
            if (targetClass == null) {
                throw new AopConfigException("TargetSource cannot determine target class: Either an interface or a target is required for proxy creation.");
            }
            if (targetClass.isInterface() || Proxy.isProxyClass(targetClass) || ClassUtils.isLambdaClass(targetClass)) {
                return new JdkDynamicAopProxy(config);
            }
            return new ObjenesisCglibAopProxy(config);
        }
        return new JdkDynamicAopProxy(config);
    }

    private boolean hasNoUserSuppliedProxyInterfaces(AdvisedSupport config) {
        Class<?>[] ifcs = config.getProxiedInterfaces();
        return ifcs.length == 0 || (ifcs.length == 1 && SpringProxy.class.isAssignableFrom(ifcs[0]));
    }
}
