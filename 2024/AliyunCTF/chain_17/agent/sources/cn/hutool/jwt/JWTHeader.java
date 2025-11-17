package cn.hutool.jwt;

import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/JWTHeader.class */
public class JWTHeader extends Claims {
    private static final long serialVersionUID = 1;
    public static String ALGORITHM = "alg";
    public static String TYPE = "typ";
    public static String CONTENT_TYPE = "cty";
    public static String KEY_ID = "kid";

    public JWTHeader setKeyId(String keyId) {
        setClaim(KEY_ID, keyId);
        return this;
    }

    public JWTHeader addHeaders(Map<String, ?> headerClaims) {
        putAll(headerClaims);
        return this;
    }
}
