package org.springframework.validation.beanvalidation;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.TraversableResolver;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorContext;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/CustomValidatorBean.class */
public class CustomValidatorBean extends SpringValidatorAdapter implements Validator, InitializingBean {

    @Nullable
    private ValidatorFactory validatorFactory;

    @Nullable
    private MessageInterpolator messageInterpolator;

    @Nullable
    private TraversableResolver traversableResolver;

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.validatorFactory == null) {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        ValidatorContext validatorContext = this.validatorFactory.usingContext();
        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = this.validatorFactory.getMessageInterpolator();
        }
        validatorContext.messageInterpolator(new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            validatorContext.traversableResolver(this.traversableResolver);
        }
        setTargetValidator(validatorContext.getValidator());
    }
}
