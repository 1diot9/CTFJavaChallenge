package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import ch.qos.logback.core.util.CloseUtil;
import ch.qos.logback.core.util.Duration;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;
import javax.net.ssl.SSLHandshakeException;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/AbstractSocketAppender.class */
public abstract class AbstractSocketAppender<E> extends AppenderBase<E> implements SocketConnector.ExceptionHandler {
    public static final int DEFAULT_PORT = 4560;
    public static final int DEFAULT_RECONNECTION_DELAY = 30000;
    public static final int DEFAULT_QUEUE_SIZE = 128;
    private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
    private static final int DEFAULT_EVENT_DELAY_TIMEOUT = 100;
    private final ObjectWriterFactory objectWriterFactory;
    private final QueueFactory queueFactory;
    private String remoteHost;
    private int port;
    private InetAddress address;
    private Duration reconnectionDelay;
    private int queueSize;
    private int acceptConnectionTimeout;
    private Duration eventDelayLimit;
    private BlockingDeque<E> deque;
    private String peerId;
    private SocketConnector connector;
    private Future<?> task;
    private volatile Socket socket;

    protected abstract void postProcessEvent(E e);

    protected abstract PreSerializationTransformer<E> getPST();

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractSocketAppender() {
        this(new QueueFactory(), new ObjectWriterFactory());
    }

    AbstractSocketAppender(QueueFactory queueFactory, ObjectWriterFactory objectWriterFactory) {
        this.port = DEFAULT_PORT;
        this.reconnectionDelay = new Duration(ExponentialBackOff.DEFAULT_MAX_INTERVAL);
        this.queueSize = 128;
        this.acceptConnectionTimeout = 5000;
        this.eventDelayLimit = new Duration(100L);
        this.objectWriterFactory = objectWriterFactory;
        this.queueFactory = queueFactory;
    }

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (isStarted()) {
            return;
        }
        int errorCount = 0;
        if (this.port <= 0) {
            errorCount = 0 + 1;
            addError("No port was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
        }
        if (this.remoteHost == null) {
            errorCount++;
            addError("No remote host was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
        }
        if (this.queueSize == 0) {
            addWarn("Queue size of zero is deprecated, use a size of one to indicate synchronous processing");
        }
        if (this.queueSize < 0) {
            errorCount++;
            addError("Queue size must be greater than zero");
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
            this.deque = this.queueFactory.newLinkedBlockingDeque(this.queueSize);
            this.peerId = "remote peer " + this.remoteHost + ":" + this.port + ": ";
            this.connector = createConnector(this.address, this.port, 0, this.reconnectionDelay.getMilliseconds());
            this.task = getContext().getExecutorService().submit(new Runnable() { // from class: ch.qos.logback.core.net.AbstractSocketAppender.1
                @Override // java.lang.Runnable
                public void run() {
                    AbstractSocketAppender.this.connectSocketAndDispatchEvents();
                }
            });
            super.start();
        }
    }

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!isStarted()) {
            return;
        }
        CloseUtil.closeQuietly(this.socket);
        this.task.cancel(true);
        super.stop();
    }

    @Override // ch.qos.logback.core.AppenderBase
    protected void append(E event) {
        if (event == null || !isStarted()) {
            return;
        }
        try {
            boolean inserted = this.deque.offer(event, this.eventDelayLimit.getMilliseconds(), TimeUnit.MILLISECONDS);
            if (!inserted) {
                addInfo("Dropping event due to timeout limit of [" + String.valueOf(this.eventDelayLimit) + "] being exceeded");
            }
        } catch (InterruptedException e) {
            addError("Interrupted while appending event to SocketAppender", e);
        }
    }

    private void connectSocketAndDispatchEvents() {
        while (socketConnectionCouldBeEstablished()) {
            try {
                try {
                    try {
                        ObjectWriter objectWriter = createObjectWriterForSocket();
                        addInfo(this.peerId + "connection established");
                        dispatchEvents(objectWriter);
                        CloseUtil.closeQuietly(this.socket);
                        this.socket = null;
                        addInfo(this.peerId + "connection closed");
                    } catch (SSLHandshakeException e) {
                        Thread.sleep(ExponentialBackOff.DEFAULT_MAX_INTERVAL);
                        CloseUtil.closeQuietly(this.socket);
                        this.socket = null;
                        addInfo(this.peerId + "connection closed");
                    } catch (IOException ex) {
                        addInfo(this.peerId + "connection failed: ", ex);
                        CloseUtil.closeQuietly(this.socket);
                        this.socket = null;
                        addInfo(this.peerId + "connection closed");
                    }
                } finally {
                }
            } catch (InterruptedException e2) {
            }
        }
        addInfo("shutting down");
    }

    private boolean socketConnectionCouldBeEstablished() throws InterruptedException {
        Socket call = this.connector.call();
        this.socket = call;
        return call != null;
    }

    private ObjectWriter createObjectWriterForSocket() throws IOException {
        this.socket.setSoTimeout(this.acceptConnectionTimeout);
        ObjectWriter objectWriter = this.objectWriterFactory.newAutoFlushingObjectWriter(this.socket.getOutputStream());
        this.socket.setSoTimeout(0);
        return objectWriter;
    }

    private SocketConnector createConnector(InetAddress address, int port, int initialDelay, long retryDelay) {
        SocketConnector connector = newConnector(address, port, initialDelay, retryDelay);
        connector.setExceptionHandler(this);
        connector.setSocketFactory(getSocketFactory());
        return connector;
    }

    private void dispatchEvents(ObjectWriter objectWriter) throws InterruptedException, IOException {
        while (true) {
            E event = this.deque.takeFirst();
            postProcessEvent(event);
            Serializable serializableEvent = getPST().transform(event);
            try {
                objectWriter.write(serializableEvent);
            } catch (IOException e) {
                tryReAddingEventToFrontOfQueue(event);
                throw e;
            }
        }
    }

    private void tryReAddingEventToFrontOfQueue(E event) {
        boolean wasInserted = this.deque.offerFirst(event);
        if (!wasInserted) {
            addInfo("Dropping event due to socket connection error and maxed out deque capacity");
        }
    }

    @Override // ch.qos.logback.core.net.SocketConnector.ExceptionHandler
    public void connectionFailed(SocketConnector connector, Exception ex) {
        if (ex instanceof InterruptedException) {
            addInfo("connector interrupted");
        } else if (ex instanceof ConnectException) {
            addInfo(this.peerId + "connection refused");
        } else {
            addInfo(this.peerId + String.valueOf(ex));
        }
    }

    protected SocketConnector newConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
        return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
    }

    protected SocketFactory getSocketFactory() {
        return SocketFactory.getDefault();
    }

    public void setRemoteHost(String host) {
        this.remoteHost = host;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setReconnectionDelay(Duration delay) {
        this.reconnectionDelay = delay;
    }

    public Duration getReconnectionDelay() {
        return this.reconnectionDelay;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setEventDelayLimit(Duration eventDelayLimit) {
        this.eventDelayLimit = eventDelayLimit;
    }

    public Duration getEventDelayLimit() {
        return this.eventDelayLimit;
    }

    void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
        this.acceptConnectionTimeout = acceptConnectionTimeout;
    }
}
