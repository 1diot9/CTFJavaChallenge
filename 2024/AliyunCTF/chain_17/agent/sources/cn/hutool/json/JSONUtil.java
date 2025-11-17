package cn.hutool.json;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.serialize.GlobalSerializeMapping;
import cn.hutool.json.serialize.JSONArraySerializer;
import cn.hutool.json.serialize.JSONDeserializer;
import cn.hutool.json.serialize.JSONObjectSerializer;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONUtil.class */
public class JSONUtil {
    public static JSONObject createObj() {
        return new JSONObject();
    }

    public static JSONObject createObj(JSONConfig config) {
        return new JSONObject(config);
    }

    public static JSONArray createArray() {
        return new JSONArray();
    }

    public static JSONArray createArray(JSONConfig config) {
        return new JSONArray(config);
    }

    public static JSONObject parseObj(String jsonStr) {
        return new JSONObject(jsonStr);
    }

    public static JSONObject parseObj(Object obj) {
        return parseObj(obj, (JSONConfig) null);
    }

    public static JSONObject parseObj(Object obj, JSONConfig config) {
        return new JSONObject(obj, (JSONConfig) ObjectUtil.defaultIfNull(config, (Supplier<? extends JSONConfig>) JSONConfig::create));
    }

    public static JSONObject parseObj(Object obj, boolean ignoreNullValue) {
        return new JSONObject(obj, ignoreNullValue);
    }

    @Deprecated
    public static JSONObject parseObj(Object obj, boolean ignoreNullValue, boolean isOrder) {
        return new JSONObject(obj, ignoreNullValue);
    }

    public static JSONArray parseArray(String jsonStr) {
        return new JSONArray(jsonStr);
    }

    public static JSONArray parseArray(Object arrayOrCollection) {
        return parseArray(arrayOrCollection, (JSONConfig) null);
    }

    public static JSONArray parseArray(Object arrayOrCollection, JSONConfig config) {
        return new JSONArray(arrayOrCollection, config);
    }

    public static JSONArray parseArray(Object arrayOrCollection, boolean ignoreNullValue) {
        return new JSONArray(arrayOrCollection, ignoreNullValue);
    }

    public static JSON parse(Object obj) {
        return parse(obj, null);
    }

    public static JSON parse(Object obj, JSONConfig config) {
        JSON json;
        if (null == obj) {
            return null;
        }
        if (obj instanceof JSON) {
            json = (JSON) obj;
        } else if (obj instanceof CharSequence) {
            String jsonStr = StrUtil.trim((CharSequence) obj);
            json = isTypeJSONArray(jsonStr) ? parseArray(jsonStr, config) : parseObj(jsonStr, config);
        } else if (obj instanceof MapWrapper) {
            json = parseObj(obj, config);
        } else if ((obj instanceof Iterable) || (obj instanceof Iterator) || ArrayUtil.isArray(obj)) {
            json = parseArray(obj, config);
        } else {
            json = parseObj(obj, config);
        }
        return json;
    }

    public static JSONObject parseFromXml(String xmlStr) {
        return XML.toJSONObject(xmlStr);
    }

    public static JSON readJSON(File file, Charset charset) throws IORuntimeException {
        return parse(FileReader.create(file, charset).readString());
    }

    public static JSONObject readJSONObject(File file, Charset charset) throws IORuntimeException {
        return parseObj(FileReader.create(file, charset).readString());
    }

    public static JSONArray readJSONArray(File file, Charset charset) throws IORuntimeException {
        return parseArray(FileReader.create(file, charset).readString());
    }

    public static String toJsonStr(JSON json, int indentFactor) {
        if (null == json) {
            return null;
        }
        return json.toJSONString(indentFactor);
    }

    public static String toJsonStr(JSON json) {
        if (null == json) {
            return null;
        }
        return json.toJSONString(0);
    }

    public static void toJsonStr(JSON json, Writer writer) {
        if (null != json) {
            json.write(writer);
        }
    }

    public static String toJsonPrettyStr(JSON json) {
        if (null == json) {
            return null;
        }
        return json.toJSONString(4);
    }

    public static String toJsonStr(Object obj) {
        return toJsonStr(obj, (JSONConfig) null);
    }

    public static String toJsonStr(Object obj, JSONConfig jsonConfig) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return StrUtil.str((CharSequence) obj);
        }
        return toJsonStr(parse(obj, jsonConfig));
    }

    public static void toJsonStr(Object obj, Writer writer) {
        if (null != obj) {
            toJsonStr(parse(obj), writer);
        }
    }

    public static String toJsonPrettyStr(Object obj) {
        return toJsonPrettyStr(parse(obj));
    }

    public static String toXmlStr(JSON json) {
        return XML.toXml(json);
    }

    public static <T> T toBean(String str, Class<T> cls) {
        return (T) toBean(parseObj(str), cls);
    }

    public static <T> T toBean(String str, JSONConfig jSONConfig, Class<T> cls) {
        return (T) toBean(parseObj(str, jSONConfig), cls);
    }

    public static <T> T toBean(JSONObject jSONObject, Class<T> cls) {
        if (null == jSONObject) {
            return null;
        }
        return (T) jSONObject.toBean((Class) cls);
    }

    public static <T> T toBean(String str, TypeReference<T> typeReference, boolean z) {
        return (T) toBean(str, typeReference.getType(), z);
    }

    public static <T> T toBean(String str, Type type, boolean z) {
        JSON parse = parse(str, JSONConfig.create().setIgnoreError(z));
        if (null == parse) {
            return null;
        }
        return (T) parse.toBean(type);
    }

    public static <T> T toBean(JSON json, TypeReference<T> typeReference, boolean z) {
        return (T) toBean(json, typeReference.getType(), z);
    }

    public static <T> T toBean(JSON json, Type type, boolean z) {
        if (null == json) {
            return null;
        }
        return (T) json.toBean(type, z);
    }

    public static <T> List<T> toList(String jsonArray, Class<T> elementType) {
        return toList(parseArray(jsonArray), elementType);
    }

    public static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
        if (null == jsonArray) {
            return null;
        }
        return jsonArray.toList(elementType);
    }

    public static Object getByPath(JSON json, String expression) {
        return getByPath(json, expression, null);
    }

    public static <T> T getByPath(JSON json, String str, T t) {
        if (null == json || StrUtil.isBlank(str)) {
            return t;
        }
        if (null != t) {
            return (T) ObjectUtil.defaultIfNull(json.getByPath(str, t.getClass()), t);
        }
        return (T) json.getByPath(str);
    }

    public static void putByPath(JSON json, String expression, Object value) {
        json.putByPath(expression, value);
    }

    public static String quote(String string) {
        return quote(string, true);
    }

    public static String quote(String string, boolean isWrap) {
        StringWriter sw = new StringWriter();
        try {
            return quote(string, sw, isWrap).toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static Writer quote(String str, Writer writer) throws IOException {
        return quote(str, writer, true);
    }

    public static Writer quote(String str, Writer writer, boolean isWrap) throws IOException {
        if (StrUtil.isEmpty(str)) {
            if (isWrap) {
                writer.write("\"\"");
            }
            return writer;
        }
        int len = str.length();
        if (isWrap) {
            writer.write(34);
        }
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\"':
                case '\\':
                    writer.write(StrPool.BACKSLASH);
                    writer.write(c);
                    break;
                default:
                    writer.write(escape(c));
                    break;
            }
        }
        if (isWrap) {
            writer.write(34);
        }
        return writer;
    }

    public static String escape(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder builder = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            builder.append(escape(c));
        }
        return builder.toString();
    }

    public static Object wrap(Object object, JSONConfig jsonConfig) {
        if (object == null) {
            if (jsonConfig.isIgnoreNullValue()) {
                return null;
            }
            return JSONNull.NULL;
        }
        if ((object instanceof JSON) || ObjectUtil.isNull(object) || (object instanceof JSONString) || (object instanceof CharSequence) || (object instanceof Number) || ObjectUtil.isBasicType(object)) {
            if ((object instanceof Number) && null != jsonConfig.getDateFormat()) {
                return new NumberWithFormat((Number) object, jsonConfig.getDateFormat());
            }
            return object;
        }
        try {
            if (object instanceof SQLException) {
                return object.toString();
            }
            if ((object instanceof Iterable) || ArrayUtil.isArray(object)) {
                return new JSONArray(object, jsonConfig);
            }
            if ((object instanceof Map) || (object instanceof Map.Entry)) {
                return new JSONObject(object, jsonConfig);
            }
            if ((object instanceof Date) || (object instanceof Calendar) || (object instanceof TemporalAccessor)) {
                return object;
            }
            if (object instanceof Enum) {
                return object.toString();
            }
            if (ClassUtil.isJdkClass(object.getClass())) {
                return object.toString();
            }
            return new JSONObject(object, jsonConfig);
        } catch (Exception e) {
            return null;
        }
    }

    public static String formatJsonStr(String jsonStr) {
        return JSONStrFormatter.format(jsonStr);
    }

    @Deprecated
    public static boolean isJson(String str) {
        return isTypeJSON(str);
    }

    public static boolean isTypeJSON(String str) {
        return isTypeJSONObject(str) || isTypeJSONArray(str);
    }

    @Deprecated
    public static boolean isJsonObj(String str) {
        return isTypeJSONObject(str);
    }

    public static boolean isTypeJSONObject(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return StrUtil.isWrap((CharSequence) StrUtil.trim(str), '{', '}');
    }

    @Deprecated
    public static boolean isJsonArray(String str) {
        return isTypeJSONArray(str);
    }

    public static boolean isTypeJSONArray(String str) {
        if (StrUtil.isBlank(str)) {
            return false;
        }
        return StrUtil.isWrap((CharSequence) StrUtil.trim(str), '[', ']');
    }

    public static boolean isNull(Object obj) {
        return null == obj || (obj instanceof JSONNull);
    }

    public static JSONObject xmlToJson(String xml) {
        return XML.toJSONObject(xml);
    }

    public static void putSerializer(Type type, JSONArraySerializer<?> serializer) {
        GlobalSerializeMapping.put(type, serializer);
    }

    public static void putSerializer(Type type, JSONObjectSerializer<?> serializer) {
        GlobalSerializeMapping.put(type, serializer);
    }

    public static void putDeserializer(Type type, JSONDeserializer<?> deserializer) {
        GlobalSerializeMapping.put(type, deserializer);
    }

    private static String escape(char c) {
        switch (c) {
            case '\b':
                return "\\b";
            case '\t':
                return "\\t";
            case '\n':
                return "\\n";
            case 11:
            default:
                if (c < ' ' || ((c >= 128 && c <= 160) || ((c >= 8192 && c <= 8208) || ((c >= 8232 && c <= 8239) || (c >= 8294 && c <= 8303))))) {
                    return HexUtil.toUnicodeHex(c);
                }
                return Character.toString(c);
            case '\f':
                return "\\f";
            case '\r':
                return "\\r";
        }
    }
}
