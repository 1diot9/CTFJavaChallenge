package org.springframework.web.context.request.async;

import java.util.concurrent.Callable;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/CallableProcessingInterceptor.class */
public interface CallableProcessingInterceptor {
    public static final Object RESULT_NONE = new Object();
    public static final Object RESPONSE_HANDLED = new Object();

    default <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
    }

    default <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
    }

    default <T> void postProcess(NativeWebRequest request, Callable<T> task, @Nullable Object concurrentResult) throws Exception {
    }

    default <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        return RESULT_NONE;
    }

    default <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
        return RESULT_NONE;
    }

    default <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
    }
}
