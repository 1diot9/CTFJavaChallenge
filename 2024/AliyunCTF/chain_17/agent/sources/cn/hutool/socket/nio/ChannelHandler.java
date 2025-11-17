package cn.hutool.socket.nio;

import java.nio.channels.SocketChannel;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/nio/ChannelHandler.class */
public interface ChannelHandler {
    void handle(SocketChannel socketChannel) throws Exception;
}
