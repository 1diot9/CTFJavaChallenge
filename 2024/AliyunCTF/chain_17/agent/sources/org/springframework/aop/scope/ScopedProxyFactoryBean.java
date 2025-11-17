package org.springframework.aop.scope;

import java.lang.reflect.Modifier;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.target.SimpleBeanTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/scope/ScopedProxyFactoryBean.class */
public class ScopedProxyFactoryBean extends ProxyConfig implements FactoryBean<Object>, BeanFactoryAware, AopInfrastructureBean {
    private final SimpleBeanTargetSource scopedTargetSource = new SimpleBeanTargetSource();

    @Nullable
    private String targetBeanName;

    @Nullable
    private Object proxy;

    public ScopedProxyFactoryBean() {
        setProxyTargetClass(true);
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
        this.scopedTargetSource.setTargetBeanName(targetBeanName);
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
        this.scopedTargetSource.setBeanFactory(beanFactory);
        ProxyFactory pf = new ProxyFactory();
        pf.copyFrom(this);
        pf.setTargetSource(this.scopedTargetSource);
        Assert.notNull(this.targetBeanName, "Property 'targetBeanName' is required");
        Class<?> beanType = beanFactory.getType(this.targetBeanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot create scoped proxy for bean '" + this.targetBeanName + "': Target type could not be determined at the time of proxy creation.");
        }
        if (!isProxyTargetClass() || beanType.isInterface() || Modifier.isPrivate(beanType.getModifiers())) {
            pf.setInterfaces(ClassUtils.getAllInterfacesForClass(beanType, cbf.getBeanClassLoader()));
        }
        ScopedObject scopedObject = new DefaultScopedObject(cbf, this.scopedTargetSource.getTargetBeanName());
        pf.addAdvice(new DelegatingIntroductionInterceptor(scopedObject));
        pf.addInterface(AopInfrastructureBean.class);
        this.proxy = pf.getProxy(cbf.getBeanClassLoader());
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        return this.scopedTargetSource.getTargetClass();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
