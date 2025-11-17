package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.core.ClassLoaderAwareGeneratorStrategy;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.NoOp;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy.class */
public class CglibAopProxy implements AopProxy, Serializable {
    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;
    protected static final Log logger = LogFactory.getLog((Class<?>) CglibAopProxy.class);
    private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap();
    private static final String COROUTINES_FLOW_CLASS_NAME = "kotlinx.coroutines.flow.Flow";
    protected final AdvisedSupport advised;

    @Nullable
    protected Object[] constructorArgs;

    @Nullable
    protected Class<?>[] constructorArgTypes;
    private final transient AdvisedDispatcher advisedDispatcher;
    private transient Map<Method, Integer> fixedInterceptorMap = Collections.emptyMap();
    private transient int fixedInterceptorOffset;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$SerializableNoOp.class */
    public static class SerializableNoOp implements NoOp, Serializable {
    }

    public CglibAopProxy(AdvisedSupport config) throws AopConfigException {
        Assert.notNull(config, "AdvisedSupport must not be null");
        this.advised = config;
        this.advisedDispatcher = new AdvisedDispatcher(this.advised);
    }

    public void setConstructorArguments(@Nullable Object[] constructorArgs, @Nullable Class<?>[] constructorArgTypes) {
        if (constructorArgs == null || constructorArgTypes == null) {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
        if (constructorArgs.length != constructorArgTypes.length) {
            throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length + ") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
        }
        this.constructorArgs = constructorArgs;
        this.constructorArgTypes = constructorArgTypes;
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy() {
        return buildProxy(null, false);
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy(@Nullable ClassLoader classLoader) {
        return buildProxy(classLoader, false);
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Class<?> getProxyClass(@Nullable ClassLoader classLoader) {
        return (Class) buildProxy(classLoader, true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object buildProxy(@Nullable ClassLoader classLoader, boolean classOnly) {
        if (logger.isTraceEnabled()) {
            logger.trace("Creating CGLIB proxy: " + this.advised.getTargetSource());
        }
        try {
            try {
                Class<?> rootClass = this.advised.getTargetClass();
                Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");
                Class<?> proxySuperClass = rootClass;
                if (rootClass.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
                    proxySuperClass = rootClass.getSuperclass();
                    Class<?>[] additionalInterfaces = rootClass.getInterfaces();
                    for (Class<?> additionalInterface : additionalInterfaces) {
                        this.advised.addInterface(additionalInterface);
                    }
                }
                validateClassIfNecessary(proxySuperClass, classLoader);
                Enhancer enhancer = createEnhancer();
                if (classLoader != 0) {
                    enhancer.setClassLoader(classLoader);
                    if (classLoader instanceof SmartClassLoader) {
                        SmartClassLoader smartClassLoader = (SmartClassLoader) classLoader;
                        if (smartClassLoader.isClassReloadable(proxySuperClass)) {
                            enhancer.setUseCache(false);
                        }
                    }
                }
                enhancer.setSuperclass(proxySuperClass);
                enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
                enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
                enhancer.setAttemptLoad(true);
                enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(classLoader));
                Callback[] callbacks = getCallbacks(rootClass);
                Class<?>[] types = new Class[callbacks.length];
                for (int x = 0; x < types.length; x++) {
                    types[x] = callbacks[x].getClass();
                }
                ProxyCallbackFilter filter = new ProxyCallbackFilter(this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset);
                enhancer.setCallbackFilter(filter);
                enhancer.setCallbackTypes(types);
                try {
                    Object createProxyClass = classOnly ? createProxyClass(enhancer) : createProxyClassAndInstance(enhancer, callbacks);
                    filter.advised.reduceToAdvisorKey();
                    return createProxyClass;
                } catch (Throwable th) {
                    filter.advised.reduceToAdvisorKey();
                    throw th;
                }
            } catch (IllegalArgumentException | CodeGenerationException ex) {
                throw new AopConfigException("Could not generate CGLIB subclass of " + this.advised.getTargetClass() + ": Common causes of this problem include using a final class or a non-visible class", ex);
            }
        } catch (Throwable ex2) {
            throw new AopConfigException("Unexpected AOP exception", ex2);
        }
    }

    protected Class<?> createProxyClass(Enhancer enhancer) {
        enhancer.setInterceptDuringConstruction(false);
        return enhancer.createClass();
    }

    protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
        enhancer.setInterceptDuringConstruction(false);
        enhancer.setCallbacks(callbacks);
        if (this.constructorArgs != null && this.constructorArgTypes != null) {
            return enhancer.create(this.constructorArgTypes, this.constructorArgs);
        }
        return enhancer.create();
    }

    protected Enhancer createEnhancer() {
        return new Enhancer();
    }

    private void validateClassIfNecessary(Class<?> proxySuperClass, @Nullable ClassLoader proxyClassLoader) {
        if (!this.advised.isOptimize() && logger.isInfoEnabled()) {
            synchronized (validatedClasses) {
                validatedClasses.computeIfAbsent(proxySuperClass, clazz -> {
                    doValidateClass(clazz, proxyClassLoader, ClassUtils.getAllInterfacesForClassAsSet(clazz));
                    return Boolean.TRUE;
                });
            }
        }
    }

    private void doValidateClass(Class<?> proxySuperClass, @Nullable ClassLoader proxyClassLoader, Set<Class<?>> ifcs) {
        if (proxySuperClass != Object.class) {
            Method[] methods = proxySuperClass.getDeclaredMethods();
            for (Method method : methods) {
                int mod = method.getModifiers();
                if (!Modifier.isStatic(mod) && !Modifier.isPrivate(mod)) {
                    if (Modifier.isFinal(mod)) {
                        if (logger.isWarnEnabled() && implementsInterface(method, ifcs)) {
                            logger.warn("Unable to proxy interface-implementing method [" + method + "] because it is marked as final, consider using interface-based JDK proxies instead.");
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Final method [" + method + "] cannot get proxied via CGLIB: Calls to this method will NOT be routed to the target instance and might lead to NPEs against uninitialized fields in the proxy instance.");
                        }
                    } else if (logger.isDebugEnabled() && !Modifier.isPublic(mod) && !Modifier.isProtected(mod) && proxyClassLoader != null && proxySuperClass.getClassLoader() != proxyClassLoader) {
                        logger.debug("Method [" + method + "] is package-visible across different ClassLoaders and cannot get proxied via CGLIB: Declare this method as public or protected if you need to support invocations through the proxy.");
                    }
                }
            }
            doValidateClass(proxySuperClass.getSuperclass(), proxyClassLoader, ifcs);
        }
    }

    private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
        Callback dynamicUnadvisedInterceptor;
        Callback targetInterceptor;
        Callback dynamicUnadvisedExposedInterceptor;
        boolean isStatic = this.advised.getTargetSource().isStatic();
        boolean isFrozen = this.advised.isFrozen();
        boolean exposeProxy = this.advised.isExposeProxy();
        Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);
        if (exposeProxy) {
            if (isStatic) {
                dynamicUnadvisedExposedInterceptor = new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget());
            } else {
                dynamicUnadvisedExposedInterceptor = new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource());
            }
            targetInterceptor = dynamicUnadvisedExposedInterceptor;
        } else {
            if (isStatic) {
                dynamicUnadvisedInterceptor = new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget());
            } else {
                dynamicUnadvisedInterceptor = new DynamicUnadvisedInterceptor(this.advised.getTargetSource());
            }
            targetInterceptor = dynamicUnadvisedInterceptor;
        }
        Callback targetDispatcher = isStatic ? new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp();
        Callback[] mainCallbacks = {aopInterceptor, targetInterceptor, new SerializableNoOp(), targetDispatcher, this.advisedDispatcher, new EqualsInterceptor(this.advised), new HashCodeInterceptor(this.advised)};
        if (isStatic && isFrozen) {
            Method[] methods = rootClass.getMethods();
            int methodsCount = methods.length;
            List<Callback> fixedCallbacks = new ArrayList<>(methodsCount);
            this.fixedInterceptorMap = CollectionUtils.newHashMap(methodsCount);
            int advicedMethodCount = methodsCount;
            for (int x = 0; x < methodsCount; x++) {
                Method method = methods[x];
                if (method.getDeclaringClass() == Object.class) {
                    advicedMethodCount--;
                } else {
                    List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, rootClass);
                    fixedCallbacks.add(new FixedChainStaticTargetInterceptor(chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass()));
                    this.fixedInterceptorMap.put(method, Integer.valueOf(x - (methodsCount - advicedMethodCount)));
                }
            }
            Callback[] callbacks = new Callback[mainCallbacks.length + advicedMethodCount];
            System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
            System.arraycopy(fixedCallbacks.toArray(x$0 -> {
                return new Callback[x$0];
            }), 0, callbacks, mainCallbacks.length, advicedMethodCount);
            this.fixedInterceptorOffset = mainCallbacks.length;
            return callbacks;
        }
        return mainCallbacks;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof CglibAopProxy) {
                CglibAopProxy that = (CglibAopProxy) other;
                if (AopProxyUtils.equalsInProxy(this.advised, that.advised)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (CglibAopProxy.class.hashCode() * 13) + this.advised.getTargetSource().hashCode();
    }

    private static boolean implementsInterface(Method method, Set<Class<?>> ifcs) {
        for (Class<?> ifc : ifcs) {
            if (ClassUtils.hasMethod(ifc, method)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static Object processReturnType(Object proxy, @Nullable Object target, Method method, Object[] arguments, @Nullable Object returnValue) {
        if (returnValue != null && returnValue == target && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            returnValue = proxy;
        }
        Class<?> returnType = method.getReturnType();
        if (returnValue == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
        }
        if (KotlinDetector.isSuspendingFunction(method)) {
            if (COROUTINES_FLOW_CLASS_NAME.equals(new MethodParameter(method, -1).getParameterType().getName())) {
                return CoroutinesUtils.asFlow(returnValue);
            }
            return CoroutinesUtils.awaitSingleOrNull(returnValue, arguments[arguments.length - 1]);
        }
        return returnValue;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$StaticUnadvisedInterceptor.class */
    public static class StaticUnadvisedInterceptor implements MethodInterceptor, Serializable {

        @Nullable
        private final Object target;

        public StaticUnadvisedInterceptor(@Nullable Object target) {
            this.target = target;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object retVal = AopUtils.invokeJoinpointUsingReflection(this.target, method, args);
            return CglibAopProxy.processReturnType(proxy, this.target, method, args, retVal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$StaticUnadvisedExposedInterceptor.class */
    public static class StaticUnadvisedExposedInterceptor implements MethodInterceptor, Serializable {

        @Nullable
        private final Object target;

        public StaticUnadvisedExposedInterceptor(@Nullable Object target) {
            this.target = target;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = AopUtils.invokeJoinpointUsingReflection(this.target, method, args);
                Object processReturnType = CglibAopProxy.processReturnType(proxy, this.target, method, args, retVal);
                AopContext.setCurrentProxy(oldProxy);
                return processReturnType;
            } catch (Throwable th) {
                AopContext.setCurrentProxy(oldProxy);
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$DynamicUnadvisedInterceptor.class */
    public static class DynamicUnadvisedInterceptor implements MethodInterceptor, Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = this.targetSource.getTarget();
            try {
                Object retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
                Object processReturnType = CglibAopProxy.processReturnType(proxy, target, method, args, retVal);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                return processReturnType;
            } catch (Throwable th) {
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$DynamicUnadvisedExposedInterceptor.class */
    public static class DynamicUnadvisedExposedInterceptor implements MethodInterceptor, Serializable {
        private final TargetSource targetSource;

        public DynamicUnadvisedExposedInterceptor(TargetSource targetSource) {
            this.targetSource = targetSource;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object oldProxy = null;
            Object target = this.targetSource.getTarget();
            try {
                oldProxy = AopContext.setCurrentProxy(proxy);
                Object retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
                Object processReturnType = CglibAopProxy.processReturnType(proxy, target, method, args, retVal);
                AopContext.setCurrentProxy(oldProxy);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                return processReturnType;
            } catch (Throwable th) {
                AopContext.setCurrentProxy(oldProxy);
                if (target != null) {
                    this.targetSource.releaseTarget(target);
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$StaticDispatcher.class */
    public static class StaticDispatcher implements Dispatcher, Serializable {

        @Nullable
        private final Object target;

        public StaticDispatcher(@Nullable Object target) {
            this.target = target;
        }

        @Override // org.springframework.cglib.proxy.Dispatcher
        @Nullable
        public Object loadObject() {
            return this.target;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$AdvisedDispatcher.class */
    public static class AdvisedDispatcher implements Dispatcher, Serializable {
        private final AdvisedSupport advised;

        public AdvisedDispatcher(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.Dispatcher
        public Object loadObject() {
            return this.advised;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$EqualsInterceptor.class */
    public static class EqualsInterceptor implements MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public EqualsInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            boolean z;
            Object other = args[0];
            if (proxy == other) {
                return true;
            }
            if (other instanceof Factory) {
                Factory factory = (Factory) other;
                Callback callback = factory.getCallback(5);
                if (callback instanceof EqualsInterceptor) {
                    EqualsInterceptor that = (EqualsInterceptor) callback;
                    if (AopProxyUtils.equalsInProxy(this.advised, that.advised)) {
                        z = true;
                        return Boolean.valueOf(z);
                    }
                }
                z = false;
                return Boolean.valueOf(z);
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$HashCodeInterceptor.class */
    public static class HashCodeInterceptor implements MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public HashCodeInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
            return Integer.valueOf((CglibAopProxy.class.hashCode() * 13) + this.advised.getTargetSource().hashCode());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$FixedChainStaticTargetInterceptor.class */
    public static class FixedChainStaticTargetInterceptor implements MethodInterceptor, Serializable {
        private final List<Object> adviceChain;

        @Nullable
        private final Object target;

        @Nullable
        private final Class<?> targetClass;

        public FixedChainStaticTargetInterceptor(List<Object> adviceChain, @Nullable Object target, @Nullable Class<?> targetClass) {
            this.adviceChain = adviceChain;
            this.target = target;
            this.targetClass = targetClass;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            MethodInvocation invocation = new CglibMethodInvocation(proxy, this.target, method, args, this.targetClass, this.adviceChain, methodProxy);
            Object retVal = invocation.proceed();
            return CglibAopProxy.processReturnType(proxy, this.target, method, args, retVal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$DynamicAdvisedInterceptor.class */
    public static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {
        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override // org.springframework.cglib.proxy.MethodInterceptor
        @Nullable
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object retVal;
            Object oldProxy = null;
            boolean setProxyContext = false;
            Object target = null;
            TargetSource targetSource = this.advised.getTargetSource();
            try {
                if (this.advised.exposeProxy) {
                    oldProxy = AopContext.setCurrentProxy(proxy);
                    setProxyContext = true;
                }
                target = targetSource.getTarget();
                Class<?> targetClass = target != null ? target.getClass() : null;
                List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
                if (chain.isEmpty()) {
                    Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                    retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
                } else {
                    retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
                }
                Object processReturnType = CglibAopProxy.processReturnType(proxy, target, method, args, retVal);
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                return processReturnType;
            } catch (Throwable th) {
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                throw th;
            }
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof DynamicAdvisedInterceptor) {
                    DynamicAdvisedInterceptor dynamicAdvisedInterceptor = (DynamicAdvisedInterceptor) other;
                    if (this.advised.equals(dynamicAdvisedInterceptor.advised)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.advised.hashCode();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$CglibMethodInvocation.class */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
        public CglibMethodInvocation(Object proxy, @Nullable Object target, Method method, Object[] arguments, @Nullable Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
            super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
        }

        @Override // org.springframework.aop.framework.ReflectiveMethodInvocation, org.aopalliance.intercept.Joinpoint
        @Nullable
        public Object proceed() throws Throwable {
            try {
                return super.proceed();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex2) {
                if (ReflectionUtils.declaresException(getMethod(), ex2.getClass()) || KotlinDetector.isKotlinType(getMethod().getDeclaringClass())) {
                    throw ex2;
                }
                throw new UndeclaredThrowableException(ex2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/CglibAopProxy$ProxyCallbackFilter.class */
    public static class ProxyCallbackFilter implements CallbackFilter {
        final AdvisedSupport advised;
        private final Map<Method, Integer> fixedInterceptorMap;
        private final int fixedInterceptorOffset;

        public ProxyCallbackFilter(AdvisedSupport advised, Map<Method, Integer> fixedInterceptorMap, int fixedInterceptorOffset) {
            this.advised = advised;
            this.fixedInterceptorMap = fixedInterceptorMap;
            this.fixedInterceptorOffset = fixedInterceptorOffset;
        }

        @Override // org.springframework.cglib.proxy.CallbackFilter
        public int accept(Method method) {
            if (AopUtils.isFinalizeMethod(method)) {
                CglibAopProxy.logger.trace("Found finalize() method - using NO_OVERRIDE");
                return 2;
            }
            if (!this.advised.isOpaque() && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Method is declared on Advised interface: " + method);
                    return 4;
                }
                return 4;
            }
            if (AopUtils.isEqualsMethod(method)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Found 'equals' method: " + method);
                    return 5;
                }
                return 5;
            }
            if (AopUtils.isHashCodeMethod(method)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Found 'hashCode' method: " + method);
                    return 6;
                }
                return 6;
            }
            Class<?> targetClass = this.advised.getTargetClass();
            List<?> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            boolean haveAdvice = !chain.isEmpty();
            boolean isStatic = this.advised.getTargetSource().isStatic();
            boolean isFrozen = this.advised.isFrozen();
            boolean exposeProxy = this.advised.isExposeProxy();
            if (haveAdvice || !isFrozen) {
                if (exposeProxy) {
                    if (CglibAopProxy.logger.isTraceEnabled()) {
                        CglibAopProxy.logger.trace("Must expose proxy on advised method: " + method);
                        return 0;
                    }
                    return 0;
                }
                if (isStatic && isFrozen && this.fixedInterceptorMap.containsKey(method)) {
                    if (CglibAopProxy.logger.isTraceEnabled()) {
                        CglibAopProxy.logger.trace("Method has advice and optimizations are enabled: " + method);
                    }
                    int index = this.fixedInterceptorMap.get(method).intValue();
                    return index + this.fixedInterceptorOffset;
                }
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Unable to apply any optimizations to advised method: " + method);
                    return 0;
                }
                return 0;
            }
            if (exposeProxy || !isStatic) {
                return 1;
            }
            Class<?> returnType = method.getReturnType();
            if (targetClass != null && returnType.isAssignableFrom(targetClass)) {
                if (CglibAopProxy.logger.isTraceEnabled()) {
                    CglibAopProxy.logger.trace("Method return type is assignable from target type and may therefore return 'this' - using INVOKE_TARGET: " + method);
                    return 1;
                }
                return 1;
            }
            if (CglibAopProxy.logger.isTraceEnabled()) {
                CglibAopProxy.logger.trace("Method return type ensures 'this' cannot be returned - using DISPATCH_TARGET: " + method);
                return 3;
            }
            return 3;
        }

        @Override // org.springframework.cglib.proxy.CallbackFilter
        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ProxyCallbackFilter) {
                    ProxyCallbackFilter that = (ProxyCallbackFilter) other;
                    if (!this.advised.getAdvisorKey().equals(that.advised.getAdvisorKey()) || !AopProxyUtils.equalsProxiedInterfaces(this.advised, that.advised) || !ObjectUtils.nullSafeEquals(this.advised.getTargetClass(), that.advised.getTargetClass()) || this.advised.getTargetSource().isStatic() != that.advised.getTargetSource().isStatic() || this.advised.isFrozen() != that.advised.isFrozen() || this.advised.isExposeProxy() != that.advised.isExposeProxy() || this.advised.isOpaque() != that.advised.isOpaque()) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.advised.getAdvisorKey().hashCode();
        }
    }
}
