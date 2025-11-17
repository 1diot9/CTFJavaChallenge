package org.springframework.beans.factory.config;

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/PropertiesFactoryBean.class */
public class PropertiesFactoryBean extends PropertiesLoaderSupport implements FactoryBean<Properties>, InitializingBean {
    private boolean singleton = true;

    @Nullable
    private Properties singletonInstance;

    public final void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public final boolean isSingleton() {
        return this.singleton;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public final void afterPropertiesSet() throws IOException {
        if (this.singleton) {
            this.singletonInstance = createProperties();
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public final Properties getObject() throws IOException {
        if (this.singleton) {
            return this.singletonInstance;
        }
        return createProperties();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<Properties> getObjectType() {
        return Properties.class;
    }

    protected Properties createProperties() throws IOException {
        return mergeProperties();
    }
}
