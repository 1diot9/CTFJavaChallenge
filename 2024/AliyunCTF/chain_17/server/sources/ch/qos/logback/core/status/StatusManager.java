package ch.qos.logback.core.status;

import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/StatusManager.class */
public interface StatusManager {
    void add(Status status);

    List<Status> getCopyOfStatusList();

    int getCount();

    boolean add(StatusListener statusListener);

    void remove(StatusListener statusListener);

    void clear();

    List<StatusListener> getCopyOfStatusListenerList();
}
