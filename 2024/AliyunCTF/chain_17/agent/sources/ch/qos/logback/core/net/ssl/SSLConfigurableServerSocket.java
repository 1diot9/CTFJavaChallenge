package ch.qos.logback.core.net.ssl;

import javax.net.ssl.SSLServerSocket;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/SSLConfigurableServerSocket.class */
public class SSLConfigurableServerSocket implements SSLConfigurable {
    private final SSLServerSocket delegate;

    public SSLConfigurableServerSocket(SSLServerSocket delegate) {
        this.delegate = delegate;
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public String[] getDefaultProtocols() {
        return this.delegate.getEnabledProtocols();
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public String[] getSupportedProtocols() {
        return this.delegate.getSupportedProtocols();
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public void setEnabledProtocols(String[] protocols) {
        this.delegate.setEnabledProtocols(protocols);
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public String[] getDefaultCipherSuites() {
        return this.delegate.getEnabledCipherSuites();
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public void setEnabledCipherSuites(String[] suites) {
        this.delegate.setEnabledCipherSuites(suites);
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public void setNeedClientAuth(boolean state) {
        this.delegate.setNeedClientAuth(state);
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public void setWantClientAuth(boolean state) {
        this.delegate.setWantClientAuth(state);
    }

    @Override // ch.qos.logback.core.net.ssl.SSLConfigurable
    public void setHostnameVerification(boolean verifyHostname) {
    }
}
