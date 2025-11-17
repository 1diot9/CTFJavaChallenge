package org.springframework.context.weaving;

import java.lang.instrument.ClassFileTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;
import org.springframework.instrument.classloading.glassfish.GlassFishLoadTimeWeaver;
import org.springframework.instrument.classloading.jboss.JBossLoadTimeWeaver;
import org.springframework.instrument.classloading.tomcat.TomcatLoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/weaving/DefaultContextLoadTimeWeaver.class */
public class DefaultContextLoadTimeWeaver implements LoadTimeWeaver, BeanClassLoaderAware, DisposableBean {
    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    public DefaultContextLoadTimeWeaver() {
    }

    public DefaultContextLoadTimeWeaver(ClassLoader beanClassLoader) {
        setBeanClassLoader(beanClassLoader);
    }

    @Override // org.springframework.beans.factory.BeanClassLoaderAware
    public void setBeanClassLoader(ClassLoader classLoader) {
        LoadTimeWeaver serverSpecificLoadTimeWeaver = createServerSpecificLoadTimeWeaver(classLoader);
        if (serverSpecificLoadTimeWeaver != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Determined server-specific load-time weaver: " + serverSpecificLoadTimeWeaver.getClass().getName());
            }
            this.loadTimeWeaver = serverSpecificLoadTimeWeaver;
        } else {
            if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
                this.logger.debug("Found Spring's JVM agent for instrumentation");
                this.loadTimeWeaver = new InstrumentationLoadTimeWeaver(classLoader);
                return;
            }
            try {
                this.loadTimeWeaver = new ReflectiveLoadTimeWeaver(classLoader);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Using reflective load-time weaver for class loader: " + this.loadTimeWeaver.getInstrumentableClassLoader().getClass().getName());
                }
            } catch (IllegalStateException ex) {
                throw new IllegalStateException(ex.getMessage() + " Specify a custom LoadTimeWeaver or start your Java virtual machine with Spring's agent: -javaagent:spring-instrument-{version}.jar");
            }
        }
    }

    @Nullable
    protected LoadTimeWeaver createServerSpecificLoadTimeWeaver(ClassLoader classLoader) {
        String name = classLoader.getClass().getName();
        try {
            if (name.startsWith("org.apache.catalina")) {
                return new TomcatLoadTimeWeaver(classLoader);
            }
            if (name.startsWith("org.glassfish")) {
                return new GlassFishLoadTimeWeaver(classLoader);
            }
            if (name.startsWith("org.jboss.modules")) {
                return new JBossLoadTimeWeaver(classLoader);
            }
            return null;
        } catch (Exception ex) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Could not obtain server-specific LoadTimeWeaver: " + ex.getMessage());
                return null;
            }
            return null;
        }
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        LoadTimeWeaver loadTimeWeaver = this.loadTimeWeaver;
        if (loadTimeWeaver instanceof InstrumentationLoadTimeWeaver) {
            InstrumentationLoadTimeWeaver iltw = (InstrumentationLoadTimeWeaver) loadTimeWeaver;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Removing all registered transformers for class loader: " + this.loadTimeWeaver.getInstrumentableClassLoader().getClass().getName());
            }
            iltw.removeTransformers();
        }
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public void addTransformer(ClassFileTransformer transformer) {
        Assert.state(this.loadTimeWeaver != null, "Not initialized");
        this.loadTimeWeaver.addTransformer(transformer);
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getInstrumentableClassLoader() {
        Assert.state(this.loadTimeWeaver != null, "Not initialized");
        return this.loadTimeWeaver.getInstrumentableClassLoader();
    }

    @Override // org.springframework.instrument.classloading.LoadTimeWeaver
    public ClassLoader getThrowawayClassLoader() {
        Assert.state(this.loadTimeWeaver != null, "Not initialized");
        return this.loadTimeWeaver.getThrowawayClassLoader();
    }
}
