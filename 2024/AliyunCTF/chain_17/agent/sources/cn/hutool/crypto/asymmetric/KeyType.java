package cn.hutool.crypto.asymmetric;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/crypto/asymmetric/KeyType.class */
public enum KeyType {
    PublicKey(1),
    PrivateKey(2),
    SecretKey(3);

    private final int value;

    KeyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
