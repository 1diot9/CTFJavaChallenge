package ch.qos.logback.core.net;

import java.net.Socket;
import java.util.concurrent.Callable;
import javax.net.SocketFactory;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/SocketConnector.class */
public interface SocketConnector extends Callable<Socket> {

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/SocketConnector$ExceptionHandler.class */
    public interface ExceptionHandler {
        void connectionFailed(SocketConnector socketConnector, Exception exc);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.concurrent.Callable
    Socket call() throws InterruptedException;

    void setExceptionHandler(ExceptionHandler exceptionHandler);

    void setSocketFactory(SocketFactory socketFactory);
}
