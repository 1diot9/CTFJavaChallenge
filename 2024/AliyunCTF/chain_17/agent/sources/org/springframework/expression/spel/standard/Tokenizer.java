package org.springframework.expression.spel.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.asm.Opcodes;
import org.springframework.expression.spel.InternalParseException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.expression.spel.SpelParseException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/standard/Tokenizer.class */
class Tokenizer {
    private static final String[] ALTERNATIVE_OPERATOR_NAMES = {"DIV", "EQ", "GE", "GT", "LE", "LT", "MOD", "NE", "NOT"};
    private static final byte[] FLAGS = new byte[256];
    private static final byte IS_DIGIT = 1;
    private static final byte IS_HEXDIGIT = 2;
    private final String expressionString;
    private final char[] charsToProcess;
    private final int max;
    private final List<Token> tokens = new ArrayList();
    private int pos = 0;

    static {
        for (int ch2 = 48; ch2 <= 57; ch2++) {
            byte[] bArr = FLAGS;
            int i = ch2;
            bArr[i] = (byte) (bArr[i] | 3);
        }
        for (int ch3 = 65; ch3 <= 70; ch3++) {
            byte[] bArr2 = FLAGS;
            int i2 = ch3;
            bArr2[i2] = (byte) (bArr2[i2] | 2);
        }
        for (int ch4 = 97; ch4 <= 102; ch4++) {
            byte[] bArr3 = FLAGS;
            int i3 = ch4;
            bArr3[i3] = (byte) (bArr3[i3] | 2);
        }
    }

    public Tokenizer(String inputData) {
        this.expressionString = inputData;
        this.charsToProcess = (inputData + "��").toCharArray();
        this.max = this.charsToProcess.length;
    }

    public List<Token> process() {
        while (this.pos < this.max) {
            char ch2 = this.charsToProcess[this.pos];
            if (isAlphabetic(ch2)) {
                lexIdentifier();
            } else {
                switch (ch2) {
                    case 0:
                        this.pos++;
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case '\b':
                    case 11:
                    case '\f':
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case ';':
                    case 'A':
                    case 'B':
                    case 'C':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'H':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'L':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case '`':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case Opcodes.DMUL /* 107 */:
                    case 'l':
                    case Opcodes.LDIV /* 109 */:
                    case Opcodes.FDIV /* 110 */:
                    case Opcodes.DDIV /* 111 */:
                    case 'p':
                    case Opcodes.LREM /* 113 */:
                    case Opcodes.FREM /* 114 */:
                    case 's':
                    case 't':
                    case Opcodes.LNEG /* 117 */:
                    case Opcodes.FNEG /* 118 */:
                    case Opcodes.DNEG /* 119 */:
                    case 'x':
                    case Opcodes.LSHL /* 121 */:
                    case 'z':
                    default:
                        throw new IllegalStateException("Unsupported character '%s' (%d) encountered at position %d in expression.".formatted(Character.valueOf(ch2), Integer.valueOf(ch2), Integer.valueOf(this.pos + 1)));
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ':
                        this.pos++;
                        break;
                    case '!':
                        if (isTwoCharToken(TokenKind.NE)) {
                            pushPairToken(TokenKind.NE);
                            break;
                        } else if (isTwoCharToken(TokenKind.PROJECT)) {
                            pushPairToken(TokenKind.PROJECT);
                            break;
                        } else {
                            pushCharToken(TokenKind.NOT);
                            break;
                        }
                    case '\"':
                        lexDoubleQuotedStringLiteral();
                        break;
                    case '#':
                        pushCharToken(TokenKind.HASH);
                        break;
                    case '$':
                        if (isTwoCharToken(TokenKind.SELECT_LAST)) {
                            pushPairToken(TokenKind.SELECT_LAST);
                            break;
                        } else {
                            lexIdentifier();
                            break;
                        }
                    case '%':
                        pushCharToken(TokenKind.MOD);
                        break;
                    case '&':
                        if (isTwoCharToken(TokenKind.SYMBOLIC_AND)) {
                            pushPairToken(TokenKind.SYMBOLIC_AND);
                            break;
                        } else {
                            pushCharToken(TokenKind.FACTORY_BEAN_REF);
                            break;
                        }
                    case '\'':
                        lexQuotedStringLiteral();
                        break;
                    case '(':
                        pushCharToken(TokenKind.LPAREN);
                        break;
                    case ')':
                        pushCharToken(TokenKind.RPAREN);
                        break;
                    case '*':
                        pushCharToken(TokenKind.STAR);
                        break;
                    case '+':
                        if (isTwoCharToken(TokenKind.INC)) {
                            pushPairToken(TokenKind.INC);
                            break;
                        } else {
                            pushCharToken(TokenKind.PLUS);
                            break;
                        }
                    case ',':
                        pushCharToken(TokenKind.COMMA);
                        break;
                    case '-':
                        if (isTwoCharToken(TokenKind.DEC)) {
                            pushPairToken(TokenKind.DEC);
                            break;
                        } else {
                            pushCharToken(TokenKind.MINUS);
                            break;
                        }
                    case '.':
                        pushCharToken(TokenKind.DOT);
                        break;
                    case '/':
                        pushCharToken(TokenKind.DIV);
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        lexNumericLiteral(ch2 == '0');
                        break;
                    case ':':
                        pushCharToken(TokenKind.COLON);
                        break;
                    case '<':
                        if (isTwoCharToken(TokenKind.LE)) {
                            pushPairToken(TokenKind.LE);
                            break;
                        } else {
                            pushCharToken(TokenKind.LT);
                            break;
                        }
                    case '=':
                        if (isTwoCharToken(TokenKind.EQ)) {
                            pushPairToken(TokenKind.EQ);
                            break;
                        } else {
                            pushCharToken(TokenKind.ASSIGN);
                            break;
                        }
                    case '>':
                        if (isTwoCharToken(TokenKind.GE)) {
                            pushPairToken(TokenKind.GE);
                            break;
                        } else {
                            pushCharToken(TokenKind.GT);
                            break;
                        }
                    case '?':
                        if (isTwoCharToken(TokenKind.SELECT)) {
                            pushPairToken(TokenKind.SELECT);
                            break;
                        } else if (isTwoCharToken(TokenKind.ELVIS)) {
                            pushPairToken(TokenKind.ELVIS);
                            break;
                        } else if (isTwoCharToken(TokenKind.SAFE_NAVI)) {
                            pushPairToken(TokenKind.SAFE_NAVI);
                            break;
                        } else {
                            pushCharToken(TokenKind.QMARK);
                            break;
                        }
                    case '@':
                        pushCharToken(TokenKind.BEAN_REF);
                        break;
                    case '[':
                        pushCharToken(TokenKind.LSQUARE);
                        break;
                    case '\\':
                        raiseParseException(this.pos, SpelMessage.UNEXPECTED_ESCAPE_CHAR, new Object[0]);
                        break;
                    case ']':
                        pushCharToken(TokenKind.RSQUARE);
                        break;
                    case '^':
                        if (isTwoCharToken(TokenKind.SELECT_FIRST)) {
                            pushPairToken(TokenKind.SELECT_FIRST);
                            break;
                        } else {
                            pushCharToken(TokenKind.POWER);
                            break;
                        }
                    case '_':
                        lexIdentifier();
                        break;
                    case '{':
                        pushCharToken(TokenKind.LCURLY);
                        break;
                    case '|':
                        if (!isTwoCharToken(TokenKind.SYMBOLIC_OR)) {
                            raiseParseException(this.pos, SpelMessage.MISSING_CHARACTER, "|");
                        }
                        pushPairToken(TokenKind.SYMBOLIC_OR);
                        break;
                    case '}':
                        pushCharToken(TokenKind.RCURLY);
                        break;
                }
            }
        }
        return this.tokens;
    }

    private void lexQuotedStringLiteral() {
        int start = this.pos;
        boolean terminated = false;
        while (!terminated) {
            this.pos++;
            char ch2 = this.charsToProcess[this.pos];
            if (ch2 == '\'') {
                if (this.charsToProcess[this.pos + 1] == '\'') {
                    this.pos++;
                } else {
                    terminated = true;
                }
            }
            if (isExhausted()) {
                raiseParseException(start, SpelMessage.NON_TERMINATING_QUOTED_STRING, new Object[0]);
            }
        }
        this.pos++;
        this.tokens.add(new Token(TokenKind.LITERAL_STRING, subarray(start, this.pos), start, this.pos));
    }

    private void lexDoubleQuotedStringLiteral() {
        int start = this.pos;
        boolean terminated = false;
        while (!terminated) {
            this.pos++;
            char ch2 = this.charsToProcess[this.pos];
            if (ch2 == '\"') {
                if (this.charsToProcess[this.pos + 1] == '\"') {
                    this.pos++;
                } else {
                    terminated = true;
                }
            }
            if (isExhausted()) {
                raiseParseException(start, SpelMessage.NON_TERMINATING_DOUBLE_QUOTED_STRING, new Object[0]);
            }
        }
        this.pos++;
        this.tokens.add(new Token(TokenKind.LITERAL_STRING, subarray(start, this.pos), start, this.pos));
    }

    private void lexNumericLiteral(boolean firstCharIsZero) {
        boolean isReal = false;
        int start = this.pos;
        char ch2 = this.charsToProcess[this.pos + 1];
        boolean isHex = ch2 == 'x' || ch2 == 'X';
        if (firstCharIsZero && isHex) {
            this.pos++;
            do {
                this.pos++;
            } while (isHexadecimalDigit(this.charsToProcess[this.pos]));
            if (isChar('L', 'l')) {
                pushHexIntToken(subarray(start + 2, this.pos), true, start, this.pos);
                this.pos++;
                return;
            } else {
                pushHexIntToken(subarray(start + 2, this.pos), false, start, this.pos);
                return;
            }
        }
        do {
            this.pos++;
        } while (isDigit(this.charsToProcess[this.pos]));
        if (this.charsToProcess[this.pos] == '.') {
            isReal = true;
            int dotpos = this.pos;
            do {
                this.pos++;
            } while (isDigit(this.charsToProcess[this.pos]));
            if (this.pos == dotpos + 1) {
                this.pos = dotpos;
                pushIntToken(subarray(start, this.pos), false, start, this.pos);
                return;
            }
        }
        int endOfNumber = this.pos;
        if (isChar('L', 'l')) {
            if (isReal) {
                raiseParseException(start, SpelMessage.REAL_CANNOT_BE_LONG, new Object[0]);
            }
            pushIntToken(subarray(start, endOfNumber), true, start, endOfNumber);
            this.pos++;
            return;
        }
        if (isExponentChar(this.charsToProcess[this.pos])) {
            this.pos++;
            char possibleSign = this.charsToProcess[this.pos];
            if (isSign(possibleSign)) {
                this.pos++;
            }
            do {
                this.pos++;
            } while (isDigit(this.charsToProcess[this.pos]));
            boolean isFloat = false;
            if (isFloatSuffix(this.charsToProcess[this.pos])) {
                isFloat = true;
                this.pos++;
            } else if (isDoubleSuffix(this.charsToProcess[this.pos])) {
                this.pos++;
            }
            pushRealToken(subarray(start, this.pos), isFloat, start, this.pos);
            return;
        }
        char ch3 = this.charsToProcess[this.pos];
        boolean isFloat2 = false;
        if (isFloatSuffix(ch3)) {
            isReal = true;
            isFloat2 = true;
            int i = this.pos + 1;
            this.pos = i;
            endOfNumber = i;
        } else if (isDoubleSuffix(ch3)) {
            isReal = true;
            int i2 = this.pos + 1;
            this.pos = i2;
            endOfNumber = i2;
        }
        if (isReal) {
            pushRealToken(subarray(start, endOfNumber), isFloat2, start, endOfNumber);
        } else {
            pushIntToken(subarray(start, endOfNumber), false, start, endOfNumber);
        }
    }

    private void lexIdentifier() {
        int start = this.pos;
        do {
            this.pos++;
        } while (isIdentifier(this.charsToProcess[this.pos]));
        char[] subarray = subarray(start, this.pos);
        if (this.pos - start == 2 || this.pos - start == 3) {
            String asString = new String(subarray).toUpperCase();
            int idx = Arrays.binarySearch(ALTERNATIVE_OPERATOR_NAMES, asString);
            if (idx >= 0) {
                pushOneCharOrTwoCharToken(TokenKind.valueOf(asString), start, subarray);
                return;
            }
        }
        this.tokens.add(new Token(TokenKind.IDENTIFIER, subarray, start, this.pos));
    }

    private void pushIntToken(char[] data, boolean isLong, int start, int end) {
        if (isLong) {
            this.tokens.add(new Token(TokenKind.LITERAL_LONG, data, start, end));
        } else {
            this.tokens.add(new Token(TokenKind.LITERAL_INT, data, start, end));
        }
    }

    private void pushHexIntToken(char[] data, boolean isLong, int start, int end) {
        if (data.length == 0) {
            if (isLong) {
                raiseParseException(start, SpelMessage.NOT_A_LONG, this.expressionString.substring(start, end + 1));
            } else {
                raiseParseException(start, SpelMessage.NOT_AN_INTEGER, this.expressionString.substring(start, end));
            }
        }
        if (isLong) {
            this.tokens.add(new Token(TokenKind.LITERAL_HEXLONG, data, start, end));
        } else {
            this.tokens.add(new Token(TokenKind.LITERAL_HEXINT, data, start, end));
        }
    }

    private void pushRealToken(char[] data, boolean isFloat, int start, int end) {
        if (isFloat) {
            this.tokens.add(new Token(TokenKind.LITERAL_REAL_FLOAT, data, start, end));
        } else {
            this.tokens.add(new Token(TokenKind.LITERAL_REAL, data, start, end));
        }
    }

    private char[] subarray(int start, int end) {
        return Arrays.copyOfRange(this.charsToProcess, start, end);
    }

    private boolean isTwoCharToken(TokenKind kind) {
        return kind.tokenChars.length == 2 && this.charsToProcess[this.pos] == kind.tokenChars[0] && this.charsToProcess[this.pos + 1] == kind.tokenChars[1];
    }

    private void pushCharToken(TokenKind kind) {
        this.tokens.add(new Token(kind, this.pos, this.pos + 1));
        this.pos++;
    }

    private void pushPairToken(TokenKind kind) {
        this.tokens.add(new Token(kind, this.pos, this.pos + 2));
        this.pos += 2;
    }

    private void pushOneCharOrTwoCharToken(TokenKind kind, int pos, char[] data) {
        this.tokens.add(new Token(kind, data, pos, pos + kind.getLength()));
    }

    private boolean isIdentifier(char ch2) {
        return isAlphabetic(ch2) || isDigit(ch2) || ch2 == '_' || ch2 == '$';
    }

    private boolean isChar(char a, char b) {
        char ch2 = this.charsToProcess[this.pos];
        return ch2 == a || ch2 == b;
    }

    private boolean isExponentChar(char ch2) {
        return ch2 == 'e' || ch2 == 'E';
    }

    private boolean isFloatSuffix(char ch2) {
        return ch2 == 'f' || ch2 == 'F';
    }

    private boolean isDoubleSuffix(char ch2) {
        return ch2 == 'd' || ch2 == 'D';
    }

    private boolean isSign(char ch2) {
        return ch2 == '+' || ch2 == '-';
    }

    private boolean isDigit(char ch2) {
        return ch2 <= 255 && (FLAGS[ch2] & 1) != 0;
    }

    private boolean isAlphabetic(char ch2) {
        return Character.isLetter(ch2);
    }

    private boolean isHexadecimalDigit(char ch2) {
        return ch2 <= 255 && (FLAGS[ch2] & 2) != 0;
    }

    private boolean isExhausted() {
        return this.pos == this.max - 1;
    }

    private void raiseParseException(int start, SpelMessage msg, Object... inserts) {
        throw new InternalParseException(new SpelParseException(this.expressionString, start, msg, inserts));
    }
}
