package org.springframework.aop.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.util.StopWatch;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/interceptor/PerformanceMonitorInterceptor.class */
public class PerformanceMonitorInterceptor extends AbstractMonitoringInterceptor {
    public PerformanceMonitorInterceptor() {
    }

    public PerformanceMonitorInterceptor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override // org.springframework.aop.interceptor.AbstractTraceInterceptor
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        StopWatch stopWatch = new StopWatch(name);
        stopWatch.start(name);
        try {
            Object proceed = invocation.proceed();
            stopWatch.stop();
            writeToLog(logger, stopWatch.shortSummary());
            return proceed;
        } catch (Throwable th) {
            stopWatch.stop();
            writeToLog(logger, stopWatch.shortSummary());
            throw th;
        }
    }
}
