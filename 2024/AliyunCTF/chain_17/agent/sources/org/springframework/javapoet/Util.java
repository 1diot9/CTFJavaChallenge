package org.springframework.javapoet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.Modifier;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/Util.class */
final class Util {
    private Util() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> Map<K, List<V>> immutableMultimap(Map<K, List<V>> multimap) {
        LinkedHashMap<K, List<V>> result = new LinkedHashMap<>();
        for (Map.Entry<K, List<V>> entry : multimap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), immutableList(entry.getValue()));
            }
        }
        return Collections.unmodifiableMap(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> Map<K, V> immutableMap(Map<K, V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap(map));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkArgument(boolean condition, String format, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> T checkNotNull(T reference, String format, Object... args) {
        if (reference == null) {
            throw new NullPointerException(String.format(format, args));
        }
        return reference;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkState(boolean condition, String format, Object... args) {
        if (!condition) {
            throw new IllegalStateException(String.format(format, args));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> List<T> immutableList(Collection<T> collection) {
        return Collections.unmodifiableList(new ArrayList(collection));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Set<T> immutableSet(Collection<T> set) {
        return Collections.unmodifiableSet(new LinkedHashSet(set));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> result = new LinkedHashSet<>();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void requireExactlyOneOf(Set<Modifier> modifiers, Modifier... mutuallyExclusive) {
        int count = 0;
        for (Modifier modifier : mutuallyExclusive) {
            if (modifiers.contains(modifier)) {
                count++;
            }
        }
        checkArgument(count == 1, "modifiers %s must contain one of %s", modifiers, Arrays.toString(mutuallyExclusive));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String characterLiteralWithoutSingleQuotes(char c) {
        switch (c) {
            case '\b':
                return "\\b";
            case '\t':
                return "\\t";
            case '\n':
                return "\\n";
            case '\f':
                return "\\f";
            case '\r':
                return "\\r";
            case '\"':
                return "\"";
            case '\'':
                return "\\'";
            case '\\':
                return "\\\\";
            default:
                return Character.isISOControl(c) ? String.format("\\u%04x", Integer.valueOf(c)) : Character.toString(c);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String stringLiteralWithDoubleQuotes(String value, String indent) {
        StringBuilder result = new StringBuilder(value.length() + 2);
        result.append('\"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\'') {
                result.append("'");
            } else if (c == '\"') {
                result.append("\\\"");
            } else {
                result.append(characterLiteralWithoutSingleQuotes(c));
                if (c == '\n' && i + 1 < value.length()) {
                    result.append("\"\n").append(indent).append(indent).append("+ \"");
                }
            }
        }
        result.append('\"');
        return result.toString();
    }
}
