package cn.hutool.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.signers.AlgorithmUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import cn.hutool.jwt.signers.NoneJWTSigner;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyPair;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/JWT.class */
public class JWT implements RegisteredPayload<JWT> {
    private final JWTHeader header;
    private final JWTPayload payload;
    private Charset charset;
    private JWTSigner signer;
    private List<String> tokens;

    public static JWT create() {
        return new JWT();
    }

    public static JWT of(String token) {
        return new JWT(token);
    }

    public JWT() {
        this.header = new JWTHeader();
        this.payload = new JWTPayload();
        this.charset = CharsetUtil.CHARSET_UTF_8;
    }

    public JWT(String token) {
        this();
        parse(token);
    }

    public JWT parse(String token) {
        Assert.notBlank(token, "Token String must be not blank!", new Object[0]);
        List<String> tokens = splitToken(token);
        this.tokens = tokens;
        this.header.parse(tokens.get(0), this.charset);
        this.payload.parse(tokens.get(1), this.charset);
        return this;
    }

    public JWT setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public JWT setKey(byte[] key) {
        String claim = (String) this.header.getClaim(JWTHeader.ALGORITHM);
        if (StrUtil.isNotBlank(claim)) {
            return setSigner(JWTSignerUtil.createSigner(claim, key));
        }
        return setSigner(JWTSignerUtil.hs256(key));
    }

    public JWT setSigner(String algorithmId, byte[] key) {
        return setSigner(JWTSignerUtil.createSigner(algorithmId, key));
    }

    public JWT setSigner(String algorithmId, Key key) {
        return setSigner(JWTSignerUtil.createSigner(algorithmId, key));
    }

    public JWT setSigner(String algorithmId, KeyPair keyPair) {
        return setSigner(JWTSignerUtil.createSigner(algorithmId, keyPair));
    }

    public JWT setSigner(JWTSigner signer) {
        this.signer = signer;
        return this;
    }

    public JWTSigner getSigner() {
        return this.signer;
    }

    public JSONObject getHeaders() {
        return this.header.getClaimsJson();
    }

    public JWTHeader getHeader() {
        return this.header;
    }

    public Object getHeader(String name) {
        return this.header.getClaim(name);
    }

    public String getAlgorithm() {
        return (String) this.header.getClaim(JWTHeader.ALGORITHM);
    }

    public JWT setHeader(String name, Object value) {
        this.header.setClaim(name, value);
        return this;
    }

    public JWT addHeaders(Map<String, ?> headers) {
        this.header.addHeaders(headers);
        return this;
    }

    public JSONObject getPayloads() {
        return this.payload.getClaimsJson();
    }

    public JWTPayload getPayload() {
        return this.payload;
    }

    public Object getPayload(String name) {
        return getPayload().getClaim(name);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.jwt.RegisteredPayload
    public JWT setPayload(String name, Object value) {
        this.payload.setClaim(name, value);
        return this;
    }

    public JWT addPayloads(Map<String, ?> payloads) {
        this.payload.addPayloads(payloads);
        return this;
    }

    public String sign() {
        return sign(this.signer);
    }

    public String sign(JWTSigner signer) {
        Assert.notNull(signer, () -> {
            return new JWTException("No Signer provided!");
        });
        String type = (String) this.header.getClaim(JWTHeader.TYPE);
        if (StrUtil.isBlank(type)) {
            this.header.setClaim(JWTHeader.TYPE, "JWT");
        }
        String algorithm = (String) this.header.getClaim(JWTHeader.ALGORITHM);
        if (StrUtil.isBlank(algorithm)) {
            this.header.setClaim(JWTHeader.ALGORITHM, AlgorithmUtil.getId(signer.getAlgorithm()));
        }
        String headerBase64 = Base64.encodeUrlSafe(this.header.toString(), this.charset);
        String payloadBase64 = Base64.encodeUrlSafe(this.payload.toString(), this.charset);
        String sign = signer.sign(headerBase64, payloadBase64);
        return StrUtil.format("{}.{}.{}", headerBase64, payloadBase64, sign);
    }

    public boolean verify() {
        return verify(this.signer);
    }

    public boolean validate(long leeway) {
        if (false == verify()) {
            return false;
        }
        try {
            JWTValidator.of(this).validateDate(DateUtil.date(), leeway);
            return true;
        } catch (ValidateException e) {
            return false;
        }
    }

    public boolean verify(JWTSigner signer) {
        if (null == signer) {
            signer = NoneJWTSigner.NONE;
        }
        List<String> tokens = this.tokens;
        if (CollUtil.isEmpty((Collection<?>) tokens)) {
            throw new JWTException("No token to verify!");
        }
        return signer.verify(tokens.get(0), tokens.get(1), tokens.get(2));
    }

    private static List<String> splitToken(String token) {
        List<String> tokens = StrUtil.split((CharSequence) token, '.');
        if (3 != tokens.size()) {
            throw new JWTException("The token was expected 3 parts, but got {}.", Integer.valueOf(tokens.size()));
        }
        return tokens;
    }
}
