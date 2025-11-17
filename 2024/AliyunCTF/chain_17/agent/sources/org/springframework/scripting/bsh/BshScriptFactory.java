package org.springframework.scripting.bsh;

import bsh.EvalError;
import java.io.IOException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/bsh/BshScriptFactory.class */
public class BshScriptFactory implements ScriptFactory, BeanClassLoaderAware {
    private final String scriptSourceLocator;

    @Nullable
    private final Class<?>[] scriptInterfaces;

    @Nullable
    private Class<?> scriptClass;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final Object scriptClassMonitor = new Object();
    private boolean wasModifiedForTypeCheck = false;

    public BshScriptFactory(String scriptSourceLocator) {
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = null;
    }

    public BshScriptFactory(String scriptSourceLocator, @Nullable Class<?>... scriptInterfaces) {
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
        this.scriptInterfaces = scriptInterfaces;
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Class<?>[] getScriptInterfaces() {
        return this.scriptInterfaces;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public boolean requiresConfigInterface() {
        return true;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Object getScriptedObject(ScriptSource scriptSource, @Nullable Class<?>... actualInterfaces) throws IOException, ScriptCompilationException {
        try {
            synchronized (this.scriptClassMonitor) {
                boolean requiresScriptEvaluation = this.wasModifiedForTypeCheck && this.scriptClass == null;
                this.wasModifiedForTypeCheck = false;
                if (scriptSource.isModified() || requiresScriptEvaluation) {
                    Object result = BshScriptUtils.evaluateBshScript(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
                    if (result instanceof Class) {
                        Class<?> type = (Class) result;
                        this.scriptClass = type;
                    } else {
                        return result;
                    }
                }
                Class<?> clazz = this.scriptClass;
                if (clazz != null) {
                    try {
                        return ReflectionUtils.accessibleConstructor(clazz, new Class[0]).newInstance(new Object[0]);
                    } catch (Throwable ex) {
                        throw new ScriptCompilationException(scriptSource, "Could not instantiate script class: " + clazz.getName(), ex);
                    }
                }
                try {
                    return BshScriptUtils.createBshObject(scriptSource.getScriptAsString(), actualInterfaces, this.beanClassLoader);
                } catch (EvalError ex2) {
                    throw new ScriptCompilationException(scriptSource, (Throwable) ex2);
                }
            }
        } catch (EvalError ex3) {
            this.scriptClass = null;
            throw new ScriptCompilationException(scriptSource, (Throwable) ex3);
        }
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        Class<?> cls;
        synchronized (this.scriptClassMonitor) {
            try {
                if (scriptSource.isModified()) {
                    this.wasModifiedForTypeCheck = true;
                    this.scriptClass = BshScriptUtils.determineBshObjectType(scriptSource.getScriptAsString(), this.beanClassLoader);
                }
                cls = this.scriptClass;
            } catch (EvalError ex) {
                this.scriptClass = null;
                throw new ScriptCompilationException(scriptSource, (Throwable) ex);
            }
        }
        return cls;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public boolean requiresScriptedObjectRefresh(ScriptSource scriptSource) {
        boolean z;
        synchronized (this.scriptClassMonitor) {
            z = scriptSource.isModified() || this.wasModifiedForTypeCheck;
        }
        return z;
    }

    public String toString() {
        return "BshScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }
}
