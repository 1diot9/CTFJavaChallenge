package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectInstanceFactory.class */
public interface AspectInstanceFactory extends Ordered {
    Object getAspectInstance();

    @Nullable
    ClassLoader getAspectClassLoader();
}
