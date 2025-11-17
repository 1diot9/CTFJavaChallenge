package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/BeanInfoFactory.class */
public interface BeanInfoFactory {
    @Nullable
    BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException;
}
