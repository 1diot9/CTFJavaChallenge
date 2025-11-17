package org.springframework.validation.method;

import java.lang.reflect.Method;
import java.util.List;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/MethodValidationException.class */
public class MethodValidationException extends RuntimeException implements MethodValidationResult {
    private final MethodValidationResult validationResult;

    public MethodValidationException(MethodValidationResult validationResult) {
        super(validationResult.toString());
        Assert.notNull(validationResult, "MethodValidationResult is required");
        this.validationResult = validationResult;
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Object getTarget() {
        return this.validationResult.getTarget();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Method getMethod() {
        return this.validationResult.getMethod();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public boolean isForReturnValue() {
        return this.validationResult.isForReturnValue();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public List<ParameterValidationResult> getAllValidationResults() {
        return this.validationResult.getAllValidationResults();
    }
}
