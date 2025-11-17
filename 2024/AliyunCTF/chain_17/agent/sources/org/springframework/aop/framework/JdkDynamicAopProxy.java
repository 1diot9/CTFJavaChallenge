package org.springframework.aop.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DecoratingProxy;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/JdkDynamicAopProxy.class */
final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {
    private static final long serialVersionUID = 5531744639992436476L;
    private static final String COROUTINES_FLOW_CLASS_NAME = "kotlinx.coroutines.flow.Flow";
    private static final Log logger = LogFactory.getLog((Class<?>) JdkDynamicAopProxy.class);
    private final AdvisedSupport advised;
    private transient ProxiedInterfacesCache cache;

    public JdkDynamicAopProxy(AdvisedSupport config) throws AopConfigException {
        ProxiedInterfacesCache cache;
        Assert.notNull(config, "AdvisedSupport must not be null");
        this.advised = config;
        Object obj = config.proxyMetadataCache;
        if (obj instanceof ProxiedInterfacesCache) {
            ProxiedInterfacesCache proxiedInterfacesCache = (ProxiedInterfacesCache) obj;
            cache = proxiedInterfacesCache;
        } else {
            cache = new ProxiedInterfacesCache(config);
            config.proxyMetadataCache = cache;
        }
        this.cache = cache;
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy() {
        return getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Object getProxy(@Nullable ClassLoader classLoader) {
        if (logger.isTraceEnabled()) {
            logger.trace("Creating JDK dynamic proxy: " + this.advised.getTargetSource());
        }
        return Proxy.newProxyInstance(determineClassLoader(classLoader), this.cache.proxiedInterfaces, this);
    }

    @Override // org.springframework.aop.framework.AopProxy
    public Class<?> getProxyClass(@Nullable ClassLoader classLoader) {
        return Proxy.getProxyClass(determineClassLoader(classLoader), this.cache.proxiedInterfaces);
    }

    private ClassLoader determineClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            return getClass().getClassLoader();
        }
        if (classLoader.getParent() == null) {
            ClassLoader aopClassLoader = getClass().getClassLoader();
            ClassLoader parent = aopClassLoader.getParent();
            while (true) {
                ClassLoader aopParent = parent;
                if (aopParent == null) {
                    break;
                }
                if (classLoader == aopParent) {
                    return aopClassLoader;
                }
                parent = aopParent.getParent();
            }
        }
        return classLoader;
    }

    @Override // java.lang.reflect.InvocationHandler
    @Nullable
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object retVal;
        Object oldProxy = null;
        boolean setProxyContext = false;
        TargetSource targetSource = this.advised.targetSource;
        try {
            if (!this.cache.equalsDefined && AopUtils.isEqualsMethod(method)) {
                Boolean valueOf = Boolean.valueOf(equals(args[0]));
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return valueOf;
            }
            if (!this.cache.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
                Integer valueOf2 = Integer.valueOf(hashCode());
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return valueOf2;
            }
            if (method.getDeclaringClass() == DecoratingProxy.class) {
                Class<?> ultimateTargetClass = AopProxyUtils.ultimateTargetClass(this.advised);
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return ultimateTargetClass;
            }
            if (!this.advised.opaque && method.getDeclaringClass().isInterface() && method.getDeclaringClass().isAssignableFrom(Advised.class)) {
                Object invokeJoinpointUsingReflection = AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
                if (0 != 0 && !targetSource.isStatic()) {
                    targetSource.releaseTarget(null);
                }
                if (0 != 0) {
                    AopContext.setCurrentProxy(null);
                }
                return invokeJoinpointUsingReflection;
            }
            if (this.advised.exposeProxy) {
                oldProxy = AopContext.setCurrentProxy(proxy);
                setProxyContext = true;
            }
            Object target = targetSource.getTarget();
            Class<?> targetClass = target != null ? target.getClass() : null;
            List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            if (chain.isEmpty()) {
                Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
                retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
            } else {
                MethodInvocation invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
                retVal = invocation.proceed();
            }
            Class<?> returnType = method.getReturnType();
            if (retVal != null && retVal == target && returnType != Object.class && returnType.isInstance(proxy) && !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
                retVal = proxy;
            } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
                throw new AopInvocationException("Null return value from advice does not match primitive return type for: " + method);
            }
            if (KotlinDetector.isSuspendingFunction(method)) {
                Object asFlow = COROUTINES_FLOW_CLASS_NAME.equals(new MethodParameter(method, -1).getParameterType().getName()) ? CoroutinesUtils.asFlow(retVal) : CoroutinesUtils.awaitSingleOrNull(retVal, args[args.length - 1]);
                if (target != null && !targetSource.isStatic()) {
                    targetSource.releaseTarget(target);
                }
                if (setProxyContext) {
                    AopContext.setCurrentProxy(oldProxy);
                }
                return asFlow;
            }
            Object obj = retVal;
            if (target != null && !targetSource.isStatic()) {
                targetSource.releaseTarget(target);
            }
            if (setProxyContext) {
                AopContext.setCurrentProxy(oldProxy);
            }
            return obj;
        } catch (Throwable th) {
            if (0 != 0 && !targetSource.isStatic()) {
                targetSource.releaseTarget(null);
            }
            if (0 != 0) {
                AopContext.setCurrentProxy(null);
            }
            throw th;
        }
    }

    public boolean equals(@Nullable Object other) {
        JdkDynamicAopProxy otherProxy;
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other instanceof JdkDynamicAopProxy) {
            JdkDynamicAopProxy jdkDynamicAopProxy = (JdkDynamicAopProxy) other;
            otherProxy = jdkDynamicAopProxy;
        } else if (Proxy.isProxyClass(other.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(other);
            if (!(ih instanceof JdkDynamicAopProxy)) {
                return false;
            }
            JdkDynamicAopProxy jdkDynamicAopProxy2 = (JdkDynamicAopProxy) ih;
            otherProxy = jdkDynamicAopProxy2;
        } else {
            return false;
        }
        return AopProxyUtils.equalsInProxy(this.advised, otherProxy.advised);
    }

    public int hashCode() {
        return (JdkDynamicAopProxy.class.hashCode() * 13) + this.advised.getTargetSource().hashCode();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.cache = new ProxiedInterfacesCache(this.advised);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/JdkDynamicAopProxy$ProxiedInterfacesCache.class */
    public static final class ProxiedInterfacesCache {
        final Class<?>[] proxiedInterfaces;
        final boolean equalsDefined;
        final boolean hashCodeDefined;

        ProxiedInterfacesCache(AdvisedSupport config) {
            this.proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(config, true);
            boolean equalsDefined = false;
            boolean hashCodeDefined = false;
            for (Class<?> proxiedInterface : this.proxiedInterfaces) {
                Method[] methods = proxiedInterface.getDeclaredMethods();
                for (Method method : methods) {
                    if (AopUtils.isEqualsMethod(method)) {
                        equalsDefined = true;
                        if (hashCodeDefined) {
                            break;
                        }
                    }
                    if (AopUtils.isHashCodeMethod(method)) {
                        hashCodeDefined = true;
                        if (equalsDefined) {
                            break;
                        }
                    }
                }
                if (equalsDefined && hashCodeDefined) {
                    break;
                }
            }
            this.equalsDefined = equalsDefined;
            this.hashCodeDefined = hashCodeDefined;
        }
    }
}
