package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/getter/GroupedTypeGetter.class */
public interface GroupedTypeGetter {
    String getStrByGroup(String str, String str2);

    Integer getIntByGroup(String str, String str2);

    Short getShortByGroup(String str, String str2);

    Boolean getBoolByGroup(String str, String str2);

    Long getLongByGroup(String str, String str2);

    Character getCharByGroup(String str, String str2);

    Double getDoubleByGroup(String str, String str2);

    Byte getByteByGroup(String str, String str2);

    BigDecimal getBigDecimalByGroup(String str, String str2);

    BigInteger getBigIntegerByGroup(String str, String str2);
}
