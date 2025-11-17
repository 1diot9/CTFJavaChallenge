package org.springframework.beans;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyAccessorFactory.class */
public final class PropertyAccessorFactory {
    private PropertyAccessorFactory() {
    }

    public static BeanWrapper forBeanPropertyAccess(Object target) {
        return new BeanWrapperImpl(target);
    }

    public static ConfigurablePropertyAccessor forDirectFieldAccess(Object target) {
        return new DirectFieldAccessor(target);
    }
}
