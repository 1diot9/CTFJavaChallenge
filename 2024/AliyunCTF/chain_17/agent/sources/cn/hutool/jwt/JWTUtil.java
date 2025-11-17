package cn.hutool.jwt;

import cn.hutool.jwt.signers.JWTSigner;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/JWTUtil.class */
public class JWTUtil {
    public static String createToken(Map<String, Object> payload, byte[] key) {
        return createToken((Map<String, Object>) null, payload, key);
    }

    public static String createToken(Map<String, Object> headers, Map<String, Object> payload, byte[] key) {
        return JWT.create().addHeaders(headers).addPayloads(payload).setKey(key).sign();
    }

    public static String createToken(Map<String, Object> payload, JWTSigner signer) {
        return createToken((Map<String, Object>) null, payload, signer);
    }

    public static String createToken(Map<String, Object> headers, Map<String, Object> payload, JWTSigner signer) {
        return JWT.create().addHeaders(headers).addPayloads(payload).setSigner(signer).sign();
    }

    public static JWT parseToken(String token) {
        return JWT.of(token);
    }

    public static boolean verify(String token, byte[] key) {
        return JWT.of(token).setKey(key).verify();
    }

    public static boolean verify(String token, JWTSigner signer) {
        return JWT.of(token).verify(signer);
    }
}
