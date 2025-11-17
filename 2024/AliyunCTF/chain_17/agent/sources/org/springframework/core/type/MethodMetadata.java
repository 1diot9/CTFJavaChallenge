package org.springframework.core.type;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/type/MethodMetadata.class */
public interface MethodMetadata extends AnnotatedTypeMetadata {
    String getMethodName();

    String getDeclaringClassName();

    String getReturnTypeName();

    boolean isAbstract();

    boolean isStatic();

    boolean isFinal();

    boolean isOverridable();
}
