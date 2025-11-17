package ch.qos.logback.core.subst;

import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.subst.Token;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/subst/Tokenizer.class */
public class Tokenizer {
    final String pattern;
    final int patternLength;
    TokenizerState state = TokenizerState.LITERAL_STATE;
    int pointer = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/subst/Tokenizer$TokenizerState.class */
    public enum TokenizerState {
        LITERAL_STATE,
        START_STATE,
        DEFAULT_VAL_STATE
    }

    public Tokenizer(String pattern) {
        this.pattern = pattern;
        this.patternLength = pattern.length();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<Token> tokenize() throws ScanException {
        List<Token> tokenList = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        while (this.pointer < this.patternLength) {
            char c = this.pattern.charAt(this.pointer);
            this.pointer++;
            switch (this.state) {
                case LITERAL_STATE:
                    handleLiteralState(c, tokenList, buf);
                    break;
                case START_STATE:
                    handleStartState(c, tokenList, buf);
                    break;
                case DEFAULT_VAL_STATE:
                    handleDefaultValueState(c, tokenList, buf);
                    break;
            }
        }
        switch (this.state) {
            case LITERAL_STATE:
                addLiteralToken(tokenList, buf);
                break;
            case START_STATE:
                buf.append('$');
                addLiteralToken(tokenList, buf);
                break;
            case DEFAULT_VAL_STATE:
                buf.append(':');
                addLiteralToken(tokenList, buf);
                break;
        }
        return tokenList;
    }

    private void handleDefaultValueState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        switch (c) {
            case '$':
                stringBuilder.append(':');
                addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.START_STATE;
                return;
            case '-':
                tokenList.add(Token.DEFAULT_SEP_TOKEN);
                this.state = TokenizerState.LITERAL_STATE;
                return;
            case '{':
                stringBuilder.append(':');
                addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                tokenList.add(Token.CURLY_LEFT_TOKEN);
                this.state = TokenizerState.LITERAL_STATE;
                return;
            default:
                stringBuilder.append(':').append(c);
                this.state = TokenizerState.LITERAL_STATE;
                return;
        }
    }

    private void handleStartState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        if (c == '{') {
            tokenList.add(Token.START_TOKEN);
        } else {
            stringBuilder.append('$').append(c);
        }
        this.state = TokenizerState.LITERAL_STATE;
    }

    private void handleLiteralState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        switch (c) {
            case '$':
                addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.START_STATE;
                return;
            case ':':
                addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.DEFAULT_VAL_STATE;
                return;
            case '{':
                addLiteralToken(tokenList, stringBuilder);
                tokenList.add(Token.CURLY_LEFT_TOKEN);
                stringBuilder.setLength(0);
                return;
            case '}':
                addLiteralToken(tokenList, stringBuilder);
                tokenList.add(Token.CURLY_RIGHT_TOKEN);
                stringBuilder.setLength(0);
                return;
            default:
                stringBuilder.append(c);
                return;
        }
    }

    private void addLiteralToken(List<Token> tokenList, StringBuilder stringBuilder) {
        if (stringBuilder.length() == 0) {
            return;
        }
        tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
    }
}
