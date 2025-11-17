package cn.hutool.crypto;

import java.security.Provider;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/GlobalBouncyCastleProvider.class */
public enum GlobalBouncyCastleProvider {
    INSTANCE;

    private Provider provider;
    private static boolean useBouncyCastle = true;

    GlobalBouncyCastleProvider() {
        try {
            this.provider = ProviderFactory.createBouncyCastleProvider();
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
        }
    }

    public Provider getProvider() {
        if (useBouncyCastle) {
            return this.provider;
        }
        return null;
    }

    public static void setUseBouncyCastle(boolean isUseBouncyCastle) {
        useBouncyCastle = isUseBouncyCastle;
    }
}
