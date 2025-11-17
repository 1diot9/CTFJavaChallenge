package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/LoggerContextAwareBase.class */
public class LoggerContextAwareBase extends ContextAwareBase implements LoggerContextAware {
    @Override // ch.qos.logback.classic.spi.LoggerContextAware
    public void setLoggerContext(LoggerContext context) {
        super.setContext(context);
    }

    @Override // ch.qos.logback.core.spi.ContextAwareBase, ch.qos.logback.core.spi.ContextAware
    public void setContext(Context context) {
        if ((context instanceof LoggerContext) || context == null) {
            super.setContext(context);
            return;
        }
        throw new IllegalArgumentException("LoggerContextAwareBase only accepts contexts of type c.l.classic.LoggerContext");
    }

    public LoggerContext getLoggerContext() {
        return (LoggerContext) this.context;
    }
}
