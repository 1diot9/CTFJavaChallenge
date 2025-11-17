package org.springframework.aop.aspectj.annotation;

import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/MetadataAwareAspectInstanceFactory.class */
public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {
    AspectMetadata getAspectMetadata();

    @Nullable
    Object getAspectCreationMutex();
}
