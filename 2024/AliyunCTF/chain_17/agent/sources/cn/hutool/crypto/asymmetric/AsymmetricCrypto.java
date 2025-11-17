package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.crypto.CipherWrapper;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SecureUtil;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/AsymmetricCrypto.class */
public class AsymmetricCrypto extends AbstractAsymmetricCrypto<AsymmetricCrypto> {
    private static final long serialVersionUID = 1;
    protected CipherWrapper cipherWrapper;
    protected int encryptBlockSize;
    protected int decryptBlockSize;

    public AsymmetricCrypto(AsymmetricAlgorithm algorithm) {
        this(algorithm, (byte[]) null, (byte[]) null);
    }

    public AsymmetricCrypto(String algorithm) {
        this(algorithm, (byte[]) null, (byte[]) null);
    }

    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, String privateKeyStr, String publicKeyStr) {
        this(algorithm.getValue(), SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
    }

    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    public AsymmetricCrypto(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    public AsymmetricCrypto(String algorithm, String privateKeyBase64, String publicKeyBase64) {
        this(algorithm, Base64.decode(privateKeyBase64), Base64.decode(publicKeyBase64));
    }

    public AsymmetricCrypto(String algorithm, byte[] privateKey, byte[] publicKey) {
        this(algorithm, KeyUtil.generatePrivateKey(algorithm, privateKey), KeyUtil.generatePublicKey(algorithm, publicKey));
    }

    public AsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(algorithm, privateKey, publicKey);
        this.encryptBlockSize = -1;
        this.decryptBlockSize = -1;
    }

    public int getEncryptBlockSize() {
        return this.encryptBlockSize;
    }

    public void setEncryptBlockSize(int encryptBlockSize) {
        this.encryptBlockSize = encryptBlockSize;
    }

    public int getDecryptBlockSize() {
        return this.decryptBlockSize;
    }

    public void setDecryptBlockSize(int decryptBlockSize) {
        this.decryptBlockSize = decryptBlockSize;
    }

    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return this.cipherWrapper.getParams();
    }

    public void setAlgorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
        this.cipherWrapper.setParams(algorithmParameterSpec);
    }

    public AsymmetricCrypto setRandom(SecureRandom random) {
        this.cipherWrapper.setRandom(random);
        return this;
    }

    @Override // cn.hutool.crypto.asymmetric.BaseAsymmetric
    public AsymmetricCrypto init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super.init(algorithm, privateKey, publicKey);
        initCipher();
        return this;
    }

    @Override // cn.hutool.crypto.asymmetric.AsymmetricEncryptor
    public byte[] encrypt(byte[] data, KeyType keyType) {
        int blockSize;
        Key key = getKeyByType(keyType);
        this.lock.lock();
        try {
            try {
                Cipher cipher = initMode(1, key);
                if (this.encryptBlockSize < 0 && (blockSize = cipher.getBlockSize()) > 0) {
                    this.encryptBlockSize = blockSize;
                }
                byte[] doFinal = doFinal(data, this.encryptBlockSize < 0 ? data.length : this.encryptBlockSize);
                this.lock.unlock();
                return doFinal;
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    @Override // cn.hutool.crypto.asymmetric.AsymmetricDecryptor
    public byte[] decrypt(byte[] data, KeyType keyType) {
        int blockSize;
        Key key = getKeyByType(keyType);
        this.lock.lock();
        try {
            try {
                Cipher cipher = initMode(2, key);
                if (this.decryptBlockSize < 0 && (blockSize = cipher.getBlockSize()) > 0) {
                    this.decryptBlockSize = blockSize;
                }
                byte[] doFinal = doFinal(data, this.decryptBlockSize < 0 ? data.length : this.decryptBlockSize);
                this.lock.unlock();
                return doFinal;
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    public Cipher getCipher() {
        return this.cipherWrapper.getCipher();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initCipher() {
        this.cipherWrapper = new CipherWrapper(this.algorithm);
    }

    private byte[] doFinal(byte[] data, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
        int dataLength = data.length;
        if (dataLength <= maxBlockSize) {
            return getCipher().doFinal(data, 0, dataLength);
        }
        return doFinalWithBlock(data, maxBlockSize);
    }

    private byte[] doFinalWithBlock(byte[] data, int maxBlockSize) throws IllegalBlockSizeException, BadPaddingException, IOException {
        int dataLength = data.length;
        FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        int offSet = 0;
        int i = dataLength;
        while (true) {
            int remainLength = i;
            if (remainLength > 0) {
                int blockSize = Math.min(remainLength, maxBlockSize);
                out.write(getCipher().doFinal(data, offSet, blockSize));
                offSet += blockSize;
                i = dataLength - offSet;
            } else {
                return out.toByteArray();
            }
        }
    }

    private Cipher initMode(int mode, Key key) throws InvalidAlgorithmParameterException, InvalidKeyException {
        return this.cipherWrapper.initMode(mode, key).getCipher();
    }
}
