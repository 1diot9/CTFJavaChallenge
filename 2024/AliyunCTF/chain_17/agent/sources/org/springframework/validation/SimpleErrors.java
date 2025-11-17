package org.springframework.validation;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/SimpleErrors.class */
public class SimpleErrors implements Errors, Serializable {
    private final Object target;
    private final String objectName;
    private final List<ObjectError> globalErrors = new ArrayList();
    private final List<FieldError> fieldErrors = new ArrayList();

    public SimpleErrors(Object target) {
        Assert.notNull(target, "Target must not be null");
        this.target = target;
        this.objectName = this.target.getClass().getSimpleName();
    }

    public SimpleErrors(Object target, String objectName) {
        Assert.notNull(target, "Target must not be null");
        this.target = target;
        this.objectName = objectName;
    }

    @Override // org.springframework.validation.Errors
    public String getObjectName() {
        return this.objectName;
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.globalErrors.add(new ObjectError(getObjectName(), new String[]{errorCode}, errorArgs, defaultMessage));
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        if (!StringUtils.hasLength(field)) {
            reject(errorCode, errorArgs, defaultMessage);
        } else {
            Object newVal = getFieldValue(field);
            this.fieldErrors.add(new FieldError(getObjectName(), field, newVal, false, new String[]{errorCode}, errorArgs, defaultMessage));
        }
    }

    @Override // org.springframework.validation.Errors
    public void addAllErrors(Errors errors) {
        this.globalErrors.addAll(errors.getGlobalErrors());
        this.fieldErrors.addAll(errors.getFieldErrors());
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getGlobalErrors() {
        return this.globalErrors;
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors() {
        return this.fieldErrors;
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Object getFieldValue(String field) {
        FieldError fieldError = getFieldError(field);
        if (fieldError != null) {
            return fieldError.getRejectedValue();
        }
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(this.target.getClass(), field);
        if (pd != null && pd.getReadMethod() != null) {
            ReflectionUtils.makeAccessible(pd.getReadMethod());
            return ReflectionUtils.invokeMethod(pd.getReadMethod(), this.target);
        }
        Field rawField = ReflectionUtils.findField(this.target.getClass(), field);
        if (rawField != null) {
            ReflectionUtils.makeAccessible(rawField);
            return ReflectionUtils.getField(rawField, this.target);
        }
        throw new IllegalArgumentException("Cannot retrieve value for field '" + field + "' - neither a getter method nor a raw field found");
    }

    @Override // org.springframework.validation.Errors
    public Class<?> getFieldType(String field) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(this.target.getClass(), field);
        if (pd != null) {
            return pd.getPropertyType();
        }
        Field rawField = ReflectionUtils.findField(this.target.getClass(), field);
        if (rawField != null) {
            return rawField.getType();
        }
        return null;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof SimpleErrors) {
                SimpleErrors that = (SimpleErrors) other;
                if (!ObjectUtils.nullSafeEquals(this.target, that.target) || !this.globalErrors.equals(that.globalErrors) || !this.fieldErrors.equals(that.fieldErrors)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    @Override // org.springframework.validation.Errors
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : this.globalErrors) {
            sb.append('\n').append(error);
        }
        for (ObjectError error2 : this.fieldErrors) {
            sb.append('\n').append(error2);
        }
        return sb.toString();
    }
}
