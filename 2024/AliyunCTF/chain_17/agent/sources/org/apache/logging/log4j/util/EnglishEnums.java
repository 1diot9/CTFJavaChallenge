package org.apache.logging.log4j.util;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/EnglishEnums.class */
public final class EnglishEnums {
    private EnglishEnums() {
    }

    public static <T extends Enum<T>> T valueOf(Class<T> cls, String str) {
        return (T) valueOf(cls, str, null);
    }

    public static <T extends Enum<T>> T valueOf(Class<T> cls, String str, T t) {
        return str == null ? t : (T) Enum.valueOf(cls, Strings.toRootUpperCase(str));
    }
}
