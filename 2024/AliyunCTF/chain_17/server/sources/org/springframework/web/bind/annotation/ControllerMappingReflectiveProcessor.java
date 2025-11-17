package org.springframework.web.bind.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/annotation/ControllerMappingReflectiveProcessor.class */
class ControllerMappingReflectiveProcessor implements ReflectiveProcessor {
    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    @Override // org.springframework.aot.hint.annotation.ReflectiveProcessor
    public void registerReflectionHints(ReflectionHints hints, AnnotatedElement element) {
        if (element instanceof Class) {
            Class<?> type = (Class) element;
            registerTypeHints(hints, type);
        } else if (element instanceof Method) {
            Method method = (Method) element;
            registerMethodHints(hints, method);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final BindingReflectionHintsRegistrar getBindingRegistrar() {
        return this.bindingRegistrar;
    }

    protected void registerTypeHints(ReflectionHints hints, Class<?> type) {
        hints.registerType(type, new MemberCategory[0]);
    }

    protected void registerMethodHints(ReflectionHints hints, Method method) {
        hints.registerMethod(method, ExecutableMode.INVOKE);
        for (Parameter parameter : method.getParameters()) {
            registerParameterTypeHints(hints, MethodParameter.forParameter(parameter));
        }
        registerReturnTypeHints(hints, MethodParameter.forExecutable(method, -1));
    }

    protected void registerParameterTypeHints(ReflectionHints hints, MethodParameter methodParameter) {
        if (methodParameter.hasParameterAnnotation(RequestBody.class) || methodParameter.hasParameterAnnotation(ModelAttribute.class) || methodParameter.hasParameterAnnotation(RequestPart.class)) {
            this.bindingRegistrar.registerReflectionHints(hints, methodParameter.getGenericParameterType());
        } else if (HttpEntity.class.isAssignableFrom(methodParameter.getParameterType())) {
            this.bindingRegistrar.registerReflectionHints(hints, getHttpEntityType(methodParameter));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerReturnTypeHints(ReflectionHints hints, MethodParameter returnTypeParameter) {
        if (AnnotatedElementUtils.hasAnnotation(returnTypeParameter.getContainingClass(), ResponseBody.class) || returnTypeParameter.hasMethodAnnotation(ResponseBody.class)) {
            this.bindingRegistrar.registerReflectionHints(hints, returnTypeParameter.getGenericParameterType());
        } else if (HttpEntity.class.isAssignableFrom(returnTypeParameter.getParameterType())) {
            this.bindingRegistrar.registerReflectionHints(hints, getHttpEntityType(returnTypeParameter));
        }
    }

    @Nullable
    private Type getHttpEntityType(MethodParameter parameter) {
        MethodParameter nestedParameter = parameter.nested();
        if (nestedParameter.getNestedParameterType() == nestedParameter.getParameterType()) {
            return null;
        }
        return nestedParameter.getNestedParameterType();
    }
}
