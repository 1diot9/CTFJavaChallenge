package org.springframework.validation.beanvalidation;

import jakarta.validation.MessageInterpolator;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/LocaleContextMessageInterpolator.class */
public class LocaleContextMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator targetInterpolator;

    public LocaleContextMessageInterpolator(MessageInterpolator targetInterpolator) {
        Assert.notNull(targetInterpolator, "Target MessageInterpolator must not be null");
        this.targetInterpolator = targetInterpolator;
    }

    public String interpolate(String message, MessageInterpolator.Context context) {
        return this.targetInterpolator.interpolate(message, context, LocaleContextHolder.getLocale());
    }

    public String interpolate(String message, MessageInterpolator.Context context, Locale locale) {
        return this.targetInterpolator.interpolate(message, context, locale);
    }
}
