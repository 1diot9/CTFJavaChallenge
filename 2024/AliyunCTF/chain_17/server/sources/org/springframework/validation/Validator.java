package org.springframework.validation;

import java.util.Objects;
import java.util.function.BiConsumer;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/Validator.class */
public interface Validator {
    boolean supports(Class<?> clazz);

    void validate(Object target, Errors errors);

    default Errors validateObject(Object target) {
        Errors errors = new SimpleErrors(target);
        validate(target, errors);
        return errors;
    }

    static <T> Validator forInstanceOf(Class<T> targetClass, BiConsumer<T, Errors> delegate) {
        Objects.requireNonNull(targetClass);
        return new TypedValidator(targetClass, targetClass::isAssignableFrom, delegate);
    }

    static <T> Validator forType(Class<T> targetClass, BiConsumer<T, Errors> delegate) {
        Objects.requireNonNull(targetClass);
        return new TypedValidator(targetClass, (v1) -> {
            return r3.equals(v1);
        }, delegate);
    }
}
