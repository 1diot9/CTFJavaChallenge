package org.h2.security;

import org.h2.message.DbException;
import org.h2.util.Bits;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/XTEA.class */
public class XTEA implements BlockCipher {
    private static final int DELTA = -1640531527;
    private int k0;
    private int k1;
    private int k2;
    private int k3;
    private int k4;
    private int k5;
    private int k6;
    private int k7;
    private int k8;
    private int k9;
    private int k10;
    private int k11;
    private int k12;
    private int k13;
    private int k14;
    private int k15;
    private int k16;
    private int k17;
    private int k18;
    private int k19;
    private int k20;
    private int k21;
    private int k22;
    private int k23;
    private int k24;
    private int k25;
    private int k26;
    private int k27;
    private int k28;
    private int k29;
    private int k30;
    private int k31;

    @Override // org.h2.security.BlockCipher
    public void setKey(byte[] bArr) {
        int[] iArr = new int[4];
        for (int i = 0; i < 16; i += 4) {
            iArr[i / 4] = Bits.readInt(bArr, i);
        }
        int[] iArr2 = new int[32];
        int i2 = 0;
        int i3 = 0;
        while (i2 < 32) {
            int i4 = i2;
            int i5 = i2 + 1;
            iArr2[i4] = i3 + iArr[i3 & 3];
            i3 -= 1640531527;
            i2 = i5 + 1;
            iArr2[i5] = i3 + iArr[(i3 >>> 11) & 3];
        }
        this.k0 = iArr2[0];
        this.k1 = iArr2[1];
        this.k2 = iArr2[2];
        this.k3 = iArr2[3];
        this.k4 = iArr2[4];
        this.k5 = iArr2[5];
        this.k6 = iArr2[6];
        this.k7 = iArr2[7];
        this.k8 = iArr2[8];
        this.k9 = iArr2[9];
        this.k10 = iArr2[10];
        this.k11 = iArr2[11];
        this.k12 = iArr2[12];
        this.k13 = iArr2[13];
        this.k14 = iArr2[14];
        this.k15 = iArr2[15];
        this.k16 = iArr2[16];
        this.k17 = iArr2[17];
        this.k18 = iArr2[18];
        this.k19 = iArr2[19];
        this.k20 = iArr2[20];
        this.k21 = iArr2[21];
        this.k22 = iArr2[22];
        this.k23 = iArr2[23];
        this.k24 = iArr2[24];
        this.k25 = iArr2[25];
        this.k26 = iArr2[26];
        this.k27 = iArr2[27];
        this.k28 = iArr2[28];
        this.k29 = iArr2[29];
        this.k30 = iArr2[30];
        this.k31 = iArr2[31];
    }

    @Override // org.h2.security.BlockCipher
    public void encrypt(byte[] bArr, int i, int i2) {
        if (i2 % 16 != 0) {
            throw DbException.getInternalError("unaligned len " + i2);
        }
        for (int i3 = i; i3 < i + i2; i3 += 8) {
            encryptBlock(bArr, bArr, i3);
        }
    }

    @Override // org.h2.security.BlockCipher
    public void decrypt(byte[] bArr, int i, int i2) {
        if (i2 % 16 != 0) {
            throw DbException.getInternalError("unaligned len " + i2);
        }
        for (int i3 = i; i3 < i + i2; i3 += 8) {
            decryptBlock(bArr, bArr, i3);
        }
    }

    private void encryptBlock(byte[] bArr, byte[] bArr2, int i) {
        int readInt = Bits.readInt(bArr, i);
        int readInt2 = Bits.readInt(bArr, i + 4);
        int i2 = readInt + ((((readInt2 << 4) ^ (readInt2 >>> 5)) + readInt2) ^ this.k0);
        int i3 = readInt2 + ((((i2 >>> 5) ^ (i2 << 4)) + i2) ^ this.k1);
        int i4 = i2 + ((((i3 << 4) ^ (i3 >>> 5)) + i3) ^ this.k2);
        int i5 = i3 + ((((i4 >>> 5) ^ (i4 << 4)) + i4) ^ this.k3);
        int i6 = i4 + ((((i5 << 4) ^ (i5 >>> 5)) + i5) ^ this.k4);
        int i7 = i5 + ((((i6 >>> 5) ^ (i6 << 4)) + i6) ^ this.k5);
        int i8 = i6 + ((((i7 << 4) ^ (i7 >>> 5)) + i7) ^ this.k6);
        int i9 = i7 + ((((i8 >>> 5) ^ (i8 << 4)) + i8) ^ this.k7);
        int i10 = i8 + ((((i9 << 4) ^ (i9 >>> 5)) + i9) ^ this.k8);
        int i11 = i9 + ((((i10 >>> 5) ^ (i10 << 4)) + i10) ^ this.k9);
        int i12 = i10 + ((((i11 << 4) ^ (i11 >>> 5)) + i11) ^ this.k10);
        int i13 = i11 + ((((i12 >>> 5) ^ (i12 << 4)) + i12) ^ this.k11);
        int i14 = i12 + ((((i13 << 4) ^ (i13 >>> 5)) + i13) ^ this.k12);
        int i15 = i13 + ((((i14 >>> 5) ^ (i14 << 4)) + i14) ^ this.k13);
        int i16 = i14 + ((((i15 << 4) ^ (i15 >>> 5)) + i15) ^ this.k14);
        int i17 = i15 + ((((i16 >>> 5) ^ (i16 << 4)) + i16) ^ this.k15);
        int i18 = i16 + ((((i17 << 4) ^ (i17 >>> 5)) + i17) ^ this.k16);
        int i19 = i17 + ((((i18 >>> 5) ^ (i18 << 4)) + i18) ^ this.k17);
        int i20 = i18 + ((((i19 << 4) ^ (i19 >>> 5)) + i19) ^ this.k18);
        int i21 = i19 + ((((i20 >>> 5) ^ (i20 << 4)) + i20) ^ this.k19);
        int i22 = i20 + ((((i21 << 4) ^ (i21 >>> 5)) + i21) ^ this.k20);
        int i23 = i21 + ((((i22 >>> 5) ^ (i22 << 4)) + i22) ^ this.k21);
        int i24 = i22 + ((((i23 << 4) ^ (i23 >>> 5)) + i23) ^ this.k22);
        int i25 = i23 + ((((i24 >>> 5) ^ (i24 << 4)) + i24) ^ this.k23);
        int i26 = i24 + ((((i25 << 4) ^ (i25 >>> 5)) + i25) ^ this.k24);
        int i27 = i25 + ((((i26 >>> 5) ^ (i26 << 4)) + i26) ^ this.k25);
        int i28 = i26 + ((((i27 << 4) ^ (i27 >>> 5)) + i27) ^ this.k26);
        int i29 = i27 + ((((i28 >>> 5) ^ (i28 << 4)) + i28) ^ this.k27);
        int i30 = i28 + ((((i29 << 4) ^ (i29 >>> 5)) + i29) ^ this.k28);
        int i31 = i29 + ((((i30 >>> 5) ^ (i30 << 4)) + i30) ^ this.k29);
        int i32 = i30 + ((((i31 << 4) ^ (i31 >>> 5)) + i31) ^ this.k30);
        int i33 = i31 + ((((i32 >>> 5) ^ (i32 << 4)) + i32) ^ this.k31);
        Bits.writeInt(bArr2, i, i32);
        Bits.writeInt(bArr2, i + 4, i33);
    }

    private void decryptBlock(byte[] bArr, byte[] bArr2, int i) {
        int readInt = Bits.readInt(bArr, i);
        int readInt2 = Bits.readInt(bArr, i + 4) - ((((readInt >>> 5) ^ (readInt << 4)) + readInt) ^ this.k31);
        int i2 = readInt - ((((readInt2 << 4) ^ (readInt2 >>> 5)) + readInt2) ^ this.k30);
        int i3 = readInt2 - ((((i2 >>> 5) ^ (i2 << 4)) + i2) ^ this.k29);
        int i4 = i2 - ((((i3 << 4) ^ (i3 >>> 5)) + i3) ^ this.k28);
        int i5 = i3 - ((((i4 >>> 5) ^ (i4 << 4)) + i4) ^ this.k27);
        int i6 = i4 - ((((i5 << 4) ^ (i5 >>> 5)) + i5) ^ this.k26);
        int i7 = i5 - ((((i6 >>> 5) ^ (i6 << 4)) + i6) ^ this.k25);
        int i8 = i6 - ((((i7 << 4) ^ (i7 >>> 5)) + i7) ^ this.k24);
        int i9 = i7 - ((((i8 >>> 5) ^ (i8 << 4)) + i8) ^ this.k23);
        int i10 = i8 - ((((i9 << 4) ^ (i9 >>> 5)) + i9) ^ this.k22);
        int i11 = i9 - ((((i10 >>> 5) ^ (i10 << 4)) + i10) ^ this.k21);
        int i12 = i10 - ((((i11 << 4) ^ (i11 >>> 5)) + i11) ^ this.k20);
        int i13 = i11 - ((((i12 >>> 5) ^ (i12 << 4)) + i12) ^ this.k19);
        int i14 = i12 - ((((i13 << 4) ^ (i13 >>> 5)) + i13) ^ this.k18);
        int i15 = i13 - ((((i14 >>> 5) ^ (i14 << 4)) + i14) ^ this.k17);
        int i16 = i14 - ((((i15 << 4) ^ (i15 >>> 5)) + i15) ^ this.k16);
        int i17 = i15 - ((((i16 >>> 5) ^ (i16 << 4)) + i16) ^ this.k15);
        int i18 = i16 - ((((i17 << 4) ^ (i17 >>> 5)) + i17) ^ this.k14);
        int i19 = i17 - ((((i18 >>> 5) ^ (i18 << 4)) + i18) ^ this.k13);
        int i20 = i18 - ((((i19 << 4) ^ (i19 >>> 5)) + i19) ^ this.k12);
        int i21 = i19 - ((((i20 >>> 5) ^ (i20 << 4)) + i20) ^ this.k11);
        int i22 = i20 - ((((i21 << 4) ^ (i21 >>> 5)) + i21) ^ this.k10);
        int i23 = i21 - ((((i22 >>> 5) ^ (i22 << 4)) + i22) ^ this.k9);
        int i24 = i22 - ((((i23 << 4) ^ (i23 >>> 5)) + i23) ^ this.k8);
        int i25 = i23 - ((((i24 >>> 5) ^ (i24 << 4)) + i24) ^ this.k7);
        int i26 = i24 - ((((i25 << 4) ^ (i25 >>> 5)) + i25) ^ this.k6);
        int i27 = i25 - ((((i26 >>> 5) ^ (i26 << 4)) + i26) ^ this.k5);
        int i28 = i26 - ((((i27 << 4) ^ (i27 >>> 5)) + i27) ^ this.k4);
        int i29 = i27 - ((((i28 >>> 5) ^ (i28 << 4)) + i28) ^ this.k3);
        int i30 = i28 - ((((i29 << 4) ^ (i29 >>> 5)) + i29) ^ this.k2);
        int i31 = i29 - ((((i30 >>> 5) ^ (i30 << 4)) + i30) ^ this.k1);
        Bits.writeInt(bArr2, i, i30 - ((((i31 << 4) ^ (i31 >>> 5)) + i31) ^ this.k0));
        Bits.writeInt(bArr2, i + 4, i31);
    }

    @Override // org.h2.security.BlockCipher
    public int getKeyLength() {
        return 16;
    }
}
