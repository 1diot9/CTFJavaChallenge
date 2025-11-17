package org.apache.logging.log4j.status;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.PropertiesUtil;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/status/StatusLogger.class */
public final class StatusLogger extends AbstractLogger {
    public static final String STATUS_DATE_FORMAT = "log4j2.StatusLogger.DateFormat";
    private static final long serialVersionUID = 2;
    private static final String NOT_AVAIL = "?";
    private final SimpleLogger logger;
    private final Collection<StatusListener> listeners;
    private final ReadWriteLock listenersLock;
    private final Queue<StatusData> messages;
    private final Lock msgLock;
    private int listenersLevel;
    static final PropertiesUtil PROPS = new PropertiesUtil("log4j2.StatusLogger.properties");
    public static final String MAX_STATUS_ENTRIES = "log4j2.status.entries";
    private static final int MAX_ENTRIES = PROPS.getIntegerProperty(MAX_STATUS_ENTRIES, 200);
    public static final String DEFAULT_STATUS_LISTENER_LEVEL = "log4j2.StatusLogger.level";
    private static final String DEFAULT_STATUS_LEVEL = PROPS.getStringProperty(DEFAULT_STATUS_LISTENER_LEVEL);
    static final boolean DEBUG_ENABLED = PropertiesUtil.getProperties().getBooleanProperty(Constants.LOG4J2_DEBUG, false, true);
    private static final StatusLogger STATUS_LOGGER = new StatusLogger(StatusLogger.class.getName(), ParameterizedNoReferenceMessageFactory.INSTANCE, SimpleLoggerFactory.getInstance());

    private StatusLogger(final String name, final MessageFactory messageFactory, final SimpleLoggerFactory loggerFactory) {
        super(name, messageFactory);
        this.listeners = new CopyOnWriteArrayList();
        this.listenersLock = new ReentrantReadWriteLock();
        this.messages = new BoundedQueue(MAX_ENTRIES);
        this.msgLock = new ReentrantLock();
        Level loggerLevel = DEBUG_ENABLED ? Level.TRACE : Level.ERROR;
        this.logger = loggerFactory.createSimpleLogger("StatusLogger", loggerLevel, messageFactory, System.err);
        this.listenersLevel = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
    }

    public static StatusLogger getLogger() {
        return STATUS_LOGGER;
    }

    public void setLevel(final Level level) {
        this.logger.setLevel(level);
    }

    public void registerListener(final StatusListener listener) {
        this.listenersLock.writeLock().lock();
        try {
            this.listeners.add(listener);
            Level lvl = listener.getStatusLevel();
            if (this.listenersLevel < lvl.intLevel()) {
                this.listenersLevel = lvl.intLevel();
            }
        } finally {
            this.listenersLock.writeLock().unlock();
        }
    }

    public void removeListener(final StatusListener listener) {
        closeSilently(listener);
        this.listenersLock.writeLock().lock();
        try {
            this.listeners.remove(listener);
            int lowest = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
            for (StatusListener statusListener : this.listeners) {
                int level = statusListener.getStatusLevel().intLevel();
                if (lowest < level) {
                    lowest = level;
                }
            }
            this.listenersLevel = lowest;
            this.listenersLock.writeLock().unlock();
        } catch (Throwable th) {
            this.listenersLock.writeLock().unlock();
            throw th;
        }
    }

    public void updateListenerLevel(final Level status) {
        if (status.intLevel() > this.listenersLevel) {
            this.listenersLevel = status.intLevel();
        }
    }

    public Iterable<StatusListener> getListeners() {
        return this.listeners;
    }

    public void reset() {
        this.listenersLock.writeLock().lock();
        try {
            for (StatusListener listener : this.listeners) {
                closeSilently(listener);
            }
        } finally {
            this.listeners.clear();
            this.listenersLock.writeLock().unlock();
            clear();
        }
    }

    private static void closeSilently(final Closeable resource) {
        try {
            resource.close();
        } catch (IOException e) {
        }
    }

    public List<StatusData> getStatusData() {
        this.msgLock.lock();
        try {
            return new ArrayList(this.messages);
        } finally {
            this.msgLock.unlock();
        }
    }

    public void clear() {
        this.msgLock.lock();
        try {
            this.messages.clear();
        } finally {
            this.msgLock.unlock();
        }
    }

    @Override // org.apache.logging.log4j.Logger
    public Level getLevel() {
        return this.logger.getLevel();
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public void logMessage(final String fqcn, final Level level, final Marker marker, final Message msg, final Throwable t) {
        StackTraceElement element = null;
        if (fqcn != null) {
            element = getStackTraceElement(fqcn, Thread.currentThread().getStackTrace());
        }
        StatusData data = new StatusData(element, level, msg, t, null);
        this.msgLock.lock();
        try {
            this.messages.add(data);
            this.msgLock.unlock();
            if (DEBUG_ENABLED || this.listeners.size() <= 0) {
                this.logger.logMessage(fqcn, level, marker, msg, t);
                return;
            }
            for (StatusListener listener : this.listeners) {
                if (data.getLevel().isMoreSpecificThan(listener.getStatusLevel())) {
                    listener.log(data);
                }
            }
        } catch (Throwable th) {
            this.msgLock.unlock();
            throw th;
        }
    }

    private StackTraceElement getStackTraceElement(final String fqcn, final StackTraceElement[] stackTrace) {
        if (fqcn == null) {
            return null;
        }
        boolean next = false;
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (next && !fqcn.equals(className)) {
                return element;
            }
            if (fqcn.equals(className)) {
                next = true;
            } else if ("?".equals(className)) {
                return null;
            }
        }
        return null;
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object... params) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final CharSequence message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final Object message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.ExtendedLogger
    public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable t) {
        return isEnabled(level, marker);
    }

    @Override // org.apache.logging.log4j.spi.AbstractLogger, org.apache.logging.log4j.Logger
    public boolean isEnabled(final Level level, final Marker marker) {
        if (DEBUG_ENABLED) {
            return true;
        }
        if (this.listeners.size() > 0) {
            return this.listenersLevel >= level.intLevel();
        }
        return this.logger.isEnabled(level, marker);
    }

    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/status/StatusLogger$BoundedQueue.class */
    private class BoundedQueue<E> extends ConcurrentLinkedQueue<E> {
        private static final long serialVersionUID = -3945953719763255337L;
        private final int size;

        BoundedQueue(final int size) {
            this.size = size;
        }

        @Override // java.util.concurrent.ConcurrentLinkedQueue, java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection, java.util.Queue
        public boolean add(final E object) {
            super.add(object);
            while (StatusLogger.this.messages.size() > this.size) {
                StatusLogger.this.messages.poll();
            }
            return this.size > 0;
        }
    }
}
