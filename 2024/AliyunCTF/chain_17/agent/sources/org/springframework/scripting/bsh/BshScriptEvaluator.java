package org.springframework.scripting.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/bsh/BshScriptEvaluator.class */
public class BshScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware {

    @Nullable
    private ClassLoader classLoader;

    public BshScriptEvaluator() {
    }

    public BshScriptEvaluator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override // org.springframework.scripting.ScriptEvaluator
    @Nullable
    public Object evaluate(ScriptSource script) {
        return evaluate(script, null);
    }

    @Override // org.springframework.scripting.ScriptEvaluator
    @Nullable
    public Object evaluate(ScriptSource script, @Nullable Map<String, Object> arguments) {
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.setClassLoader(this.classLoader);
            if (arguments != null) {
                for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                    interpreter.set(entry.getKey(), entry.getValue());
                }
            }
            return interpreter.eval(new StringReader(script.getScriptAsString()));
        } catch (EvalError ex) {
            throw new ScriptCompilationException(script, (Throwable) ex);
        } catch (IOException ex2) {
            throw new ScriptCompilationException(script, "Cannot access BeanShell script", ex2);
        }
    }
}
