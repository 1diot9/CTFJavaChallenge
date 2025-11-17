package org.springframework.validation.beanvalidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/SpringConstraintValidatorFactory.class */
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {
    private final AutowireCapableBeanFactory beanFactory;

    public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return (T) this.beanFactory.createBean(key);
    }

    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        this.beanFactory.destroyBean(instance);
    }
}
