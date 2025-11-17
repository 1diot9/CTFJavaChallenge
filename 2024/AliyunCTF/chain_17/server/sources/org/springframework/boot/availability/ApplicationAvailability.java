package org.springframework.boot.availability;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/availability/ApplicationAvailability.class */
public interface ApplicationAvailability {
    <S extends AvailabilityState> S getState(Class<S> stateType, S defaultState);

    <S extends AvailabilityState> S getState(Class<S> stateType);

    <S extends AvailabilityState> AvailabilityChangeEvent<S> getLastChangeEvent(Class<S> stateType);

    default LivenessState getLivenessState() {
        return (LivenessState) getState(LivenessState.class, LivenessState.BROKEN);
    }

    default ReadinessState getReadinessState() {
        return (ReadinessState) getState(ReadinessState.class, ReadinessState.REFUSING_TRAFFIC);
    }
}
