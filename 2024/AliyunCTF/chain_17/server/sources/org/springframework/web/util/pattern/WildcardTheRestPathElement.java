package org.springframework.web.util.pattern;

import org.springframework.web.util.pattern.PathPattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/WildcardTheRestPathElement.class */
public class WildcardTheRestPathElement extends PathElement {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WildcardTheRestPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
            return false;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = matchingContext.pathLength;
            return true;
        }
        return true;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return (this.separator + "**").toCharArray();
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getWildcardCount() {
        return 1;
    }

    public String toString() {
        return "WildcardTheRest(" + this.separator + "**)";
    }
}
