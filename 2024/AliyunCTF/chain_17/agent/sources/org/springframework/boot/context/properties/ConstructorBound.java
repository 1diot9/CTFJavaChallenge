package org.springframework.boot.context.properties;

import org.springframework.beans.factory.BeanFactory;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConstructorBound.class */
public abstract class ConstructorBound {
    public static Object from(BeanFactory beanFactory, String beanName, Class<?> beanType) {
        ConfigurationPropertiesBean bean = ConfigurationPropertiesBean.forValueObject(beanType, beanName);
        ConfigurationPropertiesBinder binder = ConfigurationPropertiesBinder.get(beanFactory);
        try {
            return binder.bindOrCreate(bean);
        } catch (Exception ex) {
            throw new ConfigurationPropertiesBindException(bean, ex);
        }
    }
}
