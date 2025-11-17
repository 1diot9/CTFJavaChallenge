package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/DES.class */
public class DES extends SymmetricCrypto {
    private static final long serialVersionUID = 1;

    public DES() {
        super(SymmetricAlgorithm.DES);
    }

    public DES(byte[] key) {
        super(SymmetricAlgorithm.DES, key);
    }

    public DES(Mode mode, Padding padding) {
        this(mode.name(), padding.name());
    }

    public DES(Mode mode, Padding padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public DES(Mode mode, Padding padding, byte[] key, byte[] iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public DES(Mode mode, Padding padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public DES(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public DES(String mode, String padding) {
        this(mode, padding, (byte[]) null);
    }

    public DES(String mode, String padding, byte[] key) {
        this(mode, padding, SecureUtil.generateKey("DES", key), (IvParameterSpec) null);
    }

    public DES(String mode, String padding, byte[] key, byte[] iv) {
        this(mode, padding, SecureUtil.generateKey("DES", key), null == iv ? null : new IvParameterSpec(iv));
    }

    public DES(String mode, String padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public DES(String mode, String padding, SecretKey key, IvParameterSpec iv) {
        super(StrUtil.format("DES/{}/{}", mode, padding), key, iv);
    }
}
