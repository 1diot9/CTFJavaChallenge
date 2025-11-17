package org.apache.logging.log4j.message;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/LoggerNameAwareMessage.class */
public interface LoggerNameAwareMessage {
    void setLoggerName(String name);

    String getLoggerName();
}
