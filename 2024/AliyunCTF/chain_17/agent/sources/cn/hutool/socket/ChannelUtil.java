package cn.hutool.socket;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/ChannelUtil.class */
public class ChannelUtil {
    public static AsynchronousChannelGroup createFixedGroup(int poolSize) {
        try {
            return AsynchronousChannelGroup.withFixedThreadPool(poolSize, ThreadFactoryBuilder.create().setNamePrefix("Huool-socket-").build());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static AsynchronousSocketChannel connect(AsynchronousChannelGroup group, InetSocketAddress address) {
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open(group);
            try {
                channel.connect(address).get();
                return channel;
            } catch (InterruptedException | ExecutionException e) {
                IoUtil.close((Closeable) channel);
                throw new SocketRuntimeException(e);
            }
        } catch (IOException e2) {
            throw new IORuntimeException(e2);
        }
    }
}
