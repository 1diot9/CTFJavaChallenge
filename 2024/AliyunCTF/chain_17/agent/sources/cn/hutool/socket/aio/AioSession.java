package cn.hutool.socket.aio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.socket.SocketConfig;
import cn.hutool.socket.SocketUtil;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/aio/AioSession.class */
public class AioSession implements Closeable {
    private static final ReadHandler READ_HANDLER = new ReadHandler();
    private final AsynchronousSocketChannel channel;
    private final IoAction<ByteBuffer> ioAction;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private final long readTimeout;
    private final long writeTimeout;

    public AioSession(AsynchronousSocketChannel channel, IoAction<ByteBuffer> ioAction, SocketConfig config) {
        this.channel = channel;
        this.ioAction = ioAction;
        this.readBuffer = ByteBuffer.allocate(config.getReadBufferSize());
        this.writeBuffer = ByteBuffer.allocate(config.getWriteBufferSize());
        this.readTimeout = config.getReadTimeout();
        this.writeTimeout = config.getWriteTimeout();
    }

    public AsynchronousSocketChannel getChannel() {
        return this.channel;
    }

    public ByteBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public ByteBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public IoAction<ByteBuffer> getIoAction() {
        return this.ioAction;
    }

    public SocketAddress getRemoteAddress() {
        return SocketUtil.getRemoteAddress(this.channel);
    }

    public AioSession read() {
        return read(READ_HANDLER);
    }

    public AioSession read(CompletionHandler<Integer, AioSession> handler) {
        if (isOpen()) {
            this.readBuffer.clear();
            this.channel.read(this.readBuffer, Math.max(this.readTimeout, 0L), TimeUnit.MILLISECONDS, this, handler);
        }
        return this;
    }

    public AioSession writeAndClose(ByteBuffer data) {
        write(data);
        return closeOut();
    }

    public Future<Integer> write(ByteBuffer data) {
        return this.channel.write(data);
    }

    public AioSession write(ByteBuffer data, CompletionHandler<Integer, AioSession> handler) {
        this.channel.write(data, Math.max(this.writeTimeout, 0L), TimeUnit.MILLISECONDS, this, handler);
        return this;
    }

    public boolean isOpen() {
        return null != this.channel && this.channel.isOpen();
    }

    public AioSession closeIn() {
        if (null != this.channel) {
            try {
                this.channel.shutdownInput();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return this;
    }

    public AioSession closeOut() {
        if (null != this.channel) {
            try {
                this.channel.shutdownOutput();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return this;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.channel);
        this.readBuffer = null;
        this.writeBuffer = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void callbackRead() {
        this.readBuffer.flip();
        this.ioAction.doAction(this, this.readBuffer);
    }
}
