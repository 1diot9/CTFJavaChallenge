package org.springframework.aop.framework;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AbstractSingletonProxyFactoryBean.class */
public abstract class AbstractSingletonProxyFactoryBean extends ProxyConfig implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean {

    @Nullable
    private Object target;

    @Nullable
    private Class<?>[] proxyInterfaces;

    @Nullable
    private Object[] preInterceptors;

    @Nullable
    private Object[] postInterceptors;
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();

    @Nullable
    private transient ClassLoader proxyClassLoader;

    @Nullable
    private Object proxy;

    protected abstract Object createMainInterceptor();

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setProxyInterfaces(Class<?>[] proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }

    public void setPreInterceptors(Object[] preInterceptors) {
        this.preInterceptors = preInterceptors;
    }

    public void setPostInterceptors(Object[] postInterceptors) {
        this.postInterceptors = postInterceptors;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    public void setProxyClassLoader(ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (this.proxyClassLoader == null) {
            this.proxyClassLoader = classLoader;
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Class<?> targetClass;
        if (this.target == null) {
            throw new IllegalArgumentException("Property 'target' is required");
        }
        if (this.target instanceof String) {
            throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
        }
        if (this.proxyClassLoader == null) {
            this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        if (this.preInterceptors != null) {
            for (Object interceptor : this.preInterceptors) {
                proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(createMainInterceptor()));
        if (this.postInterceptors != null) {
            for (Object interceptor2 : this.postInterceptors) {
                proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor2));
            }
        }
        proxyFactory.copyFrom(this);
        TargetSource targetSource = createTargetSource(this.target);
        proxyFactory.setTargetSource(targetSource);
        if (this.proxyInterfaces != null) {
            proxyFactory.setInterfaces(this.proxyInterfaces);
        } else if (!isProxyTargetClass() && (targetClass = targetSource.getTargetClass()) != null) {
            proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, this.proxyClassLoader));
        }
        postProcessProxyFactory(proxyFactory);
        this.proxy = proxyFactory.getProxy(this.proxyClassLoader);
    }

    protected TargetSource createTargetSource(Object target) {
        if (target instanceof TargetSource) {
            TargetSource targetSource = (TargetSource) target;
            return targetSource;
        }
        return new SingletonTargetSource(target);
    }

    protected void postProcessProxyFactory(ProxyFactory proxyFactory) {
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Class<?> getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        if (this.proxyInterfaces != null && this.proxyInterfaces.length == 1) {
            return this.proxyInterfaces[0];
        }
        Object obj = this.target;
        if (obj instanceof TargetSource) {
            TargetSource targetSource = (TargetSource) obj;
            return targetSource.getTargetClass();
        }
        if (this.target != null) {
            return this.target.getClass();
        }
        return null;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public final boolean isSingleton() {
        return true;
    }
}
