package cn.hutool.crypto.asymmetric;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.SecureUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collection;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/Sign.class */
public class Sign extends BaseAsymmetric<Sign> {
    private static final long serialVersionUID = 1;
    protected Signature signature;

    public Sign(SignAlgorithm algorithm) {
        this(algorithm, (byte[]) null, (byte[]) null);
    }

    public Sign(String algorithm) {
        this(algorithm, (byte[]) null, (byte[]) null);
    }

    public Sign(SignAlgorithm algorithm, String privateKeyStr, String publicKeyStr) {
        this(algorithm.getValue(), SecureUtil.decode(privateKeyStr), SecureUtil.decode(publicKeyStr));
    }

    public Sign(SignAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    public Sign(SignAlgorithm algorithm, KeyPair keyPair) {
        this(algorithm.getValue(), keyPair);
    }

    public Sign(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this(algorithm.getValue(), privateKey, publicKey);
    }

    public Sign(String algorithm, String privateKeyBase64, String publicKeyBase64) {
        this(algorithm, Base64.decode(privateKeyBase64), Base64.decode(publicKeyBase64));
    }

    public Sign(String algorithm, byte[] privateKey, byte[] publicKey) {
        this(algorithm, SecureUtil.generatePrivateKey(algorithm, privateKey), SecureUtil.generatePublicKey(algorithm, publicKey));
    }

    public Sign(String algorithm, KeyPair keyPair) {
        this(algorithm, keyPair.getPrivate(), keyPair.getPublic());
    }

    public Sign(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        super(algorithm, privateKey, publicKey);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.crypto.asymmetric.BaseAsymmetric
    public Sign init(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
        this.signature = SecureUtil.createSignature(algorithm);
        super.init(algorithm, privateKey, publicKey);
        return this;
    }

    public Sign setParameter(AlgorithmParameterSpec params) {
        try {
            this.signature.setParameter(params);
            return this;
        } catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
    }

    public byte[] sign(String data, Charset charset) {
        return sign(StrUtil.bytes(data, charset));
    }

    public byte[] sign(String data) {
        return sign(data, CharsetUtil.CHARSET_UTF_8);
    }

    public String signHex(String data, Charset charset) {
        return HexUtil.encodeHexStr(sign(data, charset));
    }

    public String signHex(String data) {
        return signHex(data, CharsetUtil.CHARSET_UTF_8);
    }

    public byte[] sign(byte[] data) {
        return sign(new ByteArrayInputStream(data), -1);
    }

    public String signHex(byte[] data) {
        return HexUtil.encodeHexStr(sign(data));
    }

    public String signHex(InputStream data) {
        return HexUtil.encodeHexStr(sign(data));
    }

    public byte[] sign(InputStream data) {
        return sign(data, 8192);
    }

    public String digestHex(InputStream data, int bufferLength) {
        return HexUtil.encodeHexStr(sign(data, bufferLength));
    }

    public byte[] sign(InputStream data, int bufferLength) {
        if (bufferLength < 1) {
            bufferLength = 8192;
        }
        byte[] buffer = new byte[bufferLength];
        this.lock.lock();
        try {
            try {
                this.signature.initSign(this.privateKey);
                try {
                    int read = data.read(buffer, 0, bufferLength);
                    while (read > -1) {
                        this.signature.update(buffer, 0, read);
                        read = data.read(buffer, 0, bufferLength);
                    }
                    byte[] result = this.signature.sign();
                    return result;
                } catch (Exception e) {
                    throw new CryptoException(e);
                }
            } catch (Exception e2) {
                throw new CryptoException(e2);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public boolean verify(byte[] data, byte[] sign) {
        this.lock.lock();
        try {
            try {
                this.signature.initVerify(this.publicKey);
                this.signature.update(data);
                boolean verify = this.signature.verify(sign);
                this.lock.unlock();
                return verify;
            } catch (Exception e) {
                throw new CryptoException(e);
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    public Signature getSignature() {
        return this.signature;
    }

    public Sign setSignature(Signature signature) {
        this.signature = signature;
        return this;
    }

    public Sign setCertificate(Certificate certificate) {
        boolean[] keyUsageInfo;
        if (certificate instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate) certificate;
            Set<String> critSet = cert.getCriticalExtensionOIDs();
            if (CollUtil.isNotEmpty((Collection<?>) critSet) && critSet.contains("2.5.29.15") && (keyUsageInfo = cert.getKeyUsage()) != null && !keyUsageInfo[0]) {
                throw new CryptoException("Wrong key usage");
            }
        }
        this.publicKey = certificate.getPublicKey();
        return this;
    }
}
