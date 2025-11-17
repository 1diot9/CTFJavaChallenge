package cn.hutool.socket.nio;

import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/nio/NioUtil.class */
public class NioUtil {
    public static void registerChannel(Selector selector, SelectableChannel channel, Operation ops) {
        if (channel == null) {
            return;
        }
        try {
            channel.configureBlocking(false);
            channel.register(selector, ops.getValue());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
