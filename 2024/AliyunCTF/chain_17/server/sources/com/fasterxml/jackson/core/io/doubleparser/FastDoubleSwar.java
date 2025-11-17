package com.fasterxml.jackson.core.io.doubleparser;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FastDoubleSwar.class */
class FastDoubleSwar {
    FastDoubleSwar() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isDigit(char c) {
        return ((char) (c - '0')) < '\n';
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isDigit(byte c) {
        return ((char) (c - 48)) < '\n';
    }

    public static boolean isEightDigits(byte[] a, int offset) {
        return isEightDigitsUtf8(readLongLE(a, offset));
    }

    public static boolean isEightDigits(char[] a, int offset) {
        long first = a[offset] | (a[offset + 1] << 16) | (a[offset + 2] << 32) | (a[offset + 3] << 48);
        long second = a[offset + 4] | (a[offset + 5] << 16) | (a[offset + 6] << 32) | (a[offset + 7] << 48);
        return isEightDigitsUtf16(first, second);
    }

    public static boolean isEightDigits(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; i++) {
            char ch2 = a.charAt(i + offset);
            success &= isDigit(ch2);
        }
        return success;
    }

    public static boolean isEightDigitsUtf16(long first, long second) {
        long fval = first - 13511005043687472L;
        long sval = second - 13511005043687472L;
        long fpre = (first + 19703549022044230L) | fval;
        long spre = (second + 19703549022044230L) | sval;
        return ((fpre | spre) & (-35747867511423104L)) == 0;
    }

    public static boolean isEightDigitsUtf8(long chunk) {
        long val = chunk - 3472328296227680304L;
        long predicate = ((chunk + 5063812098665367110L) | val) & (-9187201950435737472L);
        return predicate == 0;
    }

    public static boolean isEightZeroes(byte[] a, int offset) {
        return isEightZeroesUtf8(readLongLE(a, offset));
    }

    public static boolean isEightZeroes(CharSequence a, int offset) {
        boolean success = true;
        for (int i = 0; i < 8; i++) {
            success &= '0' == a.charAt(i + offset);
        }
        return success;
    }

    public static boolean isEightZeroes(char[] a, int offset) {
        long first = a[offset] | (a[offset + 1] << 16) | (a[offset + 2] << 32) | (a[offset + 3] << 48);
        long second = a[offset + 4] | (a[offset + 5] << 16) | (a[offset + 6] << 32) | (a[offset + 7] << 48);
        return isEightZeroesUtf16(first, second);
    }

    public static boolean isEightZeroesUtf16(long first, long second) {
        return first == 13511005043687472L && second == 13511005043687472L;
    }

    public static boolean isEightZeroesUtf8(long chunk) {
        return chunk == 3472328296227680304L;
    }

    public static int parseEightDigitsUtf16(long first, long second) {
        long fval = first - 13511005043687472L;
        long sval = second - 13511005043687472L;
        return ((int) ((sval * 281475406208040961L) >>> 48)) + (((int) ((fval * 281475406208040961L) >>> 48)) * 10000);
    }

    public static int readIntBE(byte[] a, int offset) {
        return ((a[offset] & 255) << 24) | ((a[offset + 1] & 255) << 16) | ((a[offset + 2] & 255) << 8) | (a[offset + 3] & 255);
    }

    public static int readIntLE(byte[] a, int offset) {
        return ((a[offset + 3] & 255) << 24) | ((a[offset + 2] & 255) << 16) | ((a[offset + 1] & 255) << 8) | (a[offset] & 255);
    }

    public static long readLongBE(byte[] a, int offset) {
        return ((a[offset] & 255) << 56) | ((a[offset + 1] & 255) << 48) | ((a[offset + 2] & 255) << 40) | ((a[offset + 3] & 255) << 32) | ((a[offset + 4] & 255) << 24) | ((a[offset + 5] & 255) << 16) | ((a[offset + 6] & 255) << 8) | (a[offset + 7] & 255);
    }

    public static long readLongLE(byte[] a, int offset) {
        return ((a[offset + 7] & 255) << 56) | ((a[offset + 6] & 255) << 48) | ((a[offset + 5] & 255) << 40) | ((a[offset + 4] & 255) << 32) | ((a[offset + 3] & 255) << 24) | ((a[offset + 2] & 255) << 16) | ((a[offset + 1] & 255) << 8) | (a[offset] & 255);
    }

    public static int tryToParseEightDigits(char[] a, int offset) {
        long first = a[offset] | (a[offset + 1] << 16) | (a[offset + 2] << 32) | (a[offset + 3] << 48);
        long second = a[offset + 4] | (a[offset + 5] << 16) | (a[offset + 6] << 32) | (a[offset + 7] << 48);
        return tryToParseEightDigitsUtf16(first, second);
    }

    public static int tryToParseEightDigits(byte[] a, int offset) {
        return tryToParseEightDigitsUtf8(readLongLE(a, offset));
    }

    public static int tryToParseEightDigits(CharSequence str, int offset) {
        long first = str.charAt(offset) | (str.charAt(offset + 1) << 16) | (str.charAt(offset + 2) << 32) | (str.charAt(offset + 3) << 48);
        long second = str.charAt(offset + 4) | (str.charAt(offset + 5) << 16) | (str.charAt(offset + 6) << 32) | (str.charAt(offset + 7) << 48);
        return tryToParseEightDigitsUtf16(first, second);
    }

    public static int tryToParseEightDigitsUtf16(long first, long second) {
        long fval = first - 13511005043687472L;
        long sval = second - 13511005043687472L;
        long fpre = (first + 19703549022044230L) | fval;
        long spre = (second + 19703549022044230L) | sval;
        if (((fpre | spre) & (-35747867511423104L)) != 0) {
            return -1;
        }
        return ((int) ((sval * 281475406208040961L) >>> 48)) + (((int) ((fval * 281475406208040961L) >>> 48)) * 10000);
    }

    public static int tryToParseEightDigitsUtf8(byte[] a, int offset) {
        return tryToParseEightDigitsUtf8(readLongLE(a, offset));
    }

    public static int tryToParseEightDigitsUtf8(long chunk) {
        long val = chunk - 3472328296227680304L;
        long predicate = ((chunk + 5063812098665367110L) | val) & (-9187201950435737472L);
        if (predicate != 0) {
            return -1;
        }
        long val2 = (val * 10) + (val >>> 8);
        return (int) ((((val2 & 1095216660735L) * 4294967296000100L) + (((val2 >>> 16) & 1095216660735L) * 42949672960001L)) >>> 32);
    }

    public static long tryToParseEightHexDigits(CharSequence str, int offset) {
        long first = (str.charAt(offset) << 48) | (str.charAt(offset + 1) << 32) | (str.charAt(offset + 2) << 16) | str.charAt(offset + 3);
        long second = (str.charAt(offset + 4) << 48) | (str.charAt(offset + 5) << 32) | (str.charAt(offset + 6) << 16) | str.charAt(offset + 7);
        return tryToParseEightHexDigitsUtf16(first, second);
    }

    public static long tryToParseEightHexDigits(char[] chars, int offset) {
        long first = (chars[offset] << 48) | (chars[offset + 1] << 32) | (chars[offset + 2] << 16) | chars[offset + 3];
        long second = (chars[offset + 4] << 48) | (chars[offset + 5] << 32) | (chars[offset + 6] << 16) | chars[offset + 7];
        return tryToParseEightHexDigitsUtf16(first, second);
    }

    public static long tryToParseEightHexDigits(byte[] a, int offset) {
        return tryToParseEightHexDigitsUtf8(readLongBE(a, offset));
    }

    public static long tryToParseEightHexDigitsUtf16(long first, long second) {
        long lfirst = tryToParseFourHexDigitsUtf16(first);
        long lsecond = tryToParseFourHexDigitsUtf16(second);
        return (lfirst << 16) | lsecond;
    }

    public static long tryToParseEightHexDigitsUtf8(long chunk) {
        long vec = (chunk | 2314885530818453536L) - 3472328296227680304L;
        long gt_09 = (vec + 8536140394893047414L) & (-9187201950435737472L);
        long ge_30 = vec + 5714873654208057167L;
        long ge_302 = ge_30 & (-9187201950435737472L);
        long le_37 = 3978709506094217015L + (vec ^ 9187201950435737471L);
        if (gt_09 != (ge_302 & le_37)) {
            return -1L;
        }
        long gt_09mask = (gt_09 >>> 7) * 255;
        long v = (vec & (gt_09mask ^ (-1))) | (vec - (2821266740684990247L & gt_09mask));
        long v2 = v | (v >>> 4);
        long v3 = v2 & 71777214294589695L;
        long v4 = v3 | (v3 >>> 8);
        long v5 = ((v4 >>> 16) & 4294901760L) | (v4 & 65535);
        return v5;
    }

    public static int tryToParseFourDigits(char[] a, int offset) {
        long first = a[offset] | (a[offset + 1] << 16) | (a[offset + 2] << 32) | (a[offset + 3] << 48);
        return tryToParseFourDigitsUtf16(first);
    }

    public static int tryToParseFourDigits(CharSequence str, int offset) {
        long first = str.charAt(offset) | (str.charAt(offset + 1) << 16) | (str.charAt(offset + 2) << 32) | (str.charAt(offset + 3) << 48);
        return tryToParseFourDigitsUtf16(first);
    }

    public static int tryToParseFourDigits(byte[] a, int offset) {
        return tryToParseFourDigitsUtf8(readIntLE(a, offset));
    }

    public static int tryToParseFourDigitsUtf16(long first) {
        long fval = first - 13511005043687472L;
        long fpre = (first + 19703549022044230L) | fval;
        if ((fpre & (-35747867511423104L)) != 0) {
            return -1;
        }
        return (int) ((fval * 281475406208040961L) >>> 48);
    }

    public static int tryToParseFourDigitsUtf8(int chunk) {
        int val = chunk - 808464432;
        int predicate = ((chunk + 1179010630) | val) & (-2139062144);
        if (predicate != 0) {
            return -1;
        }
        int val2 = (val * 2561) >>> 8;
        return ((val2 & 255) * 100) + ((val2 & 16711680) >> 16);
    }

    public static long tryToParseFourHexDigitsUtf16(long chunk) {
        long vec = chunk - 13511005043687472L;
        long gt_09 = (vec + 9220697983773212662L) & (-9223231297218904064L);
        long ge_30 = vec + 9209720292175216591L;
        long ge_302 = ge_30 & (-9223231297218904064L);
        long le_37 = 15481359945891895L + (vec ^ 9223231297218904063L);
        if (gt_09 != (ge_302 & le_37)) {
            return -1L;
        }
        long gt_09mask = (gt_09 >>> 15) * 65535;
        long v = (vec & (gt_09mask ^ (-1))) | (vec - (10977691597996071L & gt_09mask));
        long v2 = v | (v >>> 12);
        long v5 = (v2 | (v2 >>> 24)) & 65535;
        return v5;
    }

    public static int tryToParseUpTo7Digits(byte[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        while (from < to) {
            byte ch2 = str[from];
            success &= isDigit(ch2);
            result = ((10 * result) + ch2) - 48;
            from++;
        }
        if (success) {
            return result;
        }
        return -1;
    }

    public static int tryToParseUpTo7Digits(char[] str, int from, int to) {
        int result = 0;
        boolean success = true;
        while (from < to) {
            char ch2 = str[from];
            success &= isDigit(ch2);
            result = ((10 * result) + ch2) - 48;
            from++;
        }
        if (success) {
            return result;
        }
        return -1;
    }

    public static int tryToParseUpTo7Digits(CharSequence str, int from, int to) {
        int result = 0;
        boolean success = true;
        while (from < to) {
            char ch2 = str.charAt(from);
            success &= isDigit(ch2);
            result = ((10 * result) + ch2) - 48;
            from++;
        }
        if (success) {
            return result;
        }
        return -1;
    }

    public static void writeIntBE(byte[] a, int offset, int v) {
        a[offset] = (byte) (v >>> 24);
        a[offset + 1] = (byte) (v >>> 16);
        a[offset + 2] = (byte) (v >>> 8);
        a[offset + 3] = (byte) v;
    }

    public static void writeLongBE(byte[] a, int offset, long v) {
        a[offset] = (byte) (v >>> 56);
        a[offset + 1] = (byte) (v >>> 48);
        a[offset + 2] = (byte) (v >>> 40);
        a[offset + 3] = (byte) (v >>> 32);
        a[offset + 4] = (byte) (v >>> 24);
        a[offset + 5] = (byte) (v >>> 16);
        a[offset + 6] = (byte) (v >>> 8);
        a[offset + 7] = (byte) v;
    }

    public static double fma(double a, double b, double c) {
        return (a * b) + c;
    }
}
