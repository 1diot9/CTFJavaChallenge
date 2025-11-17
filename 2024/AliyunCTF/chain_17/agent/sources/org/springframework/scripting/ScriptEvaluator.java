package org.springframework.scripting;

import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/ScriptEvaluator.class */
public interface ScriptEvaluator {
    @Nullable
    Object evaluate(ScriptSource script) throws ScriptCompilationException;

    @Nullable
    Object evaluate(ScriptSource script, @Nullable Map<String, Object> arguments) throws ScriptCompilationException;
}
