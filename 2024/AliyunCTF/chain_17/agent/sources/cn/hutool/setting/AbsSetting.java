package cn.hutool.setting;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.getter.OptNullBasicTypeFromStringGetter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.Serializable;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/AbsSetting.class */
public abstract class AbsSetting implements OptNullBasicTypeFromStringGetter<String>, Serializable {
    private static final long serialVersionUID = 6200156302595905863L;
    private static final Log log = LogFactory.get();
    public static final String DEFAULT_DELIMITER = ",";
    public static final String DEFAULT_GROUP = "";

    public abstract String getByGroup(String str, String str2);

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    public String getStr(String key, String defaultValue) {
        return getStr(key, "", defaultValue);
    }

    public String getStr(String key, String group, String defaultValue) {
        String value = getByGroup(key, group);
        return (String) ObjectUtil.defaultIfNull(value, defaultValue);
    }

    public String getStrNotEmpty(String key, String group, String defaultValue) {
        String value = getByGroup(key, group);
        return (String) ObjectUtil.defaultIfEmpty(value, defaultValue);
    }

    public String getWithLog(String key) {
        String value = getStr(key);
        if (value == null) {
            log.debug("No key define for [{}]!", key);
        }
        return value;
    }

    public String getByGroupWithLog(String key, String group) {
        String value = getByGroup(key, group);
        if (value == null) {
            log.debug("No key define for [{}] of group [{}] !", key, group);
        }
        return value;
    }

    public String[] getStrings(String key) {
        return getStrings(key, null);
    }

    public String[] getStringsWithDefault(String key, String[] defaultValue) {
        String[] value = getStrings(key, null);
        if (null == value) {
            value = defaultValue;
        }
        return value;
    }

    public String[] getStrings(String key, String group) {
        return getStrings(key, group, ",");
    }

    public String[] getStrings(String key, String group, String delimiter) {
        String value = getByGroup(key, group);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return StrUtil.splitToArray(value, delimiter);
    }

    public Integer getInt(String key, String group) {
        return getInt(key, group, null);
    }

    public Integer getInt(String key, String group, Integer defaultValue) {
        return Convert.toInt(getByGroup(key, group), defaultValue);
    }

    public Boolean getBool(String key, String group) {
        return getBool(key, group, null);
    }

    public Boolean getBool(String key, String group, Boolean defaultValue) {
        return Convert.toBool(getByGroup(key, group), defaultValue);
    }

    public Long getLong(String key, String group) {
        return getLong(key, group, null);
    }

    public Long getLong(String key, String group, Long defaultValue) {
        return Convert.toLong(getByGroup(key, group), defaultValue);
    }

    public Character getChar(String key, String group) {
        String value = getByGroup(key, group);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return Character.valueOf(value.charAt(0));
    }

    public Double getDouble(String key, String group) {
        return getDouble(key, group, null);
    }

    public Double getDouble(String key, String group, Double defaultValue) {
        return Convert.toDouble(getByGroup(key, group), defaultValue);
    }

    public <T> T toBean(final String str, T t) {
        return (T) BeanUtil.fillBean(t, new ValueProvider<String>() { // from class: cn.hutool.setting.AbsSetting.1
            @Override // cn.hutool.core.bean.copier.ValueProvider
            public Object value(String key, Type valueType) {
                return AbsSetting.this.getByGroup(key, str);
            }

            @Override // cn.hutool.core.bean.copier.ValueProvider
            public boolean containsKey(String key) {
                return null != AbsSetting.this.getByGroup(key, str);
            }
        }, CopyOptions.create());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T toBean(String str, Class<T> cls) {
        return (T) toBean(str, (String) ReflectUtil.newInstanceIfPossible(cls));
    }

    public <T> T toBean(T t) {
        return (T) toBean((String) null, (String) t);
    }

    public <T> T toBean(Class<T> cls) {
        return (T) toBean((String) null, (Class) cls);
    }
}
