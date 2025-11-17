package org.springframework.beans.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.GenericTypeAwareAutowireCandidateResolver;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/annotation/QualifierAnnotationAutowireCandidateResolver.class */
public class QualifierAnnotationAutowireCandidateResolver extends GenericTypeAwareAutowireCandidateResolver {
    private final Set<Class<? extends Annotation>> qualifierTypes = new LinkedHashSet(2);
    private Class<? extends Annotation> valueAnnotationType = Value.class;

    /* JADX WARN: Multi-variable type inference failed */
    public QualifierAnnotationAutowireCandidateResolver() {
        this.qualifierTypes.add(Qualifier.class);
        try {
            this.qualifierTypes.add(ClassUtils.forName("jakarta.inject.Qualifier", QualifierAnnotationAutowireCandidateResolver.class.getClassLoader()));
        } catch (ClassNotFoundException e) {
        }
    }

    public QualifierAnnotationAutowireCandidateResolver(Class<? extends Annotation> qualifierType) {
        Assert.notNull(qualifierType, "'qualifierType' must not be null");
        this.qualifierTypes.add(qualifierType);
    }

    public QualifierAnnotationAutowireCandidateResolver(Set<Class<? extends Annotation>> qualifierTypes) {
        Assert.notNull(qualifierTypes, "'qualifierTypes' must not be null");
        this.qualifierTypes.addAll(qualifierTypes);
    }

    public void addQualifierType(Class<? extends Annotation> qualifierType) {
        this.qualifierTypes.add(qualifierType);
    }

    public void setValueAnnotationType(Class<? extends Annotation> valueAnnotationType) {
        this.valueAnnotationType = valueAnnotationType;
    }

    @Override // org.springframework.beans.factory.support.GenericTypeAwareAutowireCandidateResolver, org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        MethodParameter methodParam;
        Method method;
        boolean match = super.isAutowireCandidate(bdHolder, descriptor);
        if (match) {
            match = checkQualifiers(bdHolder, descriptor.getAnnotations());
            if (match && (methodParam = descriptor.getMethodParameter()) != null && ((method = methodParam.getMethod()) == null || Void.TYPE == method.getReturnType())) {
                match = checkQualifiers(bdHolder, methodParam.getMethodAnnotations());
            }
        }
        return match;
    }

    protected boolean checkQualifiers(BeanDefinitionHolder bdHolder, Annotation[] annotationsToSearch) {
        if (ObjectUtils.isEmpty((Object[]) annotationsToSearch)) {
            return true;
        }
        SimpleTypeConverter typeConverter = new SimpleTypeConverter();
        for (Annotation annotation : annotationsToSearch) {
            Class<? extends Annotation> type = annotation.annotationType();
            boolean checkMeta = true;
            boolean fallbackToMeta = false;
            if (isQualifier(type)) {
                if (!checkQualifier(bdHolder, annotation, typeConverter)) {
                    fallbackToMeta = true;
                } else {
                    checkMeta = false;
                }
            }
            if (checkMeta) {
                boolean foundMeta = false;
                for (Annotation metaAnn : type.getAnnotations()) {
                    Class<? extends Annotation> metaType = metaAnn.annotationType();
                    if (isQualifier(metaType)) {
                        foundMeta = true;
                        if ((fallbackToMeta && ObjectUtils.isEmpty(AnnotationUtils.getValue(metaAnn))) || !checkQualifier(bdHolder, metaAnn, typeConverter)) {
                            return false;
                        }
                    }
                }
                if (fallbackToMeta && !foundMeta) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean isQualifier(Class<? extends Annotation> annotationType) {
        for (Class<? extends Annotation> qualifierType : this.qualifierTypes) {
            if (annotationType.equals(qualifierType) || annotationType.isAnnotationPresent(qualifierType)) {
                return true;
            }
        }
        return false;
    }

    protected boolean checkQualifier(BeanDefinitionHolder bdHolder, Annotation annotation, TypeConverter typeConverter) {
        RootBeanDefinition dbd;
        Class<? extends Annotation> type = annotation.annotationType();
        RootBeanDefinition bd = (RootBeanDefinition) bdHolder.getBeanDefinition();
        AutowireCandidateQualifier qualifier = bd.getQualifier(type.getName());
        if (qualifier == null) {
            qualifier = bd.getQualifier(ClassUtils.getShortName(type));
        }
        if (qualifier == null) {
            Annotation targetAnnotation = getQualifiedElementAnnotation(bd, type);
            if (targetAnnotation == null) {
                targetAnnotation = getFactoryMethodAnnotation(bd, type);
            }
            if (targetAnnotation == null && (dbd = getResolvedDecoratedDefinition(bd)) != null) {
                targetAnnotation = getFactoryMethodAnnotation(dbd, type);
            }
            if (targetAnnotation == null) {
                BeanFactory beanFactory = getBeanFactory();
                if (beanFactory != null) {
                    try {
                        Class<?> beanType = beanFactory.getType(bdHolder.getBeanName());
                        if (beanType != null) {
                            targetAnnotation = AnnotationUtils.getAnnotation((AnnotatedElement) ClassUtils.getUserClass(beanType), (Class<Annotation>) type);
                        }
                    } catch (NoSuchBeanDefinitionException e) {
                    }
                }
                if (targetAnnotation == null && bd.hasBeanClass()) {
                    targetAnnotation = AnnotationUtils.getAnnotation((AnnotatedElement) ClassUtils.getUserClass(bd.getBeanClass()), (Class<Annotation>) type);
                }
            }
            if (targetAnnotation != null && targetAnnotation.equals(annotation)) {
                return true;
            }
        }
        Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation);
        if (attributes.isEmpty() && qualifier == null) {
            return false;
        }
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String attributeName = entry.getKey();
            Object expectedValue = entry.getValue();
            Object actualValue = null;
            if (qualifier != null) {
                actualValue = qualifier.getAttribute(attributeName);
            }
            if (actualValue == null) {
                actualValue = bd.getAttribute(attributeName);
            }
            if (actualValue == null && attributeName.equals("value") && (expectedValue instanceof String)) {
                String name = (String) expectedValue;
                if (bdHolder.matchesName(name)) {
                    continue;
                }
            }
            if (actualValue == null && qualifier != null) {
                actualValue = AnnotationUtils.getDefaultValue(annotation, attributeName);
            }
            if (actualValue != null) {
                actualValue = typeConverter.convertIfNecessary(actualValue, expectedValue.getClass());
            }
            if (!expectedValue.equals(actualValue)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    protected Annotation getQualifiedElementAnnotation(RootBeanDefinition bd, Class<? extends Annotation> type) {
        AnnotatedElement qualifiedElement = bd.getQualifiedElement();
        if (qualifiedElement != null) {
            return AnnotationUtils.getAnnotation(qualifiedElement, type);
        }
        return null;
    }

    @Nullable
    protected Annotation getFactoryMethodAnnotation(RootBeanDefinition bd, Class<? extends Annotation> type) {
        Method resolvedFactoryMethod = bd.getResolvedFactoryMethod();
        if (resolvedFactoryMethod != null) {
            return AnnotationUtils.getAnnotation(resolvedFactoryMethod, (Class) type);
        }
        return null;
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    public boolean isRequired(DependencyDescriptor descriptor) {
        if (!super.isRequired(descriptor)) {
            return false;
        }
        Autowired autowired = (Autowired) descriptor.getAnnotation(Autowired.class);
        return autowired == null || autowired.required();
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    public boolean hasQualifier(DependencyDescriptor descriptor) {
        for (Annotation ann : descriptor.getAnnotations()) {
            if (isQualifier(ann.annotationType())) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.beans.factory.support.SimpleAutowireCandidateResolver, org.springframework.beans.factory.support.AutowireCandidateResolver
    @Nullable
    public Object getSuggestedValue(DependencyDescriptor descriptor) {
        MethodParameter methodParam;
        Object value = findValue(descriptor.getAnnotations());
        if (value == null && (methodParam = descriptor.getMethodParameter()) != null) {
            value = findValue(methodParam.getMethodAnnotations());
        }
        return value;
    }

    @Nullable
    protected Object findValue(Annotation[] annotationsToSearch) {
        AnnotationAttributes attr;
        if (annotationsToSearch.length > 0 && (attr = AnnotatedElementUtils.getMergedAnnotationAttributes(AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType)) != null) {
            return extractValue(attr);
        }
        return null;
    }

    protected Object extractValue(AnnotationAttributes attr) {
        Object value = attr.get("value");
        if (value == null) {
            throw new IllegalStateException("Value annotation must have a value attribute");
        }
        return value;
    }
}
