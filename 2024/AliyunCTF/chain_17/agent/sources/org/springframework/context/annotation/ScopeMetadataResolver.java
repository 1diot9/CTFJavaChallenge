package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ScopeMetadataResolver.class */
public interface ScopeMetadataResolver {
    ScopeMetadata resolveScopeMetadata(BeanDefinition definition);
}
