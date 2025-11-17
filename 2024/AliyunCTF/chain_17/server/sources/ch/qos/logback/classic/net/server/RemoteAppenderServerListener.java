package ch.qos.logback.classic.net.server;

import ch.qos.logback.core.net.server.ServerSocketListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/server/RemoteAppenderServerListener.class */
class RemoteAppenderServerListener extends ServerSocketListener<RemoteAppenderClient> {
    public RemoteAppenderServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // ch.qos.logback.core.net.server.ServerSocketListener
    public RemoteAppenderClient createClient(String id, Socket socket) throws IOException {
        return new RemoteAppenderStreamClient(id, socket);
    }
}
