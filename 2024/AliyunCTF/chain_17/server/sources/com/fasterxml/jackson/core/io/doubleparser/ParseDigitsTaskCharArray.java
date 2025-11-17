package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/ParseDigitsTaskCharArray.class */
public class ParseDigitsTaskCharArray {
    static final int RECURSION_THRESHOLD = 400;

    private ParseDigitsTaskCharArray() {
    }

    static BigInteger parseDigitsIterative(char[] str, int from, int to) {
        int numDigits = to - from;
        BigSignificand bigSignificand = new BigSignificand(FastIntegerMath.estimateNumBits(numDigits));
        int preroll = from + (numDigits & 7);
        int value = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = value >= 0;
        bigSignificand.add(value);
        for (int from2 = preroll; from2 < to; from2 += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from2);
            success &= addend >= 0;
            bigSignificand.fma(100000000, addend);
        }
        if (!success) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        return bigSignificand.toBigInteger();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger parseDigitsRecursive(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        int numDigits = to - from;
        if (numDigits <= 400) {
            return parseDigitsIterative(str, from, to);
        }
        int mid = FastIntegerMath.splitFloor16(from, to);
        BigInteger high = parseDigitsRecursive(str, from, mid, powersOfTen);
        BigInteger low = parseDigitsRecursive(str, mid, to, powersOfTen);
        return low.add(FftMultiplier.multiply(high, powersOfTen.get(Integer.valueOf(to - mid))));
    }
}
