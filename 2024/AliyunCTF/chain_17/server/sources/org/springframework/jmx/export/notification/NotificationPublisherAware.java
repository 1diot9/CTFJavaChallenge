package org.springframework.jmx.export.notification;

import org.springframework.beans.factory.Aware;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/notification/NotificationPublisherAware.class */
public interface NotificationPublisherAware extends Aware {
    void setNotificationPublisher(NotificationPublisher notificationPublisher);
}
