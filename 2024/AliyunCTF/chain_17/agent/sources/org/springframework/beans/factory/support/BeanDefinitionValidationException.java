package org.springframework.beans.factory.support;

import org.springframework.beans.FatalBeanException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/BeanDefinitionValidationException.class */
public class BeanDefinitionValidationException extends FatalBeanException {
    public BeanDefinitionValidationException(String msg) {
        super(msg);
    }

    public BeanDefinitionValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
