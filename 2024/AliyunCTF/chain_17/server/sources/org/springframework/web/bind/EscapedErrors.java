package org.springframework.web.bind;

import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.util.HtmlUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/EscapedErrors.class */
public class EscapedErrors implements Errors {
    private final Errors source;

    public EscapedErrors(Errors source) {
        Assert.notNull(source, "Errors source must not be null");
        this.source = source;
    }

    public Errors getSource() {
        return this.source;
    }

    @Override // org.springframework.validation.Errors
    public String getObjectName() {
        return this.source.getObjectName();
    }

    @Override // org.springframework.validation.Errors
    public void setNestedPath(String nestedPath) {
        this.source.setNestedPath(nestedPath);
    }

    @Override // org.springframework.validation.Errors
    public String getNestedPath() {
        return this.source.getNestedPath();
    }

    @Override // org.springframework.validation.Errors
    public void pushNestedPath(String subPath) {
        this.source.pushNestedPath(subPath);
    }

    @Override // org.springframework.validation.Errors
    public void popNestedPath() throws IllegalStateException {
        this.source.popNestedPath();
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode) {
        this.source.reject(errorCode);
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, String defaultMessage) {
        this.source.reject(errorCode, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.source.reject(errorCode, errorArgs, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode) {
        this.source.rejectValue(field, errorCode);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, String defaultMessage) {
        this.source.rejectValue(field, errorCode, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        this.source.rejectValue(field, errorCode, errorArgs, defaultMessage);
    }

    @Override // org.springframework.validation.Errors
    public void addAllErrors(Errors errors) {
        this.source.addAllErrors(errors);
    }

    @Override // org.springframework.validation.Errors
    public boolean hasErrors() {
        return this.source.hasErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getErrorCount() {
        return this.source.getErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getAllErrors() {
        return escapeObjectErrors(this.source.getAllErrors());
    }

    @Override // org.springframework.validation.Errors
    public boolean hasGlobalErrors() {
        return this.source.hasGlobalErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getGlobalErrorCount() {
        return this.source.getGlobalErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getGlobalErrors() {
        return escapeObjectErrors(this.source.getGlobalErrors());
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public ObjectError getGlobalError() {
        return escapeObjectError(this.source.getGlobalError());
    }

    @Override // org.springframework.validation.Errors
    public boolean hasFieldErrors() {
        return this.source.hasFieldErrors();
    }

    @Override // org.springframework.validation.Errors
    public int getFieldErrorCount() {
        return this.source.getFieldErrorCount();
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors() {
        return this.source.getFieldErrors();
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public FieldError getFieldError() {
        return this.source.getFieldError();
    }

    @Override // org.springframework.validation.Errors
    public boolean hasFieldErrors(String field) {
        return this.source.hasFieldErrors(field);
    }

    @Override // org.springframework.validation.Errors
    public int getFieldErrorCount(String field) {
        return this.source.getFieldErrorCount(field);
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors(String field) {
        return escapeObjectErrors(this.source.getFieldErrors(field));
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public FieldError getFieldError(String field) {
        return (FieldError) escapeObjectError(this.source.getFieldError(field));
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Object getFieldValue(String field) {
        Object value = this.source.getFieldValue(field);
        if (!(value instanceof String)) {
            return value;
        }
        String text = (String) value;
        return HtmlUtils.htmlEscape(text);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Class<?> getFieldType(String field) {
        return this.source.getFieldType(field);
    }

    @Nullable
    private <T extends ObjectError> T escapeObjectError(@Nullable T t) {
        if (t == null) {
            return null;
        }
        String defaultMessage = t.getDefaultMessage();
        if (defaultMessage != null) {
            defaultMessage = HtmlUtils.htmlEscape(defaultMessage);
        }
        if (t instanceof FieldError) {
            FieldError fieldError = (FieldError) t;
            Object rejectedValue = fieldError.getRejectedValue();
            if (rejectedValue instanceof String) {
                rejectedValue = HtmlUtils.htmlEscape((String) rejectedValue);
            }
            return new FieldError(fieldError.getObjectName(), fieldError.getField(), rejectedValue, fieldError.isBindingFailure(), fieldError.getCodes(), fieldError.getArguments(), defaultMessage);
        }
        return (T) new ObjectError(t.getObjectName(), t.getCodes(), t.getArguments(), defaultMessage);
    }

    private <T extends ObjectError> List<T> escapeObjectErrors(List<T> source) {
        ArrayList arrayList = new ArrayList(source.size());
        for (T objectError : source) {
            arrayList.add(escapeObjectError(objectError));
        }
        return arrayList;
    }
}
