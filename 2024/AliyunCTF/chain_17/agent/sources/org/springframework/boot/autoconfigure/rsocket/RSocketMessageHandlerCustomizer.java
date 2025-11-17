package org.springframework.boot.autoconfigure.rsocket;

import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/rsocket/RSocketMessageHandlerCustomizer.class */
public interface RSocketMessageHandlerCustomizer {
    void customize(RSocketMessageHandler messageHandler);
}
