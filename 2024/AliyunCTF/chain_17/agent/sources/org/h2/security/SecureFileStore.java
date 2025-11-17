package org.h2.security;

import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.util.Bits;
import org.h2.util.MathUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/SecureFileStore.class */
public class SecureFileStore extends FileStore {
    private byte[] key;
    private final BlockCipher cipher;
    private final BlockCipher cipherForInitVector;
    private byte[] buffer;
    private long pos;
    private final byte[] bufferForInitVector;
    private final int keyIterations;

    public SecureFileStore(DataHandler dataHandler, String str, String str2, String str3, byte[] bArr, int i) {
        super(dataHandler, str, str2);
        this.buffer = new byte[4];
        this.key = bArr;
        this.cipher = CipherFactory.getBlockCipher(str3);
        this.cipherForInitVector = CipherFactory.getBlockCipher(str3);
        this.keyIterations = i;
        this.bufferForInitVector = new byte[16];
    }

    @Override // org.h2.store.FileStore
    protected byte[] generateSalt() {
        return MathUtils.secureRandomBytes(16);
    }

    @Override // org.h2.store.FileStore
    protected void initKey(byte[] bArr) {
        this.key = SHA256.getHashWithSalt(this.key, bArr);
        for (int i = 0; i < this.keyIterations; i++) {
            this.key = SHA256.getHash(this.key, true);
        }
        this.cipher.setKey(this.key);
        this.key = SHA256.getHash(this.key, true);
        this.cipherForInitVector.setKey(this.key);
    }

    @Override // org.h2.store.FileStore
    protected void writeDirect(byte[] bArr, int i, int i2) {
        super.write(bArr, i, i2);
        this.pos += i2;
    }

    @Override // org.h2.store.FileStore
    public void write(byte[] bArr, int i, int i2) {
        if (this.buffer.length < bArr.length) {
            this.buffer = new byte[i2];
        }
        System.arraycopy(bArr, i, this.buffer, 0, i2);
        xorInitVector(this.buffer, 0, i2, this.pos);
        this.cipher.encrypt(this.buffer, 0, i2);
        super.write(this.buffer, 0, i2);
        this.pos += i2;
    }

    @Override // org.h2.store.FileStore
    public void readFullyDirect(byte[] bArr, int i, int i2) {
        super.readFully(bArr, i, i2);
        this.pos += i2;
    }

    @Override // org.h2.store.FileStore
    public void readFully(byte[] bArr, int i, int i2) {
        super.readFully(bArr, i, i2);
        int i3 = 0;
        while (true) {
            if (i3 >= i2) {
                break;
            }
            if (bArr[i3] == 0) {
                i3++;
            } else {
                this.cipher.decrypt(bArr, i, i2);
                xorInitVector(bArr, i, i2, this.pos);
                break;
            }
        }
        this.pos += i2;
    }

    @Override // org.h2.store.FileStore
    public void seek(long j) {
        this.pos = j;
        super.seek(j);
    }

    private void xorInitVector(byte[] bArr, int i, int i2, long j) {
        byte[] bArr2 = this.bufferForInitVector;
        while (i2 > 0) {
            for (int i3 = 0; i3 < 16; i3 += 8) {
                Bits.writeLong(bArr2, i3, (j + i3) >>> 3);
            }
            this.cipherForInitVector.encrypt(bArr2, 0, 16);
            for (int i4 = 0; i4 < 16; i4++) {
                int i5 = i + i4;
                bArr[i5] = (byte) (bArr[i5] ^ bArr2[i4]);
            }
            j += 16;
            i += 16;
            i2 -= 16;
        }
    }
}
