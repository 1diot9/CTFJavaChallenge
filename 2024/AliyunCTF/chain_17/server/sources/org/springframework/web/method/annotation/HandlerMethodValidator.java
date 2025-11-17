package org.springframework.web.method.annotation;

import jakarta.validation.Validator;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.MethodValidationAdapter;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.MethodValidator;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebBindingInitializer;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/HandlerMethodValidator.class */
public final class HandlerMethodValidator implements MethodValidator {
    private static final MethodValidationAdapter.ObjectNameResolver objectNameResolver = new WebObjectNameResolver();
    private final MethodValidationAdapter validationAdapter;
    private final Predicate<MethodParameter> modelAttribitePredicate;
    private final Predicate<MethodParameter> requestParamPredicate;

    private HandlerMethodValidator(MethodValidationAdapter validationAdapter, Predicate<MethodParameter> modelAttribitePredicate, Predicate<MethodParameter> requestParamPredicate) {
        this.validationAdapter = validationAdapter;
        this.modelAttribitePredicate = modelAttribitePredicate;
        this.requestParamPredicate = requestParamPredicate;
    }

    @Override // org.springframework.validation.method.MethodValidator
    public Class<?>[] determineValidationGroups(Object target, Method method) {
        return this.validationAdapter.determineValidationGroups(target, method);
    }

    @Override // org.springframework.validation.method.MethodValidator
    public void applyArgumentValidation(Object target, Method method, @Nullable MethodParameter[] parameters, Object[] arguments, Class<?>[] groups) {
        MethodValidationResult result = validateArguments(target, method, parameters, arguments, groups);
        if (!result.hasErrors()) {
            return;
        }
        if (!result.getBeanResults().isEmpty()) {
            int bindingResultCount = 0;
            for (ParameterErrors errors : result.getBeanResults()) {
                int length = arguments.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        Object arg = arguments[i];
                        if (arg instanceof BindingResult) {
                            BindingResult bindingResult = (BindingResult) arg;
                            if (bindingResult.getObjectName().equals(errors.getObjectName())) {
                                bindingResult.addAllErrors(errors);
                                bindingResultCount++;
                                break;
                            }
                        }
                        i++;
                    }
                }
            }
            if (result.getAllValidationResults().size() == bindingResultCount) {
                return;
            }
        }
        throw new HandlerMethodValidationException(result, this.modelAttribitePredicate, this.requestParamPredicate);
    }

    @Override // org.springframework.validation.method.MethodValidator
    public MethodValidationResult validateArguments(Object target, Method method, @Nullable MethodParameter[] parameters, Object[] arguments, Class<?>[] groups) {
        return this.validationAdapter.validateArguments(target, method, parameters, arguments, groups);
    }

    @Override // org.springframework.validation.method.MethodValidator
    public void applyReturnValueValidation(Object target, Method method, @Nullable MethodParameter returnType, @Nullable Object returnValue, Class<?>[] groups) {
        MethodValidationResult result = validateReturnValue(target, method, returnType, returnValue, groups);
        if (result.hasErrors()) {
            throw new HandlerMethodValidationException(result);
        }
    }

    @Override // org.springframework.validation.method.MethodValidator
    public MethodValidationResult validateReturnValue(Object target, Method method, @Nullable MethodParameter returnType, @Nullable Object returnValue, Class<?>[] groups) {
        return this.validationAdapter.validateReturnValue(target, method, returnType, returnValue, groups);
    }

    @Nullable
    public static MethodValidator from(@Nullable WebBindingInitializer initializer, @Nullable ParameterNameDiscoverer paramNameDiscoverer, Predicate<MethodParameter> modelAttribitePredicate, Predicate<MethodParameter> requestParamPredicate) {
        ConfigurableWebBindingInitializer configurableInitializer;
        Validator validator;
        if ((initializer instanceof ConfigurableWebBindingInitializer) && (validator = getValidator((configurableInitializer = (ConfigurableWebBindingInitializer) initializer))) != null) {
            MethodValidationAdapter adapter = new MethodValidationAdapter(validator);
            adapter.setObjectNameResolver(objectNameResolver);
            if (paramNameDiscoverer != null) {
                adapter.setParameterNameDiscoverer(paramNameDiscoverer);
            }
            MessageCodesResolver codesResolver = configurableInitializer.getMessageCodesResolver();
            if (codesResolver != null) {
                adapter.setMessageCodesResolver(codesResolver);
            }
            return new HandlerMethodValidator(adapter, modelAttribitePredicate, requestParamPredicate);
        }
        return null;
    }

    @Nullable
    private static Validator getValidator(ConfigurableWebBindingInitializer initializer) {
        Validator validator = initializer.getValidator();
        if (validator instanceof Validator) {
            Validator validator2 = validator;
            return validator2;
        }
        org.springframework.validation.Validator validator3 = initializer.getValidator();
        if (validator3 instanceof SmartValidator) {
            SmartValidator smartValidator = (SmartValidator) validator3;
            return (Validator) smartValidator.unwrap(Validator.class);
        }
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/method/annotation/HandlerMethodValidator$WebObjectNameResolver.class */
    private static class WebObjectNameResolver implements MethodValidationAdapter.ObjectNameResolver {
        private WebObjectNameResolver() {
        }

        @Override // org.springframework.validation.beanvalidation.MethodValidationAdapter.ObjectNameResolver
        public String resolveName(MethodParameter param, @Nullable Object value) {
            if (param.hasParameterAnnotation(RequestBody.class) || param.hasParameterAnnotation(RequestPart.class)) {
                return Conventions.getVariableNameForParameter(param);
            }
            if (param.getParameterIndex() != -1) {
                return ModelFactory.getNameForParameter(param);
            }
            return ModelFactory.getNameForReturnValue(value, param);
        }
    }
}
