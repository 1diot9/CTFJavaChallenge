package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/AnnotationBeanNameGenerator.class */
public class AnnotationBeanNameGenerator implements BeanNameGenerator {
    private static final String COMPONENT_ANNOTATION_CLASSNAME = "org.springframework.stereotype.Component";
    private final Map<String, Set<String>> metaAnnotationTypesCache = new ConcurrentHashMap();
    public static final AnnotationBeanNameGenerator INSTANCE = new AnnotationBeanNameGenerator();
    private static final MergedAnnotation.Adapt[] ADAPTATIONS = MergedAnnotation.Adapt.values(false, true);
    private static final Log logger = LogFactory.getLog((Class<?>) AnnotationBeanNameGenerator.class);
    private static final Set<String> conventionBasedStereotypeCheckCache = ConcurrentHashMap.newKeySet();

    @Override // org.springframework.beans.factory.support.BeanNameGenerator
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) definition;
            String beanName = determineBeanNameFromAnnotation(annotatedBeanDefinition);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        return buildDefaultBeanName(definition, registry);
    }

    @Nullable
    protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
        AnnotationMetadata metadata = annotatedDef.getMetadata();
        String beanName = getExplicitBeanName(metadata);
        if (beanName != null) {
            return beanName;
        }
        List<MergedAnnotation<Annotation>> mergedAnnotations = metadata.getAnnotations().stream().filter((v0) -> {
            return v0.isDirectlyPresent();
        }).toList();
        Set<AnnotationAttributes> visited = new HashSet<>();
        for (MergedAnnotation<Annotation> mergedAnnotation : mergedAnnotations) {
            AnnotationAttributes attributes = mergedAnnotation.asAnnotationAttributes(ADAPTATIONS);
            if (visited.add(attributes)) {
                String annotationType = mergedAnnotation.getType().getName();
                Set<String> metaAnnotationTypes = this.metaAnnotationTypesCache.computeIfAbsent(annotationType, key -> {
                    return getMetaAnnotationTypes(mergedAnnotation);
                });
                if (isStereotypeWithNameValue(annotationType, metaAnnotationTypes, attributes)) {
                    Object value = attributes.get("value");
                    if (value instanceof String) {
                        String currentName = (String) value;
                        if (currentName.isBlank()) {
                            continue;
                        } else {
                            if (conventionBasedStereotypeCheckCache.add(annotationType) && metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) && logger.isWarnEnabled()) {
                                logger.warn("Support for convention-based stereotype names is deprecated and will be removed in a future version of the framework. Please annotate the 'value' attribute in @%s with @AliasFor(annotation=Component.class) to declare an explicit alias for @Component's 'value' attribute.".formatted(annotationType));
                            }
                            if (beanName != null && !currentName.equals(beanName)) {
                                throw new IllegalStateException("Stereotype annotations suggest inconsistent component names: '" + beanName + "' versus '" + currentName + "'");
                            }
                            beanName = currentName;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        return beanName;
    }

    private Set<String> getMetaAnnotationTypes(MergedAnnotation<Annotation> mergedAnnotation) {
        Set<String> result = (Set) MergedAnnotations.from(mergedAnnotation.getType()).stream().map(metaAnnotation -> {
            return metaAnnotation.getType().getName();
        }).collect(Collectors.toCollection(LinkedHashSet::new));
        return result.isEmpty() ? Collections.emptySet() : result;
    }

    @Nullable
    private String getExplicitBeanName(AnnotationMetadata metadata) {
        List<String> names = metadata.getAnnotations().stream(COMPONENT_ANNOTATION_CLASSNAME).map(annotation -> {
            return annotation.getString("value");
        }).filter(StringUtils::hasText).map((v0) -> {
            return v0.trim();
        }).distinct().toList();
        if (names.size() == 1) {
            return names.get(0);
        }
        if (names.size() > 1) {
            throw new IllegalStateException("Stereotype annotations suggest inconsistent component names: " + names);
        }
        return null;
    }

    protected boolean isStereotypeWithNameValue(String annotationType, Set<String> metaAnnotationTypes, Map<String, Object> attributes) {
        boolean isStereotype = metaAnnotationTypes.contains(COMPONENT_ANNOTATION_CLASSNAME) || annotationType.equals("jakarta.annotation.ManagedBean") || annotationType.equals("javax.annotation.ManagedBean") || annotationType.equals("jakarta.inject.Named") || annotationType.equals("javax.inject.Named");
        return isStereotype && attributes.containsKey("value");
    }

    protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return buildDefaultBeanName(definition);
    }

    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state(beanClassName != null, "No bean class name set");
        String shortClassName = ClassUtils.getShortName(beanClassName);
        return StringUtils.uncapitalizeAsProperty(shortClassName);
    }
}
