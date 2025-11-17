package org.springframework.boot.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.logging.DeferredLog;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/DeferredLogs.class */
public class DeferredLogs implements DeferredLogFactory {
    private final DeferredLog.Lines lines = new DeferredLog.Lines();
    private final List<DeferredLog> loggers = new ArrayList();

    @Override // org.springframework.boot.logging.DeferredLogFactory
    public Log getLog(Class<?> destination) {
        return getLog(() -> {
            return LogFactory.getLog((Class<?>) destination);
        });
    }

    @Override // org.springframework.boot.logging.DeferredLogFactory
    public Log getLog(Log destination) {
        return getLog(() -> {
            return destination;
        });
    }

    @Override // org.springframework.boot.logging.DeferredLogFactory
    public Log getLog(Supplier<Log> destination) {
        DeferredLog logger;
        synchronized (this.lines) {
            logger = new DeferredLog(destination, this.lines);
            this.loggers.add(logger);
        }
        return logger;
    }

    public void switchOverAll() {
        synchronized (this.lines) {
            Iterator<DeferredLog.Line> it = this.lines.iterator();
            while (it.hasNext()) {
                DeferredLog.Line line = it.next();
                DeferredLog.logTo(line.getDestination(), line.getLevel(), line.getMessage(), line.getThrowable());
            }
            for (DeferredLog logger : this.loggers) {
                logger.switchOver();
            }
            this.lines.clear();
        }
    }
}
