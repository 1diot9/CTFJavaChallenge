package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.NetworkChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLEngine;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.Acceptor;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.jsse.JSSESupport;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Endpoint.class */
public class Nio2Endpoint extends AbstractJsseEndpoint<Nio2Channel, AsynchronousSocketChannel> {
    private volatile boolean allClosed;
    private SynchronizedStack<Nio2Channel> nioChannels;
    private static final Log log = LogFactory.getLog((Class<?>) Nio2Endpoint.class);
    private static final Log logCertificate = LogFactory.getLog(Nio2Endpoint.class.getName() + ".certificate");
    private static final Log logHandshake = LogFactory.getLog(Nio2Endpoint.class.getName() + ".handshake");
    private static ThreadLocal<Boolean> inlineCompletion = new ThreadLocal<>();
    private volatile AsynchronousServerSocketChannel serverSock = null;
    private AsynchronousChannelGroup threadGroup = null;
    private SocketAddress previousAcceptedSocketRemoteAddress = null;
    private long previousAcceptedSocketNanoTime = 0;

    public int getKeepAliveCount() {
        return -1;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void bind() throws Exception {
        if (getExecutor() == null) {
            createExecutor();
        }
        if (getExecutor() instanceof ExecutorService) {
            this.threadGroup = AsynchronousChannelGroup.withThreadPool((ExecutorService) getExecutor());
        }
        if (!this.internalExecutor) {
            log.warn(sm.getString("endpoint.nio2.exclusiveExecutor"));
        }
        this.serverSock = AsynchronousServerSocketChannel.open(this.threadGroup);
        this.socketProperties.setProperties(this.serverSock);
        InetSocketAddress addr = new InetSocketAddress(getAddress(), getPortWithOffset());
        this.serverSock.bind(addr, getAcceptCount());
        initialiseSsl();
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void startInternal() throws Exception {
        if (!this.running) {
            this.allClosed = false;
            this.running = true;
            this.paused = false;
            if (this.socketProperties.getProcessorCache() != 0) {
                this.processorCache = new SynchronizedStack<>(128, this.socketProperties.getProcessorCache());
            }
            int actualBufferPool = this.socketProperties.getActualBufferPool(isSSLEnabled() ? getSniParseLimit() * 2 : 0);
            if (actualBufferPool != 0) {
                this.nioChannels = new SynchronizedStack<>(128, actualBufferPool);
            }
            if (getExecutor() == null) {
                createExecutor();
            }
            initializeConnectionLatch();
            startAcceptorThread();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void startAcceptorThread() {
        if (this.acceptor == null) {
            this.acceptor = new Nio2Acceptor(this);
            this.acceptor.setThreadName(getName() + "-Acceptor");
        }
        this.acceptor.state = Acceptor.AcceptorState.RUNNING;
        getExecutor().execute(this.acceptor);
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void resume() {
        super.resume();
        if (isRunning()) {
            this.acceptor.state = Acceptor.AcceptorState.RUNNING;
            getExecutor().execute(this.acceptor);
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void stopInternal() {
        if (!this.paused) {
            pause();
        }
        if (this.running) {
            this.running = false;
            this.acceptor.stop(10);
            getExecutor().execute(() -> {
                try {
                    for (SocketWrapperBase<Nio2Channel> wrapper : getConnections()) {
                        wrapper.close();
                    }
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                } finally {
                    this.allClosed = true;
                }
            });
            if (this.nioChannels != null) {
                while (true) {
                    Nio2Channel socket = this.nioChannels.pop();
                    if (socket == null) {
                        break;
                    } else {
                        socket.free();
                    }
                }
                this.nioChannels = null;
            }
            if (this.processorCache != null) {
                this.processorCache.clear();
                this.processorCache = null;
            }
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractJsseEndpoint, org.apache.tomcat.util.net.AbstractEndpoint
    public void unbind() throws Exception {
        if (this.running) {
            stop();
        }
        doCloseServerSocket();
        destroySsl();
        super.unbind();
        shutdownExecutor();
        if (getHandler() != null) {
            getHandler().recycle();
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected void doCloseServerSocket() throws IOException {
        if (this.serverSock != null) {
            this.serverSock.close();
            this.serverSock = null;
        }
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void shutdownExecutor() {
        if (this.threadGroup != null && this.internalExecutor) {
            try {
                long timeout = getExecutorTerminationTimeoutMillis();
                while (timeout > 0 && !this.allClosed) {
                    timeout--;
                    Thread.sleep(1L);
                }
                this.threadGroup.shutdownNow();
                if (timeout > 0) {
                    this.threadGroup.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                }
            } catch (IOException e) {
                getLog().warn(sm.getString("endpoint.warn.executorShutdown", getName()), e);
            } catch (InterruptedException e2) {
            }
            if (!this.threadGroup.isTerminated()) {
                getLog().warn(sm.getString("endpoint.warn.executorShutdown", getName()));
            }
            this.threadGroup = null;
        }
        super.shutdownExecutor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public boolean setSocketOptions(AsynchronousSocketChannel socket) {
        Nio2SocketWrapper socketWrapper = null;
        try {
            Nio2Channel channel = null;
            if (this.nioChannels != null) {
                channel = this.nioChannels.pop();
            }
            if (channel == null) {
                SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                if (isSSLEnabled()) {
                    channel = new SecureNio2Channel(bufhandler, this);
                } else {
                    channel = new Nio2Channel(bufhandler);
                }
            }
            Nio2SocketWrapper newWrapper = new Nio2SocketWrapper(channel, this);
            channel.reset(socket, newWrapper);
            this.connections.put(socket, newWrapper);
            socketWrapper = newWrapper;
            this.socketProperties.setProperties(socket);
            socketWrapper.setReadTimeout(getConnectionTimeout());
            socketWrapper.setWriteTimeout(getConnectionTimeout());
            socketWrapper.setKeepAliveLeft(getMaxKeepAliveRequests());
            return processSocket(socketWrapper, SocketEvent.OPEN_READ, false);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.error(sm.getString("endpoint.socketOptionsError"), t);
            if (socketWrapper == null) {
                destroySocket(socket);
                return false;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public void destroySocket(AsynchronousSocketChannel socket) {
        countDownConnection();
        try {
            socket.close();
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug(sm.getString("endpoint.err.close"), ioe);
            }
        }
    }

    protected SynchronizedStack<Nio2Channel> getNioChannels() {
        return this.nioChannels;
    }

    @Override // org.apache.tomcat.util.net.AbstractJsseEndpoint
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public AsynchronousSocketChannel serverSocketAccept() throws Exception {
        AsynchronousSocketChannel result = this.serverSock.accept().get();
        if (!JrePlatform.IS_WINDOWS) {
            SocketAddress currentRemoteAddress = result.getRemoteAddress();
            long currentNanoTime = System.nanoTime();
            if (currentRemoteAddress.equals(this.previousAcceptedSocketRemoteAddress) && currentNanoTime - this.previousAcceptedSocketNanoTime < 1000) {
                throw new IOException(sm.getString("endpoint.err.duplicateAccept"));
            }
            this.previousAcceptedSocketRemoteAddress = currentRemoteAddress;
            this.previousAcceptedSocketNanoTime = currentNanoTime;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    public Log getLog() {
        return log;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected Log getLogCertificate() {
        return logCertificate;
    }

    @Override // org.apache.tomcat.util.net.AbstractEndpoint
    protected SocketProcessorBase<Nio2Channel> createSocketProcessor(SocketWrapperBase<Nio2Channel> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Endpoint$Nio2Acceptor.class */
    public class Nio2Acceptor extends Acceptor<AsynchronousSocketChannel> implements CompletionHandler<AsynchronousSocketChannel, Void> {
        protected int errorDelay;

        public Nio2Acceptor(AbstractEndpoint<?, AsynchronousSocketChannel> endpoint) {
            super(endpoint);
            this.errorDelay = 0;
        }

        @Override // org.apache.tomcat.util.net.Acceptor, java.lang.Runnable
        public void run() {
            if (!Nio2Endpoint.this.isPaused()) {
                try {
                    Nio2Endpoint.this.countUpOrAwaitConnection();
                } catch (InterruptedException e) {
                }
                if (!Nio2Endpoint.this.isPaused()) {
                    Nio2Endpoint.this.serverSock.accept(null, this);
                    return;
                } else {
                    this.state = Acceptor.AcceptorState.PAUSED;
                    return;
                }
            }
            this.state = Acceptor.AcceptorState.PAUSED;
        }

        @Override // org.apache.tomcat.util.net.Acceptor
        public void stop(int waitSeconds) {
            Nio2Endpoint.this.acceptor.state = Acceptor.AcceptorState.ENDED;
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(AsynchronousSocketChannel socket, Void attachment) {
            this.errorDelay = 0;
            if (Nio2Endpoint.this.isRunning() && !Nio2Endpoint.this.isPaused()) {
                if (Nio2Endpoint.this.getMaxConnections() == -1) {
                    Nio2Endpoint.this.serverSock.accept(null, this);
                } else if (Nio2Endpoint.this.getConnectionCount() < Nio2Endpoint.this.getMaxConnections()) {
                    try {
                        Nio2Endpoint.this.countUpOrAwaitConnection();
                    } catch (InterruptedException e) {
                    }
                    Nio2Endpoint.this.serverSock.accept(null, this);
                } else {
                    Nio2Endpoint.this.getExecutor().execute(this);
                }
                if (!Nio2Endpoint.this.setSocketOptions(socket)) {
                    Nio2Endpoint.this.closeSocket(socket);
                    return;
                }
                return;
            }
            if (Nio2Endpoint.this.isRunning()) {
                this.state = Acceptor.AcceptorState.PAUSED;
            }
            Nio2Endpoint.this.destroySocket(socket);
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable t, Void attachment) {
            if (Nio2Endpoint.this.isRunning()) {
                if (!Nio2Endpoint.this.isPaused()) {
                    if (Nio2Endpoint.this.getMaxConnections() == -1) {
                        Nio2Endpoint.this.serverSock.accept(null, this);
                    } else {
                        Nio2Endpoint.this.getExecutor().execute(this);
                    }
                } else {
                    this.state = Acceptor.AcceptorState.PAUSED;
                }
                Nio2Endpoint.this.countDownConnection();
                this.errorDelay = handleExceptionWithDelay(this.errorDelay);
                ExceptionUtils.handleThrowable(t);
                Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.accept.fail"), t);
                return;
            }
            Nio2Endpoint.this.countDownConnection();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Endpoint$Nio2SocketWrapper.class */
    public static class Nio2SocketWrapper extends SocketWrapperBase<Nio2Channel> {
        private final SynchronizedStack<Nio2Channel> nioChannels;
        private SendfileData sendfileData;
        private final CompletionHandler<Integer, ByteBuffer> readCompletionHandler;
        private boolean readInterest;
        private boolean readNotify;
        private final CompletionHandler<Integer, ByteBuffer> writeCompletionHandler;
        private final CompletionHandler<Long, ByteBuffer[]> gatheringWriteCompletionHandler;
        private boolean writeInterest;
        private boolean writeNotify;
        private CompletionHandler<Integer, SendfileData> sendfileHandler;

        public Nio2SocketWrapper(Nio2Channel channel, final Nio2Endpoint endpoint) {
            super(channel, endpoint);
            this.sendfileData = null;
            this.readInterest = false;
            this.readNotify = false;
            this.writeInterest = false;
            this.writeNotify = false;
            this.sendfileHandler = new CompletionHandler<Integer, SendfileData>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.1
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nWrite, SendfileData attachment) {
                    if (nWrite.intValue() < 0) {
                        failed((Throwable) new EOFException(), attachment);
                        return;
                    }
                    attachment.pos += nWrite.intValue();
                    ByteBuffer buffer = Nio2SocketWrapper.this.getSocket().getBufHandler().getWriteBuffer();
                    if (!buffer.hasRemaining()) {
                        if (attachment.length <= 0) {
                            Nio2SocketWrapper.this.setSendfileData(null);
                            try {
                                attachment.fchannel.close();
                            } catch (IOException e) {
                            }
                            if (Nio2Endpoint.isInline()) {
                                attachment.doneInline = true;
                                return;
                            }
                            switch (attachment.keepAliveState) {
                                case NONE:
                                    Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.DISCONNECT, false);
                                    return;
                                case PIPELINED:
                                    if (!Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_READ, true)) {
                                        Nio2SocketWrapper.this.close();
                                        return;
                                    }
                                    return;
                                case OPEN:
                                    Nio2SocketWrapper.this.registerReadInterest();
                                    return;
                                default:
                                    return;
                            }
                        }
                        Nio2SocketWrapper.this.getSocket().getBufHandler().configureWriteBufferForWrite();
                        try {
                            int nRead = attachment.fchannel.read(buffer);
                            if (nRead > 0) {
                                Nio2SocketWrapper.this.getSocket().getBufHandler().configureWriteBufferForRead();
                                if (attachment.length < buffer.remaining()) {
                                    buffer.limit((buffer.limit() - buffer.remaining()) + ((int) attachment.length));
                                }
                                attachment.length -= nRead;
                            } else {
                                failed((Throwable) new EOFException(), attachment);
                                return;
                            }
                        } catch (IOException e2) {
                            failed((Throwable) e2, attachment);
                            return;
                        }
                    }
                    Nio2SocketWrapper.this.getSocket().write(buffer, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, this);
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, SendfileData attachment) {
                    try {
                        attachment.fchannel.close();
                    } catch (IOException e) {
                    }
                    if (!Nio2Endpoint.isInline()) {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, false);
                    } else {
                        attachment.doneInline = true;
                        attachment.error = true;
                    }
                }
            };
            this.nioChannels = endpoint.getNioChannels();
            this.socketBufferHandler = channel.getBufHandler();
            this.readCompletionHandler = new CompletionHandler<Integer, ByteBuffer>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.2
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nBytes, ByteBuffer attachment) {
                    boolean notify;
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + Nio2SocketWrapper.this + "], Interest: [" + Nio2SocketWrapper.this.readInterest + "]");
                    }
                    synchronized (Nio2SocketWrapper.this.readCompletionHandler) {
                        Nio2SocketWrapper.this.readNotify = false;
                        if (nBytes.intValue() < 0) {
                            failed((Throwable) new EOFException(), attachment);
                        } else {
                            if (Nio2SocketWrapper.this.readInterest && !Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.readNotify = true;
                            } else {
                                Nio2SocketWrapper.this.readPending.release();
                            }
                            Nio2SocketWrapper.this.readInterest = false;
                        }
                        notify = Nio2SocketWrapper.this.readNotify;
                    }
                    if (notify) {
                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_READ, false);
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, ByteBuffer attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException) exc;
                    } else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    if (exc instanceof AsynchronousCloseException) {
                        Nio2SocketWrapper.this.readPending.release();
                        Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.STOP, false);
                    } else if (!Nio2SocketWrapper.this.getEndpoint().processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true)) {
                        Nio2SocketWrapper.this.close();
                    }
                }
            };
            this.writeCompletionHandler = new CompletionHandler<Integer, ByteBuffer>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.3
                @Override // java.nio.channels.CompletionHandler
                public void completed(Integer nBytes, ByteBuffer attachment) {
                    boolean notify = false;
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        Nio2SocketWrapper.this.writeNotify = false;
                        if (nBytes.intValue() < 0) {
                            failed((Throwable) new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        } else if (!Nio2SocketWrapper.this.nonBlockingWriteBuffer.isEmpty()) {
                            ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(attachment);
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, Nio2SocketWrapper.this.gatheringWriteCompletionHandler);
                        } else if (attachment.hasRemaining()) {
                            Nio2SocketWrapper.this.getSocket().write(attachment, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, Nio2SocketWrapper.this.writeCompletionHandler);
                        } else {
                            if (Nio2SocketWrapper.this.writeInterest && !Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.writeNotify = true;
                                notify = true;
                            } else {
                                Nio2SocketWrapper.this.writePending.release();
                            }
                            Nio2SocketWrapper.this.writeInterest = false;
                        }
                    }
                    if (notify && !endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_WRITE, true)) {
                        Nio2SocketWrapper.this.close();
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, ByteBuffer attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException) exc;
                    } else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    Nio2SocketWrapper.this.writePending.release();
                    if (!endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true)) {
                        Nio2SocketWrapper.this.close();
                    }
                }
            };
            this.gatheringWriteCompletionHandler = new CompletionHandler<Long, ByteBuffer[]>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.4
                @Override // java.nio.channels.CompletionHandler
                public void completed(Long nBytes, ByteBuffer[] attachment) {
                    boolean notify = false;
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        Nio2SocketWrapper.this.writeNotify = false;
                        if (nBytes.longValue() < 0) {
                            failed((Throwable) new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        } else if (!Nio2SocketWrapper.this.nonBlockingWriteBuffer.isEmpty() || SocketWrapperBase.buffersArrayHasRemaining(attachment, 0, attachment.length)) {
                            ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(attachment);
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, Nio2SocketWrapper.this.gatheringWriteCompletionHandler);
                        } else {
                            if (Nio2SocketWrapper.this.writeInterest && !Nio2Endpoint.isInline()) {
                                Nio2SocketWrapper.this.writeNotify = true;
                                notify = true;
                            } else {
                                Nio2SocketWrapper.this.writePending.release();
                            }
                            Nio2SocketWrapper.this.writeInterest = false;
                        }
                    }
                    if (notify && !endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.OPEN_WRITE, true)) {
                        Nio2SocketWrapper.this.close();
                    }
                }

                @Override // java.nio.channels.CompletionHandler
                public void failed(Throwable exc, ByteBuffer[] attachment) {
                    IOException ioe;
                    if (exc instanceof IOException) {
                        ioe = (IOException) exc;
                    } else {
                        ioe = new IOException(exc);
                    }
                    Nio2SocketWrapper.this.setError(ioe);
                    Nio2SocketWrapper.this.writePending.release();
                    if (!endpoint.processSocket(Nio2SocketWrapper.this, SocketEvent.ERROR, true)) {
                        Nio2SocketWrapper.this.close();
                    }
                }
            };
        }

        public void setSendfileData(SendfileData sf) {
            this.sendfileData = sf;
        }

        public SendfileData getSendfileData() {
            return this.sendfileData;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadyForRead() throws IOException {
            synchronized (this.readCompletionHandler) {
                if (this.readNotify) {
                    return true;
                }
                if (!this.readPending.tryAcquire()) {
                    this.readInterest = true;
                    return false;
                }
                if (!this.socketBufferHandler.isReadBufferEmpty()) {
                    this.readPending.release();
                    return true;
                }
                boolean isReady = fillReadBuffer(false) > 0;
                if (!isReady) {
                    this.readInterest = true;
                }
                return isReady;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadyForWrite() {
            synchronized (this.writeCompletionHandler) {
                if (this.writeNotify) {
                    return true;
                }
                if (!this.writePending.tryAcquire()) {
                    this.writeInterest = true;
                    return false;
                }
                if (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty()) {
                    this.writePending.release();
                    return true;
                }
                boolean isReady = !flushNonBlockingInternal(true);
                if (!isReady) {
                    this.writeInterest = true;
                }
                return isReady;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            boolean notify;
            int i;
            checkError();
            if (Nio2Endpoint.log.isDebugEnabled()) {
                Nio2Endpoint.log.debug("Socket: [" + this + "], block: [" + block + "], length: [" + len + "]");
            }
            if (this.socketBufferHandler == null) {
                throw new IOException(sm.getString("socket.closed"));
            }
            synchronized (this.readCompletionHandler) {
                notify = this.readNotify;
            }
            if (!notify) {
                if (block) {
                    try {
                        this.readPending.acquire();
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                } else if (!this.readPending.tryAcquire()) {
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + this + "], Read in progress. Returning [0]");
                        return 0;
                    }
                    return 0;
                }
            }
            int nRead = populateReadBuffer(b, off, len);
            if (nRead > 0) {
                synchronized (this.readCompletionHandler) {
                    this.readNotify = false;
                }
                this.readPending.release();
                return nRead;
            }
            synchronized (this.readCompletionHandler) {
                int nRead2 = fillReadBuffer(block);
                if (nRead2 > 0) {
                    this.socketBufferHandler.configureReadBufferForRead();
                    nRead2 = Math.min(nRead2, len);
                    this.socketBufferHandler.getReadBuffer().get(b, off, nRead2);
                } else if (nRead2 == 0 && !block) {
                    this.readInterest = true;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug("Socket: [" + this + "], Read: [" + nRead2 + "]");
                }
                i = nRead2;
            }
            return i;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public int read(boolean block, ByteBuffer to) throws IOException {
            boolean notify;
            int nRead;
            int i;
            checkError();
            if (this.socketBufferHandler == null) {
                throw new IOException(sm.getString("socket.closed"));
            }
            synchronized (this.readCompletionHandler) {
                notify = this.readNotify;
            }
            if (!notify) {
                if (block) {
                    try {
                        this.readPending.acquire();
                    } catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                } else if (!this.readPending.tryAcquire()) {
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + this + "], Read in progress. Returning [0]");
                        return 0;
                    }
                    return 0;
                }
            }
            int nRead2 = populateReadBuffer(to);
            if (nRead2 > 0) {
                synchronized (this.readCompletionHandler) {
                    this.readNotify = false;
                }
                this.readPending.release();
                return nRead2;
            }
            synchronized (this.readCompletionHandler) {
                int limit = this.socketBufferHandler.getReadBuffer().capacity();
                if (block && to.remaining() >= limit) {
                    to.limit(to.position() + limit);
                    nRead = fillReadBuffer(block, to);
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + this + "], Read direct from socket: [" + nRead + "]");
                    }
                } else {
                    nRead = fillReadBuffer(block);
                    if (Nio2Endpoint.log.isDebugEnabled()) {
                        Nio2Endpoint.log.debug("Socket: [" + this + "], Read into buffer: [" + nRead + "]");
                    }
                    if (nRead > 0) {
                        nRead = populateReadBuffer(to);
                    } else if (nRead == 0 && !block) {
                        this.readInterest = true;
                    }
                }
                i = nRead;
            }
            return i;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void doClose() {
            if (Nio2Endpoint.log.isDebugEnabled()) {
                Nio2Endpoint.log.debug("Calling [" + getEndpoint() + "].closeSocket([" + this + "])");
            }
            try {
                getEndpoint().connections.remove(getSocket().getIOChannel());
                if (getSocket().isOpen()) {
                    getSocket().close(true);
                }
                if (getEndpoint().running && (this.nioChannels == null || !this.nioChannels.push(getSocket()))) {
                    getSocket().free();
                }
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.error(sm.getString("endpoint.debug.channelCloseFail"), e);
                }
            } finally {
                this.socketBufferHandler = SocketBufferHandler.EMPTY;
                this.nonBlockingWriteBuffer.clear();
                reset(Nio2Channel.CLOSED_NIO2_CHANNEL);
            }
            try {
                SendfileData data = getSendfileData();
                if (data != null && data.fchannel != null && data.fchannel.isOpen()) {
                    data.fchannel.close();
                }
            } catch (Throwable e2) {
                ExceptionUtils.handleThrowable(e2);
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.error(sm.getString("endpoint.sendfile.closeError"), e2);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean hasAsyncIO() {
            return getEndpoint().getUseAsyncIO();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean needSemaphores() {
            return true;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean hasPerOperationTimeout() {
            return true;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected <A> SocketWrapperBase<Nio2Channel>.OperationState<A> newOperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase<Nio2Channel>.VectoredIOCompletionHandler<A> completion) {
            return new Nio2OperationState(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Endpoint$Nio2SocketWrapper$Nio2OperationState.class */
        public class Nio2OperationState<A> extends SocketWrapperBase<Nio2Channel>.OperationState<A> {
            private Nio2OperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase<Nio2Channel>.VectoredIOCompletionHandler<A> completion) {
                super(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
            }

            @Override // org.apache.tomcat.util.net.SocketWrapperBase.OperationState
            protected boolean isInline() {
                return Nio2Endpoint.isInline();
            }

            @Override // org.apache.tomcat.util.net.SocketWrapperBase.OperationState
            protected void start() {
                if (this.read) {
                    synchronized (Nio2SocketWrapper.this.readCompletionHandler) {
                        Nio2SocketWrapper.this.readNotify = true;
                    }
                } else {
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        Nio2SocketWrapper.this.writeNotify = true;
                    }
                }
                Nio2Endpoint.startInline();
                try {
                    run();
                } finally {
                    Nio2Endpoint.endInline();
                }
            }

            @Override // java.lang.Runnable
            public void run() {
                if (this.read) {
                    long nBytes = 0;
                    if (!Nio2SocketWrapper.this.socketBufferHandler.isReadBufferEmpty()) {
                        synchronized (Nio2SocketWrapper.this.readCompletionHandler) {
                            Nio2SocketWrapper.this.socketBufferHandler.configureReadBufferForRead();
                            for (int i = 0; i < this.length && !Nio2SocketWrapper.this.socketBufferHandler.isReadBufferEmpty(); i++) {
                                nBytes += SocketWrapperBase.transfer(Nio2SocketWrapper.this.socketBufferHandler.getReadBuffer(), this.buffers[this.offset + i]);
                            }
                        }
                        if (nBytes > 0) {
                            this.completion.completed(Long.valueOf(nBytes), (SocketWrapperBase.OperationState) this);
                        }
                    }
                    if (nBytes == 0) {
                        Nio2SocketWrapper.this.getSocket().read(this.buffers, this.offset, this.length, this.timeout, this.unit, this, this.completion);
                        return;
                    }
                    return;
                }
                if (!Nio2SocketWrapper.this.socketBufferHandler.isWriteBufferEmpty()) {
                    synchronized (Nio2SocketWrapper.this.writeCompletionHandler) {
                        Nio2SocketWrapper.this.socketBufferHandler.configureWriteBufferForRead();
                        ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(Nio2SocketWrapper.this.socketBufferHandler.getWriteBuffer());
                        if (SocketWrapperBase.buffersArrayHasRemaining(array, 0, array.length)) {
                            Nio2SocketWrapper.this.getSocket().write(array, 0, array.length, this.timeout, this.unit, array, new CompletionHandler<Long, ByteBuffer[]>() { // from class: org.apache.tomcat.util.net.Nio2Endpoint.Nio2SocketWrapper.Nio2OperationState.1
                                @Override // java.nio.channels.CompletionHandler
                                public void completed(Long nBytes2, ByteBuffer[] buffers) {
                                    if (nBytes2.longValue() < 0) {
                                        failed((Throwable) new EOFException(), (ByteBuffer[]) null);
                                    } else if (SocketWrapperBase.buffersArrayHasRemaining(buffers, 0, buffers.length)) {
                                        Nio2SocketWrapper.this.getSocket().write(buffers, 0, buffers.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, buffers, this);
                                    } else {
                                        Nio2OperationState.this.process();
                                    }
                                }

                                @Override // java.nio.channels.CompletionHandler
                                public void failed(Throwable exc, ByteBuffer[] buffers) {
                                    Nio2OperationState.this.completion.failed(exc, (SocketWrapperBase.OperationState) Nio2OperationState.this);
                                }
                            });
                            return;
                        }
                    }
                }
                Nio2SocketWrapper.this.getSocket().write(this.buffers, this.offset, this.length, this.timeout, this.unit, this, this.completion);
            }
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        private int fillReadBuffer(boolean block, ByteBuffer to) throws IOException {
            int nRead = 0;
            Future<Integer> integer = null;
            try {
                if (block) {
                    try {
                        try {
                            try {
                                Future<Integer> integer2 = getSocket().read(to);
                                long timeout = getReadTimeout();
                                if (timeout > 0) {
                                    nRead = integer2.get(timeout, TimeUnit.MILLISECONDS).intValue();
                                } else {
                                    nRead = integer2.get().intValue();
                                }
                            } catch (TimeoutException e) {
                                integer.cancel(true);
                                throw new SocketTimeoutException();
                            }
                        } catch (ExecutionException e2) {
                            if (e2.getCause() instanceof IOException) {
                                throw ((IOException) e2.getCause());
                            }
                            throw new IOException(e2);
                        }
                    } catch (InterruptedException e3) {
                        throw new IOException(e3);
                    }
                } else {
                    Nio2Endpoint.startInline();
                    getSocket().read(to, AbstractEndpoint.toTimeout(getReadTimeout()), TimeUnit.MILLISECONDS, to, this.readCompletionHandler);
                    Nio2Endpoint.endInline();
                    if (this.readPending.availablePermits() == 1) {
                        nRead = to.position();
                    }
                }
                return nRead;
            } finally {
                this.readPending.release();
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void writeNonBlocking(byte[] buf, int off, int len) throws IOException {
            synchronized (this.writeCompletionHandler) {
                checkError();
                if (this.writeNotify || this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    int thisTime = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                    int len2 = len - thisTime;
                    int off2 = off + thisTime;
                    if (len2 > 0) {
                        this.nonBlockingWriteBuffer.add(buf, off2, len2);
                    }
                    flushNonBlockingInternal(true);
                } else {
                    this.nonBlockingWriteBuffer.add(buf, off, len);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void writeNonBlocking(ByteBuffer from) throws IOException {
            writeNonBlockingInternal(from);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void writeNonBlockingInternal(ByteBuffer from) throws IOException {
            synchronized (this.writeCompletionHandler) {
                checkError();
                if (this.writeNotify || this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    transfer(from, this.socketBufferHandler.getWriteBuffer());
                    if (from.remaining() > 0) {
                        this.nonBlockingWriteBuffer.add(from);
                    }
                    flushNonBlockingInternal(true);
                } else {
                    this.nonBlockingWriteBuffer.add(from);
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void doWrite(boolean block, ByteBuffer from) throws IOException {
            Future<Integer> integer = null;
            do {
                try {
                    integer = getSocket().write(from);
                    long timeout = getWriteTimeout();
                    if (timeout > 0) {
                        if (integer.get(timeout, TimeUnit.MILLISECONDS).intValue() < 0) {
                            throw new EOFException(sm.getString("iob.failedwrite"));
                        }
                    } else if (integer.get().intValue() < 0) {
                        throw new EOFException(sm.getString("iob.failedwrite"));
                    }
                } catch (InterruptedException e) {
                    throw new IOException(e);
                } catch (ExecutionException e2) {
                    if (e2.getCause() instanceof IOException) {
                        throw ((IOException) e2.getCause());
                    }
                    throw new IOException(e2);
                } catch (TimeoutException e3) {
                    integer.cancel(true);
                    throw new SocketTimeoutException();
                }
            } while (from.hasRemaining());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void flushBlocking() throws IOException {
            checkError();
            if (this.writePending.tryAcquire(AbstractEndpoint.toTimeout(getWriteTimeout()), TimeUnit.MILLISECONDS)) {
                this.writePending.release();
                super.flushBlocking();
                return;
            }
            throw new SocketTimeoutException();
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected boolean flushNonBlocking() throws IOException {
            checkError();
            return flushNonBlockingInternal(false);
        }

        private boolean flushNonBlockingInternal(boolean hasPermit) {
            boolean hasDataToWrite;
            synchronized (this.writeCompletionHandler) {
                if (this.writeNotify || hasPermit || this.writePending.tryAcquire()) {
                    this.writeNotify = false;
                    this.socketBufferHandler.configureWriteBufferForRead();
                    if (!this.nonBlockingWriteBuffer.isEmpty()) {
                        ByteBuffer[] array = this.nonBlockingWriteBuffer.toArray(this.socketBufferHandler.getWriteBuffer());
                        Nio2Endpoint.startInline();
                        getSocket().write(array, 0, array.length, AbstractEndpoint.toTimeout(getWriteTimeout()), TimeUnit.MILLISECONDS, array, this.gatheringWriteCompletionHandler);
                        Nio2Endpoint.endInline();
                    } else if (this.socketBufferHandler.getWriteBuffer().hasRemaining()) {
                        Nio2Endpoint.startInline();
                        getSocket().write(this.socketBufferHandler.getWriteBuffer(), AbstractEndpoint.toTimeout(getWriteTimeout()), TimeUnit.MILLISECONDS, this.socketBufferHandler.getWriteBuffer(), this.writeCompletionHandler);
                        Nio2Endpoint.endInline();
                    } else {
                        if (!hasPermit) {
                            this.writePending.release();
                        }
                        this.writeInterest = false;
                    }
                }
                hasDataToWrite = hasDataToWrite();
            }
            return hasDataToWrite;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean hasDataToRead() {
            boolean z;
            synchronized (this.readCompletionHandler) {
                z = (this.socketBufferHandler.isReadBufferEmpty() && !this.readNotify && getError() == null) ? false : true;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean hasDataToWrite() {
            boolean z;
            synchronized (this.writeCompletionHandler) {
                z = (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty() && !this.writeNotify && this.writePending.availablePermits() != 0 && getError() == null) ? false : true;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isReadPending() {
            boolean z;
            synchronized (this.readCompletionHandler) {
                z = this.readPending.availablePermits() == 0;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public boolean isWritePending() {
            boolean z;
            synchronized (this.writeCompletionHandler) {
                z = this.writePending.availablePermits() == 0;
            }
            return z;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerReadInterest() {
            synchronized (this.readCompletionHandler) {
                if (this.readNotify) {
                    return;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug(sm.getString("endpoint.debug.registerRead", this));
                }
                this.readInterest = true;
                if (this.readPending.tryAcquire()) {
                    try {
                        if (fillReadBuffer(false) > 0 && !getEndpoint().processSocket(this, SocketEvent.OPEN_READ, true)) {
                            close();
                        }
                    } catch (IOException e) {
                        setError(e);
                    }
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void registerWriteInterest() {
            synchronized (this.writeCompletionHandler) {
                if (this.writeNotify) {
                    return;
                }
                if (Nio2Endpoint.log.isDebugEnabled()) {
                    Nio2Endpoint.log.debug(sm.getString("endpoint.debug.registerWrite", this));
                }
                this.writeInterest = true;
                if (this.writePending.availablePermits() == 1 && !getEndpoint().processSocket(this, SocketEvent.OPEN_WRITE, true)) {
                    close();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            SendfileData data = (SendfileData) sendfileData;
            setSendfileData(data);
            if (data.fchannel == null || !data.fchannel.isOpen()) {
                Path path = new File(sendfileData.fileName).toPath();
                try {
                    data.fchannel = FileChannel.open(path, StandardOpenOption.READ).position(sendfileData.pos);
                } catch (IOException e) {
                    return SendfileState.ERROR;
                }
            }
            getSocket().getBufHandler().configureWriteBufferForWrite();
            ByteBuffer buffer = getSocket().getBufHandler().getWriteBuffer();
            try {
                int nRead = data.fchannel.read(buffer);
                if (nRead >= 0) {
                    data.length -= nRead;
                    getSocket().getBufHandler().configureWriteBufferForRead();
                    Nio2Endpoint.startInline();
                    getSocket().write(buffer, AbstractEndpoint.toTimeout(getWriteTimeout()), TimeUnit.MILLISECONDS, data, this.sendfileHandler);
                    Nio2Endpoint.endInline();
                    if (data.doneInline) {
                        if (data.error) {
                            return SendfileState.ERROR;
                        }
                        return SendfileState.DONE;
                    }
                    return SendfileState.PENDING;
                }
                return SendfileState.ERROR;
            } catch (IOException e2) {
                return SendfileState.ERROR;
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteAddr() {
            AsynchronousSocketChannel sc = getSocket().getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getRemoteAddress();
                } catch (IOException e) {
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.remoteAddr = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemoteHost() {
            AsynchronousSocketChannel sc = getSocket().getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getRemoteAddress();
                } catch (IOException e) {
                    Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noRemoteHost", getSocket()), e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.remoteHost = ((InetSocketAddress) socketAddress).getAddress().getHostName();
                    if (this.remoteAddr == null) {
                        this.remoteAddr = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
                    }
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateRemotePort() {
            AsynchronousSocketChannel sc = getSocket().getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getRemoteAddress();
                } catch (IOException e) {
                    Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noRemotePort", getSocket()), e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.remotePort = ((InetSocketAddress) socketAddress).getPort();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalName() {
            AsynchronousSocketChannel sc = getSocket().getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getLocalAddress();
                } catch (IOException e) {
                    Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noLocalName", getSocket()), e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.localName = ((InetSocketAddress) socketAddress).getHostName();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalAddr() {
            AsynchronousSocketChannel sc = getSocket().getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getLocalAddress();
                } catch (IOException e) {
                    Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noLocalAddr", getSocket()), e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.localAddr = ((InetSocketAddress) socketAddress).getAddress().getHostAddress();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        protected void populateLocalPort() {
            AsynchronousSocketChannel sc = getSocket().getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getLocalAddress();
                } catch (IOException e) {
                    Nio2Endpoint.log.warn(sm.getString("endpoint.warn.noLocalPort", getSocket()), e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.localPort = ((InetSocketAddress) socketAddress).getPort();
                }
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public SSLSupport getSslSupport() {
            if (getSocket() instanceof SecureNio2Channel) {
                SecureNio2Channel ch2 = (SecureNio2Channel) getSocket();
                return ch2.getSSLSupport();
            }
            return null;
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            SecureNio2Channel sslChannel = (SecureNio2Channel) getSocket();
            SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake();
                ((JSSESupport) sslSupport).setSession(engine.getSession());
            }
        }

        @Override // org.apache.tomcat.util.net.SocketWrapperBase
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
            getSocket().setAppReadBufHandler(handler);
        }
    }

    public static void startInline() {
        inlineCompletion.set(Boolean.TRUE);
    }

    public static void endInline() {
        inlineCompletion.set(Boolean.FALSE);
    }

    public static boolean isInline() {
        Boolean flag = inlineCompletion.get();
        if (flag == null) {
            return false;
        }
        return flag.booleanValue();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Endpoint$SocketProcessor.class */
    protected class SocketProcessor extends SocketProcessorBase<Nio2Channel> {
        public SocketProcessor(SocketWrapperBase<Nio2Channel> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.tomcat.util.net.SocketProcessorBase
        protected void doRun() {
            int handshake;
            boolean launch = false;
            try {
                try {
                    try {
                        try {
                            if (((Nio2Channel) this.socketWrapper.getSocket()).isHandshakeComplete()) {
                                handshake = 0;
                            } else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                                handshake = -1;
                            } else {
                                handshake = ((Nio2Channel) this.socketWrapper.getSocket()).handshake();
                                this.event = SocketEvent.OPEN_READ;
                            }
                        } catch (Throwable t) {
                            Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.processing.fail"), t);
                            if (this.socketWrapper != null) {
                                ((Nio2SocketWrapper) this.socketWrapper).close();
                            }
                            if (0 != 0) {
                                try {
                                    Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                                } catch (NullPointerException npe) {
                                    if (Nio2Endpoint.this.running) {
                                        Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.launch.fail"), npe);
                                    }
                                }
                            }
                            this.socketWrapper = null;
                            this.event = null;
                            if (!Nio2Endpoint.this.running || Nio2Endpoint.this.processorCache == null) {
                                return;
                            }
                            Nio2Endpoint.this.processorCache.push(this);
                            return;
                        }
                    } catch (VirtualMachineError vme) {
                        ExceptionUtils.handleThrowable(vme);
                        if (0 != 0) {
                            try {
                                Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                            } catch (NullPointerException npe2) {
                                if (Nio2Endpoint.this.running) {
                                    Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.launch.fail"), npe2);
                                }
                            }
                        }
                        this.socketWrapper = null;
                        this.event = null;
                        if (!Nio2Endpoint.this.running || Nio2Endpoint.this.processorCache == null) {
                            return;
                        }
                        Nio2Endpoint.this.processorCache.push(this);
                        return;
                    }
                } catch (IOException x) {
                    handshake = -1;
                    if (Nio2Endpoint.logHandshake.isDebugEnabled()) {
                        Nio2Endpoint.logHandshake.debug(AbstractEndpoint.sm.getString("endpoint.err.handshake", this.socketWrapper.getRemoteAddr(), Integer.toString(this.socketWrapper.getRemotePort())), x);
                    }
                }
                if (handshake == 0) {
                    AbstractEndpoint.Handler.SocketState socketState = AbstractEndpoint.Handler.SocketState.OPEN;
                    AbstractEndpoint.Handler.SocketState state = this.event == null ? Nio2Endpoint.this.getHandler().process(this.socketWrapper, SocketEvent.OPEN_READ) : Nio2Endpoint.this.getHandler().process(this.socketWrapper, this.event);
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.socketWrapper.close();
                    } else if (state == AbstractEndpoint.Handler.SocketState.UPGRADING) {
                        launch = true;
                    }
                } else if (handshake == -1) {
                    Nio2Endpoint.this.getHandler().process(this.socketWrapper, SocketEvent.CONNECT_FAIL);
                    this.socketWrapper.close();
                }
                if (launch) {
                    try {
                        Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                    } catch (NullPointerException npe3) {
                        if (Nio2Endpoint.this.running) {
                            Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.launch.fail"), npe3);
                        }
                    }
                }
                this.socketWrapper = null;
                this.event = null;
                if (!Nio2Endpoint.this.running || Nio2Endpoint.this.processorCache == null) {
                    return;
                }
                Nio2Endpoint.this.processorCache.push(this);
            } catch (Throwable th) {
                if (0 != 0) {
                    try {
                        Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                    } catch (NullPointerException npe4) {
                        if (Nio2Endpoint.this.running) {
                            Nio2Endpoint.log.error(AbstractEndpoint.sm.getString("endpoint.launch.fail"), npe4);
                        }
                    }
                }
                this.socketWrapper = null;
                this.event = null;
                if (Nio2Endpoint.this.running && Nio2Endpoint.this.processorCache != null) {
                    Nio2Endpoint.this.processorCache.push(this);
                }
                throw th;
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/Nio2Endpoint$SendfileData.class */
    public static class SendfileData extends SendfileDataBase {
        private FileChannel fchannel;
        private boolean doneInline;
        private boolean error;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
            this.doneInline = false;
            this.error = false;
        }
    }
}
