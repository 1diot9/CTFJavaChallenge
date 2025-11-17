package org.apache.logging.log4j.util;

import java.time.temporal.Temporal;
import java.util.Map;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/StringBuilders.class */
public final class StringBuilders {
    private static final Class<?> timeClass;
    private static final Class<?> dateClass;

    static {
        Class<?> clazz;
        Class<?> clazz2;
        try {
            clazz = Class.forName("java.sql.Time");
        } catch (ClassNotFoundException e) {
            clazz = null;
        }
        timeClass = clazz;
        try {
            clazz2 = Class.forName("java.sql.Date");
        } catch (ClassNotFoundException e2) {
            clazz2 = null;
        }
        dateClass = clazz2;
    }

    private StringBuilders() {
    }

    public static StringBuilder appendDqValue(final StringBuilder sb, final Object value) {
        return sb.append('\"').append(value).append('\"');
    }

    public static StringBuilder appendKeyDqValue(final StringBuilder sb, final Map.Entry<String, String> entry) {
        return appendKeyDqValue(sb, entry.getKey(), entry.getValue());
    }

    public static StringBuilder appendKeyDqValue(final StringBuilder sb, final String key, final Object value) {
        return sb.append(key).append('=').append('\"').append(value).append('\"');
    }

    public static void appendValue(final StringBuilder stringBuilder, final Object obj) {
        if (!appendSpecificTypes(stringBuilder, obj)) {
            stringBuilder.append(obj);
        }
    }

    public static boolean appendSpecificTypes(final StringBuilder stringBuilder, final Object obj) {
        if (obj == null || (obj instanceof String)) {
            stringBuilder.append((String) obj);
            return true;
        }
        if (obj instanceof StringBuilderFormattable) {
            ((StringBuilderFormattable) obj).formatTo(stringBuilder);
            return true;
        }
        if (obj instanceof CharSequence) {
            stringBuilder.append((CharSequence) obj);
            return true;
        }
        if (obj instanceof Integer) {
            stringBuilder.append(((Integer) obj).intValue());
            return true;
        }
        if (obj instanceof Long) {
            stringBuilder.append(((Long) obj).longValue());
            return true;
        }
        if (obj instanceof Double) {
            stringBuilder.append(((Double) obj).doubleValue());
            return true;
        }
        if (obj instanceof Boolean) {
            stringBuilder.append(((Boolean) obj).booleanValue());
            return true;
        }
        if (obj instanceof Character) {
            stringBuilder.append(((Character) obj).charValue());
            return true;
        }
        if (obj instanceof Short) {
            stringBuilder.append((int) ((Short) obj).shortValue());
            return true;
        }
        if (obj instanceof Float) {
            stringBuilder.append(((Float) obj).floatValue());
            return true;
        }
        if (obj instanceof Byte) {
            stringBuilder.append((int) ((Byte) obj).byteValue());
            return true;
        }
        if (isTime(obj) || isDate(obj) || (obj instanceof Temporal)) {
            stringBuilder.append(obj);
            return true;
        }
        return false;
    }

    private static boolean isTime(final Object obj) {
        return timeClass != null && timeClass.isAssignableFrom(obj.getClass());
    }

    private static boolean isDate(final Object obj) {
        return dateClass != null && dateClass.isAssignableFrom(obj.getClass());
    }

    public static boolean equals(final CharSequence left, final int leftOffset, final int leftLength, final CharSequence right, final int rightOffset, final int rightLength) {
        if (leftLength == rightLength) {
            for (int i = 0; i < rightLength; i++) {
                if (left.charAt(i + leftOffset) != right.charAt(i + rightOffset)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean equalsIgnoreCase(final CharSequence left, final int leftOffset, final int leftLength, final CharSequence right, final int rightOffset, final int rightLength) {
        if (leftLength == rightLength) {
            for (int i = 0; i < rightLength; i++) {
                if (Character.toLowerCase(left.charAt(i + leftOffset)) != Character.toLowerCase(right.charAt(i + rightOffset))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void trimToMaxSize(final StringBuilder stringBuilder, final int maxSize) {
        if (stringBuilder != null && stringBuilder.capacity() > maxSize) {
            stringBuilder.setLength(maxSize);
            stringBuilder.trimToSize();
        }
    }

    public static void escapeJson(final StringBuilder toAppendTo, final int start) {
        int escapeCount = 0;
        for (int i = start; i < toAppendTo.length(); i++) {
            char c = toAppendTo.charAt(i);
            switch (c) {
                case '\b':
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case '\"':
                case '\\':
                    escapeCount++;
                    break;
                default:
                    if (Character.isISOControl(c)) {
                        escapeCount += 5;
                        break;
                    } else {
                        break;
                    }
            }
        }
        int lastChar = toAppendTo.length() - 1;
        toAppendTo.setLength(toAppendTo.length() + escapeCount);
        int lastPos = toAppendTo.length() - 1;
        for (int i2 = lastChar; lastPos > i2; i2--) {
            char c2 = toAppendTo.charAt(i2);
            switch (c2) {
                case '\b':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'b');
                    break;
                case '\t':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 't');
                    break;
                case '\n':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'n');
                    break;
                case '\f':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'f');
                    break;
                case '\r':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, 'r');
                    break;
                case '\"':
                case '\\':
                    lastPos = escapeAndDecrement(toAppendTo, lastPos, c2);
                    break;
                default:
                    if (Character.isISOControl(c2)) {
                        int i3 = lastPos;
                        int lastPos2 = lastPos - 1;
                        toAppendTo.setCharAt(i3, Chars.getUpperCaseHex(c2 & 15));
                        int lastPos3 = lastPos2 - 1;
                        toAppendTo.setCharAt(lastPos2, Chars.getUpperCaseHex((c2 & 240) >> 4));
                        int lastPos4 = lastPos3 - 1;
                        toAppendTo.setCharAt(lastPos3, '0');
                        int lastPos5 = lastPos4 - 1;
                        toAppendTo.setCharAt(lastPos4, '0');
                        int lastPos6 = lastPos5 - 1;
                        toAppendTo.setCharAt(lastPos5, 'u');
                        lastPos = lastPos6 - 1;
                        toAppendTo.setCharAt(lastPos6, '\\');
                        break;
                    } else {
                        toAppendTo.setCharAt(lastPos, c2);
                        lastPos--;
                        break;
                    }
            }
        }
    }

    private static int escapeAndDecrement(final StringBuilder toAppendTo, int lastPos, final char c) {
        int lastPos2 = lastPos - 1;
        toAppendTo.setCharAt(lastPos, c);
        int lastPos3 = lastPos2 - 1;
        toAppendTo.setCharAt(lastPos2, '\\');
        return lastPos3;
    }

    public static void escapeXml(final StringBuilder toAppendTo, final int start) {
        int escapeCount = 0;
        for (int i = start; i < toAppendTo.length(); i++) {
            switch (toAppendTo.charAt(i)) {
                case '\"':
                case '\'':
                    escapeCount += 5;
                    break;
                case '&':
                    escapeCount += 4;
                    break;
                case '<':
                case Opcodes.V18 /* 62 */:
                    escapeCount += 3;
                    break;
            }
        }
        int lastChar = toAppendTo.length() - 1;
        toAppendTo.setLength(toAppendTo.length() + escapeCount);
        int lastPos = toAppendTo.length() - 1;
        for (int i2 = lastChar; lastPos > i2; i2--) {
            char c = toAppendTo.charAt(i2);
            switch (c) {
                case '\"':
                    int i3 = lastPos;
                    int lastPos2 = lastPos - 1;
                    toAppendTo.setCharAt(i3, ';');
                    int lastPos3 = lastPos2 - 1;
                    toAppendTo.setCharAt(lastPos2, 't');
                    int lastPos4 = lastPos3 - 1;
                    toAppendTo.setCharAt(lastPos3, 'o');
                    int lastPos5 = lastPos4 - 1;
                    toAppendTo.setCharAt(lastPos4, 'u');
                    int lastPos6 = lastPos5 - 1;
                    toAppendTo.setCharAt(lastPos5, 'q');
                    lastPos = lastPos6 - 1;
                    toAppendTo.setCharAt(lastPos6, '&');
                    break;
                case '&':
                    int i4 = lastPos;
                    int lastPos7 = lastPos - 1;
                    toAppendTo.setCharAt(i4, ';');
                    int lastPos8 = lastPos7 - 1;
                    toAppendTo.setCharAt(lastPos7, 'p');
                    int lastPos9 = lastPos8 - 1;
                    toAppendTo.setCharAt(lastPos8, 'm');
                    int lastPos10 = lastPos9 - 1;
                    toAppendTo.setCharAt(lastPos9, 'a');
                    lastPos = lastPos10 - 1;
                    toAppendTo.setCharAt(lastPos10, '&');
                    break;
                case '\'':
                    int i5 = lastPos;
                    int lastPos11 = lastPos - 1;
                    toAppendTo.setCharAt(i5, ';');
                    int lastPos12 = lastPos11 - 1;
                    toAppendTo.setCharAt(lastPos11, 's');
                    int lastPos13 = lastPos12 - 1;
                    toAppendTo.setCharAt(lastPos12, 'o');
                    int lastPos14 = lastPos13 - 1;
                    toAppendTo.setCharAt(lastPos13, 'p');
                    int lastPos15 = lastPos14 - 1;
                    toAppendTo.setCharAt(lastPos14, 'a');
                    lastPos = lastPos15 - 1;
                    toAppendTo.setCharAt(lastPos15, '&');
                    break;
                case '<':
                    int i6 = lastPos;
                    int lastPos16 = lastPos - 1;
                    toAppendTo.setCharAt(i6, ';');
                    int lastPos17 = lastPos16 - 1;
                    toAppendTo.setCharAt(lastPos16, 't');
                    int lastPos18 = lastPos17 - 1;
                    toAppendTo.setCharAt(lastPos17, 'l');
                    lastPos = lastPos18 - 1;
                    toAppendTo.setCharAt(lastPos18, '&');
                    break;
                case Opcodes.V18 /* 62 */:
                    int i7 = lastPos;
                    int lastPos19 = lastPos - 1;
                    toAppendTo.setCharAt(i7, ';');
                    int lastPos20 = lastPos19 - 1;
                    toAppendTo.setCharAt(lastPos19, 't');
                    int lastPos21 = lastPos20 - 1;
                    toAppendTo.setCharAt(lastPos20, 'g');
                    lastPos = lastPos21 - 1;
                    toAppendTo.setCharAt(lastPos21, '&');
                    break;
                default:
                    int i8 = lastPos;
                    lastPos--;
                    toAppendTo.setCharAt(i8, c);
                    break;
            }
        }
    }
}
