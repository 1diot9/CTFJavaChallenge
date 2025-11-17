package cn.hutool.jwt;

import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/JWTPayload.class */
public class JWTPayload extends Claims implements RegisteredPayload<JWTPayload> {
    private static final long serialVersionUID = 1;

    public JWTPayload addPayloads(Map<String, ?> payloadClaims) {
        putAll(payloadClaims);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.jwt.RegisteredPayload
    public JWTPayload setPayload(String name, Object value) {
        setClaim(name, value);
        return this;
    }
}
