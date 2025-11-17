package org.apache.logging.log4j.message;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.util.StringBuilders;

/* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ParameterFormatter.class */
final class ParameterFormatter {
    static final String RECURSION_PREFIX = "[...";
    static final String RECURSION_SUFFIX = "...]";
    static final String ERROR_PREFIX = "[!!!";
    static final String ERROR_SEPARATOR = "=>";
    static final String ERROR_MSG_SEPARATOR = ":";
    static final String ERROR_SUFFIX = "!!!]";
    private static final char DELIM_START = '{';
    private static final char DELIM_STOP = '}';
    private static final char ESCAPE_CHAR = '\\';
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZone(ZoneId.systemDefault());

    private ParameterFormatter() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MessagePatternAnalysis analyzePattern(final String pattern, final int argCount) {
        MessagePatternAnalysis analysis = new MessagePatternAnalysis();
        analyzePattern(pattern, argCount, analysis);
        return analysis;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void analyzePattern(final String pattern, final int argCount, final MessagePatternAnalysis analysis) {
        int l;
        if (pattern == null || (l = pattern.length()) < 2) {
            analysis.placeholderCount = 0;
            return;
        }
        boolean escaped = false;
        analysis.placeholderCount = 0;
        analysis.escapedCharFound = false;
        int i = 0;
        while (i < l - 1) {
            char c = pattern.charAt(i);
            if (c == '\\') {
                analysis.escapedCharFound = true;
                escaped = !escaped;
            } else if (escaped) {
                escaped = false;
            } else if (c == '{' && pattern.charAt(i + 1) == '}') {
                if (argCount >= 0 && analysis.placeholderCount >= argCount) {
                    analysis.placeholderCount++;
                    i++;
                } else {
                    analysis.ensurePlaceholderCharIndicesCapacity(argCount);
                    int[] iArr = analysis.placeholderCharIndices;
                    int i2 = analysis.placeholderCount;
                    analysis.placeholderCount = i2 + 1;
                    int i3 = i;
                    i++;
                    iArr[i2] = i3;
                }
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/message/ParameterFormatter$MessagePatternAnalysis.class */
    public static final class MessagePatternAnalysis {
        private static final int PLACEHOLDER_CHAR_INDEX_BUFFER_INITIAL_SIZE = 8;
        private static final int PLACEHOLDER_CHAR_INDEX_BUFFER_SIZE_INCREMENT = 8;
        int placeholderCount;
        int[] placeholderCharIndices;
        boolean escapedCharFound;

        /* JADX INFO: Access modifiers changed from: private */
        public void ensurePlaceholderCharIndicesCapacity(final int argCount) {
            int addExact;
            if (this.placeholderCharIndices == null) {
                int length = Math.max(argCount, 8);
                this.placeholderCharIndices = new int[length];
            } else if (this.placeholderCount >= this.placeholderCharIndices.length) {
                if (argCount > 0) {
                    addExact = argCount;
                } else {
                    addExact = Math.addExact(this.placeholderCharIndices.length, 8);
                }
                int newLength = addExact;
                int[] newPlaceholderCharIndices = new int[newLength];
                System.arraycopy(this.placeholderCharIndices, 0, newPlaceholderCharIndices, 0, this.placeholderCount);
                this.placeholderCharIndices = newPlaceholderCharIndices;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String format(final String pattern, final Object[] args, int argCount) {
        StringBuilder result = new StringBuilder();
        MessagePatternAnalysis analysis = analyzePattern(pattern, argCount);
        formatMessage(result, pattern, args, argCount, analysis);
        return result.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void formatMessage(final StringBuilder buffer, final String pattern, final Object[] args, final int argCount, final MessagePatternAnalysis analysis) {
        if (pattern == null || args == null || analysis.placeholderCount == 0) {
            buffer.append(pattern);
            return;
        }
        if (analysis.placeholderCount > args.length) {
            String message = String.format("found %d argument placeholders, but provided %d for pattern `%s`", Integer.valueOf(analysis.placeholderCount), Integer.valueOf(args.length), pattern);
            throw new IllegalArgumentException(message);
        }
        if (analysis.escapedCharFound) {
            formatMessageContainingEscapes(buffer, pattern, args, argCount, analysis);
        } else {
            formatMessageContainingNoEscapes(buffer, pattern, args, argCount, analysis);
        }
    }

    static void formatMessageContainingNoEscapes(final StringBuilder buffer, final String pattern, final Object[] args, final int argCount, final MessagePatternAnalysis analysis) {
        int precedingTextStartIndex = 0;
        int argLimit = Math.min(analysis.placeholderCount, argCount);
        for (int argIndex = 0; argIndex < argLimit; argIndex++) {
            int placeholderCharIndex = analysis.placeholderCharIndices[argIndex];
            buffer.append((CharSequence) pattern, precedingTextStartIndex, placeholderCharIndex);
            recursiveDeepToString(args[argIndex], buffer);
            precedingTextStartIndex = placeholderCharIndex + 2;
        }
        buffer.append((CharSequence) pattern, precedingTextStartIndex, pattern.length());
    }

    static void formatMessageContainingEscapes(final StringBuilder buffer, final String pattern, final Object[] args, final int argCount, final MessagePatternAnalysis analysis) {
        int precedingTextStartIndex = 0;
        int argLimit = Math.min(analysis.placeholderCount, argCount);
        for (int argIndex = 0; argIndex < argLimit; argIndex++) {
            int placeholderCharIndex = analysis.placeholderCharIndices[argIndex];
            copyMessagePatternContainingEscapes(buffer, pattern, precedingTextStartIndex, placeholderCharIndex);
            recursiveDeepToString(args[argIndex], buffer);
            precedingTextStartIndex = placeholderCharIndex + 2;
        }
        copyMessagePatternContainingEscapes(buffer, pattern, precedingTextStartIndex, pattern.length());
    }

    private static void copyMessagePatternContainingEscapes(final StringBuilder buffer, final String pattern, final int startIndex, final int endIndex) {
        boolean escaped = false;
        int i = startIndex;
        while (i < endIndex) {
            char c = pattern.charAt(i);
            if (c == '\\') {
                if (escaped) {
                    escaped = false;
                } else {
                    escaped = true;
                    buffer.append(c);
                }
            } else if (escaped) {
                if (c == '{' && pattern.charAt(i + 1) == '}') {
                    buffer.setLength(buffer.length() - 1);
                    buffer.append("{}");
                    i++;
                } else {
                    buffer.append(c);
                }
                escaped = false;
            } else {
                buffer.append(c);
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String deepToString(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof String) {
            return (String) o;
        }
        if (o instanceof Integer) {
            return Integer.toString(((Integer) o).intValue());
        }
        if (o instanceof Long) {
            return Long.toString(((Long) o).longValue());
        }
        if (o instanceof Double) {
            return Double.toString(((Double) o).doubleValue());
        }
        if (o instanceof Boolean) {
            return Boolean.toString(((Boolean) o).booleanValue());
        }
        if (o instanceof Character) {
            return Character.toString(((Character) o).charValue());
        }
        if (o instanceof Short) {
            return Short.toString(((Short) o).shortValue());
        }
        if (o instanceof Float) {
            return Float.toString(((Float) o).floatValue());
        }
        if (o instanceof Byte) {
            return Byte.toString(((Byte) o).byteValue());
        }
        StringBuilder str = new StringBuilder();
        recursiveDeepToString(o, str);
        return str.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void recursiveDeepToString(final Object o, final StringBuilder str) {
        recursiveDeepToString(o, str, null);
    }

    private static void recursiveDeepToString(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        if (appendSpecialTypes(o, str)) {
            return;
        }
        if (isMaybeRecursive(o)) {
            appendPotentiallyRecursiveValue(o, str, dejaVu);
        } else {
            tryObjectToString(o, str);
        }
    }

    private static boolean appendSpecialTypes(final Object o, final StringBuilder str) {
        return StringBuilders.appendSpecificTypes(str, o) || appendDate(o, str);
    }

    private static boolean appendDate(final Object o, final StringBuilder str) {
        if (!(o instanceof Date)) {
            return false;
        }
        str.append(DATE_FORMATTER.format(((Date) o).toInstant()));
        return true;
    }

    private static boolean isMaybeRecursive(final Object o) {
        return o.getClass().isArray() || (o instanceof Map) || (o instanceof Collection);
    }

    private static void appendPotentiallyRecursiveValue(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        Class<?> oClass = o.getClass();
        if (oClass.isArray()) {
            appendArray(o, str, dejaVu, oClass);
        } else if (o instanceof Map) {
            appendMap(o, str, dejaVu);
        } else {
            if (o instanceof Collection) {
                appendCollection(o, str, dejaVu);
                return;
            }
            throw new IllegalArgumentException("was expecting a container, found " + oClass);
        }
    }

    private static void appendArray(final Object o, final StringBuilder str, final Set<Object> dejaVu, final Class<?> oClass) {
        if (oClass == byte[].class) {
            str.append(Arrays.toString((byte[]) o));
            return;
        }
        if (oClass == short[].class) {
            str.append(Arrays.toString((short[]) o));
            return;
        }
        if (oClass == int[].class) {
            str.append(Arrays.toString((int[]) o));
            return;
        }
        if (oClass == long[].class) {
            str.append(Arrays.toString((long[]) o));
            return;
        }
        if (oClass == float[].class) {
            str.append(Arrays.toString((float[]) o));
            return;
        }
        if (oClass == double[].class) {
            str.append(Arrays.toString((double[]) o));
            return;
        }
        if (oClass == boolean[].class) {
            str.append(Arrays.toString((boolean[]) o));
            return;
        }
        if (oClass == char[].class) {
            str.append(Arrays.toString((char[]) o));
            return;
        }
        Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
            return;
        }
        Object[] oArray = (Object[]) o;
        str.append('[');
        boolean first = true;
        for (Object current : oArray) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            recursiveDeepToString(current, str, cloneDejaVu(effectiveDejaVu));
        }
        str.append(']');
    }

    private static void appendMap(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
            return;
        }
        Map<?, ?> oMap = (Map) o;
        str.append('{');
        boolean isFirst = true;
        for (Map.Entry<?, ?> entry : oMap.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                str.append(", ");
            }
            Object key = entry.getKey();
            Object value = entry.getValue();
            recursiveDeepToString(key, str, cloneDejaVu(effectiveDejaVu));
            str.append('=');
            recursiveDeepToString(value, str, cloneDejaVu(effectiveDejaVu));
        }
        str.append('}');
    }

    private static void appendCollection(final Object o, final StringBuilder str, final Set<Object> dejaVu) {
        Set<Object> effectiveDejaVu = getOrCreateDejaVu(dejaVu);
        boolean seen = !effectiveDejaVu.add(o);
        if (seen) {
            String id = identityToString(o);
            str.append("[...").append(id).append("...]");
            return;
        }
        Collection<?> oCol = (Collection) o;
        str.append('[');
        boolean isFirst = true;
        for (Object anOCol : oCol) {
            if (isFirst) {
                isFirst = false;
            } else {
                str.append(", ");
            }
            recursiveDeepToString(anOCol, str, cloneDejaVu(effectiveDejaVu));
        }
        str.append(']');
    }

    private static Set<Object> getOrCreateDejaVu(final Set<Object> dejaVu) {
        if (dejaVu == null) {
            return createDejaVu();
        }
        return dejaVu;
    }

    private static Set<Object> createDejaVu() {
        return Collections.newSetFromMap(new IdentityHashMap());
    }

    private static Set<Object> cloneDejaVu(final Set<Object> dejaVu) {
        Set<Object> clonedDejaVu = createDejaVu();
        clonedDejaVu.addAll(dejaVu);
        return clonedDejaVu;
    }

    private static void tryObjectToString(final Object o, final StringBuilder str) {
        try {
            str.append(o.toString());
        } catch (Throwable t) {
            handleErrorInObjectToString(o, str, t);
        }
    }

    private static void handleErrorInObjectToString(final Object o, final StringBuilder str, final Throwable t) {
        str.append("[!!!");
        str.append(identityToString(o));
        str.append("=>");
        String msg = t.getMessage();
        String className = t.getClass().getName();
        str.append(className);
        if (!className.equals(msg)) {
            str.append(":");
            str.append(msg);
        }
        str.append("!!!]");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String identityToString(final Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(obj));
    }
}
