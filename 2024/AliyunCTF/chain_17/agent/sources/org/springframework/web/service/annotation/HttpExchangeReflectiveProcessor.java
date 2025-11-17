package org.springframework.web.service.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/annotation/HttpExchangeReflectiveProcessor.class */
class HttpExchangeReflectiveProcessor implements ReflectiveProcessor {
    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    HttpExchangeReflectiveProcessor() {
    }

    @Override // org.springframework.aot.hint.annotation.ReflectiveProcessor
    public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
        if (element instanceof Method) {
            Method method = (Method) element;
            registerMethodHints(hints, method);
        }
    }

    protected void registerMethodHints(ReflectionHints hints, Method method) {
        hints.registerMethod(method, ExecutableMode.INVOKE);
        for (Parameter parameter : method.getParameters()) {
            registerParameterTypeHints(hints, MethodParameter.forParameter(parameter));
        }
        registerReturnTypeHints(hints, MethodParameter.forExecutable(method, -1));
    }

    protected void registerParameterTypeHints(ReflectionHints hints, MethodParameter methodParameter) {
        if (methodParameter.hasParameterAnnotation(RequestBody.class)) {
            this.bindingRegistrar.registerReflectionHints(hints, methodParameter.getGenericParameterType());
        }
    }

    protected void registerReturnTypeHints(ReflectionHints hints, MethodParameter returnTypeParameter) {
        if (!Void.TYPE.equals(returnTypeParameter.getParameterType())) {
            this.bindingRegistrar.registerReflectionHints(hints, returnTypeParameter.getGenericParameterType());
        }
    }
}
