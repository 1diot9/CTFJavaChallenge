package org.springframework.beans.factory.config;

import java.util.Properties;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlPropertiesFactoryBean.class */
public class YamlPropertiesFactoryBean extends YamlProcessor implements FactoryBean<Properties>, InitializingBean {
    private boolean singleton = true;

    @Nullable
    private Properties properties;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (isSingleton()) {
            this.properties = createProperties();
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Properties getObject() {
        return this.properties != null ? this.properties : createProperties();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return Properties.class;
    }

    protected Properties createProperties() {
        Properties result = CollectionFactory.createStringAdaptingProperties();
        process((properties, map) -> {
            result.putAll(properties);
        });
        return result;
    }
}
