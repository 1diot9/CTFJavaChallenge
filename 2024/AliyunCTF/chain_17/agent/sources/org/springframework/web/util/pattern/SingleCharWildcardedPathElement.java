package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/SingleCharWildcardedPathElement.class */
class SingleCharWildcardedPathElement extends PathElement {
    private final char[] text;
    private final int len;
    private final int questionMarkCount;
    private final boolean caseSensitive;

    public SingleCharWildcardedPathElement(int pos, char[] literalText, int questionMarkCount, boolean caseSensitive, char separator) {
        super(pos, separator);
        this.len = literalText.length;
        this.questionMarkCount = questionMarkCount;
        this.caseSensitive = caseSensitive;
        if (caseSensitive) {
            this.text = literalText;
            return;
        }
        this.text = new char[literalText.length];
        for (int i = 0; i < this.len; i++) {
            this.text[i] = Character.toLowerCase(literalText[i]);
        }
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
            for (int i = 0; i < this.len; i++) {
                char ch2 = this.text[i];
                if (ch2 != '?' && ch2 != value.charAt(i)) {
                    return false;
                }
            }
        } else {
            for (int i2 = 0; i2 < this.len; i2++) {
                char ch3 = this.text[i2];
                if (ch3 != '?' && ch3 != Character.toLowerCase(value.charAt(i2))) {
                    return false;
                }
            }
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
    public int getWildcardCount() {
        return this.questionMarkCount;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return this.len;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return this.text;
    }

    public String toString() {
        return "SingleCharWildcarded(" + String.valueOf(this.text) + ")";
    }
}
