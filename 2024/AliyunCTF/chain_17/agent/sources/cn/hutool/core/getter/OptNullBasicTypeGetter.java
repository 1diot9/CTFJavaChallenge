package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/getter/OptNullBasicTypeGetter.class */
public interface OptNullBasicTypeGetter<K> extends BasicTypeGetter<K>, OptBasicTypeGetter<K> {
    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Object getObj(K key) {
        return getObj(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default String getStr(K key) {
        return getStr(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Integer getInt(K key) {
        return getInt(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Short getShort(K key) {
        return getShort(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Boolean getBool(K key) {
        return getBool(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Long getLong(K key) {
        return getLong(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Character getChar(K key) {
        return getChar(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Float getFloat(K key) {
        return getFloat(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Double getDouble(K key) {
        return getDouble(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Byte getByte(K key) {
        return getByte(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default BigDecimal getBigDecimal(K key) {
        return getBigDecimal(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default BigInteger getBigInteger(K key) {
        return getBigInteger(key, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default <E extends Enum<E>> E getEnum(Class<E> cls, K k) {
        return (E) getEnum(cls, k, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    default Date getDate(K key) {
        return getDate(key, null);
    }
}
