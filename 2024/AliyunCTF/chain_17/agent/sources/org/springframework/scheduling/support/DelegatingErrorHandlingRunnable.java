package org.springframework.scheduling.support;

import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/support/DelegatingErrorHandlingRunnable.class */
public class DelegatingErrorHandlingRunnable implements Runnable {
    private final Runnable delegate;
    private final ErrorHandler errorHandler;

    public DelegatingErrorHandlingRunnable(Runnable delegate, ErrorHandler errorHandler) {
        Assert.notNull(delegate, "Delegate must not be null");
        Assert.notNull(errorHandler, "ErrorHandler must not be null");
        this.delegate = delegate;
        this.errorHandler = errorHandler;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            this.delegate.run();
        } catch (UndeclaredThrowableException ex) {
            this.errorHandler.handleError(ex.getUndeclaredThrowable());
        } catch (Throwable ex2) {
            this.errorHandler.handleError(ex2);
        }
    }

    public String toString() {
        return "DelegatingErrorHandlingRunnable for " + this.delegate;
    }
}
