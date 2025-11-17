package ch.qos.logback.core.testUtil;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/TrivialStatusListener.class */
public class TrivialStatusListener implements StatusListener, LifeCycle {
    public List<Status> list = new ArrayList();
    boolean start = false;

    @Override // ch.qos.logback.core.status.StatusListener
    public void addStatusEvent(Status status) {
        if (!isStarted()) {
            return;
        }
        this.list.add(status);
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.start = true;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        this.start = false;
    }

    @Override // ch.qos.logback.core.spi.LifeCycle
    public boolean isStarted() {
        return this.start;
    }
}
