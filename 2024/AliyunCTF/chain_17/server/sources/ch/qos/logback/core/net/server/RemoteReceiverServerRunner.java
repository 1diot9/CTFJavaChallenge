package ch.qos.logback.core.net.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/RemoteReceiverServerRunner.class */
public class RemoteReceiverServerRunner extends ConcurrentServerRunner<RemoteReceiverClient> {
    private final int clientQueueSize;

    public RemoteReceiverServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor, int clientQueueSize) {
        super(listener, executor);
        this.clientQueueSize = clientQueueSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.server.ConcurrentServerRunner
    public boolean configureClient(RemoteReceiverClient client) {
        client.setContext(getContext());
        client.setQueue(new ArrayBlockingQueue(this.clientQueueSize));
        return true;
    }
}
