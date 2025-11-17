package org.springframework.beans.factory.support;

import java.beans.ConstructorProperties;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.boot.web.context.WebServerGracefulShutdownLifecycle;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ConstructorResolver.class */
public class ConstructorResolver {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final NamedThreadLocal<InjectionPoint> currentInjectionPoint = new NamedThreadLocal<>("Current injection point");
    private final AbstractAutowireCapableBeanFactory beanFactory;
    private final Log logger;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ConstructorResolver$FallbackMode.class */
    public enum FallbackMode {
        NONE,
        ASSIGNABLE_ELEMENT,
        TYPE_CONVERSION
    }

    public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.logger = beanFactory.getLogger();
    }

    public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {
        int minNrOfArgs;
        ArgumentsHolder argsHolder;
        ParameterNameDiscoverer pnd;
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        Constructor<?> constructorToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                constructorToUse = (Constructor) mbd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
            }
        }
        if (constructorToUse == null || argsToUse == null) {
            Constructor<?>[] candidates = chosenCtors;
            if (candidates == null) {
                Class<?> beanClass = mbd.getBeanClass();
                try {
                    candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
                } catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Resolution of declared constructors on bean Class [" + beanClass.getName() + "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                }
            }
            if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
                Constructor<?> uniqueCandidate = candidates[0];
                if (uniqueCandidate.getParameterCount() == 0) {
                    synchronized (mbd.constructorArgumentLock) {
                        mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
                        mbd.constructorArgumentsResolved = true;
                        mbd.resolvedConstructorArguments = EMPTY_ARGS;
                    }
                    bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
                    return bw;
                }
            }
            boolean autowiring = chosenCtors != null || mbd.getResolvedAutowireMode() == 3;
            ConstructorArgumentValues resolvedValues = null;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }
            AutowireUtils.sortConstructors(candidates);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Constructor<?>> ambiguousConstructors = null;
            Deque<UnsatisfiedDependencyException> causes = null;
            for (Constructor<?> candidate : candidates) {
                int parameterCount = candidate.getParameterCount();
                if (constructorToUse != null && argsToUse != null && argsToUse.length > parameterCount) {
                    break;
                }
                if (parameterCount >= minNrOfArgs) {
                    Class<?>[] paramTypes = candidate.getParameterTypes();
                    if (resolvedValues != null) {
                        try {
                            String[] paramNames = null;
                            if (resolvedValues.containsNamedArgument()) {
                                paramNames = ConstructorPropertiesChecker.evaluate(candidate, parameterCount);
                                if (paramNames == null && (pnd = this.beanFactory.getParameterNameDiscoverer()) != null) {
                                    paramNames = pnd.getParameterNames(candidate);
                                }
                            }
                            argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
                        } catch (UnsatisfiedDependencyException ex2) {
                            if (this.logger.isTraceEnabled()) {
                                this.logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex2);
                            }
                            if (causes == null) {
                                causes = new ArrayDeque<>(1);
                            }
                            causes.add(ex2);
                        }
                    } else if (parameterCount == explicitArgs.length) {
                        argsHolder = new ArgumentsHolder(explicitArgs);
                    }
                    int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        constructorToUse = candidate;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousConstructors = null;
                    } else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                        if (ambiguousConstructors == null) {
                            ambiguousConstructors = new LinkedHashSet<>();
                            ambiguousConstructors.add(constructorToUse);
                        }
                        ambiguousConstructors.add(candidate);
                    }
                }
            }
            if (constructorToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex3 = causes.removeLast();
                    for (UnsatisfiedDependencyException cause : causes) {
                        this.beanFactory.onSuppressedException(cause);
                    }
                    throw ex3;
                }
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Could not resolve matching constructor on bean class [" + mbd.getBeanClassName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities. You should also check the consistency of arguments when mixing indexed and named arguments, especially in case of bean definition inheritance)");
            }
            if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous constructor matches found on bean class [" + mbd.getBeanClassName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousConstructors);
            }
            if (explicitArgs == null && argsHolderToUse != null) {
                argsHolderToUse.storeCache(mbd, constructorToUse);
            }
        }
        Assert.state(argsToUse != null, "Unresolved constructor arguments");
        bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
        return bw;
    }

    private Object instantiate(String beanName, RootBeanDefinition mbd, Constructor<?> constructorToUse, Object[] argsToUse) {
        try {
            InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
            return strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
        } catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, ex.getMessage(), ex);
        }
    }

    public void resolveFactoryMethodIfPossible(RootBeanDefinition mbd) {
        Class<?> factoryClass;
        boolean isStatic;
        if (mbd.getFactoryBeanName() != null) {
            factoryClass = this.beanFactory.getType(mbd.getFactoryBeanName());
            isStatic = false;
        } else {
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Assert.state(factoryClass != null, "Unresolvable factory class");
        Class<?> factoryClass2 = ClassUtils.getUserClass(factoryClass);
        Method[] candidates = getCandidateMethods(factoryClass2, mbd);
        Method uniqueCandidate = null;
        int length = candidates.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Method candidate = candidates[i];
            if ((!isStatic || isStaticCandidate(candidate, factoryClass2)) && mbd.isFactoryMethod(candidate)) {
                if (uniqueCandidate == null) {
                    uniqueCandidate = candidate;
                } else if (isParamMismatch(uniqueCandidate, candidate)) {
                    uniqueCandidate = null;
                    break;
                }
            }
            i++;
        }
        mbd.factoryMethodToIntrospect = uniqueCandidate;
    }

    private boolean isParamMismatch(Method uniqueCandidate, Method candidate) {
        int uniqueCandidateParameterCount = uniqueCandidate.getParameterCount();
        int candidateParameterCount = candidate.getParameterCount();
        return (uniqueCandidateParameterCount == candidateParameterCount && Arrays.equals(uniqueCandidate.getParameterTypes(), candidate.getParameterTypes())) ? false : true;
    }

    private Method[] getCandidateMethods(Class<?> factoryClass, RootBeanDefinition mbd) {
        return mbd.isNonPublicAccessAllowed() ? ReflectionUtils.getUniqueDeclaredMethods(factoryClass) : factoryClass.getMethods();
    }

    private boolean isStaticCandidate(Method method, Class<?> factoryClass) {
        return Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass() == factoryClass;
    }

    public BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
        Object factoryBean;
        Class<?> factoryClass;
        boolean isStatic;
        int minNrOfArgs;
        String simpleName;
        ArgumentsHolder argsHolder;
        ParameterNameDiscoverer pnd;
        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);
        String factoryBeanName = mbd.getFactoryBeanName();
        if (factoryBeanName != null) {
            if (factoryBeanName.equals(beanName)) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "factory-bean reference points back to the same bean definition");
            }
            factoryBean = this.beanFactory.getBean(factoryBeanName);
            if (mbd.isSingleton() && this.beanFactory.containsSingleton(beanName)) {
                throw new ImplicitlyAppearedSingletonException();
            }
            this.beanFactory.registerDependentBean(factoryBeanName, beanName);
            factoryClass = factoryBean.getClass();
            isStatic = false;
        } else {
            if (!mbd.hasBeanClass()) {
                throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName, "bean definition declares neither a bean class nor a factory-bean reference");
            }
            factoryBean = null;
            factoryClass = mbd.getBeanClass();
            isStatic = true;
        }
        Method factoryMethodToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;
        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        } else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                factoryMethodToUse = (Method) mbd.resolvedConstructorOrFactoryMethod;
                if (factoryMethodToUse != null && mbd.constructorArgumentsResolved) {
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = resolvePreparedArguments(beanName, mbd, bw, factoryMethodToUse, argsToResolve);
            }
        }
        if (factoryMethodToUse == null || argsToUse == null) {
            Class<?> factoryClass2 = ClassUtils.getUserClass(factoryClass);
            List<Method> candidates = null;
            if (mbd.isFactoryMethodUnique) {
                if (factoryMethodToUse == null) {
                    factoryMethodToUse = mbd.getResolvedFactoryMethod();
                }
                if (factoryMethodToUse != null) {
                    candidates = Collections.singletonList(factoryMethodToUse);
                }
            }
            if (candidates == null) {
                candidates = new ArrayList<>();
                Method[] rawCandidates = getCandidateMethods(factoryClass2, mbd);
                for (Method candidate : rawCandidates) {
                    if ((!isStatic || isStaticCandidate(candidate, factoryClass2)) && mbd.isFactoryMethod(candidate)) {
                        candidates.add(candidate);
                    }
                }
            }
            if (candidates.size() == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
                Method uniqueCandidate = candidates.get(0);
                if (uniqueCandidate.getParameterCount() == 0) {
                    mbd.factoryMethodToIntrospect = uniqueCandidate;
                    synchronized (mbd.constructorArgumentLock) {
                        mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
                        mbd.constructorArgumentsResolved = true;
                        mbd.resolvedConstructorArguments = EMPTY_ARGS;
                    }
                    bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, uniqueCandidate, EMPTY_ARGS));
                    return bw;
                }
            }
            if (candidates.size() > 1) {
                candidates.sort(AutowireUtils.EXECUTABLE_COMPARATOR);
            }
            ConstructorArgumentValues resolvedValues = null;
            boolean autowiring = mbd.getResolvedAutowireMode() == 3;
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Method> ambiguousFactoryMethods = null;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            } else if (mbd.hasConstructorArgumentValues()) {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            } else {
                minNrOfArgs = 0;
            }
            Deque<UnsatisfiedDependencyException> causes = null;
            for (Method candidate2 : candidates) {
                int parameterCount = candidate2.getParameterCount();
                if (parameterCount >= minNrOfArgs) {
                    Class<?>[] paramTypes = candidate2.getParameterTypes();
                    if (explicitArgs != null) {
                        if (paramTypes.length == explicitArgs.length) {
                            argsHolder = new ArgumentsHolder(explicitArgs);
                        }
                    } else {
                        String[] paramNames = null;
                        if (resolvedValues != null) {
                            try {
                                if (resolvedValues.containsNamedArgument() && (pnd = this.beanFactory.getParameterNameDiscoverer()) != null) {
                                    paramNames = pnd.getParameterNames(candidate2);
                                }
                            } catch (UnsatisfiedDependencyException ex) {
                                if (this.logger.isTraceEnabled()) {
                                    this.logger.trace("Ignoring factory method [" + candidate2 + "] of bean '" + beanName + "': " + ex);
                                }
                                if (causes == null) {
                                    causes = new ArrayDeque<>(1);
                                }
                                causes.add(ex);
                            }
                        }
                        argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames, candidate2, autowiring, candidates.size() == 1);
                    }
                    int typeDiffWeight = mbd.isLenientConstructorResolution() ? argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes);
                    if (typeDiffWeight < minTypeDiffWeight) {
                        factoryMethodToUse = candidate2;
                        argsHolderToUse = argsHolder;
                        argsToUse = argsHolder.arguments;
                        minTypeDiffWeight = typeDiffWeight;
                        ambiguousFactoryMethods = null;
                    } else if (factoryMethodToUse != null && typeDiffWeight == minTypeDiffWeight && !mbd.isLenientConstructorResolution() && paramTypes.length == factoryMethodToUse.getParameterCount() && !Arrays.equals(paramTypes, factoryMethodToUse.getParameterTypes())) {
                        if (ambiguousFactoryMethods == null) {
                            ambiguousFactoryMethods = new LinkedHashSet<>();
                            ambiguousFactoryMethods.add(factoryMethodToUse);
                        }
                        ambiguousFactoryMethods.add(candidate2);
                    }
                }
            }
            if (factoryMethodToUse == null || argsToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex2 = causes.removeLast();
                    for (UnsatisfiedDependencyException cause : causes) {
                        this.beanFactory.onSuppressedException(cause);
                    }
                    throw ex2;
                }
                List<String> argTypes = new ArrayList<>(minNrOfArgs);
                if (explicitArgs != null) {
                    int length = explicitArgs.length;
                    for (int i = 0; i < length; i++) {
                        Object arg = explicitArgs[i];
                        argTypes.add(arg != null ? arg.getClass().getSimpleName() : "null");
                    }
                } else if (resolvedValues != null) {
                    Set<ConstructorArgumentValues.ValueHolder> valueHolders = new LinkedHashSet<>(resolvedValues.getArgumentCount());
                    valueHolders.addAll(resolvedValues.getIndexedArgumentValues().values());
                    valueHolders.addAll(resolvedValues.getGenericArgumentValues());
                    for (ConstructorArgumentValues.ValueHolder value : valueHolders) {
                        if (value.getType() != null) {
                            simpleName = ClassUtils.getShortName(value.getType());
                        } else {
                            simpleName = value.getValue() != null ? value.getValue().getClass().getSimpleName() : "null";
                        }
                        String argType = simpleName;
                        argTypes.add(argType);
                    }
                }
                String argDesc = StringUtils.collectionToCommaDelimitedString(argTypes);
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found on class [" + factoryClass2.getName() + "]: " + (mbd.getFactoryBeanName() != null ? "factory bean '" + mbd.getFactoryBeanName() + "'; " : "") + "factory method '" + mbd.getFactoryMethodName() + "(" + argDesc + ")'. Check that a method with the specified name " + (minNrOfArgs > 0 ? "and arguments " : "") + "exists and that it is " + (isStatic ? "static" : "non-static") + ".");
            }
            if (Void.TYPE == factoryMethodToUse.getReturnType()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid factory method '" + mbd.getFactoryMethodName() + "' on class [" + factoryClass2.getName() + "]: needs to have a non-void return type!");
            }
            if (ambiguousFactoryMethods != null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Ambiguous factory method matches found on class [" + factoryClass2.getName() + "] (hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " + ambiguousFactoryMethods);
            }
            if (explicitArgs == null && argsHolderToUse != null) {
                mbd.factoryMethodToIntrospect = factoryMethodToUse;
                argsHolderToUse.storeCache(mbd, factoryMethodToUse);
            }
        }
        bw.setBeanInstance(instantiate(beanName, mbd, factoryBean, factoryMethodToUse, argsToUse));
        return bw;
    }

    private Object instantiate(String beanName, RootBeanDefinition mbd, @Nullable Object factoryBean, Method factoryMethod, Object[] args) {
        try {
            return this.beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, this.beanFactory, factoryBean, factoryMethod, args);
        } catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, ex.getMessage(), ex);
        }
    }

    private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, ConstructorArgumentValues cargs, ConstructorArgumentValues resolvedValues) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        int minNrOfArgs = cargs.getArgumentCount();
        for (Map.Entry<Integer, ConstructorArgumentValues.ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()) {
            int index = entry.getKey().intValue();
            if (index < 0) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid constructor argument index: " + index);
            }
            if (index + 1 > minNrOfArgs) {
                minNrOfArgs = index + 1;
            }
            ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
            if (valueHolder.isConverted()) {
                resolvedValues.addIndexedArgumentValue(index, valueHolder);
            } else {
                Object resolvedValue = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
                ConstructorArgumentValues.ValueHolder resolvedValueHolder = new ConstructorArgumentValues.ValueHolder(resolvedValue, valueHolder.getType(), valueHolder.getName());
                resolvedValueHolder.setSource(valueHolder);
                resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
            }
        }
        for (ConstructorArgumentValues.ValueHolder valueHolder2 : cargs.getGenericArgumentValues()) {
            if (valueHolder2.isConverted()) {
                resolvedValues.addGenericArgumentValue(valueHolder2);
            } else {
                Object resolvedValue2 = valueResolver.resolveValueIfNecessary("constructor argument", valueHolder2.getValue());
                ConstructorArgumentValues.ValueHolder resolvedValueHolder2 = new ConstructorArgumentValues.ValueHolder(resolvedValue2, valueHolder2.getType(), valueHolder2.getName());
                resolvedValueHolder2.setSource(valueHolder2);
                resolvedValues.addGenericArgumentValue(resolvedValueHolder2);
            }
        }
        return minNrOfArgs;
    }

    private ArgumentsHolder createArgumentArray(String beanName, RootBeanDefinition mbd, @Nullable ConstructorArgumentValues resolvedValues, BeanWrapper bw, Class<?>[] paramTypes, @Nullable String[] paramNames, Executable executable, boolean autowiring, boolean fallback) throws UnsatisfiedDependencyException {
        Object convertedValue;
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
        Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
        Set<String> allAutowiredBeanNames = new LinkedHashSet<>(paramTypes.length * 2);
        for (int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++) {
            Class<?> paramType = paramTypes[paramIndex];
            String paramName = paramNames != null ? paramNames[paramIndex] : "";
            ConstructorArgumentValues.ValueHolder valueHolder = null;
            if (resolvedValues != null) {
                valueHolder = resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
                if (valueHolder == null && (!autowiring || paramTypes.length == resolvedValues.getArgumentCount())) {
                    valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
                }
            }
            if (valueHolder != null) {
                usedValueHolders.add(valueHolder);
                Object originalValue = valueHolder.getValue();
                if (valueHolder.isConverted()) {
                    convertedValue = valueHolder.getConvertedValue();
                    args.preparedArguments[paramIndex] = convertedValue;
                } else {
                    MethodParameter methodParam = MethodParameter.forExecutable(executable, paramIndex);
                    try {
                        convertedValue = converter.convertIfNecessary(originalValue, paramType, methodParam);
                        Object sourceHolder = valueHolder.getSource();
                        if (sourceHolder instanceof ConstructorArgumentValues.ValueHolder) {
                            ConstructorArgumentValues.ValueHolder constructorValueHolder = (ConstructorArgumentValues.ValueHolder) sourceHolder;
                            Object sourceValue = constructorValueHolder.getValue();
                            args.resolveNecessary = true;
                            args.preparedArguments[paramIndex] = sourceValue;
                        }
                    } catch (TypeMismatchException ex) {
                        throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(valueHolder.getValue()) + "] to required type [" + paramType.getName() + "]: " + ex.getMessage());
                    }
                }
                args.arguments[paramIndex] = convertedValue;
                args.rawArguments[paramIndex] = originalValue;
            } else {
                MethodParameter methodParam2 = MethodParameter.forExecutable(executable, paramIndex);
                if (!autowiring) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam2), "Ambiguous argument values for parameter of type [" + paramType.getName() + "] - did you specify the correct bean references as arguments?");
                }
                try {
                    ConstructorDependencyDescriptor desc = new ConstructorDependencyDescriptor(methodParam2, true);
                    Set<String> autowiredBeanNames = new LinkedHashSet<>(2);
                    Object arg = resolveAutowiredArgument(desc, paramType, beanName, autowiredBeanNames, converter, fallback);
                    if (arg != null) {
                        setShortcutIfPossible(desc, paramType, autowiredBeanNames);
                    }
                    allAutowiredBeanNames.addAll(autowiredBeanNames);
                    args.rawArguments[paramIndex] = arg;
                    args.arguments[paramIndex] = arg;
                    args.preparedArguments[paramIndex] = desc;
                    args.resolveNecessary = true;
                } catch (BeansException ex2) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam2), ex2);
                }
            }
        }
        registerDependentBeans(executable, beanName, allAutowiredBeanNames);
        return args;
    }

    private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapper bw, Executable executable, Object[] argsToResolve) {
        TypeConverter customConverter = this.beanFactory.getCustomTypeConverter();
        TypeConverter converter = customConverter != null ? customConverter : bw;
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.beanFactory, beanName, mbd, converter);
        Class<?>[] paramTypes = executable.getParameterTypes();
        Object[] resolvedArgs = new Object[argsToResolve.length];
        for (int argIndex = 0; argIndex < argsToResolve.length; argIndex++) {
            Object argValue = argsToResolve[argIndex];
            Class<?> paramType = paramTypes[argIndex];
            boolean convertNecessary = false;
            if (argValue instanceof ConstructorDependencyDescriptor) {
                ConstructorDependencyDescriptor descriptor = (ConstructorDependencyDescriptor) argValue;
                try {
                    argValue = resolveAutowiredArgument(descriptor, paramType, beanName, null, converter, true);
                } catch (BeansException ex) {
                    Set<String> autowiredBeanNames = null;
                    if (descriptor.hasShortcut()) {
                        descriptor.setShortcut(null);
                        autowiredBeanNames = new LinkedHashSet<>(2);
                    }
                    this.logger.debug("Failed to resolve cached argument", ex);
                    argValue = resolveAutowiredArgument(descriptor, paramType, beanName, autowiredBeanNames, converter, true);
                    if (autowiredBeanNames != null && !descriptor.hasShortcut()) {
                        if (argValue != null) {
                            setShortcutIfPossible(descriptor, paramType, autowiredBeanNames);
                        }
                        registerDependentBeans(executable, beanName, autowiredBeanNames);
                    }
                }
            } else if (argValue instanceof BeanMetadataElement) {
                argValue = valueResolver.resolveValueIfNecessary("constructor argument", argValue);
                convertNecessary = true;
            } else if (argValue instanceof String) {
                String text = (String) argValue;
                argValue = this.beanFactory.evaluateBeanDefinitionString(text, mbd);
                convertNecessary = true;
            }
            if (convertNecessary) {
                MethodParameter methodParam = MethodParameter.forExecutable(executable, argIndex);
                try {
                    argValue = converter.convertIfNecessary(argValue, paramType, methodParam);
                } catch (TypeMismatchException ex2) {
                    throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, new InjectionPoint(methodParam), "Could not convert argument value of type [" + ObjectUtils.nullSafeClassName(argValue) + "] to required type [" + paramType.getName() + "]: " + ex2.getMessage());
                }
            }
            resolvedArgs[argIndex] = argValue;
        }
        return resolvedArgs;
    }

    private Constructor<?> getUserDeclaredConstructor(Constructor<?> constructor) {
        Class<?> declaringClass = constructor.getDeclaringClass();
        Class<?> userClass = ClassUtils.getUserClass(declaringClass);
        if (userClass != declaringClass) {
            try {
                return userClass.getDeclaredConstructor(constructor.getParameterTypes());
            } catch (NoSuchMethodException e) {
            }
        }
        return constructor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Object resolveAutowiredArgument(DependencyDescriptor descriptor, Class<?> paramType, String beanName, @Nullable Set<String> autowiredBeanNames, TypeConverter typeConverter, boolean fallback) {
        if (InjectionPoint.class.isAssignableFrom(paramType)) {
            InjectionPoint injectionPoint = currentInjectionPoint.get();
            if (injectionPoint == null) {
                throw new IllegalStateException("No current InjectionPoint available for " + descriptor);
            }
            return injectionPoint;
        }
        try {
            return this.beanFactory.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
        } catch (NoUniqueBeanDefinitionException ex) {
            throw ex;
        } catch (NoSuchBeanDefinitionException ex2) {
            if (fallback) {
                if (paramType.isArray()) {
                    return Array.newInstance(paramType.componentType(), 0);
                }
                if (CollectionFactory.isApproximableCollectionType(paramType)) {
                    return CollectionFactory.createCollection(paramType, 0);
                }
                if (CollectionFactory.isApproximableMapType(paramType)) {
                    return CollectionFactory.createMap(paramType, 0);
                }
            }
            throw ex2;
        }
    }

    private void setShortcutIfPossible(ConstructorDependencyDescriptor descriptor, Class<?> paramType, Set<String> autowiredBeanNames) {
        if (autowiredBeanNames.size() == 1) {
            String autowiredBeanName = autowiredBeanNames.iterator().next();
            if (this.beanFactory.containsBean(autowiredBeanName) && this.beanFactory.isTypeMatch(autowiredBeanName, paramType)) {
                descriptor.setShortcut(autowiredBeanName);
            }
        }
    }

    private void registerDependentBeans(Executable executable, String beanName, Set<String> autowiredBeanNames) {
        for (String autowiredBeanName : autowiredBeanNames) {
            this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Autowiring by type from bean name '" + beanName + "' via " + (executable instanceof Constructor ? BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE : "factory method") + " to bean named '" + autowiredBeanName + "'");
            }
        }
    }

    public Executable resolveConstructorOrFactoryMethod(String beanName, RootBeanDefinition mbd) {
        Supplier<ResolvableType> beanType = () -> {
            return getBeanType(beanName, mbd);
        };
        List<ResolvableType> valueTypes = mbd.hasConstructorArgumentValues() ? determineParameterValueTypes(mbd) : Collections.emptyList();
        Method resolvedFactoryMethod = resolveFactoryMethod(beanName, mbd, valueTypes);
        if (resolvedFactoryMethod != null) {
            return resolvedFactoryMethod;
        }
        Class<?> factoryBeanClass = getFactoryBeanClass(beanName, mbd);
        if (factoryBeanClass != null && !factoryBeanClass.equals(mbd.getResolvableType().toClass())) {
            ResolvableType resolvableType = mbd.getResolvableType();
            boolean isCompatible = ResolvableType.forClass(factoryBeanClass).as(FactoryBean.class).getGeneric(0).isAssignableFrom(resolvableType);
            Assert.state(isCompatible, (Supplier<String>) () -> {
                return String.format("Incompatible target type '%s' for factory bean '%s'", resolvableType.toClass().getName(), factoryBeanClass.getName());
            });
            Constructor<?> constructor = resolveConstructor(beanName, mbd, () -> {
                return ResolvableType.forClass(factoryBeanClass);
            }, valueTypes);
            if (constructor != null) {
                return constructor;
            }
            throw new IllegalStateException("No suitable FactoryBean constructor found for " + mbd + " and argument types " + valueTypes);
        }
        Constructor<?> constructor2 = resolveConstructor(beanName, mbd, beanType, valueTypes);
        if (constructor2 != null) {
            return constructor2;
        }
        throw new IllegalStateException("No constructor or factory method candidate found for " + mbd + " and argument types " + valueTypes);
    }

    private List<ResolvableType> determineParameterValueTypes(RootBeanDefinition mbd) {
        List<ResolvableType> parameterTypes = new ArrayList<>();
        for (ConstructorArgumentValues.ValueHolder valueHolder : mbd.getConstructorArgumentValues().getIndexedArgumentValues().values()) {
            parameterTypes.add(determineParameterValueType(mbd, valueHolder));
        }
        for (ConstructorArgumentValues.ValueHolder valueHolder2 : mbd.getConstructorArgumentValues().getGenericArgumentValues()) {
            parameterTypes.add(determineParameterValueType(mbd, valueHolder2));
        }
        return parameterTypes;
    }

    private ResolvableType determineParameterValueType(RootBeanDefinition mbd, ConstructorArgumentValues.ValueHolder valueHolder) {
        if (valueHolder.getType() != null) {
            return ResolvableType.forClass(ClassUtils.resolveClassName(valueHolder.getType(), this.beanFactory.getBeanClassLoader()));
        }
        Object value = valueHolder.getValue();
        if (value instanceof BeanReference) {
            BeanReference br = (BeanReference) value;
            if (value instanceof RuntimeBeanReference) {
                RuntimeBeanReference rbr = (RuntimeBeanReference) value;
                if (rbr.getBeanType() != null) {
                    return ResolvableType.forClass(rbr.getBeanType());
                }
            }
            return ResolvableType.forClass(this.beanFactory.getType(br.getBeanName(), false));
        }
        if (value instanceof BeanDefinition) {
            BeanDefinition innerBd = (BeanDefinition) value;
            ResolvableType type = getBeanType("(inner bean)", this.beanFactory.getMergedBeanDefinition("(inner bean)", innerBd, mbd));
            return FactoryBean.class.isAssignableFrom(type.toClass()) ? type.as(FactoryBean.class).getGeneric(0) : type;
        }
        if (value instanceof TypedStringValue) {
            TypedStringValue typedValue = (TypedStringValue) value;
            if (typedValue.hasTargetType()) {
                return ResolvableType.forClass(typedValue.getTargetType());
            }
            return ResolvableType.forClass(String.class);
        }
        if (value instanceof Class) {
            Class<?> clazz = (Class) value;
            return ResolvableType.forClassWithGenerics((Class<?>) Class.class, (Class<?>[]) new Class[]{clazz});
        }
        return ResolvableType.forInstance(value);
    }

    @Nullable
    private Constructor<?> resolveConstructor(String beanName, RootBeanDefinition mbd, Supplier<ResolvableType> beanType, List<ResolvableType> valueTypes) {
        Class<?> type = ClassUtils.getUserClass(beanType.get().toClass());
        Constructor<?>[] ctors = this.beanFactory.determineConstructorsFromBeanPostProcessors(type, beanName);
        if (ctors == null) {
            if (!mbd.hasConstructorArgumentValues()) {
                ctors = mbd.getPreferredConstructors();
            }
            if (ctors == null) {
                ctors = mbd.isNonPublicAccessAllowed() ? type.getDeclaredConstructors() : type.getConstructors();
            }
        }
        if (ctors.length == 1) {
            return ctors[0];
        }
        Function<Constructor<?>, List<ResolvableType>> parameterTypesFactory = executable -> {
            List<ResolvableType> types = new ArrayList<>();
            for (int i = 0; i < executable.getParameterCount(); i++) {
                types.add(ResolvableType.forConstructorParameter(executable, i));
            }
            return types;
        };
        List<Constructor<?>> matches = Arrays.stream(ctors).filter(executable2 -> {
            return match((List) parameterTypesFactory.apply(executable2), valueTypes, FallbackMode.NONE);
        }).toList();
        if (matches.size() == 1) {
            return matches.get(0);
        }
        List<Constructor<?>> assignableElementFallbackMatches = Arrays.stream(ctors).filter(executable3 -> {
            return match((List) parameterTypesFactory.apply(executable3), valueTypes, FallbackMode.ASSIGNABLE_ELEMENT);
        }).toList();
        if (assignableElementFallbackMatches.size() == 1) {
            return assignableElementFallbackMatches.get(0);
        }
        List<Constructor<?>> typeConversionFallbackMatches = Arrays.stream(ctors).filter(executable4 -> {
            return match((List) parameterTypesFactory.apply(executable4), valueTypes, FallbackMode.TYPE_CONVERSION);
        }).toList();
        if (typeConversionFallbackMatches.size() == 1) {
            return typeConversionFallbackMatches.get(0);
        }
        return null;
    }

    @Nullable
    private Method resolveFactoryMethod(String beanName, RootBeanDefinition mbd, List<ResolvableType> valueTypes) {
        Class<?> factoryClass;
        boolean isStatic;
        Method resolvedFactoryMethod;
        if (mbd.isFactoryMethodUnique && (resolvedFactoryMethod = mbd.getResolvedFactoryMethod()) != null) {
            return resolvedFactoryMethod;
        }
        String factoryMethodName = mbd.getFactoryMethodName();
        if (factoryMethodName != null) {
            String factoryBeanName = mbd.getFactoryBeanName();
            if (factoryBeanName != null) {
                factoryClass = this.beanFactory.getType(factoryBeanName);
                isStatic = false;
            } else {
                factoryClass = this.beanFactory.resolveBeanClass(mbd, beanName, new Class[0]);
                isStatic = true;
            }
            Assert.state(factoryClass != null, (Supplier<String>) () -> {
                return "Failed to determine bean class of " + mbd;
            });
            Method[] rawCandidates = getCandidateMethods(factoryClass, mbd);
            List<Method> candidates = new ArrayList<>();
            for (Method candidate : rawCandidates) {
                if ((!isStatic || isStaticCandidate(candidate, factoryClass)) && mbd.isFactoryMethod(candidate)) {
                    candidates.add(candidate);
                }
            }
            Method result = null;
            if (candidates.size() == 1) {
                result = candidates.get(0);
            } else if (candidates.size() > 1) {
                Function<Method, List<ResolvableType>> parameterTypesFactory = method -> {
                    List<ResolvableType> types = new ArrayList<>();
                    for (int i = 0; i < method.getParameterCount(); i++) {
                        types.add(ResolvableType.forMethodParameter(method, i));
                    }
                    return types;
                };
                result = resolveFactoryMethod(candidates, parameterTypesFactory, valueTypes);
            }
            if (result == null) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "No matching factory method found on class [" + factoryClass.getName() + "]: " + (mbd.getFactoryBeanName() != null ? "factory bean '" + mbd.getFactoryBeanName() + "'; " : "") + "factory method '" + mbd.getFactoryMethodName() + "'. ");
            }
            return result;
        }
        return null;
    }

    @Nullable
    private Method resolveFactoryMethod(List<Method> executables, Function<Method, List<ResolvableType>> parameterTypesFactory, List<ResolvableType> valueTypes) {
        List<Method> matches = executables.stream().filter(executable -> {
            return match((List) parameterTypesFactory.apply(executable), valueTypes, FallbackMode.NONE);
        }).toList();
        if (matches.size() == 1) {
            return matches.get(0);
        }
        List<Method> assignableElementFallbackMatches = executables.stream().filter(executable2 -> {
            return match((List) parameterTypesFactory.apply(executable2), valueTypes, FallbackMode.ASSIGNABLE_ELEMENT);
        }).toList();
        if (assignableElementFallbackMatches.size() == 1) {
            return assignableElementFallbackMatches.get(0);
        }
        List<Method> typeConversionFallbackMatches = executables.stream().filter(executable3 -> {
            return match((List) parameterTypesFactory.apply(executable3), valueTypes, FallbackMode.TYPE_CONVERSION);
        }).toList();
        Assert.state(typeConversionFallbackMatches.size() <= 1, (Supplier<String>) () -> {
            return "Multiple matches with parameters '" + valueTypes + "': " + typeConversionFallbackMatches;
        });
        if (typeConversionFallbackMatches.size() == 1) {
            return typeConversionFallbackMatches.get(0);
        }
        return null;
    }

    private boolean match(List<ResolvableType> parameterTypes, List<ResolvableType> valueTypes, FallbackMode fallbackMode) {
        if (parameterTypes.size() != valueTypes.size()) {
            return false;
        }
        for (int i = 0; i < parameterTypes.size(); i++) {
            if (!isMatch(parameterTypes.get(i), valueTypes.get(i), fallbackMode)) {
                return false;
            }
        }
        return true;
    }

    private boolean isMatch(ResolvableType parameterType, ResolvableType valueType, FallbackMode fallbackMode) {
        if (isAssignable(valueType).test(parameterType)) {
            return true;
        }
        switch (fallbackMode) {
            case ASSIGNABLE_ELEMENT:
                return isAssignable(valueType).test(extractElementType(parameterType));
            case TYPE_CONVERSION:
                return typeConversionFallback(valueType).test(parameterType);
            default:
                return false;
        }
    }

    private Predicate<ResolvableType> isAssignable(ResolvableType valueType) {
        return parameterType -> {
            return valueType == ResolvableType.NONE || parameterType.isAssignableFrom(valueType);
        };
    }

    private ResolvableType extractElementType(ResolvableType parameterType) {
        if (parameterType.isArray()) {
            return parameterType.getComponentType();
        }
        return Collection.class.isAssignableFrom(parameterType.toClass()) ? parameterType.as(Collection.class).getGeneric(0) : ResolvableType.NONE;
    }

    private Predicate<ResolvableType> typeConversionFallback(ResolvableType valueType) {
        return parameterType -> {
            if (valueOrCollection(valueType, this::isStringForClassFallback).test(parameterType)) {
                return true;
            }
            return valueOrCollection(valueType, this::isSimpleValueType).test(parameterType);
        };
    }

    private Predicate<ResolvableType> valueOrCollection(ResolvableType valueType, Function<ResolvableType, Predicate<ResolvableType>> predicateProvider) {
        return parameterType -> {
            if (((Predicate) predicateProvider.apply(valueType)).test(parameterType) || ((Predicate) predicateProvider.apply(extractElementType(valueType))).test(extractElementType(parameterType))) {
                return true;
            }
            return ((Predicate) predicateProvider.apply(valueType)).test(extractElementType(parameterType));
        };
    }

    private Predicate<ResolvableType> isStringForClassFallback(ResolvableType valueType) {
        return parameterType -> {
            return valueType.isAssignableFrom(String.class) && parameterType.isAssignableFrom(Class.class);
        };
    }

    private Predicate<ResolvableType> isSimpleValueType(ResolvableType valueType) {
        return parameterType -> {
            return BeanUtils.isSimpleValueType(parameterType.toClass()) && BeanUtils.isSimpleValueType(valueType.toClass());
        };
    }

    @Nullable
    private Class<?> getFactoryBeanClass(String beanName, RootBeanDefinition mbd) {
        Class<?> beanClass = this.beanFactory.resolveBeanClass(mbd, beanName, new Class[0]);
        if (beanClass == null || !FactoryBean.class.isAssignableFrom(beanClass)) {
            return null;
        }
        return beanClass;
    }

    private ResolvableType getBeanType(String beanName, RootBeanDefinition mbd) {
        ResolvableType resolvableType = mbd.getResolvableType();
        if (resolvableType != ResolvableType.NONE) {
            return resolvableType;
        }
        return ResolvableType.forClass(this.beanFactory.resolveBeanClass(mbd, beanName, new Class[0]));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static InjectionPoint setCurrentInjectionPoint(@Nullable InjectionPoint injectionPoint) {
        InjectionPoint old = currentInjectionPoint.get();
        if (injectionPoint != null) {
            currentInjectionPoint.set(injectionPoint);
        } else {
            currentInjectionPoint.remove();
        }
        return old;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static Constructor<?>[] determinePreferredConstructors(Class<?> clazz) {
        Constructor<?> defaultCtor;
        Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
        try {
            defaultCtor = clazz.getDeclaredConstructor(new Class[0]);
        } catch (NoSuchMethodException e) {
            defaultCtor = null;
        }
        if (primaryCtor != null) {
            if (defaultCtor != null && !primaryCtor.equals(defaultCtor)) {
                return new Constructor[]{primaryCtor, defaultCtor};
            }
            return new Constructor[]{primaryCtor};
        }
        Constructor<?>[] ctors = clazz.getConstructors();
        if (ctors.length == 1) {
            if (defaultCtor != null && !ctors[0].equals(defaultCtor)) {
                return new Constructor[]{ctors[0], defaultCtor};
            }
            return ctors;
        }
        if (ctors.length == 0) {
            Constructor<?>[] ctors2 = clazz.getDeclaredConstructors();
            if (ctors2.length == 1) {
                return ctors2;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ConstructorResolver$ArgumentsHolder.class */
    public static class ArgumentsHolder {
        public final Object[] rawArguments;
        public final Object[] arguments;
        public final Object[] preparedArguments;
        public boolean resolveNecessary = false;

        public ArgumentsHolder(int size) {
            this.rawArguments = new Object[size];
            this.arguments = new Object[size];
            this.preparedArguments = new Object[size];
        }

        public ArgumentsHolder(Object[] args) {
            this.rawArguments = args;
            this.arguments = args;
            this.preparedArguments = args;
        }

        public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
            int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
            int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
            return Math.min(rawTypeDiffWeight, typeDiffWeight);
        }

        public int getAssignabilityWeight(Class<?>[] paramTypes) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
                    return Integer.MAX_VALUE;
                }
            }
            for (int i2 = 0; i2 < paramTypes.length; i2++) {
                if (!ClassUtils.isAssignableValue(paramTypes[i2], this.rawArguments[i2])) {
                    return 2147483135;
                }
            }
            return WebServerGracefulShutdownLifecycle.SMART_LIFECYCLE_PHASE;
        }

        public void storeCache(RootBeanDefinition mbd, Executable constructorOrFactoryMethod) {
            synchronized (mbd.constructorArgumentLock) {
                mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
                mbd.constructorArgumentsResolved = true;
                if (this.resolveNecessary) {
                    mbd.preparedConstructorArguments = this.preparedArguments;
                } else {
                    mbd.resolvedConstructorArguments = this.arguments;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ConstructorResolver$ConstructorPropertiesChecker.class */
    public static class ConstructorPropertiesChecker {
        private ConstructorPropertiesChecker() {
        }

        @Nullable
        public static String[] evaluate(Constructor<?> candidate, int paramCount) {
            ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
            if (cp != null) {
                String[] names = cp.value();
                if (names.length != paramCount) {
                    throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not corresponding to actual number of parameters (" + paramCount + "): " + candidate);
                }
                return names;
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/support/ConstructorResolver$ConstructorDependencyDescriptor.class */
    public static class ConstructorDependencyDescriptor extends DependencyDescriptor {

        @Nullable
        private volatile String shortcut;

        public ConstructorDependencyDescriptor(MethodParameter methodParameter, boolean required) {
            super(methodParameter, required);
        }

        public void setShortcut(@Nullable String shortcut) {
            this.shortcut = shortcut;
        }

        public boolean hasShortcut() {
            return this.shortcut != null;
        }

        @Override // org.springframework.beans.factory.config.DependencyDescriptor
        public Object resolveShortcut(BeanFactory beanFactory) {
            String shortcut = this.shortcut;
            if (shortcut != null) {
                return beanFactory.getBean(shortcut, getDependencyType());
            }
            return null;
        }
    }
}
