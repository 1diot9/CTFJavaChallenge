package ch.qos.logback.core.status;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/StatusBase.class */
public abstract class StatusBase implements Status {
    private static final List<Status> EMPTY_LIST = new ArrayList(0);
    int level;
    final String message;
    final Object origin;
    List<Status> childrenList;
    Throwable throwable;
    long timestamp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StatusBase(int level, String msg, Object origin) {
        this(level, msg, origin, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StatusBase(int level, String msg, Object origin, Throwable t) {
        this.level = level;
        this.message = msg;
        this.origin = origin;
        this.throwable = t;
        this.timestamp = System.currentTimeMillis();
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized void add(Status child) {
        if (child == null) {
            throw new NullPointerException("Null values are not valid Status.");
        }
        if (this.childrenList == null) {
            this.childrenList = new ArrayList();
        }
        this.childrenList.add(child);
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized boolean hasChildren() {
        return this.childrenList != null && this.childrenList.size() > 0;
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized Iterator<Status> iterator() {
        if (this.childrenList != null) {
            return this.childrenList.iterator();
        }
        return EMPTY_LIST.iterator();
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized boolean remove(Status statusToRemove) {
        if (this.childrenList == null) {
            return false;
        }
        return this.childrenList.remove(statusToRemove);
    }

    @Override // ch.qos.logback.core.status.Status
    public int getLevel() {
        return this.level;
    }

    @Override // ch.qos.logback.core.status.Status
    public synchronized int getEffectiveLevel() {
        int result = this.level;
        Iterator<Status> it = iterator();
        while (it.hasNext()) {
            Status s = it.next();
            int effLevel = s.getEffectiveLevel();
            if (effLevel > result) {
                result = effLevel;
            }
        }
        return result;
    }

    @Override // ch.qos.logback.core.status.Status
    public String getMessage() {
        return this.message;
    }

    @Override // ch.qos.logback.core.status.Status
    public Object getOrigin() {
        return this.origin;
    }

    @Override // ch.qos.logback.core.status.Status
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override // ch.qos.logback.core.status.Status
    public long getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        switch (getEffectiveLevel()) {
            case 0:
                buf.append("INFO");
                break;
            case 1:
                buf.append("WARN");
                break;
            case 2:
                buf.append("ERROR");
                break;
        }
        if (this.origin != null) {
            buf.append(" in ");
            buf.append(this.origin);
            buf.append(" -");
        }
        buf.append(CharSequenceUtil.SPACE);
        buf.append(this.message);
        if (this.throwable != null) {
            buf.append(CharSequenceUtil.SPACE);
            buf.append(this.throwable);
        }
        return buf.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusBase that = (StatusBase) o;
        return this.level == that.level && this.timestamp == that.timestamp && Objects.equals(this.message, that.message);
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.level), this.message, Long.valueOf(this.timestamp));
    }
}
