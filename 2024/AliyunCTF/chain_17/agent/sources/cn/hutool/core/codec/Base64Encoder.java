package cn.hutool.core.codec;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base64Encoder.class */
public class Base64Encoder {
    private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
    private static final byte[] STANDARD_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};

    public static byte[] encode(byte[] arr, boolean lineSep) {
        return encode(arr, lineSep, false);
    }

    public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
        return encode(arr, lineSep, true);
    }

    public static String encode(CharSequence source) {
        return encode(source, DEFAULT_CHARSET);
    }

    public static String encodeUrlSafe(CharSequence source) {
        return encodeUrlSafe(source, DEFAULT_CHARSET);
    }

    public static String encode(CharSequence source, Charset charset) {
        return encode(StrUtil.bytes(source, charset));
    }

    public static String encodeUrlSafe(CharSequence source, Charset charset) {
        return encodeUrlSafe(StrUtil.bytes(source, charset));
    }

    public static String encode(byte[] source) {
        return StrUtil.str(encode(source, false), DEFAULT_CHARSET);
    }

    public static String encodeUrlSafe(byte[] source) {
        return StrUtil.str(encodeUrlSafe(source, false), DEFAULT_CHARSET);
    }

    public static String encodeStr(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
        return StrUtil.str(encode(arr, isMultiLine, isUrlSafe), DEFAULT_CHARSET);
    }

    public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
        if (null == arr) {
            return null;
        }
        int len = arr.length;
        if (len == 0) {
            return new byte[0];
        }
        int evenlen = (len / 3) * 3;
        int cnt = (((len - 1) / 3) + 1) << 2;
        int destlen = cnt + (isMultiLine ? ((cnt - 1) / 76) << 1 : 0);
        byte[] dest = new byte[destlen];
        byte[] encodeTable = isUrlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < evenlen) {
            int i = s;
            int s2 = s + 1;
            int s3 = s2 + 1;
            int i2 = ((arr[i] & 255) << 16) | ((arr[s2] & 255) << 8);
            s = s3 + 1;
            int i3 = i2 | (arr[s3] & 255);
            int i4 = d;
            int d2 = d + 1;
            dest[i4] = encodeTable[(i3 >>> 18) & 63];
            int d3 = d2 + 1;
            dest[d2] = encodeTable[(i3 >>> 12) & 63];
            int d4 = d3 + 1;
            dest[d3] = encodeTable[(i3 >>> 6) & 63];
            d = d4 + 1;
            dest[d4] = encodeTable[i3 & 63];
            if (isMultiLine) {
                cc++;
                if (cc == 19 && d < destlen - 2) {
                    int d5 = d + 1;
                    dest[d] = 13;
                    d = d5 + 1;
                    dest[d5] = 10;
                    cc = 0;
                }
            }
        }
        int left = len - evenlen;
        if (left > 0) {
            int i5 = ((arr[evenlen] & 255) << 10) | (left == 2 ? (arr[len - 1] & 255) << 2 : 0);
            dest[destlen - 4] = encodeTable[i5 >> 12];
            dest[destlen - 3] = encodeTable[(i5 >>> 6) & 63];
            if (isUrlSafe) {
                int urlSafeLen = destlen - 2;
                if (2 == left) {
                    dest[destlen - 2] = encodeTable[i5 & 63];
                    urlSafeLen++;
                }
                byte[] urlSafeDest = new byte[urlSafeLen];
                System.arraycopy(dest, 0, urlSafeDest, 0, urlSafeLen);
                return urlSafeDest;
            }
            dest[destlen - 2] = left == 2 ? encodeTable[i5 & 63] : (byte) 61;
            dest[destlen - 1] = 61;
        }
        return dest;
    }
}
