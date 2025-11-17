package org.jooq.tools.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/json/JSONValue.class */
public class JSONValue {
    public static void writeJSONString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }
        if (value instanceof String) {
            out.write(34);
            out.write(escape((String) value));
            out.write(34);
            return;
        }
        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
                out.write("null");
                return;
            } else {
                out.write(value.toString());
                return;
            }
        }
        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN()) {
                out.write("null");
                return;
            } else {
                out.write(value.toString());
                return;
            }
        }
        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }
        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }
        if (value instanceof Map) {
            JSONObject.writeJSONString((Map) value, out);
        } else {
            if (value instanceof List) {
                JSONArray.writeJSONString((List) value, out);
                return;
            }
            out.write(34);
            out.write(escape(value.toString()));
            out.write(34);
        }
    }

    public static String toJSONString(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + escape((String) value) + "\"";
        }
        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
                return "null";
            }
            return value.toString();
        }
        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN()) {
                return "null";
            }
            return value.toString();
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map) {
            return JSONObject.toJSONString((Map) value);
        }
        if (value instanceof List) {
            return JSONArray.toJSONString((List) value);
        }
        return "\"" + escape(value.toString()) + "\"";
    }

    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); i++) {
            char ch2 = s.charAt(i);
            switch (ch2) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if ((ch2 >= 0 && ch2 <= 31) || ((ch2 >= 127 && ch2 <= 159) || (ch2 >= 8192 && ch2 <= 8447))) {
                        String ss = Integer.toHexString(ch2);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                        break;
                    } else {
                        sb.append(ch2);
                        break;
                    }
                    break;
            }
        }
    }

    public static Object parseWithException(Reader in) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(in);
    }

    public static Object parseWithException(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(s);
    }
}
