package org.springframework.beans.factory.aot;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.aot.generate.GeneratedMethods;
import org.springframework.aot.generate.ValueCodeGenerator;
import org.springframework.aot.generate.ValueCodeGeneratorDelegates;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.javapoet.CodeBlock;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertiesCodeGenerator.class */
class BeanDefinitionPropertiesCodeGenerator {
    private static final RootBeanDefinition DEFAULT_BEAN_DEFINITION = new RootBeanDefinition();
    private static final String BEAN_DEFINITION_VARIABLE = "beanDefinition";
    private final RuntimeHints hints;
    private final Predicate<String> attributeFilter;
    private final ValueCodeGenerator valueCodeGenerator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionPropertiesCodeGenerator(RuntimeHints hints, Predicate<String> attributeFilter, GeneratedMethods generatedMethods, List<ValueCodeGenerator.Delegate> additionalDelegates, BiFunction<String, Object, CodeBlock> customValueCodeGenerator) {
        this.hints = hints;
        this.attributeFilter = attributeFilter;
        List<ValueCodeGenerator.Delegate> allDelegates = new ArrayList<>();
        allDelegates.add((valueCodeGenerator, value) -> {
            return (CodeBlock) customValueCodeGenerator.apply(PropertyNamesStack.peek(), value);
        });
        allDelegates.addAll(additionalDelegates);
        allDelegates.addAll(BeanDefinitionPropertyValueCodeGeneratorDelegates.INSTANCES);
        allDelegates.addAll(ValueCodeGeneratorDelegates.INSTANCES);
        this.valueCodeGenerator = ValueCodeGenerator.with(allDelegates).scoped(generatedMethods);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CodeBlock generateCode(RootBeanDefinition beanDefinition) {
        CodeBlock.Builder code = CodeBlock.builder();
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.isPrimary();
        }, "$L.setPrimary($L)");
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.getScope();
        }, this::hasScope, "$L.setScope($S)");
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.getDependsOn();
        }, this::hasDependsOn, "$L.setDependsOn($L)", this::toStringVarArgs);
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.isAutowireCandidate();
        }, "$L.setAutowireCandidate($L)");
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.getRole();
        }, (v1, v2) -> {
            return hasRole(v1, v2);
        }, "$L.setRole($L)", (v1) -> {
            return toRole(v1);
        });
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.getLazyInit();
        }, "$L.setLazyInit($L)");
        addStatementForValue(code, beanDefinition, (v0) -> {
            return v0.isSynthetic();
        }, "$L.setSynthetic($L)");
        addInitDestroyMethods(code, beanDefinition, beanDefinition.getInitMethodNames(), "$L.setInitMethodNames($L)");
        addInitDestroyMethods(code, beanDefinition, beanDefinition.getDestroyMethodNames(), "$L.setDestroyMethodNames($L)");
        addConstructorArgumentValues(code, beanDefinition);
        addPropertyValues(code, beanDefinition);
        addAttributes(code, beanDefinition);
        addQualifiers(code, beanDefinition);
        return code.build();
    }

    private void addInitDestroyMethods(CodeBlock.Builder code, AbstractBeanDefinition beanDefinition, @Nullable String[] methodNames, String format) {
        this.hints.reflection().registerType(TypeReference.of("org.reactivestreams.Publisher"), new MemberCategory[0]);
        if (!ObjectUtils.isEmpty((Object[]) methodNames)) {
            Class<?> beanType = ClassUtils.getUserClass(beanDefinition.getResolvableType().toClass());
            Arrays.stream(methodNames).forEach(methodName -> {
                addInitDestroyHint(beanType, methodName);
            });
            CodeBlock arguments = (CodeBlock) Arrays.stream(methodNames).map(name -> {
                return CodeBlock.of("$S", name);
            }).collect(CodeBlock.joining(", "));
            code.addStatement(format, "beanDefinition", arguments);
        }
    }

    private void addInitDestroyHint(Class<?> beanUserClass, String methodName) {
        Class<?> methodDeclaringClass = beanUserClass;
        int indexOfDot = methodName.lastIndexOf(46);
        if (indexOfDot > 0) {
            String className = methodName.substring(0, indexOfDot);
            methodName = methodName.substring(indexOfDot + 1);
            if (!beanUserClass.getName().equals(className)) {
                try {
                    methodDeclaringClass = ClassUtils.forName(className, beanUserClass.getClassLoader());
                } catch (Throwable ex) {
                    throw new IllegalStateException("Failed to load Class [" + className + "] from ClassLoader [" + beanUserClass.getClassLoader() + "]", ex);
                }
            }
        }
        Method method = ReflectionUtils.findMethod(methodDeclaringClass, methodName);
        if (method != null) {
            this.hints.reflection().registerMethod(method, ExecutableMode.INVOKE);
            Method interfaceMethod = ClassUtils.getInterfaceMethodIfPossible(method, beanUserClass);
            if (!interfaceMethod.equals(method)) {
                this.hints.reflection().registerMethod(interfaceMethod, ExecutableMode.INVOKE);
            }
        }
    }

    private void addConstructorArgumentValues(CodeBlock.Builder code, BeanDefinition beanDefinition) {
        ConstructorArgumentValues constructorValues = beanDefinition.getConstructorArgumentValues();
        Map<Integer, ConstructorArgumentValues.ValueHolder> indexedValues = constructorValues.getIndexedArgumentValues();
        if (!indexedValues.isEmpty()) {
            indexedValues.forEach((index, valueHolder) -> {
                Object value = valueHolder.getValue();
                CodeBlock valueCode = castIfNecessary(value == null, Object.class, generateValue(valueHolder.getName(), value));
                code.addStatement("$L.getConstructorArgumentValues().addIndexedArgumentValue($L, $L)", "beanDefinition", index, valueCode);
            });
        }
        List<ConstructorArgumentValues.ValueHolder> genericValues = constructorValues.getGenericArgumentValues();
        if (!genericValues.isEmpty()) {
            genericValues.forEach(valueHolder2 -> {
                String valueName = valueHolder2.getName();
                CodeBlock valueCode = generateValue(valueName, valueHolder2.getValue());
                if (valueName != null) {
                    CodeBlock valueTypeCode = this.valueCodeGenerator.generateCode(valueHolder2.getType());
                    code.addStatement("$L.getConstructorArgumentValues().addGenericArgumentValue(new $T($L, $L, $S))", "beanDefinition", ConstructorArgumentValues.ValueHolder.class, valueCode, valueTypeCode, valueName);
                } else if (valueHolder2.getType() != null) {
                    code.addStatement("$L.getConstructorArgumentValues().addGenericArgumentValue($L, $S)", "beanDefinition", valueCode, valueHolder2.getType());
                } else {
                    code.addStatement("$L.getConstructorArgumentValues().addGenericArgumentValue($L)", "beanDefinition", valueCode);
                }
            });
        }
    }

    private void addPropertyValues(CodeBlock.Builder code, RootBeanDefinition beanDefinition) {
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            Class<?> infrastructureType = getInfrastructureType(beanDefinition);
            Map<String, Method> writeMethods = infrastructureType != Object.class ? getWriteMethods(infrastructureType) : Collections.emptyMap();
            Iterator<PropertyValue> it = propertyValues.iterator();
            while (it.hasNext()) {
                PropertyValue propertyValue = it.next();
                String name = propertyValue.getName();
                CodeBlock valueCode = generateValue(name, propertyValue.getValue());
                code.addStatement("$L.getPropertyValues().addPropertyValue($S, $L)", "beanDefinition", name, valueCode);
                Method writeMethod = writeMethods.get(name);
                if (writeMethod != null) {
                    registerReflectionHints(beanDefinition, writeMethod);
                }
            }
        }
    }

    private void registerReflectionHints(RootBeanDefinition beanDefinition, Method writeMethod) {
        this.hints.reflection().registerMethod(writeMethod, ExecutableMode.INVOKE);
        Class<?> targetType = beanDefinition.getTargetType();
        while (true) {
            Class<?> searchType = targetType;
            if (searchType == null || searchType == writeMethod.getDeclaringClass()) {
                break;
            }
            this.hints.reflection().registerType(searchType, MemberCategory.DECLARED_FIELDS);
            targetType = searchType.getSuperclass();
        }
        this.hints.reflection().registerType(writeMethod.getDeclaringClass(), MemberCategory.DECLARED_FIELDS);
    }

    private void addQualifiers(CodeBlock.Builder code, RootBeanDefinition beanDefinition) {
        Set<AutowireCandidateQualifier> qualifiers = beanDefinition.getQualifiers();
        if (!qualifiers.isEmpty()) {
            for (AutowireCandidateQualifier qualifier : qualifiers) {
                Collection<CodeBlock> arguments = new ArrayList<>();
                arguments.add(CodeBlock.of("$S", qualifier.getTypeName()));
                Object qualifierValue = qualifier.getAttribute("value");
                if (qualifierValue != null) {
                    arguments.add(generateValue("value", qualifierValue));
                }
                code.addStatement("$L.addQualifier(new $T($L))", "beanDefinition", AutowireCandidateQualifier.class, CodeBlock.join(arguments, ", "));
            }
        }
    }

    private CodeBlock generateValue(@Nullable String name, @Nullable Object value) {
        try {
            PropertyNamesStack.push(name);
            CodeBlock generateCode = this.valueCodeGenerator.generateCode(value);
            PropertyNamesStack.pop();
            return generateCode;
        } catch (Throwable th) {
            PropertyNamesStack.pop();
            throw th;
        }
    }

    private Class<?> getInfrastructureType(RootBeanDefinition beanDefinition) {
        if (beanDefinition.hasBeanClass()) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (FactoryBean.class.isAssignableFrom(beanClass)) {
                return beanClass;
            }
        }
        return ClassUtils.getUserClass(beanDefinition.getResolvableType().toClass());
    }

    private Map<String, Method> getWriteMethods(Class<?> clazz) {
        Map<String, Method> writeMethods = new HashMap<>();
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(clazz)) {
            writeMethods.put(propertyDescriptor.getName(), propertyDescriptor.getWriteMethod());
        }
        return Collections.unmodifiableMap(writeMethods);
    }

    private void addAttributes(CodeBlock.Builder code, BeanDefinition beanDefinition) {
        String[] attributeNames = beanDefinition.attributeNames();
        if (!ObjectUtils.isEmpty((Object[]) attributeNames)) {
            for (String attributeName : attributeNames) {
                if (this.attributeFilter.test(attributeName)) {
                    CodeBlock value = this.valueCodeGenerator.generateCode(beanDefinition.getAttribute(attributeName));
                    code.addStatement("$L.setAttribute($S, $L)", "beanDefinition", attributeName, value);
                }
            }
        }
    }

    private boolean hasScope(String defaultValue, String actualValue) {
        return StringUtils.hasText(actualValue) && !"singleton".equals(actualValue);
    }

    private boolean hasDependsOn(String[] defaultValue, String[] actualValue) {
        return !ObjectUtils.isEmpty((Object[]) actualValue);
    }

    private boolean hasRole(int defaultValue, int actualValue) {
        return actualValue != 0;
    }

    private CodeBlock toStringVarArgs(String[] strings) {
        return (CodeBlock) Arrays.stream(strings).map(string -> {
            return CodeBlock.of("$S", string);
        }).collect(CodeBlock.joining(","));
    }

    private Object toRole(int value) {
        switch (value) {
            case 1:
                return CodeBlock.builder().add("$T.ROLE_SUPPORT", BeanDefinition.class).build();
            case 2:
                return CodeBlock.builder().add("$T.ROLE_INFRASTRUCTURE", BeanDefinition.class).build();
            default:
                return Integer.valueOf(value);
        }
    }

    private <B extends BeanDefinition, T> void addStatementForValue(CodeBlock.Builder code, BeanDefinition beanDefinition, Function<B, T> getter, String format) {
        addStatementForValue(code, beanDefinition, getter, (defaultValue, actualValue) -> {
            return !Objects.equals(defaultValue, actualValue);
        }, format);
    }

    private <B extends BeanDefinition, T> void addStatementForValue(CodeBlock.Builder code, BeanDefinition beanDefinition, Function<B, T> getter, BiPredicate<T, T> filter, String format) {
        addStatementForValue(code, beanDefinition, getter, filter, format, actualValue -> {
            return actualValue;
        });
    }

    private <B extends BeanDefinition, T> void addStatementForValue(CodeBlock.Builder code, BeanDefinition beanDefinition, Function<B, T> getter, BiPredicate<T, T> filter, String format, Function<T, Object> formatter) {
        T defaultValue = getter.apply(DEFAULT_BEAN_DEFINITION);
        T actualValue = getter.apply(beanDefinition);
        if (filter.test(defaultValue, actualValue)) {
            code.addStatement(format, "beanDefinition", formatter.apply(actualValue));
        }
    }

    private CodeBlock castIfNecessary(boolean castNecessary, Class<?> castType, CodeBlock valueCode) {
        return castNecessary ? CodeBlock.of("($T) $L", castType, valueCode) : valueCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionPropertiesCodeGenerator$PropertyNamesStack.class */
    public static class PropertyNamesStack {
        private static final ThreadLocal<ArrayDeque<String>> threadLocal = ThreadLocal.withInitial(ArrayDeque::new);

        PropertyNamesStack() {
        }

        static void push(@Nullable String name) {
            String valueToSet = name != null ? name : "";
            threadLocal.get().push(valueToSet);
        }

        static void pop() {
            threadLocal.get().pop();
        }

        @Nullable
        static String peek() {
            String value = threadLocal.get().peek();
            if ("".equals(value)) {
                return null;
            }
            return value;
        }
    }
}
