package org.springframework.validation;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/SmartValidator.class */
public interface SmartValidator extends Validator {
    void validate(Object target, Errors errors, Object... validationHints);

    default void validateValue(Class<?> targetType, String fieldName, @Nullable Object value, Errors errors, Object... validationHints) {
        throw new IllegalArgumentException("Cannot validate individual value for " + targetType);
    }

    @Nullable
    default <T> T unwrap(@Nullable Class<T> type) {
        return null;
    }
}
