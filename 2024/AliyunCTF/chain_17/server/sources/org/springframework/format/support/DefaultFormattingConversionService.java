package org.springframework.format.support;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/DefaultFormattingConversionService.class */
public class DefaultFormattingConversionService extends FormattingConversionService {
    private static final boolean jsr354Present;

    static {
        ClassLoader classLoader = DefaultFormattingConversionService.class.getClassLoader();
        jsr354Present = ClassUtils.isPresent("javax.money.MonetaryAmount", classLoader);
    }

    public DefaultFormattingConversionService() {
        this(null, true);
    }

    public DefaultFormattingConversionService(boolean registerDefaultFormatters) {
        this(null, registerDefaultFormatters);
    }

    public DefaultFormattingConversionService(@Nullable StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {
        if (embeddedValueResolver != null) {
            setEmbeddedValueResolver(embeddedValueResolver);
        }
        DefaultConversionService.addDefaultConverters(this);
        if (registerDefaultFormatters) {
            addDefaultFormatters(this);
        }
    }

    public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
        formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
        if (jsr354Present) {
            formatterRegistry.addFormatter(new CurrencyUnitFormatter());
            formatterRegistry.addFormatter(new MonetaryAmountFormatter());
            formatterRegistry.addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
        }
        new DateTimeFormatterRegistrar().registerFormatters(formatterRegistry);
        new DateFormatterRegistrar().registerFormatters(formatterRegistry);
    }
}
