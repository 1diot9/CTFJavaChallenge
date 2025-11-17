package org.springframework.web.util.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.pattern.PatternParseException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/pattern/InternalPathPatternParser.class */
public class InternalPathPatternParser {
    private final PathPatternParser parser;
    private int pathPatternLength;
    int pos;
    private int singleCharWildcardCount;
    private int pathElementStart;
    private int variableCaptureStart;

    @Nullable
    private List<String> capturedVariableNames;

    @Nullable
    private PathElement headPE;

    @Nullable
    private PathElement currentPE;
    private char[] pathPatternData = new char[0];
    private boolean wildcard = false;
    private boolean isCaptureTheRestVariable = false;
    private boolean insideVariableCapture = false;
    private int variableCaptureCount = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InternalPathPatternParser(PathPatternParser parentParser) {
        this.parser = parentParser;
    }

    public PathPattern parse(String pathPattern) throws PatternParseException {
        Assert.notNull(pathPattern, "Path pattern must not be null");
        this.pathPatternData = pathPattern.toCharArray();
        this.pathPatternLength = this.pathPatternData.length;
        this.headPE = null;
        this.currentPE = null;
        this.capturedVariableNames = null;
        this.pathElementStart = -1;
        this.pos = 0;
        resetPathElementState();
        while (this.pos < this.pathPatternLength) {
            char ch2 = this.pathPatternData[this.pos];
            char separator = this.parser.getPathOptions().separator();
            if (ch2 == separator) {
                if (this.pathElementStart != -1) {
                    pushPathElement(createPathElement());
                }
                if (peekDoubleWildcard()) {
                    pushPathElement(new WildcardTheRestPathElement(this.pos, separator));
                    this.pos += 2;
                } else {
                    pushPathElement(new SeparatorPathElement(this.pos, separator));
                }
            } else {
                if (this.pathElementStart == -1) {
                    this.pathElementStart = this.pos;
                }
                if (ch2 == '?') {
                    this.singleCharWildcardCount++;
                } else if (ch2 == '{') {
                    if (this.insideVariableCapture) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_NESTED_CAPTURE, new Object[0]);
                    }
                    this.insideVariableCapture = true;
                    this.variableCaptureStart = this.pos;
                } else if (ch2 == '}') {
                    if (!this.insideVariableCapture) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_OPEN_CAPTURE, new Object[0]);
                    }
                    this.insideVariableCapture = false;
                    if (this.isCaptureTheRestVariable && this.pos + 1 < this.pathPatternLength) {
                        throw new PatternParseException(this.pos + 1, this.pathPatternData, PatternParseException.PatternMessage.NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST, new Object[0]);
                    }
                    this.variableCaptureCount++;
                } else if (ch2 == ':') {
                    if (this.insideVariableCapture && !this.isCaptureTheRestVariable) {
                        skipCaptureRegex();
                        this.insideVariableCapture = false;
                        this.variableCaptureCount++;
                    }
                } else if (ch2 == '*') {
                    if (this.insideVariableCapture && this.variableCaptureStart == this.pos - 1) {
                        this.isCaptureTheRestVariable = true;
                    }
                    this.wildcard = true;
                }
                if (this.insideVariableCapture) {
                    if (this.variableCaptureStart + 1 + (this.isCaptureTheRestVariable ? 1 : 0) == this.pos && !Character.isJavaIdentifierStart(ch2)) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_CHARACTER_AT_START_OF_CAPTURE_DESCRIPTOR, Character.toString(ch2));
                    }
                    if (this.pos > this.variableCaptureStart + 1 + (this.isCaptureTheRestVariable ? 1 : 0) && !Character.isJavaIdentifierPart(ch2) && ch2 != '-') {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_CHARACTER_IN_CAPTURE_DESCRIPTOR, Character.toString(ch2));
                    }
                } else {
                    continue;
                }
            }
            this.pos++;
        }
        if (this.pathElementStart != -1) {
            pushPathElement(createPathElement());
        }
        return new PathPattern(pathPattern, this.parser, this.headPE);
    }

    private void skipCaptureRegex() {
        this.pos++;
        int regexStart = this.pos;
        int curlyBracketDepth = 0;
        boolean z = false;
        while (true) {
            boolean previousBackslash = z;
            if (this.pos < this.pathPatternLength) {
                char ch2 = this.pathPatternData[this.pos];
                if (ch2 == '\\' && !previousBackslash) {
                    this.pos++;
                    z = true;
                } else {
                    if (ch2 == '{' && !previousBackslash) {
                        curlyBracketDepth++;
                    } else if (ch2 == '}' && !previousBackslash) {
                        if (curlyBracketDepth == 0) {
                            if (regexStart == this.pos) {
                                throw new PatternParseException(regexStart, this.pathPatternData, PatternParseException.PatternMessage.MISSING_REGEX_CONSTRAINT, new Object[0]);
                            }
                            return;
                        }
                        curlyBracketDepth--;
                    }
                    if (ch2 == this.parser.getPathOptions().separator() && !previousBackslash) {
                        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
                    }
                    this.pos++;
                    z = false;
                }
            } else {
                throw new PatternParseException(this.pos - 1, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
            }
        }
    }

    private boolean peekDoubleWildcard() {
        if (this.pos + 2 >= this.pathPatternLength || this.pathPatternData[this.pos + 1] != '*' || this.pathPatternData[this.pos + 2] != '*') {
            return false;
        }
        char separator = this.parser.getPathOptions().separator();
        if (this.pos + 3 >= this.pathPatternLength || this.pathPatternData[this.pos + 3] != separator) {
            return this.pos + 3 == this.pathPatternLength;
        }
        throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.NO_MORE_DATA_EXPECTED_AFTER_CAPTURE_THE_REST, new Object[0]);
    }

    private void pushPathElement(PathElement newPathElement) {
        if (newPathElement instanceof CaptureTheRestPathElement) {
            if (this.currentPE == null) {
                this.headPE = newPathElement;
                this.currentPE = newPathElement;
            } else if (this.currentPE instanceof SeparatorPathElement) {
                PathElement peBeforeSeparator = this.currentPE.prev;
                if (peBeforeSeparator == null) {
                    this.headPE = newPathElement;
                    newPathElement.prev = null;
                } else {
                    peBeforeSeparator.next = newPathElement;
                    newPathElement.prev = peBeforeSeparator;
                }
                this.currentPE = newPathElement;
            } else {
                throw new IllegalStateException("Expected SeparatorPathElement but was " + this.currentPE);
            }
        } else if (this.headPE == null) {
            this.headPE = newPathElement;
            this.currentPE = newPathElement;
        } else if (this.currentPE != null) {
            this.currentPE.next = newPathElement;
            newPathElement.prev = this.currentPE;
            this.currentPE = newPathElement;
        }
        resetPathElementState();
    }

    private char[] getPathElementText() {
        char[] pathElementText = new char[this.pos - this.pathElementStart];
        System.arraycopy(this.pathPatternData, this.pathElementStart, pathElementText, 0, this.pos - this.pathElementStart);
        return pathElementText;
    }

    private PathElement createPathElement() {
        PathElement newPE;
        if (this.insideVariableCapture) {
            throw new PatternParseException(this.pos, this.pathPatternData, PatternParseException.PatternMessage.MISSING_CLOSE_CAPTURE, new Object[0]);
        }
        char separator = this.parser.getPathOptions().separator();
        if (this.variableCaptureCount > 0) {
            if (this.variableCaptureCount == 1 && this.pathElementStart == this.variableCaptureStart && this.pathPatternData[this.pos - 1] == '}') {
                if (this.isCaptureTheRestVariable) {
                    newPE = new CaptureTheRestPathElement(this.pathElementStart, getPathElementText(), separator);
                } else {
                    try {
                        newPE = new CaptureVariablePathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), separator);
                        recordCapturedVariable(this.pathElementStart, ((CaptureVariablePathElement) newPE).getVariableName());
                    } catch (PatternSyntaxException pse) {
                        throw new PatternParseException(pse, findRegexStart(this.pathPatternData, this.pathElementStart) + pse.getIndex(), this.pathPatternData, PatternParseException.PatternMessage.REGEX_PATTERN_SYNTAX_EXCEPTION, new Object[0]);
                    }
                }
            } else {
                if (this.isCaptureTheRestVariable) {
                    throw new PatternParseException(this.pathElementStart, this.pathPatternData, PatternParseException.PatternMessage.CAPTURE_ALL_IS_STANDALONE_CONSTRUCT, new Object[0]);
                }
                RegexPathElement newRegexSection = new RegexPathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), this.pathPatternData, separator);
                for (String variableName : newRegexSection.getVariableNames()) {
                    recordCapturedVariable(this.pathElementStart, variableName);
                }
                newPE = newRegexSection;
            }
        } else if (this.wildcard) {
            if (this.pos - 1 == this.pathElementStart) {
                newPE = new WildcardPathElement(this.pathElementStart, separator);
            } else {
                newPE = new RegexPathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), this.pathPatternData, separator);
            }
        } else if (this.singleCharWildcardCount != 0) {
            newPE = new SingleCharWildcardedPathElement(this.pathElementStart, getPathElementText(), this.singleCharWildcardCount, this.parser.isCaseSensitive(), separator);
        } else {
            newPE = new LiteralPathElement(this.pathElementStart, getPathElementText(), this.parser.isCaseSensitive(), separator);
        }
        return newPE;
    }

    private int findRegexStart(char[] data, int offset) {
        for (int pos = offset; pos < data.length; pos++) {
            if (data[pos] == ':') {
                return pos + 1;
            }
        }
        return -1;
    }

    private void resetPathElementState() {
        this.pathElementStart = -1;
        this.singleCharWildcardCount = 0;
        this.insideVariableCapture = false;
        this.variableCaptureCount = 0;
        this.wildcard = false;
        this.isCaptureTheRestVariable = false;
        this.variableCaptureStart = -1;
    }

    private void recordCapturedVariable(int pos, String variableName) {
        if (this.capturedVariableNames == null) {
            this.capturedVariableNames = new ArrayList();
        }
        if (this.capturedVariableNames.contains(variableName)) {
            throw new PatternParseException(pos, this.pathPatternData, PatternParseException.PatternMessage.ILLEGAL_DOUBLE_CAPTURE, variableName);
        }
        this.capturedVariableNames.add(variableName);
    }
}
