package org.springframework.web.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/BindErrorUtils.class */
public abstract class BindErrorUtils {
    private static final MessageSource defaultMessageSource = new MethodArgumentErrorMessageSource();

    public static String resolveAndJoin(List<? extends MessageSourceResolvable> errors) {
        return resolveAndJoin(errors, defaultMessageSource, Locale.getDefault());
    }

    public static String resolveAndJoin(List<? extends MessageSourceResolvable> errors, MessageSource messageSource, Locale locale) {
        return resolveAndJoin(", and ", "", "", errors, messageSource, locale);
    }

    public static String resolveAndJoin(CharSequence delimiter, CharSequence prefix, CharSequence suffix, List<? extends MessageSourceResolvable> errors, MessageSource messageSource, Locale locale) {
        return (String) errors.stream().map(error -> {
            return messageSource.getMessage(error, locale);
        }).filter(StringUtils::hasText).collect(Collectors.joining(delimiter, prefix, suffix));
    }

    public static <E extends MessageSourceResolvable> Map<E, String> resolve(List<E> errors) {
        return resolve(errors, defaultMessageSource, Locale.getDefault());
    }

    public static <E extends MessageSourceResolvable> Map<E, String> resolve(List<E> errors, MessageSource messageSource, Locale locale) {
        Map<E, String> map = new LinkedHashMap<>(errors.size());
        errors.forEach(error -> {
            map.put(error, messageSource.getMessage(error, locale));
        });
        return map;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/BindErrorUtils$MethodArgumentErrorMessageSource.class */
    private static class MethodArgumentErrorMessageSource extends StaticMessageSource {
        MethodArgumentErrorMessageSource() {
            setUseCodeAsDefaultMessage(true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.context.support.AbstractMessageSource
        public String getDefaultMessage(MessageSourceResolvable resolvable, Locale locale) {
            String message = super.getDefaultMessage(resolvable, locale);
            if (!(resolvable instanceof FieldError)) {
                return message;
            }
            FieldError error = (FieldError) resolvable;
            return error.getField() + ": " + message;
        }
    }
}
