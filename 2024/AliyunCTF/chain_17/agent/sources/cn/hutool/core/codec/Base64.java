package cn.hutool.core.codec;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Base64.class */
public class Base64 {
    private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;

    public static byte[] encode(byte[] arr, boolean lineSep) {
        if (lineSep) {
            return java.util.Base64.getMimeEncoder().encode(arr);
        }
        return java.util.Base64.getEncoder().encode(arr);
    }

    @Deprecated
    public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
        return Base64Encoder.encodeUrlSafe(arr, lineSep);
    }

    public static String encode(CharSequence source) {
        return encode(source, DEFAULT_CHARSET);
    }

    public static String encodeUrlSafe(CharSequence source) {
        return encodeUrlSafe(source, DEFAULT_CHARSET);
    }

    public static String encode(CharSequence source, String charset) {
        return encode(source, CharsetUtil.charset(charset));
    }

    public static String encodeWithoutPadding(CharSequence source, String charset) {
        return encodeWithoutPadding(StrUtil.bytes(source, charset));
    }

    @Deprecated
    public static String encodeUrlSafe(CharSequence source, String charset) {
        return encodeUrlSafe(source, CharsetUtil.charset(charset));
    }

    public static String encode(CharSequence source, Charset charset) {
        return encode(StrUtil.bytes(source, charset));
    }

    public static String encodeUrlSafe(CharSequence source, Charset charset) {
        return encodeUrlSafe(StrUtil.bytes(source, charset));
    }

    public static String encode(byte[] source) {
        return java.util.Base64.getEncoder().encodeToString(source);
    }

    public static String encodeWithoutPadding(byte[] source) {
        return java.util.Base64.getEncoder().withoutPadding().encodeToString(source);
    }

    public static String encodeUrlSafe(byte[] source) {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(source);
    }

    public static String encode(InputStream in) {
        return encode(IoUtil.readBytes(in));
    }

    public static String encodeUrlSafe(InputStream in) {
        return encodeUrlSafe(IoUtil.readBytes(in));
    }

    public static String encode(File file) {
        return encode(FileUtil.readBytes(file));
    }

    public static String encodeUrlSafe(File file) {
        return encodeUrlSafe(FileUtil.readBytes(file));
    }

    public static String encodeStr(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
        return StrUtil.str(encode(arr, isMultiLine, isUrlSafe), DEFAULT_CHARSET);
    }

    public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
        return Base64Encoder.encode(arr, isMultiLine, isUrlSafe);
    }

    public static String decodeStrGbk(CharSequence source) {
        return Base64Decoder.decodeStr(source, CharsetUtil.CHARSET_GBK);
    }

    public static String decodeStr(CharSequence source) {
        return Base64Decoder.decodeStr(source);
    }

    public static String decodeStr(CharSequence source, String charset) {
        return decodeStr(source, CharsetUtil.charset(charset));
    }

    public static String decodeStr(CharSequence source, Charset charset) {
        return Base64Decoder.decodeStr(source, charset);
    }

    public static File decodeToFile(CharSequence base64, File destFile) {
        return FileUtil.writeBytes(Base64Decoder.decode(base64), destFile);
    }

    public static void decodeToStream(CharSequence base64, OutputStream out, boolean isCloseOut) {
        IoUtil.write(out, isCloseOut, Base64Decoder.decode(base64));
    }

    public static byte[] decode(CharSequence base64) {
        return Base64Decoder.decode(base64);
    }

    public static byte[] decode(byte[] in) {
        return Base64Decoder.decode(in);
    }

    public static boolean isBase64(CharSequence base64) {
        if (base64 == null || base64.length() < 2) {
            return false;
        }
        byte[] bytes = StrUtil.utf8Bytes(base64);
        if (bytes.length != base64.length()) {
            return false;
        }
        return isBase64(bytes);
    }

    public static boolean isBase64(byte[] base64Bytes) {
        if (base64Bytes == null || base64Bytes.length < 3) {
            return false;
        }
        boolean hasPadding = false;
        for (byte base64Byte : base64Bytes) {
            if (hasPadding) {
                if (61 != base64Byte) {
                    return false;
                }
            } else if (61 == base64Byte) {
                hasPadding = true;
            } else {
                if (false == (Base64Decoder.isBase64Code(base64Byte) || isWhiteSpace(base64Byte))) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isWhiteSpace(byte byteToCheck) {
        switch (byteToCheck) {
            case 9:
            case 10:
            case 13:
            case 32:
                return true;
            default:
                return false;
        }
    }
}
