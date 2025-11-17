package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/BeanExpressionResolver.class */
public interface BeanExpressionResolver {
    @Nullable
    Object evaluate(@Nullable String value, BeanExpressionContext beanExpressionContext) throws BeansException;
}
