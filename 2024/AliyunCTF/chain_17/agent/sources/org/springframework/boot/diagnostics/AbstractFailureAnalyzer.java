package org.springframework.boot.diagnostics;

import java.lang.Throwable;
import org.springframework.core.ResolvableType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/AbstractFailureAnalyzer.class */
public abstract class AbstractFailureAnalyzer<T extends Throwable> implements FailureAnalyzer {
    protected abstract FailureAnalysis analyze(Throwable rootFailure, T cause);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.boot.diagnostics.FailureAnalyzer
    public FailureAnalysis analyze(Throwable failure) {
        Throwable findCause = findCause(failure, getCauseType());
        if (findCause != null) {
            return analyze(failure, findCause);
        }
        return null;
    }

    protected Class<? extends T> getCauseType() {
        return (Class<? extends T>) ResolvableType.forClass(AbstractFailureAnalyzer.class, getClass()).resolveGeneric(new int[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final <E extends Throwable> E findCause(Throwable th, Class<E> cls) {
        while (th != null) {
            if (cls.isInstance(th)) {
                return (E) th;
            }
            th = th.getCause();
        }
        return null;
    }
}
