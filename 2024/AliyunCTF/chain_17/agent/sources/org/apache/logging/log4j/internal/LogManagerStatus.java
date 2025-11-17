package org.apache.logging.log4j.internal;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/internal/LogManagerStatus.class */
public class LogManagerStatus {
    private static boolean initialized = false;

    public static void setInitialized(final boolean managerStatus) {
        initialized = managerStatus;
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
