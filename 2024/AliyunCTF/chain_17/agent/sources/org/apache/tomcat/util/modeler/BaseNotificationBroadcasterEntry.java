package org.apache.tomcat.util.modeler;

import javax.management.NotificationFilter;
import javax.management.NotificationListener;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: BaseNotificationBroadcaster.java */
/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/BaseNotificationBroadcasterEntry.class */
public class BaseNotificationBroadcasterEntry {
    public NotificationFilter filter;
    public Object handback;
    public NotificationListener listener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseNotificationBroadcasterEntry(NotificationListener listener, NotificationFilter filter, Object handback) {
        this.filter = null;
        this.handback = null;
        this.listener = null;
        this.listener = listener;
        this.filter = filter;
        this.handback = handback;
    }
}
