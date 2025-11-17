package cn.hutool.core.codec;

import cn.hutool.core.lang.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Rot.class */
public class Rot {
    private static final char aCHAR = 'a';
    private static final char zCHAR = 'z';
    private static final char ACHAR = 'A';
    private static final char ZCHAR = 'Z';
    private static final char CHAR0 = '0';
    private static final char CHAR9 = '9';

    public static String encode13(String message) {
        return encode13(message, true);
    }

    public static String encode13(String message, boolean isEncodeNumber) {
        return encode(message, 13, isEncodeNumber);
    }

    public static String encode(String message, int offset, boolean isEncodeNumber) {
        Assert.notNull(message, "message must not be null", new Object[0]);
        int len = message.length();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = encodeChar(message.charAt(i), offset, isEncodeNumber);
        }
        return new String(chars);
    }

    public static String decode13(String rot) {
        return decode13(rot, true);
    }

    public static String decode13(String rot, boolean isDecodeNumber) {
        return decode(rot, 13, isDecodeNumber);
    }

    public static String decode(String rot, int offset, boolean isDecodeNumber) {
        Assert.notNull(rot, "rot must not be null", new Object[0]);
        int len = rot.length();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = decodeChar(rot.charAt(i), offset, isDecodeNumber);
        }
        return new String(chars);
    }

    private static char encodeChar(char c, int offset, boolean isDecodeNumber) {
        if (isDecodeNumber && c >= '0' && c <= '9') {
            c = (char) (((char) ((((char) (c - '0')) + offset) % 10)) + '0');
        }
        if (c >= 'A' && c <= 'Z') {
            c = (char) (((char) ((((char) (c - 'A')) + offset) % 26)) + 'A');
        } else if (c >= 'a' && c <= 'z') {
            c = (char) (((char) ((((char) (c - 'a')) + offset) % 26)) + 'a');
        }
        return c;
    }

    private static char decodeChar(char c, int offset, boolean isDecodeNumber) {
        int temp;
        int temp2 = c;
        if (isDecodeNumber && temp2 >= 48 && temp2 <= 57) {
            int temp3 = (temp2 - 48) - offset;
            while (temp3 < 0) {
                temp3 += 10;
            }
            temp2 = temp3 + 48;
        }
        if (temp2 >= 65 && temp2 <= 90) {
            int i = (temp2 - 65) - offset;
            while (true) {
                temp = i;
                if (temp >= 0) {
                    break;
                }
                i = 26 + temp;
            }
            temp2 = temp + 65;
        } else if (temp2 >= 97 && temp2 <= 122) {
            int temp4 = (temp2 - 97) - offset;
            if (temp4 < 0) {
                temp4 = 26 + temp4;
            }
            temp2 = temp4 + 97;
        }
        return (char) temp2;
    }
}
