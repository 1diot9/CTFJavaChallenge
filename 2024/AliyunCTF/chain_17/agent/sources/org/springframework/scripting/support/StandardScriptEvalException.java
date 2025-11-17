package org.springframework.scripting.support;

import javax.script.ScriptException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/support/StandardScriptEvalException.class */
public class StandardScriptEvalException extends RuntimeException {
    private final ScriptException scriptException;

    public StandardScriptEvalException(ScriptException ex) {
        super(ex.getMessage());
        this.scriptException = ex;
    }

    public final ScriptException getScriptException() {
        return this.scriptException;
    }

    @Override // java.lang.Throwable
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
