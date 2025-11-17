package ch.qos.logback.core.util;

import java.util.concurrent.atomic.AtomicLong;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/SimpleInvocationGate.class */
public class SimpleInvocationGate implements InvocationGate {
    AtomicLong atomicNext;
    final Duration increment;
    public static final Duration DEFAULT_INCREMENT = Duration.buildBySeconds(60.0d);

    public SimpleInvocationGate() {
        this(DEFAULT_INCREMENT);
    }

    public SimpleInvocationGate(Duration anIncrement) {
        this.atomicNext = new AtomicLong(0L);
        this.increment = anIncrement;
    }

    @Override // ch.qos.logback.core.util.InvocationGate
    public boolean isTooSoon(long currentTime) {
        if (currentTime == -1) {
            return false;
        }
        long localNext = this.atomicNext.get();
        if (currentTime >= localNext) {
            long next2 = currentTime + this.increment.getMilliseconds();
            boolean success = this.atomicNext.compareAndSet(localNext, next2);
            return !success;
        }
        return true;
    }
}
