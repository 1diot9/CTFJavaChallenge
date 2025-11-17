package org.apache.logging.log4j.util;

import cn.hutool.core.text.StrPool;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/Strings.class */
public final class Strings {
    public static final String EMPTY = "";
    private static final String COMMA_DELIMITED_RE = "\\s*,\\s*";
    private static final ThreadLocal<StringBuilder> tempStr = ThreadLocal.withInitial(StringBuilder::new);
    public static final String[] EMPTY_ARRAY = new String[0];
    public static final String LINE_SEPARATOR = SystemPropertiesPropertySource.getSystemProperty("line.separator", StrPool.LF);

    public static String dquote(final String str) {
        return '\"' + str + '\"';
    }

    public static boolean isBlank(final String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null) {
            return null;
        }
        return join(iterable.iterator(), separator);
    }

    public static String join(final Iterator<?> iterator, final char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }
        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String[] splitList(final String string) {
        return string != null ? string.split(COMMA_DELIMITED_RE) : new String[0];
    }

    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static String quote(final String str) {
        return '\'' + str + '\'';
    }

    public static String trimToNull(final String str) {
        String ts = str == null ? null : str.trim();
        if (isEmpty(ts)) {
            return null;
        }
        return ts;
    }

    private Strings() {
    }

    public static String toRootLowerCase(final String str) {
        return str.toLowerCase(Locale.ROOT);
    }

    public static String toRootUpperCase(final String str) {
        return str.toUpperCase(Locale.ROOT);
    }

    public static String concat(final String str1, final String str2) {
        if (isEmpty(str1)) {
            return str2;
        }
        if (isEmpty(str2)) {
            return str1;
        }
        StringBuilder sb = tempStr.get();
        try {
            String sb2 = sb.append(str1).append(str2).toString();
            sb.setLength(0);
            return sb2;
        } catch (Throwable th) {
            sb.setLength(0);
            throw th;
        }
    }

    public static String repeat(final String str, final int count) {
        Objects.requireNonNull(str, "str");
        if (count < 0) {
            throw new IllegalArgumentException("count");
        }
        StringBuilder sb = tempStr.get();
        for (int index = 0; index < count; index++) {
            try {
                sb.append(str);
            } catch (Throwable th) {
                sb.setLength(0);
                throw th;
            }
        }
        String sb2 = sb.toString();
        sb.setLength(0);
        return sb2;
    }
}
