package org.springframework.beans.factory;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/SmartFactoryBean.class */
public interface SmartFactoryBean<T> extends FactoryBean<T> {
    default boolean isPrototype() {
        return false;
    }

    default boolean isEagerInit() {
        return false;
    }
}
