package org.apache.tomcat.util.net;

import jakarta.servlet.ServletConnection;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase.class */
public abstract class SocketWrapperBase<E> {
    private E socket;
    private final AbstractEndpoint<E, ?> endpoint;
    private final String connectionId;
    protected final Semaphore readPending;
    protected final Semaphore writePending;
    private static final Log log = LogFactory.getLog((Class<?>) SocketWrapperBase.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) SocketWrapperBase.class);
    private static final AtomicLong connectionIdGenerator = new AtomicLong(0);
    public static final CompletionCheck COMPLETE_WRITE = new CompletionCheck() { // from class: org.apache.tomcat.util.net.SocketWrapperBase.1
        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            for (int i = 0; i < length; i++) {
                if (buffers[offset + i].hasRemaining()) {
                    return CompletionHandlerCall.CONTINUE;
                }
            }
            return state == CompletionState.DONE ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
        }
    };
    public static final CompletionCheck COMPLETE_WRITE_WITH_COMPLETION = new CompletionCheck() { // from class: org.apache.tomcat.util.net.SocketWrapperBase.2
        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            for (int i = 0; i < length; i++) {
                if (buffers[offset + i].hasRemaining()) {
                    return CompletionHandlerCall.CONTINUE;
                }
            }
            return CompletionHandlerCall.DONE;
        }
    };
    public static final CompletionCheck READ_DATA = new CompletionCheck() { // from class: org.apache.tomcat.util.net.SocketWrapperBase.3
        @Override // org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            return state == CompletionState.DONE ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
        }
    };
    public static final CompletionCheck COMPLETE_READ_WITH_COMPLETION = COMPLETE_WRITE_WITH_COMPLETION;
    public static final CompletionCheck COMPLETE_READ = COMPLETE_WRITE;
    private final Lock lock = new ReentrantLock();
    protected final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile long readTimeout = -1;
    private volatile long writeTimeout = -1;
    protected volatile IOException previousIOException = null;
    private volatile int keepAliveLeft = 100;
    private String negotiatedProtocol = null;
    protected String localAddr = null;
    protected String localName = null;
    protected int localPort = -1;
    protected String remoteAddr = null;
    protected String remoteHost = null;
    protected int remotePort = -1;
    protected volatile ServletConnection servletConnection = null;
    private volatile IOException error = null;
    protected volatile SocketBufferHandler socketBufferHandler = null;
    protected int bufferedWriteSize = 65536;
    protected final WriteBuffer nonBlockingWriteBuffer = new WriteBuffer(this.bufferedWriteSize);
    protected volatile SocketWrapperBase<E>.OperationState<?> readOperation = null;
    protected volatile SocketWrapperBase<E>.OperationState<?> writeOperation = null;
    private final AtomicReference<Object> currentProcessor = new AtomicReference<>();

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase$BlockingMode.class */
    public enum BlockingMode {
        CLASSIC,
        NON_BLOCK,
        SEMI_BLOCK,
        BLOCK
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase$CompletionCheck.class */
    public interface CompletionCheck {
        CompletionHandlerCall callHandler(CompletionState completionState, ByteBuffer[] byteBufferArr, int i, int i2);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase$CompletionHandlerCall.class */
    public enum CompletionHandlerCall {
        CONTINUE,
        NONE,
        DONE
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase$CompletionState.class */
    public enum CompletionState {
        PENDING,
        NOT_DONE,
        INLINE,
        ERROR,
        DONE
    }

    protected abstract void populateRemoteHost();

    protected abstract void populateRemoteAddr();

    protected abstract void populateRemotePort();

    protected abstract void populateLocalName();

    protected abstract void populateLocalAddr();

    protected abstract void populateLocalPort();

    public abstract int read(boolean z, byte[] bArr, int i, int i2) throws IOException;

    public abstract int read(boolean z, ByteBuffer byteBuffer) throws IOException;

    public abstract boolean isReadyForRead() throws IOException;

    public abstract void setAppReadBufHandler(ApplicationBufferHandler applicationBufferHandler);

    protected abstract void doClose();

    protected abstract boolean flushNonBlocking() throws IOException;

    protected abstract void doWrite(boolean z, ByteBuffer byteBuffer) throws IOException;

    public abstract void registerReadInterest();

    public abstract void registerWriteInterest();

    public abstract SendfileDataBase createSendfileData(String str, long j, long j2);

    public abstract SendfileState processSendfile(SendfileDataBase sendfileDataBase);

    public abstract void doClientAuth(SSLSupport sSLSupport) throws IOException;

    public abstract SSLSupport getSslSupport();

    protected abstract <A> SocketWrapperBase<E>.OperationState<A> newOperationState(boolean z, ByteBuffer[] byteBufferArr, int i, int i2, BlockingMode blockingMode, long j, TimeUnit timeUnit, A a, CompletionCheck completionCheck, CompletionHandler<Long, ? super A> completionHandler, Semaphore semaphore, SocketWrapperBase<E>.VectoredIOCompletionHandler<A> vectoredIOCompletionHandler);

    public SocketWrapperBase(E socket, AbstractEndpoint<E, ?> endpoint) {
        this.socket = socket;
        this.endpoint = endpoint;
        if (endpoint.getUseAsyncIO() || needSemaphores()) {
            this.readPending = new Semaphore(1);
            this.writePending = new Semaphore(1);
        } else {
            this.readPending = null;
            this.writePending = null;
        }
        this.connectionId = Long.toHexString(connectionIdGenerator.getAndIncrement());
    }

    public E getSocket() {
        return this.socket;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reset(E closedSocket) {
        this.socket = closedSocket;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractEndpoint<E, ?> getEndpoint() {
        return this.endpoint;
    }

    public Lock getLock() {
        return this.lock;
    }

    public Object getCurrentProcessor() {
        return this.currentProcessor.get();
    }

    public void setCurrentProcessor(Object currentProcessor) {
        this.currentProcessor.set(currentProcessor);
    }

    public Object takeCurrentProcessor() {
        return this.currentProcessor.getAndSet(null);
    }

    public void execute(Runnable runnable) {
        Executor executor = this.endpoint.getExecutor();
        if (!this.endpoint.isRunning() || executor == null) {
            throw new RejectedExecutionException();
        }
        executor.execute(runnable);
    }

    public IOException getError() {
        return this.error;
    }

    public void setError(IOException error) {
        if (this.error != null) {
            return;
        }
        this.error = error;
    }

    public void checkError() throws IOException {
        if (this.error != null) {
            throw this.error;
        }
    }

    public String getNegotiatedProtocol() {
        return this.negotiatedProtocol;
    }

    public void setNegotiatedProtocol(String negotiatedProtocol) {
        this.negotiatedProtocol = negotiatedProtocol;
    }

    public void setReadTimeout(long readTimeout) {
        if (readTimeout > 0) {
            this.readTimeout = readTimeout;
        } else {
            this.readTimeout = -1L;
        }
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        if (writeTimeout > 0) {
            this.writeTimeout = writeTimeout;
        } else {
            this.writeTimeout = -1L;
        }
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setKeepAliveLeft(int keepAliveLeft) {
        this.keepAliveLeft = keepAliveLeft;
    }

    public int decrementKeepAlive() {
        int i = this.keepAliveLeft - 1;
        this.keepAliveLeft = i;
        return i;
    }

    public String getRemoteHost() {
        if (this.remoteHost == null) {
            populateRemoteHost();
        }
        return this.remoteHost;
    }

    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            populateRemoteAddr();
        }
        return this.remoteAddr;
    }

    public int getRemotePort() {
        if (this.remotePort == -1) {
            populateRemotePort();
        }
        return this.remotePort;
    }

    public String getLocalName() {
        if (this.localName == null) {
            populateLocalName();
        }
        return this.localName;
    }

    public String getLocalAddr() {
        if (this.localAddr == null) {
            populateLocalAddr();
        }
        return this.localAddr;
    }

    public int getLocalPort() {
        if (this.localPort == -1) {
            populateLocalPort();
        }
        return this.localPort;
    }

    public SocketBufferHandler getSocketBufferHandler() {
        return this.socketBufferHandler;
    }

    public boolean hasDataToRead() {
        return true;
    }

    public boolean hasDataToWrite() {
        return (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty()) ? false : true;
    }

    public boolean isReadyForWrite() {
        boolean result = canWrite();
        if (!result) {
            registerWriteInterest();
        }
        return result;
    }

    public boolean canWrite() {
        if (this.socketBufferHandler == null) {
            throw new IllegalStateException(sm.getString("socket.closed"));
        }
        return this.socketBufferHandler.isWriteBufferWritable() && this.nonBlockingWriteBuffer.isEmpty();
    }

    public String toString() {
        return super.toString() + ":" + String.valueOf(this.socket);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int populateReadBuffer(byte[] b, int off, int len) {
        this.socketBufferHandler.configureReadBufferForRead();
        ByteBuffer readBuffer = this.socketBufferHandler.getReadBuffer();
        int remaining = readBuffer.remaining();
        if (remaining > 0) {
            remaining = Math.min(remaining, len);
            readBuffer.get(b, off, remaining);
            if (log.isDebugEnabled()) {
                log.debug("Socket: [" + this + "], Read from buffer: [" + remaining + "]");
            }
        }
        return remaining;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int populateReadBuffer(ByteBuffer to) {
        this.socketBufferHandler.configureReadBufferForRead();
        int nRead = transfer(this.socketBufferHandler.getReadBuffer(), to);
        if (log.isDebugEnabled()) {
            log.debug("Socket: [" + this + "], Read from buffer: [" + nRead + "]");
        }
        return nRead;
    }

    public void unRead(ByteBuffer returnedInput) {
        if (returnedInput != null) {
            this.socketBufferHandler.unReadReadBuffer(returnedInput);
        }
    }

    public void close() {
        try {
            if (this.closed.compareAndSet(false, true)) {
                getEndpoint().getHandler().release(this);
            }
        } catch (Throwable e) {
            ExceptionUtils.handleThrowable(e);
            if (log.isDebugEnabled()) {
                log.error(sm.getString("endpoint.debug.handlerRelease"), e);
            }
        } finally {
            getEndpoint().countDownConnection();
            doClose();
        }
    }

    public boolean isClosed() {
        return this.closed.get();
    }

    public final void write(boolean block, byte[] buf, int off, int len) throws IOException {
        if (len == 0 || buf == null) {
            return;
        }
        if (block) {
            writeBlocking(buf, off, len);
        } else {
            writeNonBlocking(buf, off, len);
        }
    }

    public final void write(boolean block, ByteBuffer from) throws IOException {
        if (from == null || from.remaining() == 0) {
            return;
        }
        if (block) {
            writeBlocking(from);
        } else {
            writeNonBlocking(from);
        }
    }

    protected void writeBlocking(byte[] buf, int off, int len) throws IOException {
        if (len > 0) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            while (true) {
                int thisTime = transfer;
                len -= thisTime;
                if (len > 0) {
                    off += thisTime;
                    doWrite(true);
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeBlocking(ByteBuffer from) throws IOException {
        if (from.hasRemaining()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            transfer(from, this.socketBufferHandler.getWriteBuffer());
            while (from.hasRemaining()) {
                doWrite(true);
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, this.socketBufferHandler.getWriteBuffer());
            }
        }
    }

    protected void writeNonBlocking(byte[] buf, int off, int len) throws IOException {
        if (len > 0 && this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            while (true) {
                int thisTime = transfer;
                len -= thisTime;
                if (len <= 0) {
                    break;
                }
                off += thisTime;
                doWrite(false);
                if (len <= 0 || !this.socketBufferHandler.isWriteBufferWritable()) {
                    break;
                }
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer = transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            }
        }
        if (len > 0) {
            this.nonBlockingWriteBuffer.add(buf, off, len);
        }
    }

    protected void writeNonBlocking(ByteBuffer from) throws IOException {
        if (from.hasRemaining() && this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            writeNonBlockingInternal(from);
        }
        if (from.hasRemaining()) {
            this.nonBlockingWriteBuffer.add(from);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeNonBlockingInternal(ByteBuffer from) throws IOException {
        this.socketBufferHandler.configureWriteBufferForWrite();
        transfer(from, this.socketBufferHandler.getWriteBuffer());
        while (from.hasRemaining()) {
            doWrite(false);
            if (this.socketBufferHandler.isWriteBufferWritable()) {
                this.socketBufferHandler.configureWriteBufferForWrite();
                transfer(from, this.socketBufferHandler.getWriteBuffer());
            } else {
                return;
            }
        }
    }

    public boolean flush(boolean block) throws IOException {
        boolean result = false;
        if (block) {
            flushBlocking();
        } else {
            result = flushNonBlocking();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flushBlocking() throws IOException {
        doWrite(true);
        if (!this.nonBlockingWriteBuffer.isEmpty()) {
            this.nonBlockingWriteBuffer.write((SocketWrapperBase<?>) this, true);
            if (!this.socketBufferHandler.isWriteBufferEmpty()) {
                doWrite(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doWrite(boolean block) throws IOException {
        this.socketBufferHandler.configureWriteBufferForRead();
        doWrite(block, this.socketBufferHandler.getWriteBuffer());
    }

    public void processSocket(SocketEvent socketStatus, boolean dispatch) {
        this.endpoint.processSocket(this, socketStatus, dispatch);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase$OperationState.class */
    public abstract class OperationState<A> implements Runnable {
        protected final boolean read;
        protected final ByteBuffer[] buffers;
        protected final int offset;
        protected final int length;
        protected final A attachment;
        protected final long timeout;
        protected final TimeUnit unit;
        protected final BlockingMode block;
        protected final CompletionCheck check;
        protected final CompletionHandler<Long, ? super A> handler;
        protected final Semaphore semaphore;
        protected final SocketWrapperBase<E>.VectoredIOCompletionHandler<A> completion;
        protected final AtomicBoolean callHandler;
        protected volatile long nBytes = 0;
        protected volatile CompletionState state = CompletionState.PENDING;
        protected boolean completionDone = true;

        protected abstract boolean isInline();

        /* JADX INFO: Access modifiers changed from: protected */
        public OperationState(boolean read, ByteBuffer[] buffers, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase<E>.VectoredIOCompletionHandler<A> completion) {
            this.read = read;
            this.buffers = buffers;
            this.offset = offset;
            this.length = length;
            this.block = block;
            this.timeout = timeout;
            this.unit = unit;
            this.attachment = attachment;
            this.check = check;
            this.handler = handler;
            this.semaphore = semaphore;
            this.completion = completion;
            this.callHandler = handler != null ? new AtomicBoolean(true) : null;
        }

        protected boolean hasOutboundRemaining() {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public boolean process() {
            try {
                SocketWrapperBase.this.getEndpoint().getExecutor().execute(this);
                return true;
            } catch (RejectedExecutionException ree) {
                SocketWrapperBase.log.warn(SocketWrapperBase.sm.getString("endpoint.executor.fail", SocketWrapperBase.this), ree);
                return false;
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                SocketWrapperBase.log.error(SocketWrapperBase.sm.getString("endpoint.process.fail"), t);
                return false;
            }
        }

        protected void start() {
            run();
        }

        protected void end() {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SocketWrapperBase$VectoredIOCompletionHandler.class */
    public class VectoredIOCompletionHandler<A> implements CompletionHandler<Long, SocketWrapperBase<E>.OperationState<A>> {
        protected VectoredIOCompletionHandler() {
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Long l, SocketWrapperBase<E>.OperationState<A> operationState) {
            if (l.longValue() < 0) {
                failed((Throwable) new EOFException(), (OperationState) operationState);
                return;
            }
            operationState.nBytes += l.longValue();
            CompletionState completionState = operationState.isInline() ? CompletionState.INLINE : CompletionState.DONE;
            boolean z = true;
            boolean z2 = true;
            if (operationState.check != null) {
                CompletionHandlerCall callHandler = operationState.check.callHandler(completionState, operationState.buffers, operationState.offset, operationState.length);
                if (callHandler == CompletionHandlerCall.CONTINUE || (!operationState.read && operationState.hasOutboundRemaining())) {
                    z = false;
                } else if (callHandler == CompletionHandlerCall.NONE) {
                    z2 = false;
                }
            }
            if (z) {
                boolean z3 = false;
                if (operationState.read) {
                    SocketWrapperBase.this.readOperation = null;
                } else {
                    SocketWrapperBase.this.writeOperation = null;
                }
                operationState.semaphore.release();
                if (operationState.block == BlockingMode.BLOCK && completionState != CompletionState.INLINE) {
                    z3 = true;
                } else {
                    operationState.state = completionState;
                }
                operationState.end();
                if (z2 && operationState.handler != null && operationState.callHandler.compareAndSet(true, false)) {
                    operationState.handler.completed(Long.valueOf(operationState.nBytes), operationState.attachment);
                }
                synchronized (operationState) {
                    operationState.completionDone = true;
                    if (z3) {
                        operationState.state = completionState;
                        operationState.notify();
                    }
                }
                return;
            }
            synchronized (operationState) {
                operationState.completionDone = true;
            }
            operationState.run();
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable th, SocketWrapperBase<E>.OperationState<A> operationState) {
            IOException iOException = null;
            if (th instanceof InterruptedByTimeoutException) {
                iOException = new SocketTimeoutException();
                th = iOException;
            } else if (th instanceof IOException) {
                iOException = (IOException) th;
            }
            SocketWrapperBase.this.setError(iOException);
            boolean z = false;
            if (operationState.read) {
                SocketWrapperBase.this.readOperation = null;
            } else {
                SocketWrapperBase.this.writeOperation = null;
            }
            operationState.semaphore.release();
            if (operationState.block == BlockingMode.BLOCK) {
                z = true;
            } else {
                operationState.state = operationState.isInline() ? CompletionState.ERROR : CompletionState.DONE;
            }
            operationState.end();
            if (operationState.handler != null && operationState.callHandler.compareAndSet(true, false)) {
                operationState.handler.failed(th, operationState.attachment);
            }
            synchronized (operationState) {
                operationState.completionDone = true;
                if (z) {
                    operationState.state = operationState.isInline() ? CompletionState.ERROR : CompletionState.DONE;
                    operationState.notify();
                }
            }
        }
    }

    public boolean hasAsyncIO() {
        return this.readPending != null;
    }

    public boolean needSemaphores() {
        return false;
    }

    public boolean hasPerOperationTimeout() {
        return false;
    }

    public boolean isReadPending() {
        return false;
    }

    public boolean isWritePending() {
        return false;
    }

    public final <A> CompletionState read(long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler, ByteBuffer... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return read(dsts, 0, dsts.length, BlockingMode.CLASSIC, timeout, unit, attachment, null, handler);
    }

    public final <A> CompletionState read(BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, ByteBuffer... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return read(dsts, 0, dsts.length, block, timeout, unit, attachment, check, handler);
    }

    public final <A> CompletionState read(ByteBuffer[] dsts, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        return vectoredOperation(true, dsts, offset, length, block, timeout, unit, attachment, check, handler);
    }

    public final <A> CompletionState write(long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler, ByteBuffer... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return write(srcs, 0, srcs.length, BlockingMode.CLASSIC, timeout, unit, attachment, null, handler);
    }

    public final <A> CompletionState write(BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, ByteBuffer... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return write(srcs, 0, srcs.length, block, timeout, unit, attachment, check, handler);
    }

    public final <A> CompletionState write(ByteBuffer[] srcs, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        return vectoredOperation(false, srcs, offset, length, block, timeout, unit, attachment, check, handler);
    }

    /* JADX WARN: Code restructure failed: missing block: B:89:0x00ad, code lost:            if (r15.writePending.tryAcquire(r21, r23) == false) goto L36;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected final <A> org.apache.tomcat.util.net.SocketWrapperBase.CompletionState vectoredOperation(boolean r16, java.nio.ByteBuffer[] r17, int r18, int r19, org.apache.tomcat.util.net.SocketWrapperBase.BlockingMode r20, long r21, java.util.concurrent.TimeUnit r23, A r24, org.apache.tomcat.util.net.SocketWrapperBase.CompletionCheck r25, java.nio.channels.CompletionHandler<java.lang.Long, ? super A> r26) {
        /*
            Method dump skipped, instructions count: 528
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SocketWrapperBase.vectoredOperation(boolean, java.nio.ByteBuffer[], int, int, org.apache.tomcat.util.net.SocketWrapperBase$BlockingMode, long, java.util.concurrent.TimeUnit, java.lang.Object, org.apache.tomcat.util.net.SocketWrapperBase$CompletionCheck, java.nio.channels.CompletionHandler):org.apache.tomcat.util.net.SocketWrapperBase$CompletionState");
    }

    private String getTimeoutMsg(boolean read) {
        if (read) {
            return sm.getString("socketWrapper.readTimeout");
        }
        return sm.getString("socketWrapper.writeTimeout");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int transfer(byte[] from, int offset, int length, ByteBuffer to) {
        int max = Math.min(length, to.remaining());
        if (max > 0) {
            to.put(from, offset, max);
        }
        return max;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int transfer(ByteBuffer from, ByteBuffer to) {
        int max = Math.min(from.remaining(), to.remaining());
        if (max > 0) {
            int fromLimit = from.limit();
            from.limit(from.position() + max);
            to.put(from);
            from.limit(fromLimit);
        }
        return max;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean buffersArrayHasRemaining(ByteBuffer[] buffers, int offset, int length) {
        for (int pos = offset; pos < offset + length; pos++) {
            if (buffers[pos].hasRemaining()) {
                return true;
            }
        }
        return false;
    }

    public ServletConnection getServletConnection(String protocol, String protocolConnectionId) {
        if (this.servletConnection == null) {
            this.servletConnection = new ServletConnectionImpl(this.connectionId, protocol, protocolConnectionId, this.endpoint.isSSLEnabled());
        }
        return this.servletConnection;
    }
}
