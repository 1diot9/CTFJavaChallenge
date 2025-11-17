package org.h2.security;

import org.h2.util.Bits;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/Fog.class */
public class Fog implements BlockCipher {
    private int key;

    @Override // org.h2.security.BlockCipher
    public void encrypt(byte[] bArr, int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3 += 16) {
            encryptBlock(bArr, bArr, i3);
        }
    }

    @Override // org.h2.security.BlockCipher
    public void decrypt(byte[] bArr, int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3 += 16) {
            decryptBlock(bArr, bArr, i3);
        }
    }

    private void encryptBlock(byte[] bArr, byte[] bArr2, int i) {
        int readInt = Bits.readInt(bArr, i);
        int readInt2 = Bits.readInt(bArr, i + 4);
        int readInt3 = Bits.readInt(bArr, i + 8);
        int readInt4 = Bits.readInt(bArr, i + 12);
        int i2 = this.key;
        int rotateLeft = Integer.rotateLeft(readInt ^ i2, readInt2);
        int rotateLeft2 = Integer.rotateLeft(readInt3 ^ i2, readInt2);
        int rotateLeft3 = Integer.rotateLeft(readInt2 ^ i2, rotateLeft);
        int rotateLeft4 = Integer.rotateLeft(readInt4 ^ i2, rotateLeft);
        Bits.writeInt(bArr2, i, rotateLeft);
        Bits.writeInt(bArr2, i + 4, rotateLeft3);
        Bits.writeInt(bArr2, i + 8, rotateLeft2);
        Bits.writeInt(bArr2, i + 12, rotateLeft4);
    }

    private void decryptBlock(byte[] bArr, byte[] bArr2, int i) {
        int readInt = Bits.readInt(bArr, i);
        int readInt2 = Bits.readInt(bArr, i + 4);
        int readInt3 = Bits.readInt(bArr, i + 8);
        int readInt4 = Bits.readInt(bArr, i + 12);
        int i2 = this.key;
        int rotateRight = Integer.rotateRight(readInt2, readInt) ^ i2;
        int rotateRight2 = Integer.rotateRight(readInt4, readInt) ^ i2;
        int rotateRight3 = Integer.rotateRight(readInt, rotateRight) ^ i2;
        int rotateRight4 = Integer.rotateRight(readInt3, rotateRight) ^ i2;
        Bits.writeInt(bArr2, i, rotateRight3);
        Bits.writeInt(bArr2, i + 4, rotateRight);
        Bits.writeInt(bArr2, i + 8, rotateRight4);
        Bits.writeInt(bArr2, i + 12, rotateRight2);
    }

    @Override // org.h2.security.BlockCipher
    public int getKeyLength() {
        return 16;
    }

    @Override // org.h2.security.BlockCipher
    public void setKey(byte[] bArr) {
        this.key = (int) Bits.readLong(bArr, 0);
    }
}
