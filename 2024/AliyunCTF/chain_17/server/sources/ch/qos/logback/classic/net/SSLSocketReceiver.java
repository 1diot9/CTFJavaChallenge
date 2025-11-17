package ch.qos.logback.classic.net;

import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/SSLSocketReceiver.class */
public class SSLSocketReceiver extends SocketReceiver implements SSLComponent {
    private SSLConfiguration ssl;
    private SocketFactory socketFactory;

    @Override // ch.qos.logback.classic.net.SocketReceiver
    protected SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.net.SocketReceiver, ch.qos.logback.classic.net.ReceiverBase
    public boolean shouldStart() {
        try {
            SSLContext sslContext = getSsl().createContext(this);
            SSLParametersConfiguration parameters = getSsl().getParameters();
            parameters.setContext(getContext());
            this.socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
            return super.shouldStart();
        } catch (Exception ex) {
            addError(ex.getMessage(), ex);
            return false;
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
