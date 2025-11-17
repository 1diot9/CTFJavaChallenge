package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/PathPatternsRequestCondition.class */
public final class PathPatternsRequestCondition extends AbstractRequestCondition<PathPatternsRequestCondition> {
    private static final SortedSet<PathPattern> EMPTY_PATH_PATTERN = new TreeSet(Collections.singleton(new PathPatternParser().parse("")));
    private static final Set<String> EMPTY_PATH = Collections.singleton("");
    private static final SortedSet<PathPattern> ROOT_PATH_PATTERNS = new TreeSet(List.of(new PathPatternParser().parse(""), new PathPatternParser().parse("/")));
    private final SortedSet<PathPattern> patterns;

    public PathPatternsRequestCondition() {
        this(EMPTY_PATH_PATTERN);
    }

    public PathPatternsRequestCondition(PathPatternParser parser, String... patterns) {
        this(parse(parser, patterns));
    }

    private static SortedSet<PathPattern> parse(PathPatternParser parser, String... patterns) {
        if (patterns.length == 0 || (patterns.length == 1 && !StringUtils.hasText(patterns[0]))) {
            return EMPTY_PATH_PATTERN;
        }
        SortedSet<PathPattern> result = new TreeSet<>();
        for (String pattern : patterns) {
            result.add(parser.parse(parser.initFullPathPattern(pattern)));
        }
        return result;
    }

    private PathPatternsRequestCondition(SortedSet<PathPattern> patterns) {
        this.patterns = patterns;
    }

    public Set<PathPattern> getPatterns() {
        return this.patterns;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<PathPattern> getContent() {
        return this.patterns;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    public PathPattern getFirstPattern() {
        return this.patterns.first();
    }

    public boolean isEmptyPathMapping() {
        return this.patterns == EMPTY_PATH_PATTERN;
    }

    public Set<String> getDirectPaths() {
        if (isEmptyPathMapping()) {
            return EMPTY_PATH;
        }
        Set<String> result = Collections.emptySet();
        for (PathPattern pattern : this.patterns) {
            if (!pattern.hasPatternSyntax()) {
                result = result.isEmpty() ? new HashSet<>(1) : result;
                result.add(pattern.getPatternString());
            }
        }
        return result;
    }

    public Set<String> getPatternValues() {
        return isEmptyPathMapping() ? EMPTY_PATH : (Set) getPatterns().stream().map((v0) -> {
            return v0.getPatternString();
        }).collect(Collectors.toSet());
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public PathPatternsRequestCondition combine(PathPatternsRequestCondition other) {
        if (isEmptyPathMapping() && other.isEmptyPathMapping()) {
            return new PathPatternsRequestCondition(ROOT_PATH_PATTERNS);
        }
        if (other.isEmptyPathMapping()) {
            return this;
        }
        if (isEmptyPathMapping()) {
            return other;
        }
        SortedSet<PathPattern> combined = new TreeSet<>();
        for (PathPattern pattern1 : this.patterns) {
            for (PathPattern pattern2 : other.patterns) {
                combined.add(pattern1.combine(pattern2));
            }
        }
        return new PathPatternsRequestCondition(combined);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public PathPatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        PathContainer path = ServletRequestPathUtils.getParsedRequestPath(request).pathWithinApplication();
        SortedSet<PathPattern> matches = getMatchingPatterns(path);
        if (matches != null) {
            return new PathPatternsRequestCondition(matches);
        }
        return null;
    }

    @Nullable
    private SortedSet<PathPattern> getMatchingPatterns(PathContainer path) {
        TreeSet<PathPattern> result = null;
        for (PathPattern pattern : this.patterns) {
            if (pattern.matches(path)) {
                result = result != null ? result : new TreeSet<>();
                result.add(pattern);
            }
        }
        return result;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(PathPatternsRequestCondition other, HttpServletRequest request) {
        Iterator<PathPattern> iterator = this.patterns.iterator();
        Iterator<PathPattern> iteratorOther = other.getPatterns().iterator();
        while (iterator.hasNext() && iteratorOther.hasNext()) {
            int result = PathPattern.SPECIFICITY_COMPARATOR.compare(iterator.next(), iteratorOther.next());
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
