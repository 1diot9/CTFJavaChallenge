package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/BindingErrorProcessor.class */
public interface BindingErrorProcessor {
    void processMissingFieldError(String missingField, BindingResult bindingResult);

    void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult);
}
