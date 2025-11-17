package org.springframework.aop.framework;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.TargetSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.core.DecoratingProxy;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/AopProxyUtils.class */
public abstract class AopProxyUtils {
    @Nullable
    public static Object getSingletonTarget(Object candidate) {
        if (candidate instanceof Advised) {
            Advised advised = (Advised) candidate;
            TargetSource targetSource = advised.getTargetSource();
            if (targetSource instanceof SingletonTargetSource) {
                SingletonTargetSource singleTargetSource = (SingletonTargetSource) targetSource;
                return singleTargetSource.getTarget();
            }
            return null;
        }
        return null;
    }

    public static Class<?> ultimateTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Class<?> result = null;
        for (Object current = candidate; current instanceof TargetClassAware; current = getSingletonTarget(current)) {
            TargetClassAware targetClassAware = (TargetClassAware) current;
            result = targetClassAware.getTargetClass();
        }
        if (result == null) {
            result = AopUtils.isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass();
        }
        return result;
    }

    public static Class<?>[] completeJdkProxyInterfaces(Class<?>... userInterfaces) {
        List<Class<?>> completedInterfaces = new ArrayList<>(userInterfaces.length + 3);
        for (Class<?> ifc : userInterfaces) {
            Assert.notNull(ifc, "'userInterfaces' must not contain null values");
            Assert.isTrue(ifc.isInterface() && !ifc.isSealed(), (Supplier<String>) () -> {
                return ifc.getName() + " must be a non-sealed interface";
            });
            completedInterfaces.add(ifc);
        }
        completedInterfaces.add(SpringProxy.class);
        completedInterfaces.add(Advised.class);
        completedInterfaces.add(DecoratingProxy.class);
        return (Class[]) completedInterfaces.toArray(x$0 -> {
            return new Class[x$0];
        });
    }

    public static Class<?>[] completeProxiedInterfaces(AdvisedSupport advised) {
        return completeProxiedInterfaces(advised, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Class<?>[] completeProxiedInterfaces(AdvisedSupport advised, boolean decoratingProxy) {
        Class<?> targetClass;
        Class<?>[] specifiedInterfaces = advised.getProxiedInterfaces();
        if (specifiedInterfaces.length == 0 && (targetClass = advised.getTargetClass()) != null) {
            if (targetClass.isInterface()) {
                advised.setInterfaces(targetClass);
            } else if (Proxy.isProxyClass(targetClass) || ClassUtils.isLambdaClass(targetClass)) {
                advised.setInterfaces(targetClass.getInterfaces());
            }
            specifiedInterfaces = advised.getProxiedInterfaces();
        }
        List<Class<?>> proxiedInterfaces = new ArrayList<>(specifiedInterfaces.length + 3);
        for (Class<?> ifc : specifiedInterfaces) {
            if (!ifc.isSealed()) {
                proxiedInterfaces.add(ifc);
            }
        }
        if (!advised.isInterfaceProxied(SpringProxy.class)) {
            proxiedInterfaces.add(SpringProxy.class);
        }
        if (!advised.isOpaque() && !advised.isInterfaceProxied(Advised.class)) {
            proxiedInterfaces.add(Advised.class);
        }
        if (decoratingProxy && !advised.isInterfaceProxied(DecoratingProxy.class)) {
            proxiedInterfaces.add(DecoratingProxy.class);
        }
        return ClassUtils.toClassArray(proxiedInterfaces);
    }

    public static Class<?>[] proxiedUserInterfaces(Object proxy) {
        Class<?>[] proxyInterfaces = proxy.getClass().getInterfaces();
        int nonUserIfcCount = 0;
        if (proxy instanceof SpringProxy) {
            nonUserIfcCount = 0 + 1;
        }
        if (proxy instanceof Advised) {
            nonUserIfcCount++;
        }
        if (proxy instanceof DecoratingProxy) {
            nonUserIfcCount++;
        }
        Class<?>[] userInterfaces = (Class[]) Arrays.copyOf(proxyInterfaces, proxyInterfaces.length - nonUserIfcCount);
        Assert.notEmpty(userInterfaces, "JDK proxy must implement one or more interfaces");
        return userInterfaces;
    }

    public static boolean equalsInProxy(AdvisedSupport a, AdvisedSupport b) {
        return a == b || (equalsProxiedInterfaces(a, b) && equalsAdvisors(a, b) && a.getTargetSource().equals(b.getTargetSource()));
    }

    public static boolean equalsProxiedInterfaces(AdvisedSupport a, AdvisedSupport b) {
        return Arrays.equals(a.getProxiedInterfaces(), b.getProxiedInterfaces());
    }

    public static boolean equalsAdvisors(AdvisedSupport a, AdvisedSupport b) {
        return a.getAdvisorCount() == b.getAdvisorCount() && Arrays.equals(a.getAdvisors(), b.getAdvisors());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Object[] adaptArgumentsIfNecessary(Method method, @Nullable Object[] arguments) {
        if (ObjectUtils.isEmpty(arguments)) {
            return new Object[0];
        }
        if (method.isVarArgs() && method.getParameterCount() == arguments.length) {
            Class<?>[] paramTypes = method.getParameterTypes();
            int varargIndex = paramTypes.length - 1;
            Class<?> varargType = paramTypes[varargIndex];
            if (varargType.isArray()) {
                Object varargArray = arguments[varargIndex];
                if ((varargArray instanceof Object[]) && !varargType.isInstance(varargArray)) {
                    Object[] newArguments = new Object[arguments.length];
                    System.arraycopy(arguments, 0, newArguments, 0, varargIndex);
                    Class<?> targetElementType = varargType.componentType();
                    int varargLength = Array.getLength(varargArray);
                    Object newVarargArray = Array.newInstance(targetElementType, varargLength);
                    System.arraycopy(varargArray, 0, newVarargArray, 0, varargLength);
                    newArguments[varargIndex] = newVarargArray;
                    return newArguments;
                }
            }
        }
        return arguments;
    }
}
