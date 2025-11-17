package cn.hutool.socket.aio;

import cn.hutool.socket.SocketRuntimeException;
import java.nio.channels.CompletionHandler;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/aio/ReadHandler.class */
public class ReadHandler implements CompletionHandler<Integer, AioSession> {
    @Override // java.nio.channels.CompletionHandler
    public void completed(Integer result, AioSession session) {
        session.callbackRead();
    }

    @Override // java.nio.channels.CompletionHandler
    public void failed(Throwable exc, AioSession session) {
        throw new SocketRuntimeException(exc);
    }
}
