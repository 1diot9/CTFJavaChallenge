package org.jooq.tools.json;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/json/JSONObject.class */
public class JSONObject extends HashMap {
    public JSONObject() {
    }

    public JSONObject(Map map) {
        super(map);
    }

    public static void writeJSONString(Map<?, ?> map, Writer out) throws IOException {
        if (map == null) {
            out.write("null");
            return;
        }
        boolean first = true;
        out.write(123);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                out.write(44);
            }
            out.write(34);
            out.write(escape(String.valueOf(entry.getKey())));
            out.write(34);
            out.write(58);
            JSONValue.writeJSONString(entry.getValue(), out);
        }
        out.write(125);
    }

    public static String toJSONString(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        sb.append('{');
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
        }
        sb.append('}');
        return sb.toString();
    }

    private static String toJSONString(String key, Object value, StringBuffer sb) {
        sb.append('\"');
        if (key == null) {
            sb.append("null");
        } else {
            JSONValue.escape(key, sb);
        }
        sb.append('\"').append(':');
        sb.append(JSONValue.toJSONString(value));
        return sb.toString();
    }

    public static String toString(String key, Object value) {
        StringBuffer sb = new StringBuffer();
        toJSONString(key, value, sb);
        return sb.toString();
    }

    @Override // java.util.AbstractMap
    public String toString() {
        return toJSONString(this);
    }

    public static String escape(String s) {
        return JSONValue.escape(s);
    }
}
