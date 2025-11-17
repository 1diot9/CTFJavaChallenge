package org.apache.tomcat.util.net.jsse;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/net/jsse/JSSEKeyManager.class */
public final class JSSEKeyManager extends X509ExtendedKeyManager {
    private X509KeyManager delegate;
    private String serverKeyAlias;

    public JSSEKeyManager(X509KeyManager mgr, String serverKeyAlias) {
        this.delegate = mgr;
        this.serverKeyAlias = serverKeyAlias;
    }

    @Override // javax.net.ssl.X509KeyManager
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        if (this.serverKeyAlias != null) {
            return this.serverKeyAlias;
        }
        return this.delegate.chooseServerAlias(keyType, issuers, socket);
    }

    @Override // javax.net.ssl.X509ExtendedKeyManager
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        if (this.serverKeyAlias != null) {
            return this.serverKeyAlias;
        }
        return super.chooseEngineServerAlias(keyType, issuers, engine);
    }

    @Override // javax.net.ssl.X509KeyManager
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return this.delegate.chooseClientAlias(keyType, issuers, socket);
    }

    @Override // javax.net.ssl.X509KeyManager
    public X509Certificate[] getCertificateChain(String alias) {
        return this.delegate.getCertificateChain(alias);
    }

    @Override // javax.net.ssl.X509KeyManager
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return this.delegate.getClientAliases(keyType, issuers);
    }

    @Override // javax.net.ssl.X509KeyManager
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return this.delegate.getServerAliases(keyType, issuers);
    }

    @Override // javax.net.ssl.X509KeyManager
    public PrivateKey getPrivateKey(String alias) {
        return this.delegate.getPrivateKey(alias);
    }

    @Override // javax.net.ssl.X509ExtendedKeyManager
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return this.delegate.chooseClientAlias(keyType, issuers, null);
    }
}
