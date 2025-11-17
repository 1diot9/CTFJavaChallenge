package cn.hutool.crypto;

import java.security.Provider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/ProviderFactory.class */
public class ProviderFactory {
    public static Provider createBouncyCastleProvider() {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        SecureUtil.addProvider(provider);
        return provider;
    }
}
