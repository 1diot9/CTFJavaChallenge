package org.apache.logging.log4j.message;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/FlowMessageFactory.class */
public interface FlowMessageFactory {
    EntryMessage newEntryMessage(String message, Object... params);

    EntryMessage newEntryMessage(Message message);

    ExitMessage newExitMessage(String format, Object result);

    ExitMessage newExitMessage(Message message);

    ExitMessage newExitMessage(Object result, Message message);

    ExitMessage newExitMessage(EntryMessage message);

    ExitMessage newExitMessage(Object result, EntryMessage message);
}
