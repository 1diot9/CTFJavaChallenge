package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/TypeMismatchException.class */
public class TypeMismatchException extends PropertyAccessException {
    public static final String ERROR_CODE = "typeMismatch";

    @Nullable
    private String propertyName;

    @Nullable
    private final transient Object value;

    @Nullable
    private final Class<?> requiredType;

    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class<?> requiredType) {
        this(propertyChangeEvent, requiredType, (Throwable) null);
    }

    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(propertyChangeEvent, "Failed to convert property value of type '" + ClassUtils.getDescriptiveType(propertyChangeEvent.getNewValue()) + "'" + (requiredType != null ? " to required type '" + ClassUtils.getQualifiedName(requiredType) + "'" : "") + (propertyChangeEvent.getPropertyName() != null ? " for property '" + propertyChangeEvent.getPropertyName() + "'" : "") + (cause != null ? "; " + cause.getMessage() : ""), cause);
        this.propertyName = propertyChangeEvent.getPropertyName();
        this.value = propertyChangeEvent.getNewValue();
        this.requiredType = requiredType;
    }

    public TypeMismatchException(@Nullable Object value, @Nullable Class<?> requiredType) {
        this(value, requiredType, (Throwable) null);
    }

    public TypeMismatchException(@Nullable Object value, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super("Failed to convert value of type '" + ClassUtils.getDescriptiveType(value) + "'" + (requiredType != null ? " to required type '" + ClassUtils.getQualifiedName(requiredType) + "'" : "") + (cause != null ? "; " + cause.getMessage() : ""), cause);
        this.value = value;
        this.requiredType = requiredType;
    }

    public void initPropertyName(String propertyName) {
        Assert.state(this.propertyName == null, "Property name already initialized");
        this.propertyName = propertyName;
    }

    @Override // org.springframework.beans.PropertyAccessException
    @Nullable
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override // org.springframework.beans.PropertyAccessException
    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Nullable
    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    @Override // org.springframework.beans.PropertyAccessException
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
