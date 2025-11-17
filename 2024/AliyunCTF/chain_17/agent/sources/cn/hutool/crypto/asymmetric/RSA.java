package cn.hutool.crypto.asymmetric;

import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.GlobalBouncyCastleProvider;
import cn.hutool.crypto.SecureUtil;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/RSA.class */
public class RSA extends AsymmetricCrypto {
    private static final long serialVersionUID = 1;
    private static final AsymmetricAlgorithm ALGORITHM_RSA = AsymmetricAlgorithm.RSA_ECB_PKCS1;

    public static PrivateKey generatePrivateKey(BigInteger modulus, BigInteger privateExponent) {
        return SecureUtil.generatePrivateKey(ALGORITHM_RSA.getValue(), new RSAPrivateKeySpec(modulus, privateExponent));
    }

    public static PublicKey generatePublicKey(BigInteger modulus, BigInteger publicExponent) {
        return SecureUtil.generatePublicKey(ALGORITHM_RSA.getValue(), new RSAPublicKeySpec(modulus, publicExponent));
    }

    public RSA() {
        super(ALGORITHM_RSA);
    }

    public RSA(String rsaAlgorithm) {
        super(rsaAlgorithm);
    }

    public RSA(String privateKeyStr, String publicKeyStr) {
        super(ALGORITHM_RSA, privateKeyStr, publicKeyStr);
    }

    public RSA(String rsaAlgorithm, String privateKeyStr, String publicKeyStr) {
        super(rsaAlgorithm, privateKeyStr, publicKeyStr);
    }

    public RSA(byte[] privateKey, byte[] publicKey) {
        super(ALGORITHM_RSA, privateKey, publicKey);
    }

    public RSA(BigInteger modulus, BigInteger privateExponent, BigInteger publicExponent) {
        this(generatePrivateKey(modulus, privateExponent), generatePublicKey(modulus, publicExponent));
    }

    public RSA(PrivateKey privateKey, PublicKey publicKey) {
        super(ALGORITHM_RSA, privateKey, publicKey);
    }

    public RSA(String rsaAlgorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(rsaAlgorithm, privateKey, publicKey);
    }

    @Override // cn.hutool.crypto.asymmetric.AsymmetricCrypto, cn.hutool.crypto.asymmetric.AsymmetricEncryptor
    public byte[] encrypt(byte[] data, KeyType keyType) {
        if (this.encryptBlockSize < 0 && null == GlobalBouncyCastleProvider.INSTANCE.getProvider()) {
            this.encryptBlockSize = (((RSAKey) getKeyByType(keyType)).getModulus().bitLength() / 8) - 11;
        }
        return super.encrypt(data, keyType);
    }

    @Override // cn.hutool.crypto.asymmetric.AsymmetricCrypto, cn.hutool.crypto.asymmetric.AsymmetricDecryptor
    public byte[] decrypt(byte[] bytes, KeyType keyType) {
        if (this.decryptBlockSize < 0 && null == GlobalBouncyCastleProvider.INSTANCE.getProvider()) {
            this.decryptBlockSize = ((RSAKey) getKeyByType(keyType)).getModulus().bitLength() / 8;
        }
        return super.decrypt(bytes, keyType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // cn.hutool.crypto.asymmetric.AsymmetricCrypto
    public void initCipher() {
        try {
            super.initCipher();
        } catch (CryptoException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoSuchAlgorithmException) {
                this.algorithm = AsymmetricAlgorithm.RSA.getValue();
                super.initCipher();
            }
            throw e;
        }
    }
}
