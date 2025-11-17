package cn.hutool.socket.protocol;

import cn.hutool.socket.aio.AioSession;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/protocol/MsgDecoder.class */
public interface MsgDecoder<T> {
    T decode(AioSession aioSession, ByteBuffer byteBuffer);
}
