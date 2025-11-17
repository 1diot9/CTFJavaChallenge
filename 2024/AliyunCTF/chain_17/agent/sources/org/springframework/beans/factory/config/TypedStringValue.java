package org.springframework.beans.factory.config;

import java.util.Comparator;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/TypedStringValue.class */
public class TypedStringValue implements BeanMetadataElement, Comparable<TypedStringValue> {

    @Nullable
    private String value;

    @Nullable
    private volatile Object targetType;

    @Nullable
    private Object source;

    @Nullable
    private String specifiedTypeName;
    private volatile boolean dynamic;

    public TypedStringValue(@Nullable String value) {
        setValue(value);
    }

    public TypedStringValue(@Nullable String value, Class<?> targetType) {
        setValue(value);
        setTargetType(targetType);
    }

    public TypedStringValue(@Nullable String value, String targetTypeName) {
        setValue(value);
        setTargetTypeName(targetTypeName);
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public void setTargetType(Class<?> targetType) {
        Assert.notNull(targetType, "'targetType' must not be null");
        this.targetType = targetType;
    }

    public Class<?> getTargetType() {
        Object targetTypeValue = this.targetType;
        if (!(targetTypeValue instanceof Class)) {
            throw new IllegalStateException("Typed String value does not carry a resolved target type");
        }
        Class<?> clazz = (Class) targetTypeValue;
        return clazz;
    }

    public void setTargetTypeName(@Nullable String targetTypeName) {
        this.targetType = targetTypeName;
    }

    @Nullable
    public String getTargetTypeName() {
        Object targetTypeValue = this.targetType;
        if (targetTypeValue instanceof Class) {
            Class<?> clazz = (Class) targetTypeValue;
            return clazz.getName();
        }
        return (String) targetTypeValue;
    }

    public boolean hasTargetType() {
        return this.targetType instanceof Class;
    }

    @Nullable
    public Class<?> resolveTargetType(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
        String typeName = getTargetTypeName();
        if (typeName == null) {
            return null;
        }
        Class<?> resolvedClass = ClassUtils.forName(typeName, classLoader);
        this.targetType = resolvedClass;
        return resolvedClass;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void setSpecifiedTypeName(@Nullable String specifiedTypeName) {
        this.specifiedTypeName = specifiedTypeName;
    }

    @Nullable
    public String getSpecifiedTypeName() {
        return this.specifiedTypeName;
    }

    public void setDynamic() {
        this.dynamic = true;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    @Override // java.lang.Comparable
    public int compareTo(@Nullable TypedStringValue o) {
        return Comparator.comparing((v0) -> {
            return v0.getValue();
        }).compare(this, o);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof TypedStringValue) {
                TypedStringValue that = (TypedStringValue) other;
                if (!ObjectUtils.nullSafeEquals(this.value, that.value) || !ObjectUtils.nullSafeEquals(this.targetType, that.targetType)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.value, this.targetType);
    }

    public String toString() {
        return "TypedStringValue: value [" + this.value + "], target type [" + this.targetType + "]";
    }
}
