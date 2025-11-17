package org.apache.tomcat.websocket.server;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.websocket.BackgroundProcess;
import org.apache.tomcat.websocket.BackgroundProcessManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/WsWriteTimeout.class */
public class WsWriteTimeout implements BackgroundProcess {
    private final Set<WsRemoteEndpointImplServer> endpoints = new ConcurrentSkipListSet(Comparator.comparingLong((v0) -> {
        return v0.getTimeoutExpiry();
    }));
    private final AtomicInteger count = new AtomicInteger(0);
    private int backgroundProcessCount = 0;
    private volatile int processPeriod = 1;

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void backgroundProcess() {
        this.backgroundProcessCount++;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            long now = System.currentTimeMillis();
            for (WsRemoteEndpointImplServer endpoint : this.endpoints) {
                if (endpoint.getTimeoutExpiry() < now) {
                    endpoint.onTimeout(false);
                } else {
                    return;
                }
            }
        }
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public void setProcessPeriod(int period) {
        this.processPeriod = period;
    }

    @Override // org.apache.tomcat.websocket.BackgroundProcess
    public int getProcessPeriod() {
        return this.processPeriod;
    }

    public void register(WsRemoteEndpointImplServer endpoint) {
        boolean result = this.endpoints.add(endpoint);
        if (result) {
            int newCount = this.count.incrementAndGet();
            if (newCount == 1) {
                BackgroundProcessManager.getInstance().register(this);
            }
        }
    }

    public void unregister(WsRemoteEndpointImplServer endpoint) {
        boolean result = this.endpoints.remove(endpoint);
        if (result) {
            int newCount = this.count.decrementAndGet();
            if (newCount == 0) {
                BackgroundProcessManager.getInstance().unregister(this);
            }
        }
    }
}
