package cn.hutool.core.net;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/SSLContextBuilder.class */
public class SSLContextBuilder implements SSLProtocols, Builder<SSLContext> {
    private static final long serialVersionUID = 1;
    private KeyManager[] keyManagers;
    private String protocol = "TLS";
    private TrustManager[] trustManagers = {DefaultTrustManager.INSTANCE};
    private SecureRandom secureRandom = new SecureRandom();

    public static SSLContextBuilder create() {
        return new SSLContextBuilder();
    }

    public SSLContextBuilder setProtocol(String protocol) {
        if (StrUtil.isNotBlank(protocol)) {
            this.protocol = protocol;
        }
        return this;
    }

    public SSLContextBuilder setTrustManagers(TrustManager... trustManagers) {
        if (ArrayUtil.isNotEmpty((Object[]) trustManagers)) {
            this.trustManagers = trustManagers;
        }
        return this;
    }

    public SSLContextBuilder setKeyManagers(KeyManager... keyManagers) {
        if (ArrayUtil.isNotEmpty((Object[]) keyManagers)) {
            this.keyManagers = keyManagers;
        }
        return this;
    }

    public SSLContextBuilder setSecureRandom(SecureRandom secureRandom) {
        if (null != secureRandom) {
            this.secureRandom = secureRandom;
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public SSLContext build() {
        return buildQuietly();
    }

    public SSLContext buildChecked() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(this.protocol);
        sslContext.init(this.keyManagers, this.trustManagers, this.secureRandom);
        return sslContext;
    }

    public SSLContext buildQuietly() throws IORuntimeException {
        try {
            return buildChecked();
        } catch (GeneralSecurityException e) {
            throw new IORuntimeException(e);
        }
    }
}
