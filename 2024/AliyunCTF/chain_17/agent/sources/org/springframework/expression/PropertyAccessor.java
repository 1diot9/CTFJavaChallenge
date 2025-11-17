package org.springframework.expression;

import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/PropertyAccessor.class */
public interface PropertyAccessor {
    @Nullable
    Class<?>[] getSpecificTargetClasses();

    boolean canRead(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    TypedValue read(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    boolean canWrite(EvaluationContext context, @Nullable Object target, String name) throws AccessException;

    void write(EvaluationContext context, @Nullable Object target, String name, @Nullable Object newValue) throws AccessException;
}
