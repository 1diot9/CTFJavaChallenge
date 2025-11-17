package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/WildcardPathElement.class */
class WildcardPathElement extends PathElement {
    public WildcardPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        String segmentData = null;
        if (pathIndex < matchingContext.pathLength) {
            PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
            if (!(element instanceof PathContainer.PathSegment)) {
                return false;
            }
            PathContainer.PathSegment pathSegment = (PathContainer.PathSegment) element;
            segmentData = pathSegment.valueToMatch();
            pathIndex++;
        }
        if (!isNoMorePattern()) {
            return (segmentData == null || segmentData.length() == 0 || this.next == null || !this.next.matches(pathIndex, matchingContext)) ? false : true;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = pathIndex;
            return true;
        }
        if (pathIndex == matchingContext.pathLength) {
            return true;
        }
        return matchingContext.isMatchOptionalTrailingSeparator() && segmentData != null && segmentData.length() > 0 && pathIndex + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return new char[]{'*'};
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getWildcardCount() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getScore() {
        return 100;
    }

    public String toString() {
        return "Wildcard(*)";
    }
}
