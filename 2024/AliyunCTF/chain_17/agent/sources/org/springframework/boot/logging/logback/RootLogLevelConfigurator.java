package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.spi.ContextAwareBase;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/logging/logback/RootLogLevelConfigurator.class */
public class RootLogLevelConfigurator extends ContextAwareBase implements Configurator {
    @Override // ch.qos.logback.classic.spi.Configurator
    public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
        loggerContext.getLogger("ROOT").setLevel(Level.INFO);
        return Configurator.ExecutionStatus.INVOKE_NEXT_IF_ANY;
    }
}
