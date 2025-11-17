package org.springframework.jmx.export.notification;

import javax.management.Notification;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/notification/NotificationPublisher.class */
public interface NotificationPublisher {
    void sendNotification(Notification notification) throws UnableToSendNotificationException;
}
