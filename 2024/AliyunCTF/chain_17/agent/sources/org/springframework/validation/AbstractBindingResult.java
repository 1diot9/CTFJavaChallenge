package org.springframework.validation;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/AbstractBindingResult.class */
public abstract class AbstractBindingResult extends AbstractErrors implements BindingResult, Serializable {
    private final String objectName;
    private MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
    private final List<ObjectError> errors = new ArrayList();
    private final Map<String, Class<?>> fieldTypes = new HashMap();
    private final Map<String, Object> fieldValues = new HashMap();
    private final Set<String> suppressedFields = new HashSet();

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public abstract Object getTarget();

    @Nullable
    protected abstract Object getActualFieldValue(String field);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractBindingResult(String objectName) {
        this.objectName = objectName;
    }

    public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        Assert.notNull(messageCodesResolver, "MessageCodesResolver must not be null");
        this.messageCodesResolver = messageCodesResolver;
    }

    public MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }

    @Override // org.springframework.validation.Errors
    public String getObjectName() {
        return this.objectName;
    }

    @Override // org.springframework.validation.Errors
    public void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        addError(new ObjectError(getObjectName(), resolveMessageCodes(errorCode), errorArgs, defaultMessage));
    }

    @Override // org.springframework.validation.Errors
    public void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage) {
        if (!StringUtils.hasLength(getNestedPath()) && !StringUtils.hasLength(field)) {
            reject(errorCode, errorArgs, defaultMessage);
            return;
        }
        String fixedField = fixedField(field);
        Object newVal = getActualFieldValue(fixedField);
        FieldError fe = new FieldError(getObjectName(), fixedField, newVal, false, resolveMessageCodes(errorCode, field), errorArgs, defaultMessage);
        addError(fe);
    }

    @Override // org.springframework.validation.Errors
    public void addAllErrors(Errors errors) {
        if (!errors.getObjectName().equals(getObjectName())) {
            throw new IllegalArgumentException("Errors object needs to have same object name");
        }
        this.errors.addAll(errors.getAllErrors());
    }

    @Override // org.springframework.validation.Errors
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    @Override // org.springframework.validation.Errors
    public int getErrorCount() {
        return this.errors.size();
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getAllErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    @Override // org.springframework.validation.Errors
    public List<ObjectError> getGlobalErrors() {
        List<ObjectError> result = new ArrayList<>();
        for (ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) {
                result.add(objectError);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public ObjectError getGlobalError() {
        for (ObjectError objectError : this.errors) {
            if (!(objectError instanceof FieldError)) {
                return objectError;
            }
        }
        return null;
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors() {
        List<FieldError> result = new ArrayList<>();
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                result.add(fieldError);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public FieldError getFieldError() {
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                return fieldError;
            }
        }
        return null;
    }

    @Override // org.springframework.validation.AbstractErrors, org.springframework.validation.Errors
    public List<FieldError> getFieldErrors(String field) {
        List<FieldError> result = new ArrayList<>();
        String fixedField = fixedField(field);
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (isMatchingFieldError(fixedField, fieldError)) {
                    result.add(fieldError);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public FieldError getFieldError(String field) {
        String fixedField = fixedField(field);
        for (ObjectError objectError : this.errors) {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                if (isMatchingFieldError(fixedField, fieldError)) {
                    return fieldError;
                }
            }
        }
        return null;
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Object getFieldValue(String field) {
        FieldError fieldError = getFieldError(field);
        if (fieldError != null) {
            Object value = fieldError.getRejectedValue();
            return (fieldError.isBindingFailure() || getTarget() == null) ? value : formatFieldValue(field, value);
        }
        if (getTarget() != null) {
            return formatFieldValue(field, getActualFieldValue(fixedField(field)));
        }
        return this.fieldValues.get(field);
    }

    @Override // org.springframework.validation.Errors
    @Nullable
    public Class<?> getFieldType(@Nullable String field) {
        Object value;
        if (getTarget() != null && (value = getActualFieldValue(fixedField(field))) != null) {
            return value.getClass();
        }
        return this.fieldTypes.get(field);
    }

    @Override // org.springframework.validation.BindingResult
    public Map<String, Object> getModel() {
        Map<String, Object> model = new LinkedHashMap<>(2);
        model.put(getObjectName(), getTarget());
        model.put(MODEL_KEY_PREFIX + getObjectName(), this);
        return model;
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public Object getRawFieldValue(String field) {
        if (getTarget() != null) {
            return getActualFieldValue(fixedField(field));
        }
        return null;
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public PropertyEditor findEditor(@Nullable String field, @Nullable Class<?> valueType) {
        PropertyEditorRegistry editorRegistry = getPropertyEditorRegistry();
        if (editorRegistry != null) {
            Class<?> valueTypeToUse = valueType;
            if (valueTypeToUse == null) {
                valueTypeToUse = getFieldType(field);
            }
            return editorRegistry.findCustomEditor(valueTypeToUse, fixedField(field));
        }
        return null;
    }

    @Override // org.springframework.validation.BindingResult
    @Nullable
    public PropertyEditorRegistry getPropertyEditorRegistry() {
        return null;
    }

    @Override // org.springframework.validation.BindingResult
    public String[] resolveMessageCodes(String errorCode) {
        return getMessageCodesResolver().resolveMessageCodes(errorCode, getObjectName());
    }

    @Override // org.springframework.validation.BindingResult
    public String[] resolveMessageCodes(String errorCode, @Nullable String field) {
        return getMessageCodesResolver().resolveMessageCodes(errorCode, getObjectName(), fixedField(field), getFieldType(field));
    }

    @Override // org.springframework.validation.BindingResult
    public void addError(ObjectError error) {
        this.errors.add(error);
    }

    @Override // org.springframework.validation.BindingResult
    public void recordFieldValue(String field, Class<?> type, @Nullable Object value) {
        this.fieldTypes.put(field, type);
        this.fieldValues.put(field, value);
    }

    @Override // org.springframework.validation.BindingResult
    public void recordSuppressedField(String field) {
        this.suppressedFields.add(field);
    }

    @Override // org.springframework.validation.BindingResult
    public String[] getSuppressedFields() {
        return StringUtils.toStringArray(this.suppressedFields);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof BindingResult) {
                BindingResult that = (BindingResult) other;
                if (!getObjectName().equals(that.getObjectName()) || !ObjectUtils.nullSafeEquals(getTarget(), that.getTarget()) || !getAllErrors().equals(that.getAllErrors())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return getObjectName().hashCode();
    }

    @Nullable
    protected Object formatFieldValue(String field, @Nullable Object value) {
        return value;
    }
}
