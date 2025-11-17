package org.springframework.beans.factory.aot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionValueResolver;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.ThrowingBiFunction;
import org.springframework.util.function.ThrowingFunction;
import org.springframework.util.function.ThrowingSupplier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanInstanceSupplier.class */
public final class BeanInstanceSupplier<T> extends AutowiredElementResolver implements InstanceSupplier<T> {
    private final ExecutableLookup lookup;

    @Nullable
    private final ThrowingBiFunction<RegisteredBean, AutowiredArguments, T> generator;

    @Nullable
    private final String[] shortcuts;

    private BeanInstanceSupplier(ExecutableLookup lookup, @Nullable ThrowingBiFunction<RegisteredBean, AutowiredArguments, T> generator, @Nullable String[] shortcuts) {
        this.lookup = lookup;
        this.generator = generator;
        this.shortcuts = shortcuts;
    }

    public static <T> BeanInstanceSupplier<T> forConstructor(Class<?>... parameterTypes) {
        Assert.notNull(parameterTypes, "'parameterTypes' must not be null");
        Assert.noNullElements(parameterTypes, "'parameterTypes' must not contain null elements");
        return new BeanInstanceSupplier<>(new ConstructorLookup(parameterTypes), null, null);
    }

    public static <T> BeanInstanceSupplier<T> forFactoryMethod(Class<?> declaringClass, String methodName, Class<?>... parameterTypes) {
        Assert.notNull(declaringClass, "'declaringClass' must not be null");
        Assert.hasText(methodName, "'methodName' must not be empty");
        Assert.notNull(parameterTypes, "'parameterTypes' must not be null");
        Assert.noNullElements(parameterTypes, "'parameterTypes' must not contain null elements");
        return new BeanInstanceSupplier<>(new FactoryMethodLookup(declaringClass, methodName, parameterTypes), null, null);
    }

    ExecutableLookup getLookup() {
        return this.lookup;
    }

    public BeanInstanceSupplier<T> withGenerator(ThrowingBiFunction<RegisteredBean, AutowiredArguments, T> generator) {
        Assert.notNull(generator, "'generator' must not be null");
        return new BeanInstanceSupplier<>(this.lookup, generator, this.shortcuts);
    }

    public BeanInstanceSupplier<T> withGenerator(ThrowingFunction<RegisteredBean, T> generator) {
        Assert.notNull(generator, "'generator' must not be null");
        return new BeanInstanceSupplier<>(this.lookup, (registeredBean, args) -> {
            return generator.apply(registeredBean);
        }, this.shortcuts);
    }

    @Deprecated(since = "6.0.11", forRemoval = true)
    public BeanInstanceSupplier<T> withGenerator(ThrowingSupplier<T> generator) {
        Assert.notNull(generator, "'generator' must not be null");
        return new BeanInstanceSupplier<>(this.lookup, (registeredBean, args) -> {
            return generator.get();
        }, this.shortcuts);
    }

    public BeanInstanceSupplier<T> withShortcuts(String... beanNames) {
        return new BeanInstanceSupplier<>(this.lookup, this.generator, beanNames);
    }

    @Override // org.springframework.beans.factory.support.InstanceSupplier
    public T get(RegisteredBean registeredBean) throws Exception {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        Executable executable = this.lookup.get(registeredBean);
        AutowiredArguments arguments = resolveArguments(registeredBean, executable);
        if (this.generator != null) {
            return invokeBeanSupplier(executable, () -> {
                return this.generator.apply(registeredBean, arguments);
            });
        }
        return invokeBeanSupplier(executable, () -> {
            return instantiate(registeredBean.getBeanFactory(), executable, arguments.toArray());
        });
    }

    private T invokeBeanSupplier(Executable executable, ThrowingSupplier<T> beanSupplier) {
        if (!(executable instanceof Method)) {
            return beanSupplier.get();
        }
        Method method = (Method) executable;
        try {
            SimpleInstantiationStrategy.setCurrentlyInvokedFactoryMethod(method);
            T t = beanSupplier.get();
            SimpleInstantiationStrategy.setCurrentlyInvokedFactoryMethod(null);
            return t;
        } catch (Throwable th) {
            SimpleInstantiationStrategy.setCurrentlyInvokedFactoryMethod(null);
            throw th;
        }
    }

    @Override // org.springframework.beans.factory.support.InstanceSupplier
    @Nullable
    public Method getFactoryMethod() {
        ExecutableLookup executableLookup = this.lookup;
        if (executableLookup instanceof FactoryMethodLookup) {
            FactoryMethodLookup factoryMethodLookup = (FactoryMethodLookup) executableLookup;
            return factoryMethodLookup.get();
        }
        return null;
    }

    AutowiredArguments resolveArguments(RegisteredBean registeredBean) {
        Assert.notNull(registeredBean, "'registeredBean' must not be null");
        return resolveArguments(registeredBean, this.lookup.get(registeredBean));
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0077  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.springframework.beans.factory.aot.AutowiredArguments resolveArguments(org.springframework.beans.factory.support.RegisteredBean r9, java.lang.reflect.Executable r10) {
        /*
            Method dump skipped, instructions count: 231
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.aot.BeanInstanceSupplier.resolveArguments(org.springframework.beans.factory.support.RegisteredBean, java.lang.reflect.Executable):org.springframework.beans.factory.aot.AutowiredArguments");
    }

    private MethodParameter getMethodParameter(Executable executable, int index) {
        if (executable instanceof Constructor) {
            Constructor<?> constructor = (Constructor) executable;
            return new MethodParameter(constructor, index);
        }
        if (executable instanceof Method) {
            Method method = (Method) executable;
            return new MethodParameter(method, index);
        }
        throw new IllegalStateException("Unsupported executable: " + executable.getClass().getName());
    }

    private ConstructorArgumentValues.ValueHolder[] resolveArgumentValues(RegisteredBean registeredBean, Executable executable) {
        Parameter[] parameters = executable.getParameters();
        ConstructorArgumentValues.ValueHolder[] resolved = new ConstructorArgumentValues.ValueHolder[parameters.length];
        RootBeanDefinition beanDefinition = registeredBean.getMergedBeanDefinition();
        if (beanDefinition.hasConstructorArgumentValues()) {
            HierarchicalBeanFactory beanFactory = registeredBean.getBeanFactory();
            if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
                AbstractAutowireCapableBeanFactory beanFactory2 = (AbstractAutowireCapableBeanFactory) beanFactory;
                BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(beanFactory2, registeredBean.getBeanName(), beanDefinition, beanFactory2.getTypeConverter());
                ConstructorArgumentValues values = resolveConstructorArguments(valueResolver, beanDefinition.getConstructorArgumentValues());
                Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(parameters.length);
                for (int i = 0; i < parameters.length; i++) {
                    Class<?> parameterType = parameters[i].getType();
                    String parameterName = parameters[i].isNamePresent() ? parameters[i].getName() : null;
                    ConstructorArgumentValues.ValueHolder valueHolder = values.getArgumentValue(i, parameterType, parameterName, usedValueHolders);
                    if (valueHolder != null) {
                        resolved[i] = valueHolder;
                        usedValueHolders.add(valueHolder);
                    }
                }
            }
        }
        return resolved;
    }

    private ConstructorArgumentValues resolveConstructorArguments(BeanDefinitionValueResolver valueResolver, ConstructorArgumentValues constructorArguments) {
        ConstructorArgumentValues resolvedConstructorArguments = new ConstructorArgumentValues();
        for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : constructorArguments.getIndexedArgumentValues().entrySet()) {
            resolvedConstructorArguments.addIndexedArgumentValue(entry.getKey().intValue(), resolveArgumentValue(valueResolver, entry.getValue()));
        }
        for (ConstructorArgumentValues.ValueHolder valueHolder : constructorArguments.getGenericArgumentValues()) {
            resolvedConstructorArguments.addGenericArgumentValue(resolveArgumentValue(valueResolver, valueHolder));
        }
        return resolvedConstructorArguments;
    }

    private ConstructorArgumentValues.ValueHolder resolveArgumentValue(BeanDefinitionValueResolver resolver, ConstructorArgumentValues.ValueHolder valueHolder) {
        if (valueHolder.isConverted()) {
            return valueHolder;
        }
        Object value = resolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
        ConstructorArgumentValues.ValueHolder resolvedHolder = new ConstructorArgumentValues.ValueHolder(value, valueHolder.getType(), valueHolder.getName());
        resolvedHolder.setSource(valueHolder);
        return resolvedHolder;
    }

    @Nullable
    private Object resolveAutowiredArgument(RegisteredBean registeredBean, DependencyDescriptor descriptor, @Nullable ConstructorArgumentValues.ValueHolder argumentValue, Set<String> autowiredBeanNames) {
        TypeConverter typeConverter = registeredBean.getBeanFactory().getTypeConverter();
        if (argumentValue != null) {
            return argumentValue.isConverted() ? argumentValue.getConvertedValue() : typeConverter.convertIfNecessary(argumentValue.getValue(), descriptor.getDependencyType(), descriptor.getMethodParameter());
        }
        try {
            return registeredBean.resolveAutowiredArgument(descriptor, typeConverter, autowiredBeanNames);
        } catch (BeansException ex) {
            throw new UnsatisfiedDependencyException((String) null, registeredBean.getBeanName(), descriptor, ex);
        }
    }

    private T instantiate(ConfigurableBeanFactory configurableBeanFactory, Executable executable, Object[] objArr) {
        if (executable instanceof Constructor) {
            Constructor<?> constructor = (Constructor) executable;
            try {
                return (T) instantiate(constructor, objArr);
            } catch (Exception e) {
                throw new BeanInstantiationException(constructor, e.getMessage(), e);
            }
        }
        if (executable instanceof Method) {
            Method method = (Method) executable;
            try {
                return (T) instantiate(configurableBeanFactory, method, objArr);
            } catch (Exception e2) {
                throw new BeanInstantiationException(method, e2.getMessage(), e2);
            }
        }
        throw new IllegalStateException("Unsupported executable " + executable.getClass().getName());
    }

    private Object instantiate(Constructor<?> constructor, Object[] args) throws Exception {
        Class<?> declaringClass = constructor.getDeclaringClass();
        if (ClassUtils.isInnerClass(declaringClass)) {
            Object enclosingInstance = createInstance(declaringClass.getEnclosingClass());
            args = ObjectUtils.addObjectToArray(args, enclosingInstance, 0);
        }
        return BeanUtils.instantiateClass(constructor, args);
    }

    private Object instantiate(ConfigurableBeanFactory beanFactory, Method method, Object[] args) throws Exception {
        Object target = getFactoryMethodTarget(beanFactory, method);
        ReflectionUtils.makeAccessible(method);
        return method.invoke(target, args);
    }

    @Nullable
    private Object getFactoryMethodTarget(BeanFactory beanFactory, Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            return null;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        return beanFactory.getBean(declaringClass);
    }

    private Object createInstance(Class<?> clazz) throws Exception {
        if (!ClassUtils.isInnerClass(clazz)) {
            Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[0]);
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance(new Object[0]);
        }
        Class<?> enclosingClass = clazz.getEnclosingClass();
        return clazz.getDeclaredConstructor(enclosingClass).newInstance(createInstance(enclosingClass));
    }

    private static String toCommaSeparatedNames(Class<?>... parameterTypes) {
        return (String) Arrays.stream(parameterTypes).map((v0) -> {
            return v0.getName();
        }).collect(Collectors.joining(", "));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanInstanceSupplier$ExecutableLookup.class */
    static abstract class ExecutableLookup {
        abstract Executable get(RegisteredBean registeredBean);

        ExecutableLookup() {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanInstanceSupplier$ConstructorLookup.class */
    private static class ConstructorLookup extends ExecutableLookup {
        private final Class<?>[] parameterTypes;

        ConstructorLookup(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        @Override // org.springframework.beans.factory.aot.BeanInstanceSupplier.ExecutableLookup
        public Executable get(RegisteredBean registeredBean) {
            Class<?> beanClass = registeredBean.getBeanClass();
            try {
                Class<?>[] actualParameterTypes = !ClassUtils.isInnerClass(beanClass) ? this.parameterTypes : (Class[]) ObjectUtils.addObjectToArray(this.parameterTypes, beanClass.getEnclosingClass(), 0);
                return beanClass.getDeclaredConstructor(actualParameterTypes);
            } catch (NoSuchMethodException ex) {
                throw new IllegalArgumentException("%s cannot be found on %s".formatted(this, beanClass.getName()), ex);
            }
        }

        public String toString() {
            return "Constructor with parameter types [%s]".formatted(BeanInstanceSupplier.toCommaSeparatedNames(this.parameterTypes));
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanInstanceSupplier$FactoryMethodLookup.class */
    private static class FactoryMethodLookup extends ExecutableLookup {
        private final Class<?> declaringClass;
        private final String methodName;
        private final Class<?>[] parameterTypes;

        FactoryMethodLookup(Class<?> declaringClass, String methodName, Class<?>[] parameterTypes) {
            this.declaringClass = declaringClass;
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        @Override // org.springframework.beans.factory.aot.BeanInstanceSupplier.ExecutableLookup
        public Executable get(RegisteredBean registeredBean) {
            return get();
        }

        Method get() {
            Method method = ReflectionUtils.findMethod(this.declaringClass, this.methodName, this.parameterTypes);
            Assert.notNull(method, (Supplier<String>) () -> {
                return "%s cannot be found".formatted(this);
            });
            return method;
        }

        public String toString() {
            return "Factory method '%s' with parameter types [%s] declared on %s".formatted(this.methodName, BeanInstanceSupplier.toCommaSeparatedNames(this.parameterTypes), this.declaringClass);
        }
    }
}
