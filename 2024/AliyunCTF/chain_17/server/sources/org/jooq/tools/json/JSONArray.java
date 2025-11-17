package org.jooq.tools.json;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/json/JSONArray.class */
public class JSONArray extends ArrayList {
    public JSONArray() {
    }

    public JSONArray(Collection c) {
        super(c);
    }

    public static void writeJSONString(List<?> list, Writer out) throws IOException {
        if (list == null) {
            out.write("null");
            return;
        }
        boolean first = true;
        out.write(91);
        for (Object value : list) {
            if (first) {
                first = false;
            } else {
                out.write(44);
            }
            if (value == null) {
                out.write("null");
            } else {
                JSONValue.writeJSONString(value, out);
            }
        }
        out.write(93);
    }

    public static String toJSONString(List<?> list) {
        if (list == null) {
            return "null";
        }
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (Object value : list) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            if (value == null) {
                sb.append("null");
            } else {
                sb.append(JSONValue.toJSONString(value));
            }
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        return toJSONString(this);
    }
}
