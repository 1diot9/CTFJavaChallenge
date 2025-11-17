package cn.hutool.core.util;

import ch.qos.logback.core.net.ssl.SSL;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.WeightRandom;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/RandomUtil.class */
public class RandomUtil {
    public static final String BASE_NUMBER = "0123456789";
    public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz";
    public static final String BASE_CHAR_NUMBER = "abcdefghijklmnopqrstuvwxyz0123456789";

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }

    public static SecureRandom createSecureRandom(byte[] seed) {
        return null == seed ? new SecureRandom() : new SecureRandom(seed);
    }

    public static SecureRandom getSecureRandom() {
        return getSecureRandom(null);
    }

    public static SecureRandom getSecureRandom(byte[] seed) {
        return createSecureRandom(seed);
    }

    public static SecureRandom getSHA1PRNGRandom(byte[] seed) {
        try {
            SecureRandom random = SecureRandom.getInstance(SSL.DEFAULT_SECURE_RANDOM_ALGORITHM);
            if (null != seed) {
                random.setSeed(seed);
            }
            return random;
        } catch (NoSuchAlgorithmException e) {
            throw new UtilException(e);
        }
    }

    public static SecureRandom getSecureRandomStrong() {
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new UtilException(e);
        }
    }

    public static Random getRandom(boolean isSecure) {
        return isSecure ? getSecureRandom() : getRandom();
    }

    public static boolean randomBoolean() {
        return 0 == randomInt(2);
    }

    public static char randomChinese() {
        return (char) randomInt(19968, 40959);
    }

    public static int randomInt(int min, int max) {
        return getRandom().nextInt(min, max);
    }

    public static int randomInt() {
        return getRandom().nextInt();
    }

    public static int randomInt(int limit) {
        return getRandom().nextInt(limit);
    }

    public static long randomLong(long min, long max) {
        return getRandom().nextLong(min, max);
    }

    public static long randomLong() {
        return getRandom().nextLong();
    }

    public static long randomLong(long limit) {
        return getRandom().nextLong(limit);
    }

    public static double randomDouble(double min, double max) {
        return getRandom().nextDouble(min, max);
    }

    public static double randomDouble(double min, double max, int scale, RoundingMode roundingMode) {
        return NumberUtil.round(randomDouble(min, max), scale, roundingMode).doubleValue();
    }

    public static double randomDouble() {
        return getRandom().nextDouble();
    }

    public static double randomDouble(int scale, RoundingMode roundingMode) {
        return NumberUtil.round(randomDouble(), scale, roundingMode).doubleValue();
    }

    public static double randomDouble(double limit) {
        return getRandom().nextDouble(limit);
    }

    public static double randomDouble(double limit, int scale, RoundingMode roundingMode) {
        return NumberUtil.round(randomDouble(limit), scale, roundingMode).doubleValue();
    }

    public static BigDecimal randomBigDecimal() {
        return NumberUtil.toBigDecimal(Double.valueOf(getRandom().nextDouble()));
    }

    public static BigDecimal randomBigDecimal(BigDecimal limit) {
        return NumberUtil.toBigDecimal(Double.valueOf(getRandom().nextDouble(limit.doubleValue())));
    }

    public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max) {
        return NumberUtil.toBigDecimal(Double.valueOf(getRandom().nextDouble(min.doubleValue(), max.doubleValue())));
    }

    public static byte[] randomBytes(int length) {
        byte[] bytes = new byte[length];
        getRandom().nextBytes(bytes);
        return bytes;
    }

    public static <T> T randomEle(List<T> list) {
        return (T) randomEle(list, list.size());
    }

    public static <T> T randomEle(List<T> list, int limit) {
        if (list.size() < limit) {
            limit = list.size();
        }
        return list.get(randomInt(limit));
    }

    public static <T> T randomEle(T[] tArr) {
        return (T) randomEle(tArr, tArr.length);
    }

    public static <T> T randomEle(T[] array, int limit) {
        if (array.length < limit) {
            limit = array.length;
        }
        return array[randomInt(limit)];
    }

    public static <T> List<T> randomEles(List<T> list, int count) {
        ArrayList arrayList = new ArrayList(count);
        int limit = list.size();
        while (arrayList.size() < count) {
            arrayList.add(randomEle(list, limit));
        }
        return arrayList;
    }

    public static <T> List<T> randomEleList(List<T> source, int count) {
        if (count >= source.size()) {
            return ListUtil.toList((Collection) source);
        }
        int[] randomList = ArrayUtil.sub(randomInts(source.size()), 0, count);
        List<T> result = new ArrayList<>();
        for (int e : randomList) {
            result.add(source.get(e));
        }
        return result;
    }

    public static <T> Set<T> randomEleSet(Collection<T> collection, int count) {
        ArrayList<T> source = CollUtil.distinct(collection);
        if (count > source.size()) {
            throw new IllegalArgumentException("Count is larger than collection distinct size !");
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(count);
        int limit = source.size();
        while (linkedHashSet.size() < count) {
            linkedHashSet.add(randomEle(source, limit));
        }
        return linkedHashSet;
    }

    public static int[] randomInts(int length) {
        int[] range = ArrayUtil.range(length);
        for (int i = 0; i < length; i++) {
            int random = randomInt(i, length);
            ArrayUtil.swap(range, i, random);
        }
        return range;
    }

    public static String randomString(int length) {
        return randomString(BASE_CHAR_NUMBER, length);
    }

    public static String randomStringUpper(int length) {
        return randomString(BASE_CHAR_NUMBER, length).toUpperCase();
    }

    public static String randomStringWithoutStr(int length, String elemData) {
        String baseStr = StrUtil.removeAll(BASE_CHAR_NUMBER, elemData.toLowerCase().toCharArray());
        return randomString(baseStr, length);
    }

    public static String randomNumbers(int length) {
        return randomString(BASE_NUMBER, length);
    }

    public static String randomString(String baseString, int length) {
        if (StrUtil.isEmpty(baseString)) {
            return "";
        }
        if (length < 1) {
            length = 1;
        }
        StringBuilder sb = new StringBuilder(length);
        int baseLength = baseString.length();
        for (int i = 0; i < length; i++) {
            int number = randomInt(baseLength);
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }

    public static char randomNumber() {
        return randomChar(BASE_NUMBER);
    }

    public static char randomChar() {
        return randomChar(BASE_CHAR_NUMBER);
    }

    public static char randomChar(String baseString) {
        return baseString.charAt(randomInt(baseString.length()));
    }

    @Deprecated
    public static Color randomColor() {
        Random random = getRandom();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static <T> WeightRandom<T> weightRandom(WeightRandom.WeightObj<T>[] weightObjs) {
        return new WeightRandom<>(weightObjs);
    }

    public static <T> WeightRandom<T> weightRandom(Iterable<WeightRandom.WeightObj<T>> weightObjs) {
        return new WeightRandom<>(weightObjs);
    }

    public static DateTime randomDay(int min, int max) {
        return randomDate(DateUtil.date(), DateField.DAY_OF_YEAR, min, max);
    }

    public static DateTime randomDate(Date baseDate, DateField dateField, int min, int max) {
        if (null == baseDate) {
            baseDate = DateUtil.date();
        }
        return DateUtil.offset(baseDate, dateField, randomInt(min, max));
    }
}
