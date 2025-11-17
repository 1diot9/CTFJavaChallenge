package org.springframework.validation.beanvalidation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationInterceptor.class */
public class MethodValidationInterceptor implements MethodInterceptor {
    private static final boolean REACTOR_PRESENT = ClassUtils.isPresent("reactor.core.publisher.Mono", MethodValidationInterceptor.class.getClassLoader());
    private final MethodValidationAdapter validationAdapter;
    private final boolean adaptViolations;

    public MethodValidationInterceptor() {
        this(new MethodValidationAdapter(), false);
    }

    public MethodValidationInterceptor(ValidatorFactory validatorFactory) {
        this(new MethodValidationAdapter(validatorFactory), false);
    }

    public MethodValidationInterceptor(Validator validator) {
        this(new MethodValidationAdapter(validator), false);
    }

    public MethodValidationInterceptor(Supplier<Validator> validator) {
        this(validator, false);
    }

    public MethodValidationInterceptor(Supplier<Validator> validator, boolean adaptViolations) {
        this(new MethodValidationAdapter(validator), adaptViolations);
    }

    private MethodValidationInterceptor(MethodValidationAdapter validationAdapter, boolean adaptViolations) {
        this.validationAdapter = validationAdapter;
        this.adaptViolations = adaptViolations;
    }

    @Override // org.aopalliance.intercept.MethodInterceptor
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (isFactoryBeanMetadataMethod(invocation.getMethod())) {
            return invocation.proceed();
        }
        Object target = getTarget(invocation);
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        Class<?>[] groups = determineValidationGroups(invocation);
        if (REACTOR_PRESENT) {
            arguments = ReactorValidationHelper.insertAsyncValidation(this.validationAdapter.getSpringValidatorAdapter(), this.adaptViolations, target, method, arguments);
        }
        if (this.adaptViolations) {
            this.validationAdapter.applyArgumentValidation(target, method, null, arguments, groups);
        } else {
            Set<ConstraintViolation<Object>> violations = this.validationAdapter.invokeValidatorForArguments(target, method, arguments, groups);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
        Object returnValue = invocation.proceed();
        if (this.adaptViolations) {
            this.validationAdapter.applyReturnValueValidation(target, method, null, arguments, groups);
        } else {
            Set<ConstraintViolation<Object>> violations2 = this.validationAdapter.invokeValidatorForReturnValue(target, method, returnValue, groups);
            if (!violations2.isEmpty()) {
                throw new ConstraintViolationException(violations2);
            }
        }
        return returnValue;
    }

    private static Object getTarget(MethodInvocation invocation) {
        Object target = invocation.getThis();
        if (target == null && (invocation instanceof ProxyMethodInvocation)) {
            ProxyMethodInvocation methodInvocation = (ProxyMethodInvocation) invocation;
            target = methodInvocation.getProxy();
        }
        Assert.state(target != null, "Target must not be null");
        return target;
    }

    private boolean isFactoryBeanMetadataMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        if (clazz.isInterface()) {
            return (clazz == FactoryBean.class || clazz == SmartFactoryBean.class) && !method.getName().equals("getObject");
        }
        Class<?> factoryBeanType = null;
        if (SmartFactoryBean.class.isAssignableFrom(clazz)) {
            factoryBeanType = SmartFactoryBean.class;
        } else if (FactoryBean.class.isAssignableFrom(clazz)) {
            factoryBeanType = FactoryBean.class;
        }
        return (factoryBeanType == null || method.getName().equals("getObject") || !ClassUtils.hasMethod(factoryBeanType, method)) ? false : true;
    }

    protected Class<?>[] determineValidationGroups(MethodInvocation invocation) {
        Object target = getTarget(invocation);
        return this.validationAdapter.determineValidationGroups(target, invocation.getMethod());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationInterceptor$ReactorValidationHelper.class */
    private static final class ReactorValidationHelper {
        private static final ReactiveAdapterRegistry reactiveAdapterRegistry = ReactiveAdapterRegistry.getSharedInstance();

        private ReactorValidationHelper() {
        }

        static Object[] insertAsyncValidation(Supplier<SpringValidatorAdapter> validatorAdapterSupplier, boolean adaptViolations, Object target, Method method, Object[] arguments) {
            Class<?>[] groups;
            Flux doOnNext;
            for (int i = 0; i < method.getParameterCount(); i++) {
                if (arguments[i] != null) {
                    Class<?> parameterType = method.getParameterTypes()[i];
                    ReactiveAdapter reactiveAdapter = reactiveAdapterRegistry.getAdapter(parameterType);
                    if (reactiveAdapter != null && !reactiveAdapter.isNoValue() && (groups = determineValidationGroups(method.getParameters()[i])) != null) {
                        SpringValidatorAdapter validatorAdapter = validatorAdapterSupplier.get();
                        MethodParameter param = new MethodParameter(method, i);
                        int i2 = i;
                        if (reactiveAdapter.isMultiValue()) {
                            doOnNext = Flux.from(reactiveAdapter.toPublisher(arguments[i])).doOnNext(value -> {
                                validate(validatorAdapter, adaptViolations, target, method, param, value, groups);
                            });
                        } else {
                            doOnNext = Mono.from(reactiveAdapter.toPublisher(arguments[i])).doOnNext(value2 -> {
                                validate(validatorAdapter, adaptViolations, target, method, param, value2, groups);
                            });
                        }
                        arguments[i2] = doOnNext;
                    }
                }
            }
            return arguments;
        }

        @Nullable
        private static Class<?>[] determineValidationGroups(Parameter parameter) {
            Validated validated = (Validated) AnnotationUtils.findAnnotation(parameter, Validated.class);
            if (validated != null) {
                return validated.value();
            }
            Valid valid = AnnotationUtils.findAnnotation(parameter, (Class<Valid>) Valid.class);
            if (valid != null) {
                return new Class[0];
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static <T> void validate(SpringValidatorAdapter validatorAdapter, boolean adaptViolations, Object target, Method method, MethodParameter parameter, Object argument, Class<?>[] groups) {
            if (adaptViolations) {
                Errors errors = new BeanPropertyBindingResult(argument, argument.getClass().getSimpleName());
                validatorAdapter.validate(argument, errors);
                if (errors.hasErrors()) {
                    ParameterErrors paramErrors = new ParameterErrors(parameter, argument, errors, null, null, null);
                    List<ParameterValidationResult> results = Collections.singletonList(paramErrors);
                    throw new MethodValidationException(MethodValidationResult.create(target, method, results));
                }
                return;
            }
            Set<ConstraintViolation<T>> violations = validatorAdapter.validate((SpringValidatorAdapter) argument, groups);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
    }
}
