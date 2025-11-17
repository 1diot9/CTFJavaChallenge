package org.springframework.scripting.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.scripting.ScriptCompilationException;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/groovy/GroovyScriptFactory.class */
public class GroovyScriptFactory implements ScriptFactory, BeanFactoryAware, BeanClassLoaderAware {
    private final String scriptSourceLocator;

    @Nullable
    private GroovyObjectCustomizer groovyObjectCustomizer;

    @Nullable
    private CompilerConfiguration compilerConfiguration;

    @Nullable
    private GroovyClassLoader groovyClassLoader;

    @Nullable
    private Class<?> scriptClass;

    @Nullable
    private Class<?> scriptResultClass;

    @Nullable
    private CachedResultHolder cachedResult;
    private final Object scriptClassMonitor;
    private boolean wasModifiedForTypeCheck;

    public GroovyScriptFactory(String scriptSourceLocator) {
        this.scriptClassMonitor = new Object();
        this.wasModifiedForTypeCheck = false;
        Assert.hasText(scriptSourceLocator, "'scriptSourceLocator' must not be empty");
        this.scriptSourceLocator = scriptSourceLocator;
    }

    public GroovyScriptFactory(String scriptSourceLocator, @Nullable GroovyObjectCustomizer groovyObjectCustomizer) {
        this(scriptSourceLocator);
        this.groovyObjectCustomizer = groovyObjectCustomizer;
    }

    public GroovyScriptFactory(String scriptSourceLocator, @Nullable CompilerConfiguration compilerConfiguration) {
        this(scriptSourceLocator);
        this.compilerConfiguration = compilerConfiguration;
    }

    public GroovyScriptFactory(String scriptSourceLocator, CompilationCustomizer... compilationCustomizers) {
        this(scriptSourceLocator);
        if (!ObjectUtils.isEmpty((Object[]) compilationCustomizers)) {
            this.compilerConfiguration = new CompilerConfiguration();
            this.compilerConfiguration.addCompilationCustomizers(compilationCustomizers);
        }
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory clbf = (ConfigurableListableBeanFactory) beanFactory;
            clbf.ignoreDependencyType(MetaClass.class);
        }
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        if (classLoader instanceof GroovyClassLoader) {
            GroovyClassLoader gcl = (GroovyClassLoader) classLoader;
            if (this.compilerConfiguration == null || gcl.hasCompatibleConfiguration(this.compilerConfiguration)) {
                this.groovyClassLoader = gcl;
                return;
            }
        }
        this.groovyClassLoader = buildGroovyClassLoader(classLoader);
    }

    public GroovyClassLoader getGroovyClassLoader() {
        GroovyClassLoader groovyClassLoader;
        synchronized (this.scriptClassMonitor) {
            if (this.groovyClassLoader == null) {
                this.groovyClassLoader = buildGroovyClassLoader(ClassUtils.getDefaultClassLoader());
            }
            groovyClassLoader = this.groovyClassLoader;
        }
        return groovyClassLoader;
    }

    protected GroovyClassLoader buildGroovyClassLoader(@Nullable ClassLoader classLoader) {
        return this.compilerConfiguration != null ? new GroovyClassLoader(classLoader, this.compilerConfiguration) : new GroovyClassLoader(classLoader);
    }

    @Override // org.springframework.scripting.ScriptFactory
    public String getScriptSourceLocator() {
        return this.scriptSourceLocator;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Class<?>[] getScriptInterfaces() {
        return null;
    }

    @Override // org.springframework.scripting.ScriptFactory
    public boolean requiresConfigInterface() {
        return false;
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Object getScriptedObject(ScriptSource scriptSource, @Nullable Class<?>... actualInterfaces) throws IOException, ScriptCompilationException {
        synchronized (this.scriptClassMonitor) {
            try {
                this.wasModifiedForTypeCheck = false;
                if (this.cachedResult != null) {
                    Object result = this.cachedResult.object;
                    this.cachedResult = null;
                    return result;
                }
                if (this.scriptClass == null || scriptSource.isModified()) {
                    this.scriptClass = getGroovyClassLoader().parseClass(scriptSource.getScriptAsString(), scriptSource.suggestedClassName());
                    if (Script.class.isAssignableFrom(this.scriptClass)) {
                        Object result2 = executeScript(scriptSource, this.scriptClass);
                        this.scriptResultClass = result2 != null ? result2.getClass() : null;
                        return result2;
                    }
                    this.scriptResultClass = this.scriptClass;
                }
                Class<?> scriptClassToExecute = this.scriptClass;
                return executeScript(scriptSource, scriptClassToExecute);
            } catch (CompilationFailedException ex) {
                this.scriptClass = null;
                this.scriptResultClass = null;
                throw new ScriptCompilationException(scriptSource, (Throwable) ex);
            }
        }
    }

    @Override // org.springframework.scripting.ScriptFactory
    @Nullable
    public Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException {
        Class<?> cls;
        synchronized (this.scriptClassMonitor) {
            try {
                if (this.scriptClass == null || scriptSource.isModified()) {
                    this.wasModifiedForTypeCheck = true;
                    this.scriptClass = getGroovyClassLoader().parseClass(scriptSource.getScriptAsString(), scriptSource.suggestedClassName());
                    if (Script.class.isAssignableFrom(this.scriptClass)) {
                        Object result = executeScript(scriptSource, this.scriptClass);
                        this.scriptResultClass = result != null ? result.getClass() : null;
                        this.cachedResult = new CachedResultHolder(result);
                    } else {
                        this.scriptResultClass = this.scriptClass;
                    }
                }
                cls = this.scriptResultClass;
            } catch (CompilationFailedException ex) {
                this.scriptClass = null;
                this.scriptResultClass = null;
                this.cachedResult = null;
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

    @Nullable
    protected Object executeScript(ScriptSource scriptSource, Class<?> scriptClass) throws ScriptCompilationException {
        try {
            GroovyObject groovyObj = (GroovyObject) ReflectionUtils.accessibleConstructor(scriptClass, new Class[0]).newInstance(new Object[0]);
            if (this.groovyObjectCustomizer != null) {
                this.groovyObjectCustomizer.customize(groovyObj);
            }
            if (groovyObj instanceof Script) {
                Script script = (Script) groovyObj;
                return script.run();
            }
            return groovyObj;
        } catch (IllegalAccessException ex) {
            throw new ScriptCompilationException(scriptSource, "Could not access Groovy script constructor: " + scriptClass.getName(), ex);
        } catch (InstantiationException ex2) {
            throw new ScriptCompilationException(scriptSource, "Unable to instantiate Groovy script class: " + scriptClass.getName(), ex2);
        } catch (NoSuchMethodException ex3) {
            throw new ScriptCompilationException("No default constructor on Groovy script class: " + scriptClass.getName(), ex3);
        } catch (InvocationTargetException ex4) {
            throw new ScriptCompilationException("Failed to invoke Groovy script constructor: " + scriptClass.getName(), ex4.getTargetException());
        }
    }

    public String toString() {
        return "GroovyScriptFactory: script source locator [" + this.scriptSourceLocator + "]";
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/groovy/GroovyScriptFactory$CachedResultHolder.class */
    private static class CachedResultHolder {

        @Nullable
        public final Object object;

        public CachedResultHolder(@Nullable Object object) {
            this.object = object;
        }
    }
}
