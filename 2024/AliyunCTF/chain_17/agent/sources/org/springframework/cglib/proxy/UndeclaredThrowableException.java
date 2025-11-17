package org.springframework.cglib.proxy;

import org.springframework.cglib.core.CodeGenerationException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/UndeclaredThrowableException.class */
public class UndeclaredThrowableException extends CodeGenerationException {
    public UndeclaredThrowableException(Throwable t) {
        super(t);
    }

    public Throwable getUndeclaredThrowable() {
        return getCause();
    }
}
