package ch.qos.logback.core.status;

import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/Status.class */
public interface Status {
    public static final int INFO = 0;
    public static final int WARN = 1;
    public static final int ERROR = 2;

    int getLevel();

    int getEffectiveLevel();

    Object getOrigin();

    String getMessage();

    Throwable getThrowable();

    long getTimestamp();

    boolean hasChildren();

    void add(Status status);

    boolean remove(Status status);

    Iterator<Status> iterator();

    @Deprecated
    default Long getDate() {
        return Long.valueOf(getTimestamp());
    }
}
