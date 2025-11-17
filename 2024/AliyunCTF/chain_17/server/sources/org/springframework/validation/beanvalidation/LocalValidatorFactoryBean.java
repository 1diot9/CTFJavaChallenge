package org.springframework.validation.beanvalidation;

import jakarta.validation.ClockProvider;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.ParameterNameProvider;
import jakarta.validation.TraversableResolver;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.ValidationProviderResolver;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorContext;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.bootstrap.GenericBootstrap;
import jakarta.validation.bootstrap.ProviderSpecificBootstrap;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/LocalValidatorFactoryBean.class */
public class LocalValidatorFactoryBean extends SpringValidatorAdapter implements ValidatorFactory, ApplicationContextAware, InitializingBean, DisposableBean {

    @Nullable
    private Class providerClass;

    @Nullable
    private ValidationProviderResolver validationProviderResolver;

    @Nullable
    private MessageInterpolator messageInterpolator;

    @Nullable
    private TraversableResolver traversableResolver;

    @Nullable
    private ConstraintValidatorFactory constraintValidatorFactory;

    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer;

    @Nullable
    private Resource[] mappingLocations;
    private final Map<String, String> validationPropertyMap = new HashMap();

    @Nullable
    private Consumer<Configuration<?>> configurationInitializer;

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private ValidatorFactory validatorFactory;

    public void setProviderClass(Class providerClass) {
        this.providerClass = providerClass;
    }

    public void setValidationProviderResolver(ValidationProviderResolver validationProviderResolver) {
        this.validationProviderResolver = validationProviderResolver;
    }

    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public void setValidationMessageSource(MessageSource messageSource) {
        this.messageInterpolator = HibernateValidatorDelegate.buildMessageInterpolator(messageSource);
    }

    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public void setConstraintValidatorFactory(ConstraintValidatorFactory constraintValidatorFactory) {
        this.constraintValidatorFactory = constraintValidatorFactory;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setMappingLocations(Resource... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    public void setValidationProperties(Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap(jpaProperties, this.validationPropertyMap);
    }

    public void setValidationPropertyMap(@Nullable Map<String, String> validationProperties) {
        if (validationProperties != null) {
            this.validationPropertyMap.putAll(validationProperties);
        }
    }

    public Map<String, String> getValidationPropertyMap() {
        return this.validationPropertyMap;
    }

    public void setConfigurationInitializer(Consumer<Configuration<?>> configurationInitializer) {
        this.configurationInitializer = configurationInitializer;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        Configuration<?> configuration;
        if (this.providerClass != null) {
            ProviderSpecificBootstrap bootstrap = Validation.byProvider(this.providerClass);
            if (this.validationProviderResolver != null) {
                bootstrap = bootstrap.providerResolver(this.validationProviderResolver);
            }
            configuration = bootstrap.configure();
        } else {
            GenericBootstrap bootstrap2 = Validation.byDefaultProvider();
            if (this.validationProviderResolver != null) {
                bootstrap2 = bootstrap2.providerResolver(this.validationProviderResolver);
            }
            configuration = bootstrap2.configure();
        }
        if (this.applicationContext != null) {
            try {
                Method eclMethod = configuration.getClass().getMethod("externalClassLoader", ClassLoader.class);
                ReflectionUtils.invokeMethod(eclMethod, configuration, this.applicationContext.getClassLoader());
            } catch (NoSuchMethodException e) {
            }
        }
        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = configuration.getDefaultMessageInterpolator();
        }
        configuration.messageInterpolator(new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            configuration.traversableResolver(this.traversableResolver);
        }
        ConstraintValidatorFactory targetConstraintValidatorFactory = this.constraintValidatorFactory;
        if (targetConstraintValidatorFactory == null && this.applicationContext != null) {
            targetConstraintValidatorFactory = new SpringConstraintValidatorFactory(this.applicationContext.getAutowireCapableBeanFactory());
        }
        if (targetConstraintValidatorFactory != null) {
            configuration.constraintValidatorFactory(targetConstraintValidatorFactory);
        }
        if (this.parameterNameDiscoverer != null) {
            configureParameterNameProvider(this.parameterNameDiscoverer, configuration);
        }
        List<InputStream> mappingStreams = null;
        if (this.mappingLocations != null) {
            mappingStreams = new ArrayList<>(this.mappingLocations.length);
            for (Resource location : this.mappingLocations) {
                try {
                    InputStream stream = location.getInputStream();
                    mappingStreams.add(stream);
                    configuration.addMapping(stream);
                } catch (IOException e2) {
                    closeMappingStreams(mappingStreams);
                    throw new IllegalStateException("Cannot read mapping resource: " + location);
                }
            }
        }
        Map<String, String> map = this.validationPropertyMap;
        Configuration<?> configuration2 = configuration;
        Objects.requireNonNull(configuration2);
        map.forEach(configuration2::addProperty);
        if (this.configurationInitializer != null) {
            this.configurationInitializer.accept(configuration);
        }
        postProcessConfiguration(configuration);
        try {
            this.validatorFactory = configuration.buildValidatorFactory();
            setTargetValidator(this.validatorFactory.getValidator());
            closeMappingStreams(mappingStreams);
        } catch (Throwable th) {
            closeMappingStreams(mappingStreams);
            throw th;
        }
    }

    private void configureParameterNameProvider(final ParameterNameDiscoverer discoverer, Configuration<?> configuration) {
        final ParameterNameProvider defaultProvider = configuration.getDefaultParameterNameProvider();
        configuration.parameterNameProvider(new ParameterNameProvider() { // from class: org.springframework.validation.beanvalidation.LocalValidatorFactoryBean.1
            public List<String> getParameterNames(Constructor<?> constructor) {
                String[] paramNames = discoverer.getParameterNames(constructor);
                return paramNames != null ? Arrays.asList(paramNames) : defaultProvider.getParameterNames(constructor);
            }

            public List<String> getParameterNames(Method method) {
                String[] paramNames = discoverer.getParameterNames(method);
                return paramNames != null ? Arrays.asList(paramNames) : defaultProvider.getParameterNames(method);
            }
        });
    }

    private void closeMappingStreams(@Nullable List<InputStream> mappingStreams) {
        if (!CollectionUtils.isEmpty(mappingStreams)) {
            for (InputStream stream : mappingStreams) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected void postProcessConfiguration(Configuration<?> configuration) {
    }

    public Validator getValidator() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.getValidator();
    }

    public ValidatorContext usingContext() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.usingContext();
    }

    public MessageInterpolator getMessageInterpolator() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.getMessageInterpolator();
    }

    public TraversableResolver getTraversableResolver() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.getTraversableResolver();
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.getConstraintValidatorFactory();
    }

    public ParameterNameProvider getParameterNameProvider() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.getParameterNameProvider();
    }

    public ClockProvider getClockProvider() {
        Assert.state(this.validatorFactory != null, "No target ValidatorFactory set");
        return this.validatorFactory.getClockProvider();
    }

    @Override // org.springframework.validation.beanvalidation.SpringValidatorAdapter, org.springframework.validation.SmartValidator
    public <T> T unwrap(@Nullable Class<T> cls) {
        if (cls == null || !ValidatorFactory.class.isAssignableFrom(cls)) {
            try {
                return (T) super.unwrap(cls);
            } catch (ValidationException e) {
            }
        }
        if (this.validatorFactory != null) {
            try {
                return (T) this.validatorFactory.unwrap(cls);
            } catch (ValidationException e2) {
                if (ValidatorFactory.class == cls) {
                    return (T) this.validatorFactory;
                }
                throw e2;
            }
        }
        throw new ValidationException("Cannot unwrap to " + cls);
    }

    public void close() {
        if (this.validatorFactory != null) {
            this.validatorFactory.close();
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        close();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/LocalValidatorFactoryBean$HibernateValidatorDelegate.class */
    private static class HibernateValidatorDelegate {
        private HibernateValidatorDelegate() {
        }

        public static MessageInterpolator buildMessageInterpolator(MessageSource messageSource) {
            return new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(messageSource));
        }
    }
}
