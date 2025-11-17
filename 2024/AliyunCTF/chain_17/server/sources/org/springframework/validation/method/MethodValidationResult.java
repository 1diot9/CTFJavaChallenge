package org.springframework.validation.method;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.context.MessageSourceResolvable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/MethodValidationResult.class */
public interface MethodValidationResult {
    Object getTarget();

    Method getMethod();

    boolean isForReturnValue();

    List<ParameterValidationResult> getAllValidationResults();

    default boolean hasErrors() {
        return !getAllValidationResults().isEmpty();
    }

    default List<? extends MessageSourceResolvable> getAllErrors() {
        return getAllValidationResults().stream().flatMap(result -> {
            return result.getResolvableErrors().stream();
        }).toList();
    }

    default List<ParameterValidationResult> getValueResults() {
        return getAllValidationResults().stream().filter(result -> {
            return !(result instanceof ParameterErrors);
        }).toList();
    }

    default List<ParameterErrors> getBeanResults() {
        return getAllValidationResults().stream().filter(result -> {
            return result instanceof ParameterErrors;
        }).map(result2 -> {
            return (ParameterErrors) result2;
        }).toList();
    }

    static MethodValidationResult create(Object target, Method method, List<ParameterValidationResult> results) {
        return new DefaultMethodValidationResult(target, method, results);
    }

    static MethodValidationResult emptyResult() {
        return new EmptyMethodValidationResult();
    }
}
