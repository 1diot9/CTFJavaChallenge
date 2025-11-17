package cn.hutool.json.xml;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import java.util.Iterator;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/xml/JSONXMLSerializer.class */
public class JSONXMLSerializer {
    public static String toXml(Object object) throws JSONException {
        return toXml(object, null);
    }

    public static String toXml(Object object, String tagName) throws JSONException {
        return toXml(object, tagName, "content");
    }

    public static String toXml(Object object, String tagName, String... contentKeys) throws JSONException {
        if (null == object) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (object instanceof JSONObject) {
            appendTag(sb, tagName, false);
            ((JSONObject) object).forEach((key, value) -> {
                if (ArrayUtil.isArray(value)) {
                    value = new JSONArray(value);
                }
                if (ArrayUtil.contains(contentKeys, key)) {
                    if (value instanceof JSONArray) {
                        int i = 0;
                        Iterator<Object> it = ((JSONArray) value).iterator();
                        while (it.hasNext()) {
                            Object val = it.next();
                            if (i > 0) {
                                sb.append('\n');
                            }
                            sb.append(EscapeUtil.escapeXml(val.toString()));
                            i++;
                        }
                        return;
                    }
                    sb.append(EscapeUtil.escapeXml(value.toString()));
                    return;
                }
                if (StrUtil.isEmptyIfStr(value)) {
                    sb.append(wrapWithTag(null, key));
                    return;
                }
                if (value instanceof JSONArray) {
                    Iterator<Object> it2 = ((JSONArray) value).iterator();
                    while (it2.hasNext()) {
                        Object val2 = it2.next();
                        if (val2 instanceof JSONArray) {
                            sb.append(wrapWithTag(toXml(val2, null, contentKeys), key));
                        } else {
                            sb.append(toXml(val2, key, contentKeys));
                        }
                    }
                    return;
                }
                sb.append(toXml(value, key, contentKeys));
            });
            appendTag(sb, tagName, true);
            return sb.toString();
        }
        if (ArrayUtil.isArray(object)) {
            object = new JSONArray(object);
        }
        if (object instanceof JSONArray) {
            Iterator<Object> it = ((JSONArray) object).iterator();
            while (it.hasNext()) {
                Object val = it.next();
                sb.append(toXml(val, tagName == null ? BeanDefinitionParserDelegate.ARRAY_ELEMENT : tagName, contentKeys));
            }
            return sb.toString();
        }
        return wrapWithTag(EscapeUtil.escapeXml(object.toString()), tagName);
    }

    private static void appendTag(StringBuilder sb, String tagName, boolean isEndTag) {
        if (StrUtil.isNotBlank(tagName)) {
            sb.append('<');
            if (isEndTag) {
                sb.append('/');
            }
            sb.append(tagName).append('>');
        }
    }

    private static String wrapWithTag(String content, String tagName) {
        if (StrUtil.isBlank(tagName)) {
            return StrUtil.wrap(content, "\"");
        }
        if (StrUtil.isEmpty(content)) {
            return "<" + tagName + "/>";
        }
        return "<" + tagName + ">" + content + "</" + tagName + ">";
    }
}
