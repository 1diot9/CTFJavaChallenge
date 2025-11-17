package ch.qos.logback.core.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/RemoteReceiverServerListener.class */
class RemoteReceiverServerListener extends ServerSocketListener<RemoteReceiverClient> {
    public RemoteReceiverServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // ch.qos.logback.core.net.server.ServerSocketListener
    public RemoteReceiverClient createClient(String id, Socket socket) throws IOException {
        return new RemoteReceiverStreamClient(id, socket);
    }
}
