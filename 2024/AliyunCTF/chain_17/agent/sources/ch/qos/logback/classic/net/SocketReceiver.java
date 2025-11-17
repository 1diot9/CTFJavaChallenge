package ch.qos.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.server.HardenedLoggingEventInputStream;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.DefaultSocketConnector;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.util.CloseUtil;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import javax.net.SocketFactory;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/SocketReceiver.class */
public class SocketReceiver extends ReceiverBase implements Runnable, SocketConnector.ExceptionHandler {
    private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
    private String remoteHost;
    private InetAddress address;
    private int port;
    private int reconnectionDelay;
    private int acceptConnectionTimeout = 5000;
    private String receiverId;
    private volatile Socket socket;
    private Future<Socket> connectorTask;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.net.ReceiverBase
    public boolean shouldStart() {
        int errorCount = 0;
        if (this.port == 0) {
            errorCount = 0 + 1;
            addError("No port was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_port");
        }
        if (this.remoteHost == null) {
            errorCount++;
            addError("No host name or address was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_host");
        }
        if (this.reconnectionDelay == 0) {
            this.reconnectionDelay = 30000;
        }
        if (errorCount == 0) {
            try {
                this.address = InetAddress.getByName(this.remoteHost);
            } catch (UnknownHostException e) {
                addError("unknown host: " + this.remoteHost);
                errorCount++;
            }
        }
        if (errorCount == 0) {
            this.receiverId = "receiver " + this.remoteHost + ":" + this.port + ": ";
        }
        return errorCount == 0;
    }

    @Override // ch.qos.logback.classic.net.ReceiverBase
    protected void onStop() {
        if (this.socket != null) {
            CloseUtil.closeQuietly(this.socket);
        }
    }

    @Override // ch.qos.logback.classic.net.ReceiverBase
    protected Runnable getRunnableTask() {
        return this;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            LoggerContext lc = (LoggerContext) getContext();
            while (!Thread.currentThread().isInterrupted()) {
                SocketConnector connector = createConnector(this.address, this.port, 0, this.reconnectionDelay);
                this.connectorTask = activateConnector(connector);
                if (this.connectorTask == null) {
                    break;
                }
                this.socket = waitForConnectorToReturnASocket();
                if (this.socket == null) {
                    break;
                } else {
                    dispatchEvents(lc);
                }
            }
        } catch (InterruptedException e) {
        }
        addInfo("shutting down");
    }

    private SocketConnector createConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
        SocketConnector connector = newConnector(address, port, initialDelay, retryDelay);
        connector.setExceptionHandler(this);
        connector.setSocketFactory(getSocketFactory());
        return connector;
    }

    private Future<Socket> activateConnector(SocketConnector connector) {
        try {
            return getContext().getScheduledExecutorService().submit(connector);
        } catch (RejectedExecutionException e) {
            return null;
        }
    }

    private Socket waitForConnectorToReturnASocket() throws InterruptedException {
        try {
            Socket s = this.connectorTask.get();
            this.connectorTask = null;
            return s;
        } catch (ExecutionException e) {
            return null;
        }
    }

    private void dispatchEvents(LoggerContext lc) {
        ObjectInputStream ois = null;
        try {
            try {
                try {
                    this.socket.setSoTimeout(this.acceptConnectionTimeout);
                    ois = new HardenedLoggingEventInputStream(this.socket.getInputStream());
                    this.socket.setSoTimeout(0);
                    addInfo(this.receiverId + "connection established");
                    while (true) {
                        ILoggingEvent event = (ILoggingEvent) ois.readObject();
                        Logger remoteLogger = lc.getLogger(event.getLoggerName());
                        if (remoteLogger.isEnabledFor(event.getLevel())) {
                            remoteLogger.callAppenders(event);
                        }
                    }
                } catch (EOFException e) {
                    addInfo(this.receiverId + "end-of-stream detected");
                    CloseUtil.closeQuietly(ois);
                    CloseUtil.closeQuietly(this.socket);
                    this.socket = null;
                    addInfo(this.receiverId + "connection closed");
                } catch (IOException ex) {
                    addInfo(this.receiverId + "connection failed: " + String.valueOf(ex));
                    CloseUtil.closeQuietly(ois);
                    CloseUtil.closeQuietly(this.socket);
                    this.socket = null;
                    addInfo(this.receiverId + "connection closed");
                }
            } catch (ClassNotFoundException ex2) {
                addInfo(this.receiverId + "unknown event class: " + String.valueOf(ex2));
                CloseUtil.closeQuietly(ois);
                CloseUtil.closeQuietly(this.socket);
                this.socket = null;
                addInfo(this.receiverId + "connection closed");
            }
        } catch (Throwable th) {
            CloseUtil.closeQuietly(ois);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            addInfo(this.receiverId + "connection closed");
            throw th;
        }
    }

    @Override // ch.qos.logback.core.net.SocketConnector.ExceptionHandler
    public void connectionFailed(SocketConnector connector, Exception ex) {
        if (ex instanceof InterruptedException) {
            addInfo("connector interrupted");
        } else if (ex instanceof ConnectException) {
            addInfo(this.receiverId + "connection refused");
        } else {
            addInfo(this.receiverId + String.valueOf(ex));
        }
    }

    protected SocketConnector newConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
        return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
    }

    protected SocketFactory getSocketFactory() {
        return SocketFactory.getDefault();
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setReconnectionDelay(int reconnectionDelay) {
        this.reconnectionDelay = reconnectionDelay;
    }

    public void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
        this.acceptConnectionTimeout = acceptConnectionTimeout;
    }
}
