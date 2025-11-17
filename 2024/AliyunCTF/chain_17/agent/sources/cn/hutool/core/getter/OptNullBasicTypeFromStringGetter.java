package cn.hutool.core.getter;

import cn.hutool.core.convert.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/getter/OptNullBasicTypeFromStringGetter.class */
public interface OptNullBasicTypeFromStringGetter<K> extends OptNullBasicTypeGetter<K> {
    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Object getObj(K key, Object defaultValue) {
        return getStr(key, null == defaultValue ? null : defaultValue.toString());
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Integer getInt(K key, Integer defaultValue) {
        return Convert.toInt(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Short getShort(K key, Short defaultValue) {
        return Convert.toShort(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Boolean getBool(K key, Boolean defaultValue) {
        return Convert.toBool(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Long getLong(K key, Long defaultValue) {
        return Convert.toLong(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Character getChar(K key, Character defaultValue) {
        return Convert.toChar(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Float getFloat(K key, Float defaultValue) {
        return Convert.toFloat(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Double getDouble(K key, Double defaultValue) {
        return Convert.toDouble(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Byte getByte(K key, Byte defaultValue) {
        return Convert.toByte(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
        return Convert.toBigDecimal(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default BigInteger getBigInteger(K key, BigInteger defaultValue) {
        return Convert.toBigInteger(getStr(key), defaultValue);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default <E extends Enum<E>> E getEnum(Class<E> cls, K k, E e) {
        return (E) Convert.toEnum(cls, getStr(k), e);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    default Date getDate(K key, Date defaultValue) {
        return Convert.toDate(getStr(key), defaultValue);
    }
}
