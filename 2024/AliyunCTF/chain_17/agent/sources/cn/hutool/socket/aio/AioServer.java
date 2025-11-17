package cn.hutool.socket.aio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.socket.SocketConfig;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/aio/AioServer.class */
public class AioServer implements Closeable {
    private static final Log log = LogFactory.get();
    private static final AcceptHandler ACCEPT_HANDLER = new AcceptHandler();
    private AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel channel;
    protected IoAction<ByteBuffer> ioAction;
    protected final SocketConfig config;

    public AioServer(int port) {
        this(new InetSocketAddress(port), new SocketConfig());
    }

    public AioServer(InetSocketAddress address, SocketConfig config) {
        this.config = config;
        init(address);
    }

    public AioServer init(InetSocketAddress address) {
        try {
            this.group = AsynchronousChannelGroup.withFixedThreadPool(this.config.getThreadPoolSize(), ThreadFactoryBuilder.create().setNamePrefix("Hutool-socket-").build());
            this.channel = AsynchronousServerSocketChannel.open(this.group).bind((SocketAddress) address);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public void start(boolean sync) {
        doStart(sync);
    }

    public <T> AioServer setOption(SocketOption<T> name, T value) throws IOException {
        this.channel.setOption((SocketOption<SocketOption<T>>) name, (SocketOption<T>) value);
        return this;
    }

    public IoAction<ByteBuffer> getIoAction() {
        return this.ioAction;
    }

    public AioServer setIoAction(IoAction<ByteBuffer> ioAction) {
        this.ioAction = ioAction;
        return this;
    }

    public AsynchronousServerSocketChannel getChannel() {
        return this.channel;
    }

    public AioServer accept() {
        this.channel.accept(this, ACCEPT_HANDLER);
        return this;
    }

    public boolean isOpen() {
        return null != this.channel && this.channel.isOpen();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.channel);
        if (null != this.group && false == this.group.isShutdown()) {
            try {
                this.group.shutdownNow();
            } catch (IOException e) {
            }
        }
        synchronized (this) {
            notify();
        }
    }

    private void doStart(boolean sync) {
        log.debug("Aio Server started, waiting for accept.", new Object[0]);
        accept();
        if (sync) {
            ThreadUtil.sync(this);
        }
    }
}
