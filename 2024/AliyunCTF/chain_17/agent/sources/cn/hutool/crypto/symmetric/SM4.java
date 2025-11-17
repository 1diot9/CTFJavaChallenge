package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/SM4.class */
public class SM4 extends SymmetricCrypto {
    private static final long serialVersionUID = 1;
    public static final String ALGORITHM_NAME = "SM4";

    public SM4() {
        super(ALGORITHM_NAME);
    }

    public SM4(byte[] key) {
        super(ALGORITHM_NAME, key);
    }

    public SM4(Mode mode, Padding padding) {
        this(mode.name(), padding.name());
    }

    public SM4(Mode mode, Padding padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public SM4(Mode mode, Padding padding, byte[] key, byte[] iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public SM4(Mode mode, Padding padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public SM4(Mode mode, Padding padding, SecretKey key, byte[] iv) {
        this(mode, padding, key, ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
    }

    public SM4(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public SM4(String mode, String padding) {
        this(mode, padding, (byte[]) null);
    }

    public SM4(String mode, String padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public SM4(String mode, String padding, byte[] key, byte[] iv) {
        this(mode, padding, SecureUtil.generateKey(ALGORITHM_NAME, key), ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
    }

    public SM4(String mode, String padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public SM4(String mode, String padding, SecretKey key, IvParameterSpec iv) {
        super(StrUtil.format("SM4/{}/{}", mode, padding), key, iv);
    }
}
