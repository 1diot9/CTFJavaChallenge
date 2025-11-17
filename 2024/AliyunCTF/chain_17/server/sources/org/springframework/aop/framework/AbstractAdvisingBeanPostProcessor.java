package org.springframework.aop.framework;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AbstractAdvisingBeanPostProcessor.class */
public abstract class AbstractAdvisingBeanPostProcessor extends ProxyProcessorSupport implements SmartInstantiationAwareBeanPostProcessor {

    @Nullable
    protected Advisor advisor;
    protected boolean beforeExistingAdvisors = false;
    private final Map<Class<?>, Boolean> eligibleBeans = new ConcurrentHashMap(256);

    public void setBeforeExistingAdvisors(boolean beforeExistingAdvisors) {
        this.beforeExistingAdvisors = beforeExistingAdvisors;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v0 */
    @Override // org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor
    public Class<?> determineBeanType(Class<?> cls, String str) {
        if (this.advisor != null && isEligible(cls)) {
            ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.copyFrom(this);
            proxyFactory.setTargetClass(cls);
            if (!proxyFactory.isProxyTargetClass()) {
                evaluateProxyInterfaces(cls, proxyFactory);
            }
            proxyFactory.addAdvisor(this.advisor);
            customizeProxyFactory(proxyFactory);
            ?? proxyClassLoader = getProxyClassLoader();
            boolean z = proxyClassLoader instanceof SmartClassLoader;
            ClassLoader classLoader = proxyClassLoader;
            if (z) {
                SmartClassLoader smartClassLoader = (SmartClassLoader) proxyClassLoader;
                classLoader = proxyClassLoader;
                if (proxyClassLoader != cls.getClassLoader()) {
                    classLoader = smartClassLoader.getOriginalClassLoader();
                }
            }
            return proxyFactory.getProxyClass(classLoader);
        }
        return cls;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v0 */
    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object obj, String str) {
        if (this.advisor == null || (obj instanceof AopInfrastructureBean)) {
            return obj;
        }
        if (obj instanceof Advised) {
            Advised advised = (Advised) obj;
            if (!advised.isFrozen() && isEligible(AopUtils.getTargetClass(obj))) {
                if (this.beforeExistingAdvisors) {
                    advised.addAdvisor(0, this.advisor);
                } else {
                    if (advised.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE && advised.getAdvisorCount() > 0) {
                        advised.addAdvisor(advised.getAdvisorCount() - 1, this.advisor);
                        return obj;
                    }
                    advised.addAdvisor(this.advisor);
                }
                return obj;
            }
        }
        if (isEligible(obj, str)) {
            ProxyFactory prepareProxyFactory = prepareProxyFactory(obj, str);
            if (!prepareProxyFactory.isProxyTargetClass()) {
                evaluateProxyInterfaces(obj.getClass(), prepareProxyFactory);
            }
            prepareProxyFactory.addAdvisor(this.advisor);
            customizeProxyFactory(prepareProxyFactory);
            ?? proxyClassLoader = getProxyClassLoader();
            boolean z = proxyClassLoader instanceof SmartClassLoader;
            ClassLoader classLoader = proxyClassLoader;
            if (z) {
                SmartClassLoader smartClassLoader = (SmartClassLoader) proxyClassLoader;
                classLoader = proxyClassLoader;
                if (proxyClassLoader != obj.getClass().getClassLoader()) {
                    classLoader = smartClassLoader.getOriginalClassLoader();
                }
            }
            return prepareProxyFactory.getProxy(classLoader);
        }
        return obj;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isEligible(Object bean, String beanName) {
        return isEligible(bean.getClass());
    }

    protected boolean isEligible(Class<?> targetClass) {
        Boolean eligible = this.eligibleBeans.get(targetClass);
        if (eligible != null) {
            return eligible.booleanValue();
        }
        if (this.advisor == null) {
            return false;
        }
        Boolean eligible2 = Boolean.valueOf(AopUtils.canApply(this.advisor, targetClass));
        this.eligibleBeans.put(targetClass, eligible2);
        return eligible2.booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ProxyFactory prepareProxyFactory(Object bean, String beanName) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.copyFrom(this);
        proxyFactory.setTarget(bean);
        return proxyFactory;
    }

    protected void customizeProxyFactory(ProxyFactory proxyFactory) {
    }
}
