package org.springframework.format.support;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionService.class */
public class FormattingConversionService extends GenericConversionService implements FormatterRegistry, EmbeddedValueResolverAware {

    @Nullable
    private StringValueResolver embeddedValueResolver;
    private final Map<AnnotationConverterKey, GenericConverter> cachedPrinters = new ConcurrentHashMap(64);
    private final Map<AnnotationConverterKey, GenericConverter> cachedParsers = new ConcurrentHashMap(64);

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    public void addPrinter(Printer<?> printer) {
        Class<?> fieldType = getFieldType(printer, Printer.class);
        addConverter(new PrinterConverter(fieldType, printer, this));
    }

    public void addParser(Parser<?> parser) {
        Class<?> fieldType = getFieldType(parser, Parser.class);
        addConverter(new ParserConverter(fieldType, parser, this));
    }

    public void addFormatter(Formatter<?> formatter) {
        addFormatterForFieldType(getFieldType(formatter), formatter);
    }

    public void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter) {
        addConverter(new PrinterConverter(fieldType, formatter, this));
        addConverter(new ParserConverter(fieldType, formatter, this));
    }

    public void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser) {
        addConverter(new PrinterConverter(fieldType, printer, this));
        addConverter(new ParserConverter(fieldType, parser, this));
    }

    public void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory) {
        Class<? extends Annotation> annotationType = getAnnotationType(annotationFormatterFactory);
        if (this.embeddedValueResolver != null && (annotationFormatterFactory instanceof EmbeddedValueResolverAware)) {
            EmbeddedValueResolverAware embeddedValueResolverAware = (EmbeddedValueResolverAware) annotationFormatterFactory;
            embeddedValueResolverAware.setEmbeddedValueResolver(this.embeddedValueResolver);
        }
        Set<Class<?>> fieldTypes = annotationFormatterFactory.getFieldTypes();
        for (Class<?> fieldType : fieldTypes) {
            addConverter(new AnnotationPrinterConverter(annotationType, annotationFormatterFactory, fieldType));
            addConverter(new AnnotationParserConverter(annotationType, annotationFormatterFactory, fieldType));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?> getFieldType(Formatter<?> formatter) {
        return getFieldType(formatter, Formatter.class);
    }

    private static <T> Class<?> getFieldType(T instance, Class<T> genericInterface) {
        Class<?> fieldType = GenericTypeResolver.resolveTypeArgument(instance.getClass(), genericInterface);
        if (fieldType == null && (instance instanceof DecoratingProxy)) {
            DecoratingProxy decoratingProxy = (DecoratingProxy) instance;
            fieldType = GenericTypeResolver.resolveTypeArgument(decoratingProxy.getDecoratedClass(), genericInterface);
        }
        Assert.notNull(fieldType, (Supplier<String>) () -> {
            return "Unable to extract the parameterized field type from " + ClassUtils.getShortName((Class<?>) genericInterface) + " [" + instance.getClass().getName() + "]; does the class parameterize the <T> generic type?";
        });
        return fieldType;
    }

    static Class<? extends Annotation> getAnnotationType(AnnotationFormatterFactory<? extends Annotation> factory) {
        Class resolveTypeArgument = GenericTypeResolver.resolveTypeArgument(factory.getClass(), AnnotationFormatterFactory.class);
        if (resolveTypeArgument == null) {
            throw new IllegalArgumentException("Unable to extract parameterized Annotation type argument from AnnotationFormatterFactory [" + factory.getClass().getName() + "]; does the factory parameterize the <A extends Annotation> generic type?");
        }
        return resolveTypeArgument;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionService$PrinterConverter.class */
    public static class PrinterConverter implements GenericConverter {
        private final Class<?> fieldType;
        private final TypeDescriptor printerObjectType;
        private final Printer printer;
        private final ConversionService conversionService;

        public PrinterConverter(Class<?> fieldType, Printer<?> printer, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.printerObjectType = TypeDescriptor.valueOf(resolvePrinterObjectType(printer));
            this.printer = printer;
            this.conversionService = conversionService;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(this.fieldType, String.class));
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (!sourceType.isAssignableTo(this.printerObjectType)) {
                source = this.conversionService.convert(source, sourceType, this.printerObjectType);
            }
            if (source == null) {
                return "";
            }
            return this.printer.print(source, LocaleContextHolder.getLocale());
        }

        @Nullable
        private Class<?> resolvePrinterObjectType(Printer<?> printer) {
            return GenericTypeResolver.resolveTypeArgument(printer.getClass(), Printer.class);
        }

        public String toString() {
            return this.fieldType.getName() + " -> " + String.class.getName() + " : " + this.printer;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionService$ParserConverter.class */
    public static class ParserConverter implements GenericConverter {
        private final Class<?> fieldType;
        private final Parser<?> parser;
        private final ConversionService conversionService;

        public ParserConverter(Class<?> fieldType, Parser<?> parser, ConversionService conversionService) {
            this.fieldType = fieldType;
            this.parser = parser;
            this.conversionService = conversionService;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, this.fieldType));
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            String text = (String) source;
            if (!StringUtils.hasText(text)) {
                return null;
            }
            try {
                Object result = this.parser.parse(text, LocaleContextHolder.getLocale());
                TypeDescriptor resultType = TypeDescriptor.valueOf(result.getClass());
                if (!resultType.isAssignableTo(targetType)) {
                    result = this.conversionService.convert(result, resultType, targetType);
                }
                return result;
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new IllegalArgumentException("Parse attempt failed for value [" + text + "]", ex2);
            }
        }

        public String toString() {
            return String.class.getName() + " -> " + this.fieldType.getName() + ": " + this.parser;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionService$AnnotationPrinterConverter.class */
    public class AnnotationPrinterConverter implements ConditionalGenericConverter {
        private final Class<? extends Annotation> annotationType;
        private final AnnotationFormatterFactory annotationFormatterFactory;
        private final Class<?> fieldType;

        public AnnotationPrinterConverter(Class<? extends Annotation> annotationType, AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(this.fieldType, String.class));
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return sourceType.hasAnnotation(this.annotationType);
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Annotation ann = sourceType.getAnnotation(this.annotationType);
            if (ann == null) {
                throw new IllegalStateException("Expected [" + this.annotationType.getName() + "] to be present on " + sourceType);
            }
            AnnotationConverterKey converterKey = new AnnotationConverterKey(ann, sourceType.getObjectType());
            GenericConverter converter = FormattingConversionService.this.cachedPrinters.get(converterKey);
            if (converter == null) {
                Printer<?> printer = this.annotationFormatterFactory.getPrinter(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new PrinterConverter(this.fieldType, printer, FormattingConversionService.this);
                FormattingConversionService.this.cachedPrinters.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        public String toString() {
            return "@" + this.annotationType.getName() + " " + this.fieldType.getName() + " -> " + String.class.getName() + ": " + this.annotationFormatterFactory;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionService$AnnotationParserConverter.class */
    public class AnnotationParserConverter implements ConditionalGenericConverter {
        private final Class<? extends Annotation> annotationType;
        private final AnnotationFormatterFactory annotationFormatterFactory;
        private final Class<?> fieldType;

        public AnnotationParserConverter(Class<? extends Annotation> annotationType, AnnotationFormatterFactory<?> annotationFormatterFactory, Class<?> fieldType) {
            this.annotationType = annotationType;
            this.annotationFormatterFactory = annotationFormatterFactory;
            this.fieldType = fieldType;
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(String.class, this.fieldType));
        }

        @Override // org.springframework.core.convert.converter.ConditionalConverter
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return targetType.hasAnnotation(this.annotationType);
        }

        @Override // org.springframework.core.convert.converter.GenericConverter
        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Annotation ann = targetType.getAnnotation(this.annotationType);
            if (ann == null) {
                throw new IllegalStateException("Expected [" + this.annotationType.getName() + "] to be present on " + targetType);
            }
            AnnotationConverterKey converterKey = new AnnotationConverterKey(ann, targetType.getObjectType());
            GenericConverter converter = FormattingConversionService.this.cachedParsers.get(converterKey);
            if (converter == null) {
                Parser<?> parser = this.annotationFormatterFactory.getParser(converterKey.getAnnotation(), converterKey.getFieldType());
                converter = new ParserConverter(this.fieldType, parser, FormattingConversionService.this);
                FormattingConversionService.this.cachedParsers.put(converterKey, converter);
            }
            return converter.convert(source, sourceType, targetType);
        }

        public String toString() {
            return String.class.getName() + " -> @" + this.annotationType.getName() + " " + this.fieldType.getName() + ": " + this.annotationFormatterFactory;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionService$AnnotationConverterKey.class */
    private static class AnnotationConverterKey {
        private final Annotation annotation;
        private final Class<?> fieldType;

        public AnnotationConverterKey(Annotation annotation, Class<?> fieldType) {
            this.annotation = annotation;
            this.fieldType = fieldType;
        }

        public Annotation getAnnotation() {
            return this.annotation;
        }

        public Class<?> getFieldType() {
            return this.fieldType;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof AnnotationConverterKey) {
                    AnnotationConverterKey that = (AnnotationConverterKey) other;
                    if (this.fieldType != that.fieldType || !this.annotation.equals(that.annotation)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.fieldType.hashCode() * 29) + this.annotation.hashCode();
        }
    }
}
