package cn.hutool.crypto.digest;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/digest/HmacAlgorithm.class */
public enum HmacAlgorithm {
    HmacMD5("HmacMD5"),
    HmacSHA1("HmacSHA1"),
    HmacSHA256("HmacSHA256"),
    HmacSHA384("HmacSHA384"),
    HmacSHA512("HmacSHA512"),
    HmacSM3("HmacSM3"),
    SM4CMAC("SM4CMAC");

    private final String value;

    HmacAlgorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
