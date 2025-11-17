package org.springframework.validation.beanvalidation;

import jakarta.validation.ValidationException;
import org.apache.commons.logging.LogFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/OptionalValidatorFactoryBean.class */
public class OptionalValidatorFactoryBean extends LocalValidatorFactoryBean {
    @Override // org.springframework.validation.beanvalidation.LocalValidatorFactoryBean, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch (ValidationException e) {
            LogFactory.getLog(getClass()).debug("Failed to set up a Bean Validation provider", e);
        }
    }
}
