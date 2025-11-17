package cn.hutool.crypto;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/CipherMode.class */
public enum CipherMode {
    encrypt(1),
    decrypt(2),
    wrap(3),
    unwrap(4);

    private final int value;

    CipherMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
