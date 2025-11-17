package cn.hutool.socket.aio;

import cn.hutool.log.StaticLog;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/aio/SimpleIoAction.class */
public abstract class SimpleIoAction implements IoAction<ByteBuffer> {
    @Override // cn.hutool.socket.aio.IoAction
    public void accept(AioSession session) {
    }

    @Override // cn.hutool.socket.aio.IoAction
    public void failed(Throwable exc, AioSession session) {
        StaticLog.error(exc);
    }
}
