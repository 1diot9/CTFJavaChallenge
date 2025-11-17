package org.springframework.util;

import java.util.Comparator;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/PathMatcher.class */
public interface PathMatcher {
    boolean isPattern(String path);

    boolean match(String pattern, String path);

    boolean matchStart(String pattern, String path);

    String extractPathWithinPattern(String pattern, String path);

    Map<String, String> extractUriTemplateVariables(String pattern, String path);

    Comparator<String> getPatternComparator(String path);

    String combine(String pattern1, String pattern2);
}
