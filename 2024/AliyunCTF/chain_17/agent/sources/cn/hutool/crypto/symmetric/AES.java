package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/AES.class */
public class AES extends SymmetricCrypto {
    private static final long serialVersionUID = 1;

    public AES() {
        super(SymmetricAlgorithm.AES);
    }

    public AES(byte[] key) {
        super(SymmetricAlgorithm.AES, key);
    }

    public AES(SecretKey key) {
        super(SymmetricAlgorithm.AES, key);
    }

    public AES(Mode mode, Padding padding) {
        this(mode.name(), padding.name());
    }

    public AES(Mode mode, Padding padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public AES(Mode mode, Padding padding, byte[] key, byte[] iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public AES(Mode mode, Padding padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public AES(Mode mode, Padding padding, SecretKey key, byte[] iv) {
        this(mode, padding, key, ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
    }

    public AES(Mode mode, Padding padding, SecretKey key, AlgorithmParameterSpec paramsSpec) {
        this(mode.name(), padding.name(), key, paramsSpec);
    }

    public AES(String mode, String padding) {
        this(mode, padding, (byte[]) null);
    }

    public AES(String mode, String padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public AES(String mode, String padding, byte[] key, byte[] iv) {
        this(mode, padding, KeyUtil.generateKey(SymmetricAlgorithm.AES.getValue(), key), ArrayUtil.isEmpty(iv) ? null : new IvParameterSpec(iv));
    }

    public AES(String mode, String padding, SecretKey key) {
        this(mode, padding, key, (AlgorithmParameterSpec) null);
    }

    public AES(String mode, String padding, SecretKey key, AlgorithmParameterSpec paramsSpec) {
        super(StrUtil.format("AES/{}/{}", mode, padding), key, paramsSpec);
    }
}
