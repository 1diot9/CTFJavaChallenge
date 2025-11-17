package cn.hutool.crypto.asymmetric;

import cn.hutool.crypto.asymmetric.AbstractAsymmetricCrypto;
import java.security.PrivateKey;
import java.security.PublicKey;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/AbstractAsymmetricCrypto.class */
public abstract class AbstractAsymmetricCrypto<T extends AbstractAsymmetricCrypto<T>> extends BaseAsymmetric<T> implements AsymmetricEncryptor, AsymmetricDecryptor {
    private static final long serialVersionUID = 1;

    public AbstractAsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(algorithm, privateKey, publicKey);
    }
}
