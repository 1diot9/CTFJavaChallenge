package org.springframework.validation;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/BindException.class */
public class BindException extends Exception implements BindingResult {
    private final BindingResult bindingResult;

    public BindException(BindingResult bindingResult) {
        Assert.notNull(bindingResult, "BindingResult must not be null");
        this.bindingResult = bindingResult;
    }

    public BindException(Object target, String objectName) {
        Assert.notNull(target, "Target object must not be null");
        this.bindingResult = new BeanPropertyBindingResult(target, objectName);
    }

    public final BindingResult getBindingResult() {
        return this.bindingResult;
    }

    @Override // org.springframework.validation.Errors
    public String getObjectName() {
        return this.bindingResult.getObjectName();
    }

    @Override // org.springframework.validation.Errors
    public void setNestedPath(String nestedPath) {
        this.bindingResult.setNestedPath(nestedPath);
    }

    @Override // org.springframework.validation.Errors
    public String getNestedPath() {
        return this.bindingResult.getNestedPath();
    }

    @Override // org.springframework.validation.Errors
    public void pushNestedPath(String subPath) {
        this.bindingResult.pushNestedPath(subPath);
    }

    @Override // org.springframework.validation.Errors
    public void popNestedPath() throws IllegalStateException {
        this.bindingResult.popNestedPath();
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode) {
        this.bindingResult.reject(errorCode);
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, String defaultMessage) {
        this.bindingResult.reject(errorCode, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.bindingResult.reject(errorCode, errorArgs, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode) {
        this.bindingResult.rejectValue(field, errorCode);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        this.bindingResult.rejectValue(field, errorCode, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.bindingResult.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void addAllErrors(Errors errors) {
        this.bindingResult.addAllErrors(errors);
    }

    @Override // org.springframework.validation.Errors
    public boolean hasErrors() {
        return this.bindingResult.hasErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getErrorCount() {
        return this.bindingResult.getErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getAllErrors() {
        return this.bindingResult.getAllErrors();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasGlobalErrors() {
        return this.bindingResult.hasGlobalErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getGlobalErrorCount() {
        return this.bindingResult.getGlobalErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getGlobalErrors() {
        return this.bindingResult.getGlobalErrors();
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public ObjectError getGlobalError() {
        return this.bindingResult.getGlobalError();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasFieldErrors() {
        return this.bindingResult.hasFieldErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getFieldErrorCount() {
        return this.bindingResult.getFieldErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors() {
        return this.bindingResult.getFieldErrors();
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public FieldError getFieldError() {
        return this.bindingResult.getFieldError();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasFieldErrors(String field) {
        return this.bindingResult.hasFieldErrors(field);
    }

    @Override // org.springframework.validation.Errors
    public int getFieldErrorCount(String field) {
        return this.bindingResult.getFieldErrorCount(field);
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors(String field) {
        return this.bindingResult.getFieldErrors(field);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public FieldError getFieldError(String field) {
        return this.bindingResult.getFieldError(field);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Object getFieldValue(String field) {
        return this.bindingResult.getFieldValue(field);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Class<?> getFieldType(String field) {
        return this.bindingResult.getFieldType(field);
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public Object getTarget() {
        return this.bindingResult.getTarget();
    }

    @Override // org.springframework.validation.BindingResult
    public Map<String, Object> getModel() {
        return this.bindingResult.getModel();
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public Object getRawFieldValue(String field) {
        return this.bindingResult.getRawFieldValue(field);
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public PropertyEditor findEditor(@Nullable String field, @Nullable Class valueType) {
        return this.bindingResult.findEditor(field, valueType);
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return this.bindingResult.getPropertyEditorRegistry();
    }

    @Override // org.springframework.validation.BindingResult
    public String[] resolveMessageCodes(String errorCode) {
        return this.bindingResult.resolveMessageCodes(errorCode);
    }

    @Override // org.springframework.validation.BindingResult
    public String[] resolveMessageCodes(String errorCode, String field) {
        return this.bindingResult.resolveMessageCodes(errorCode, field);
    }

    @Override // org.springframework.validation.BindingResult
    public void addError(ObjectError error) {
        this.bindingResult.addError(error);
    }

    @Override // org.springframework.validation.BindingResult
    public void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
        this.bindingResult.recordFieldValue(field, type, value);
    }

    @Override // org.springframework.validation.BindingResult
    public void recordSuppressedField(String field) {
        this.bindingResult.recordSuppressedField(field);
    }

    @Override // org.springframework.validation.BindingResult
    public String[] getSuppressedFields() {
        return this.bindingResult.getSuppressedFields();
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.bindingResult.toString();
    }

    public boolean equals(@Nullable Object other) {
        return this == other || this.bindingResult.equals(other);
    }

    public int hashCode() {
        return this.bindingResult.hashCode();
    }
}
