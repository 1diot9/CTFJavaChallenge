package org.springframework.boot.availability;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/availability/ReadinessState.class */
public enum ReadinessState implements AvailabilityState {
    ACCEPTING_TRAFFIC,
    REFUSING_TRAFFIC
}
