package org.apache.logging.log4j.message;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/MultiformatMessage.class */
public interface MultiformatMessage extends Message {
    String getFormattedMessage(String[] formats);

    String[] getFormats();
}
