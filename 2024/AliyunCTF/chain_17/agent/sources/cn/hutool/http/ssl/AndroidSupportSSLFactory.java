package cn.hutool.http.ssl;

import cn.hutool.core.io.IORuntimeException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/ssl/AndroidSupportSSLFactory.class */
public class AndroidSupportSSLFactory extends CustomProtocolsSSLFactory {
    private static final String[] protocols = {"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};

    public AndroidSupportSSLFactory() throws IORuntimeException {
        super(protocols);
    }
}
