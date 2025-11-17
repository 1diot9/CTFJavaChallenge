package org.springframework.boot.rsocket.messaging;

import org.springframework.messaging.rsocket.RSocketStrategies;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/rsocket/messaging/RSocketStrategiesCustomizer.class */
public interface RSocketStrategiesCustomizer {
    void customize(RSocketStrategies.Builder strategies);
}
