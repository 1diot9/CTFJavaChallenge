package org.springframework.aop.framework;

import java.lang.reflect.Constructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.objenesis.SpringObjenesis;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/ObjenesisCglibAopProxy.class */
class ObjenesisCglibAopProxy extends CglibAopProxy {
    private static final Log logger = LogFactory.getLog((Class<?>) ObjenesisCglibAopProxy.class);
    private static final SpringObjenesis objenesis = new SpringObjenesis();

    public ObjenesisCglibAopProxy(AdvisedSupport config) {
        super(config);
    }

    @Override // org.springframework.aop.framework.CglibAopProxy
    protected Class<?> createProxyClass(Enhancer enhancer) {
        return enhancer.createClass();
    }

    @Override // org.springframework.aop.framework.CglibAopProxy
    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        Constructor<?> declaredConstructor;
        Class<?> proxyClass = enhancer.createClass();
        Object proxyInstance = null;
        if (objenesis.isWorthTrying()) {
            try {
                proxyInstance = objenesis.newInstance(proxyClass, enhancer.getUseCache());
            } catch (Throwable ex) {
                logger.debug("Unable to instantiate proxy using Objenesis, falling back to regular proxy construction", ex);
            }
        }
        if (proxyInstance == null) {
            try {
                if (this.constructorArgs != null) {
                    declaredConstructor = proxyClass.getDeclaredConstructor(this.constructorArgTypes);
                } else {
                    declaredConstructor = proxyClass.getDeclaredConstructor(new Class[0]);
                }
                Constructor<?> ctor = declaredConstructor;
                ReflectionUtils.makeAccessible(ctor);
                proxyInstance = this.constructorArgs != null ? ctor.newInstance(this.constructorArgs) : ctor.newInstance(new Object[0]);
            } catch (Throwable ex2) {
                throw new AopConfigException("Unable to instantiate proxy using Objenesis, and regular proxy instantiation via default constructor fails as well", ex2);
            }
        }
        ((Factory) proxyInstance).setCallbacks(callbacks);
        return proxyInstance;
    }
}
