package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.server.ConcurrentServerRunner;
import ch.qos.logback.core.net.server.ServerListener;
import java.util.concurrent.Executor;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/server/RemoteAppenderServerRunner.class */
class RemoteAppenderServerRunner extends ConcurrentServerRunner<RemoteAppenderClient> {
    public RemoteAppenderServerRunner(ServerListener<RemoteAppenderClient> listener, Executor executor) {
        super(listener, executor);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.server.ConcurrentServerRunner
    public boolean configureClient(RemoteAppenderClient client) {
        client.setLoggerContext((LoggerContext) getContext());
        return true;
    }
}
