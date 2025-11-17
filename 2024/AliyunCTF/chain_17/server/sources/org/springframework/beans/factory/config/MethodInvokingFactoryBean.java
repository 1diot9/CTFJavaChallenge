package org.springframework.beans.factory.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/MethodInvokingFactoryBean.class */
public class MethodInvokingFactoryBean extends MethodInvokingBean implements FactoryBean<Object> {
    private boolean singleton = true;
    private boolean initialized = false;

    @Nullable
    private Object singletonObject;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override // org.springframework.beans.factory.config.MethodInvokingBean, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        prepare();
        if (this.singleton) {
            this.initialized = true;
            this.singletonObject = invokeWithTargetException();
        }
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() throws Exception {
        if (this.singleton) {
            if (!this.initialized) {
                throw new FactoryBeanNotInitializedException();
            }
            return this.singletonObject;
        }
        return invokeWithTargetException();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        if (!isPrepared()) {
            return null;
        }
        return getPreparedMethod().getReturnType();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return this.singleton;
    }
}
