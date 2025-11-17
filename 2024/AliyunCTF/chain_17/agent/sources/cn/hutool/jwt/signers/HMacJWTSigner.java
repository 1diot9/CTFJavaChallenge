package cn.hutool.jwt.signers;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import java.nio.charset.Charset;
import java.security.Key;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/signers/HMacJWTSigner.class */
public class HMacJWTSigner implements JWTSigner {
    private Charset charset = CharsetUtil.CHARSET_UTF_8;
    private final HMac hMac;

    public HMacJWTSigner(String algorithm, byte[] key) {
        this.hMac = new HMac(algorithm, key);
    }

    public HMacJWTSigner(String algorithm, Key key) {
        this.hMac = new HMac(algorithm, key);
    }

    public HMacJWTSigner setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override // cn.hutool.jwt.signers.JWTSigner
    public String sign(String headerBase64, String payloadBase64) {
        return this.hMac.digestBase64(StrUtil.format("{}.{}", headerBase64, payloadBase64), this.charset, true);
    }

    @Override // cn.hutool.jwt.signers.JWTSigner
    public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
        String sign = sign(headerBase64, payloadBase64);
        return this.hMac.verify(StrUtil.bytes(sign, this.charset), StrUtil.bytes(signBase64, this.charset));
    }

    @Override // cn.hutool.jwt.signers.JWTSigner
    public String getAlgorithm() {
        return this.hMac.getAlgorithm();
    }
}
