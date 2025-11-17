package org.springframework.validation.beanvalidation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Conventions;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.MethodValidator;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationAdapter.class */
public class MethodValidationAdapter implements MethodValidator {
    private static final MethodValidationResult emptyValidationResult = MethodValidationResult.emptyResult();
    private static final ObjectNameResolver defaultObjectNameResolver = new DefaultObjectNameResolver();
    private static final Comparator<ParameterValidationResult> resultComparator = new ResultComparator();
    private final Supplier<Validator> validator;
    private final Supplier<SpringValidatorAdapter> validatorAdapter;
    private MessageCodesResolver messageCodesResolver;
    private ParameterNameDiscoverer parameterNameDiscoverer;
    private ObjectNameResolver objectNameResolver;

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationAdapter$ObjectNameResolver.class */
    public interface ObjectNameResolver {
        String resolveName(MethodParameter parameter, @Nullable Object value);
    }

    public MethodValidationAdapter() {
        this.messageCodesResolver = new DefaultMessageCodesResolver();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.objectNameResolver = defaultObjectNameResolver;
        this.validator = SingletonSupplier.of(() -> {
            return Validation.buildDefaultValidatorFactory().getValidator();
        });
        this.validatorAdapter = initValidatorAdapter(this.validator);
    }

    public MethodValidationAdapter(ValidatorFactory validatorFactory) {
        this.messageCodesResolver = new DefaultMessageCodesResolver();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.objectNameResolver = defaultObjectNameResolver;
        if (validatorFactory instanceof SpringValidatorAdapter) {
            SpringValidatorAdapter adapter = (SpringValidatorAdapter) validatorFactory;
            this.validator = () -> {
                return adapter;
            };
            this.validatorAdapter = () -> {
                return adapter;
            };
        } else {
            Objects.requireNonNull(validatorFactory);
            this.validator = SingletonSupplier.of(validatorFactory::getValidator);
            this.validatorAdapter = SingletonSupplier.of(() -> {
                return new SpringValidatorAdapter(this.validator.get());
            });
        }
    }

    public MethodValidationAdapter(Validator validator) {
        this.messageCodesResolver = new DefaultMessageCodesResolver();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.objectNameResolver = defaultObjectNameResolver;
        this.validator = () -> {
            return validator;
        };
        this.validatorAdapter = initValidatorAdapter(this.validator);
    }

    public MethodValidationAdapter(Supplier<Validator> validator) {
        this.messageCodesResolver = new DefaultMessageCodesResolver();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        this.objectNameResolver = defaultObjectNameResolver;
        this.validator = validator;
        this.validatorAdapter = initValidatorAdapter(validator);
    }

    private static Supplier<SpringValidatorAdapter> initValidatorAdapter(Supplier<Validator> validatorSupplier) {
        return SingletonSupplier.of(() -> {
            Validator validator = (Validator) validatorSupplier.get();
            if (!(validator instanceof SpringValidatorAdapter)) {
                return new SpringValidatorAdapter(validator);
            }
            SpringValidatorAdapter sva = (SpringValidatorAdapter) validator;
            return sva;
        });
    }

    public Supplier<SpringValidatorAdapter> getSpringValidatorAdapter() {
        return this.validatorAdapter;
    }

    public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
        this.messageCodesResolver = messageCodesResolver;
    }

    public MessageCodesResolver getMessageCodesResolver() {
        return this.messageCodesResolver;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    public void setObjectNameResolver(ObjectNameResolver nameResolver) {
        this.objectNameResolver = nameResolver;
    }

    @Override // org.springframework.validation.method.MethodValidator
    public Class<?>[] determineValidationGroups(Object target, Method method) {
        Validated validatedAnn = (Validated) AnnotationUtils.findAnnotation(method, Validated.class);
        if (validatedAnn == null) {
            if (AopUtils.isAopProxy(target)) {
                for (Class<?> type : AopProxyUtils.proxiedUserInterfaces(target)) {
                    validatedAnn = (Validated) AnnotationUtils.findAnnotation(type, Validated.class);
                    if (validatedAnn != null) {
                        break;
                    }
                }
            } else {
                validatedAnn = (Validated) AnnotationUtils.findAnnotation(target.getClass(), Validated.class);
            }
        }
        return validatedAnn != null ? validatedAnn.value() : new Class[0];
    }

    @Override // org.springframework.validation.method.MethodValidator
    public final MethodValidationResult validateArguments(Object target, Method method, @Nullable MethodParameter[] parameters, Object[] arguments, Class<?>[] groups) {
        Set<ConstraintViolation<Object>> violations = invokeValidatorForArguments(target, method, arguments, groups);
        if (violations.isEmpty()) {
            return emptyValidationResult;
        }
        return adaptViolations(target, method, violations, i -> {
            return parameters != null ? parameters[i.intValue()] : initMethodParameter(method, i.intValue());
        }, i2 -> {
            return arguments[i2.intValue()];
        });
    }

    public final Set<ConstraintViolation<Object>> invokeValidatorForArguments(Object target, Method method, Object[] arguments, Class<?>[] groups) {
        Set<ConstraintViolation<Object>> violations;
        ExecutableValidator execVal = this.validator.get().forExecutables();
        try {
            violations = execVal.validateParameters(target, method, arguments, groups);
        } catch (IllegalArgumentException e) {
            Method bridgedMethod = BridgeMethodResolver.getMostSpecificMethod(method, target.getClass());
            violations = execVal.validateParameters(target, bridgedMethod, arguments, groups);
        }
        return violations;
    }

    @Override // org.springframework.validation.method.MethodValidator
    public final MethodValidationResult validateReturnValue(Object target, Method method, @Nullable MethodParameter returnType, @Nullable Object returnValue, Class<?>[] groups) {
        Set<ConstraintViolation<Object>> violations = invokeValidatorForReturnValue(target, method, returnValue, groups);
        if (violations.isEmpty()) {
            return emptyValidationResult;
        }
        return adaptViolations(target, method, violations, i -> {
            return returnType != null ? returnType : initMethodParameter(method, -1);
        }, i2 -> {
            return returnValue;
        });
    }

    public final Set<ConstraintViolation<Object>> invokeValidatorForReturnValue(Object target, Method method, @Nullable Object returnValue, Class<?>[] groups) {
        ExecutableValidator execVal = this.validator.get().forExecutables();
        return execVal.validateReturnValue(target, method, returnValue, groups);
    }

    private MethodValidationResult adaptViolations(Object target, Method method, Set<ConstraintViolation<Object>> violations, Function<Integer, MethodParameter> parameterFunction, Function<Integer, Object> argumentFunction) {
        MethodParameter parameter;
        Object value;
        Object container;
        Map<Path.Node, ParamValidationResultBuilder> paramViolations = new LinkedHashMap<>();
        Map<Path.Node, ParamErrorsBuilder> nestedViolations = new LinkedHashMap<>();
        for (ConstraintViolation<Object> violation : violations) {
            Iterator<Path.Node> itr = violation.getPropertyPath().iterator();
            while (itr.hasNext()) {
                Path.Node node = itr.next();
                if (node.getKind().equals(ElementKind.PARAMETER)) {
                    parameter = parameterFunction.apply(Integer.valueOf(node.as(Path.ParameterNode.class).getParameterIndex()));
                } else if (node.getKind().equals(ElementKind.RETURN_VALUE)) {
                    parameter = parameterFunction.apply(-1);
                }
                Object arg = argumentFunction.apply(Integer.valueOf(parameter.getParameterIndex()));
                if (itr.hasNext()) {
                    node = itr.next();
                }
                Integer index = node.getIndex();
                Object key = node.getKey();
                if (index != null && (arg instanceof List)) {
                    List<?> list = (List) arg;
                    value = list.get(index.intValue());
                    container = list;
                } else if (index != null && (arg instanceof Object[])) {
                    Object[] array = (Object[]) arg;
                    value = array[index.intValue()];
                    container = array;
                } else if (key != null && (arg instanceof Map)) {
                    Map<?, ?> map = (Map) arg;
                    value = map.get(key);
                    container = map;
                } else if (arg instanceof Optional) {
                    Optional<?> optional = (Optional) arg;
                    value = optional.orElse(null);
                    container = optional;
                } else {
                    Assert.state(!node.isInIterable(), "No way to unwrap Iterable without index");
                    value = arg;
                    container = null;
                }
                if (node.getKind().equals(ElementKind.PROPERTY)) {
                    MethodParameter methodParameter = parameter;
                    Object obj = value;
                    Object obj2 = container;
                    nestedViolations.computeIfAbsent(node, k -> {
                        return new ParamErrorsBuilder(methodParameter, obj, obj2, index, key);
                    }).addViolation(violation);
                } else {
                    MethodParameter methodParameter2 = parameter;
                    Object obj3 = value;
                    Object obj4 = container;
                    paramViolations.computeIfAbsent(node, p -> {
                        return new ParamValidationResultBuilder(target, methodParameter2, obj3, obj4, index, key);
                    }).addViolation(violation);
                }
            }
        }
        List<ParameterValidationResult> resultList = new ArrayList<>();
        paramViolations.forEach((param, builder) -> {
            resultList.add(builder.build());
        });
        nestedViolations.forEach((key2, builder2) -> {
            resultList.add(builder2.build());
        });
        resultList.sort(resultComparator);
        return MethodValidationResult.create(target, method, resultList);
    }

    private MethodParameter initMethodParameter(Method method, int index) {
        MethodParameter parameter = new MethodParameter(method, index);
        parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
        return parameter;
    }

    private MessageSourceResolvable createMessageSourceResolvable(Object target, MethodParameter parameter, ConstraintViolation<Object> violation) {
        String objectName = Conventions.getVariableName(target) + "#" + parameter.getExecutable().getName();
        String paramName = parameter.getParameterName() != null ? parameter.getParameterName() : "";
        Class<?> parameterType = parameter.getParameterType();
        ConstraintDescriptor<?> descriptor = violation.getConstraintDescriptor();
        String code = descriptor.getAnnotation().annotationType().getSimpleName();
        String[] codes = this.messageCodesResolver.resolveMessageCodes(code, objectName, paramName, parameterType);
        Object[] arguments = this.validatorAdapter.get().getArgumentsForConstraint(objectName, paramName, descriptor);
        return new DefaultMessageSourceResolvable(codes, arguments, violation.getMessage());
    }

    private BindingResult createBindingResult(MethodParameter parameter, @Nullable Object argument) {
        String objectName = this.objectNameResolver.resolveName(parameter, argument);
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(argument, objectName);
        result.setMessageCodesResolver(this.messageCodesResolver);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationAdapter$ParamValidationResultBuilder.class */
    public final class ParamValidationResultBuilder {
        private final Object target;
        private final MethodParameter parameter;

        @Nullable
        private final Object value;

        @Nullable
        private final Object container;

        @Nullable
        private final Integer containerIndex;

        @Nullable
        private final Object containerKey;
        private final List<MessageSourceResolvable> resolvableErrors = new ArrayList();

        public ParamValidationResultBuilder(Object target, MethodParameter parameter, @Nullable Object value, @Nullable Object container, @Nullable Integer containerIndex, @Nullable Object containerKey) {
            this.target = target;
            this.parameter = parameter;
            this.value = value;
            this.container = container;
            this.containerIndex = containerIndex;
            this.containerKey = containerKey;
        }

        public void addViolation(ConstraintViolation<Object> violation) {
            this.resolvableErrors.add(MethodValidationAdapter.this.createMessageSourceResolvable(this.target, this.parameter, violation));
        }

        public ParameterValidationResult build() {
            return new ParameterValidationResult(this.parameter, this.value, this.resolvableErrors, this.container, this.containerIndex, this.containerKey);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationAdapter$ParamErrorsBuilder.class */
    public final class ParamErrorsBuilder {
        private final MethodParameter parameter;

        @Nullable
        private final Object bean;

        @Nullable
        private final Object container;

        @Nullable
        private final Integer containerIndex;

        @Nullable
        private final Object containerKey;
        private final Errors errors;
        private final Set<ConstraintViolation<Object>> violations = new LinkedHashSet();

        public ParamErrorsBuilder(MethodParameter param, @Nullable Object bean, @Nullable Object container, @Nullable Integer containerIndex, @Nullable Object containerKey) {
            this.parameter = param;
            this.bean = bean;
            this.container = container;
            this.containerIndex = containerIndex;
            this.containerKey = containerKey;
            this.errors = MethodValidationAdapter.this.createBindingResult(param, this.bean);
        }

        public void addViolation(ConstraintViolation<Object> violation) {
            this.violations.add(violation);
        }

        public ParameterErrors build() {
            MethodValidationAdapter.this.validatorAdapter.get().processConstraintViolations(this.violations, this.errors);
            return new ParameterErrors(this.parameter, this.bean, this.errors, this.container, this.containerIndex, this.containerKey);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationAdapter$DefaultObjectNameResolver.class */
    private static class DefaultObjectNameResolver implements ObjectNameResolver {
        private DefaultObjectNameResolver() {
        }

        @Override // org.springframework.validation.beanvalidation.MethodValidationAdapter.ObjectNameResolver
        public String resolveName(MethodParameter parameter, @Nullable Object value) {
            String objectName = null;
            if (parameter.getParameterIndex() != -1) {
                objectName = parameter.getParameterName();
            } else {
                try {
                    Method method = parameter.getMethod();
                    if (method != null) {
                        Class<?> containingClass = parameter.getContainingClass();
                        Class<?> resolvedType = GenericTypeResolver.resolveReturnType(method, containingClass);
                        objectName = Conventions.getVariableNameForReturnType(method, resolvedType, value);
                    }
                } catch (IllegalArgumentException e) {
                }
            }
            if (objectName == null) {
                int index = parameter.getParameterIndex();
                objectName = parameter.getExecutable().getName() + (index != -1 ? ".arg" + index : ".returnValue");
            }
            return objectName;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/beanvalidation/MethodValidationAdapter$ResultComparator.class */
    private static final class ResultComparator implements Comparator<ParameterValidationResult> {
        private ResultComparator() {
        }

        @Override // java.util.Comparator
        public int compare(ParameterValidationResult result1, ParameterValidationResult result2) {
            int i;
            int index1 = result1.getMethodParameter().getParameterIndex();
            int index2 = result2.getMethodParameter().getParameterIndex();
            int i2 = Integer.compare(index1, index2);
            if (i2 != 0) {
                return i2;
            }
            if (!(result1 instanceof ParameterErrors)) {
                return 0;
            }
            ParameterErrors errors1 = (ParameterErrors) result1;
            if (result2 instanceof ParameterErrors) {
                ParameterErrors errors2 = (ParameterErrors) result2;
                Integer containerIndex1 = errors1.getContainerIndex();
                Integer containerIndex2 = errors2.getContainerIndex();
                if (containerIndex1 != null && containerIndex2 != null && (i = Integer.compare(containerIndex1.intValue(), containerIndex2.intValue())) != 0) {
                    return i;
                }
                return compareKeys(errors1, errors2);
            }
            return 0;
        }

        private <E> int compareKeys(ParameterErrors errors1, ParameterErrors errors2) {
            Object key1 = errors1.getContainerKey();
            Object key2 = errors2.getContainerKey();
            if ((key1 instanceof Comparable) && (key2 instanceof Comparable)) {
                return ((Comparable) key1).compareTo(key2);
            }
            return 0;
        }
    }
}
