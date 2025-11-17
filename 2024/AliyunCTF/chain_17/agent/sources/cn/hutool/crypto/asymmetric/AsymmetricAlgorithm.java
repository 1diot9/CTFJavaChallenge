package cn.hutool.crypto.asymmetric;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/AsymmetricAlgorithm.class */
public enum AsymmetricAlgorithm {
    RSA("RSA"),
    RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
    RSA_ECB("RSA/ECB/NoPadding"),
    RSA_None("RSA/None/NoPadding");

    private final String value;

    AsymmetricAlgorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
