package cn.hutool.json;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.convert.Converter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.convert.impl.ArrayConverter;
import cn.hutool.core.convert.impl.BeanConverter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.serialize.GlobalSerializeMapping;
import cn.hutool.json.serialize.JSONDeserializer;
import java.lang.reflect.Type;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONConverter.class */
public class JSONConverter implements Converter<JSON> {
    static {
        ConverterRegistry registry = ConverterRegistry.getInstance();
        registry.putCustom(JSON.class, JSONConverter.class);
        registry.putCustom(JSONObject.class, JSONConverter.class);
        registry.putCustom(JSONArray.class, JSONConverter.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Object toArray(JSONArray jsonArray, Class<?> arrayClass) {
        return new ArrayConverter(arrayClass).convert(jsonArray, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
        return Convert.toList(elementType, jsonArray);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v18, types: [T, cn.hutool.json.JSONBeanParser] */
    public static <T> T jsonConvert(Type type, Object obj, JSONConfig jSONConfig) throws ConvertException {
        if (JSONUtil.isNull(obj)) {
            return null;
        }
        if (type instanceof Class) {
            Class cls = (Class) type;
            if (JSONBeanParser.class.isAssignableFrom(cls)) {
                ?? r0 = (T) ((JSONBeanParser) ReflectUtil.newInstanceIfPossible(cls));
                if (0 == r0) {
                    throw new ConvertException("Can not instance [{}]", type);
                }
                r0.parse(obj);
                return r0;
            }
            if (type == byte[].class && (obj instanceof CharSequence)) {
                return (T) Base64.decode((CharSequence) obj);
            }
        }
        return (T) jsonToBean(type, obj, jSONConfig.isIgnoreError());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static <T> T jsonToBean(Type type, Object obj, boolean z) throws ConvertException {
        if (JSONUtil.isNull(obj)) {
            return null;
        }
        if (obj instanceof JSON) {
            JSONDeserializer<?> deserializer = GlobalSerializeMapping.getDeserializer(type);
            if (null != deserializer) {
                return (T) deserializer.deserialize((JSON) obj);
            }
            if ((obj instanceof JSONGetter) && (type instanceof Class) && BeanUtil.hasSetter((Class) type)) {
                return new BeanConverter(type, InternalJSONUtil.toCopyOptions(((JSONGetter) obj).getConfig()).setIgnoreError(z)).convertWithCheck(obj, null, z);
            }
        }
        T t = (T) Convert.convertWithCheck(type, obj, null, z);
        if (null == t && false == z) {
            if (StrUtil.isBlankIfStr(obj)) {
                return null;
            }
            throw new ConvertException("Can not convert {} to type {}", obj, ObjectUtil.defaultIfNull(TypeUtil.getClass(type), type));
        }
        return t;
    }

    @Override // cn.hutool.core.convert.Converter
    public JSON convert(Object value, JSON defaultValue) throws IllegalArgumentException {
        return JSONUtil.parse(value);
    }
}
