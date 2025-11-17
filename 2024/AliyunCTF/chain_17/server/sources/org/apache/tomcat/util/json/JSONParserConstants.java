package org.apache.tomcat.util.json;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/json/JSONParserConstants.class */
public interface JSONParserConstants {
    public static final int EOF = 0;
    public static final int C_SINGLE_COMMENT = 1;
    public static final int C_MULTILINE_COMMENT = 2;
    public static final int SH_SINGLE_COMMENT = 3;
    public static final int WHITESPACE = 4;
    public static final int EOL = 5;
    public static final int COMMA = 6;
    public static final int BRACE_OPEN = 7;
    public static final int BRACE_CLOSE = 8;
    public static final int COLON = 9;
    public static final int BRACKET_OPEN = 10;
    public static final int BRACKET_CLOSE = 11;
    public static final int ZERO = 12;
    public static final int DIGIT_NONZERO = 13;
    public static final int DIGIT = 14;
    public static final int NUMBER_INTEGER = 15;
    public static final int NUMBER_DECIMAL = 16;
    public static final int TRUE = 17;
    public static final int FALSE = 18;
    public static final int NULL = 19;
    public static final int QUOTE_DOUBLE = 20;
    public static final int QUOTE_SINGLE = 21;
    public static final int STRING_SINGLE_EMPTY = 22;
    public static final int STRING_DOUBLE_EMPTY = 23;
    public static final int STRING_SINGLE_BODY = 24;
    public static final int STRING_DOUBLE_BODY = 25;
    public static final int STRING_SINGLE_NONEMPTY = 26;
    public static final int STRING_DOUBLE_NONEMPTY = 27;
    public static final int SYMBOL = 28;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = {"<EOF>", "<C_SINGLE_COMMENT>", "<C_MULTILINE_COMMENT>", "<SH_SINGLE_COMMENT>", "<WHITESPACE>", "<EOL>", "\",\"", "\"{\"", "\"}\"", "\":\"", "\"[\"", "\"]\"", "\"0\"", "<DIGIT_NONZERO>", "<DIGIT>", "<NUMBER_INTEGER>", "<NUMBER_DECIMAL>", "\"true\"", "\"false\"", "\"null\"", "\"\\\"\"", "\"\\'\"", "\"\\'\\'\"", "\"\\\"\\\"\"", "<STRING_SINGLE_BODY>", "<STRING_DOUBLE_BODY>", "<STRING_SINGLE_NONEMPTY>", "<STRING_DOUBLE_NONEMPTY>", "<SYMBOL>"};
}
