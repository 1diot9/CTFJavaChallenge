package org.h2.security;

import cn.hutool.core.text.StrPool;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.h2.util.Bits;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/security/SHA256.class */
public class SHA256 {
    private SHA256() {
    }

    public static byte[] getHashWithSalt(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = new byte[bArr.length + bArr2.length];
        System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
        System.arraycopy(bArr2, 0, bArr3, bArr.length, bArr2.length);
        return getHash(bArr3, true);
    }

    public static byte[] getKeyPasswordHash(String str, char[] cArr) {
        String str2 = str + StrPool.AT;
        byte[] bArr = new byte[2 * (str2.length() + cArr.length)];
        int i = 0;
        int length = str2.length();
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str2.charAt(i2);
            int i3 = i;
            int i4 = i + 1;
            bArr[i3] = (byte) (charAt >> '\b');
            i = i4 + 1;
            bArr[i4] = (byte) charAt;
        }
        for (char c : cArr) {
            int i5 = i;
            int i6 = i + 1;
            bArr[i5] = (byte) (c >> '\b');
            i = i6 + 1;
            bArr[i6] = (byte) c;
        }
        Arrays.fill(cArr, (char) 0);
        return getHash(bArr, true);
    }

    public static byte[] getHMAC(byte[] bArr, byte[] bArr2) {
        return initMac(bArr).doFinal(bArr2);
    }

    private static Mac initMac(byte[] bArr) {
        if (bArr.length == 0) {
            bArr = new byte[1];
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(bArr, "HmacSHA256"));
            return mac;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getPBKDF2(byte[] bArr, byte[] bArr2, int i, int i2) {
        int i3;
        byte[] bArr3 = new byte[i2];
        Mac initMac = initMac(bArr);
        byte[] bArr4 = new byte[64 + Math.max(32, bArr2.length + 4)];
        byte[] bArr5 = null;
        int i4 = 1;
        for (int i5 = 0; i5 < i2; i5 += 32) {
            for (int i6 = 0; i6 < i; i6++) {
                if (i6 == 0) {
                    System.arraycopy(bArr2, 0, bArr4, 0, bArr2.length);
                    Bits.writeInt(bArr4, bArr2.length, i4);
                    i3 = bArr2.length + 4;
                } else {
                    System.arraycopy(bArr5, 0, bArr4, 0, 32);
                    i3 = 32;
                }
                initMac.update(bArr4, 0, i3);
                bArr5 = initMac.doFinal();
                for (int i7 = 0; i7 < 32 && i7 + i5 < i2; i7++) {
                    int i8 = i7 + i5;
                    bArr3[i8] = (byte) (bArr3[i8] ^ bArr5[i7]);
                }
            }
            i4++;
        }
        Arrays.fill(bArr, (byte) 0);
        return bArr3;
    }

    public static byte[] getHash(byte[] bArr, boolean z) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bArr);
            if (z) {
                Arrays.fill(bArr, (byte) 0);
            }
            return digest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
