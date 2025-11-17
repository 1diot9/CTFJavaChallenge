package org.apache.catalina.valves.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/QuotedStringTokenizer.class */
public class QuotedStringTokenizer {
    protected static final StringManager sm = StringManager.getManager((Class<?>) QuotedStringTokenizer.class);
    private Iterator<String> tokenIterator;
    private int tokenCount;
    private int returnedTokens = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/rewrite/QuotedStringTokenizer$WordMode.class */
    public enum WordMode {
        SPACES,
        QUOTED,
        ESCAPED,
        SIMPLE,
        COMMENT
    }

    public QuotedStringTokenizer(String text) {
        List<String> tokens;
        if (text != null) {
            tokens = tokenizeText(text);
        } else {
            tokens = Collections.emptyList();
        }
        this.tokenCount = tokens.size();
        this.tokenIterator = tokens.iterator();
    }

    private List<String> tokenizeText(String inputText) {
        List<String> tokens = new ArrayList<>();
        int length = inputText.length();
        WordMode currentMode = WordMode.SPACES;
        StringBuilder currentToken = new StringBuilder();
        for (int pos = 0; pos < length; pos++) {
            char currentChar = inputText.charAt(pos);
            switch (currentMode) {
                case SPACES:
                    currentMode = handleSpaces(currentToken, currentChar);
                    break;
                case QUOTED:
                    currentMode = handleQuoted(tokens, currentToken, currentChar);
                    break;
                case ESCAPED:
                    currentToken.append(currentChar);
                    currentMode = WordMode.QUOTED;
                    break;
                case SIMPLE:
                    currentMode = handleSimple(tokens, currentToken, currentChar);
                    break;
                case COMMENT:
                    if (currentChar != '\r' && currentChar != '\n') {
                        break;
                    } else {
                        currentMode = WordMode.SPACES;
                        break;
                    }
                    break;
                default:
                    throw new IllegalStateException(sm.getString("quotedStringTokenizer.tokenizeError", inputText, Integer.valueOf(pos), currentMode));
            }
        }
        String possibleLastToken = currentToken.toString();
        if (!possibleLastToken.isEmpty()) {
            tokens.add(possibleLastToken);
        }
        return tokens;
    }

    private WordMode handleSimple(List<String> tokens, StringBuilder currentToken, char currentChar) {
        if (Character.isWhitespace(currentChar)) {
            tokens.add(currentToken.toString());
            currentToken.setLength(0);
            return WordMode.SPACES;
        }
        currentToken.append(currentChar);
        return WordMode.SIMPLE;
    }

    private WordMode handleQuoted(List<String> tokens, StringBuilder currentToken, char currentChar) {
        if (currentChar == '\"') {
            tokens.add(currentToken.toString());
            currentToken.setLength(0);
            return WordMode.SPACES;
        }
        if (currentChar == '\\') {
            return WordMode.ESCAPED;
        }
        currentToken.append(currentChar);
        return WordMode.QUOTED;
    }

    private WordMode handleSpaces(StringBuilder currentToken, char currentChar) {
        if (!Character.isWhitespace(currentChar)) {
            if (currentChar == '\"') {
                return WordMode.QUOTED;
            }
            if (currentChar == '#') {
                return WordMode.COMMENT;
            }
            currentToken.append(currentChar);
            return WordMode.SIMPLE;
        }
        return WordMode.SPACES;
    }

    public boolean hasMoreTokens() {
        return this.tokenIterator.hasNext();
    }

    public String nextToken() {
        this.returnedTokens++;
        return this.tokenIterator.next();
    }

    public int countTokens() {
        return this.tokenCount - this.returnedTokens;
    }
}
