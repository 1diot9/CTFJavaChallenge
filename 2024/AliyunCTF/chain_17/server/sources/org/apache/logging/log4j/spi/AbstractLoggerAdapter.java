package org.apache.logging.log4j.spi;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.util.LoaderUtil;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/spi/AbstractLoggerAdapter.class */
public abstract class AbstractLoggerAdapter<L> implements LoggerAdapter<L>, LoggerContextShutdownAware {
    protected final Map<LoggerContext, ConcurrentMap<String, L>> registry = new ConcurrentHashMap();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    protected abstract L newLogger(final String name, final LoggerContext context);

    protected abstract LoggerContext getContext();

    @Override // org.apache.logging.log4j.spi.LoggerAdapter
    public L getLogger(final String name) {
        LoggerContext context = getContext();
        ConcurrentMap<String, L> loggers = getLoggersInContext(context);
        L logger = loggers.get(name);
        if (logger != null) {
            return logger;
        }
        loggers.putIfAbsent(name, newLogger(name, context));
        return loggers.get(name);
    }

    @Override // org.apache.logging.log4j.spi.LoggerContextShutdownAware
    public void contextShutdown(final LoggerContext loggerContext) {
        this.registry.remove(loggerContext);
    }

    public ConcurrentMap<String, L> getLoggersInContext(final LoggerContext context) {
        this.lock.readLock().lock();
        try {
            ConcurrentMap<String, L> loggers = this.registry.get(context);
            if (loggers != null) {
                return loggers;
            }
            this.lock.writeLock().lock();
            try {
                ConcurrentMap<String, L> loggers2 = this.registry.get(context);
                if (loggers2 == null) {
                    loggers2 = new ConcurrentHashMap();
                    this.registry.put(context, loggers2);
                    if (context instanceof LoggerContextShutdownEnabled) {
                        ((LoggerContextShutdownEnabled) context).addShutdownListener(this);
                    }
                }
                return loggers2;
            } finally {
                this.lock.writeLock().unlock();
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Set<LoggerContext> getLoggerContexts() {
        return new HashSet(this.registry.keySet());
    }

    protected LoggerContext getContext(final Class<?> callerClass) {
        ClassLoader cl = null;
        if (callerClass != null) {
            cl = callerClass.getClassLoader();
        }
        if (cl == null) {
            cl = LoaderUtil.getThreadContextClassLoader();
        }
        return LogManager.getContext(cl, false);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.lock.writeLock().lock();
        try {
            this.registry.clear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
