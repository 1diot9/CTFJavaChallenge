package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/DigestUtils.class */
public abstract class DigestUtils {
    private static final String MD5_ALGORITHM_NAME = "MD5";
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static byte[] md5Digest(byte[] bytes) {
        return digest(MD5_ALGORITHM_NAME, bytes);
    }

    public static byte[] md5Digest(InputStream inputStream) throws IOException {
        return digest(MD5_ALGORITHM_NAME, inputStream);
    }

    public static String md5DigestAsHex(byte[] bytes) {
        return digestAsHexString(MD5_ALGORITHM_NAME, bytes);
    }

    public static String md5DigestAsHex(InputStream inputStream) throws IOException {
        return digestAsHexString(MD5_ALGORITHM_NAME, inputStream);
    }

    public static StringBuilder appendMd5DigestAsHex(byte[] bytes, StringBuilder builder) {
        return appendDigestAsHex(MD5_ALGORITHM_NAME, bytes, builder);
    }

    public static StringBuilder appendMd5DigestAsHex(InputStream inputStream, StringBuilder builder) throws IOException {
        return appendDigestAsHex(MD5_ALGORITHM_NAME, inputStream, builder);
    }

    private static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + algorithm + "\"", ex);
        }
    }

    private static byte[] digest(String algorithm, byte[] bytes) {
        return getDigest(algorithm).digest(bytes);
    }

    private static byte[] digest(String algorithm, InputStream inputStream) throws IOException {
        MessageDigest messageDigest = getDigest(algorithm);
        if (inputStream instanceof UpdateMessageDigestInputStream) {
            UpdateMessageDigestInputStream digestInputStream = (UpdateMessageDigestInputStream) inputStream;
            digestInputStream.updateMessageDigest(messageDigest);
            return messageDigest.digest();
        }
        byte[] buffer = new byte[8192];
        while (true) {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            } else {
                return messageDigest.digest();
            }
        }
    }

    private static String digestAsHexString(String algorithm, byte[] bytes) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return new String(hexDigest);
    }

    private static String digestAsHexString(String algorithm, InputStream inputStream) throws IOException {
        char[] hexDigest = digestAsHexChars(algorithm, inputStream);
        return new String(hexDigest);
    }

    private static StringBuilder appendDigestAsHex(String algorithm, byte[] bytes, StringBuilder builder) {
        char[] hexDigest = digestAsHexChars(algorithm, bytes);
        return builder.append(hexDigest);
    }

    private static StringBuilder appendDigestAsHex(String algorithm, InputStream inputStream, StringBuilder builder) throws IOException {
        char[] hexDigest = digestAsHexChars(algorithm, inputStream);
        return builder.append(hexDigest);
    }

    private static char[] digestAsHexChars(String algorithm, byte[] bytes) {
        byte[] digest = digest(algorithm, bytes);
        return encodeHex(digest);
    }

    private static char[] digestAsHexChars(String algorithm, InputStream inputStream) throws IOException {
        byte[] digest = digest(algorithm, inputStream);
        return encodeHex(digest);
    }

    private static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < chars.length) {
                byte b = bytes[i2 / 2];
                chars[i2] = HEX_CHARS[(b >>> 4) & 15];
                chars[i2 + 1] = HEX_CHARS[b & 15];
                i = i2 + 2;
            } else {
                return chars;
            }
        }
    }
}
