package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/MethodReplacer.class */
public interface MethodReplacer {
    Object reimplement(Object obj, Method method, Object[] args) throws Throwable;
}
