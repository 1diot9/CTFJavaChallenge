package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.asymmetric.BaseAsymmetric;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/BaseAsymmetric.class */
public class BaseAsymmetric<T extends BaseAsymmetric<T>> implements Serializable {
    private static final long serialVersionUID = 1;
    protected String algorithm;
    protected PublicKey publicKey;
    protected PrivateKey privateKey;
    protected final Lock lock = new ReentrantLock();

    public BaseAsymmetric(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        init(algorithm, privateKey, publicKey);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public T init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this.algorithm = algorithm;
        if (null == privateKey && null == publicKey) {
            initKeys();
        } else {
            if (null != privateKey) {
                this.privateKey = privateKey;
            }
            if (null != publicKey) {
                this.publicKey = publicKey;
            }
        }
        return this;
    }

    public T initKeys() {
        KeyPair keyPair = KeyUtil.generateKeyPair(this.algorithm);
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
        return this;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getPublicKeyBase64() {
        PublicKey publicKey = getPublicKey();
        if (null == publicKey) {
            return null;
        }
        return Base64.encode(publicKey.getEncoded());
    }

    public T setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public String getPrivateKeyBase64() {
        PrivateKey privateKey = getPrivateKey();
        if (null == privateKey) {
            return null;
        }
        return Base64.encode(privateKey.getEncoded());
    }

    public T setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public T setKey(Key key) {
        Assert.notNull(key, "key must be not null !", new Object[0]);
        if (key instanceof PublicKey) {
            return setPublicKey((PublicKey) key);
        }
        if (key instanceof PrivateKey) {
            return setPrivateKey((PrivateKey) key);
        }
        throw new CryptoException("Unsupported key type: {}", key.getClass());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Key getKeyByType(KeyType type) {
        switch (type) {
            case PrivateKey:
                if (null == this.privateKey) {
                    throw new NullPointerException("Private key must not null when use it !");
                }
                return this.privateKey;
            case PublicKey:
                if (null == this.publicKey) {
                    throw new NullPointerException("Public key must not null when use it !");
                }
                return this.publicKey;
            default:
                throw new CryptoException("Unsupported key type: " + type);
        }
    }
}
