package org.springframework.format.support;

import java.util.Set;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormattingConversionServiceFactoryBean.class */
public class FormattingConversionServiceFactoryBean implements FactoryBean<FormattingConversionService>, EmbeddedValueResolverAware, InitializingBean {

    @Nullable
    private Set<?> converters;

    @Nullable
    private Set<?> formatters;

    @Nullable
    private Set<FormatterRegistrar> formatterRegistrars;
    private boolean registerDefaultFormatters = true;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Nullable
    private FormattingConversionService conversionService;

    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

    public void setFormatters(Set<?> formatters) {
        this.formatters = formatters;
    }

    public void setFormatterRegistrars(Set<FormatterRegistrar> formatterRegistrars) {
        this.formatterRegistrars = formatterRegistrars;
    }

    public void setRegisterDefaultFormatters(boolean registerDefaultFormatters) {
        this.registerDefaultFormatters = registerDefaultFormatters;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver embeddedValueResolver) {
        this.embeddedValueResolver = embeddedValueResolver;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        this.conversionService = new DefaultFormattingConversionService(this.embeddedValueResolver, this.registerDefaultFormatters);
        ConversionServiceFactory.registerConverters(this.converters, this.conversionService);
        registerFormatters(this.conversionService);
    }

    private void registerFormatters(FormattingConversionService conversionService) {
        if (this.formatters != null) {
            for (Object candidate : this.formatters) {
                if (candidate instanceof Formatter) {
                    Formatter<?> formatter = (Formatter) candidate;
                    conversionService.addFormatter(formatter);
                } else if (candidate instanceof AnnotationFormatterFactory) {
                    AnnotationFormatterFactory<?> factory = (AnnotationFormatterFactory) candidate;
                    conversionService.addFormatterForFieldAnnotation(factory);
                } else {
                    throw new IllegalArgumentException("Custom formatters must be implementations of Formatter or AnnotationFormatterFactory");
                }
            }
        }
        if (this.formatterRegistrars != null) {
            for (FormatterRegistrar registrar : this.formatterRegistrars) {
                registrar.registerFormatters(conversionService);
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public FormattingConversionService getObject() {
        return this.conversionService;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends FormattingConversionService> getObjectType() {
        return FormattingConversionService.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
