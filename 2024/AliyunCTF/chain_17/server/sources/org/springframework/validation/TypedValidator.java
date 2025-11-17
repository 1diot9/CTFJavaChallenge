package org.springframework.validation;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/TypedValidator.class */
final class TypedValidator<T> implements Validator {
    private final Class<T> targetClass;
    private final Predicate<Class<?>> supports;
    private final BiConsumer<T, Errors> validate;

    public TypedValidator(Class<T> targetClass, Predicate<Class<?>> supports, BiConsumer<T, Errors> validate) {
        Assert.notNull(targetClass, "TargetClass must not be null");
        Assert.notNull(supports, "Supports function must not be null");
        Assert.notNull(validate, "Validate function must not be null");
        this.targetClass = targetClass;
        this.supports = supports;
        this.validate = validate;
    }

    @Override // org.springframework.validation.Validator
    public boolean supports(Class<?> clazz) {
        return this.supports.test(clazz);
    }

    @Override // org.springframework.validation.Validator
    public void validate(Object obj, Errors errors) {
        this.validate.accept(this.targetClass.cast(obj), errors);
    }
}
