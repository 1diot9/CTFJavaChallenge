package org.springframework.web.bind.annotation;

import org.springframework.aot.hint.ReflectionHints;
import org.springframework.core.MethodParameter;
import org.springframework.http.ProblemDetail;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/annotation/ExceptionHandlerReflectiveProcessor.class */
class ExceptionHandlerReflectiveProcessor extends ControllerMappingReflectiveProcessor {
    ExceptionHandlerReflectiveProcessor() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.bind.annotation.ControllerMappingReflectiveProcessor
    public void registerReturnTypeHints(ReflectionHints hints, MethodParameter returnTypeParameter) {
        Class<?> returnType = returnTypeParameter.getParameterType();
        if (ProblemDetail.class.isAssignableFrom(returnType)) {
            getBindingRegistrar().registerReflectionHints(hints, returnTypeParameter.getGenericParameterType());
        }
        super.registerReturnTypeHints(hints, returnTypeParameter);
    }
}
