package org.springframework.core;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/MethodIntrospector.class */
public final class MethodIntrospector {

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/MethodIntrospector$MetadataLookup.class */
    public interface MetadataLookup<T> {
        @Nullable
        T inspect(Method method);
    }

    private MethodIntrospector() {
    }

    public static <T> Map<Method, T> selectMethods(Class<?> targetType, final MetadataLookup<T> metadataLookup) {
        Map<Method, T> methodMap = new LinkedHashMap<>();
        Set<Class<?>> handlerTypes = new LinkedHashSet<>();
        Class<?> specificHandlerType = null;
        if (!Proxy.isProxyClass(targetType)) {
            specificHandlerType = ClassUtils.getUserClass(targetType);
            handlerTypes.add(specificHandlerType);
        }
        handlerTypes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetType));
        for (Class<?> currentHandlerType : handlerTypes) {
            Class<?> targetClass = specificHandlerType != null ? specificHandlerType : currentHandlerType;
            ReflectionUtils.doWithMethods(currentHandlerType, method -> {
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                Object inspect = metadataLookup.inspect(specificMethod);
                if (inspect != null) {
                    Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
                    if (bridgedMethod == specificMethod || bridgedMethod == method || metadataLookup.inspect(bridgedMethod) == null) {
                        methodMap.put(specificMethod, inspect);
                    }
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
        }
        return methodMap;
    }

    public static Set<Method> selectMethods(Class<?> targetType, final ReflectionUtils.MethodFilter methodFilter) {
        return selectMethods(targetType, method -> {
            if (methodFilter.matches(method)) {
                return Boolean.TRUE;
            }
            return null;
        }).keySet();
    }

    public static Method selectInvocableMethod(Method method, Class<?> targetType) {
        if (method.getDeclaringClass().isAssignableFrom(targetType)) {
            return method;
        }
        try {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> ifc : targetType.getInterfaces()) {
                try {
                    return ifc.getMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                }
            }
            return targetType.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e2) {
            throw new IllegalStateException(String.format("Need to invoke method '%s' declared on target class '%s', but not found in any interface(s) of the exposed proxy type. Either pull the method up to an interface or switch to CGLIB proxies by enforcing proxy-target-class mode in your configuration.", method.getName(), method.getDeclaringClass().getSimpleName()));
        }
    }
}
