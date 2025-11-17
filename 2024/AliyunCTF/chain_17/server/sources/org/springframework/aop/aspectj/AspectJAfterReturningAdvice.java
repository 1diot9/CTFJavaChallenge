package org.springframework.aop.aspectj;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.springframework.aop.AfterAdvice;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.TypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/AspectJAfterReturningAdvice.class */
public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice implements AfterReturningAdvice, AfterAdvice, Serializable {
    public AspectJAfterReturningAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
        super(aspectJBeforeAdviceMethod, pointcut, aif);
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override // org.springframework.aop.aspectj.AspectJPrecedenceInformation
    public boolean isAfterAdvice() {
        return true;
    }

    @Override // org.springframework.aop.aspectj.AbstractAspectJAdvice
    public void setReturningName(String name) {
        setReturningNameNoCheck(name);
    }

    @Override // org.springframework.aop.AfterReturningAdvice
    public void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable {
        if (shouldInvokeOnReturnValueOf(method, returnValue)) {
            invokeAdviceMethod(getJoinPointMatch(), returnValue, null);
        }
    }

    private boolean shouldInvokeOnReturnValueOf(Method method, @Nullable Object returnValue) {
        Class<?> type = getDiscoveredReturningType();
        Type genericType = getDiscoveredReturningGenericType();
        return matchesReturnValue(type, method, returnValue) && (genericType == null || genericType == type || TypeUtils.isAssignable(genericType, method.getGenericReturnType()));
    }

    private boolean matchesReturnValue(Class<?> type, Method method, @Nullable Object returnValue) {
        if (returnValue != null) {
            return ClassUtils.isAssignableValue(type, returnValue);
        }
        if (Object.class == type && Void.TYPE == method.getReturnType()) {
            return true;
        }
        return ClassUtils.isAssignable(type, method.getReturnType());
    }
}
