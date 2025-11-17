package org.slf4j.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.event.SubstituteLoggingEvent;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/SubstituteLoggerFactory.class */
public class SubstituteLoggerFactory implements ILoggerFactory {
    volatile boolean postInitialization = false;
    final Map<String, SubstituteLogger> loggers = new ConcurrentHashMap();
    final LinkedBlockingQueue<SubstituteLoggingEvent> eventQueue = new LinkedBlockingQueue<>();

    @Override // org.slf4j.ILoggerFactory
    public synchronized Logger getLogger(String name) {
        SubstituteLogger logger = this.loggers.get(name);
        if (logger == null) {
            logger = new SubstituteLogger(name, this.eventQueue, this.postInitialization);
            this.loggers.put(name, logger);
        }
        return logger;
    }

    public List<String> getLoggerNames() {
        return new ArrayList(this.loggers.keySet());
    }

    public List<SubstituteLogger> getLoggers() {
        return new ArrayList(this.loggers.values());
    }

    public LinkedBlockingQueue<SubstituteLoggingEvent> getEventQueue() {
        return this.eventQueue;
    }

    public void postInitialization() {
        this.postInitialization = true;
    }

    public void clear() {
        this.loggers.clear();
        this.eventQueue.clear();
    }
}
