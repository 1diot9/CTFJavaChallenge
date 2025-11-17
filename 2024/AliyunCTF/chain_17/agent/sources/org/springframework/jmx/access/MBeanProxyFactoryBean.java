package org.springframework.jmx.access;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/access/MBeanProxyFactoryBean.class */
public class MBeanProxyFactoryBean extends MBeanClientInterceptor implements FactoryBean<Object>, BeanClassLoaderAware, InitializingBean {

    @Nullable
    private Class<?> proxyInterface;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Nullable
    private Object mbeanProxy;

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    @Override // org.springframework.jmx.access.MBeanClientInterceptor, org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.jmx.access.MBeanClientInterceptor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws MBeanServerNotFoundException, MBeanInfoRetrievalException {
        super.afterPropertiesSet();
        if (this.proxyInterface == null) {
            this.proxyInterface = getManagementInterface();
            if (this.proxyInterface == null) {
                throw new IllegalArgumentException("Property 'proxyInterface' or 'managementInterface' is required");
            }
        } else if (getManagementInterface() == null) {
            setManagementInterface(this.proxyInterface);
        }
        this.mbeanProxy = new ProxyFactory(this.proxyInterface, this).getProxy(this.beanClassLoader);
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() {
        return this.mbeanProxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return this.proxyInterface;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
