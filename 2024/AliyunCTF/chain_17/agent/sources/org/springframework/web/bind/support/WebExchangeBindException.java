package org.springframework.web.bind.support;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.BindErrorUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/WebExchangeBindException.class */
public class WebExchangeBindException extends ServerWebInputException implements BindingResult {
    private final BindingResult bindingResult;

    public WebExchangeBindException(MethodParameter parameter, BindingResult bindingResult) {
        super("Validation failure", parameter, null, null, null);
        this.bindingResult = bindingResult;
        getBody().setDetail("Invalid request content.");
    }

    public final BindingResult getBindingResult() {
        return this.bindingResult;
    }

    @Override // org.springframework.web.ErrorResponseException, org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments() {
        return new Object[]{BindErrorUtils.resolveAndJoin(getGlobalErrors()), BindErrorUtils.resolveAndJoin(getFieldErrors())};
    }

    @Override // org.springframework.web.ErrorResponse
    public Object[] getDetailMessageArguments(MessageSource source, Locale locale) {
        return new Object[]{BindErrorUtils.resolveAndJoin(getGlobalErrors(), source, locale), BindErrorUtils.resolveAndJoin(getFieldErrors(), source, locale)};
    }

    @Deprecated(since = "6.1", forRemoval = true)
    public Map<ObjectError, String> resolveErrorMessages(MessageSource messageSource, Locale locale) {
        return BindErrorUtils.resolve(getAllErrors(), messageSource, locale);
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

    @Override // org.springframework.web.server.ResponseStatusException, org.springframework.web.ErrorResponseException, java.lang.Throwable
    public String getMessage() {
        MethodParameter parameter = getMethodParameter();
        Assert.state(parameter != null, "No MethodParameter");
        StringBuilder sb = new StringBuilder("Validation failed for argument at index ").append(parameter.getParameterIndex()).append(" in method: ").append(parameter.getExecutable().toGenericString()).append(", with ").append(getErrorCount()).append(" error(s): ");
        for (ObjectError error : getAllErrors()) {
            sb.append('[').append(error).append("] ");
        }
        return sb.toString();
    }

    public boolean equals(@Nullable Object other) {
        return this == other || this.bindingResult.equals(other);
    }

    public int hashCode() {
        return this.bindingResult.hashCode();
    }
}
