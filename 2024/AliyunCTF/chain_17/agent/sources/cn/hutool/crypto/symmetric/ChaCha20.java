package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.KeyUtil;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/ChaCha20.class */
public class ChaCha20 extends SymmetricCrypto {
    private static final long serialVersionUID = 1;
    public static final String ALGORITHM_NAME = "ChaCha20";

    public ChaCha20(byte[] key, byte[] iv) {
        super(ALGORITHM_NAME, KeyUtil.generateKey(ALGORITHM_NAME, key), generateIvParam(iv));
    }

    private static IvParameterSpec generateIvParam(byte[] iv) {
        if (null == iv) {
            iv = RandomUtil.randomBytes(12);
        }
        return new IvParameterSpec(iv);
    }
}
