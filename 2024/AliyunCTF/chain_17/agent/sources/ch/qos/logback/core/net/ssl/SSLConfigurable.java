package ch.qos.logback.core.net.ssl;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/ssl/SSLConfigurable.class */
public interface SSLConfigurable {
    String[] getDefaultProtocols();

    String[] getSupportedProtocols();

    void setEnabledProtocols(String[] strArr);

    String[] getDefaultCipherSuites();

    String[] getSupportedCipherSuites();

    void setEnabledCipherSuites(String[] strArr);

    void setNeedClientAuth(boolean z);

    void setWantClientAuth(boolean z);

    void setHostnameVerification(boolean z);
}
