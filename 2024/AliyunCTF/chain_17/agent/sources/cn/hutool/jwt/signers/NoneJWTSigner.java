package cn.hutool.jwt.signers;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/signers/NoneJWTSigner.class */
public class NoneJWTSigner implements JWTSigner {
    public static final String ID_NONE = "none";
    public static NoneJWTSigner NONE = new NoneJWTSigner();

    @Override // cn.hutool.jwt.signers.JWTSigner
    public String sign(String headerBase64, String payloadBase64) {
        return "";
    }

    @Override // cn.hutool.jwt.signers.JWTSigner
    public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
        return StrUtil.isEmpty(signBase64);
    }

    @Override // cn.hutool.jwt.signers.JWTSigner
    public String getAlgorithm() {
        return "none";
    }
}
