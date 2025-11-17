package org.springframework.web.util.pattern;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.text.StrPool;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.server.PathContainer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PatternParseException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/RegexPathElement.class */
class RegexPathElement extends PathElement {
    private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");
    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";
    private final char[] regex;
    private final boolean caseSensitive;
    private final Pattern pattern;
    private int wildcardCount;
    private final List<String> variableNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegexPathElement(int pos, char[] regex, boolean caseSensitive, char[] completePattern, char separator) {
        super(pos, separator);
        this.variableNames = new ArrayList();
        this.regex = regex;
        this.caseSensitive = caseSensitive;
        this.pattern = buildPattern(regex, completePattern);
    }

    public Pattern buildPattern(char[] regex, char[] completePattern) {
        StringBuilder patternBuilder = new StringBuilder();
        String text = new String(regex);
        Matcher matcher = GLOB_PATTERN.matcher(text);
        int i = 0;
        while (true) {
            int end = i;
            if (matcher.find()) {
                patternBuilder.append(quote(text, end, matcher.start()));
                String match = matcher.group();
                if (CoreConstants.NA.equals(match)) {
                    patternBuilder.append('.');
                } else if ("*".equals(match)) {
                    patternBuilder.append(".*");
                    int pos = matcher.start();
                    if (pos < 1 || text.charAt(pos - 1) != '.') {
                        this.wildcardCount++;
                    }
                } else if (match.startsWith(StrPool.DELIM_START) && match.endsWith("}")) {
                    int colonIdx = match.indexOf(58);
                    if (colonIdx == -1) {
                        patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                        String variableName = matcher.group(1);
                        if (this.variableNames.contains(variableName)) {
                            throw new PatternParseException(this.pos, completePattern, PatternParseException.PatternMessage.ILLEGAL_DOUBLE_CAPTURE, variableName);
                        }
                        this.variableNames.add(variableName);
                    } else {
                        String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append('(');
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(')');
                        String variableName2 = match.substring(1, colonIdx);
                        if (this.variableNames.contains(variableName2)) {
                            throw new PatternParseException(this.pos, completePattern, PatternParseException.PatternMessage.ILLEGAL_DOUBLE_CAPTURE, variableName2);
                        }
                        this.variableNames.add(variableName2);
                    }
                }
                i = matcher.end();
            } else {
                patternBuilder.append(quote(text, end, text.length()));
                return Pattern.compile(patternBuilder.toString(), 32 | (this.caseSensitive ? 0 : 2));
            }
        }
    }

    public List<String> getVariableNames() {
        return this.variableNames;
    }

    private String quote(String s, int start, int end) {
        if (start == end) {
            return "";
        }
        return Pattern.quote(s.substring(start, end));
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        MultiValueMap<String, String> multiValueMap;
        String textToMatch = matchingContext.pathElementValue(pathIndex);
        Matcher matcher = this.pattern.matcher(textToMatch);
        boolean matches = matcher.matches();
        if (matches) {
            if (isNoMorePattern()) {
                if (matchingContext.determineRemainingPath && (this.variableNames.isEmpty() || textToMatch.length() > 0)) {
                    matchingContext.remainingPathIndex = pathIndex + 1;
                    matches = true;
                } else {
                    matches = pathIndex + 1 >= matchingContext.pathLength && (this.variableNames.isEmpty() || textToMatch.length() > 0);
                    if (!matches && matchingContext.isMatchOptionalTrailingSeparator()) {
                        matches = (this.variableNames.isEmpty() || textToMatch.length() > 0) && pathIndex + 2 >= matchingContext.pathLength && matchingContext.isSeparator(pathIndex + 1);
                    }
                }
            } else {
                matches = this.next != null && this.next.matches(pathIndex + 1, matchingContext);
            }
        }
        if (matches && matchingContext.extractingVariables) {
            if (this.variableNames.size() != matcher.groupCount()) {
                throw new IllegalArgumentException("The number of capturing groups in the pattern segment " + this.pattern + " does not match the number of URI template variables it defines, which can occur if capturing groups are used in a URI template regex. Use non-capturing groups instead.");
            }
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String name = this.variableNames.get(i - 1);
                String value = matcher.group(i);
                if (i == this.variableNames.size()) {
                    multiValueMap = ((PathContainer.PathSegment) matchingContext.pathElements.get(pathIndex)).parameters();
                } else {
                    multiValueMap = NO_PARAMETERS;
                }
                matchingContext.set(name, value, multiValueMap);
            }
        }
        return matches;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        int varsLength = 0;
        for (String variableName : this.variableNames) {
            varsLength += variableName.length();
        }
        return (this.regex.length - varsLength) - this.variableNames.size();
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return this.regex;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getCaptureCount() {
        return this.variableNames.size();
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getWildcardCount() {
        return this.wildcardCount;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getScore() {
        return (getCaptureCount() * 1) + (getWildcardCount() * 100);
    }

    public String toString() {
        return "Regex(" + String.valueOf(this.regex) + ")";
    }
}
