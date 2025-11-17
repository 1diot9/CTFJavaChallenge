package cn.hutool.socket.nio;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.log.StaticLog;
import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/nio/AcceptHandler.class */
public class AcceptHandler implements CompletionHandler<ServerSocketChannel, NioServer> {
    @Override // java.nio.channels.CompletionHandler
    public void completed(ServerSocketChannel serverSocketChannel, NioServer nioServer) {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            StaticLog.debug("Client [{}] accepted.", socketChannel.getRemoteAddress());
            NioUtil.registerChannel(nioServer.getSelector(), socketChannel, Operation.READ);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // java.nio.channels.CompletionHandler
    public void failed(Throwable exc, NioServer nioServer) {
        StaticLog.error(exc);
    }
}
