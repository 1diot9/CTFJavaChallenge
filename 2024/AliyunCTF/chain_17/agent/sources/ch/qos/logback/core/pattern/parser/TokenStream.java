package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.util.IEscapeUtil;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import ch.qos.logback.core.pattern.util.RestrictedEscapeUtil;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;
import org.h2.server.pg.PgServer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/TokenStream.class */
public class TokenStream {
    final String pattern;
    final int patternLength;
    final IEscapeUtil escapeUtil;
    final IEscapeUtil optionEscapeUtil;
    TokenizerState state;
    int pointer;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/TokenStream$TokenizerState.class */
    public enum TokenizerState {
        LITERAL_STATE,
        FORMAT_MODIFIER_STATE,
        KEYWORD_STATE,
        OPTION_STATE,
        RIGHT_PARENTHESIS_STATE
    }

    TokenStream(String pattern) {
        this(pattern, new RegularEscapeUtil());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TokenStream(String pattern, IEscapeUtil escapeUtil) {
        this.optionEscapeUtil = new RestrictedEscapeUtil();
        this.state = TokenizerState.LITERAL_STATE;
        this.pointer = 0;
        if (pattern == null || pattern.length() == 0) {
            throw new IllegalArgumentException("null or empty pattern string not allowed");
        }
        this.pattern = pattern;
        this.patternLength = pattern.length();
        this.escapeUtil = escapeUtil;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Token> tokenize() throws ScanException {
        List<Token> tokenList = new ArrayList<>();
        StringBuffer buf = new StringBuffer();
        while (this.pointer < this.patternLength) {
            char c = this.pattern.charAt(this.pointer);
            this.pointer++;
            switch (this.state) {
                case LITERAL_STATE:
                    handleLiteralState(c, tokenList, buf);
                    break;
                case FORMAT_MODIFIER_STATE:
                    handleFormatModifierState(c, tokenList, buf);
                    break;
                case KEYWORD_STATE:
                    handleKeywordState(c, tokenList, buf);
                    break;
                case OPTION_STATE:
                    processOption(c, tokenList, buf);
                    break;
                case RIGHT_PARENTHESIS_STATE:
                    handleRightParenthesisState(c, tokenList, buf);
                    break;
            }
        }
        switch (this.state) {
            case LITERAL_STATE:
                addValuedToken(1000, buf, tokenList);
                break;
            case FORMAT_MODIFIER_STATE:
            case OPTION_STATE:
                throw new ScanException("Unexpected end of pattern string");
            case KEYWORD_STATE:
                tokenList.add(new Token(1004, buf.toString()));
                break;
            case RIGHT_PARENTHESIS_STATE:
                tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
                break;
        }
        return tokenList;
    }

    private void handleRightParenthesisState(char c, List<Token> tokenList, StringBuffer buf) {
        tokenList.add(Token.RIGHT_PARENTHESIS_TOKEN);
        switch (c) {
            case ')':
                return;
            case '\\':
                escape("%{}", buf);
                this.state = TokenizerState.LITERAL_STATE;
                return;
            case '{':
                this.state = TokenizerState.OPTION_STATE;
                return;
            default:
                buf.append(c);
                this.state = TokenizerState.LITERAL_STATE;
                return;
        }
    }

    private void processOption(char c, List<Token> tokenList, StringBuffer buf) throws ScanException {
        OptionTokenizer ot = new OptionTokenizer(this);
        ot.tokenize(c, tokenList);
    }

    private void handleFormatModifierState(char c, List<Token> tokenList, StringBuffer buf) {
        if (c == '(') {
            addValuedToken(1002, buf, tokenList);
            tokenList.add(Token.BARE_COMPOSITE_KEYWORD_TOKEN);
            this.state = TokenizerState.LITERAL_STATE;
        } else {
            if (Character.isJavaIdentifierStart(c)) {
                addValuedToken(1002, buf, tokenList);
                this.state = TokenizerState.KEYWORD_STATE;
                buf.append(c);
                return;
            }
            buf.append(c);
        }
    }

    private void handleLiteralState(char c, List<Token> tokenList, StringBuffer buf) {
        switch (c) {
            case '%':
                addValuedToken(1000, buf, tokenList);
                tokenList.add(Token.PERCENT_TOKEN);
                this.state = TokenizerState.FORMAT_MODIFIER_STATE;
                return;
            case ')':
                addValuedToken(1000, buf, tokenList);
                this.state = TokenizerState.RIGHT_PARENTHESIS_STATE;
                return;
            case '\\':
                escape("%()", buf);
                return;
            default:
                buf.append(c);
                return;
        }
    }

    private void handleKeywordState(char c, List<Token> tokenList, StringBuffer buf) {
        if (Character.isJavaIdentifierPart(c)) {
            buf.append(c);
            return;
        }
        if (c == '{') {
            addValuedToken(1004, buf, tokenList);
            this.state = TokenizerState.OPTION_STATE;
            return;
        }
        if (c == '(') {
            addValuedToken(PgServer.PG_TYPE_INT2_ARRAY, buf, tokenList);
            this.state = TokenizerState.LITERAL_STATE;
            return;
        }
        if (c == '%') {
            addValuedToken(1004, buf, tokenList);
            tokenList.add(Token.PERCENT_TOKEN);
            this.state = TokenizerState.FORMAT_MODIFIER_STATE;
        } else {
            if (c == ')') {
                addValuedToken(1004, buf, tokenList);
                this.state = TokenizerState.RIGHT_PARENTHESIS_STATE;
                return;
            }
            addValuedToken(1004, buf, tokenList);
            if (c == '\\') {
                if (this.pointer < this.patternLength) {
                    String str = this.pattern;
                    int i = this.pointer;
                    this.pointer = i + 1;
                    char next = str.charAt(i);
                    this.escapeUtil.escape("%()", buf, next, this.pointer);
                }
            } else {
                buf.append(c);
            }
            this.state = TokenizerState.LITERAL_STATE;
        }
    }

    void escape(String escapeChars, StringBuffer buf) {
        if (this.pointer < this.patternLength) {
            String str = this.pattern;
            int i = this.pointer;
            this.pointer = i + 1;
            char next = str.charAt(i);
            this.escapeUtil.escape(escapeChars, buf, next, this.pointer);
        }
    }

    void optionEscape(String escapeChars, StringBuffer buf) {
        if (this.pointer < this.patternLength) {
            String str = this.pattern;
            int i = this.pointer;
            this.pointer = i + 1;
            char next = str.charAt(i);
            this.optionEscapeUtil.escape(escapeChars, buf, next, this.pointer);
        }
    }

    private void addValuedToken(int type, StringBuffer buf, List<Token> tokenList) {
        if (buf.length() > 0) {
            tokenList.add(new Token(type, buf.toString()));
            buf.setLength(0);
        }
    }
}
