package cn.hutool.core.util;

import cn.hutool.core.codec.Base16Codec;
import java.awt.Color;
import java.math.BigInteger;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/HexUtil.class */
public class HexUtil {
    public static boolean isHexNumber(String value) {
        if (StrUtil.startWith((CharSequence) value, '-')) {
            return false;
        }
        int index = 0;
        if (value.startsWith("0x", 0) || value.startsWith("0X", 0)) {
            index = 0 + 2;
        } else if (value.startsWith("#", 0)) {
            index = 0 + 1;
        }
        try {
            new BigInteger(value.substring(index), 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(String str, Charset charset) {
        return encodeHex(StrUtil.bytes(str, charset), true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return (toLowerCase ? Base16Codec.CODEC_LOWER : Base16Codec.CODEC_UPPER).encode(data);
    }

    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(String data, Charset charset) {
        return encodeHexStr(StrUtil.bytes(data, charset), true);
    }

    public static String encodeHexStr(String data) {
        return encodeHexStr(data, CharsetUtil.CHARSET_UTF_8);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }

    public static String decodeHexStr(String hexStr) {
        return decodeHexStr(hexStr, CharsetUtil.CHARSET_UTF_8);
    }

    public static String decodeHexStr(String hexStr, Charset charset) {
        if (StrUtil.isEmpty(hexStr)) {
            return hexStr;
        }
        return StrUtil.str(decodeHex(hexStr), charset);
    }

    public static String decodeHexStr(char[] hexData, Charset charset) {
        return StrUtil.str(decodeHex(hexData), charset);
    }

    public static byte[] decodeHex(String hexStr) {
        return decodeHex((CharSequence) hexStr);
    }

    public static byte[] decodeHex(char[] hexData) {
        return decodeHex(String.valueOf(hexData));
    }

    public static byte[] decodeHex(CharSequence hexData) {
        return Base16Codec.CODEC_LOWER.decode(hexData);
    }

    public static String encodeColor(Color color) {
        return encodeColor(color, "#");
    }

    public static String encodeColor(Color color, String prefix) {
        StringBuilder builder = new StringBuilder(prefix);
        String colorHex = Integer.toHexString(color.getRed());
        if (1 == colorHex.length()) {
            builder.append('0');
        }
        builder.append(colorHex);
        String colorHex2 = Integer.toHexString(color.getGreen());
        if (1 == colorHex2.length()) {
            builder.append('0');
        }
        builder.append(colorHex2);
        String colorHex3 = Integer.toHexString(color.getBlue());
        if (1 == colorHex3.length()) {
            builder.append('0');
        }
        builder.append(colorHex3);
        return builder.toString();
    }

    public static Color decodeColor(String hexColor) {
        return Color.decode(hexColor);
    }

    public static String toUnicodeHex(int value) {
        StringBuilder builder = new StringBuilder(6);
        builder.append("\\u");
        String hex = toHex(value);
        int len = hex.length();
        if (len < 4) {
            builder.append((CharSequence) "0000", 0, 4 - len);
        }
        builder.append(hex);
        return builder.toString();
    }

    public static String toUnicodeHex(char ch2) {
        return Base16Codec.CODEC_LOWER.toUnicodeHex(ch2);
    }

    public static String toHex(int value) {
        return Integer.toHexString(value);
    }

    public static int hexToInt(String value) {
        return Integer.parseInt(value, 16);
    }

    public static String toHex(long value) {
        return Long.toHexString(value);
    }

    public static long hexToLong(String value) {
        return Long.parseLong(value, 16);
    }

    public static void appendHex(StringBuilder builder, byte b, boolean toLowerCase) {
        (toLowerCase ? Base16Codec.CODEC_LOWER : Base16Codec.CODEC_UPPER).appendHex(builder, b);
    }

    public static BigInteger toBigInteger(String hexStr) {
        if (null == hexStr) {
            return null;
        }
        return new BigInteger(hexStr, 16);
    }

    public static String format(String hexStr) {
        int length = hexStr.length();
        StringBuilder builder = StrUtil.builder(length + (length / 2));
        builder.append(hexStr.charAt(0)).append(hexStr.charAt(1));
        for (int i = 2; i < length - 1; i += 2) {
            builder.append(' ').append(hexStr.charAt(i)).append(hexStr.charAt(i + 1));
        }
        return builder.toString();
    }
}
