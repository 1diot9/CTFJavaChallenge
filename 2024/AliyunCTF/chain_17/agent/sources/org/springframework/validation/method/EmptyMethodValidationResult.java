package org.springframework.validation.method;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/method/EmptyMethodValidationResult.class */
final class EmptyMethodValidationResult implements MethodValidationResult {
    @Override // org.springframework.validation.method.MethodValidationResult
    public Object getTarget() {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public Method getMethod() {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public boolean isForReturnValue() {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.validation.method.MethodValidationResult
    public List<ParameterValidationResult> getAllValidationResults() {
        return Collections.emptyList();
    }

    public String toString() {
        return "0 validation errors";
    }
}
