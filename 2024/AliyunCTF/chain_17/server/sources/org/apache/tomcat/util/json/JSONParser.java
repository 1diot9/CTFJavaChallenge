package org.apache.tomcat.util.json;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/json/JSONParser.class */
public class JSONParser implements JSONParserConstants {
    private boolean nativeNumbers;
    public JSONParserTokenManager token_source;
    JavaCharStream jj_input_stream;
    public Token token;
    public Token jj_nt;
    private int jj_gen;
    private final int[] jj_la1;
    private static int[] jj_la1_0;
    private List<int[]> jj_expentries;
    private int[] jj_expentry;
    private int jj_kind;
    private int trace_indent;
    private boolean trace_enabled;

    public JSONParser(String input) {
        this(new StringReader(input));
    }

    public LinkedHashMap<String, Object> parseObject() throws ParseException {
        LinkedHashMap<String, Object> toReturn = object();
        if (!ensureEOF()) {
            throw new IllegalStateException("Expected EOF, but still had content to parse");
        }
        return toReturn;
    }

    public ArrayList<Object> parseArray() throws ParseException {
        ArrayList<Object> toReturn = list();
        if (!ensureEOF()) {
            throw new IllegalStateException("Expected EOF, but still had content to parse");
        }
        return toReturn;
    }

    public Object parse() throws ParseException {
        Object toReturn = anything();
        if (!ensureEOF()) {
            throw new IllegalStateException("Expected EOF, but still had content to parse");
        }
        return toReturn;
    }

    private static String substringBefore(String str, char delim) {
        int pos = str.indexOf(delim);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public void setNativeNumbers(boolean value) {
        this.nativeNumbers = value;
    }

    public boolean getNativeNumbers() {
        return this.nativeNumbers;
    }

    public final boolean ensureEOF() throws ParseException {
        jj_consume_token(0);
        return true;
    }

    public final Object anything() throws ParseException {
        Object x;
        switch (this.jj_nt.kind) {
            case 7:
                x = object();
                break;
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 20:
            case 21:
            case 24:
            case 25:
            default:
                this.jj_la1[0] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 10:
                x = list();
                break;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 26:
            case 27:
                x = value();
                break;
        }
        return x;
    }

    public final String objectKey() throws ParseException {
        Object o;
        String key;
        switch (this.jj_nt.kind) {
            case 15:
            case 16:
            case 17:
            case 18:
                switch (this.jj_nt.kind) {
                    case 15:
                    case 16:
                        o = number();
                        break;
                    case 17:
                    case 18:
                        o = booleanValue();
                        break;
                    default:
                        this.jj_la1[1] = this.jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                key = o.toString();
                break;
            case 19:
                nullValue();
                key = null;
                break;
            case 20:
            case 21:
            case 24:
            case 25:
            default:
                this.jj_la1[2] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 22:
            case 23:
            case 26:
            case 27:
                key = string();
                break;
            case 28:
                key = symbol();
                break;
        }
        return key;
    }

    public final LinkedHashMap<String, Object> object() throws ParseException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        jj_consume_token(7);
        switch (this.jj_nt.kind) {
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 26:
            case 27:
            case 28:
                String key = objectKey();
                jj_consume_token(9);
                Object value = anything();
                map.put(key, value);
                while (true) {
                    switch (this.jj_nt.kind) {
                        case 6:
                            jj_consume_token(6);
                            String key2 = objectKey();
                            jj_consume_token(9);
                            Object value2 = anything();
                            map.put(key2, value2);
                        default:
                            this.jj_la1[3] = this.jj_gen;
                            break;
                    }
                }
            case 20:
            case 21:
            case 24:
            case 25:
            default:
                this.jj_la1[4] = this.jj_gen;
                break;
        }
        jj_consume_token(8);
        return map;
    }

    public final ArrayList<Object> list() throws ParseException {
        ArrayList<Object> list = new ArrayList<>();
        jj_consume_token(10);
        switch (this.jj_nt.kind) {
            case 7:
            case 10:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 22:
            case 23:
            case 26:
            case 27:
                Object value = anything();
                list.add(value);
                while (true) {
                    switch (this.jj_nt.kind) {
                        case 6:
                            jj_consume_token(6);
                            Object value2 = anything();
                            list.add(value2);
                        default:
                            this.jj_la1[5] = this.jj_gen;
                            break;
                    }
                }
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 20:
            case 21:
            case 24:
            case 25:
            default:
                this.jj_la1[6] = this.jj_gen;
                break;
        }
        jj_consume_token(11);
        list.trimToSize();
        return list;
    }

    public final Object value() throws ParseException {
        Object x;
        switch (this.jj_nt.kind) {
            case 15:
            case 16:
                x = number();
                break;
            case 17:
            case 18:
                x = booleanValue();
                break;
            case 19:
                x = nullValue();
                break;
            case 20:
            case 21:
            case 24:
            case 25:
            default:
                this.jj_la1[7] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
            case 22:
            case 23:
            case 26:
            case 27:
                x = string();
                break;
        }
        return x;
    }

    public final Object nullValue() throws ParseException {
        jj_consume_token(19);
        return null;
    }

    public final Boolean booleanValue() throws ParseException {
        Boolean b;
        switch (this.jj_nt.kind) {
            case 17:
                jj_consume_token(17);
                b = Boolean.TRUE;
                break;
            case 18:
                jj_consume_token(18);
                b = Boolean.FALSE;
                break;
            default:
                this.jj_la1[8] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        return b;
    }

    public final Number number() throws ParseException {
        switch (this.jj_nt.kind) {
            case 15:
                Token t = jj_consume_token(15);
                if (this.nativeNumbers) {
                    return Double.valueOf(t.image);
                }
                return new BigInteger(substringBefore(t.image, '.'));
            case 16:
                Token t2 = jj_consume_token(16);
                if (this.nativeNumbers) {
                    return Long.valueOf(t2.image);
                }
                return new BigDecimal(t2.image);
            default:
                this.jj_la1[9] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final String string() throws ParseException {
        String s;
        switch (this.jj_nt.kind) {
            case 22:
            case 26:
                s = singleQuoteString();
                break;
            case 23:
            case 27:
                s = doubleQuoteString();
                break;
            case 24:
            case 25:
            default:
                this.jj_la1[10] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
        return s;
    }

    public final String doubleQuoteString() throws ParseException {
        switch (this.jj_nt.kind) {
            case 23:
                jj_consume_token(23);
                return "";
            case 27:
                jj_consume_token(27);
                String image = this.token.image;
                return image.substring(1, image.length() - 1);
            default:
                this.jj_la1[11] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final String singleQuoteString() throws ParseException {
        switch (this.jj_nt.kind) {
            case 22:
                jj_consume_token(22);
                return "";
            case 26:
                jj_consume_token(26);
                String image = this.token.image;
                return image.substring(1, image.length() - 1);
            default:
                this.jj_la1[12] = this.jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    public final String symbol() throws ParseException {
        jj_consume_token(28);
        return this.token.image;
    }

    static {
        jj_la1_init_0();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{214926464, 491520, 483360768, 64, 483360768, 64, 214926464, 214925312, Opcodes.ASM6, 98304, 213909504, 142606336, 71303168};
    }

    public JSONParser(InputStream stream) {
        this(stream, null);
    }

    public JSONParser(InputStream stream, String encoding) {
        this.nativeNumbers = false;
        this.jj_la1 = new int[13];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.trace_indent = 0;
        try {
            this.jj_input_stream = new JavaCharStream(stream, encoding, 1, 1);
            this.token_source = new JSONParserTokenManager(this.jj_input_stream);
            this.token = new Token();
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            this.jj_nt = nextToken;
            token.next = nextToken;
            this.jj_gen = 0;
            for (int i = 0; i < 13; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void ReInit(InputStream stream) {
        ReInit(stream, null);
    }

    public void ReInit(InputStream stream, String encoding) {
        try {
            this.jj_input_stream.ReInit(stream, encoding, 1, 1);
            this.token_source.ReInit(this.jj_input_stream);
            this.token = new Token();
            Token token = this.token;
            Token nextToken = this.token_source.getNextToken();
            this.jj_nt = nextToken;
            token.next = nextToken;
            this.jj_gen = 0;
            for (int i = 0; i < 13; i++) {
                this.jj_la1[i] = -1;
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONParser(Reader stream) {
        this.nativeNumbers = false;
        this.jj_la1 = new int[13];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.trace_indent = 0;
        this.jj_input_stream = new JavaCharStream(stream, 1, 1);
        this.token_source = new JSONParserTokenManager(this.jj_input_stream);
        this.token = new Token();
        Token token = this.token;
        Token nextToken = this.token_source.getNextToken();
        this.jj_nt = nextToken;
        token.next = nextToken;
        this.jj_gen = 0;
        for (int i = 0; i < 13; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(Reader stream) {
        if (this.jj_input_stream == null) {
            this.jj_input_stream = new JavaCharStream(stream, 1, 1);
        } else {
            this.jj_input_stream.ReInit(stream, 1, 1);
        }
        if (this.token_source == null) {
            this.token_source = new JSONParserTokenManager(this.jj_input_stream);
        }
        this.token_source.ReInit(this.jj_input_stream);
        this.token = new Token();
        Token token = this.token;
        Token nextToken = this.token_source.getNextToken();
        this.jj_nt = nextToken;
        token.next = nextToken;
        this.jj_gen = 0;
        for (int i = 0; i < 13; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public JSONParser(JSONParserTokenManager tm) {
        this.nativeNumbers = false;
        this.jj_la1 = new int[13];
        this.jj_expentries = new ArrayList();
        this.jj_kind = -1;
        this.trace_indent = 0;
        this.token_source = tm;
        this.token = new Token();
        Token token = this.token;
        Token nextToken = this.token_source.getNextToken();
        this.jj_nt = nextToken;
        token.next = nextToken;
        this.jj_gen = 0;
        for (int i = 0; i < 13; i++) {
            this.jj_la1[i] = -1;
        }
    }

    public void ReInit(JSONParserTokenManager tm) {
        this.token_source = tm;
        this.token = new Token();
        Token token = this.token;
        Token nextToken = this.token_source.getNextToken();
        this.jj_nt = nextToken;
        token.next = nextToken;
        this.jj_gen = 0;
        for (int i = 0; i < 13; i++) {
            this.jj_la1[i] = -1;
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        Token token = this.jj_nt;
        this.token = token;
        if (token.next != null) {
            this.jj_nt = this.jj_nt.next;
        } else {
            Token token2 = this.jj_nt;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            this.jj_nt = nextToken;
        }
        if (this.token.kind == kind) {
            this.jj_gen++;
            return this.token;
        }
        this.jj_nt = this.token;
        this.token = oldToken;
        this.jj_kind = kind;
        throw generateParseException();
    }

    public final Token getNextToken() {
        Token token = this.jj_nt;
        this.token = token;
        if (token.next != null) {
            this.jj_nt = this.jj_nt.next;
        } else {
            Token token2 = this.jj_nt;
            Token nextToken = this.token_source.getNextToken();
            token2.next = nextToken;
            this.jj_nt = nextToken;
        }
        this.jj_gen++;
        return this.token;
    }

    public final Token getToken(int index) {
        Token token;
        Token t = this.token;
        for (int i = 0; i < index; i++) {
            if (t.next != null) {
                token = t.next;
            } else {
                Token nextToken = this.token_source.getNextToken();
                token = nextToken;
                t.next = nextToken;
            }
            t = token;
        }
        return t;
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [int[], int[][]] */
    public ParseException generateParseException() {
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[29];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (int i = 0; i < 13; i++) {
            if (this.jj_la1[i] == this.jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                }
            }
        }
        for (int i2 = 0; i2 < 29; i2++) {
            if (la1tokens[i2]) {
                this.jj_expentry = new int[1];
                this.jj_expentry[0] = i2;
                this.jj_expentries.add(this.jj_expentry);
            }
        }
        ?? r0 = new int[this.jj_expentries.size()];
        for (int i3 = 0; i3 < this.jj_expentries.size(); i3++) {
            r0[i3] = this.jj_expentries.get(i3);
        }
        return new ParseException(this.token, r0, tokenImage);
    }

    public final boolean trace_enabled() {
        return this.trace_enabled;
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }
}
