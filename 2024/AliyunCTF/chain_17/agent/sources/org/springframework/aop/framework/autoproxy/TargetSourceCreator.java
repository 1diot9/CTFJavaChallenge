package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/TargetSourceCreator.class */
public interface TargetSourceCreator {
    @Nullable
    TargetSource getTargetSource(Class<?> beanClass, String beanName);
}
