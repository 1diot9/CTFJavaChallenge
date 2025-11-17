package org.apache.tomcat.util.modeler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.MBeanNotificationInfo;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/modeler/NotificationInfo.class */
public class NotificationInfo extends FeatureInfo {
    private static final long serialVersionUID = -6319885418912650856L;
    transient MBeanNotificationInfo info = null;
    protected String[] notifTypes = new String[0];
    protected final ReadWriteLock notifTypesLock = new ReentrantReadWriteLock();

    @Override // org.apache.tomcat.util.modeler.FeatureInfo
    public void setDescription(String description) {
        super.setDescription(description);
        this.info = null;
    }

    @Override // org.apache.tomcat.util.modeler.FeatureInfo
    public void setName(String name) {
        super.setName(name);
        this.info = null;
    }

    public String[] getNotifTypes() {
        Lock readLock = this.notifTypesLock.readLock();
        readLock.lock();
        try {
            return this.notifTypes;
        } finally {
            readLock.unlock();
        }
    }

    public void addNotifType(String notifType) {
        Lock writeLock = this.notifTypesLock.writeLock();
        writeLock.lock();
        try {
            String[] results = new String[this.notifTypes.length + 1];
            System.arraycopy(this.notifTypes, 0, results, 0, this.notifTypes.length);
            results[this.notifTypes.length] = notifType;
            this.notifTypes = results;
            this.info = null;
            writeLock.unlock();
        } catch (Throwable th) {
            writeLock.unlock();
            throw th;
        }
    }

    public MBeanNotificationInfo createNotificationInfo() {
        if (this.info != null) {
            return this.info;
        }
        this.info = new MBeanNotificationInfo(getNotifTypes(), getName(), getDescription());
        return this.info;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NotificationInfo[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", description=");
        sb.append(this.description);
        sb.append(", notifTypes=");
        Lock readLock = this.notifTypesLock.readLock();
        readLock.lock();
        try {
            sb.append(this.notifTypes.length);
            sb.append(']');
            return sb.toString();
        } finally {
            readLock.unlock();
        }
    }
}
