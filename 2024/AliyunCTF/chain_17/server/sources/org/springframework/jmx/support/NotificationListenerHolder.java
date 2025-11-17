package org.springframework.jmx.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/support/NotificationListenerHolder.class */
public class NotificationListenerHolder {

    @Nullable
    private NotificationListener notificationListener;

    @Nullable
    private NotificationFilter notificationFilter;

    @Nullable
    private Object handback;

    @Nullable
    protected Set<Object> mappedObjectNames;

    public void setNotificationListener(@Nullable NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    @Nullable
    public NotificationListener getNotificationListener() {
        return this.notificationListener;
    }

    public void setNotificationFilter(@Nullable NotificationFilter notificationFilter) {
        this.notificationFilter = notificationFilter;
    }

    @Nullable
    public NotificationFilter getNotificationFilter() {
        return this.notificationFilter;
    }

    public void setHandback(@Nullable Object handback) {
        this.handback = handback;
    }

    @Nullable
    public Object getHandback() {
        return this.handback;
    }

    public void setMappedObjectName(@Nullable Object mappedObjectName) {
        this.mappedObjectNames = mappedObjectName != null ? new LinkedHashSet(Collections.singleton(mappedObjectName)) : null;
    }

    public void setMappedObjectNames(Object... mappedObjectNames) {
        this.mappedObjectNames = new LinkedHashSet(Arrays.asList(mappedObjectNames));
    }

    @Nullable
    public ObjectName[] getResolvedObjectNames() throws MalformedObjectNameException {
        if (this.mappedObjectNames == null) {
            return null;
        }
        ObjectName[] resolved = new ObjectName[this.mappedObjectNames.size()];
        int i = 0;
        for (Object objectName : this.mappedObjectNames) {
            resolved[i] = ObjectNameManager.getInstance(objectName);
            i++;
        }
        return resolved;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof NotificationListenerHolder) {
                NotificationListenerHolder that = (NotificationListenerHolder) other;
                if (!ObjectUtils.nullSafeEquals(this.notificationListener, that.notificationListener) || !ObjectUtils.nullSafeEquals(this.notificationFilter, that.notificationFilter) || !ObjectUtils.nullSafeEquals(this.handback, that.handback) || !ObjectUtils.nullSafeEquals(this.mappedObjectNames, that.mappedObjectNames)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.notificationListener, this.notificationFilter, this.handback, this.mappedObjectNames);
    }
}
