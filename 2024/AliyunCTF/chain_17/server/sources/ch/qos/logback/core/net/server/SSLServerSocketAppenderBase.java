package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/SSLServerSocketAppenderBase.class */
public abstract class SSLServerSocketAppenderBase<E> extends AbstractServerSocketAppender<E> implements SSLComponent {
    private SSLConfiguration ssl;
    private ServerSocketFactory socketFactory;

    @Override // ch.qos.logback.core.net.server.AbstractServerSocketAppender
    protected ServerSocketFactory getServerSocketFactory() {
        return this.socketFactory;
    }

    @Override // ch.qos.logback.core.net.server.AbstractServerSocketAppender, ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        try {
            SSLContext sslContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            this.socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
            super.start();
        } catch (Exception ex) {
            addError(ex.getMessage(), ex);
        }
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
