package cn.hutool.http.ssl;

import cn.hutool.core.util.StrUtil;
import javax.net.ssl.SSLSocketFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/ssl/DefaultSSLInfo.class */
public class DefaultSSLInfo {
    public static final TrustAnyHostnameVerifier TRUST_ANY_HOSTNAME_VERIFIER = new TrustAnyHostnameVerifier();
    public static final SSLSocketFactory DEFAULT_SSF;

    static {
        if (StrUtil.equalsIgnoreCase("dalvik", System.getProperty("java.vm.name"))) {
            DEFAULT_SSF = new AndroidSupportSSLFactory();
        } else {
            DEFAULT_SSF = new DefaultSSLFactory();
        }
    }
}
