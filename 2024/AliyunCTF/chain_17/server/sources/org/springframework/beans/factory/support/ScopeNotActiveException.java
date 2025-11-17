package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanCreationException;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ScopeNotActiveException.class */
public class ScopeNotActiveException extends BeanCreationException {
    public ScopeNotActiveException(String beanName, String scopeName, IllegalStateException cause) {
        super(beanName, "Scope '" + scopeName + "' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton", cause);
    }
}
