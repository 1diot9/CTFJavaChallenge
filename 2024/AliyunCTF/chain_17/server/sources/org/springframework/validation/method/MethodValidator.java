package org.springframework.validation.method;

import java.lang.reflect.Method;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/MethodValidator.class */
public interface MethodValidator {
    Class<?>[] determineValidationGroups(Object target, Method method);

    MethodValidationResult validateArguments(Object target, Method method, @Nullable MethodParameter[] parameters, Object[] arguments, Class<?>[] groups);

    MethodValidationResult validateReturnValue(Object target, Method method, @Nullable MethodParameter returnType, @Nullable Object returnValue, Class<?>[] groups);

    default void applyArgumentValidation(Object target, Method method, @Nullable MethodParameter[] parameters, Object[] arguments, Class<?>[] groups) {
        MethodValidationResult result = validateArguments(target, method, parameters, arguments, groups);
        if (result.hasErrors()) {
            throw new MethodValidationException(result);
        }
    }

    default void applyReturnValueValidation(Object target, Method method, @Nullable MethodParameter returnType, @Nullable Object returnValue, Class<?>[] groups) {
        MethodValidationResult result = validateReturnValue(target, method, returnType, returnValue, groups);
        if (result.hasErrors()) {
            throw new MethodValidationException(result);
        }
    }
}
