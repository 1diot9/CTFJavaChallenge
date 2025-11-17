package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ContextAware;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/Configurator.class */
public interface Configurator extends ContextAware {

    /* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/Configurator$ExecutionStatus.class */
    public enum ExecutionStatus {
        NEUTRAL,
        INVOKE_NEXT_IF_ANY,
        DO_NOT_INVOKE_NEXT_IF_ANY
    }

    ExecutionStatus configure(LoggerContext loggerContext);
}
