package ch.qos.logback.core.status;

import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/StatusListenerAsList.class */
public class StatusListenerAsList implements StatusListener {
    List<Status> statusList = new ArrayList();

    @Override // ch.qos.logback.core.status.StatusListener
    public void addStatusEvent(Status status) {
        this.statusList.add(status);
    }

    public List<Status> getStatusList() {
        return this.statusList;
    }
}
