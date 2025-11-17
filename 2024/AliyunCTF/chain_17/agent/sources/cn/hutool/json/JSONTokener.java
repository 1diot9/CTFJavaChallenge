package cn.hutool.json;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONTokener.class */
public class JSONTokener {
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private boolean usePrevious;
    private final Reader reader;
    private final JSONConfig config;

    public JSONTokener(Reader reader, JSONConfig config) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = (char) 0;
        this.index = 0L;
        this.character = 1L;
        this.line = 1L;
        this.config = config;
    }

    public JSONTokener(InputStream inputStream, JSONConfig config) throws JSONException {
        this(IoUtil.getUtf8Reader(inputStream), config);
    }

    public JSONTokener(CharSequence s, JSONConfig config) {
        this(new StringReader(StrUtil.str(s)), config);
    }

    public void back() throws JSONException {
        if (this.usePrevious || this.index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        this.index--;
        this.character--;
        this.usePrevious = true;
        this.eof = false;
    }

    public boolean end() {
        return this.eof && false == this.usePrevious;
    }

    public boolean more() throws JSONException {
        next();
        if (end()) {
            return false;
        }
        back();
        return true;
    }

    public char next() throws JSONException {
        int c;
        if (this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
                if (c <= 0) {
                    this.eof = true;
                    c = 0;
                }
            } catch (IOException exception) {
                throw new JSONException(exception);
            }
        }
        this.index++;
        if (this.previous == '\r') {
            this.line++;
            this.character = c == 10 ? 0L : 1L;
        } else if (c == 10) {
            this.line++;
            this.character = 0L;
        } else {
            this.character++;
        }
        this.previous = (char) c;
        return this.previous;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public char getPrevious() {
        return this.previous;
    }

    public char next(char c) throws JSONException {
        char n = next();
        if (n != c) {
            throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
        }
        return n;
    }

    public String next(int n) throws JSONException {
        if (n == 0) {
            return "";
        }
        char[] chars = new char[n];
        for (int pos = 0; pos < n; pos++) {
            chars[pos] = next();
            if (end()) {
                throw syntaxError("Substring bounds error");
            }
        }
        return new String(chars);
    }

    public char nextClean() throws JSONException {
        char c;
        do {
            c = next();
            if (c == 0) {
                break;
            }
        } while (c <= ' ');
        return c;
    }

    public String nextString(char quote) throws JSONException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = next();
            switch (c) {
                case 0:
                case '\n':
                case '\r':
                    throw syntaxError("Unterminated string");
                case '\\':
                    char c2 = next();
                    switch (c2) {
                        case '\"':
                        case '\'':
                        case '/':
                        case '\\':
                            sb.append(c2);
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case Opcodes.FDIV /* 110 */:
                            sb.append('\n');
                            break;
                        case Opcodes.FREM /* 114 */:
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case Opcodes.LNEG /* 117 */:
                            sb.append((char) Integer.parseInt(next(4), 16));
                            break;
                        default:
                            throw syntaxError("Illegal escape.");
                    }
                default:
                    if (c == quote) {
                        return sb.toString();
                    }
                    sb.append(c);
                    break;
            }
        }
    }

    public String nextTo(char delimiter) throws JSONException {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = next();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                break;
            }
            sb.append(c);
        }
        if (c != 0) {
            back();
        }
        return sb.toString().trim();
    }

    public String nextTo(String delimiters) throws JSONException {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = next();
            if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
                break;
            }
            sb.append(c);
        }
        if (c != 0) {
            back();
        }
        return sb.toString().trim();
    }

    public Object nextValue() throws JSONException {
        char c = nextClean();
        switch (c) {
            case '\"':
            case '\'':
                return nextString(c);
            case '[':
                back();
                try {
                    return new JSONArray(this, this.config);
                } catch (StackOverflowError e) {
                    throw new JSONException("JSONArray depth too large to process.", e);
                }
            case '{':
                back();
                try {
                    return new JSONObject(this, this.config);
                } catch (StackOverflowError e2) {
                    throw new JSONException("JSONObject depth too large to process.", e2);
                }
            default:
                StringBuilder sb = new StringBuilder();
                while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
                    sb.append(c);
                    c = next();
                }
                back();
                String string = sb.toString().trim();
                if (0 == string.length()) {
                    throw syntaxError("Missing value");
                }
                return InternalJSONUtil.stringToValue(string);
        }
    }

    public char skipTo(char to) throws JSONException {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                c = next();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while (c != to);
            back();
            return c;
        } catch (IOException exception) {
            throw new JSONException(exception);
        }
    }

    public JSONException syntaxError(String message) {
        return new JSONException(message + this);
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray(this.config);
        if (nextClean() != '[') {
            throw syntaxError("A JSONArray text must start with '['");
        }
        if (nextClean() != ']') {
            back();
            while (true) {
                if (nextClean() == ',') {
                    back();
                    jsonArray.add(JSONNull.NULL);
                } else {
                    back();
                    jsonArray.add(nextValue());
                }
                switch (nextClean()) {
                    case ',':
                        if (nextClean() == ']') {
                            return jsonArray;
                        }
                        back();
                    case ']':
                        return jsonArray;
                    default:
                        throw syntaxError("Expected a ',' or ']'");
                }
            }
        } else {
            return jsonArray;
        }
    }

    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
    }
}
