package org.springframework.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/SimpleIdGenerator.class */
public class SimpleIdGenerator implements IdGenerator {
    private final AtomicLong leastSigBits = new AtomicLong();

    @Override // org.springframework.util.IdGenerator
    public UUID generateId() {
        return new UUID(0L, this.leastSigBits.incrementAndGet());
    }
}
