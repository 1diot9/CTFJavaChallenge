package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.NonNull;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanDefinitionOverrideException.class */
public class BeanDefinitionOverrideException extends BeanDefinitionStoreException {
    private final BeanDefinition beanDefinition;
    private final BeanDefinition existingDefinition;

    public BeanDefinitionOverrideException(String beanName, BeanDefinition beanDefinition, BeanDefinition existingDefinition) {
        super(beanDefinition.getResourceDescription(), beanName, "Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName + "' since there is already [" + existingDefinition + "] bound.");
        this.beanDefinition = beanDefinition;
        this.existingDefinition = existingDefinition;
    }

    @Override // org.springframework.beans.factory.BeanDefinitionStoreException
    @NonNull
    public String getResourceDescription() {
        return String.valueOf(super.getResourceDescription());
    }

    @Override // org.springframework.beans.factory.BeanDefinitionStoreException
    @NonNull
    public String getBeanName() {
        return String.valueOf(super.getBeanName());
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    public BeanDefinition getExistingDefinition() {
        return this.existingDefinition;
    }
}
