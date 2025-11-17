package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/boolex/IEvaluator.class */
public interface IEvaluator {
    boolean doEvaluate(ILoggingEvent iLoggingEvent);
}
