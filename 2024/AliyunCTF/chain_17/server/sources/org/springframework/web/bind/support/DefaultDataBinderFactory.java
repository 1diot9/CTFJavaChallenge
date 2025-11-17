package org.springframework.web.bind.support;

import jakarta.validation.Validator;
import java.lang.annotation.Annotation;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/DefaultDataBinderFactory.class */
public class DefaultDataBinderFactory implements WebDataBinderFactory {

    @Nullable
    private final WebBindingInitializer initializer;
    private boolean methodValidationApplicable;

    public DefaultDataBinderFactory(@Nullable WebBindingInitializer initializer) {
        this.initializer = initializer;
    }

    public void setMethodValidationApplicable(boolean methodValidationApplicable) {
        this.methodValidationApplicable = methodValidationApplicable;
    }

    @Override // org.springframework.web.bind.support.WebDataBinderFactory
    public final WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName) throws Exception {
        return createBinderInternal(webRequest, target, objectName, null);
    }

    @Override // org.springframework.web.bind.support.WebDataBinderFactory
    public final WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable Object target, String objectName, ResolvableType type) throws Exception {
        return createBinderInternal(webRequest, target, objectName, type);
    }

    private WebDataBinder createBinderInternal(NativeWebRequest webRequest, @Nullable Object target, String objectName, @Nullable ResolvableType type) throws Exception {
        WebDataBinder dataBinder = createBinderInstance(target, objectName, webRequest);
        dataBinder.setNameResolver(new BindParamNameResolver());
        if (target == null && type != null) {
            dataBinder.setTargetType(type);
        }
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder);
        }
        initBinder(dataBinder, webRequest);
        if (this.methodValidationApplicable && type != null) {
            Object source = type.getSource();
            if (source instanceof MethodParameter) {
                MethodParameter parameter = (MethodParameter) source;
                MethodValidationInitializer.initBinder(dataBinder, parameter);
            }
        }
        return dataBinder;
    }

    protected WebDataBinder createBinderInstance(@Nullable Object target, String objectName, NativeWebRequest webRequest) throws Exception {
        return new WebRequestDataBinder(target, objectName);
    }

    protected void initBinder(WebDataBinder dataBinder, NativeWebRequest webRequest) throws Exception {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/bind/support/DefaultDataBinderFactory$MethodValidationInitializer.class */
    public static class MethodValidationInitializer {
        private MethodValidationInitializer() {
        }

        public static void initBinder(DataBinder binder, MethodParameter parameter) {
            for (Annotation annotation : parameter.getParameterAnnotations()) {
                if (annotation.annotationType().getName().equals("jakarta.validation.Valid")) {
                    binder.setExcludedValidators(v -> {
                        if (!(v instanceof Validator)) {
                            if (v instanceof SmartValidator) {
                                SmartValidator sv = (SmartValidator) v;
                                if (sv.unwrap(Validator.class) != null) {
                                }
                            }
                            return false;
                        }
                        return true;
                    });
                }
            }
        }
    }
}
