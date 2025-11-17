package cn.hutool.crypto.symmetric;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/DESede.class */
public class DESede extends SymmetricCrypto {
    private static final long serialVersionUID = 1;

    public DESede() {
        super(SymmetricAlgorithm.DESede);
    }

    public DESede(byte[] key) {
        super(SymmetricAlgorithm.DESede, key);
    }

    public DESede(Mode mode, Padding padding) {
        this(mode.name(), padding.name());
    }

    public DESede(Mode mode, Padding padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public DESede(Mode mode, Padding padding, byte[] key, byte[] iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public DESede(Mode mode, Padding padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public DESede(Mode mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        this(mode.name(), padding.name(), key, iv);
    }

    public DESede(String mode, String padding) {
        this(mode, padding, (byte[]) null);
    }

    public DESede(String mode, String padding, byte[] key) {
        this(mode, padding, key, (byte[]) null);
    }

    public DESede(String mode, String padding, byte[] key, byte[] iv) {
        this(mode, padding, SecureUtil.generateKey(SymmetricAlgorithm.DESede.getValue(), key), null == iv ? null : new IvParameterSpec(iv));
    }

    public DESede(String mode, String padding, SecretKey key) {
        this(mode, padding, key, (IvParameterSpec) null);
    }

    public DESede(String mode, String padding, SecretKey key, IvParameterSpec iv) {
        super(StrUtil.format("{}/{}/{}", SymmetricAlgorithm.DESede.getValue(), mode, padding), key, iv);
    }
}
