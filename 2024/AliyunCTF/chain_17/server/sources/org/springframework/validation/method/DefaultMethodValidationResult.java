package org.springframework.validation.method;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/DefaultMethodValidationResult.class */
final class DefaultMethodValidationResult implements MethodValidationResult {
    private final Object target;
    private final Method method;
    private final List<ParameterValidationResult> allValidationResults;
    private final boolean forReturnValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultMethodValidationResult(Object target, Method method, List<ParameterValidationResult> results) {
        Assert.notEmpty(results, "'results' is required and must not be empty");
        Assert.notNull(target, "'target' is required");
        Assert.notNull(method, "Method is required");
        this.target = target;
        this.method = method;
        this.allValidationResults = results;
        this.forReturnValue = results.get(0).getMethodParameter().getParameterIndex() == -1;
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Object getTarget() {
        return this.target;
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Method getMethod() {
        return this.method;
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public boolean isForReturnValue() {
        return this.forReturnValue;
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public List<ParameterValidationResult> getAllValidationResults() {
        return this.allValidationResults;
    }

    public String toString() {
        return getAllErrors().size() + " validation errors for " + (isForReturnValue() ? "return value" : JsonEncoder.ARGUMENT_ARRAY_ATTR_NAME) + " of " + this.method.toGenericString();
    }
}
