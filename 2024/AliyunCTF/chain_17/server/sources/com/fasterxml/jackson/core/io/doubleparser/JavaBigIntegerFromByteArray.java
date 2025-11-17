package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaBigIntegerFromByteArray.class */
class JavaBigIntegerFromByteArray extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 1292782622;
    private static final int MAX_DECIMAL_DIGITS = 646456993;
    private static final int MAX_HEX_DIGITS = 536870912;

    public BigInteger parseBigIntegerLiteral(byte[] str, int offset, int length, int radix) throws NumberFormatException {
        try {
            int endIndex = offset + length;
            if (offset < 0 || endIndex < offset || endIndex > str.length || length > 1292782622) {
                throw new IllegalArgumentException(AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
            }
            int index = offset;
            byte ch2 = str[index];
            boolean isNegative = ch2 == 45;
            if (isNegative || ch2 == 43) {
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
                    return new BigInteger(new String(str, offset, length, StandardCharsets.ISO_8859_1), radix);
            }
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    private BigInteger parseDecDigits(byte[] str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (numDigits > 18) {
            return parseManyDecDigits(str, from, to, isNegative);
        }
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0;
        for (int from2 = preroll; from2 < to; from2 += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigitsUtf8(str, from2);
            success &= addend >= 0;
            significand = (significand * 100000000) + addend;
        }
        if (success) {
            return BigInteger.valueOf(isNegative ? -significand : significand);
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }

    private BigInteger parseHexDigits(byte[] str, int from, int to, boolean isNegative) {
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
            byte chLow = str[from2];
            int valueLow = chLow < 0 ? (byte) -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            index = 1 + 1;
            bytes[1] = (byte) valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from2 + ((to - from2) & 7);
        while (from2 < prerollLimit) {
            byte chHigh = str[from2];
            byte chLow2 = str[from2 + 1];
            int valueHigh = chHigh < 0 ? (byte) -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chHigh];
            int valueLow2 = chLow2 < 0 ? (byte) -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow2];
            int i = index;
            index++;
            bytes[i] = (byte) ((valueHigh << 4) | valueLow2);
            illegalDigits |= valueHigh < 0 || valueLow2 < 0;
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

    private BigInteger parseManyDecDigits(byte[] str, int from, int to, boolean isNegative) {
        int from2 = skipZeroes(str, from, to);
        int numDigits = to - from2;
        if (numDigits > MAX_DECIMAL_DIGITS) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
        Map<Integer, BigInteger> powersOfTen = FastIntegerMath.fillPowersOf10Floor16(from2, to);
        BigInteger result = ParseDigitsTaskByteArray.parseDigitsRecursive(str, from2, to, powersOfTen);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(byte[] str, int from, int to) {
        while (from < to - 8 && FastDoubleSwar.isEightZeroes(str, from)) {
            from += 8;
        }
        while (from < to && str[from] == 48) {
            from++;
        }
        return from;
    }
}
