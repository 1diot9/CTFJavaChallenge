package org.springframework.validation;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/AbstractErrors.class */
public abstract class AbstractErrors implements Errors, Serializable {
    private String nestedPath = "";
    private final Deque<String> nestedPathStack = new ArrayDeque();

    @Override // org.springframework.validation.Errors
    public void setNestedPath(@Nullable String nestedPath) {
        doSetNestedPath(nestedPath);
        this.nestedPathStack.clear();
    }

    @Override // org.springframework.validation.Errors
    public String getNestedPath() {
        return this.nestedPath;
    }

    @Override // org.springframework.validation.Errors
    public void pushNestedPath(String subPath) {
        this.nestedPathStack.push(getNestedPath());
        doSetNestedPath(getNestedPath() + subPath);
    }

    @Override // org.springframework.validation.Errors
    public void popNestedPath() throws IllegalStateException {
        try {
            String formerNestedPath = this.nestedPathStack.pop();
            doSetNestedPath(formerNestedPath);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Cannot pop nested path: no nested path on stack");
        }
    }

    protected void doSetNestedPath(@Nullable String nestedPath) {
        if (nestedPath == null) {
            nestedPath = "";
        }
        String nestedPath2 = canonicalFieldName(nestedPath);
        if (nestedPath2.length() > 0 && !nestedPath2.endsWith(".")) {
            nestedPath2 = nestedPath2 + ".";
        }
        this.nestedPath = nestedPath2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String fixedField(@Nullable String field) {
        if (StringUtils.hasLength(field)) {
            return getNestedPath() + canonicalFieldName(field);
        }
        String path = getNestedPath();
        return path.endsWith(".") ? path.substring(0, path.length() - ".".length()) : path;
    }

    protected String canonicalFieldName(String field) {
        return field;
    }

    @Override // org.springframework.validation.Errors
    public List<FieldError> getFieldErrors(String field) {
        List<FieldError> fieldErrors = getFieldErrors();
        List<FieldError> result = new ArrayList<>();
        String fixedField = fixedField(field);
        for (FieldError fieldError : fieldErrors) {
            if (isMatchingFieldError(fixedField, fieldError)) {
                result.add(fieldError);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isMatchingFieldError(String field, FieldError fieldError) {
        if (field.equals(fieldError.getField())) {
            return true;
        }
        int endIndex = field.length() - 1;
        return endIndex >= 0 && field.charAt(endIndex) == '*' && (endIndex == 0 || field.regionMatches(0, fieldError.getField(), 0, endIndex));
    }

    @Override // org.springframework.validation.Errors
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(": ").append(getErrorCount()).append(" errors");
        for (ObjectError error : getAllErrors()) {
            sb.append('\n').append(error);
        }
        return sb.toString();
    }
}
