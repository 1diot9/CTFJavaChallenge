package org.springframework.jmx.export.notification;

import org.springframework.jmx.JmxException;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/notification/UnableToSendNotificationException.class */
public class UnableToSendNotificationException extends JmxException {
    public UnableToSendNotificationException(String msg) {
        super(msg);
    }

    public UnableToSendNotificationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
