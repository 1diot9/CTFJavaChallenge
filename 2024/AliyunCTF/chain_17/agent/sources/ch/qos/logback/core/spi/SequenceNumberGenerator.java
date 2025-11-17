package ch.qos.logback.core.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/SequenceNumberGenerator.class */
public interface SequenceNumberGenerator extends ContextAware {
    long nextSequenceNumber();
}
