package org.springframework.boot.convert;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.StringValueResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/convert/ApplicationConversionService.class */
public class ApplicationConversionService extends FormattingConversionService {
    private static volatile ApplicationConversionService sharedInstance;
    private final boolean unmodifiable;

    public ApplicationConversionService() {
        this(null);
    }

    public ApplicationConversionService(StringValueResolver embeddedValueResolver) {
        this(embeddedValueResolver, false);
    }

    private ApplicationConversionService(StringValueResolver embeddedValueResolver, boolean unmodifiable) {
        if (embeddedValueResolver != null) {
            setEmbeddedValueResolver(embeddedValueResolver);
        }
        configure(this);
        this.unmodifiable = unmodifiable;
    }

    @Override // org.springframework.format.support.FormattingConversionService, org.springframework.format.FormatterRegistry
    public void addPrinter(Printer<?> printer) {
        assertModifiable();
        super.addPrinter(printer);
    }

    @Override // org.springframework.format.support.FormattingConversionService, org.springframework.format.FormatterRegistry
    public void addParser(Parser<?> parser) {
        assertModifiable();
        super.addParser(parser);
    }

    @Override // org.springframework.format.support.FormattingConversionService, org.springframework.format.FormatterRegistry
    public void addFormatter(Formatter<?> formatter) {
        assertModifiable();
        super.addFormatter(formatter);
    }

    @Override // org.springframework.format.support.FormattingConversionService, org.springframework.format.FormatterRegistry
    public void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter) {
        assertModifiable();
        super.addFormatterForFieldType(fieldType, formatter);
    }

    @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.converter.ConverterRegistry
    public void addConverter(Converter<?, ?> converter) {
        assertModifiable();
        super.addConverter(converter);
    }

    @Override // org.springframework.format.support.FormattingConversionService, org.springframework.format.FormatterRegistry
    public void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser) {
        assertModifiable();
        super.addFormatterForFieldType(fieldType, printer, parser);
    }

    @Override // org.springframework.format.support.FormattingConversionService, org.springframework.format.FormatterRegistry
    public void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory) {
        assertModifiable();
        super.addFormatterForFieldAnnotation(annotationFormatterFactory);
    }

    @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.converter.ConverterRegistry
    public <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter) {
        assertModifiable();
        super.addConverter(sourceType, targetType, converter);
    }

    @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.converter.ConverterRegistry
    public void addConverter(GenericConverter converter) {
        assertModifiable();
        super.addConverter(converter);
    }

    @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.converter.ConverterRegistry
    public void addConverterFactory(ConverterFactory<?, ?> factory) {
        assertModifiable();
        super.addConverterFactory(factory);
    }

    @Override // org.springframework.core.convert.support.GenericConversionService, org.springframework.core.convert.converter.ConverterRegistry
    public void removeConvertible(Class<?> sourceType, Class<?> targetType) {
        assertModifiable();
        super.removeConvertible(sourceType, targetType);
    }

    private void assertModifiable() {
        if (this.unmodifiable) {
            throw new UnsupportedOperationException("This ApplicationConversionService cannot be modified");
        }
    }

    public boolean isConvertViaObjectSourceType(TypeDescriptor sourceType, TypeDescriptor targetType) {
        GenericConverter converter = getConverter(sourceType, targetType);
        Set<GenericConverter.ConvertiblePair> pairs = converter != null ? converter.getConvertibleTypes() : null;
        if (pairs != null) {
            for (GenericConverter.ConvertiblePair pair : pairs) {
                if (Object.class.equals(pair.getSourceType())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static ConversionService getSharedInstance() {
        ApplicationConversionService sharedInstance2 = sharedInstance;
        if (sharedInstance2 == null) {
            synchronized (ApplicationConversionService.class) {
                sharedInstance2 = sharedInstance;
                if (sharedInstance2 == null) {
                    sharedInstance2 = new ApplicationConversionService(null, true);
                    sharedInstance = sharedInstance2;
                }
            }
        }
        return sharedInstance2;
    }

    public static void configure(FormatterRegistry registry) {
        DefaultConversionService.addDefaultConverters(registry);
        DefaultFormattingConversionService.addDefaultFormatters(registry);
        addApplicationFormatters(registry);
        addApplicationConverters(registry);
    }

    public static void addApplicationConverters(ConverterRegistry registry) {
        addDelimitedStringConverters(registry);
        registry.addConverter(new StringToDurationConverter());
        registry.addConverter(new DurationToStringConverter());
        registry.addConverter(new NumberToDurationConverter());
        registry.addConverter(new DurationToNumberConverter());
        registry.addConverter(new StringToPeriodConverter());
        registry.addConverter(new PeriodToStringConverter());
        registry.addConverter(new NumberToPeriodConverter());
        registry.addConverter(new StringToDataSizeConverter());
        registry.addConverter(new NumberToDataSizeConverter());
        registry.addConverter(new StringToFileConverter());
        registry.addConverter(new InputStreamSourceToByteArrayConverter());
        registry.addConverterFactory(new LenientStringToEnumConverterFactory());
        registry.addConverterFactory(new LenientBooleanToEnumConverterFactory());
        if (registry instanceof ConversionService) {
            ConversionService conversionService = (ConversionService) registry;
            addApplicationConverters(registry, conversionService);
        }
    }

    private static void addApplicationConverters(ConverterRegistry registry, ConversionService conversionService) {
        registry.addConverter(new CharSequenceToObjectConverter(conversionService));
    }

    public static void addDelimitedStringConverters(ConverterRegistry registry) {
        ConversionService service = (ConversionService) registry;
        registry.addConverter(new ArrayToDelimitedStringConverter(service));
        registry.addConverter(new CollectionToDelimitedStringConverter(service));
        registry.addConverter(new DelimitedStringToArrayConverter(service));
        registry.addConverter(new DelimitedStringToCollectionConverter(service));
    }

    public static void addApplicationFormatters(FormatterRegistry registry) {
        registry.addFormatter(new CharArrayFormatter());
        registry.addFormatter(new InetAddressFormatter());
        registry.addFormatter(new IsoOffsetFormatter());
    }

    public static void addBeans(FormatterRegistry registry, ListableBeanFactory beanFactory) {
        Set<Object> beans = new LinkedHashSet<>();
        beans.addAll(beanFactory.getBeansOfType(GenericConverter.class).values());
        beans.addAll(beanFactory.getBeansOfType(Converter.class).values());
        beans.addAll(beanFactory.getBeansOfType(Printer.class).values());
        beans.addAll(beanFactory.getBeansOfType(Parser.class).values());
        for (Object bean : beans) {
            if (bean instanceof GenericConverter) {
                registry.addConverter((GenericConverter) bean);
            } else if (bean instanceof Converter) {
                registry.addConverter((Converter<?, ?>) bean);
            } else if (bean instanceof Formatter) {
                registry.addFormatter((Formatter) bean);
            } else if (bean instanceof Printer) {
                registry.addPrinter((Printer) bean);
            } else if (bean instanceof Parser) {
                registry.addParser((Parser) bean);
            }
        }
    }
}
