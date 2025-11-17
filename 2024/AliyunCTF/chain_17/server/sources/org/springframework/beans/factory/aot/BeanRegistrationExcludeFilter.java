package org.springframework.beans.factory.aot;

import org.springframework.beans.factory.support.RegisteredBean;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationExcludeFilter.class */
public interface BeanRegistrationExcludeFilter {
    boolean isExcludedFromAotProcessing(RegisteredBean registeredBean);
}
