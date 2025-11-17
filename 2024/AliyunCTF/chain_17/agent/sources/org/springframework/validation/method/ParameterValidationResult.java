package org.springframework.validation.method;

import java.util.Collection;
import java.util.List;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/ParameterValidationResult.class */
public class ParameterValidationResult {
    private final MethodParameter methodParameter;

    @Nullable
    private final Object argument;
    private final List<MessageSourceResolvable> resolvableErrors;

    @Nullable
    private final Object container;

    @Nullable
    private final Integer containerIndex;

    @Nullable
    private final Object containerKey;

    public ParameterValidationResult(MethodParameter param, @Nullable Object arg, Collection<? extends MessageSourceResolvable> errors, @Nullable Object container, @Nullable Integer index, @Nullable Object key) {
        Assert.notNull(param, "MethodParameter is required");
        Assert.notEmpty(errors, "`resolvableErrors` must not be empty");
        this.methodParameter = param;
        this.argument = arg;
        this.resolvableErrors = List.copyOf(errors);
        this.container = container;
        this.containerIndex = index;
        this.containerKey = key;
    }

    @Deprecated(since = "6.1.3", forRemoval = true)
    public ParameterValidationResult(MethodParameter param, @Nullable Object arg, Collection<? extends MessageSourceResolvable> errors) {
        this(param, arg, errors, null, null, null);
    }

    public MethodParameter getMethodParameter() {
        return this.methodParameter;
    }

    @Nullable
    public Object getArgument() {
        return this.argument;
    }

    public List<MessageSourceResolvable> getResolvableErrors() {
        return this.resolvableErrors;
    }

    @Nullable
    public Object getContainer() {
        return this.container;
    }

    @Nullable
    public Integer getContainerIndex() {
        return this.containerIndex;
    }

    @Nullable
    public Object getContainerKey() {
        return this.containerKey;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!super.equals(other)) {
            return false;
        }
        ParameterValidationResult otherResult = (ParameterValidationResult) other;
        return getMethodParameter().equals(otherResult.getMethodParameter()) && ObjectUtils.nullSafeEquals(getArgument(), otherResult.getArgument()) && ObjectUtils.nullSafeEquals(getContainerIndex(), otherResult.getContainerIndex()) && ObjectUtils.nullSafeEquals(getContainerKey(), otherResult.getContainerKey());
    }

    public int hashCode() {
        int hashCode = super.hashCode();
        return (29 * ((29 * ((29 * ((29 * hashCode) + getMethodParameter().hashCode())) + ObjectUtils.nullSafeHashCode(getArgument()))) + ObjectUtils.nullSafeHashCode(getContainerIndex()))) + ObjectUtils.nullSafeHashCode(getContainerKey());
    }

    public String toString() {
        return getClass().getSimpleName() + " for " + this.methodParameter + ", argument value '" + ObjectUtils.nullSafeConciseToString(this.argument) + "'," + (this.containerIndex != null ? "containerIndex[" + this.containerIndex + "]," : "") + (this.containerKey != null ? "containerKey['" + this.containerKey + "']," : "") + " errors: " + getResolvableErrors();
    }
}
