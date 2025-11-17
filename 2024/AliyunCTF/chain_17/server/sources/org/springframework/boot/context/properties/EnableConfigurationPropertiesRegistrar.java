package org.springframework.boot.context.properties;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Conventions;
import org.springframework.core.type.AnnotationMetadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/EnableConfigurationPropertiesRegistrar.class */
class EnableConfigurationPropertiesRegistrar implements ImportBeanDefinitionRegistrar {
    private static final String METHOD_VALIDATION_EXCLUDE_FILTER_BEAN_NAME = Conventions.getQualifiedAttributeName(EnableConfigurationPropertiesRegistrar.class, "methodValidationExcludeFilter");

    EnableConfigurationPropertiesRegistrar() {
    }

    @Override // org.springframework.context.annotation.ImportBeanDefinitionRegistrar
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerInfrastructureBeans(registry);
        registerMethodValidationExcludeFilter(registry);
        ConfigurationPropertiesBeanRegistrar beanRegistrar = new ConfigurationPropertiesBeanRegistrar(registry);
        Set<Class<?>> types = getTypes(metadata);
        Objects.requireNonNull(beanRegistrar);
        types.forEach(beanRegistrar::register);
    }

    private Set<Class<?>> getTypes(AnnotationMetadata metadata) {
        return (Set) metadata.getAnnotations().stream(EnableConfigurationProperties.class).flatMap(annotation -> {
            return Arrays.stream(annotation.getClassArray("value"));
        }).filter(type -> {
            return Void.TYPE != type;
        }).collect(Collectors.toSet());
    }

    static void registerInfrastructureBeans(BeanDefinitionRegistry registry) {
        ConfigurationPropertiesBindingPostProcessor.register(registry);
        BoundConfigurationProperties.register(registry);
    }

    static void registerMethodValidationExcludeFilter(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(METHOD_VALIDATION_EXCLUDE_FILTER_BEAN_NAME)) {
            BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) MethodValidationExcludeFilter.class, "byAnnotation").addConstructorArgValue(ConfigurationProperties.class).setRole(2).getBeanDefinition();
            registry.registerBeanDefinition(METHOD_VALIDATION_EXCLUDE_FILTER_BEAN_NAME, definition);
        }
    }
}
