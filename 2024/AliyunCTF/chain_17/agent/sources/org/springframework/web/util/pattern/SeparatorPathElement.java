package org.springframework.web.util.pattern;

import org.springframework.web.util.pattern.PathPattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/SeparatorPathElement.class */
public class SeparatorPathElement extends PathElement {
    /* JADX INFO: Access modifiers changed from: package-private */
    public SeparatorPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && matchingContext.isSeparator(pathIndex)) {
            if (!isNoMorePattern()) {
                return this.next != null && this.next.matches(pathIndex + 1, matchingContext);
            }
            if (!matchingContext.determineRemainingPath) {
                return pathIndex + 1 == matchingContext.pathLength;
            }
            matchingContext.remainingPathIndex = pathIndex + 1;
            return true;
        }
        return false;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return new char[]{this.separator};
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean isLiteral() {
        return true;
    }

    public String toString() {
        return "Separator(" + this.separator + ")";
    }
}
