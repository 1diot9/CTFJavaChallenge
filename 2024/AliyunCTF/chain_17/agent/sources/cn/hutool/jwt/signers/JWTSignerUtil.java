package cn.hutool.jwt.signers;

import cn.hutool.core.lang.Assert;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/signers/JWTSignerUtil.class */
public class JWTSignerUtil {
    public static JWTSigner none() {
        return NoneJWTSigner.NONE;
    }

    public static JWTSigner hs256(byte[] key) {
        return createSigner("HS256", key);
    }

    public static JWTSigner hs384(byte[] key) {
        return createSigner("HS384", key);
    }

    public static JWTSigner hs512(byte[] key) {
        return createSigner("HS512", key);
    }

    public static JWTSigner rs256(Key key) {
        return createSigner("RS256", key);
    }

    public static JWTSigner rs384(Key key) {
        return createSigner("RS384", key);
    }

    public static JWTSigner rs512(Key key) {
        return createSigner("RS512", key);
    }

    public static JWTSigner es256(Key key) {
        return createSigner("ES256", key);
    }

    public static JWTSigner es384(Key key) {
        return createSigner("ES384", key);
    }

    public static JWTSigner es512(Key key) {
        return createSigner("ES512", key);
    }

    public static JWTSigner hmd5(Key key) {
        return createSigner("HMD5", key);
    }

    public static JWTSigner hsha1(Key key) {
        return createSigner("HSHA1", key);
    }

    public static JWTSigner sm4cmac(Key key) {
        return createSigner("SM4CMAC", key);
    }

    public static JWTSigner rmd2(Key key) {
        return createSigner("RMD2", key);
    }

    public static JWTSigner rmd5(Key key) {
        return createSigner("RMD5", key);
    }

    public static JWTSigner rsha1(Key key) {
        return createSigner("RSHA1", key);
    }

    public static JWTSigner dnone(Key key) {
        return createSigner("DNONE", key);
    }

    public static JWTSigner dsha1(Key key) {
        return createSigner("DSHA1", key);
    }

    public static JWTSigner enone(Key key) {
        return createSigner("ENONE", key);
    }

    public static JWTSigner esha1(Key key) {
        return createSigner("ESHA1", key);
    }

    public static JWTSigner createSigner(String algorithmId, byte[] key) {
        Assert.notNull(key, "Signer key must be not null!", new Object[0]);
        if (null == algorithmId || "none".equals(algorithmId)) {
            return none();
        }
        return new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
    }

    public static JWTSigner createSigner(String algorithmId, KeyPair keyPair) {
        Assert.notNull(keyPair, "Signer key pair must be not null!", new Object[0]);
        if (null == algorithmId || "none".equals(algorithmId)) {
            return none();
        }
        return new AsymmetricJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), keyPair);
    }

    public static JWTSigner createSigner(String algorithmId, Key key) {
        Assert.notNull(key, "Signer key must be not null!", new Object[0]);
        if (null == algorithmId || "none".equals(algorithmId)) {
            return NoneJWTSigner.NONE;
        }
        if ((key instanceof PrivateKey) || (key instanceof PublicKey)) {
            return new AsymmetricJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
        }
        return new HMacJWTSigner(AlgorithmUtil.getAlgorithm(algorithmId), key);
    }
}
