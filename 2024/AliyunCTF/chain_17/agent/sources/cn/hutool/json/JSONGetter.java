package cn.hutool.json;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter;
import cn.hutool.core.util.StrUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONGetter.class */
public interface JSONGetter<K> extends OptNullBasicTypeFromObjectGetter<K> {
    JSONConfig getConfig();

    default boolean isNull(K key) {
        return JSONUtil.isNull(getObj(key));
    }

    default String getStrEscaped(K key) {
        return getStrEscaped(key, null);
    }

    default String getStrEscaped(K key, String defaultValue) {
        return JSONUtil.escape(getStr(key, defaultValue));
    }

    default JSONArray getJSONArray(K key) {
        Object object = getObj(key);
        if (JSONUtil.isNull(object)) {
            return null;
        }
        if (object instanceof JSON) {
            return (JSONArray) object;
        }
        return new JSONArray(object, getConfig());
    }

    default JSONObject getJSONObject(K key) {
        Object object = getObj(key);
        if (JSONUtil.isNull(object)) {
            return null;
        }
        if (object instanceof JSON) {
            return (JSONObject) object;
        }
        return new JSONObject(object, getConfig());
    }

    default <T> T getBean(K k, Class<T> cls) {
        JSONObject jSONObject = getJSONObject(k);
        if (null == jSONObject) {
            return null;
        }
        return (T) jSONObject.toBean((Class) cls);
    }

    default <T> List<T> getBeanList(K key, Class<T> beanType) {
        JSONArray jsonArray = getJSONArray(key);
        if (null == jsonArray) {
            return null;
        }
        return jsonArray.toList(beanType);
    }

    @Override // cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter, cn.hutool.core.getter.OptBasicTypeGetter
    default Date getDate(K key, Date defaultValue) {
        Object obj = getObj(key);
        if (JSONUtil.isNull(obj)) {
            return defaultValue;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        if (obj instanceof NumberWithFormat) {
            return (Date) ((NumberWithFormat) obj).convert(Date.class, obj);
        }
        Optional<String> formatOps = Optional.ofNullable(getConfig()).map((v0) -> {
            return v0.getDateFormat();
        });
        if (formatOps.isPresent()) {
            String format = formatOps.get();
            if (StrUtil.isNotBlank(format)) {
                String str = Convert.toStr(obj);
                if (null == str) {
                    return defaultValue;
                }
                return DateUtil.parse(str, format);
            }
        }
        return Convert.toDate(obj, defaultValue);
    }

    default LocalDateTime getLocalDateTime(K key, LocalDateTime defaultValue) {
        Object obj = getObj(key);
        if (JSONUtil.isNull(obj)) {
            return defaultValue;
        }
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        Optional<String> formatOps = Optional.ofNullable(getConfig()).map((v0) -> {
            return v0.getDateFormat();
        });
        if (formatOps.isPresent()) {
            String format = formatOps.get();
            if (StrUtil.isNotBlank(format)) {
                String str = Convert.toStr(obj);
                if (null == str) {
                    return defaultValue;
                }
                return LocalDateTimeUtil.parse(str, format);
            }
        }
        return Convert.toLocalDateTime(obj, defaultValue);
    }

    default byte[] getBytes(K key) {
        return (byte[]) get(key, byte[].class);
    }

    default <T> T get(K k, Class<T> cls) throws ConvertException {
        return (T) get(k, cls, false);
    }

    default <T> T get(K k, Class<T> cls, boolean z) throws ConvertException {
        Object obj = getObj(k);
        if (JSONUtil.isNull(obj)) {
            return null;
        }
        return (T) JSONConverter.jsonConvert(cls, obj, JSONConfig.create().setIgnoreError(z));
    }
}
