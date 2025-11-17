package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/ExtendedThrowableProxyConverter.class */
public class ExtendedThrowableProxyConverter extends ThrowableProxyConverter {
    @Override // ch.qos.logback.classic.pattern.ThrowableProxyConverter
    protected void extraData(StringBuilder builder, StackTraceElementProxy step) {
        ThrowableProxyUtil.subjoinPackagingData(builder, step);
    }

    protected void prepareLoggingEvent(ILoggingEvent event) {
    }
}
