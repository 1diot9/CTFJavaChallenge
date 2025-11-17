package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritePendingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.util.net.TLSClientHelloExtractor;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel.class */
public class SecureNio2Channel extends Nio2Channel {
    private static final Log log = LogFactory.getLog((Class<?>) SecureNio2Channel.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) SecureNio2Channel.class);
    private static final int DEFAULT_NET_BUFFER_SIZE = 16921;
    protected final Nio2Endpoint endpoint;
    protected ByteBuffer netInBuffer;
    protected ByteBuffer netOutBuffer;
    protected SSLEngine sslEngine;
    protected volatile boolean sniComplete;
    private volatile boolean handshakeComplete;
    private volatile SSLEngineResult.HandshakeStatus handshakeStatus;
    protected boolean closed;
    protected boolean closing;
    private final Map<String, List<String>> additionalTlsAttributes;
    private volatile boolean unwrapBeforeRead;
    private final CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> handshakeReadCompletionHandler;
    private final CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> handshakeWriteCompletionHandler;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$OverflowState.class */
    public enum OverflowState {
        NONE,
        PROCESSING,
        DONE
    }

    public SecureNio2Channel(SocketBufferHandler bufHandler, Nio2Endpoint endpoint) {
        super(bufHandler);
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.additionalTlsAttributes = new HashMap();
        this.endpoint = endpoint;
        if (endpoint.getSocketProperties().getDirectSslBuffer()) {
            this.netInBuffer = ByteBuffer.allocateDirect(DEFAULT_NET_BUFFER_SIZE);
            this.netOutBuffer = ByteBuffer.allocateDirect(DEFAULT_NET_BUFFER_SIZE);
        } else {
            this.netInBuffer = ByteBuffer.allocate(DEFAULT_NET_BUFFER_SIZE);
            this.netOutBuffer = ByteBuffer.allocate(DEFAULT_NET_BUFFER_SIZE);
        }
        this.handshakeReadCompletionHandler = new HandshakeReadCompletionHandler();
        this.handshakeWriteCompletionHandler = new HandshakeWriteCompletionHandler();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$HandshakeReadCompletionHandler.class */
    private class HandshakeReadCompletionHandler implements CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> {
        private HandshakeReadCompletionHandler() {
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer result, SocketWrapperBase<Nio2Channel> attachment) {
            if (result.intValue() < 0) {
                failed((Throwable) new EOFException(), attachment);
            } else {
                SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.OPEN_READ, false);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, SocketWrapperBase<Nio2Channel> attachment) {
            SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.ERROR, false);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$HandshakeWriteCompletionHandler.class */
    private class HandshakeWriteCompletionHandler implements CompletionHandler<Integer, SocketWrapperBase<Nio2Channel>> {
        private HandshakeWriteCompletionHandler() {
        }

        @Override // java.nio.channels.CompletionHandler
        public void completed(Integer result, SocketWrapperBase<Nio2Channel> attachment) {
            if (result.intValue() < 0) {
                failed((Throwable) new EOFException(), attachment);
            } else {
                SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.OPEN_WRITE, false);
            }
        }

        @Override // java.nio.channels.CompletionHandler
        public void failed(Throwable exc, SocketWrapperBase<Nio2Channel> attachment) {
            SecureNio2Channel.this.endpoint.processSocket(attachment, SocketEvent.ERROR, false);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public void reset(AsynchronousSocketChannel channel, SocketWrapperBase<Nio2Channel> socket) throws IOException {
        super.reset(channel, socket);
        this.sslEngine = null;
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.unwrapBeforeRead = true;
        this.closed = false;
        this.closing = false;
        this.netInBuffer.clear();
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public void free() {
        super.free();
        if (this.endpoint.getSocketProperties().getDirectSslBuffer()) {
            ByteBufferUtils.cleanDirectBuffer(this.netInBuffer);
            ByteBufferUtils.cleanDirectBuffer(this.netOutBuffer);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$FutureFlush.class */
    public class FutureFlush implements Future<Boolean> {
        private Future<Integer> integer;
        private Exception e;

        protected FutureFlush() {
            this.e = null;
            try {
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
            } catch (IllegalStateException e) {
                this.e = e;
            }
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.e != null) {
                return true;
            }
            return this.integer.cancel(mayInterruptIfRunning);
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            if (this.e != null) {
                return true;
            }
            return this.integer.isCancelled();
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            if (this.e != null) {
                return true;
            }
            return this.integer.isDone();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Boolean get() throws InterruptedException, ExecutionException {
            if (this.e != null) {
                throw new ExecutionException(this.e);
            }
            return Boolean.valueOf(this.integer.get().intValue() >= 0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.e != null) {
                throw new ExecutionException(this.e);
            }
            return Boolean.valueOf(this.integer.get(timeout, unit).intValue() >= 0);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public Future<Boolean> flush() {
        return new FutureFlush();
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public int handshake() throws IOException {
        return handshakeInternal(true);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:16:0x0040. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0211  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0226 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected int handshakeInternal(boolean r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 775
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SecureNio2Channel.handshakeInternal(boolean):int");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:13:0x00c5. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0185  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x01ff  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int processSNI() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 634
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SecureNio2Channel.processSNI():int");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.apache.tomcat.util.net.SecureNio2Channel$5, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$5.class */
    public static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus;

        static {
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.COMPLETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NOT_PRESENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NEED_READ.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.UNDERFLOW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$tomcat$util$net$TLSClientHelloExtractor$ExtractorResult[TLSClientHelloExtractor.ExtractorResult.NON_SECURE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus = new int[SSLEngineResult.HandshakeStatus.values().length];
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.FINISHED.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_WRAP.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_UNWRAP.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[SSLEngineResult.HandshakeStatus.NEED_TASK.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:27:0x00dd. Please report as an issue. */
    public void rehandshake() throws IOException {
        if (this.netInBuffer.position() > 0 && this.netInBuffer.position() < this.netInBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netInputNotEmpty"));
        }
        if (this.netOutBuffer.position() > 0 && this.netOutBuffer.position() < this.netOutBuffer.limit()) {
            throw new IOException(sm.getString("channel.nio.ssl.netOutputNotEmpty"));
        }
        if (!getBufHandler().isReadBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appInputNotEmpty"));
        }
        if (!getBufHandler().isWriteBufferEmpty()) {
            throw new IOException(sm.getString("channel.nio.ssl.appOutputNotEmpty"));
        }
        this.netOutBuffer.position(0);
        this.netOutBuffer.limit(0);
        this.netInBuffer.position(0);
        this.netInBuffer.limit(0);
        getBufHandler().reset();
        this.handshakeComplete = false;
        this.sslEngine.beginHandshake();
        this.handshakeStatus = this.sslEngine.getHandshakeStatus();
        boolean handshaking = true;
        while (handshaking) {
            try {
                int hsStatus = handshakeInternal(false);
                switch (hsStatus) {
                    case -1:
                        throw new EOFException(sm.getString("channel.nio.ssl.eofDuringHandshake"));
                    case 0:
                        handshaking = false;
                }
            } catch (IOException x) {
                closeSilently();
                throw x;
            } catch (Exception cx) {
                closeSilently();
                IOException x2 = new IOException(cx);
                throw x2;
            }
        }
    }

    protected SSLEngineResult.HandshakeStatus tasks() {
        while (true) {
            Runnable r = this.sslEngine.getDelegatedTask();
            if (r != null) {
                r.run();
            } else {
                return this.sslEngine.getHandshakeStatus();
            }
        }
    }

    protected SSLEngineResult handshakeWrap() throws IOException {
        this.netOutBuffer.clear();
        getBufHandler().configureWriteBufferForRead();
        SSLEngineResult result = this.sslEngine.wrap(getBufHandler().getWriteBuffer(), this.netOutBuffer);
        this.netOutBuffer.flip();
        this.handshakeStatus = result.getHandshakeStatus();
        return result;
    }

    protected SSLEngineResult handshakeUnwrap() throws IOException {
        SSLEngineResult result;
        boolean cont;
        do {
            this.netInBuffer.flip();
            getBufHandler().configureReadBufferForWrite();
            result = this.sslEngine.unwrap(this.netInBuffer, getBufHandler().getReadBuffer());
            this.netInBuffer.compact();
            this.handshakeStatus = result.getHandshakeStatus();
            if (result.getStatus() == SSLEngineResult.Status.OK && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.handshakeStatus = tasks();
            }
            cont = result.getStatus() == SSLEngineResult.Status.OK && this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        } while (cont);
        return result;
    }

    public SSLSupport getSSLSupport() {
        if (this.sslEngine != null) {
            SSLSession session = this.sslEngine.getSession();
            return this.endpoint.getSslImplementation().getSSLSupport(session, this.additionalTlsAttributes);
        }
        return null;
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.AsynchronousChannel, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closing) {
            return;
        }
        this.closing = true;
        if (this.sslEngine == null) {
            this.netOutBuffer.clear();
            this.closed = true;
            return;
        }
        this.sslEngine.closeOutbound();
        long timeout = this.endpoint.getConnectionTimeout();
        try {
            if (timeout > 0) {
                if (!flush().get(timeout, TimeUnit.MILLISECONDS).booleanValue()) {
                    closeSilently();
                    throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
                }
            } else if (!flush().get().booleanValue()) {
                closeSilently();
                throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
            }
            this.netOutBuffer.clear();
            SSLEngineResult handshake = this.sslEngine.wrap(getEmptyBuf(), this.netOutBuffer);
            if (handshake.getStatus() != SSLEngineResult.Status.CLOSED) {
                throw new IOException(sm.getString("channel.nio.ssl.invalidCloseState"));
            }
            this.netOutBuffer.flip();
            try {
                if (timeout > 0) {
                    if (!flush().get(timeout, TimeUnit.MILLISECONDS).booleanValue()) {
                        closeSilently();
                        throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
                    }
                } else if (!flush().get().booleanValue()) {
                    closeSilently();
                    throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"));
                }
                this.closed = (this.netOutBuffer.hasRemaining() || handshake.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP) ? false : true;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                closeSilently();
                throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"), e);
            } catch (WritePendingException e2) {
                closeSilently();
                throw new IOException(sm.getString("channel.nio.ssl.pendingWriteDuringClose"), e2);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e3) {
            closeSilently();
            throw new IOException(sm.getString("channel.nio.ssl.remainingDataDuringClose"), e3);
        } catch (WritePendingException e4) {
            closeSilently();
            throw new IOException(sm.getString("channel.nio.ssl.pendingWriteDuringClose"), e4);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public void close(boolean force) throws IOException {
        try {
            close();
            if (force || this.closed) {
                this.closed = true;
                this.sc.close();
            }
        } catch (Throwable th) {
            if (force || this.closed) {
                this.closed = true;
                this.sc.close();
            }
            throw th;
        }
    }

    private void closeSilently() {
        try {
            close(true);
        } catch (IOException ioe) {
            log.debug(sm.getString("channel.nio.ssl.closeSilentError"), ioe);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$FutureRead.class */
    private class FutureRead implements Future<Integer> {
        private ByteBuffer dst;
        private Future<Integer> integer;

        private FutureRead(ByteBuffer dst) {
            this.dst = dst;
            if (SecureNio2Channel.this.unwrapBeforeRead || SecureNio2Channel.this.netInBuffer.position() > 0) {
                this.integer = null;
            } else {
                this.integer = SecureNio2Channel.this.sc.read(SecureNio2Channel.this.netInBuffer);
            }
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.integer == null) {
                return false;
            }
            return this.integer.cancel(mayInterruptIfRunning);
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            if (this.integer == null) {
                return false;
            }
            return this.integer.isCancelled();
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            if (this.integer == null) {
                return true;
            }
            return this.integer.isDone();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get() throws InterruptedException, ExecutionException {
            try {
                return this.integer == null ? unwrap(SecureNio2Channel.this.netInBuffer.position(), -1L, TimeUnit.MILLISECONDS) : unwrap(this.integer.get().intValue(), -1L, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                throw new ExecutionException(e);
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.integer == null ? unwrap(SecureNio2Channel.this.netInBuffer.position(), timeout, unit) : unwrap(this.integer.get(timeout, unit).intValue(), timeout, unit);
        }

        /* JADX WARN: Code restructure failed: missing block: B:29:0x01dc, code lost:            if (r11.dst.hasRemaining() != false) goto L55;     */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x01df, code lost:            r11.this$0.unwrapBeforeRead = true;     */
        /* JADX WARN: Code restructure failed: missing block: B:32:0x01f7, code lost:            return java.lang.Integer.valueOf(r16);     */
        /* JADX WARN: Code restructure failed: missing block: B:33:0x01ea, code lost:            r11.this$0.unwrapBeforeRead = false;     */
        /* JADX WARN: Removed duplicated region for block: B:27:0x01d5 A[EDGE_INSN: B:27:0x01d5->B:28:0x01d5 BREAK  A[LOOP:0: B:11:0x0025->B:34:?], SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:34:? A[LOOP:0: B:11:0x0025->B:34:?, LOOP_END, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private java.lang.Integer unwrap(int r12, long r13, java.util.concurrent.TimeUnit r15) throws java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException, java.lang.InterruptedException {
            /*
                Method dump skipped, instructions count: 504
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SecureNio2Channel.FutureRead.unwrap(int, long, java.util.concurrent.TimeUnit):java.lang.Integer");
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.AsynchronousByteChannel
    public Future<Integer> read(ByteBuffer dst) {
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        return new FutureRead(dst);
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/SecureNio2Channel$FutureWrite.class */
    private class FutureWrite implements Future<Integer> {
        private final ByteBuffer src;
        private Future<Integer> integer = null;
        private int written = 0;
        private Throwable t;

        private FutureWrite(ByteBuffer src) {
            this.t = null;
            this.src = src;
            if (SecureNio2Channel.this.closing || SecureNio2Channel.this.closed) {
                this.t = new IOException(SecureNio2Channel.sm.getString("channel.nio.ssl.closing"));
            } else {
                wrap();
            }
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.integer.cancel(mayInterruptIfRunning);
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            return this.integer.isCancelled();
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            return this.integer.isDone();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get() throws InterruptedException, ExecutionException {
            if (this.t != null) {
                throw new ExecutionException(this.t);
            }
            if (this.integer.get().intValue() > 0 && this.written == 0) {
                wrap();
                return get();
            }
            if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
                return get();
            }
            return Integer.valueOf(this.written);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Future
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.t != null) {
                throw new ExecutionException(this.t);
            }
            if (this.integer.get(timeout, unit).intValue() > 0 && this.written == 0) {
                wrap();
                return get(timeout, unit);
            }
            if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
                return get(timeout, unit);
            }
            return Integer.valueOf(this.written);
        }

        protected void wrap() {
            try {
                if (!SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                    SecureNio2Channel.this.netOutBuffer.clear();
                    SSLEngineResult result = SecureNio2Channel.this.sslEngine.wrap(this.src, SecureNio2Channel.this.netOutBuffer);
                    this.written = result.bytesConsumed();
                    SecureNio2Channel.this.netOutBuffer.flip();
                    if (result.getStatus() == SSLEngineResult.Status.OK) {
                        if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            SecureNio2Channel.this.tasks();
                        }
                    } else {
                        this.t = new IOException(SecureNio2Channel.sm.getString("channel.nio.ssl.wrapFail", result.getStatus()));
                    }
                }
                this.integer = SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer);
            } catch (SSLException e) {
                this.t = e;
            }
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel, java.nio.channels.AsynchronousByteChannel
    public Future<Integer> write(ByteBuffer src) {
        return new FutureWrite(src);
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public <A> void read(final ByteBuffer dst, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Integer, ? super A> handler) {
        if (this.closing || this.closed) {
            handler.completed(-1, attachment);
            return;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        CompletionHandler<Integer, A> readCompletionHandler = new CompletionHandler<Integer, A>() { // from class: org.apache.tomcat.util.net.SecureNio2Channel.1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.nio.channels.CompletionHandler
            public /* bridge */ /* synthetic */ void completed(Integer num, Object obj) {
                completed2(num, (Integer) obj);
            }

            /* JADX WARN: Code restructure failed: missing block: B:23:0x0180, code lost:            if (r12.hasRemaining() != false) goto L41;     */
            /* JADX WARN: Code restructure failed: missing block: B:24:0x0183, code lost:            r9.this$0.unwrapBeforeRead = true;     */
            /* JADX WARN: Code restructure failed: missing block: B:25:0x0196, code lost:            r11.completed(java.lang.Integer.valueOf(r13), r11);     */
            /* JADX WARN: Code restructure failed: missing block: B:26:?, code lost:            return;     */
            /* JADX WARN: Code restructure failed: missing block: B:27:0x018e, code lost:            r9.this$0.unwrapBeforeRead = false;     */
            /* JADX WARN: Multi-variable type inference failed */
            /* JADX WARN: Removed duplicated region for block: B:21:0x017c A[EDGE_INSN: B:21:0x017c->B:22:0x017c BREAK  A[LOOP:0: B:8:0x001e->B:28:?], SYNTHETIC] */
            /* JADX WARN: Removed duplicated region for block: B:28:? A[LOOP:0: B:8:0x001e->B:28:?, LOOP_END, SYNTHETIC] */
            /* renamed from: completed, reason: avoid collision after fix types in other method */
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void completed2(java.lang.Integer r10, A r11) {
                /*
                    Method dump skipped, instructions count: 432
                    To view this dump change 'Code comments level' option to 'DEBUG'
                */
                throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.net.SecureNio2Channel.AnonymousClass1.completed2(java.lang.Integer, java.lang.Object):void");
            }

            @Override // java.nio.channels.CompletionHandler
            public void failed(Throwable exc, A attach) {
                handler.failed(exc, attach);
            }
        };
        if (this.unwrapBeforeRead || this.netInBuffer.position() > 0) {
            readCompletionHandler.completed(Integer.valueOf(this.netInBuffer.position()), attachment);
        } else {
            this.sc.read(this.netInBuffer, timeout, unit, attachment, readCompletionHandler);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public <A> void read(final ByteBuffer[] dsts, final int offset, final int length, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Long, ? super A> handler) {
        if (offset < 0 || dsts == null || offset + length > dsts.length) {
            throw new IllegalArgumentException();
        }
        if (this.closing || this.closed) {
            handler.completed(-1L, attachment);
            return;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        CompletionHandler<Integer, A> readCompletionHandler = new CompletionHandler<Integer, A>() { // from class: org.apache.tomcat.util.net.SecureNio2Channel.2
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.nio.channels.CompletionHandler
            public /* bridge */ /* synthetic */ void completed(Integer num, Object obj) {
                completed2(num, (Integer) obj);
            }

            /* JADX WARN: Multi-variable type inference failed */
            /* renamed from: completed, reason: avoid collision after fix types in other method */
            public void completed2(Integer nBytes, A attach) {
                if (nBytes.intValue() < 0) {
                    failed(new EOFException(), attach);
                    return;
                }
                try {
                    long read = 0;
                    ByteBuffer[] dsts2 = dsts;
                    int length2 = length;
                    OverflowState overflowState = OverflowState.NONE;
                    while (true) {
                        if (overflowState == OverflowState.PROCESSING) {
                            overflowState = OverflowState.DONE;
                        }
                        SecureNio2Channel.this.netInBuffer.flip();
                        SSLEngineResult unwrap = SecureNio2Channel.this.sslEngine.unwrap(SecureNio2Channel.this.netInBuffer, dsts2, offset, length2);
                        SecureNio2Channel.this.netInBuffer.compact();
                        if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                            read += unwrap.bytesProduced();
                            if (overflowState == OverflowState.DONE) {
                                read -= SecureNio2Channel.this.getBufHandler().getReadBuffer().position();
                            }
                            if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                                SecureNio2Channel.this.tasks();
                            }
                            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                if (read == 0) {
                                    SecureNio2Channel.this.sc.read(SecureNio2Channel.this.netInBuffer, timeout, unit, attachment, this);
                                    return;
                                }
                            }
                            if ((SecureNio2Channel.this.netInBuffer.position() != 0 && overflowState != OverflowState.PROCESSING) || overflowState == OverflowState.DONE) {
                                break;
                            }
                        } else {
                            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW && read > 0) {
                                break;
                            }
                            if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                                ByteBuffer readBuffer = SecureNio2Channel.this.getBufHandler().getReadBuffer();
                                boolean found = false;
                                boolean resized = true;
                                for (int i = 0; i < length2; i++) {
                                    if (dsts[offset + i] == SecureNio2Channel.this.getBufHandler().getReadBuffer()) {
                                        SecureNio2Channel.this.getBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                                        if (dsts[offset + i] == SecureNio2Channel.this.getBufHandler().getReadBuffer()) {
                                            resized = false;
                                        }
                                        dsts[offset + i] = SecureNio2Channel.this.getBufHandler().getReadBuffer();
                                        found = true;
                                    } else if (SecureNio2Channel.this.getAppReadBufHandler() != null && dsts[offset + i] == SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer()) {
                                        SecureNio2Channel.this.getAppReadBufHandler().expand(SecureNio2Channel.this.sslEngine.getSession().getApplicationBufferSize());
                                        if (dsts[offset + i] == SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer()) {
                                            resized = false;
                                        }
                                        dsts[offset + i] = SecureNio2Channel.this.getAppReadBufHandler().getByteBuffer();
                                        found = true;
                                    }
                                }
                                if (found) {
                                    if (!resized) {
                                        throw new IOException(SecureNio2Channel.sm.getString("channel.nio.ssl.unwrapFail", unwrap.getStatus()));
                                    }
                                } else {
                                    dsts2 = new ByteBuffer[dsts.length + 1];
                                    int dstOffset = 0;
                                    for (int i2 = 0; i2 < dsts.length + 1; i2++) {
                                        if (i2 == offset + length) {
                                            dsts2[i2] = readBuffer;
                                            dstOffset = -1;
                                        } else {
                                            dsts2[i2] = dsts[i2 + dstOffset];
                                        }
                                    }
                                    length2 = length + 1;
                                    SecureNio2Channel.this.getBufHandler().configureReadBufferForWrite();
                                    overflowState = OverflowState.PROCESSING;
                                }
                                if (SecureNio2Channel.this.netInBuffer.position() != 0) {
                                }
                            } else if (unwrap.getStatus() != SSLEngineResult.Status.CLOSED) {
                                throw new IOException(SecureNio2Channel.sm.getString("channel.nio.ssl.unwrapFail", unwrap.getStatus()));
                            }
                        }
                    }
                    int capacity = 0;
                    int endOffset = offset + length;
                    for (int i3 = offset; i3 < endOffset; i3++) {
                        capacity += dsts[i3].remaining();
                    }
                    if (capacity == 0) {
                        SecureNio2Channel.this.unwrapBeforeRead = true;
                    } else {
                        SecureNio2Channel.this.unwrapBeforeRead = false;
                    }
                    handler.completed(Long.valueOf(read), attach);
                } catch (Exception e) {
                    failed(e, attach);
                }
            }

            @Override // java.nio.channels.CompletionHandler
            public void failed(Throwable exc, A attach) {
                handler.failed(exc, attach);
            }
        };
        if (this.unwrapBeforeRead || this.netInBuffer.position() > 0) {
            readCompletionHandler.completed(Integer.valueOf(this.netInBuffer.position()), attachment);
        } else {
            this.sc.read(this.netInBuffer, timeout, unit, attachment, readCompletionHandler);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public <A> void write(final ByteBuffer src, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Integer, ? super A> handler) {
        if (this.closing || this.closed) {
            handler.failed(new IOException(sm.getString("channel.nio.ssl.closing")), attachment);
            return;
        }
        try {
            this.netOutBuffer.clear();
            SSLEngineResult result = this.sslEngine.wrap(src, this.netOutBuffer);
            final int written = result.bytesConsumed();
            this.netOutBuffer.flip();
            if (result.getStatus() == SSLEngineResult.Status.OK) {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    tasks();
                }
                this.sc.write(this.netOutBuffer, timeout, unit, attachment, new CompletionHandler<Integer, A>() { // from class: org.apache.tomcat.util.net.SecureNio2Channel.3
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.nio.channels.CompletionHandler
                    public /* bridge */ /* synthetic */ void completed(Integer num, Object obj) {
                        completed2(num, (Integer) obj);
                    }

                    /* JADX WARN: Multi-variable type inference failed */
                    /* renamed from: completed, reason: avoid collision after fix types in other method */
                    public void completed2(Integer nBytes, A attach) {
                        if (nBytes.intValue() < 0) {
                            failed(new EOFException(), attach);
                            return;
                        }
                        if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                            SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer, timeout, unit, attachment, this);
                        } else if (written == 0) {
                            SecureNio2Channel.this.write(src, timeout, unit, attachment, handler);
                        } else {
                            handler.completed(Integer.valueOf(written), attach);
                        }
                    }

                    @Override // java.nio.channels.CompletionHandler
                    public void failed(Throwable exc, A attach) {
                        handler.failed(exc, attach);
                    }
                });
                return;
            }
            throw new IOException(sm.getString("channel.nio.ssl.wrapFail", result.getStatus()));
        } catch (Exception e) {
            handler.failed(e, attachment);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public <A> void write(final ByteBuffer[] srcs, final int offset, final int length, final long timeout, final TimeUnit unit, final A attachment, final CompletionHandler<Long, ? super A> handler) {
        if (offset < 0 || length < 0 || offset > srcs.length - length) {
            throw new IndexOutOfBoundsException();
        }
        if (this.closing || this.closed) {
            handler.failed(new IOException(sm.getString("channel.nio.ssl.closing")), attachment);
            return;
        }
        try {
            this.netOutBuffer.clear();
            SSLEngineResult result = this.sslEngine.wrap(srcs, offset, length, this.netOutBuffer);
            final int written = result.bytesConsumed();
            this.netOutBuffer.flip();
            if (result.getStatus() == SSLEngineResult.Status.OK) {
                if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    tasks();
                }
                this.sc.write(this.netOutBuffer, timeout, unit, attachment, new CompletionHandler<Integer, A>() { // from class: org.apache.tomcat.util.net.SecureNio2Channel.4
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.nio.channels.CompletionHandler
                    public /* bridge */ /* synthetic */ void completed(Integer num, Object obj) {
                        completed2(num, (Integer) obj);
                    }

                    /* JADX WARN: Multi-variable type inference failed */
                    /* renamed from: completed, reason: avoid collision after fix types in other method */
                    public void completed2(Integer nBytes, A attach) {
                        if (nBytes.intValue() < 0) {
                            failed(new EOFException(), attach);
                            return;
                        }
                        if (SecureNio2Channel.this.netOutBuffer.hasRemaining()) {
                            SecureNio2Channel.this.sc.write(SecureNio2Channel.this.netOutBuffer, timeout, unit, attachment, this);
                        } else if (written == 0) {
                            SecureNio2Channel.this.write(srcs, offset, length, timeout, unit, attachment, handler);
                        } else {
                            handler.completed(Long.valueOf(written), attach);
                        }
                    }

                    @Override // java.nio.channels.CompletionHandler
                    public void failed(Throwable exc, A attach) {
                        handler.failed(exc, attach);
                    }
                });
                return;
            }
            throw new IOException(sm.getString("channel.nio.ssl.wrapFail", result.getStatus()));
        } catch (Exception e) {
            handler.failed(e, attachment);
        }
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }

    @Override // org.apache.tomcat.util.net.Nio2Channel
    public boolean isClosing() {
        return this.closing;
    }

    public SSLEngine getSslEngine() {
        return this.sslEngine;
    }

    public ByteBuffer getEmptyBuf() {
        return emptyBuf;
    }
}
