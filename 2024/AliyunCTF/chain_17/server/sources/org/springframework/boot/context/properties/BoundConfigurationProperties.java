package org.springframework.boot.context.properties;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/BoundConfigurationProperties.class */
public class BoundConfigurationProperties {
    private final Map<ConfigurationPropertyName, ConfigurationProperty> properties = new LinkedHashMap();
    private static final String BEAN_NAME = BoundConfigurationProperties.class.getName();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void add(ConfigurationProperty configurationProperty) {
        this.properties.put(configurationProperty.getName(), configurationProperty);
    }

    public ConfigurationProperty get(ConfigurationPropertyName name) {
        return this.properties.get(name);
    }

    public Map<ConfigurationPropertyName, ConfigurationProperty> getAll() {
        return Collections.unmodifiableMap(this.properties);
    }

    public static BoundConfigurationProperties get(ApplicationContext context) {
        if (!context.containsBeanDefinition(BEAN_NAME)) {
            return null;
        }
        return (BoundConfigurationProperties) context.getBean(BEAN_NAME, BoundConfigurationProperties.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void register(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "Registry must not be null");
        if (!registry.containsBeanDefinition(BEAN_NAME)) {
            BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition((Class<?>) BoundConfigurationProperties.class).getBeanDefinition();
            definition.setRole(2);
            registry.registerBeanDefinition(BEAN_NAME, definition);
        }
    }
}
