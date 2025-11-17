package org.apache.tomcat.util.buf;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/StringUtils.class */
public final class StringUtils {
    private static final String EMPTY_STRING = "";

    private StringUtils() {
    }

    public static String join(String[] array) {
        if (array == null) {
            return "";
        }
        return join(Arrays.asList(array));
    }

    public static void join(String[] array, char separator, StringBuilder sb) {
        if (array == null) {
            return;
        }
        join(Arrays.asList(array), separator, sb);
    }

    public static String join(Collection<String> collection) {
        return join(collection, ',');
    }

    public static String join(Collection<String> collection, char separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        join(collection, separator, result);
        return result.toString();
    }

    public static void join(Iterable<String> iterable, char separator, StringBuilder sb) {
        join(iterable, separator, x -> {
            return x;
        }, sb);
    }

    public static <T> void join(T[] array, char separator, Function<T, String> function, StringBuilder sb) {
        if (array == null) {
            return;
        }
        join(Arrays.asList(array), separator, function, sb);
    }

    public static <T> void join(Iterable<T> iterable, char separator, Function<T, String> function, StringBuilder sb) {
        if (iterable == null) {
            return;
        }
        boolean first = true;
        for (T value : iterable) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(function.apply(value));
        }
    }
}
