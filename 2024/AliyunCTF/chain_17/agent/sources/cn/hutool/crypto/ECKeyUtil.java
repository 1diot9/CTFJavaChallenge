package cn.hutool.crypto;

import cn.hutool.core.io.IORuntimeException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.BigIntegers;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/ECKeyUtil.class */
public class ECKeyUtil {
    public static AsymmetricKeyParameter toParams(Key key) {
        if (key instanceof PrivateKey) {
            return toPrivateParams((PrivateKey) key);
        }
        if (key instanceof PublicKey) {
            return toPublicParams((PublicKey) key);
        }
        return null;
    }

    public static ECPublicKeyParameters getPublicParams(ECPrivateKeyParameters privateKeyParameters) {
        ECDomainParameters domainParameters = privateKeyParameters.getParameters();
        ECPoint q = new FixedPointCombMultiplier().multiply(domainParameters.getG(), privateKeyParameters.getD());
        return new ECPublicKeyParameters(q, domainParameters);
    }

    public static ECPublicKeyParameters toSm2PublicParams(byte[] q) {
        return toPublicParams(q, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPublicKeyParameters toSm2PublicParams(String q) {
        return toPublicParams(q, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPublicKeyParameters toSm2PublicParams(String x, String y) {
        return toPublicParams(x, y, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPublicKeyParameters toSm2PublicParams(byte[] xBytes, byte[] yBytes) {
        return toPublicParams(xBytes, yBytes, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPublicKeyParameters toPublicParams(String x, String y, ECDomainParameters domainParameters) {
        return toPublicParams(SecureUtil.decode(x), SecureUtil.decode(y), domainParameters);
    }

    public static ECPublicKeyParameters toPublicParams(byte[] xBytes, byte[] yBytes, ECDomainParameters domainParameters) {
        if (null == xBytes || null == yBytes) {
            return null;
        }
        return toPublicParams(BigIntegers.fromUnsignedByteArray(xBytes), BigIntegers.fromUnsignedByteArray(yBytes), domainParameters);
    }

    public static ECPublicKeyParameters toPublicParams(BigInteger x, BigInteger y, ECDomainParameters domainParameters) {
        if (null == x || null == y) {
            return null;
        }
        ECCurve curve = domainParameters.getCurve();
        return toPublicParams(curve.createPoint(x, y), domainParameters);
    }

    public static ECPublicKeyParameters toPublicParams(String pointEncoded, ECDomainParameters domainParameters) {
        ECCurve curve = domainParameters.getCurve();
        return toPublicParams(curve.decodePoint(SecureUtil.decode(pointEncoded)), domainParameters);
    }

    public static ECPublicKeyParameters toPublicParams(byte[] pointEncoded, ECDomainParameters domainParameters) {
        ECCurve curve = domainParameters.getCurve();
        return toPublicParams(curve.decodePoint(pointEncoded), domainParameters);
    }

    public static ECPublicKeyParameters toPublicParams(ECPoint point, ECDomainParameters domainParameters) {
        return new ECPublicKeyParameters(point, domainParameters);
    }

    public static ECPublicKeyParameters toPublicParams(PublicKey publicKey) {
        if (null == publicKey) {
            return null;
        }
        try {
            return ECUtil.generatePublicKeyParameter(publicKey);
        } catch (InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }

    public static ECPrivateKeyParameters toSm2PrivateParams(String d) {
        return toPrivateParams(d, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPrivateKeyParameters toSm2PrivateParams(byte[] d) {
        return toPrivateParams(d, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPrivateKeyParameters toSm2PrivateParams(BigInteger d) {
        return toPrivateParams(d, SmUtil.SM2_DOMAIN_PARAMS);
    }

    public static ECPrivateKeyParameters toPrivateParams(String d, ECDomainParameters domainParameters) {
        if (null == d) {
            return null;
        }
        return toPrivateParams(BigIntegers.fromUnsignedByteArray(SecureUtil.decode(d)), domainParameters);
    }

    public static ECPrivateKeyParameters toPrivateParams(byte[] d, ECDomainParameters domainParameters) {
        return toPrivateParams(BigIntegers.fromUnsignedByteArray(d), domainParameters);
    }

    public static ECPrivateKeyParameters toPrivateParams(BigInteger d, ECDomainParameters domainParameters) {
        if (null == d) {
            return null;
        }
        return new ECPrivateKeyParameters(d, domainParameters);
    }

    public static ECPrivateKeyParameters toPrivateParams(PrivateKey privateKey) {
        if (null == privateKey) {
            return null;
        }
        try {
            return ECUtil.generatePrivateKeyParameter(privateKey);
        } catch (InvalidKeyException e) {
            throw new CryptoException(e);
        }
    }

    public static PrivateKey toSm2PrivateKey(ECPrivateKey privateKey) {
        try {
            PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, SmUtil.ID_SM2_PUBLIC_KEY_PARAM), privateKey);
            return KeyUtil.generatePrivateKey("SM2", info.getEncoded());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static KeySpec createOpenSSHPrivateKeySpec(byte[] key) {
        return new OpenSSHPrivateKeySpec(key);
    }

    public static KeySpec createOpenSSHPublicKeySpec(byte[] key) {
        return new OpenSSHPublicKeySpec(key);
    }

    public static ECPrivateKeyParameters decodePrivateKeyParams(byte[] privateKeyBytes) {
        PrivateKey privateKey;
        try {
            return toSm2PrivateParams(privateKeyBytes);
        } catch (Exception e) {
            try {
                privateKey = KeyUtil.generatePrivateKey("sm2", privateKeyBytes);
            } catch (Exception e2) {
                privateKey = KeyUtil.generatePrivateKey("sm2", createOpenSSHPrivateKeySpec(privateKeyBytes));
            }
            return toPrivateParams(privateKey);
        }
    }

    public static ECPublicKeyParameters decodePublicKeyParams(byte[] publicKeyBytes) {
        PublicKey publicKey;
        try {
            return toSm2PublicParams(publicKeyBytes);
        } catch (Exception e) {
            try {
                publicKey = KeyUtil.generatePublicKey("sm2", publicKeyBytes);
            } catch (Exception e2) {
                publicKey = KeyUtil.generatePublicKey("sm2", createOpenSSHPublicKeySpec(publicKeyBytes));
            }
            return toPublicParams(publicKey);
        }
    }
}
