package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConditionEvaluator.class */
public class ConditionEvaluator {
    private final ConditionContextImpl context;

    public ConditionEvaluator(@Nullable BeanDefinitionRegistry registry, @Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {
        this.context = new ConditionContextImpl(registry, environment, resourceLoader);
    }

    public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
        return shouldSkip(metadata, null);
    }

    public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationCondition.ConfigurationPhase phase) {
        if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
            return false;
        }
        if (phase == null) {
            if (metadata instanceof AnnotationMetadata) {
                AnnotationMetadata annotationMetadata = (AnnotationMetadata) metadata;
                if (ConfigurationClassUtils.isConfigurationCandidate(annotationMetadata)) {
                    return shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
                }
            }
            return shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }
        List<Condition> conditions = new ArrayList<>();
        for (String[] conditionClasses : getConditionClasses(metadata)) {
            for (String conditionClass : conditionClasses) {
                conditions.add(getCondition(conditionClass, this.context.getClassLoader()));
            }
        }
        AnnotationAwareOrderComparator.sort(conditions);
        for (Condition condition : conditions) {
            ConfigurationCondition.ConfigurationPhase requiredPhase = null;
            if (condition instanceof ConfigurationCondition) {
                ConfigurationCondition configurationCondition = (ConfigurationCondition) condition;
                requiredPhase = configurationCondition.getConfigurationPhase();
            }
            if (requiredPhase == null || requiredPhase == phase) {
                if (!condition.matches(this.context, metadata)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
        Object values = attributes != null ? attributes.get("value") : null;
        return (List) (values != null ? values : Collections.emptyList());
    }

    private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
        Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
        return (Condition) BeanUtils.instantiateClass(conditionClass);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/ConditionEvaluator$ConditionContextImpl.class */
    public static class ConditionContextImpl implements ConditionContext {

        @Nullable
        private final BeanDefinitionRegistry registry;

        @Nullable
        private final ConfigurableListableBeanFactory beanFactory;
        private final Environment environment;
        private final ResourceLoader resourceLoader;

        @Nullable
        private final ClassLoader classLoader;

        public ConditionContextImpl(@Nullable BeanDefinitionRegistry registry, @Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {
            this.registry = registry;
            this.beanFactory = deduceBeanFactory(registry);
            this.environment = environment != null ? environment : deduceEnvironment(registry);
            this.resourceLoader = resourceLoader != null ? resourceLoader : deduceResourceLoader(registry);
            this.classLoader = deduceClassLoader(resourceLoader, this.beanFactory);
        }

        @Nullable
        private static ConfigurableListableBeanFactory deduceBeanFactory(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof ConfigurableListableBeanFactory) {
                ConfigurableListableBeanFactory configurableListableBeanFactory = (ConfigurableListableBeanFactory) source;
                return configurableListableBeanFactory;
            }
            if (source instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) source;
                return configurableApplicationContext.getBeanFactory();
            }
            return null;
        }

        private static Environment deduceEnvironment(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof EnvironmentCapable) {
                EnvironmentCapable environmentCapable = (EnvironmentCapable) source;
                return environmentCapable.getEnvironment();
            }
            return new StandardEnvironment();
        }

        private static ResourceLoader deduceResourceLoader(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof ResourceLoader) {
                ResourceLoader resourceLoader = (ResourceLoader) source;
                return resourceLoader;
            }
            return new DefaultResourceLoader();
        }

        @Nullable
        private static ClassLoader deduceClassLoader(@Nullable ResourceLoader resourceLoader, @Nullable ConfigurableListableBeanFactory beanFactory) {
            ClassLoader classLoader;
            if (resourceLoader != null && (classLoader = resourceLoader.getClassLoader()) != null) {
                return classLoader;
            }
            if (beanFactory != null) {
                return beanFactory.getBeanClassLoader();
            }
            return ClassUtils.getDefaultClassLoader();
        }

        @Override // org.springframework.context.annotation.ConditionContext
        public BeanDefinitionRegistry getRegistry() {
            Assert.state(this.registry != null, "No BeanDefinitionRegistry available");
            return this.registry;
        }

        @Override // org.springframework.context.annotation.ConditionContext
        @Nullable
        public ConfigurableListableBeanFactory getBeanFactory() {
            return this.beanFactory;
        }

        @Override // org.springframework.context.annotation.ConditionContext
        public Environment getEnvironment() {
            return this.environment;
        }

        @Override // org.springframework.context.annotation.ConditionContext
        public ResourceLoader getResourceLoader() {
            return this.resourceLoader;
        }

        @Override // org.springframework.context.annotation.ConditionContext
        @Nullable
        public ClassLoader getClassLoader() {
            return this.classLoader;
        }
    }
}
