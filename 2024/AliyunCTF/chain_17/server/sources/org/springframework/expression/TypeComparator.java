package org.springframework.expression;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/TypeComparator.class */
public interface TypeComparator {
    boolean canCompare(@Nullable Object firstObject, @Nullable Object secondObject);

    int compare(@Nullable Object firstObject, @Nullable Object secondObject) throws EvaluationException;
}
