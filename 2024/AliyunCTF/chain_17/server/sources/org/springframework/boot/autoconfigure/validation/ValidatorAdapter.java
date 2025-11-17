package org.springframework.boot.autoconfigure.validation;

import jakarta.validation.ValidationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/validation/ValidatorAdapter.class */
public class ValidatorAdapter implements SmartValidator, ApplicationContextAware, InitializingBean, DisposableBean {
    private final SmartValidator target;
    private final boolean existingBean;

    ValidatorAdapter(SmartValidator target, boolean existingBean) {
        this.target = target;
        this.existingBean = existingBean;
    }

    public final Validator getTarget() {
        return this.target;
    }

    @Override // org.springframework.validation.Validator
    public boolean supports(Class<?> clazz) {
        return this.target.supports(clazz);
    }

    @Override // org.springframework.validation.Validator
    public void validate(Object target, Errors errors) {
        this.target.validate(target, errors);
    }

    @Override // org.springframework.validation.SmartValidator
    public void validate(Object target, Errors errors, Object... validationHints) {
        this.target.validate(target, errors, validationHints);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.existingBean) {
            return;
        }
        SmartValidator smartValidator = this.target;
        if (smartValidator instanceof ApplicationContextAware) {
            ApplicationContextAware contextAwareTarget = (ApplicationContextAware) smartValidator;
            contextAwareTarget.setApplicationContext(applicationContext);
        }
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.existingBean) {
            return;
        }
        SmartValidator smartValidator = this.target;
        if (smartValidator instanceof InitializingBean) {
            InitializingBean initializingBean = (InitializingBean) smartValidator;
            initializingBean.afterPropertiesSet();
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws Exception {
        if (this.existingBean) {
            return;
        }
        SmartValidator smartValidator = this.target;
        if (smartValidator instanceof DisposableBean) {
            DisposableBean disposableBean = (DisposableBean) smartValidator;
            disposableBean.destroy();
        }
    }

    public static Validator get(ApplicationContext applicationContext, Validator validator) {
        if (validator != null) {
            return wrap(validator, false);
        }
        return getExistingOrCreate(applicationContext);
    }

    private static Validator getExistingOrCreate(ApplicationContext applicationContext) {
        Validator existing = getExisting(applicationContext);
        if (existing != null) {
            return wrap(existing, true);
        }
        return create(applicationContext);
    }

    private static Validator getExisting(ApplicationContext applicationContext) {
        try {
            Validator validator = (jakarta.validation.Validator) applicationContext.getBean(jakarta.validation.Validator.class);
            if (validator instanceof Validator) {
                Validator validator2 = validator;
                return validator2;
            }
            return new SpringValidatorAdapter(validator);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private static Validator create(MessageSource messageSource) {
        OptionalValidatorFactoryBean validator = new OptionalValidatorFactoryBean();
        try {
            MessageInterpolatorFactory factory = new MessageInterpolatorFactory(messageSource);
            validator.setMessageInterpolator(factory.getObject());
        } catch (ValidationException e) {
        }
        return wrap(validator, false);
    }

    private static Validator wrap(Validator validator, boolean existingBean) {
        if (validator instanceof jakarta.validation.Validator) {
            jakarta.validation.Validator jakartaValidator = (jakarta.validation.Validator) validator;
            if (jakartaValidator instanceof SpringValidatorAdapter) {
                SpringValidatorAdapter adapter = (SpringValidatorAdapter) jakartaValidator;
                return new ValidatorAdapter(adapter, existingBean);
            }
            return new ValidatorAdapter(new SpringValidatorAdapter(jakartaValidator), existingBean);
        }
        return validator;
    }

    @Override // org.springframework.validation.SmartValidator
    public <T> T unwrap(Class<T> cls) {
        if (cls.isInstance(this.target)) {
            return (T) this.target;
        }
        return (T) this.target.unwrap(cls);
    }
}
