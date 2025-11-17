package cn.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/getter/OptArrayTypeGetter.class */
public interface OptArrayTypeGetter {
    Object[] getObjs(String str, Object[] objArr);

    String[] getStrs(String str, String[] strArr);

    Integer[] getInts(String str, Integer[] numArr);

    Short[] getShorts(String str, Short[] shArr);

    Boolean[] getBools(String str, Boolean[] boolArr);

    Long[] getLongs(String str, Long[] lArr);

    Character[] getChars(String str, Character[] chArr);

    Double[] getDoubles(String str, Double[] dArr);

    Byte[] getBytes(String str, Byte[] bArr);

    BigInteger[] getBigIntegers(String str, BigInteger[] bigIntegerArr);

    BigDecimal[] getBigDecimals(String str, BigDecimal[] bigDecimalArr);
}
