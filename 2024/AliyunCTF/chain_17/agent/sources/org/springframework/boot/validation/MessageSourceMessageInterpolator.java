package org.springframework.boot.validation;

import jakarta.validation.MessageInterpolator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/validation/MessageSourceMessageInterpolator.class */
class MessageSourceMessageInterpolator implements MessageInterpolator {
    private static final char PREFIX = '{';
    private static final char SUFFIX = '}';
    private static final char ESCAPE = '\\';
    private final MessageSource messageSource;
    private final MessageInterpolator messageInterpolator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MessageSourceMessageInterpolator(MessageSource messageSource, MessageInterpolator messageInterpolator) {
        this.messageSource = messageSource;
        this.messageInterpolator = messageInterpolator;
    }

    public String interpolate(String messageTemplate, MessageInterpolator.Context context) {
        return interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
    }

    public String interpolate(String messageTemplate, MessageInterpolator.Context context, Locale locale) {
        String message = replaceParameters(messageTemplate, locale);
        return this.messageInterpolator.interpolate(message, context, locale);
    }

    private String replaceParameters(String message, Locale locale) {
        return replaceParameters(message, locale, new LinkedHashSet(4));
    }

    private String replaceParameters(String message, Locale locale, Set<String> visitedParameters) {
        StringBuilder buf = new StringBuilder(message);
        int parentheses = 0;
        int startIndex = -1;
        int endIndex = -1;
        int i = 0;
        while (i < buf.length()) {
            if (buf.charAt(i) == '\\') {
                i++;
            } else if (buf.charAt(i) == '{') {
                if (startIndex == -1) {
                    startIndex = i;
                }
                parentheses++;
            } else if (buf.charAt(i) == '}') {
                if (parentheses > 0) {
                    parentheses--;
                }
                endIndex = i;
            }
            if (parentheses == 0 && startIndex < endIndex) {
                String parameter = buf.substring(startIndex + 1, endIndex);
                if (!visitedParameters.add(parameter)) {
                    throw new IllegalArgumentException("Circular reference '{" + String.join(" -> ", visitedParameters) + " -> " + parameter + "}'");
                }
                String value = replaceParameter(parameter, locale, visitedParameters);
                if (value != null) {
                    buf.replace(startIndex, endIndex + 1, value);
                    i = (startIndex + value.length()) - 1;
                }
                visitedParameters.remove(parameter);
                startIndex = -1;
                endIndex = -1;
            }
            i++;
        }
        return buf.toString();
    }

    private String replaceParameter(String parameter, Locale locale, Set<String> visitedParameters) {
        String parameter2 = replaceParameters(parameter, locale, visitedParameters);
        String value = this.messageSource.getMessage(parameter2, null, null, locale);
        if (value == null || isUsingCodeAsDefaultMessage(value, parameter2)) {
            return null;
        }
        return replaceParameters(value, locale, visitedParameters);
    }

    private boolean isUsingCodeAsDefaultMessage(String value, String parameter) {
        return value.equals(parameter);
    }
}
