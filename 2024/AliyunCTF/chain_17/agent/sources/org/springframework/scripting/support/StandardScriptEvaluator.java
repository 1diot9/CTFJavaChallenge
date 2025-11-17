package org.springframework.scripting.support;

import java.io.IOException;
import java.util.Map;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/support/StandardScriptEvaluator.class */
public class StandardScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware {

    @Nullable
    private String engineName;

    @Nullable
    private volatile Bindings globalBindings;

    @Nullable
    private volatile ScriptEngineManager scriptEngineManager;

    public StandardScriptEvaluator() {
    }

    public StandardScriptEvaluator(ClassLoader classLoader) {
        this.scriptEngineManager = new ScriptEngineManager(classLoader);
    }

    public StandardScriptEvaluator(ScriptEngineManager scriptEngineManager) {
        this.scriptEngineManager = scriptEngineManager;
    }

    public void setLanguage(String language) {
        this.engineName = language;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public void setGlobalBindings(Map<String, Object> globalBindings) {
        Bindings bindings = StandardScriptUtils.getBindings(globalBindings);
        this.globalBindings = bindings;
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager != null) {
            scriptEngineManager.setBindings(bindings);
        }
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (this.scriptEngineManager == null) {
            ScriptEngineManager scriptEngineManager = new ScriptEngineManager(classLoader);
            this.scriptEngineManager = scriptEngineManager;
            Bindings bindings = this.globalBindings;
            if (bindings != null) {
                scriptEngineManager.setBindings(bindings);
            }
        }
    }

    @Override // org.springframework.scripting.ScriptEvaluator
    @Nullable
    public Object evaluate(ScriptSource script) {
        return evaluate(script, null);
    }

    @Override // org.springframework.scripting.ScriptEvaluator
    @Nullable
    public Object evaluate(ScriptSource script, @Nullable Map<String, Object> argumentBindings) {
        ScriptEngine engine = getScriptEngine(script);
        try {
            if (CollectionUtils.isEmpty(argumentBindings)) {
                return engine.eval(script.getScriptAsString());
            }
            Bindings bindings = StandardScriptUtils.getBindings(argumentBindings);
            return engine.eval(script.getScriptAsString(), bindings);
        } catch (ScriptException ex) {
            throw new ScriptCompilationException(script, new StandardScriptEvalException(ex));
        } catch (IOException ex2) {
            throw new ScriptCompilationException(script, "Cannot access script for ScriptEngine", ex2);
        }
    }

    protected ScriptEngine getScriptEngine(ScriptSource script) {
        ScriptEngineManager scriptEngineManager = this.scriptEngineManager;
        if (scriptEngineManager == null) {
            scriptEngineManager = new ScriptEngineManager();
            this.scriptEngineManager = scriptEngineManager;
        }
        if (StringUtils.hasText(this.engineName)) {
            return StandardScriptUtils.retrieveEngineByName(scriptEngineManager, this.engineName);
        }
        if (script instanceof ResourceScriptSource) {
            ResourceScriptSource resourceScriptSource = (ResourceScriptSource) script;
            Resource resource = resourceScriptSource.getResource();
            String extension = StringUtils.getFilenameExtension(resource.getFilename());
            if (extension == null) {
                throw new IllegalStateException("No script language defined, and no file extension defined for resource: " + resource);
            }
            ScriptEngine engine = scriptEngineManager.getEngineByExtension(extension);
            if (engine == null) {
                throw new IllegalStateException("No matching engine found for file extension '" + extension + "'");
            }
            return engine;
        }
        throw new IllegalStateException("No script language defined, and no resource associated with script: " + script);
    }
}
