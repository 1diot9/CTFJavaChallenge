package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/SimpleTraceInterceptor.class */
public class SimpleTraceInterceptor extends AbstractTraceInterceptor {
    public SimpleTraceInterceptor() {
    }

    public SimpleTraceInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override // org.springframework.aop.interceptor.AbstractTraceInterceptor
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String invocationDescription = getInvocationDescription(invocation);
        writeToLog(logger, "Entering " + invocationDescription);
        try {
            Object rval = invocation.proceed();
            writeToLog(logger, "Exiting " + invocationDescription);
            return rval;
        } catch (Throwable ex) {
            writeToLog(logger, "Exception thrown in " + invocationDescription, ex);
            throw ex;
        }
    }

    protected String getInvocationDescription(MethodInvocation invocation) {
        Object target = invocation.getThis();
        Assert.state(target != null, "Target must not be null");
        String className = target.getClass().getName();
        return "method '" + invocation.getMethod().getName() + "' of class [" + className + "]";
    }
}
