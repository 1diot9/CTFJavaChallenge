package org.springframework.validation;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/FieldError.class */
public class FieldError extends ObjectError {
    private final String field;

    @Nullable
    private final Object rejectedValue;
    private final boolean bindingFailure;

    public FieldError(String objectName, String field, String defaultMessage) {
        this(objectName, field, null, false, null, null, defaultMessage);
    }

    public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {
        super(objectName, codes, arguments, defaultMessage);
        Assert.notNull(field, "Field must not be null");
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.bindingFailure = bindingFailure;
    }

    public String getField() {
        return this.field;
    }

    @Nullable
    public Object getRejectedValue() {
        return this.rejectedValue;
    }

    public boolean isBindingFailure() {
        return this.bindingFailure;
    }

    @Override // org.springframework.validation.ObjectError, org.springframework.context.support.DefaultMessageSourceResolvable
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        FieldError otherError = (FieldError) other;
        return getField().equals(otherError.getField()) && ObjectUtils.nullSafeEquals(getRejectedValue(), otherError.getRejectedValue()) && isBindingFailure() == otherError.isBindingFailure();
    }

    @Override // org.springframework.validation.ObjectError, org.springframework.context.support.DefaultMessageSourceResolvable
    public int hashCode() {
        int hashCode = super.hashCode();
        return (29 * ((29 * ((29 * hashCode) + getField().hashCode())) + ObjectUtils.nullSafeHashCode(getRejectedValue()))) + (isBindingFailure() ? 1 : 0);
    }

    @Override // org.springframework.validation.ObjectError, org.springframework.context.support.DefaultMessageSourceResolvable
    public String toString() {
        return "Field error in object '" + getObjectName() + "' on field '" + this.field + "': rejected value [" + ObjectUtils.nullSafeToString(this.rejectedValue) + "]; " + resolvableToString();
    }
}
