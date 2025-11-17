package org.apache.logging.log4j.util;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertySource.class */
public interface PropertySource {
    int getPriority();

    default void forEach(BiConsumer<String, String> action) {
    }

    default Collection<String> getPropertyNames() {
        return Collections.emptySet();
    }

    default CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
        return null;
    }

    default String getProperty(String key) {
        return null;
    }

    default boolean containsProperty(String key) {
        return false;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertySource$Comparator.class */
    public static class Comparator implements java.util.Comparator<PropertySource>, Serializable {
        private static final long serialVersionUID = 1;

        @Override // java.util.Comparator
        public int compare(final PropertySource o1, final PropertySource o2) {
            return Integer.compare(((PropertySource) Objects.requireNonNull(o1)).getPriority(), ((PropertySource) Objects.requireNonNull(o2)).getPriority());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/log4j-api-2.21.1.jar:org/apache/logging/log4j/util/PropertySource$Util.class */
    public static final class Util {
        private static final Pattern PREFIX_PATTERN = Pattern.compile("(^log4j2?[-._/]?|^org\\.apache\\.logging\\.log4j\\.)|(?=AsyncLogger(Config)?\\.)", 2);
        private static final Pattern PROPERTY_TOKENIZER = Pattern.compile("([A-Z]*[a-z0-9]+|[A-Z0-9]+)[-._/]?");
        private static final Map<CharSequence, List<CharSequence>> CACHE = new ConcurrentHashMap();

        static {
            CACHE.put("disableThreadContext", Arrays.asList("disable", "thread", "context"));
            CACHE.put("disableThreadContextStack", Arrays.asList("disable", "thread", "context", "stack"));
            CACHE.put("disableThreadContextMap", Arrays.asList("disable", "thread", "context", BeanDefinitionParserDelegate.MAP_ELEMENT));
            CACHE.put("isThreadContextMapInheritable", Arrays.asList(BeanUtil.PREFIX_GETTER_IS, "thread", "context", BeanDefinitionParserDelegate.MAP_ELEMENT, "inheritable"));
        }

        public static List<CharSequence> tokenize(final CharSequence value) {
            if (CACHE.containsKey(value)) {
                return CACHE.get(value);
            }
            List<CharSequence> tokens = new ArrayList<>();
            Matcher prefixMatcher = PREFIX_PATTERN.matcher(value);
            if (prefixMatcher.find(0)) {
                Matcher matcher = PROPERTY_TOKENIZER.matcher(value);
                for (int start = prefixMatcher.end(); matcher.find(start); start = matcher.end()) {
                    tokens.add(Strings.toRootLowerCase(matcher.group(1)));
                }
            }
            CACHE.put(value, tokens);
            return tokens;
        }

        public static CharSequence joinAsCamelCase(final Iterable<? extends CharSequence> tokens) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (CharSequence token : tokens) {
                if (first) {
                    sb.append(token);
                } else {
                    sb.append(Character.toUpperCase(token.charAt(0)));
                    if (token.length() > 1) {
                        sb.append(token.subSequence(1, token.length()));
                    }
                }
                first = false;
            }
            return sb.toString();
        }

        private Util() {
        }
    }
}
