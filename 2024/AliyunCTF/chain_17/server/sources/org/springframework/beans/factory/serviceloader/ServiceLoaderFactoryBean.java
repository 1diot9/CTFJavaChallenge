package org.springframework.beans.factory.serviceloader;

import java.util.ServiceLoader;
import org.springframework.beans.factory.BeanClassLoaderAware;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/serviceloader/ServiceLoaderFactoryBean.class */
public class ServiceLoaderFactoryBean extends AbstractServiceLoaderBasedFactoryBean implements BeanClassLoaderAware {
    @Override // org.springframework.beans.factory.serviceloader.AbstractServiceLoaderBasedFactoryBean
    protected Object getObjectToExpose(ServiceLoader<?> serviceLoader) {
        return serviceLoader;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return ServiceLoader.class;
    }
}
