package ch.qos.logback.core.net;

import java.util.concurrent.LinkedBlockingDeque;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/QueueFactory.class */
public class QueueFactory {
    public <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity) {
        int actualCapacity = capacity < 1 ? 1 : capacity;
        return new LinkedBlockingDeque<>(actualCapacity);
    }
}
