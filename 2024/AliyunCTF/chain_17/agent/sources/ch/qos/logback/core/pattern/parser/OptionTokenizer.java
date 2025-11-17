package ch.qos.logback.core.pattern.parser;

import ch.qos.logback.core.pattern.parser.TokenStream;
import ch.qos.logback.core.pattern.util.AsIsEscapeUtil;
import ch.qos.logback.core.pattern.util.IEscapeUtil;
import ch.qos.logback.core.spi.ScanException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/parser/OptionTokenizer.class */
public class OptionTokenizer {
    private static final int EXPECTING_STATE = 0;
    private static final int RAW_COLLECTING_STATE = 1;
    private static final int QUOTED_COLLECTING_STATE = 2;
    final IEscapeUtil escapeUtil;
    final TokenStream tokenStream;
    final String pattern;
    final int patternLength;
    char quoteChar;
    int state;

    /* JADX INFO: Access modifiers changed from: package-private */
    public OptionTokenizer(TokenStream tokenStream) {
        this(tokenStream, new AsIsEscapeUtil());
    }

    OptionTokenizer(TokenStream tokenStream, IEscapeUtil escapeUtil) {
        this.state = 0;
        this.tokenStream = tokenStream;
        this.pattern = tokenStream.pattern;
        this.patternLength = tokenStream.patternLength;
        this.escapeUtil = escapeUtil;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void tokenize(char firstChar, List<Token> tokenList) throws ScanException {
        StringBuffer buf = new StringBuffer();
        List<String> optionList = new ArrayList<>();
        char c = firstChar;
        while (this.tokenStream.pointer < this.patternLength) {
            switch (this.state) {
                case 0:
                    switch (c) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                        case ',':
                            break;
                        case '\"':
                        case '\'':
                            this.state = 2;
                            this.quoteChar = c;
                            break;
                        case '}':
                            emitOptionToken(tokenList, optionList);
                            return;
                        default:
                            buf.append(c);
                            this.state = 1;
                            break;
                    }
                case 1:
                    switch (c) {
                        case ',':
                            optionList.add(buf.toString().trim());
                            buf.setLength(0);
                            this.state = 0;
                            break;
                        case '}':
                            optionList.add(buf.toString().trim());
                            emitOptionToken(tokenList, optionList);
                            return;
                        default:
                            buf.append(c);
                            break;
                    }
                case 2:
                    if (c == this.quoteChar) {
                        optionList.add(buf.toString());
                        buf.setLength(0);
                        this.state = 0;
                        break;
                    } else if (c == '\\') {
                        escape(String.valueOf(this.quoteChar), buf);
                        break;
                    } else {
                        buf.append(c);
                        break;
                    }
            }
            c = this.pattern.charAt(this.tokenStream.pointer);
            this.tokenStream.pointer++;
        }
        if (c == '}') {
            if (this.state == 0) {
                emitOptionToken(tokenList, optionList);
                return;
            } else {
                if (this.state == 1) {
                    optionList.add(buf.toString().trim());
                    emitOptionToken(tokenList, optionList);
                    return;
                }
                throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
            }
        }
        throw new ScanException("Unexpected end of pattern string in OptionTokenizer");
    }

    void emitOptionToken(List<Token> tokenList, List<String> optionList) {
        tokenList.add(new Token(1006, optionList));
        this.tokenStream.state = TokenStream.TokenizerState.LITERAL_STATE;
    }

    void escape(String escapeChars, StringBuffer buf) {
        if (this.tokenStream.pointer < this.patternLength) {
            String str = this.pattern;
            TokenStream tokenStream = this.tokenStream;
            int i = tokenStream.pointer;
            tokenStream.pointer = i + 1;
            char next = str.charAt(i);
            this.escapeUtil.escape(escapeChars, buf, next, this.tokenStream.pointer);
        }
    }
}
