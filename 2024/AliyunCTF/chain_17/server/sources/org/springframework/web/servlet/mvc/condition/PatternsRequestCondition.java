package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/PatternsRequestCondition.class */
public class PatternsRequestCondition extends AbstractRequestCondition<PatternsRequestCondition> {
    private static final Set<String> EMPTY_PATH_PATTERN = Collections.singleton("");
    private static final String[] ROOT_PATH_PATTERNS = {"", "/"};
    private final Set<String> patterns;
    private final PathMatcher pathMatcher;
    private final boolean useSuffixPatternMatch;
    private final boolean useTrailingSlashMatch;
    private final List<String> fileExtensions;

    public PatternsRequestCondition(String... patterns) {
        this(patterns, true, null);
    }

    public PatternsRequestCondition(String[] patterns, boolean useTrailingSlashMatch, @Nullable PathMatcher pathMatcher) {
        this(patterns, null, pathMatcher, useTrailingSlashMatch);
    }

    @Deprecated
    public PatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useTrailingSlashMatch) {
        this(patterns, urlPathHelper, pathMatcher, false, useTrailingSlashMatch);
    }

    @Deprecated
    public PatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch) {
        this(patterns, urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch, null);
    }

    @Deprecated
    public PatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch, @Nullable List<String> fileExtensions) {
        this.fileExtensions = new ArrayList();
        this.patterns = initPatterns(patterns);
        this.pathMatcher = pathMatcher != null ? pathMatcher : new AntPathMatcher();
        this.useSuffixPatternMatch = useSuffixPatternMatch;
        this.useTrailingSlashMatch = useTrailingSlashMatch;
        if (fileExtensions != null) {
            for (String fileExtension : fileExtensions) {
                if (fileExtension.charAt(0) != '.') {
                    fileExtension = "." + fileExtension;
                }
                this.fileExtensions.add(fileExtension);
            }
        }
    }

    private static Set<String> initPatterns(String[] patterns) {
        if (!hasPattern(patterns)) {
            return EMPTY_PATH_PATTERN;
        }
        Set<String> result = new LinkedHashSet<>(patterns.length);
        for (String pattern : patterns) {
            result.add(PathPatternParser.defaultInstance.initFullPathPattern(pattern));
        }
        return result;
    }

    private static boolean hasPattern(String[] patterns) {
        if (!ObjectUtils.isEmpty((Object[]) patterns)) {
            for (String pattern : patterns) {
                if (StringUtils.hasText(pattern)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private PatternsRequestCondition(Set<String> patterns, PatternsRequestCondition other) {
        this.fileExtensions = new ArrayList();
        this.patterns = patterns;
        this.pathMatcher = other.pathMatcher;
        this.useSuffixPatternMatch = other.useSuffixPatternMatch;
        this.useTrailingSlashMatch = other.useTrailingSlashMatch;
        this.fileExtensions.addAll(other.fileExtensions);
    }

    public Set<String> getPatterns() {
        return this.patterns;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<String> getContent() {
        return this.patterns;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    public boolean isEmptyPathMapping() {
        return this.patterns == EMPTY_PATH_PATTERN;
    }

    public Set<String> getDirectPaths() {
        if (isEmptyPathMapping()) {
            return EMPTY_PATH_PATTERN;
        }
        Set<String> result = Collections.emptySet();
        for (String pattern : this.patterns) {
            if (!this.pathMatcher.isPattern(pattern)) {
                result = result.isEmpty() ? new HashSet<>(1) : result;
                result.add(pattern);
            }
        }
        return result;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public PatternsRequestCondition combine(PatternsRequestCondition other) {
        if (isEmptyPathMapping() && other.isEmptyPathMapping()) {
            return new PatternsRequestCondition(ROOT_PATH_PATTERNS);
        }
        if (other.isEmptyPathMapping()) {
            return this;
        }
        if (isEmptyPathMapping()) {
            return other;
        }
        Set<String> result = new LinkedHashSet<>();
        if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
            for (String pattern1 : this.patterns) {
                for (String pattern2 : other.patterns) {
                    result.add(this.pathMatcher.combine(pattern1, pattern2));
                }
            }
        }
        return new PatternsRequestCondition(result, this);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public PatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        String lookupPath = UrlPathHelper.getResolvedLookupPath(request);
        List<String> matches = getMatchingPatterns(lookupPath);
        if (matches.isEmpty()) {
            return null;
        }
        return new PatternsRequestCondition(new LinkedHashSet(matches), this);
    }

    public List<String> getMatchingPatterns(String lookupPath) {
        List<String> matches = null;
        for (String pattern : this.patterns) {
            String match = getMatchingPattern(pattern, lookupPath);
            if (match != null) {
                matches = matches != null ? matches : new ArrayList<>();
                matches.add(match);
            }
        }
        if (matches == null) {
            return Collections.emptyList();
        }
        if (matches.size() > 1) {
            matches.sort(this.pathMatcher.getPatternComparator(lookupPath));
        }
        return matches;
    }

    @Nullable
    private String getMatchingPattern(String pattern, String lookupPath) {
        if (pattern.equals(lookupPath)) {
            return pattern;
        }
        if (this.useSuffixPatternMatch) {
            if (!this.fileExtensions.isEmpty() && lookupPath.indexOf(46) != -1) {
                for (String extension : this.fileExtensions) {
                    if (this.pathMatcher.match(pattern + extension, lookupPath)) {
                        return pattern + extension;
                    }
                }
            } else {
                boolean hasSuffix = pattern.indexOf(46) != -1;
                if (!hasSuffix && this.pathMatcher.match(pattern + ".*", lookupPath)) {
                    return pattern + ".*";
                }
            }
        }
        if (this.pathMatcher.match(pattern, lookupPath)) {
            return pattern;
        }
        if (this.useTrailingSlashMatch && !pattern.endsWith("/") && this.pathMatcher.match(pattern + "/", lookupPath)) {
            return pattern + "/";
        }
        return null;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(PatternsRequestCondition other, HttpServletRequest request) {
        String lookupPath = UrlPathHelper.getResolvedLookupPath(request);
        Comparator<String> patternComparator = this.pathMatcher.getPatternComparator(lookupPath);
        Iterator<String> iterator = this.patterns.iterator();
        Iterator<String> iteratorOther = other.patterns.iterator();
        while (iterator.hasNext() && iteratorOther.hasNext()) {
            int result = patternComparator.compare(iterator.next(), iteratorOther.next());
            if (result != 0) {
                return result;
            }
        }
        if (iterator.hasNext()) {
            return -1;
        }
        if (iteratorOther.hasNext()) {
            return 1;
        }
        return 0;
    }
}
