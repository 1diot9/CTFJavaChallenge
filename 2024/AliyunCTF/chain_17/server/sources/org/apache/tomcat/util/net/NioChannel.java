package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SocketChannel;
import org.apache.tomcat.util.net.NioEndpoint;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/NioChannel.class */
public class NioChannel implements ByteChannel, ScatteringByteChannel, GatheringByteChannel {
    protected final SocketBufferHandler bufHandler;
    protected SocketChannel sc = null;
    protected NioEndpoint.NioSocketWrapper socketWrapper = null;
    private ApplicationBufferHandler appReadBufHandler;
    protected static final StringManager sm = StringManager.getManager((Class<?>) NioChannel.class);
    protected static final ByteBuffer emptyBuf = ByteBuffer.allocate(0);
    static final NioChannel CLOSED_NIO_CHANNEL = new NioChannel(SocketBufferHandler.EMPTY) { // from class: org.apache.tomcat.util.net.NioChannel.1
        @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.Channel
        public boolean isOpen() {
            return false;
        }

        @Override // org.apache.tomcat.util.net.NioChannel
        public void reset(SocketChannel channel, NioEndpoint.NioSocketWrapper socketWrapper) throws IOException {
        }

        @Override // org.apache.tomcat.util.net.NioChannel
        public void free() {
        }

        @Override // org.apache.tomcat.util.net.NioChannel
        protected ApplicationBufferHandler getAppReadBufHandler() {
            return ApplicationBufferHandler.EMPTY;
        }

        @Override // org.apache.tomcat.util.net.NioChannel
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        }

        @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.ReadableByteChannel
        public int read(ByteBuffer dst) throws IOException {
            return -1;
        }

        @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.ScatteringByteChannel
        public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
            return -1L;
        }

        @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.WritableByteChannel
        public int write(ByteBuffer src) throws IOException {
            checkInterruptStatus();
            throw new ClosedChannelException();
        }

        @Override // org.apache.tomcat.util.net.NioChannel, java.nio.channels.GatheringByteChannel
        public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
            throw new ClosedChannelException();
        }

        @Override // org.apache.tomcat.util.net.NioChannel
        public String toString() {
            return "Closed NioChannel";
        }
    };

    public NioChannel(SocketBufferHandler bufHandler) {
        this.bufHandler = bufHandler;
    }

    public void reset(SocketChannel channel, NioEndpoint.NioSocketWrapper socketWrapper) throws IOException {
        this.sc = channel;
        this.socketWrapper = socketWrapper;
        this.bufHandler.reset();
    }

    NioEndpoint.NioSocketWrapper getSocketWrapper() {
        return this.socketWrapper;
    }

    public void free() {
        this.bufHandler.free();
    }

    @Override // java.nio.channels.Channel, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.sc.close();
    }

    public void close(boolean force) throws IOException {
        if (isOpen() || force) {
            close();
        }
    }

    @Override // java.nio.channels.Channel
    public boolean isOpen() {
        return this.sc.isOpen();
    }

    @Override // java.nio.channels.WritableByteChannel
    public int write(ByteBuffer src) throws IOException {
        checkInterruptStatus();
        return this.sc.write(src);
    }

    @Override // java.nio.channels.GatheringByteChannel
    public long write(ByteBuffer[] srcs) throws IOException {
        return write(srcs, 0, srcs.length);
    }

    @Override // java.nio.channels.GatheringByteChannel
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        checkInterruptStatus();
        return this.sc.write(srcs, offset, length);
    }

    @Override // java.nio.channels.ReadableByteChannel
    public int read(ByteBuffer dst) throws IOException {
        return this.sc.read(dst);
    }

    @Override // java.nio.channels.ScatteringByteChannel
    public long read(ByteBuffer[] dsts) throws IOException {
        return read(dsts, 0, dsts.length);
    }

    @Override // java.nio.channels.ScatteringByteChannel
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        return this.sc.read(dsts, offset, length);
    }

    public SocketBufferHandler getBufHandler() {
        return this.bufHandler;
    }

    public SocketChannel getIOChannel() {
        return this.sc;
    }

    public boolean isClosing() {
        return false;
    }

    public boolean isHandshakeComplete() {
        return true;
    }

    public int handshake(boolean read, boolean write) throws IOException {
        return 0;
    }

    public String toString() {
        return super.toString() + ":" + this.sc;
    }

    public int getOutboundRemaining() {
        return 0;
    }

    public boolean flushOutbound() throws IOException {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkInterruptStatus() throws IOException {
        if (Thread.interrupted()) {
            throw new IOException(sm.getString("channel.nio.interrupted"));
        }
    }

    public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        this.appReadBufHandler = handler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ApplicationBufferHandler getAppReadBufHandler() {
        return this.appReadBufHandler;
    }
}
