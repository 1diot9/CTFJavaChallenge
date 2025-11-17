package ch.qos.logback.core.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/ConfigurableSSLServerSocketFactory.class */
public class ConfigurableSSLServerSocketFactory extends ServerSocketFactory {
    private final SSLParametersConfiguration parameters;
    private final SSLServerSocketFactory delegate;

    public ConfigurableSSLServerSocketFactory(SSLParametersConfiguration parameters, SSLServerSocketFactory delegate) {
        this.parameters = parameters;
        this.delegate = delegate;
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) this.delegate.createServerSocket(port, backlog, ifAddress);
        this.parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) this.delegate.createServerSocket(port, backlog);
        this.parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }

    @Override // javax.net.ServerSocketFactory
    public ServerSocket createServerSocket(int port) throws IOException {
        SSLServerSocket socket = (SSLServerSocket) this.delegate.createServerSocket(port);
        this.parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }
}
