package cn.hutool.crypto.symmetric;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/symmetric/SymmetricAlgorithm.class */
public enum SymmetricAlgorithm {
    AES("AES"),
    ARCFOUR("ARCFOUR"),
    Blowfish("Blowfish"),
    DES("DES"),
    DESede("DESede"),
    RC2("RC2"),
    PBEWithMD5AndDES("PBEWithMD5AndDES"),
    PBEWithSHA1AndDESede("PBEWithSHA1AndDESede"),
    PBEWithSHA1AndRC2_40("PBEWithSHA1AndRC2_40");

    private final String value;

    SymmetricAlgorithm(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
