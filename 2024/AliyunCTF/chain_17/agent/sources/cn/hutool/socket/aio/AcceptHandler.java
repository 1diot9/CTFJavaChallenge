package cn.hutool.socket.aio;

import cn.hutool.log.StaticLog;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/aio/AcceptHandler.class */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {
    @Override // java.nio.channels.CompletionHandler
    public void completed(AsynchronousSocketChannel socketChannel, AioServer aioServer) {
        aioServer.accept();
        IoAction<ByteBuffer> ioAction = aioServer.ioAction;
        AioSession session = new AioSession(socketChannel, ioAction, aioServer.config);
        ioAction.accept(session);
        session.read();
    }

    @Override // java.nio.channels.CompletionHandler
    public void failed(Throwable exc, AioServer aioServer) {
        StaticLog.error(exc);
    }
}
