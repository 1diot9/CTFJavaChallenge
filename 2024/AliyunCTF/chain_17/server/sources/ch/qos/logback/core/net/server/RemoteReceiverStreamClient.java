package ch.qos.logback.core.net.server;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.CloseUtil;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/RemoteReceiverStreamClient.class */
class RemoteReceiverStreamClient extends ContextAwareBase implements RemoteReceiverClient {
    private final String clientId;
    private final Socket socket;
    private final OutputStream outputStream;
    private BlockingQueue<Serializable> queue;

    public RemoteReceiverStreamClient(String id, Socket socket) {
        this.clientId = "client " + id + ": ";
        this.socket = socket;
        this.outputStream = null;
    }

    RemoteReceiverStreamClient(String id, OutputStream outputStream) {
        this.clientId = "client " + id + ": ";
        this.socket = null;
        this.outputStream = outputStream;
    }

    @Override // ch.qos.logback.core.net.server.RemoteReceiverClient
    public void setQueue(BlockingQueue<Serializable> queue) {
        this.queue = queue;
    }

    @Override // ch.qos.logback.core.net.server.RemoteReceiverClient
    public boolean offer(Serializable event) {
        if (this.queue == null) {
            throw new IllegalStateException("client has no event queue");
        }
        return this.queue.offer(event);
    }

    @Override // ch.qos.logback.core.net.server.Client, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.socket == null) {
            return;
        }
        CloseUtil.closeQuietly(this.socket);
    }

    @Override // java.lang.Runnable
    public void run() {
        addInfo(this.clientId + "connected");
        ObjectOutputStream oos = null;
        try {
            try {
                try {
                    try {
                        int counter = 0;
                        oos = createObjectOutputStream();
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                Serializable event = this.queue.take();
                                oos.writeObject(event);
                                oos.flush();
                                counter++;
                                if (counter >= 70) {
                                    counter = 0;
                                    oos.reset();
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (oos != null) {
                            CloseUtil.closeQuietly(oos);
                        }
                        close();
                        addInfo(this.clientId + "connection closed");
                    } catch (IOException ex) {
                        addError(this.clientId + String.valueOf(ex));
                        if (oos != null) {
                            CloseUtil.closeQuietly(oos);
                        }
                        close();
                        addInfo(this.clientId + "connection closed");
                    }
                } catch (RuntimeException ex2) {
                    addError(this.clientId + String.valueOf(ex2));
                    if (oos != null) {
                        CloseUtil.closeQuietly(oos);
                    }
                    close();
                    addInfo(this.clientId + "connection closed");
                }
            } catch (SocketException ex3) {
                addInfo(this.clientId + String.valueOf(ex3));
                if (oos != null) {
                    CloseUtil.closeQuietly(oos);
                }
                close();
                addInfo(this.clientId + "connection closed");
            }
        } catch (Throwable th) {
            if (oos != null) {
                CloseUtil.closeQuietly(oos);
            }
            close();
            addInfo(this.clientId + "connection closed");
            throw th;
        }
    }

    private ObjectOutputStream createObjectOutputStream() throws IOException {
        if (this.socket == null) {
            return new ObjectOutputStream(this.outputStream);
        }
        return new ObjectOutputStream(this.socket.getOutputStream());
    }
}
