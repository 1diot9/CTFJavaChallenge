package org.springframework.boot.context.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConversionServiceDeducer.class */
class ConversionServiceDeducer {
    private final ApplicationContext applicationContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConversionServiceDeducer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ConversionService> getConversionServices() {
        if (hasUserDefinedConfigurationServiceBean()) {
            return Collections.singletonList((ConversionService) this.applicationContext.getBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
        }
        ApplicationContext applicationContext = this.applicationContext;
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) applicationContext;
            return getConversionServices(configurableContext);
        }
        return null;
    }

    private List<ConversionService> getConversionServices(ConfigurableApplicationContext applicationContext) {
        List<ConversionService> conversionServices = new ArrayList<>();
        ConverterBeans converterBeans = new ConverterBeans(applicationContext);
        if (!converterBeans.isEmpty()) {
            FormattingConversionService beansConverterService = new FormattingConversionService();
            DefaultConversionService.addCollectionConverters(beansConverterService);
            beansConverterService.addConverter(new ConfigurationPropertiesCharSequenceToObjectConverter(beansConverterService));
            converterBeans.addTo(beansConverterService);
            conversionServices.add(beansConverterService);
        }
        if (applicationContext.getBeanFactory().getConversionService() != null) {
            conversionServices.add(applicationContext.getBeanFactory().getConversionService());
        }
        if (!converterBeans.isEmpty()) {
            conversionServices.add(ApplicationConversionService.getSharedInstance());
        }
        return conversionServices;
    }

    private boolean hasUserDefinedConfigurationServiceBean() {
        return this.applicationContext.containsBean(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME) && this.applicationContext.getAutowireCapableBeanFactory().isTypeMatch(ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME, ConversionService.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/ConversionServiceDeducer$ConverterBeans.class */
    public static class ConverterBeans {
        private final List<Converter> converters;
        private final List<GenericConverter> genericConverters;
        private final List<Formatter> formatters;

        ConverterBeans(ConfigurableApplicationContext applicationContext) {
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            this.converters = beans(Converter.class, ConfigurationPropertiesBinding.VALUE, beanFactory);
            this.genericConverters = beans(GenericConverter.class, ConfigurationPropertiesBinding.VALUE, beanFactory);
            this.formatters = beans(Formatter.class, ConfigurationPropertiesBinding.VALUE, beanFactory);
        }

        private <T> List<T> beans(Class<T> type, String qualifier, ListableBeanFactory beanFactory) {
            return new ArrayList(BeanFactoryAnnotationUtils.qualifiedBeansOfType(beanFactory, type, qualifier).values());
        }

        boolean isEmpty() {
            return this.converters.isEmpty() && this.genericConverters.isEmpty() && this.formatters.isEmpty();
        }

        void addTo(FormatterRegistry registry) {
            for (Converter converter : this.converters) {
                registry.addConverter((Converter<?, ?>) converter);
            }
            for (GenericConverter genericConverter : this.genericConverters) {
                registry.addConverter(genericConverter);
            }
            for (Formatter formatter : this.formatters) {
                registry.addFormatter(formatter);
            }
        }
    }
}
