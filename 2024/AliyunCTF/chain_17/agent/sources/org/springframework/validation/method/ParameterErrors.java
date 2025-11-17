package org.springframework.validation.method;

import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/ParameterErrors.class */
public class ParameterErrors extends ParameterValidationResult implements Errors {
    private final Errors errors;

    public ParameterErrors(MethodParameter parameter, @Nullable Object argument, Errors errors, @Nullable Object container, @Nullable Integer index, @Nullable Object key) {
        super(parameter, argument, errors.getAllErrors(), container, index, key);
        this.errors = errors;
    }

    @Override // org.springframework.validation.Errors
    public String getObjectName() {
        return this.errors.getObjectName();
    }

    @Override // org.springframework.validation.Errors
    public void setNestedPath(String nestedPath) {
        this.errors.setNestedPath(nestedPath);
    }

    @Override // org.springframework.validation.Errors
    public String getNestedPath() {
        return this.errors.getNestedPath();
    }

    @Override // org.springframework.validation.Errors
    public void pushNestedPath(String subPath) {
        this.errors.pushNestedPath(subPath);
    }

    @Override // org.springframework.validation.Errors
    public void popNestedPath() throws IllegalStateException {
        this.errors.popNestedPath();
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode) {
        this.errors.reject(errorCode);
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, String defaultMessage) {
        this.errors.reject(errorCode, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.errors.reject(errorCode, errorArgs, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode) {
        this.errors.rejectValue(field, errorCode);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        this.errors.rejectValue(field, errorCode, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.errors.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void addAllErrors(Errors errors) {
        this.errors.addAllErrors(errors);
    }

    @Override // org.springframework.validation.Errors
    public boolean hasErrors() {
        return this.errors.hasErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getErrorCount() {
        return this.errors.getErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getAllErrors() {
        return this.errors.getAllErrors();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasGlobalErrors() {
        return this.errors.hasGlobalErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getGlobalErrorCount() {
        return this.errors.getGlobalErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getGlobalErrors() {
        return this.errors.getGlobalErrors();
    }

    @Override // org.springframework.validation.Errors
    public ObjectError getGlobalError() {
        return this.errors.getGlobalError();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasFieldErrors() {
        return this.errors.hasFieldErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getFieldErrorCount() {
        return this.errors.getFieldErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors() {
        return this.errors.getFieldErrors();
    }

    @Override // org.springframework.validation.Errors
    public FieldError getFieldError() {
        return this.errors.getFieldError();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasFieldErrors(String field) {
        return this.errors.hasFieldErrors(field);
    }

    @Override // org.springframework.validation.Errors
    public int getFieldErrorCount(String field) {
        return this.errors.getFieldErrorCount(field);
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors(String field) {
        return this.errors.getFieldErrors(field);
    }

    @Override // org.springframework.validation.Errors
    public FieldError getFieldError(String field) {
        return this.errors.getFieldError(field);
    }

    @Override // org.springframework.validation.Errors
    public Object getFieldValue(String field) {
        return this.errors.getFieldError(field);
    }

    @Override // org.springframework.validation.Errors
    public Class<?> getFieldType(String field) {
        return this.errors.getFieldType(field);
    }
}
