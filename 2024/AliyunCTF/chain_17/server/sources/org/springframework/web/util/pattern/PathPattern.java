package org.springframework.web.util.pattern;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PathPattern.class */
public class PathPattern implements Comparable<PathPattern> {
    private static final PathContainer EMPTY_PATH = PathContainer.parsePath("");
    public static final Comparator<PathPattern> SPECIFICITY_COMPARATOR = Comparator.nullsLast(Comparator.comparingInt(p -> {
        return p.isCatchAll() ? 1 : 0;
    }).thenComparingInt(p2 -> {
        if (p2.isCatchAll()) {
            return scoreByNormalizedLength(p2);
        }
        return 0;
    }).thenComparingInt((v0) -> {
        return v0.getScore();
    }).thenComparingInt(PathPattern::scoreByNormalizedLength));
    private final String patternString;
    private final PathPatternParser parser;
    private final PathContainer.Options pathOptions;
    private final boolean matchOptionalTrailingSeparator;
    private final boolean caseSensitive;

    @Nullable
    private final PathElement head;
    private int capturedVariableCount;
    private int normalizedLength;
    private boolean endsWithSeparatorWildcard;
    private int score;
    private boolean catchAll;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PathPattern(String patternText, PathPatternParser parser, @Nullable PathElement head) {
        this.endsWithSeparatorWildcard = false;
        this.catchAll = false;
        this.patternString = patternText;
        this.parser = parser;
        this.pathOptions = parser.getPathOptions();
        this.matchOptionalTrailingSeparator = parser.isMatchOptionalTrailingSeparator();
        this.caseSensitive = parser.isCaseSensitive();
        this.head = head;
        PathElement pathElement = head;
        while (true) {
            PathElement elem = pathElement;
            if (elem != null) {
                this.capturedVariableCount += elem.getCaptureCount();
                this.normalizedLength += elem.getNormalizedLength();
                this.score += elem.getScore();
                if ((elem instanceof CaptureTheRestPathElement) || (elem instanceof WildcardTheRestPathElement)) {
                    this.catchAll = true;
                }
                if ((elem instanceof SeparatorPathElement) && (elem.next instanceof WildcardPathElement) && elem.next.next == null) {
                    this.endsWithSeparatorWildcard = true;
                }
                pathElement = elem.next;
            } else {
                return;
            }
        }
    }

    public String getPatternString() {
        return this.patternString;
    }

    public boolean hasPatternSyntax() {
        return this.score > 0 || this.catchAll || this.patternString.indexOf(63) != -1;
    }

    public boolean matches(PathContainer pathContainer) {
        if (this.head == null) {
            return !hasLength(pathContainer) || (this.matchOptionalTrailingSeparator && pathContainerIsJustSeparator(pathContainer));
        }
        if (!hasLength(pathContainer)) {
            if ((this.head instanceof WildcardTheRestPathElement) || (this.head instanceof CaptureTheRestPathElement)) {
                pathContainer = EMPTY_PATH;
            } else {
                return false;
            }
        }
        MatchingContext matchingContext = new MatchingContext(pathContainer, false);
        return this.head.matches(0, matchingContext);
    }

    @Nullable
    public PathMatchInfo matchAndExtract(PathContainer pathContainer) {
        if (this.head == null) {
            if (!hasLength(pathContainer) || (this.matchOptionalTrailingSeparator && pathContainerIsJustSeparator(pathContainer))) {
                return PathMatchInfo.EMPTY;
            }
            return null;
        }
        if (!hasLength(pathContainer)) {
            if ((this.head instanceof WildcardTheRestPathElement) || (this.head instanceof CaptureTheRestPathElement)) {
                pathContainer = EMPTY_PATH;
            } else {
                return null;
            }
        }
        MatchingContext matchingContext = new MatchingContext(pathContainer, true);
        if (this.head.matches(0, matchingContext)) {
            return matchingContext.getPathMatchResult();
        }
        return null;
    }

    @Nullable
    public PathRemainingMatchInfo matchStartOfPath(PathContainer pathContainer) {
        PathContainer pathMatched;
        PathContainer pathRemaining;
        if (this.head == null) {
            return new PathRemainingMatchInfo(EMPTY_PATH, pathContainer);
        }
        if (!hasLength(pathContainer)) {
            return null;
        }
        MatchingContext matchingContext = new MatchingContext(pathContainer, true);
        matchingContext.setMatchAllowExtraPath();
        boolean matches = this.head.matches(0, matchingContext);
        if (!matches) {
            return null;
        }
        if (matchingContext.remainingPathIndex == pathContainer.elements().size()) {
            pathMatched = pathContainer;
            pathRemaining = EMPTY_PATH;
        } else {
            pathMatched = pathContainer.subPath(0, matchingContext.remainingPathIndex);
            pathRemaining = pathContainer.subPath(matchingContext.remainingPathIndex);
        }
        return new PathRemainingMatchInfo(pathMatched, pathRemaining, matchingContext.getPathMatchResult());
    }

    public PathContainer extractPathWithinPattern(PathContainer path) {
        PathContainer resultPath;
        List<PathContainer.Element> pathElements = path.elements();
        int pathElementsCount = pathElements.size();
        int startIndex = 0;
        PathElement elem = this.head;
        while (elem != null && elem.isLiteral()) {
            elem = elem.next;
            startIndex++;
        }
        if (elem == null) {
            return PathContainer.parsePath("");
        }
        while (startIndex < pathElementsCount && (pathElements.get(startIndex) instanceof PathContainer.Separator)) {
            startIndex++;
        }
        int endIndex = pathElements.size();
        while (endIndex > 0 && (pathElements.get(endIndex - 1) instanceof PathContainer.Separator)) {
            endIndex--;
        }
        boolean multipleAdjacentSeparators = false;
        int i = startIndex;
        while (true) {
            if (i < endIndex - 1) {
                if (!(pathElements.get(i) instanceof PathContainer.Separator) || !(pathElements.get(i + 1) instanceof PathContainer.Separator)) {
                    i++;
                } else {
                    multipleAdjacentSeparators = true;
                    break;
                }
            } else {
                break;
            }
        }
        if (multipleAdjacentSeparators) {
            StringBuilder sb = new StringBuilder();
            int i2 = startIndex;
            while (i2 < endIndex) {
                int i3 = i2;
                i2++;
                PathContainer.Element e = pathElements.get(i3);
                sb.append(e.value());
                if (e instanceof PathContainer.Separator) {
                    while (i2 < endIndex && (pathElements.get(i2) instanceof PathContainer.Separator)) {
                        i2++;
                    }
                }
            }
            resultPath = PathContainer.parsePath(sb.toString(), this.pathOptions);
        } else if (startIndex >= endIndex) {
            resultPath = PathContainer.parsePath("");
        } else {
            resultPath = path.subPath(startIndex, endIndex);
        }
        return resultPath;
    }

    @Override // java.lang.Comparable
    public int compareTo(@Nullable PathPattern otherPattern) {
        int result = SPECIFICITY_COMPARATOR.compare(this, otherPattern);
        return (result != 0 || otherPattern == null) ? result : this.patternString.compareTo(otherPattern.patternString);
    }

    public PathPattern combine(PathPattern pattern2string) {
        if (!StringUtils.hasLength(this.patternString)) {
            if (!StringUtils.hasLength(pattern2string.patternString)) {
                return this.parser.parse("");
            }
            return pattern2string;
        }
        if (!StringUtils.hasLength(pattern2string.patternString)) {
            return this;
        }
        if (!this.patternString.equals(pattern2string.patternString) && this.capturedVariableCount == 0 && matches(PathContainer.parsePath(pattern2string.patternString))) {
            return pattern2string;
        }
        if (this.endsWithSeparatorWildcard) {
            return this.parser.parse(concat(this.patternString.substring(0, this.patternString.length() - 2), pattern2string.patternString));
        }
        int starDotPos1 = this.patternString.indexOf("*.");
        if (this.capturedVariableCount != 0 || starDotPos1 == -1 || getSeparator() == '.') {
            return this.parser.parse(concat(this.patternString, pattern2string.patternString));
        }
        String firstExtension = this.patternString.substring(starDotPos1 + 1);
        String p2string = pattern2string.patternString;
        int dotPos2 = p2string.indexOf(46);
        String file2 = dotPos2 == -1 ? p2string : p2string.substring(0, dotPos2);
        String secondExtension = dotPos2 == -1 ? "" : p2string.substring(dotPos2);
        boolean firstExtensionWild = firstExtension.equals(".*") || firstExtension.isEmpty();
        boolean secondExtensionWild = secondExtension.equals(".*") || secondExtension.isEmpty();
        if (firstExtensionWild || secondExtensionWild) {
            return this.parser.parse(file2 + (firstExtensionWild ? secondExtension : firstExtension));
        }
        throw new IllegalArgumentException("Cannot combine patterns: " + this.patternString + " and " + pattern2string);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof PathPattern) {
                PathPattern that = (PathPattern) other;
                if (!this.patternString.equals(that.getPatternString()) || getSeparator() != that.getSeparator() || this.caseSensitive != that.caseSensitive) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((this.patternString.hashCode() + getSeparator()) * 17) + (this.caseSensitive ? 1 : 0);
    }

    public String toString() {
        return this.patternString;
    }

    int getScore() {
        return this.score;
    }

    boolean isCatchAll() {
        return this.catchAll;
    }

    int getNormalizedLength() {
        return this.normalizedLength;
    }

    char getSeparator() {
        return this.pathOptions.separator();
    }

    int getCapturedVariableCount() {
        return this.capturedVariableCount;
    }

    String toChainString() {
        StringJoiner stringJoiner = new StringJoiner(" ");
        PathElement pathElement = this.head;
        while (true) {
            PathElement pe = pathElement;
            if (pe != null) {
                stringJoiner.add(pe.toString());
                pathElement = pe.next;
            } else {
                return stringJoiner.toString();
            }
        }
    }

    String computePatternString() {
        StringBuilder sb = new StringBuilder();
        PathElement pathElement = this.head;
        while (true) {
            PathElement pe = pathElement;
            if (pe != null) {
                sb.append(pe.getChars());
                pathElement = pe.next;
            } else {
                return sb.toString();
            }
        }
    }

    @Nullable
    PathElement getHeadSection() {
        return this.head;
    }

    private String concat(String path1, String path2) {
        boolean path1EndsWithSeparator = path1.charAt(path1.length() - 1) == getSeparator();
        boolean path2StartsWithSeparator = path2.charAt(0) == getSeparator();
        if (path1EndsWithSeparator && path2StartsWithSeparator) {
            return path1 + path2.substring(1);
        }
        if (path1EndsWithSeparator || path2StartsWithSeparator) {
            return path1 + path2;
        }
        return path1 + getSeparator() + path2;
    }

    private boolean hasLength(@Nullable PathContainer container) {
        return container != null && container.elements().size() > 0;
    }

    private static int scoreByNormalizedLength(PathPattern pattern) {
        return -pattern.getNormalizedLength();
    }

    private boolean pathContainerIsJustSeparator(PathContainer pathContainer) {
        return pathContainer.value().length() == 1 && pathContainer.value().charAt(0) == getSeparator();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PathPattern$PathMatchInfo.class */
    public static class PathMatchInfo {
        private static final PathMatchInfo EMPTY = new PathMatchInfo(Collections.emptyMap(), Collections.emptyMap());
        private final Map<String, String> uriVariables;
        private final Map<String, MultiValueMap<String, String>> matrixVariables;

        PathMatchInfo(Map<String, String> uriVars, @Nullable Map<String, MultiValueMap<String, String>> matrixVars) {
            this.uriVariables = Collections.unmodifiableMap(uriVars);
            this.matrixVariables = matrixVars != null ? Collections.unmodifiableMap(matrixVars) : Collections.emptyMap();
        }

        public Map<String, String> getUriVariables() {
            return this.uriVariables;
        }

        public Map<String, MultiValueMap<String, String>> getMatrixVariables() {
            return this.matrixVariables;
        }

        public String toString() {
            return "PathMatchInfo[uriVariables=" + this.uriVariables + ", matrixVariables=" + this.matrixVariables + "]";
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PathPattern$PathRemainingMatchInfo.class */
    public static class PathRemainingMatchInfo {
        private final PathContainer pathMatched;
        private final PathContainer pathRemaining;
        private final PathMatchInfo pathMatchInfo;

        PathRemainingMatchInfo(PathContainer pathMatched, PathContainer pathRemaining) {
            this(pathMatched, pathRemaining, PathMatchInfo.EMPTY);
        }

        PathRemainingMatchInfo(PathContainer pathMatched, PathContainer pathRemaining, PathMatchInfo pathMatchInfo) {
            this.pathRemaining = pathRemaining;
            this.pathMatched = pathMatched;
            this.pathMatchInfo = pathMatchInfo;
        }

        public PathContainer getPathMatched() {
            return this.pathMatched;
        }

        public PathContainer getPathRemaining() {
            return this.pathRemaining;
        }

        public Map<String, String> getUriVariables() {
            return this.pathMatchInfo.getUriVariables();
        }

        public Map<String, MultiValueMap<String, String>> getMatrixVariables() {
            return this.pathMatchInfo.getMatrixVariables();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/PathPattern$MatchingContext.class */
    public class MatchingContext {
        final PathContainer candidate;
        final List<PathContainer.Element> pathElements;
        final int pathLength;

        @Nullable
        private Map<String, String> extractedUriVariables;

        @Nullable
        private Map<String, MultiValueMap<String, String>> extractedMatrixVariables;
        boolean extractingVariables;
        boolean determineRemainingPath = false;
        int remainingPathIndex;

        public MatchingContext(PathContainer pathContainer, boolean extractVariables) {
            this.candidate = pathContainer;
            this.pathElements = pathContainer.elements();
            this.pathLength = this.pathElements.size();
            this.extractingVariables = extractVariables;
        }

        public void setMatchAllowExtraPath() {
            this.determineRemainingPath = true;
        }

        public boolean isMatchOptionalTrailingSeparator() {
            return PathPattern.this.matchOptionalTrailingSeparator;
        }

        public void set(String key, String value, MultiValueMap<String, String> parameters) {
            if (this.extractedUriVariables == null) {
                this.extractedUriVariables = new HashMap();
            }
            this.extractedUriVariables.put(key, value);
            if (!parameters.isEmpty()) {
                if (this.extractedMatrixVariables == null) {
                    this.extractedMatrixVariables = new HashMap();
                }
                this.extractedMatrixVariables.put(key, CollectionUtils.unmodifiableMultiValueMap(parameters));
            }
        }

        public PathMatchInfo getPathMatchResult() {
            if (this.extractedUriVariables == null) {
                return PathMatchInfo.EMPTY;
            }
            return new PathMatchInfo(this.extractedUriVariables, this.extractedMatrixVariables);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean isSeparator(int pathIndex) {
            return this.pathElements.get(pathIndex) instanceof PathContainer.Separator;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String pathElementValue(int pathIndex) {
            PathContainer.Element element = pathIndex < this.pathLength ? this.pathElements.get(pathIndex) : null;
            if (!(element instanceof PathContainer.PathSegment)) {
                return "";
            }
            PathContainer.PathSegment pathSegment = (PathContainer.PathSegment) element;
            return pathSegment.valueToMatch();
        }
    }
}
