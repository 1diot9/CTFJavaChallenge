package org.h2.store.fs.encrypt;

import org.apache.tomcat.util.bcel.Const;
import org.h2.security.BlockCipher;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/fs/encrypt/XTS.class */
class XTS {
    private static final int GF_128_FEEDBACK = 135;
    private static final int CIPHER_BLOCK_SIZE = 16;
    private final BlockCipher cipher;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XTS(BlockCipher blockCipher) {
        this.cipher = blockCipher;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void encrypt(long j, int i, byte[] bArr, int i2) {
        byte[] initTweak = initTweak(j);
        int i3 = 0;
        while (i3 + 16 <= i) {
            if (i3 > 0) {
                updateTweak(initTweak);
            }
            xorTweak(bArr, i3 + i2, initTweak);
            this.cipher.encrypt(bArr, i3 + i2, 16);
            xorTweak(bArr, i3 + i2, initTweak);
            i3 += 16;
        }
        if (i3 < i) {
            updateTweak(initTweak);
            swap(bArr, i3 + i2, (i3 - 16) + i2, i - i3);
            xorTweak(bArr, (i3 - 16) + i2, initTweak);
            this.cipher.encrypt(bArr, (i3 - 16) + i2, 16);
            xorTweak(bArr, (i3 - 16) + i2, initTweak);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void decrypt(long j, int i, byte[] bArr, int i2) {
        byte[] initTweak = initTweak(j);
        byte[] bArr2 = initTweak;
        int i3 = 0;
        while (i3 + 16 <= i) {
            if (i3 > 0) {
                updateTweak(initTweak);
                if (i3 + 16 + 16 > i && i3 + 16 < i) {
                    bArr2 = (byte[]) initTweak.clone();
                    updateTweak(initTweak);
                }
            }
            xorTweak(bArr, i3 + i2, initTweak);
            this.cipher.decrypt(bArr, i3 + i2, 16);
            xorTweak(bArr, i3 + i2, initTweak);
            i3 += 16;
        }
        if (i3 < i) {
            swap(bArr, i3, (i3 - 16) + i2, (i - i3) + i2);
            xorTweak(bArr, (i3 - 16) + i2, bArr2);
            this.cipher.decrypt(bArr, (i3 - 16) + i2, 16);
            xorTweak(bArr, (i3 - 16) + i2, bArr2);
        }
    }

    private byte[] initTweak(long j) {
        byte[] bArr = new byte[16];
        int i = 0;
        while (i < 16) {
            bArr[i] = (byte) (j & 255);
            i++;
            j >>>= 8;
        }
        this.cipher.encrypt(bArr, 0, 16);
        return bArr;
    }

    private static void xorTweak(byte[] bArr, int i, byte[] bArr2) {
        for (int i2 = 0; i2 < 16; i2++) {
            int i3 = i + i2;
            bArr[i3] = (byte) (bArr[i3] ^ bArr2[i2]);
        }
    }

    private static void updateTweak(byte[] bArr) {
        byte b = 0;
        byte b2 = 0;
        for (int i = 0; i < 16; i++) {
            b2 = (byte) ((bArr[i] >> 7) & 1);
            bArr[i] = (byte) (((bArr[i] << 1) + b) & Const.MAX_ARRAY_DIMENSIONS);
            b = b2;
        }
        if (b2 != 0) {
            bArr[0] = (byte) (bArr[0] ^ 135);
        }
    }

    private static void swap(byte[] bArr, int i, int i2, int i3) {
        for (int i4 = 0; i4 < i3; i4++) {
            byte b = bArr[i + i4];
            bArr[i + i4] = bArr[i2 + i4];
            bArr[i2 + i4] = b;
        }
    }
}
