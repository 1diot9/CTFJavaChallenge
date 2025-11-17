package org.apache.tomcat.util.modeler;

import java.util.HashSet;
import java.util.Set;
import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/BaseAttributeFilter.class */
public class BaseAttributeFilter implements NotificationFilter {
    private static final long serialVersionUID = 1;
    private Set<String> names = new HashSet();

    public BaseAttributeFilter(String name) {
        if (name != null) {
            addAttribute(name);
        }
    }

    public void addAttribute(String name) {
        synchronized (this.names) {
            this.names.add(name);
        }
    }

    public void clear() {
        synchronized (this.names) {
            this.names.clear();
        }
    }

    public String[] getNames() {
        String[] strArr;
        synchronized (this.names) {
            strArr = (String[]) this.names.toArray(new String[0]);
        }
        return strArr;
    }

    public boolean isNotificationEnabled(Notification notification) {
        if (notification == null || !(notification instanceof AttributeChangeNotification)) {
            return false;
        }
        AttributeChangeNotification acn = (AttributeChangeNotification) notification;
        if (!"jmx.attribute.change".equals(acn.getType())) {
            return false;
        }
        synchronized (this.names) {
            if (this.names.size() < 1) {
                return true;
            }
            return this.names.contains(acn.getAttributeName());
        }
    }

    public void removeAttribute(String name) {
        synchronized (this.names) {
            this.names.remove(name);
        }
    }
}
