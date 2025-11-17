package org.apache.tomcat.util.buf;

import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/HexUtils.class */
public final class HexUtils {
    private static final StringManager sm = StringManager.getManager((Class<?>) HexUtils.class);
    private static final int[] DEC = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15};
    private static final byte[] HEX = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static final char[] hex = "0123456789abcdef".toCharArray();

    public static int getDec(int index) {
        try {
            return DEC[index - 48];
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    public static byte getHex(int index) {
        return HEX[index];
    }

    public static String toHexString(char c) {
        StringBuilder sb = new StringBuilder(4);
        sb.append(hex[(c & 61440) >> 12]);
        sb.append(hex[(c & 3840) >> 8]);
        sb.append(hex[(c & 240) >> 4]);
        sb.append(hex[c & 15]);
        return sb.toString();
    }

    public static String toHexString(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            sb.append(hex[(aByte & 240) >> 4]).append(hex[aByte & 15]);
        }
        return sb.toString();
    }

    public static byte[] fromHexString(String input) {
        if (input == null) {
            return null;
        }
        if ((input.length() & 1) == 1) {
            throw new IllegalArgumentException(sm.getString("hexUtils.fromHex.oddDigits"));
        }
        char[] inputChars = input.toCharArray();
        byte[] result = new byte[input.length() >> 1];
        for (int i = 0; i < result.length; i++) {
            int upperNibble = getDec(inputChars[2 * i]);
            int lowerNibble = getDec(inputChars[(2 * i) + 1]);
            if (upperNibble < 0 || lowerNibble < 0) {
                throw new IllegalArgumentException(sm.getString("hexUtils.fromHex.nonHex"));
            }
            result[i] = (byte) ((upperNibble << 4) + lowerNibble);
        }
        return result;
    }
}
