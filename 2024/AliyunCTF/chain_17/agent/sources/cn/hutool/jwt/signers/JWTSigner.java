package cn.hutool.jwt.signers;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/jwt/signers/JWTSigner.class */
public interface JWTSigner {
    String sign(String str, String str2);

    boolean verify(String str, String str2, String str3);

    String getAlgorithm();

    default String getAlgorithmId() {
        return AlgorithmUtil.getId(getAlgorithm());
    }
}
