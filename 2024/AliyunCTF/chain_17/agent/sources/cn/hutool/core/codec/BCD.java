package cn.hutool.core.codec;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

@Deprecated
/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/BCD.class */
public class BCD {
    public static byte[] strToBcd(String asc) {
        int j;
        int i;
        Assert.notNull(asc, "ASCII must not be null!", new Object[0]);
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = CustomBooleanEditor.VALUE_0 + asc;
            len = asc.length();
        }
        if (len >= 2) {
            len >>= 1;
        }
        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes();
        for (int p = 0; p < asc.length() / 2; p++) {
            if (abt[2 * p] >= 48 && abt[2 * p] <= 57) {
                j = abt[2 * p] - 48;
            } else if (abt[2 * p] >= 97 && abt[2 * p] <= 122) {
                j = (abt[2 * p] - 97) + 10;
            } else {
                j = (abt[2 * p] - 65) + 10;
            }
            if (abt[(2 * p) + 1] >= 48 && abt[(2 * p) + 1] <= 57) {
                i = abt[(2 * p) + 1] - 48;
            } else if (abt[(2 * p) + 1] >= 97 && abt[(2 * p) + 1] <= 122) {
                i = (abt[(2 * p) + 1] - 97) + 10;
            } else {
                i = (abt[(2 * p) + 1] - 65) + 10;
            }
            int k = i;
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    public static byte[] ascToBcd(byte[] ascii) {
        Assert.notNull(ascii, "Ascii must be not null!", new Object[0]);
        return ascToBcd(ascii, ascii.length);
    }

    public static byte[] ascToBcd(byte[] ascii, int ascLength) {
        byte ascToBcd;
        Assert.notNull(ascii, "Ascii must be not null!", new Object[0]);
        byte[] bcd = new byte[ascLength / 2];
        int j = 0;
        for (int i = 0; i < (ascLength + 1) / 2; i++) {
            int i2 = j;
            j++;
            bcd[i] = ascToBcd(ascii[i2]);
            int i3 = i;
            if (j >= ascLength) {
                ascToBcd = 0;
            } else {
                j++;
                ascToBcd = ascToBcd(ascii[j]);
            }
            bcd[i3] = (byte) (ascToBcd + (bcd[i] << 4));
        }
        return bcd;
    }

    public static String bcdToStr(byte[] bytes) {
        Assert.notNull(bytes, "Bcd bytes must be not null!", new Object[0]);
        char[] temp = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            char val = (char) (((bytes[i] & 240) >> 4) & 15);
            temp[i * 2] = (char) (val > '\t' ? (val + 'A') - 10 : val + '0');
            char val2 = (char) (bytes[i] & 15);
            temp[(i * 2) + 1] = (char) (val2 > '\t' ? (val2 + 'A') - 10 : val2 + '0');
        }
        return new String(temp);
    }

    private static byte ascToBcd(byte asc) {
        byte bcd;
        if (asc >= 48 && asc <= 57) {
            bcd = (byte) (asc - 48);
        } else if (asc >= 65 && asc <= 70) {
            bcd = (byte) ((asc - 65) + 10);
        } else if (asc >= 97 && asc <= 102) {
            bcd = (byte) ((asc - 97) + 10);
        } else {
            bcd = (byte) (asc - 48);
        }
        return bcd;
    }
}
