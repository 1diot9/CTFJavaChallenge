package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlProcessor.class */
public abstract class YamlProcessor {
    private final Log logger = LogFactory.getLog(getClass());
    private ResolutionMethod resolutionMethod = ResolutionMethod.OVERRIDE;
    private Resource[] resources = new Resource[0];
    private List<DocumentMatcher> documentMatchers = Collections.emptyList();
    private boolean matchDefault = true;
    private Set<String> supportedTypes = Collections.emptySet();

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlProcessor$DocumentMatcher.class */
    public interface DocumentMatcher {
        MatchStatus matches(Properties properties);
    }

    @FunctionalInterface
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlProcessor$MatchCallback.class */
    public interface MatchCallback {
        void process(Properties properties, Map<String, Object> map);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlProcessor$ResolutionMethod.class */
    public enum ResolutionMethod {
        OVERRIDE,
        OVERRIDE_AND_IGNORE,
        FIRST_FOUND
    }

    public void setDocumentMatchers(DocumentMatcher... matchers) {
        this.documentMatchers = List.of((Object[]) matchers);
    }

    public void setMatchDefault(boolean matchDefault) {
        this.matchDefault = matchDefault;
    }

    public void setResolutionMethod(ResolutionMethod resolutionMethod) {
        Assert.notNull(resolutionMethod, "ResolutionMethod must not be null");
        this.resolutionMethod = resolutionMethod;
    }

    public void setResources(Resource... resources) {
        this.resources = resources;
    }

    public void setSupportedTypes(Class<?>... supportedTypes) {
        if (ObjectUtils.isEmpty((Object[]) supportedTypes)) {
            this.supportedTypes = Collections.emptySet();
        } else {
            Assert.noNullElements(supportedTypes, "'supportedTypes' must not contain null elements");
            this.supportedTypes = (Set) Arrays.stream(supportedTypes).map((v0) -> {
                return v0.getName();
            }).collect(Collectors.toUnmodifiableSet());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void process(MatchCallback callback) {
        Yaml yaml = createYaml();
        for (Resource resource : this.resources) {
            boolean found = process(callback, yaml, resource);
            if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND && found) {
                return;
            }
        }
    }

    protected Yaml createYaml() {
        LoaderOptions loaderOptions = new LoaderOptions();
        loaderOptions.setAllowDuplicateKeys(false);
        loaderOptions.setTagInspector(new SupportedTagInspector());
        DumperOptions dumperOptions = new DumperOptions();
        return new Yaml(new Constructor(loaderOptions), new Representer(dumperOptions), dumperOptions, loaderOptions);
    }

    private boolean process(MatchCallback callback, Yaml yaml, Resource resource) {
        int count = 0;
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Loading from YAML: " + resource);
            }
            Reader reader = new UnicodeReader(resource.getInputStream());
            try {
                for (Object object : yaml.loadAll(reader)) {
                    if (object != null && process(asMap(object), callback)) {
                        count++;
                        if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND) {
                            break;
                        }
                    }
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Loaded " + count + " document" + (count > 1 ? "s" : "") + " from YAML resource: " + resource);
                }
                reader.close();
            } finally {
            }
        } catch (IOException ex) {
            handleProcessError(resource, ex);
        }
        return count > 0;
    }

    private void handleProcessError(Resource resource, IOException ex) {
        if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND && this.resolutionMethod != ResolutionMethod.OVERRIDE_AND_IGNORE) {
            throw new IllegalStateException(ex);
        }
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Could not load map from " + resource + ": " + ex.getMessage());
        }
    }

    private Map<String, Object> asMap(Object object) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            result.put("document", object);
            return result;
        }
        Map map = (Map) object;
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                value = asMap(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                result.put("[" + key.toString() + "]", value);
            }
        });
        return result;
    }

    private boolean process(Map<String, Object> map, MatchCallback callback) {
        Properties properties = CollectionFactory.createStringAdaptingProperties();
        properties.putAll(getFlattenedMap(map));
        if (this.documentMatchers.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Merging document (no matchers set): " + map);
            }
            callback.process(properties, map);
            return true;
        }
        MatchStatus result = MatchStatus.ABSTAIN;
        for (DocumentMatcher matcher : this.documentMatchers) {
            MatchStatus match = matcher.matches(properties);
            result = MatchStatus.getMostSpecific(match, result);
            if (match == MatchStatus.FOUND) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Matched document with document matcher: " + properties);
                }
                callback.process(properties, map);
                return true;
            }
        }
        if (result == MatchStatus.ABSTAIN && this.matchDefault) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Matched document with default matcher: " + map);
            }
            callback.process(properties, map);
            return true;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Unmatched document: " + map);
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
        source.forEach((key, value) -> {
            if (StringUtils.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + "." + key;
                }
            }
            if (value instanceof String) {
                result.put(key, value);
                return;
            }
            if (value instanceof Map) {
                Map map = (Map) value;
                buildFlattenedMap(result, map, key);
                return;
            }
            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                if (collection.isEmpty()) {
                    result.put(key, "");
                    return;
                }
                int count = 0;
                for (Object object : collection) {
                    int i = count;
                    count++;
                    buildFlattenedMap(result, Collections.singletonMap("[" + i + "]", object), key);
                }
                return;
            }
            result.put(key, value != null ? value : "");
        });
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlProcessor$MatchStatus.class */
    public enum MatchStatus {
        FOUND,
        NOT_FOUND,
        ABSTAIN;

        public static MatchStatus getMostSpecific(MatchStatus a, MatchStatus b) {
            return a.ordinal() < b.ordinal() ? a : b;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/YamlProcessor$SupportedTagInspector.class */
    public class SupportedTagInspector implements TagInspector {
        private SupportedTagInspector() {
        }

        @Override // org.yaml.snakeyaml.inspector.TagInspector
        public boolean isGlobalTagAllowed(Tag tag) {
            return YamlProcessor.this.supportedTypes.contains(tag.getClassName());
        }
    }
}
