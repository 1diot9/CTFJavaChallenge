package com.fasterxml.jackson.core.io.doubleparser;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.math.BigInteger;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.h2.util.DateTimeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FastIntegerMath.class */
class FastIntegerMath {
    public static final BigInteger FIVE = BigInteger.valueOf(5);
    static final BigInteger TEN_POW_16 = BigInteger.valueOf(10000000000000000L);
    static final BigInteger FIVE_POW_16 = BigInteger.valueOf(152587890625L);
    private static final BigInteger[] SMALL_POWERS_OF_TEN = {BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(100), BigInteger.valueOf(1000), BigInteger.valueOf(AbstractComponentTracker.LINGERING_TIMEOUT), BigInteger.valueOf(100000), BigInteger.valueOf(1000000), BigInteger.valueOf(10000000), BigInteger.valueOf(100000000), BigInteger.valueOf(DateTimeUtils.NANOS_PER_SECOND), BigInteger.valueOf(10000000000L), BigInteger.valueOf(100000000000L), BigInteger.valueOf(1000000000000L), BigInteger.valueOf(10000000000000L), BigInteger.valueOf(100000000000000L), BigInteger.valueOf(1000000000000000L)};

    private FastIntegerMath() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger computePowerOfTen(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
        if (n < SMALL_POWERS_OF_TEN.length) {
            return SMALL_POWERS_OF_TEN[n];
        }
        if (powersOfTen != null) {
            Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(Integer.valueOf(n));
            Integer floorN = floorEntry.getKey();
            if (floorN.intValue() == n) {
                return floorEntry.getValue();
            }
            return FftMultiplier.multiply(floorEntry.getValue(), computePowerOfTen(powersOfTen, n - floorN.intValue()));
        }
        return FIVE.pow(n).shiftLeft(n);
    }

    static BigInteger computeTenRaisedByNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
        int n2 = n & (-16);
        Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(Integer.valueOf(n2));
        int floorPower = floorEntry.getKey().intValue();
        BigInteger floorValue = floorEntry.getValue();
        if (floorPower == n2) {
            return floorValue;
        }
        int diff = n2 - floorPower;
        BigInteger diffValue = (BigInteger) powersOfTen.get(Integer.valueOf(diff));
        if (diffValue == null) {
            diffValue = computeTenRaisedByNFloor16Recursive(powersOfTen, diff);
            powersOfTen.put(Integer.valueOf(diff), diffValue);
        }
        return FftMultiplier.multiply(floorValue, diffValue);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NavigableMap<Integer, BigInteger> createPowersOfTenFloor16Map() {
        NavigableMap<Integer, BigInteger> powersOfTen = new TreeMap<>();
        powersOfTen.put(0, BigInteger.ONE);
        powersOfTen.put(16, TEN_POW_16);
        return powersOfTen;
    }

    public static long estimateNumBits(long numDecimalDigits) {
        return ((numDecimalDigits * 3402) >>> 10) + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NavigableMap<Integer, BigInteger> fillPowersOf10Floor16(int from, int to) {
        NavigableMap<Integer, BigInteger> powers = new TreeMap<>();
        powers.put(0, BigInteger.valueOf(5L));
        powers.put(16, FIVE_POW_16);
        fillPowersOfNFloor16Recursive(powers, from, to);
        for (Map.Entry<Integer, BigInteger> e : powers.entrySet()) {
            e.setValue(e.getValue().shiftLeft(e.getKey().intValue()));
        }
        return powers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void fillPowersOfNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int from, int to) {
        int numDigits = to - from;
        if (numDigits <= 18) {
            return;
        }
        int mid = splitFloor16(from, to);
        int n = to - mid;
        if (!powersOfTen.containsKey(Integer.valueOf(n))) {
            fillPowersOfNFloor16Recursive(powersOfTen, from, mid);
            fillPowersOfNFloor16Recursive(powersOfTen, mid, to);
            powersOfTen.put(Integer.valueOf(n), computeTenRaisedByNFloor16Recursive(powersOfTen, n));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UInt128 fullMultiplication(long x, long y) {
        long x0 = x & 4294967295L;
        long x1 = x >>> 32;
        long y0 = y & 4294967295L;
        long y1 = y >>> 32;
        long p11 = x1 * y1;
        long p01 = x0 * y1;
        long p10 = x1 * y0;
        long p00 = x0 * y0;
        long middle = p10 + (p00 >>> 32) + (p01 & 4294967295L);
        return new UInt128(p11 + (middle >>> 32) + (p01 >>> 32), (middle << 32) | (p00 & 4294967295L));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int splitFloor16(int from, int to) {
        int mid = (from + to) >>> 1;
        return to - ((((to - mid) + 15) >> 4) << 4);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FastIntegerMath$UInt128.class */
    static class UInt128 {
        final long high;
        final long low;

        private UInt128(long high, long low) {
            this.high = high;
            this.low = low;
        }
    }
}
