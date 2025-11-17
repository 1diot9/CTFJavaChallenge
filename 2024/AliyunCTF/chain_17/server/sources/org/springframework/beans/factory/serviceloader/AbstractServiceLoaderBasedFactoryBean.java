package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/serviceloader/AbstractServiceLoaderBasedFactoryBean.class */
public abstract class AbstractServiceLoaderBasedFactoryBean extends AbstractFactoryBean<Object> implements BeanClassLoaderAware {

    @Nullable
    private Class<?> serviceType;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    protected abstract Object getObjectToExpose(ServiceLoader<?> serviceLoader);

    public void setServiceType(@Nullable Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    @Nullable
    public Class<?> getServiceType() {
        return this.serviceType;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean
    protected Object createInstance() {
        Assert.state(getServiceType() != null, "Property 'serviceType' is required");
        return getObjectToExpose(ServiceLoader.load(getServiceType(), this.beanClassLoader));
    }
}
