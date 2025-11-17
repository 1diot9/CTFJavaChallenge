package org.springframework.util;

import java.util.Base64;

@Deprecated(since = "6.0.5", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/Base64Utils.class */
public abstract class Base64Utils {
    public static byte[] encode(byte[] src) {
        if (src.length == 0) {
            return src;
        }
        return Base64.getEncoder().encode(src);
    }

    public static byte[] decode(byte[] src) {
        if (src.length == 0) {
            return src;
        }
        return Base64.getDecoder().decode(src);
    }

    public static byte[] encodeUrlSafe(byte[] src) {
        if (src.length == 0) {
            return src;
        }
        return Base64.getUrlEncoder().encode(src);
    }

    public static byte[] decodeUrlSafe(byte[] src) {
        if (src.length == 0) {
            return src;
        }
        return Base64.getUrlDecoder().decode(src);
    }

    public static String encodeToString(byte[] src) {
        if (src.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(src);
    }

    public static byte[] decodeFromString(String src) {
        if (src.isEmpty()) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(src);
    }

    public static String encodeToUrlSafeString(byte[] src) {
        return Base64.getUrlEncoder().encodeToString(src);
    }

    public static byte[] decodeFromUrlSafeString(String src) {
        return Base64.getUrlDecoder().decode(src);
    }
}
