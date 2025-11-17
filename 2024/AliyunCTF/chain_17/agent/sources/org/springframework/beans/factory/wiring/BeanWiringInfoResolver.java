package org.springframework.beans.factory.wiring;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/wiring/BeanWiringInfoResolver.class */
public interface BeanWiringInfoResolver {
    @Nullable
    BeanWiringInfo resolveWiringInfo(Object beanInstance);
}
