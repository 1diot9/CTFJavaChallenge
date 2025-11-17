package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.io.doubleparser.JavaDoubleParser;
import com.fasterxml.jackson.core.io.doubleparser.JavaFloatParser;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/NumberInput.class */
public final class NumberInput {

    @Deprecated
    public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
    static final long L_BILLION = 1000000000;
    static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static int parseInt(char[] ch2, int off, int len) {
        if (len > 0 && ch2[off] == '+') {
            off++;
            len--;
        }
        int num = ch2[(off + len) - 1] - '0';
        switch (len) {
            case 2:
                num += (ch2[off] - '0') * 10;
                break;
            case 3:
                int i = off;
                off++;
                num += (ch2[i] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
            case 4:
                int i2 = off;
                off++;
                num += (ch2[i2] - '0') * 1000;
                int i3 = off;
                off++;
                num += (ch2[i3] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
            case 5:
                int i4 = off;
                off++;
                num += (ch2[i4] - '0') * 10000;
                int i22 = off;
                off++;
                num += (ch2[i22] - '0') * 1000;
                int i32 = off;
                off++;
                num += (ch2[i32] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
            case 6:
                int i5 = off;
                off++;
                num += (ch2[i5] - '0') * 100000;
                int i42 = off;
                off++;
                num += (ch2[i42] - '0') * 10000;
                int i222 = off;
                off++;
                num += (ch2[i222] - '0') * 1000;
                int i322 = off;
                off++;
                num += (ch2[i322] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
            case 7:
                int i6 = off;
                off++;
                num += (ch2[i6] - '0') * 1000000;
                int i52 = off;
                off++;
                num += (ch2[i52] - '0') * 100000;
                int i422 = off;
                off++;
                num += (ch2[i422] - '0') * 10000;
                int i2222 = off;
                off++;
                num += (ch2[i2222] - '0') * 1000;
                int i3222 = off;
                off++;
                num += (ch2[i3222] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
            case 8:
                int i7 = off;
                off++;
                num += (ch2[i7] - '0') * 10000000;
                int i62 = off;
                off++;
                num += (ch2[i62] - '0') * 1000000;
                int i522 = off;
                off++;
                num += (ch2[i522] - '0') * 100000;
                int i4222 = off;
                off++;
                num += (ch2[i4222] - '0') * 10000;
                int i22222 = off;
                off++;
                num += (ch2[i22222] - '0') * 1000;
                int i32222 = off;
                off++;
                num += (ch2[i32222] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
            case 9:
                int i8 = off;
                off++;
                num += (ch2[i8] - '0') * 100000000;
                int i72 = off;
                off++;
                num += (ch2[i72] - '0') * 10000000;
                int i622 = off;
                off++;
                num += (ch2[i622] - '0') * 1000000;
                int i5222 = off;
                off++;
                num += (ch2[i5222] - '0') * 100000;
                int i42222 = off;
                off++;
                num += (ch2[i42222] - '0') * 10000;
                int i222222 = off;
                off++;
                num += (ch2[i222222] - '0') * 1000;
                int i322222 = off;
                off++;
                num += (ch2[i322222] - '0') * 100;
                num += (ch2[off] - '0') * 10;
                break;
        }
        return num;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x00ba, code lost:            if (r8 < r0) goto L43;     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00bd, code lost:            r1 = r8;        r8 = r8 + 1;        r0 = r4.charAt(r1);     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00ca, code lost:            if (r0 > '9') goto L58;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00d0, code lost:            if (r0 >= '0') goto L49;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00d8, code lost:            r9 = (r9 * 10) + (r0 - '0');     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00e7, code lost:            if (r8 < r0) goto L59;     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00d7, code lost:            return java.lang.Integer.parseInt(r4);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int parseInt(java.lang.String r4) {
        /*
            Method dump skipped, instructions count: 247
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.io.NumberInput.parseInt(java.lang.String):int");
    }

    public static long parseLong(char[] ch2, int off, int len) {
        int len1 = len - 9;
        long val = parseInt(ch2, off, len1) * L_BILLION;
        return val + parseInt(ch2, off + len1, 9);
    }

    public static long parseLong19(char[] ch2, int off, boolean negative) {
        long num = 0;
        for (int i = 0; i < 19; i++) {
            char c = ch2[off + i];
            num = (num * 10) + (c - '0');
        }
        return negative ? -num : num;
    }

    public static long parseLong(String s) {
        int length = s.length();
        if (length <= 9) {
            return parseInt(s);
        }
        return Long.parseLong(s);
    }

    public static boolean inLongRange(char[] ch2, int off, int len, boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) {
            return true;
        }
        if (len > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = ch2[off + i] - cmpStr.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static boolean inLongRange(String s, boolean negative) {
        String cmp = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmp.length();
        int alen = s.length();
        if (alen < cmpLen) {
            return true;
        }
        if (alen > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; i++) {
            int diff = s.charAt(i) - cmp.charAt(i);
            if (diff != 0) {
                return diff < 0;
            }
        }
        return true;
    }

    public static int parseAsInt(String s, int def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        int i = 0;
        char sign = s2.charAt(0);
        if (sign == '+') {
            s2 = s2.substring(1);
            len = s2.length();
        } else if (sign == '-') {
            i = 1;
        }
        while (i < len) {
            char c = s2.charAt(i);
            if (c <= '9' && c >= '0') {
                i++;
            } else {
                try {
                    return (int) parseDouble(s2, true);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
        }
        try {
            return Integer.parseInt(s2);
        } catch (NumberFormatException e2) {
            return def;
        }
    }

    public static long parseAsLong(String s, long def) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        int i = 0;
        char sign = s2.charAt(0);
        if (sign == '+') {
            s2 = s2.substring(1);
            len = s2.length();
        } else if (sign == '-') {
            i = 1;
        }
        while (i < len) {
            char c = s2.charAt(i);
            if (c <= '9' && c >= '0') {
                i++;
            } else {
                try {
                    return (long) parseDouble(s2, true);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
        }
        try {
            return Long.parseLong(s2);
        } catch (NumberFormatException e2) {
            return def;
        }
    }

    public static double parseAsDouble(String s, double def) {
        return parseAsDouble(s, def, false);
    }

    public static double parseAsDouble(String s, double def, boolean useFastParser) {
        if (s == null) {
            return def;
        }
        String s2 = s.trim();
        int len = s2.length();
        if (len == 0) {
            return def;
        }
        try {
            return parseDouble(s2, useFastParser);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static double parseDouble(String s) throws NumberFormatException {
        return parseDouble(s, false);
    }

    public static double parseDouble(String s, boolean useFastParser) throws NumberFormatException {
        return useFastParser ? JavaDoubleParser.parseDouble(s) : Double.parseDouble(s);
    }

    public static float parseFloat(String s) throws NumberFormatException {
        return parseFloat(s, false);
    }

    public static float parseFloat(String s, boolean useFastParser) throws NumberFormatException {
        return useFastParser ? JavaFloatParser.parseFloat(s) : Float.parseFloat(s);
    }

    public static BigDecimal parseBigDecimal(String s) throws NumberFormatException {
        return BigDecimalParser.parse(s);
    }

    public static BigDecimal parseBigDecimal(String s, boolean useFastParser) throws NumberFormatException {
        if (useFastParser) {
            return BigDecimalParser.parseWithFastParser(s);
        }
        return BigDecimalParser.parse(s);
    }

    public static BigDecimal parseBigDecimal(char[] ch2, int off, int len) throws NumberFormatException {
        return BigDecimalParser.parse(ch2, off, len);
    }

    public static BigDecimal parseBigDecimal(char[] ch2, int off, int len, boolean useFastParser) throws NumberFormatException {
        if (useFastParser) {
            return BigDecimalParser.parseWithFastParser(ch2, off, len);
        }
        return BigDecimalParser.parse(ch2, off, len);
    }

    public static BigDecimal parseBigDecimal(char[] ch2) throws NumberFormatException {
        return BigDecimalParser.parse(ch2);
    }

    public static BigDecimal parseBigDecimal(char[] ch2, boolean useFastParser) throws NumberFormatException {
        if (useFastParser) {
            return BigDecimalParser.parseWithFastParser(ch2, 0, ch2.length);
        }
        return BigDecimalParser.parse(ch2);
    }

    public static BigInteger parseBigInteger(String s) throws NumberFormatException {
        return new BigInteger(s);
    }

    public static BigInteger parseBigInteger(String s, boolean useFastParser) throws NumberFormatException {
        if (useFastParser) {
            return BigIntegerParser.parseWithFastParser(s);
        }
        return parseBigInteger(s);
    }

    public static BigInteger parseBigIntegerWithRadix(String s, int radix, boolean useFastParser) throws NumberFormatException {
        if (useFastParser) {
            return BigIntegerParser.parseWithFastParser(s, radix);
        }
        return new BigInteger(s, radix);
    }
}
