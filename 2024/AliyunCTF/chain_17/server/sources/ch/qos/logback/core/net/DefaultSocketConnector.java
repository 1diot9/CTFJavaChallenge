package ch.qos.logback.core.net;

import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.util.DelayStrategy;
import ch.qos.logback.core.util.FixedDelay;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/DefaultSocketConnector.class */
public class DefaultSocketConnector implements SocketConnector {
    private final InetAddress address;
    private final int port;
    private final DelayStrategy delayStrategy;
    private SocketConnector.ExceptionHandler exceptionHandler;
    private SocketFactory socketFactory;

    public DefaultSocketConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
        this(address, port, new FixedDelay(initialDelay, retryDelay));
    }

    public DefaultSocketConnector(InetAddress address, int port, DelayStrategy delayStrategy) {
        this.address = address;
        this.port = port;
        this.delayStrategy = delayStrategy;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // ch.qos.logback.core.net.SocketConnector, java.util.concurrent.Callable
    public Socket call() throws InterruptedException {
        Socket socket;
        useDefaultsForMissingFields();
        Socket createSocket = createSocket();
        while (true) {
            socket = createSocket;
            if (socket != null || Thread.currentThread().isInterrupted()) {
                break;
            }
            Thread.sleep(this.delayStrategy.nextDelay());
            createSocket = createSocket();
        }
        return socket;
    }

    private Socket createSocket() {
        Socket newSocket = null;
        try {
            newSocket = this.socketFactory.createSocket(this.address, this.port);
        } catch (IOException ioex) {
            this.exceptionHandler.connectionFailed(this, ioex);
        }
        return newSocket;
    }

    private void useDefaultsForMissingFields() {
        if (this.exceptionHandler == null) {
            this.exceptionHandler = new ConsoleExceptionHandler();
        }
        if (this.socketFactory == null) {
            this.socketFactory = SocketFactory.getDefault();
        }
    }

    @Override // ch.qos.logback.core.net.SocketConnector
    public void setExceptionHandler(SocketConnector.ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override // ch.qos.logback.core.net.SocketConnector
    public void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/DefaultSocketConnector$ConsoleExceptionHandler.class */
    public static class ConsoleExceptionHandler implements SocketConnector.ExceptionHandler {
        private ConsoleExceptionHandler() {
        }

        @Override // ch.qos.logback.core.net.SocketConnector.ExceptionHandler
        public void connectionFailed(SocketConnector connector, Exception ex) {
            System.out.println(ex);
        }
    }
}
