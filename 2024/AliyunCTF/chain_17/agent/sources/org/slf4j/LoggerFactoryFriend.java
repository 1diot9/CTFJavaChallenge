package org.slf4j;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/LoggerFactoryFriend.class */
public class LoggerFactoryFriend {
    public static void reset() {
        LoggerFactory.reset();
    }

    public static void setDetectLoggerNameMismatch(boolean enabled) {
        LoggerFactory.DETECT_LOGGER_NAME_MISMATCH = enabled;
    }
}
