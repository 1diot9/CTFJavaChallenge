package org.apache.tomcat.util.http.fileupload;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.util.mime.MimeUtility;
import org.apache.tomcat.util.http.fileupload.util.mime.RFC2231Utility;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/ParameterParser.class */
public class ParameterParser {
    private char[] chars = null;
    private int pos = 0;
    private int len = 0;
    private int i1 = 0;
    private int i2 = 0;
    private boolean lowerCaseNames = false;

    private boolean hasChar() {
        return this.pos < this.len;
    }

    private String getToken(boolean quoted) {
        while (this.i1 < this.i2 && Character.isWhitespace(this.chars[this.i1])) {
            this.i1++;
        }
        while (this.i2 > this.i1 && Character.isWhitespace(this.chars[this.i2 - 1])) {
            this.i2--;
        }
        if (quoted && this.i2 - this.i1 >= 2 && this.chars[this.i1] == '\"' && this.chars[this.i2 - 1] == '\"') {
            this.i1++;
            this.i2--;
        }
        String result = null;
        if (this.i2 > this.i1) {
            result = new String(this.chars, this.i1, this.i2 - this.i1);
        }
        return result;
    }

    private boolean isOneOf(char ch2, char[] charray) {
        boolean result = false;
        int length = charray.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            char element = charray[i];
            if (ch2 != element) {
                i++;
            } else {
                result = true;
                break;
            }
        }
        return result;
    }

    private String parseToken(char[] terminators) {
        this.i1 = this.pos;
        this.i2 = this.pos;
        while (hasChar()) {
            char ch2 = this.chars[this.pos];
            if (isOneOf(ch2, terminators)) {
                break;
            }
            this.i2++;
            this.pos++;
        }
        return getToken(false);
    }

    private String parseQuotedToken(char[] terminators) {
        this.i1 = this.pos;
        this.i2 = this.pos;
        boolean quoted = false;
        boolean charEscaped = false;
        while (hasChar()) {
            char ch2 = this.chars[this.pos];
            if (!quoted && isOneOf(ch2, terminators)) {
                break;
            }
            if (!charEscaped && ch2 == '\"') {
                quoted = !quoted;
            }
            charEscaped = !charEscaped && ch2 == '\\';
            this.i2++;
            this.pos++;
        }
        return getToken(true);
    }

    public boolean isLowerCaseNames() {
        return this.lowerCaseNames;
    }

    public void setLowerCaseNames(boolean b) {
        this.lowerCaseNames = b;
    }

    public Map<String, String> parse(String str, char[] separators) {
        if (separators == null || separators.length == 0) {
            return new HashMap();
        }
        char separator = separators[0];
        if (str != null) {
            int idx = str.length();
            for (char separator2 : separators) {
                int tmp = str.indexOf(separator2);
                if (tmp != -1 && tmp < idx) {
                    idx = tmp;
                    separator = separator2;
                }
            }
        }
        return parse(str, separator);
    }

    public Map<String, String> parse(String str, char separator) {
        if (str == null) {
            return new HashMap();
        }
        return parse(str.toCharArray(), separator);
    }

    public Map<String, String> parse(char[] charArray, char separator) {
        if (charArray == null) {
            return new HashMap();
        }
        return parse(charArray, 0, charArray.length, separator);
    }

    public Map<String, String> parse(char[] charArray, int offset, int length, char separator) {
        if (charArray == null) {
            return new HashMap();
        }
        HashMap<String, String> params = new HashMap<>();
        this.chars = (char[]) charArray.clone();
        this.pos = offset;
        this.len = length;
        while (hasChar()) {
            String paramName = parseToken(new char[]{'=', separator});
            String paramValue = null;
            if (hasChar() && charArray[this.pos] == '=') {
                this.pos++;
                paramValue = parseQuotedToken(new char[]{separator});
                if (paramValue != null) {
                    try {
                        paramValue = RFC2231Utility.hasEncodedValue(paramName) ? RFC2231Utility.decodeText(paramValue) : MimeUtility.decodeText(paramValue);
                    } catch (UnsupportedEncodingException e) {
                    }
                }
            }
            if (hasChar() && charArray[this.pos] == separator) {
                this.pos++;
            }
            if (paramName != null && !paramName.isEmpty()) {
                String paramName2 = RFC2231Utility.stripDelimiter(paramName);
                if (this.lowerCaseNames) {
                    paramName2 = paramName2.toLowerCase(Locale.ENGLISH);
                }
                params.put(paramName2, paramValue);
            }
        }
        return params;
    }
}
