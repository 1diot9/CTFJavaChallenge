package org.apache.logging.log4j.message;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ThreadInformation.class */
public interface ThreadInformation {
    void printThreadInfo(StringBuilder sb);

    void printStack(StringBuilder sb, StackTraceElement[] trace);
}
