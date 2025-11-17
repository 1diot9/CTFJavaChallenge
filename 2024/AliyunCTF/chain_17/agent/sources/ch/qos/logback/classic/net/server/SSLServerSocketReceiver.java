package ch.qos.logback.classic.net.server;

import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/server/SSLServerSocketReceiver.class */
public class SSLServerSocketReceiver extends ServerSocketReceiver implements SSLComponent {
    private SSLConfiguration ssl;
    private ServerSocketFactory socketFactory;

    @Override // ch.qos.logback.classic.net.server.ServerSocketReceiver
    protected ServerSocketFactory getServerSocketFactory() throws Exception {
        if (this.socketFactory == null) {
            SSLContext sslContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            this.socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
        }
        return this.socketFactory;
    }

    @Override // ch.qos.logback.core.net.ssl.SSLComponent
    public SSLConfiguration getSsl() {
        if (this.ssl == null) {
            this.ssl = new SSLConfiguration();
        }
        return this.ssl;
    }

    @Override // ch.qos.logback.core.net.ssl.SSLComponent
    public void setSsl(SSLConfiguration ssl) {
        this.ssl = ssl;
    }
}
