package org.springframework.boot.context.properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.AttributeAccessor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/BindMethodAttribute.class */
final class BindMethodAttribute {
    static final String NAME = BindMethod.class.getName();

    private BindMethodAttribute() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BindMethod get(ApplicationContext applicationContext, String beanName) {
        if (!(applicationContext instanceof ConfigurableApplicationContext)) {
            return null;
        }
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        return get(configurableApplicationContext.getBeanFactory(), beanName);
    }

    static BindMethod get(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory.containsBeanDefinition(beanName)) {
            return get(beanFactory.getBeanDefinition(beanName));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BindMethod get(BeanDefinitionRegistry beanDefinitionRegistry, String beanName) {
        if (beanDefinitionRegistry.containsBeanDefinition(beanName)) {
            return get(beanDefinitionRegistry.getBeanDefinition(beanName));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BindMethod get(AttributeAccessor attributes) {
        return (BindMethod) attributes.getAttribute(NAME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void set(AttributeAccessor attributes, BindMethod bindMethod) {
        attributes.setAttribute(NAME, bindMethod);
    }
}
