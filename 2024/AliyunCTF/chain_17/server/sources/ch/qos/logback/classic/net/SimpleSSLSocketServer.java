package ch.qos.logback.classic.net;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import java.security.NoSuchAlgorithmException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/SimpleSSLSocketServer.class */
public class SimpleSSLSocketServer extends SimpleSocketServer {
    private final ServerSocketFactory socketFactory;

    public static void main(String[] argv) throws Exception {
        doMain(SimpleSSLSocketServer.class, argv);
    }

    public SimpleSSLSocketServer(LoggerContext lc, int port) throws NoSuchAlgorithmException {
        this(lc, port, SSLContext.getDefault());
    }

    public SimpleSSLSocketServer(LoggerContext lc, int port, SSLContext sslContext) {
        super(lc, port);
        if (sslContext == null) {
            throw new NullPointerException("SSL context required");
        }
        SSLParametersConfiguration parameters = new SSLParametersConfiguration();
        parameters.setContext(lc);
        this.socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
    }

    @Override // ch.qos.logback.classic.net.SimpleSocketServer
    protected ServerSocketFactory getServerSocketFactory() {
        return this.socketFactory;
    }
}
