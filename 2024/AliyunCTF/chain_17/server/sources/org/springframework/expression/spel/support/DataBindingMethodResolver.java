package org.springframework.expression.spel.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.MethodExecutor;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/DataBindingMethodResolver.class */
public final class DataBindingMethodResolver extends ReflectiveMethodResolver {
    private DataBindingMethodResolver() {
    }

    @Override // org.springframework.expression.spel.support.ReflectiveMethodResolver, org.springframework.expression.MethodResolver
    @Nullable
    public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name, List<TypeDescriptor> argumentTypes) throws AccessException {
        if (targetObject instanceof Class) {
            throw new IllegalArgumentException("DataBindingMethodResolver does not support Class targets");
        }
        return super.resolve(context, targetObject, name, argumentTypes);
    }

    @Override // org.springframework.expression.spel.support.ReflectiveMethodResolver
    protected boolean isCandidateForInvocation(Method method, Class<?> targetClass) {
        Class<?> clazz;
        return (Modifier.isStatic(method.getModifiers()) || (clazz = method.getDeclaringClass()) == Object.class || clazz == Class.class || ClassLoader.class.isAssignableFrom(targetClass)) ? false : true;
    }

    public static DataBindingMethodResolver forInstanceMethodInvocation() {
        return new DataBindingMethodResolver();
    }
}
