package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaBigIntegerFromCharSequence.class */
class JavaBigIntegerFromCharSequence extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 1292782622;
    private static final int MAX_DECIMAL_DIGITS = 646456993;
    private static final int MAX_HEX_DIGITS = 536870912;

    public BigInteger parseBigIntegerLiteral(CharSequence str, int offset, int length, int radix) throws NumberFormatException {
        try {
            int endIndex = offset + length;
            if (offset < 0 || endIndex < offset || endIndex > str.length() || length > 1292782622) {
                throw new IllegalArgumentException(AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
            }
            int index = offset;
            char ch2 = str.charAt(index);
            boolean isNegative = ch2 == '-';
            if (isNegative || ch2 == '+') {
                index++;
                if (charAt(str, index, endIndex) == 0) {
                    throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
                }
            }
            switch (radix) {
                case 10:
                    return parseDecDigits(str, index, endIndex, isNegative);
                case 16:
                    return parseHexDigits(str, index, endIndex, isNegative);
                default:
                    return new BigInteger(str.subSequence(offset, length).toString(), radix);
            }
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    private BigInteger parseDecDigits(CharSequence str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (numDigits > 18) {
            return parseManyDecDigits(str, from, to, isNegative);
        }
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0;
        for (int from2 = preroll; from2 < to; from2 += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from2);
            success &= addend >= 0;
            significand = (significand * 100000000) + addend;
        }
        if (success) {
            return BigInteger.valueOf(isNegative ? -significand : significand);
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }

    private BigInteger parseHexDigits(CharSequence str, int from, int to, boolean isNegative) {
        int from2 = skipZeroes(str, from, to);
        int numDigits = to - from2;
        if (numDigits <= 0) {
            return BigInteger.ZERO;
        }
        if (numDigits > 536870912) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
        byte[] bytes = new byte[((numDigits + 1) >> 1) + 1];
        int index = 1;
        boolean illegalDigits = false;
        if ((numDigits & 1) != 0) {
            from2++;
            char chLow = str.charAt(from2);
            int valueLow = lookupHex(chLow);
            index = 1 + 1;
            bytes[1] = (byte) valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from2 + ((to - from2) & 7);
        while (from2 < prerollLimit) {
            char chHigh = str.charAt(from2);
            char chLow2 = str.charAt(from2 + 1);
            int valueHigh = lookupHex(chHigh);
            int valueLow2 = lookupHex(chLow2);
            int i = index;
            index++;
            bytes[i] = (byte) ((valueHigh << 4) | valueLow2);
            illegalDigits |= valueLow2 < 0 || valueHigh < 0;
            from2 += 2;
        }
        while (from2 < to) {
            long value = FastDoubleSwar.tryToParseEightHexDigits(str, from2);
            FastDoubleSwar.writeIntBE(bytes, index, (int) value);
            illegalDigits |= value < 0;
            from2 += 8;
            index += 4;
        }
        if (illegalDigits) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        BigInteger result = new BigInteger(bytes);
        return isNegative ? result.negate() : result;
    }

    private BigInteger parseManyDecDigits(CharSequence str, int from, int to, boolean isNegative) {
        int from2 = skipZeroes(str, from, to);
        int numDigits = to - from2;
        if (numDigits > MAX_DECIMAL_DIGITS) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
        Map<Integer, BigInteger> powersOfTen = FastIntegerMath.fillPowersOf10Floor16(from2, to);
        BigInteger result = ParseDigitsTaskCharSequence.parseDigitsRecursive(str, from2, to, powersOfTen);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(CharSequence str, int from, int to) {
        while (from < to && str.charAt(from) == '0') {
            from++;
        }
        return from;
    }
}
