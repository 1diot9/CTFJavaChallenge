package org.h2.security;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/BlockCipher.class */
public interface BlockCipher {
    public static final int ALIGN = 16;

    void setKey(byte[] bArr);

    void encrypt(byte[] bArr, int i, int i2);

    void decrypt(byte[] bArr, int i, int i2);

    int getKeyLength();
}
