package org.apache.tomcat.util.json;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/json/ParseException.class */
public class ParseException extends Exception {
    private static final long serialVersionUID = 1;
    protected static String EOL = System.getProperty("line.separator", "\n");
    public Token currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;

    public ParseException(Token currentTokenVal, int[][] expectedTokenSequencesVal, String[] tokenImageVal) {
        super(initialise(currentTokenVal, expectedTokenSequencesVal, tokenImageVal));
        this.currentToken = currentTokenVal;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
    }

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    private static String initialise(Token currentToken, int[][] expectedTokenSequences, String[] tokenImage) {
        String retval;
        StringBuffer expected = new StringBuffer();
        int maxSize = 0;
        for (int i = 0; i < expectedTokenSequences.length; i++) {
            if (maxSize < expectedTokenSequences[i].length) {
                maxSize = expectedTokenSequences[i].length;
            }
            for (int j = 0; j < expectedTokenSequences[i].length; j++) {
                expected.append(tokenImage[expectedTokenSequences[i][j]]).append(' ');
            }
            if (expectedTokenSequences[i][expectedTokenSequences[i].length - 1] != 0) {
                expected.append("...");
            }
            expected.append(EOL).append("    ");
        }
        String retval2 = "Encountered \"";
        Token tok = currentToken.next;
        int i2 = 0;
        while (true) {
            if (i2 >= maxSize) {
                break;
            }
            if (i2 != 0) {
                retval2 = retval2 + " ";
            }
            if (tok.kind == 0) {
                retval2 = retval2 + tokenImage[0];
                break;
            }
            retval2 = (((retval2 + " " + tokenImage[tok.kind]) + " \"") + add_escapes(tok.image)) + " \"";
            tok = tok.next;
            i2++;
        }
        String retval3 = (retval2 + "\" at line " + currentToken.next.beginLine + ", column " + currentToken.next.beginColumn) + "." + EOL;
        if (expectedTokenSequences.length != 0) {
            if (expectedTokenSequences.length == 1) {
                retval = retval3 + "Was expecting:" + EOL + "    ";
            } else {
                retval = retval3 + "Was expecting one of:" + EOL + "    ";
            }
            retval3 = retval + expected.toString();
        }
        return retval3;
    }

    static String add_escapes(String str) {
        StringBuffer retval = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case '\b':
                    retval.append("\\b");
                    break;
                case '\t':
                    retval.append("\\t");
                    break;
                case '\n':
                    retval.append("\\n");
                    break;
                case '\f':
                    retval.append("\\f");
                    break;
                case '\r':
                    retval.append("\\r");
                    break;
                case '\"':
                    retval.append("\\\"");
                    break;
                case '\'':
                    retval.append("\\'");
                    break;
                case '\\':
                    retval.append("\\\\");
                    break;
                default:
                    char ch2 = str.charAt(i);
                    if (ch2 < ' ' || ch2 > '~') {
                        String s = "0000" + Integer.toString(ch2, 16);
                        retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                        break;
                    } else {
                        retval.append(ch2);
                        break;
                    }
            }
        }
        return retval.toString();
    }
}
