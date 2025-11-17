package ch.qos.logback.core.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/ConfigurationEvent.class */
public class ConfigurationEvent {
    final EventType eventType;
    final Object data;

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/ConfigurationEvent$EventType.class */
    public enum EventType {
        CHANGE_DETECTOR_REGISTERED,
        CHANGE_DETECTOR_RUNNING,
        CHANGE_DETECTED,
        CONFIGURATION_STARTED,
        CONFIGURATION_ENDED
    }

    private ConfigurationEvent(EventType eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
    }

    public static ConfigurationEvent newConfigurationChangeDetectorRunningEvent(Object data) {
        return new ConfigurationEvent(EventType.CHANGE_DETECTOR_RUNNING, data);
    }

    public static ConfigurationEvent newConfigurationChangeDetectorRegisteredEvent(Object data) {
        return new ConfigurationEvent(EventType.CHANGE_DETECTOR_REGISTERED, data);
    }

    public static ConfigurationEvent newConfigurationChangeDetectedEvent(Object data) {
        return new ConfigurationEvent(EventType.CHANGE_DETECTED, data);
    }

    public static ConfigurationEvent newConfigurationStartedEvent(Object data) {
        return new ConfigurationEvent(EventType.CONFIGURATION_STARTED, data);
    }

    public static ConfigurationEvent newConfigurationEndedEvent(Object data) {
        return new ConfigurationEvent(EventType.CONFIGURATION_ENDED, data);
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Object getData() {
        return this.data;
    }

    public String toString() {
        return "ConfigurationEvent{eventType=" + String.valueOf(this.eventType) + ", data=" + String.valueOf(this.data) + "}";
    }
}
