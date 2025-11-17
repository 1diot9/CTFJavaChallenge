package org.springframework.scripting.support;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/support/StandardScriptUtils.class */
public abstract class StandardScriptUtils {
    public static ScriptEngine retrieveEngineByName(ScriptEngineManager scriptEngineManager, String engineName) {
        ScriptEngine engine = scriptEngineManager.getEngineByName(engineName);
        if (engine == null) {
            Set<String> engineNames = new LinkedHashSet<>();
            for (ScriptEngineFactory engineFactory : scriptEngineManager.getEngineFactories()) {
                List<String> factoryNames = engineFactory.getNames();
                if (factoryNames.contains(engineName)) {
                    try {
                        engineFactory.getScriptEngine().setBindings(scriptEngineManager.getBindings(), 200);
                    } catch (Throwable ex) {
                        throw new IllegalStateException("Script engine with name '" + engineName + "' failed to initialize", ex);
                    }
                }
                engineNames.addAll(factoryNames);
            }
            throw new IllegalArgumentException("Script engine with name '" + engineName + "' not found; registered engine names: " + engineNames);
        }
        return engine;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Bindings getBindings(Map<String, Object> bindings) {
        if (!(bindings instanceof Bindings)) {
            return new SimpleBindings(bindings);
        }
        Bindings b = (Bindings) bindings;
        return b;
    }
}
