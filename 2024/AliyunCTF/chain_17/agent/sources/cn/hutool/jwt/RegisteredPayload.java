package cn.hutool.jwt;

import cn.hutool.jwt.RegisteredPayload;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/RegisteredPayload.class */
public interface RegisteredPayload<T extends RegisteredPayload<T>> {
    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String AUDIENCE = "aud";
    public static final String EXPIRES_AT = "exp";
    public static final String NOT_BEFORE = "nbf";
    public static final String ISSUED_AT = "iat";
    public static final String JWT_ID = "jti";

    T setPayload(String str, Object obj);

    default T setIssuer(String issuer) {
        return setPayload(ISSUER, issuer);
    }

    default T setSubject(String subject) {
        return setPayload(SUBJECT, subject);
    }

    default T setAudience(String... audience) {
        return setPayload(AUDIENCE, audience);
    }

    default T setExpiresAt(Date expiresAt) {
        return setPayload(EXPIRES_AT, expiresAt);
    }

    default T setNotBefore(Date notBefore) {
        return setPayload(NOT_BEFORE, notBefore);
    }

    default T setIssuedAt(Date issuedAt) {
        return setPayload(ISSUED_AT, issuedAt);
    }

    default T setJWTId(String jwtId) {
        return setPayload(JWT_ID, jwtId);
    }
}
