package ch.qos.logback.core.spi;

import java.util.concurrent.atomic.AtomicLong;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/BasicSequenceNumberGenerator.class */
public class BasicSequenceNumberGenerator extends ContextAwareBase implements SequenceNumberGenerator {
    private final AtomicLong atomicLong = new AtomicLong();

    @Override // ch.qos.logback.core.spi.SequenceNumberGenerator
    public long nextSequenceNumber() {
        return this.atomicLong.incrementAndGet();
    }
}
