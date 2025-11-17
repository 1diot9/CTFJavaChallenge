package org.springframework.context.annotation;

import org.springframework.core.type.AnnotatedTypeMetadata;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/Condition.class */
public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
