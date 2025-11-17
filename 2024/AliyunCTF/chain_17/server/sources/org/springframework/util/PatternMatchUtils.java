package org.springframework.util;

import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/PatternMatchUtils.class */
public abstract class PatternMatchUtils {
    public static boolean simpleMatch(@Nullable String pattern, @Nullable String str) {
        if (pattern == null || str == null) {
            return false;
        }
        int firstIndex = pattern.indexOf(42);
        if (firstIndex == -1) {
            return pattern.equals(str);
        }
        if (firstIndex != 0) {
            return str.length() >= firstIndex && pattern.startsWith(str.substring(0, firstIndex)) && simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex));
        }
        if (pattern.length() == 1) {
            return true;
        }
        int nextIndex = pattern.indexOf(42, 1);
        if (nextIndex == -1) {
            return str.endsWith(pattern.substring(1));
        }
        String part = pattern.substring(1, nextIndex);
        if (part.isEmpty()) {
            return simpleMatch(pattern.substring(nextIndex), str);
        }
        int indexOf = str.indexOf(part);
        while (true) {
            int partIndex = indexOf;
            if (partIndex != -1) {
                if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
                    return true;
                }
                indexOf = str.indexOf(part, partIndex + 1);
            } else {
                return false;
            }
        }
    }

    public static boolean simpleMatch(@Nullable String[] patterns, @Nullable String str) {
        if (patterns != null) {
            for (String pattern : patterns) {
                if (simpleMatch(pattern, str)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
