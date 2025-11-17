package cn.hutool.http.ssl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.SSLUtil;
import cn.hutool.core.util.ArrayUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/ssl/CustomProtocolsSSLFactory.class */
public class CustomProtocolsSSLFactory extends SSLSocketFactory {
    private final String[] protocols;
    private final SSLSocketFactory base = SSLUtil.createSSLContext(null).getSocketFactory();

    public CustomProtocolsSSLFactory(String... protocols) throws IORuntimeException {
        this.protocols = protocols;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getDefaultCipherSuites() {
        return this.base.getDefaultCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getSupportedCipherSuites() {
        return this.base.getSupportedCipherSuites();
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket() throws IOException {
        SSLSocket sslSocket = (SSLSocket) this.base.createSocket();
        resetProtocols(sslSocket);
        return sslSocket;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public SSLSocket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        SSLSocket socket = (SSLSocket) this.base.createSocket(s, host, port, autoClose);
        resetProtocols(socket);
        return socket;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) this.base.createSocket(host, port);
        resetProtocols(socket);
        return socket;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        SSLSocket socket = (SSLSocket) this.base.createSocket(host, port, localHost, localPort);
        resetProtocols(socket);
        return socket;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket socket = (SSLSocket) this.base.createSocket(host, port);
        resetProtocols(socket);
        return socket;
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        SSLSocket socket = (SSLSocket) this.base.createSocket(address, port, localAddress, localPort);
        resetProtocols(socket);
        return socket;
    }

    private void resetProtocols(SSLSocket socket) {
        if (ArrayUtil.isNotEmpty((Object[]) this.protocols)) {
            socket.setEnabledProtocols(this.protocols);
        }
    }
}
