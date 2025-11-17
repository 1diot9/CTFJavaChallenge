package cn.hutool.crypto.digest.mac;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SM4Engine;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/mac/SM4MacEngine.class */
public class SM4MacEngine extends CBCBlockCipherMacEngine {
    private static final int MAC_SIZE = 128;

    public SM4MacEngine(CipherParameters params) {
        super((BlockCipher) new SM4Engine(), 128, params);
    }
}
