package ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/util/StringCollectionUtil.class */
public class StringCollectionUtil {
    public static void retainMatching(Collection<String> values, String... patterns) {
        retainMatching(values, Arrays.asList(patterns));
    }

    public static void retainMatching(Collection<String> values, Collection<String> patterns) {
        if (patterns.isEmpty()) {
            return;
        }
        List<String> matches = new ArrayList<>(values.size());
        for (String p : patterns) {
            Pattern pattern = Pattern.compile(p);
            for (String value : values) {
                if (pattern.matcher(value).matches()) {
                    matches.add(value);
                }
            }
        }
        values.retainAll(matches);
    }

    public static void removeMatching(Collection<String> values, String... patterns) {
        removeMatching(values, Arrays.asList(patterns));
    }

    public static void removeMatching(Collection<String> values, Collection<String> patterns) {
        List<String> matches = new ArrayList<>(values.size());
        for (String p : patterns) {
            Pattern pattern = Pattern.compile(p);
            for (String value : values) {
                if (pattern.matcher(value).matches()) {
                    matches.add(value);
                }
            }
        }
        values.removeAll(matches);
    }
}
