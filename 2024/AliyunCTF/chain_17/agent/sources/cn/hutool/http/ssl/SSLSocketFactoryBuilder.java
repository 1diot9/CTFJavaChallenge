package cn.hutool.http.ssl;

import cn.hutool.core.net.SSLContextBuilder;
import cn.hutool.core.net.SSLProtocols;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/ssl/SSLSocketFactoryBuilder.class */
public class SSLSocketFactoryBuilder implements SSLProtocols {
    SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();

    public static SSLSocketFactoryBuilder create() {
        return new SSLSocketFactoryBuilder();
    }

    public SSLSocketFactoryBuilder setProtocol(String protocol) {
        this.sslContextBuilder.setProtocol(protocol);
        return this;
    }

    public SSLSocketFactoryBuilder setTrustManagers(TrustManager... trustManagers) {
        this.sslContextBuilder.setTrustManagers(trustManagers);
        return this;
    }

    public SSLSocketFactoryBuilder setKeyManagers(KeyManager... keyManagers) {
        this.sslContextBuilder.setKeyManagers(keyManagers);
        return this;
    }

    public SSLSocketFactoryBuilder setSecureRandom(SecureRandom secureRandom) {
        this.sslContextBuilder.setSecureRandom(secureRandom);
        return this;
    }

    public SSLSocketFactory build() throws NoSuchAlgorithmException, KeyManagementException {
        return this.sslContextBuilder.buildChecked().getSocketFactory();
    }
}
