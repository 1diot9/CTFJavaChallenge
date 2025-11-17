package org.springframework.aop.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlinx.coroutines.Job;
import org.reactivestreams.Publisher;
import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/AopUtils.class */
public abstract class AopUtils {
    public static boolean isAopProxy(@Nullable Object object) {
        return (object instanceof SpringProxy) && (Proxy.isProxyClass(object.getClass()) || object.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR));
    }

    public static boolean isJdkDynamicProxy(@Nullable Object object) {
        return (object instanceof SpringProxy) && Proxy.isProxyClass(object.getClass());
    }

    public static boolean isCglibProxy(@Nullable Object object) {
        return (object instanceof SpringProxy) && object.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR);
    }

    public static Class<?> getTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Class<?> result = null;
        if (candidate instanceof TargetClassAware) {
            TargetClassAware targetClassAware = (TargetClassAware) candidate;
            result = targetClassAware.getTargetClass();
        }
        if (result == null) {
            result = isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass();
        }
        return result;
    }

    public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
        if (targetType == null) {
            return method;
        }
        Method methodToUse = MethodIntrospector.selectInvocableMethod(method, targetType);
        if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier.isStatic(methodToUse.getModifiers()) && SpringProxy.class.isAssignableFrom(targetType)) {
            throw new IllegalStateException(String.format("Need to invoke method '%s' found on proxy for target class '%s' but cannot be delegated to target bean. Switch its visibility to package or protected.", method.getName(), method.getDeclaringClass().getSimpleName()));
        }
        return methodToUse;
    }

    public static boolean isEqualsMethod(@Nullable Method method) {
        return ReflectionUtils.isEqualsMethod(method);
    }

    public static boolean isHashCodeMethod(@Nullable Method method) {
        return ReflectionUtils.isHashCodeMethod(method);
    }

    public static boolean isToStringMethod(@Nullable Method method) {
        return ReflectionUtils.isToStringMethod(method);
    }

    public static boolean isFinalizeMethod(@Nullable Method method) {
        return method != null && method.getName().equals("finalize") && method.getParameterCount() == 0;
    }

    public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
        Class<?> specificTargetClass = targetClass != null ? ClassUtils.getUserClass(targetClass) : null;
        return BridgeMethodResolver.getMostSpecificMethod(method, specificTargetClass);
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass) {
        return canApply(pc, targetClass, false);
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
        Assert.notNull(pc, "Pointcut must not be null");
        if (!pc.getClassFilter().matches(targetClass)) {
            return false;
        }
        MethodMatcher methodMatcher = pc.getMethodMatcher();
        if (methodMatcher == MethodMatcher.TRUE) {
            return true;
        }
        IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
        if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
            IntroductionAwareMethodMatcher iamm = (IntroductionAwareMethodMatcher) methodMatcher;
            introductionAwareMethodMatcher = iamm;
        }
        Set<Class<?>> classes = new LinkedHashSet<>();
        if (!Proxy.isProxyClass(targetClass)) {
            classes.add(ClassUtils.getUserClass(targetClass));
        }
        classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
        for (Class<?> clazz : classes) {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
            for (Method method : methods) {
                if (introductionAwareMethodMatcher != null) {
                    if (introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions)) {
                        return true;
                    }
                } else if (methodMatcher.matches(method, targetClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canApply(Advisor advisor, Class<?> targetClass) {
        return canApply(advisor, targetClass, false);
    }

    public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
        if (advisor instanceof IntroductionAdvisor) {
            IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
            return ia.getClassFilter().matches(targetClass);
        }
        if (advisor instanceof PointcutAdvisor) {
            PointcutAdvisor pca = (PointcutAdvisor) advisor;
            return canApply(pca.getPointcut(), targetClass, hasIntroductions);
        }
        return true;
    }

    public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        List<Advisor> eligibleAdvisors = new ArrayList<>();
        for (Advisor candidate : candidateAdvisors) {
            if ((candidate instanceof IntroductionAdvisor) && canApply(candidate, clazz)) {
                eligibleAdvisors.add(candidate);
            }
        }
        boolean hasIntroductions = !eligibleAdvisors.isEmpty();
        for (Advisor candidate2 : candidateAdvisors) {
            if (!(candidate2 instanceof IntroductionAdvisor) && canApply(candidate2, clazz, hasIntroductions)) {
                eligibleAdvisors.add(candidate2);
            }
        }
        return eligibleAdvisors;
    }

    @Nullable
    public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args) throws Throwable {
        try {
            ReflectionUtils.makeAccessible(method);
            return KotlinDetector.isSuspendingFunction(method) ? KotlinDelegate.invokeSuspendingFunction(method, target, args) : method.invoke(target, args);
        } catch (IllegalAccessException ex) {
            throw new AopInvocationException("Could not access method [" + method + "]", ex);
        } catch (IllegalArgumentException ex2) {
            throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" + method + "] on target [" + target + "]", ex2);
        } catch (InvocationTargetException ex3) {
            throw ex3.getTargetException();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/AopUtils$KotlinDelegate.class */
    private static class KotlinDelegate {
        private KotlinDelegate() {
        }

        public static Publisher<?> invokeSuspendingFunction(Method method, Object target, Object... args) {
            Continuation<?> continuation = (Continuation) args[args.length - 1];
            Assert.state(continuation != null, "No Continuation available");
            CoroutineContext context = continuation.getContext().minusKey(Job.Key);
            return CoroutinesUtils.invokeSuspendingFunction(context, method, target, args);
        }
    }
}
