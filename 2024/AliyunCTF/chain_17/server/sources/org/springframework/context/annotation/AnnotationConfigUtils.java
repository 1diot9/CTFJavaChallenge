package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/AnnotationConfigUtils.class */
public abstract class AnnotationConfigUtils {
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalConfigurationAnnotationProcessor";
    public static final String CONFIGURATION_BEAN_NAME_GENERATOR = "org.springframework.context.annotation.internalConfigurationBeanNameGenerator";
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";
    public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalCommonAnnotationProcessor";
    public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalPersistenceAnnotationProcessor";
    private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME = "org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";
    public static final String EVENT_LISTENER_PROCESSOR_BEAN_NAME = "org.springframework.context.event.internalEventListenerProcessor";
    public static final String EVENT_LISTENER_FACTORY_BEAN_NAME = "org.springframework.context.event.internalEventListenerFactory";
    private static final ClassLoader classLoader = AnnotationConfigUtils.class.getClassLoader();
    private static final boolean jakartaAnnotationsPresent = ClassUtils.isPresent("jakarta.annotation.PostConstruct", classLoader);
    private static final boolean jsr250Present = ClassUtils.isPresent("javax.annotation.PostConstruct", classLoader);
    private static final boolean jpaPresent;

    static {
        jpaPresent = ClassUtils.isPresent("jakarta.persistence.EntityManagerFactory", classLoader) && ClassUtils.isPresent(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, classLoader);
    }

    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }

    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(BeanDefinitionRegistry registry, @Nullable Object source) {
        DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
        if (beanFactory != null) {
            if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
                beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
            }
            if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
                beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
            }
        }
        Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);
        if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition((Class<?>) ConfigurationClassPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def2 = new RootBeanDefinition((Class<?>) AutowiredAnnotationBeanPostProcessor.class);
            def2.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def2, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if ((jakartaAnnotationsPresent || jsr250Present) && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def3 = new RootBeanDefinition((Class<?>) CommonAnnotationBeanPostProcessor.class);
            def3.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def3, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def4 = new RootBeanDefinition();
            try {
                def4.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, AnnotationConfigUtils.class.getClassLoader()));
                def4.setSource(source);
                beanDefs.add(registerPostProcessor(registry, def4, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Cannot load optional framework class: org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor", ex);
            }
        }
        if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def5 = new RootBeanDefinition((Class<?>) EventListenerMethodProcessor.class);
            def5.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def5, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        }
        if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
            RootBeanDefinition def6 = new RootBeanDefinition((Class<?>) DefaultEventListenerFactory.class);
            def6.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def6, EVENT_LISTENER_FACTORY_BEAN_NAME));
        }
        return beanDefs;
    }

    private static BeanDefinitionHolder registerPostProcessor(BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {
        definition.setRole(2);
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(definition, beanName);
    }

    @Nullable
    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
        if (registry instanceof DefaultListableBeanFactory) {
            DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) registry;
            return dlbf;
        }
        if (registry instanceof GenericApplicationContext) {
            GenericApplicationContext gac = (GenericApplicationContext) registry;
            return gac.getDefaultListableBeanFactory();
        }
        return null;
    }

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        processCommonDefinitionAnnotations(abd, abd.getMetadata());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
        AnnotationAttributes lazy;
        AnnotationAttributes lazy2 = attributesFor(metadata, (Class<?>) Lazy.class);
        if (lazy2 != null) {
            abd.setLazyInit(lazy2.getBoolean("value"));
        } else if (abd.getMetadata() != metadata && (lazy = attributesFor(abd.getMetadata(), (Class<?>) Lazy.class)) != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        }
        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        AnnotationAttributes dependsOn = attributesFor(metadata, (Class<?>) DependsOn.class);
        if (dependsOn != null) {
            abd.setDependsOn(dependsOn.getStringArray("value"));
        }
        AnnotationAttributes role = attributesFor(metadata, (Class<?>) Role.class);
        if (role != null) {
            abd.setRole(role.getNumber("value").intValue());
        }
        AnnotationAttributes description = attributesFor(metadata, (Class<?>) Description.class);
        if (description != null) {
            abd.setDescription(description.getString("value"));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BeanDefinitionHolder applyScopedProxyMode(ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<?> annotationType) {
        return attributesFor(metadata, annotationType.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, String annotationTypeName) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationTypeName));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata, Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType, Predicate<MergedAnnotation<? extends Annotation>> predicate) {
        return metadata.getMergedRepeatableAnnotationAttributes(annotationType, containerType, predicate, false, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata, Class<? extends Annotation> annotationType, Class<? extends Annotation> containerType, boolean sortByReversedMetaDistance) {
        return metadata.getMergedRepeatableAnnotationAttributes(annotationType, containerType, false, sortByReversedMetaDistance);
    }
}
