package org.apache.logging.log4j.spi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/Provider.class */
public class Provider {
    public static final String FACTORY_PRIORITY = "FactoryPriority";
    public static final String THREAD_CONTEXT_MAP = "ThreadContextMap";
    public static final String LOGGER_CONTEXT_FACTORY = "LoggerContextFactory";
    private static final Integer DEFAULT_PRIORITY = -1;
    private static final Logger LOGGER = StatusLogger.getLogger();
    private final Integer priority;
    private final String className;
    private final Class<? extends LoggerContextFactory> loggerContextFactoryClass;
    private final String threadContextMap;
    private final Class<? extends ThreadContextMap> threadContextMapClass;
    private final String versions;
    private final URL url;
    private final WeakReference<ClassLoader> classLoader;

    public Provider(final Properties props, final URL url, final ClassLoader classLoader) {
        this.url = url;
        this.classLoader = new WeakReference<>(classLoader);
        String weight = props.getProperty(FACTORY_PRIORITY);
        this.priority = weight == null ? DEFAULT_PRIORITY : Integer.valueOf(weight);
        this.className = props.getProperty(LOGGER_CONTEXT_FACTORY);
        this.threadContextMap = props.getProperty(THREAD_CONTEXT_MAP);
        this.loggerContextFactoryClass = null;
        this.threadContextMapClass = null;
        this.versions = null;
    }

    public Provider(final Integer priority, final String versions, final Class<? extends LoggerContextFactory> loggerContextFactoryClass) {
        this(priority, versions, loggerContextFactoryClass, null);
    }

    public Provider(final Integer priority, final String versions, final Class<? extends LoggerContextFactory> loggerContextFactoryClass, final Class<? extends ThreadContextMap> threadContextMapClass) {
        this.url = null;
        this.classLoader = null;
        this.priority = priority;
        this.loggerContextFactoryClass = loggerContextFactoryClass;
        this.threadContextMapClass = threadContextMapClass;
        this.className = null;
        this.threadContextMap = null;
        this.versions = versions;
    }

    public String getVersions() {
        return this.versions;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public String getClassName() {
        if (this.loggerContextFactoryClass != null) {
            return this.loggerContextFactoryClass.getName();
        }
        return this.className;
    }

    public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
        ClassLoader loader;
        if (this.loggerContextFactoryClass != null) {
            return this.loggerContextFactoryClass;
        }
        if (this.className == null || (loader = this.classLoader.get()) == null) {
            return null;
        }
        try {
            Class<?> clazz = loader.loadClass(this.className);
            if (LoggerContextFactory.class.isAssignableFrom(clazz)) {
                return clazz.asSubclass(LoggerContextFactory.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Unable to create class {} specified in {}", this.className, this.url.toString(), e);
            return null;
        }
    }

    public String getThreadContextMap() {
        if (this.threadContextMapClass != null) {
            return this.threadContextMapClass.getName();
        }
        return this.threadContextMap;
    }

    public Class<? extends ThreadContextMap> loadThreadContextMap() {
        ClassLoader loader;
        if (this.threadContextMapClass != null) {
            return this.threadContextMapClass;
        }
        if (this.threadContextMap == null || (loader = this.classLoader.get()) == null) {
            return null;
        }
        try {
            Class<?> clazz = loader.loadClass(this.threadContextMap);
            if (ThreadContextMap.class.isAssignableFrom(clazz)) {
                return clazz.asSubclass(ThreadContextMap.class);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Unable to create class {} specified in {}", this.threadContextMap, this.url.toString(), e);
            return null;
        }
    }

    public URL getUrl() {
        return this.url;
    }

    public String toString() {
        ClassLoader loader;
        StringBuilder result = new StringBuilder("Provider[");
        if (!DEFAULT_PRIORITY.equals(this.priority)) {
            result.append("priority=").append(this.priority).append(", ");
        }
        if (this.threadContextMap != null) {
            result.append("threadContextMap=").append(this.threadContextMap).append(", ");
        } else if (this.threadContextMapClass != null) {
            result.append("threadContextMapClass=").append(this.threadContextMapClass.getName());
        }
        if (this.className != null) {
            result.append("className=").append(this.className).append(", ");
        } else if (this.loggerContextFactoryClass != null) {
            result.append("class=").append(this.loggerContextFactoryClass.getName());
        }
        if (this.url != null) {
            result.append("url=").append(this.url);
        }
        if (this.classLoader == null || (loader = this.classLoader.get()) == null) {
            result.append(", classLoader=null(not reachable)");
        } else {
            result.append(", classLoader=").append(loader);
        }
        result.append("]");
        return result.toString();
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Provider provider = (Provider) o;
        if (this.priority != null) {
            if (!this.priority.equals(provider.priority)) {
                return false;
            }
        } else if (provider.priority != null) {
            return false;
        }
        if (this.className != null) {
            if (!this.className.equals(provider.className)) {
                return false;
            }
        } else if (provider.className != null) {
            return false;
        }
        if (this.loggerContextFactoryClass != null) {
            if (!this.loggerContextFactoryClass.equals(provider.loggerContextFactoryClass)) {
                return false;
            }
        } else if (provider.loggerContextFactoryClass != null) {
            return false;
        }
        return this.versions != null ? this.versions.equals(provider.versions) : provider.versions == null;
    }

    public int hashCode() {
        int result = this.priority != null ? this.priority.hashCode() : 0;
        return (31 * ((31 * ((31 * result) + (this.className != null ? this.className.hashCode() : 0))) + (this.loggerContextFactoryClass != null ? this.loggerContextFactoryClass.hashCode() : 0))) + (this.versions != null ? this.versions.hashCode() : 0);
    }
}
