package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/getter/OptBasicTypeGetter.class */
public interface OptBasicTypeGetter<K> {
    Object getObj(K k, Object obj);

    String getStr(K k, String str);

    Integer getInt(K k, Integer num);

    Short getShort(K k, Short sh);

    Boolean getBool(K k, Boolean bool);

    Long getLong(K k, Long l);

    Character getChar(K k, Character ch2);

    Float getFloat(K k, Float f);

    Double getDouble(K k, Double d);

    Byte getByte(K k, Byte b);

    BigDecimal getBigDecimal(K k, BigDecimal bigDecimal);

    BigInteger getBigInteger(K k, BigInteger bigInteger);

    <E extends Enum<E>> E getEnum(Class<E> cls, K k, E e);

    Date getDate(K k, Date date);
}
