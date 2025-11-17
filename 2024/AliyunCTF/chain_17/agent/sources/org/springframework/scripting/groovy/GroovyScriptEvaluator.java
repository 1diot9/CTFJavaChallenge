package org.springframework.scripting.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovyShell;
import java.io.IOException;
import java.util.Map;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/groovy/GroovyScriptEvaluator.class */
public class GroovyScriptEvaluator implements ScriptEvaluator, BeanClassLoaderAware {

    @Nullable
    private ClassLoader classLoader;
    private CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

    public GroovyScriptEvaluator() {
    }

    public GroovyScriptEvaluator(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setCompilerConfiguration(@Nullable CompilerConfiguration compilerConfiguration) {
        this.compilerConfiguration = compilerConfiguration != null ? compilerConfiguration : new CompilerConfiguration();
    }

    public CompilerConfiguration getCompilerConfiguration() {
        return this.compilerConfiguration;
    }

    public void setCompilationCustomizers(CompilationCustomizer... compilationCustomizers) {
        this.compilerConfiguration.addCompilationCustomizers(compilationCustomizers);
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
        String str;
        GroovyShell groovyShell = new GroovyShell(this.classLoader, new Binding(arguments), this.compilerConfiguration);
        try {
            if (script instanceof ResourceScriptSource) {
                ResourceScriptSource resourceScriptSource = (ResourceScriptSource) script;
                str = resourceScriptSource.getResource().getFilename();
            } else {
                str = null;
            }
            String filename = str;
            if (filename != null) {
                return groovyShell.evaluate(script.getScriptAsString(), filename);
            }
            return groovyShell.evaluate(script.getScriptAsString());
        } catch (IOException ex) {
            throw new ScriptCompilationException(script, "Cannot access Groovy script", ex);
        } catch (GroovyRuntimeException ex2) {
            throw new ScriptCompilationException(script, (Throwable) ex2);
        }
    }
}
