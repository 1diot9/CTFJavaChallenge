package org.springframework.context.index;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Deprecated(since = "6.1", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/index/CandidateComponentsIndex.class */
public class CandidateComponentsIndex {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher(".");
    private final MultiValueMap<String, Entry> index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CandidateComponentsIndex(List<Properties> content) {
        this.index = parseIndex(content);
    }

    private static MultiValueMap<String, Entry> parseIndex(List<Properties> content) {
        MultiValueMap<String, Entry> index = new LinkedMultiValueMap<>();
        for (Properties entry : content) {
            entry.forEach((type, values) -> {
                String[] stereotypes = ((String) values).split(",");
                for (String stereotype : stereotypes) {
                    index.add(stereotype, new Entry((String) type));
                }
            });
        }
        return index;
    }

    public Set<String> getCandidateTypes(String basePackage, String stereotype) {
        List<Entry> candidates = (List) this.index.get(stereotype);
        if (candidates != null) {
            return (Set) candidates.parallelStream().filter(t -> {
                return t.match(basePackage);
            }).map(t2 -> {
                return t2.type;
            }).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/index/CandidateComponentsIndex$Entry.class */
    public static class Entry {
        private final String type;
        private final String packageName;

        Entry(String type) {
            this.type = type;
            this.packageName = ClassUtils.getPackageName(type);
        }

        public boolean match(String basePackage) {
            if (CandidateComponentsIndex.pathMatcher.isPattern(basePackage)) {
                return CandidateComponentsIndex.pathMatcher.match(basePackage, this.packageName);
            }
            return this.type.startsWith(basePackage);
        }
    }
}
