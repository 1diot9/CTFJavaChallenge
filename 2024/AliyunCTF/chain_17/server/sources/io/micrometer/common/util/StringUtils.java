package io.micrometer.common.util;

import io.micrometer.common.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/util/StringUtils.class */
public final class StringUtils {
    public static boolean isBlank(@Nullable String string) {
        if (isEmpty(string)) {
            return true;
        }
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(@Nullable String string) {
        return !isBlank(string);
    }

    public static boolean isEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable String string) {
        return !isEmpty(string);
    }

    public static String truncate(String string, int maxLength) {
        if (string.length() > maxLength) {
            return string.substring(0, maxLength);
        }
        return string;
    }

    public static String truncate(String string, int maxLength, String truncationIndicator) {
        if (truncationIndicator.length() >= maxLength) {
            throw new IllegalArgumentException("maxLength must be greater than length of truncationIndicator");
        }
        if (string.length() > maxLength) {
            int remainingLength = maxLength - truncationIndicator.length();
            return string.substring(0, remainingLength) + truncationIndicator;
        }
        return string;
    }

    private StringUtils() {
    }
}
