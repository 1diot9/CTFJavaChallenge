package cn.hutool.socket.nio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.socket.SocketRuntimeException;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/nio/NioClient.class */
public class NioClient implements Closeable {
    private static final Log log = Log.get();
    private Selector selector;
    private SocketChannel channel;
    private ChannelHandler handler;

    public NioClient(String host, int port) {
        init(new InetSocketAddress(host, port));
    }

    public NioClient(InetSocketAddress address) {
        init(address);
    }

    public NioClient init(InetSocketAddress address) {
        try {
            this.channel = SocketChannel.open();
            this.channel.configureBlocking(false);
            this.channel.connect(address);
            this.selector = Selector.open();
            this.channel.register(this.selector, 1);
            do {
            } while (false == this.channel.finishConnect());
            return this;
        } catch (IOException e) {
            close();
            throw new IORuntimeException(e);
        }
    }

    public NioClient setChannelHandler(ChannelHandler handler) {
        this.handler = handler;
        return this;
    }

    public void listen() {
        ThreadUtil.execute(() -> {
            try {
                doListen();
            } catch (IOException e) {
                log.error("Listen failed", e);
            }
        });
    }

    private void doListen() throws IOException {
        while (this.selector.isOpen() && 0 != this.selector.select()) {
            Iterator<SelectionKey> keyIter = this.selector.selectedKeys().iterator();
            while (keyIter.hasNext()) {
                handle(keyIter.next());
                keyIter.remove();
            }
        }
    }

    private void handle(SelectionKey key) {
        if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            try {
                this.handler.handle(socketChannel);
            } catch (Exception e) {
                throw new SocketRuntimeException(e);
            }
        }
    }

    public NioClient write(ByteBuffer... datas) {
        try {
            this.channel.write(datas);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public SocketChannel getChannel() {
        return this.channel;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        IoUtil.close((Closeable) this.selector);
        IoUtil.close((Closeable) this.channel);
    }
}
