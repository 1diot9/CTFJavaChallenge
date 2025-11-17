package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/LiteralPathElement.class */
class LiteralPathElement extends PathElement {
    private final String text;
    private final int len;
    private final boolean caseSensitive;

    public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive, char separator) {
        super(pos, separator);
        this.len = literalText.length;
        this.caseSensitive = caseSensitive;
        this.text = new String(literalText);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex >= matchingContext.pathLength) {
            return false;
        }
        PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
        if (!(element instanceof PathContainer.PathSegment)) {
            return false;
        }
        PathContainer.PathSegment pathSegment = (PathContainer.PathSegment) element;
        String value = pathSegment.valueToMatch();
        if (value.length() != this.len) {
            return false;
        }
        if (this.caseSensitive) {
            if (!this.text.equals(value)) {
                return false;
            }
        } else if (!this.text.equalsIgnoreCase(value)) {
            return false;
        }
        int pathIndex2 = pathIndex + 1;
        if (!isNoMorePattern()) {
            return this.next != null && this.next.matches(pathIndex2, matchingContext);
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = pathIndex2;
            return true;
        }
        if (pathIndex2 == matchingContext.pathLength) {
            return true;
        }
        return matchingContext.isMatchOptionalTrailingSeparator() && pathIndex2 + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex2);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return this.len;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return this.text.toCharArray();
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean isLiteral() {
        return true;
    }

    public String toString() {
        return "Literal(" + this.text + ")";
    }
}
