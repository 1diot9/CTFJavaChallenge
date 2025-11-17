package org.springframework.boot.autoconfigure.validation;

import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.boot.validation.beanvalidation.FilteredMethodValidationPostProcessor;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@AutoConfiguration
@ConditionalOnClass({ExecutableValidator.class})
@ConditionalOnResource(resources = {"classpath:META-INF/services/jakarta.validation.spi.ValidationProvider"})
@Import({PrimaryDefaultValidatorPostProcessor.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/validation/ValidationAutoConfiguration.class */
public class ValidationAutoConfiguration {
    @ConditionalOnMissingBean({Validator.class})
    @Bean
    @Role(2)
    public static LocalValidatorFactoryBean defaultValidator(ApplicationContext applicationContext, ObjectProvider<ValidationConfigurationCustomizer> customizers) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConfigurationInitializer(configuration -> {
            customizers.orderedStream().forEach(customizer -> {
                customizer.customize(configuration);
            });
        });
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(applicationContext);
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        return factoryBean;
    }

    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    @Bean
    public static MethodValidationPostProcessor methodValidationPostProcessor(Environment environment, ObjectProvider<Validator> validator, ObjectProvider<MethodValidationExcludeFilter> excludeFilters) {
        FilteredMethodValidationPostProcessor processor = new FilteredMethodValidationPostProcessor(excludeFilters.orderedStream());
        boolean proxyTargetClass = ((Boolean) environment.getProperty("spring.aop.proxy-target-class", Boolean.class, true)).booleanValue();
        processor.setProxyTargetClass(proxyTargetClass);
        processor.setValidatorProvider(validator);
        return processor;
    }
}
