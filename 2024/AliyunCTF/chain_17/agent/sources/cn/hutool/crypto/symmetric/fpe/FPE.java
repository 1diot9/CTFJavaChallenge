package cn.hutool.crypto.symmetric.fpe;

import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import java.io.Serializable;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AlphabetMapper;
import org.bouncycastle.jcajce.spec.FPEParameterSpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/fpe/FPE.class */
public class FPE implements Serializable {
    private static final long serialVersionUID = 1;
    private final AES aes;
    private final AlphabetMapper mapper;

    public FPE(FPEMode mode, byte[] key, AlphabetMapper mapper) {
        this(mode, key, mapper, null);
    }

    public FPE(FPEMode mode, byte[] key, AlphabetMapper mapper, byte[] tweak) {
        mode = null == mode ? FPEMode.FF1 : mode;
        if (null == tweak) {
            switch (mode) {
                case FF1:
                    tweak = new byte[0];
                    break;
                case FF3_1:
                    tweak = new byte[7];
                    break;
            }
        }
        this.aes = new AES(mode.value, Padding.NoPadding.name(), KeyUtil.generateKey(mode.value, key), (AlgorithmParameterSpec) new FPEParameterSpec(mapper.getRadix(), tweak));
        this.mapper = mapper;
    }

    public String encrypt(String data) {
        if (null == data) {
            return null;
        }
        return new String(encrypt(data.toCharArray()));
    }

    public char[] encrypt(char[] data) {
        if (null == data) {
            return null;
        }
        return this.mapper.convertToChars(this.aes.encrypt(this.mapper.convertToIndexes(data)));
    }

    public String decrypt(String data) {
        if (null == data) {
            return null;
        }
        return new String(decrypt(data.toCharArray()));
    }

    public char[] decrypt(char[] data) {
        if (null == data) {
            return null;
        }
        return this.mapper.convertToChars(this.aes.decrypt(this.mapper.convertToIndexes(data)));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/fpe/FPE$FPEMode.class */
    public enum FPEMode {
        FF1("FF1"),
        FF3_1("FF3-1");

        private final String value;

        FPEMode(String name) {
            this.value = name;
        }

        public String getValue() {
            return this.value;
        }
    }
}
