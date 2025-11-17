package cn.hutool.crypto.digest.mac;

import java.security.Key;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/mac/CBCBlockCipherMacEngine.class */
public class CBCBlockCipherMacEngine extends BCMacEngine {
    public CBCBlockCipherMacEngine(BlockCipher digest, int macSizeInBits, Key key, byte[] iv) {
        this(digest, macSizeInBits, key.getEncoded(), iv);
    }

    public CBCBlockCipherMacEngine(BlockCipher digest, int macSizeInBits, byte[] key, byte[] iv) {
        this(digest, macSizeInBits, (CipherParameters) new ParametersWithIV(new KeyParameter(key), iv));
    }

    public CBCBlockCipherMacEngine(BlockCipher cipher, int macSizeInBits, Key key) {
        this(cipher, macSizeInBits, key.getEncoded());
    }

    public CBCBlockCipherMacEngine(BlockCipher cipher, int macSizeInBits, byte[] key) {
        this(cipher, macSizeInBits, (CipherParameters) new KeyParameter(key));
    }

    public CBCBlockCipherMacEngine(BlockCipher cipher, int macSizeInBits, CipherParameters params) {
        this(new CBCBlockCipherMac(cipher, macSizeInBits), params);
    }

    public CBCBlockCipherMacEngine(CBCBlockCipherMac mac, CipherParameters params) {
        super(mac, params);
    }

    public CBCBlockCipherMacEngine init(BlockCipher cipher, CipherParameters params) {
        return (CBCBlockCipherMacEngine) init((org.bouncycastle.crypto.Mac) new CBCBlockCipherMac(cipher), params);
    }
}
