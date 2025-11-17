package org.springframework.web.context.request.async;

import java.util.function.Consumer;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/context/request/async/AsyncWebRequest.class */
public interface AsyncWebRequest extends NativeWebRequest {
    void setTimeout(@Nullable Long timeout);

    void addTimeoutHandler(Runnable runnable);

    void addErrorHandler(Consumer<Throwable> exceptionHandler);

    void addCompletionHandler(Runnable runnable);

    void startAsync();

    boolean isAsyncStarted();

    void dispatch();

    boolean isAsyncComplete();
}
