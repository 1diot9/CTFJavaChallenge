package org.h2.security;

import java.security.MessageDigest;
import java.util.Arrays;
import org.h2.util.Bits;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/SHA3.class */
public final class SHA3 extends MessageDigest {
    private static final long[] ROUND_CONSTANTS;
    private final int digestLength;
    private final int rate;
    private long state00;
    private long state01;
    private long state02;
    private long state03;
    private long state04;
    private long state05;
    private long state06;
    private long state07;
    private long state08;
    private long state09;
    private long state10;
    private long state11;
    private long state12;
    private long state13;
    private long state14;
    private long state15;
    private long state16;
    private long state17;
    private long state18;
    private long state19;
    private long state20;
    private long state21;
    private long state22;
    private long state23;
    private long state24;
    private final byte[] buf;
    private int bufcnt;

    static {
        long[] jArr = new long[24];
        byte b = 1;
        for (int i = 0; i < 24; i++) {
            jArr[i] = 0;
            for (int i2 = 0; i2 < 7; i2++) {
                byte b2 = b;
                b = (byte) (b2 < 0 ? (b2 << 1) ^ Opcodes.LREM : b2 << 1);
                if ((b2 & 1) != 0) {
                    int i3 = i;
                    jArr[i3] = jArr[i3] ^ (1 << ((1 << i2) - 1));
                }
            }
        }
        ROUND_CONSTANTS = jArr;
    }

    public static SHA3 getSha3_224() {
        return new SHA3("SHA3-224", 28);
    }

    public static SHA3 getSha3_256() {
        return new SHA3("SHA3-256", 32);
    }

    public static SHA3 getSha3_384() {
        return new SHA3("SHA3-384", 48);
    }

    public static SHA3 getSha3_512() {
        return new SHA3("SHA3-512", 64);
    }

    private SHA3(String str, int i) {
        super(str);
        this.digestLength = i;
        int i2 = 200 - (i * 2);
        this.rate = i2;
        this.buf = new byte[i2];
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // java.security.MessageDigestSpi
    protected byte[] engineDigest() {
        this.buf[this.bufcnt] = 6;
        Arrays.fill(this.buf, this.bufcnt + 1, this.rate, (byte) 0);
        byte[] bArr = this.buf;
        int i = this.rate - 1;
        bArr[i] = (byte) (bArr[i] | 128);
        absorbQueue();
        byte[] bArr2 = new byte[this.digestLength];
        switch (this.digestLength) {
            case 28:
                Bits.writeIntLE(bArr2, 24, (int) this.state03);
                break;
            case 32:
                Bits.writeLongLE(bArr2, 24, this.state03);
                break;
            case 48:
                Bits.writeLongLE(bArr2, 40, this.state05);
                Bits.writeLongLE(bArr2, 32, this.state04);
                Bits.writeLongLE(bArr2, 24, this.state03);
                break;
            case 64:
                Bits.writeLongLE(bArr2, 56, this.state07);
                Bits.writeLongLE(bArr2, 48, this.state06);
                Bits.writeLongLE(bArr2, 40, this.state05);
                Bits.writeLongLE(bArr2, 32, this.state04);
                Bits.writeLongLE(bArr2, 24, this.state03);
                break;
        }
        Bits.writeLongLE(bArr2, 16, this.state02);
        Bits.writeLongLE(bArr2, 8, this.state01);
        Bits.writeLongLE(bArr2, 0, this.state00);
        engineReset();
        return bArr2;
    }

    @Override // java.security.MessageDigestSpi
    protected int engineGetDigestLength() {
        return this.digestLength;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r25v0, types: [org.h2.security.SHA3] */
    @Override // java.security.MessageDigestSpi
    protected void engineReset() {
        this.state00 = 0L;
        this.state01 = 0L;
        0.state02 = this;
        this.state03 = this;
        this.state04 = 0L;
        0L.state05 = this;
        this.state06 = this;
        this.state07 = 0L;
        0L.state08 = this;
        this.state09 = this;
        this.state10 = 0L;
        0L.state11 = this;
        this.state12 = this;
        this.state13 = 0L;
        0L.state14 = this;
        this.state15 = this;
        this.state16 = 0L;
        0L.state17 = this;
        this.state18 = this;
        this.state19 = 0L;
        0L.state20 = this;
        this.state21 = this;
        this.state22 = 0L;
        0L.state23 = this;
        this.state24 = this;
        Arrays.fill(this.buf, (byte) 0);
        this.bufcnt = 0;
    }

    @Override // java.security.MessageDigestSpi
    protected void engineUpdate(byte b) {
        byte[] bArr = this.buf;
        int i = this.bufcnt;
        this.bufcnt = i + 1;
        bArr[i] = b;
        if (this.bufcnt == this.rate) {
            absorbQueue();
        }
    }

    @Override // java.security.MessageDigestSpi
    protected void engineUpdate(byte[] bArr, int i, int i2) {
        while (i2 > 0) {
            if (this.bufcnt == 0 && i2 >= this.rate) {
                do {
                    absorb(bArr, i);
                    i += this.rate;
                    i2 -= this.rate;
                } while (i2 >= this.rate);
            } else {
                int min = Math.min(i2, this.rate - this.bufcnt);
                System.arraycopy(bArr, i, this.buf, this.bufcnt, min);
                this.bufcnt += min;
                i += min;
                i2 -= min;
                if (this.bufcnt == this.rate) {
                    absorbQueue();
                }
            }
        }
    }

    private void absorbQueue() {
        absorb(this.buf, 0);
        this.bufcnt = 0;
    }

    private void absorb(byte[] bArr, int i) {
        switch (this.digestLength) {
            case 28:
                this.state17 ^= Bits.readLongLE(bArr, i + 136);
            case 32:
                this.state13 ^= Bits.readLongLE(bArr, i + 104);
                this.state14 ^= Bits.readLongLE(bArr, i + 112);
                this.state15 ^= Bits.readLongLE(bArr, i + 120);
                this.state16 ^= Bits.readLongLE(bArr, i + 128);
            case 48:
                this.state09 ^= Bits.readLongLE(bArr, i + 72);
                this.state10 ^= Bits.readLongLE(bArr, i + 80);
                this.state11 ^= Bits.readLongLE(bArr, i + 88);
                this.state12 ^= Bits.readLongLE(bArr, i + 96);
                break;
        }
        this.state00 ^= Bits.readLongLE(bArr, i);
        this.state01 ^= Bits.readLongLE(bArr, i + 8);
        this.state02 ^= Bits.readLongLE(bArr, i + 16);
        this.state03 ^= Bits.readLongLE(bArr, i + 24);
        this.state04 ^= Bits.readLongLE(bArr, i + 32);
        this.state05 ^= Bits.readLongLE(bArr, i + 40);
        this.state06 ^= Bits.readLongLE(bArr, i + 48);
        this.state07 ^= Bits.readLongLE(bArr, i + 56);
        this.state08 ^= Bits.readLongLE(bArr, i + 64);
        for (int i2 = 0; i2 < 24; i2++) {
            long j = (((this.state00 ^ this.state05) ^ this.state10) ^ this.state15) ^ this.state20;
            long j2 = (((this.state01 ^ this.state06) ^ this.state11) ^ this.state16) ^ this.state21;
            long j3 = (((this.state02 ^ this.state07) ^ this.state12) ^ this.state17) ^ this.state22;
            long j4 = (((this.state03 ^ this.state08) ^ this.state13) ^ this.state18) ^ this.state23;
            long j5 = (((this.state04 ^ this.state09) ^ this.state14) ^ this.state19) ^ this.state24;
            long j6 = ((j2 << 1) | (j2 >>> 63)) ^ j5;
            this.state00 ^= j6;
            this.state05 ^= j6;
            this.state10 ^= j6;
            this.state15 ^= j6;
            this.state20 ^= j6;
            long j7 = ((j3 << 1) | (j3 >>> 63)) ^ j;
            this.state01 ^= j7;
            this.state06 ^= j7;
            this.state11 ^= j7;
            this.state16 ^= j7;
            this.state21 ^= j7;
            long j8 = ((j4 << 1) | (j4 >>> 63)) ^ j2;
            this.state02 ^= j8;
            this.state07 ^= j8;
            this.state12 ^= j8;
            this.state17 ^= j8;
            this.state22 ^= j8;
            long j9 = ((j5 << 1) | (j5 >>> 63)) ^ j3;
            this.state03 ^= j9;
            this.state08 ^= j9;
            this.state13 ^= j9;
            this.state18 ^= j9;
            this.state23 ^= j9;
            long j10 = ((j << 1) | (j >>> 63)) ^ j4;
            this.state04 ^= j10;
            this.state09 ^= j10;
            this.state14 ^= j10;
            this.state19 ^= j10;
            this.state24 ^= j10;
            long j11 = this.state00;
            long j12 = (this.state06 << 44) | (this.state06 >>> 20);
            long j13 = (this.state12 << 43) | (this.state12 >>> 21);
            long j14 = (this.state18 << 21) | (this.state18 >>> 43);
            long j15 = (this.state24 << 14) | (this.state24 >>> 50);
            long j16 = (this.state03 << 28) | (this.state03 >>> 36);
            long j17 = (this.state09 << 20) | (this.state09 >>> 44);
            long j18 = (this.state10 << 3) | (this.state10 >>> 61);
            long j19 = (this.state16 << 45) | (this.state16 >>> 19);
            long j20 = (this.state22 << 61) | (this.state22 >>> 3);
            long j21 = (this.state01 << 1) | (this.state01 >>> 63);
            long j22 = (this.state07 << 6) | (this.state07 >>> 58);
            long j23 = (this.state13 << 25) | (this.state13 >>> 39);
            long j24 = (this.state19 << 8) | (this.state19 >>> 56);
            long j25 = (this.state20 << 18) | (this.state20 >>> 46);
            long j26 = (this.state04 << 27) | (this.state04 >>> 37);
            long j27 = (this.state05 << 36) | (this.state05 >>> 28);
            long j28 = (this.state11 << 10) | (this.state11 >>> 54);
            long j29 = (this.state17 << 15) | (this.state17 >>> 49);
            long j30 = (this.state23 << 56) | (this.state23 >>> 8);
            long j31 = (this.state02 << 62) | (this.state02 >>> 2);
            long j32 = (this.state08 << 55) | (this.state08 >>> 9);
            long j33 = (this.state14 << 39) | (this.state14 >>> 25);
            long j34 = (this.state15 << 41) | (this.state15 >>> 23);
            long j35 = (this.state21 << 2) | (this.state21 >>> 62);
            this.state00 = (j11 ^ ((j12 ^ (-1)) & j13)) ^ ROUND_CONSTANTS[i2];
            this.state01 = j12 ^ ((j13 ^ (-1)) & j14);
            this.state02 = j13 ^ ((j14 ^ (-1)) & j15);
            this.state03 = j14 ^ ((j15 ^ (-1)) & j11);
            this.state04 = j15 ^ ((j11 ^ (-1)) & j12);
            this.state05 = j16 ^ ((j17 ^ (-1)) & j18);
            this.state06 = j17 ^ ((j18 ^ (-1)) & j19);
            this.state07 = j18 ^ ((j19 ^ (-1)) & j20);
            this.state08 = j19 ^ ((j20 ^ (-1)) & j16);
            this.state09 = j20 ^ ((j16 ^ (-1)) & j17);
            this.state10 = j21 ^ ((j22 ^ (-1)) & j23);
            this.state11 = j22 ^ ((j23 ^ (-1)) & j24);
            this.state12 = j23 ^ ((j24 ^ (-1)) & j25);
            this.state13 = j24 ^ ((j25 ^ (-1)) & j21);
            this.state14 = j25 ^ ((j21 ^ (-1)) & j22);
            this.state15 = j26 ^ ((j27 ^ (-1)) & j28);
            this.state16 = j27 ^ ((j28 ^ (-1)) & j29);
            this.state17 = j28 ^ ((j29 ^ (-1)) & j30);
            this.state18 = j29 ^ ((j30 ^ (-1)) & j26);
            this.state19 = j30 ^ ((j26 ^ (-1)) & j27);
            this.state20 = j31 ^ ((j32 ^ (-1)) & j33);
            this.state21 = j32 ^ ((j33 ^ (-1)) & j34);
            this.state22 = j33 ^ ((j34 ^ (-1)) & j35);
            this.state23 = j34 ^ ((j35 ^ (-1)) & j31);
            this.state24 = j35 ^ ((j31 ^ (-1)) & j32);
        }
    }
}
