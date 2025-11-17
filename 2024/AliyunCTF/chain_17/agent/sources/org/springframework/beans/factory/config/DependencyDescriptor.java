package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/DependencyDescriptor.class */
public class DependencyDescriptor extends InjectionPoint implements Serializable {
    private final Class<?> declaringClass;

    @Nullable
    private String methodName;

    @Nullable
    private Class<?>[] parameterTypes;
    private int parameterIndex;

    @Nullable
    private String fieldName;
    private final boolean required;
    private final boolean eager;
    private int nestingLevel;

    @Nullable
    private Class<?> containingClass;

    @Nullable
    private volatile transient ResolvableType resolvableType;

    @Nullable
    private volatile transient TypeDescriptor typeDescriptor;

    public DependencyDescriptor(MethodParameter methodParameter, boolean required) {
        this(methodParameter, required, true);
    }

    public DependencyDescriptor(MethodParameter methodParameter, boolean required, boolean eager) {
        super(methodParameter);
        this.nestingLevel = 1;
        this.declaringClass = methodParameter.getDeclaringClass();
        if (methodParameter.getMethod() != null) {
            this.methodName = methodParameter.getMethod().getName();
        }
        this.parameterTypes = methodParameter.getExecutable().getParameterTypes();
        this.parameterIndex = methodParameter.getParameterIndex();
        this.containingClass = methodParameter.getContainingClass();
        this.required = required;
        this.eager = eager;
    }

    public DependencyDescriptor(Field field, boolean required) {
        this(field, required, true);
    }

    public DependencyDescriptor(Field field, boolean required, boolean eager) {
        super(field);
        this.nestingLevel = 1;
        this.declaringClass = field.getDeclaringClass();
        this.fieldName = field.getName();
        this.required = required;
        this.eager = eager;
    }

    public DependencyDescriptor(DependencyDescriptor original) {
        super(original);
        this.nestingLevel = 1;
        this.declaringClass = original.declaringClass;
        this.methodName = original.methodName;
        this.parameterTypes = original.parameterTypes;
        this.parameterIndex = original.parameterIndex;
        this.fieldName = original.fieldName;
        this.containingClass = original.containingClass;
        this.required = original.required;
        this.eager = original.eager;
        this.nestingLevel = original.nestingLevel;
    }

    public boolean isRequired() {
        if (this.required) {
            return this.field != null ? (this.field.getType() == Optional.class || hasNullableAnnotation() || (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(this.field.getDeclaringClass()) && KotlinDelegate.isNullable(this.field))) ? false : true : !obtainMethodParameter().isOptional();
        }
        return false;
    }

    private boolean hasNullableAnnotation() {
        for (Annotation ann : getAnnotations()) {
            if ("Nullable".equals(ann.annotationType().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEager() {
        return this.eager;
    }

    @Nullable
    public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) throws BeansException {
        throw new NoUniqueBeanDefinitionException(type, matchingBeans.keySet());
    }

    @Nullable
    public Object resolveShortcut(BeanFactory beanFactory) throws BeansException {
        return null;
    }

    public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    public void increaseNestingLevel() {
        this.nestingLevel++;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            this.methodParameter = this.methodParameter.nested();
        }
    }

    public void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.resolvableType = null;
        if (this.methodParameter != null) {
            this.methodParameter = this.methodParameter.withContainingClass(containingClass);
        }
    }

    public ResolvableType getResolvableType() {
        ResolvableType forMethodParameter;
        ResolvableType resolvableType = this.resolvableType;
        if (resolvableType == null) {
            if (this.field != null) {
                forMethodParameter = ResolvableType.forField(this.field, this.nestingLevel, this.containingClass);
            } else {
                forMethodParameter = ResolvableType.forMethodParameter(obtainMethodParameter());
            }
            resolvableType = forMethodParameter;
            this.resolvableType = resolvableType;
        }
        return resolvableType;
    }

    public TypeDescriptor getTypeDescriptor() {
        TypeDescriptor typeDescriptor;
        TypeDescriptor typeDescriptor2 = this.typeDescriptor;
        if (typeDescriptor2 == null) {
            if (this.field != null) {
                typeDescriptor = new TypeDescriptor(getResolvableType(), getDependencyType(), getAnnotations());
            } else {
                typeDescriptor = new TypeDescriptor(obtainMethodParameter());
            }
            typeDescriptor2 = typeDescriptor;
            this.typeDescriptor = typeDescriptor2;
        }
        return typeDescriptor2;
    }

    public boolean fallbackMatchAllowed() {
        return false;
    }

    public DependencyDescriptor forFallbackMatch() {
        return new DependencyDescriptor(this) { // from class: org.springframework.beans.factory.config.DependencyDescriptor.1
            @Override // org.springframework.beans.factory.config.DependencyDescriptor
            public boolean fallbackMatchAllowed() {
                return true;
            }
        };
    }

    public void initParameterNameDiscovery(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        if (this.methodParameter != null) {
            this.methodParameter.initParameterNameDiscovery(parameterNameDiscoverer);
        }
    }

    @Nullable
    public String getDependencyName() {
        return this.field != null ? this.field.getName() : obtainMethodParameter().getParameterName();
    }

    public Class<?> getDependencyType() {
        if (this.field != null) {
            if (this.nestingLevel > 1) {
                Class<?> clazz = getResolvableType().getRawClass();
                return clazz != null ? clazz : Object.class;
            }
            return this.field.getType();
        }
        return obtainMethodParameter().getNestedParameterType();
    }

    public boolean supportsLazyResolution() {
        return true;
    }

    @Override // org.springframework.beans.factory.InjectionPoint
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        DependencyDescriptor otherDesc = (DependencyDescriptor) other;
        return this.required == otherDesc.required && this.eager == otherDesc.eager && this.nestingLevel == otherDesc.nestingLevel && this.containingClass == otherDesc.containingClass;
    }

    @Override // org.springframework.beans.factory.InjectionPoint
    public int hashCode() {
        return (31 * super.hashCode()) + ObjectUtils.nullSafeHashCode(this.containingClass);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        try {
            if (this.fieldName != null) {
                this.field = this.declaringClass.getDeclaredField(this.fieldName);
            } else {
                if (this.methodName != null) {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes), this.parameterIndex);
                } else {
                    this.methodParameter = new MethodParameter(this.declaringClass.getDeclaredConstructor(this.parameterTypes), this.parameterIndex);
                }
                for (int i = 1; i < this.nestingLevel; i++) {
                    this.methodParameter = this.methodParameter.nested();
                }
            }
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not find original class structure", ex);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/DependencyDescriptor$KotlinDelegate.class */
    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static boolean isNullable(Field field) {
            KProperty<?> property = ReflectJvmMapping.getKotlinProperty(field);
            return property != null && property.getReturnType().isMarkedNullable();
        }
    }
}
