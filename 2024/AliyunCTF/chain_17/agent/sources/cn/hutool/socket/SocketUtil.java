package cn.hutool.socket;

import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/socket/SocketUtil.class */
public class SocketUtil {
    public static SocketAddress getRemoteAddress(AsynchronousSocketChannel channel) throws IORuntimeException {
        if (null == channel) {
            return null;
        }
        try {
            return channel.getRemoteAddress();
        } catch (ClosedChannelException e) {
            return null;
        } catch (IOException e2) {
            throw new IORuntimeException(e2);
        }
    }

    public static boolean isConnected(AsynchronousSocketChannel channel) throws IORuntimeException {
        return null != getRemoteAddress(channel);
    }

    public static Socket connect(String hostname, int port) throws IORuntimeException {
        return connect(hostname, port, -1);
    }

    public static Socket connect(String hostname, int port, int connectionTimeout) throws IORuntimeException {
        return connect(new InetSocketAddress(hostname, port), connectionTimeout);
    }

    public static Socket connect(InetSocketAddress address, int connectionTimeout) throws IORuntimeException {
        Socket socket = new Socket();
        try {
            if (connectionTimeout <= 0) {
                socket.connect(address);
            } else {
                socket.connect(address, connectionTimeout);
            }
            return socket;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
