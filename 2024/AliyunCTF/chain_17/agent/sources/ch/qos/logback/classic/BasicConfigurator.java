package ch.qos.logback.classic;

import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

@ConfiguratorRank(ConfiguratorRank.FALLBACK)
/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/BasicConfigurator.class */
public class BasicConfigurator extends ContextAwareBase implements Configurator {
    @Override // ch.qos.logback.classic.spi.Configurator
    public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
        addInfo("Setting up default configuration.");
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(this.context);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(this.context);
        TTLLLayout layout = new TTLLLayout();
        layout.setContext(this.context);
        layout.start();
        encoder.setLayout(layout);
        ca.setEncoder(encoder);
        ca.start();
        Logger rootLogger = loggerContext.getLogger("ROOT");
        rootLogger.addAppender(ca);
        return Configurator.ExecutionStatus.NEUTRAL;
    }
}
