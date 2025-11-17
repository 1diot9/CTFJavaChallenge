package cn.hutool.core.getter;

import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/getter/OptNullBasicTypeFromObjectGetter.class */
public interface OptNullBasicTypeFromObjectGetter<K> extends OptNullBasicTypeGetter<K> {
    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default String getStr(K key, String defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toStr(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Integer getInt(K key, Integer defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toInt(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Short getShort(K key, Short defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toShort(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Boolean getBool(K key, Boolean defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toBool(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Long getLong(K key, Long defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toLong(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Character getChar(K key, Character defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toChar(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Float getFloat(K key, Float defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toFloat(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Double getDouble(K key, Double defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toDouble(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Byte getByte(K key, Byte defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toByte(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toBigDecimal(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default BigInteger getBigInteger(K key, BigInteger defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toBigInteger(obj, defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default <E extends Enum<E>> E getEnum(Class<E> cls, K k, E e) {
        Object obj = getObj(k);
        if (null == obj) {
            return e;
        }
        return (E) Convert.toEnum(cls, obj, e);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Date getDate(K key, Date defaultValue) {
        Object obj = getObj(key);
        if (null == obj) {
            return defaultValue;
        }
        return Convert.toDate(obj, defaultValue);
    }
}
