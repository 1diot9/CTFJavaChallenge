package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/ObjectFactory.class */
public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}
