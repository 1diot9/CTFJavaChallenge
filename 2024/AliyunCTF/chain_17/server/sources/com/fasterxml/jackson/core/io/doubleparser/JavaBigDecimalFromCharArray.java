package com.fasterxml.jackson.core.io.doubleparser;

import ch.qos.logback.core.spi.AbstractComponentTracker;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NavigableMap;
import org.apache.tomcat.jni.SSL;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaBigDecimalFromCharArray.class */
final class JavaBigDecimalFromCharArray extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 1292782635;
    private static final int MANY_DIGITS_THRESHOLD = 32;
    private static final int MAX_DIGIT_COUNT = 1292782621;
    private static final long MAX_EXPONENT_NUMBER = 2147483647L;

    public BigDecimal parseBigDecimalString(char[] str, int offset, int length) {
        int digitCount;
        long exponent;
        int exponentIndicatorIndex;
        int digits;
        try {
            if (length >= 32) {
                return parseBigDecimalStringWithManyDigits(str, offset, length);
            }
            long significand = 0;
            int decimalPointIndex = -1;
            int endIndex = offset + length;
            int index = offset;
            char ch2 = charAt(str, index, endIndex);
            boolean illegal = false;
            boolean isNegative = ch2 == '-';
            if (isNegative || ch2 == '+') {
                index++;
                ch2 = charAt(str, index, endIndex);
                if (ch2 == 0) {
                    throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
                }
            }
            int integerPartIndex = index;
            while (index < endIndex) {
                ch2 = str[index];
                if (FastDoubleSwar.isDigit(ch2)) {
                    significand = ((10 * significand) + ch2) - 48;
                } else {
                    if (ch2 != '.') {
                        break;
                    }
                    illegal |= decimalPointIndex >= 0;
                    decimalPointIndex = index;
                    while (index < endIndex - 4 && (digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1)) >= 0) {
                        significand = (AbstractComponentTracker.LINGERING_TIMEOUT * significand) + digits;
                        index += 4;
                    }
                }
                index++;
            }
            int significandEndIndex = index;
            if (decimalPointIndex < 0) {
                digitCount = significandEndIndex - integerPartIndex;
                decimalPointIndex = significandEndIndex;
                exponent = 0;
            } else {
                digitCount = (significandEndIndex - integerPartIndex) - 1;
                exponent = (decimalPointIndex - significandEndIndex) + 1;
            }
            long expNumber = 0;
            if ((ch2 | ' ') == 101) {
                exponentIndicatorIndex = index;
                index++;
                char ch3 = charAt(str, index, endIndex);
                boolean isExponentNegative = ch3 == '-';
                if (isExponentNegative || ch3 == '+') {
                    index++;
                    ch3 = charAt(str, index, endIndex);
                }
                illegal |= !FastDoubleSwar.isDigit(ch3);
                do {
                    if (expNumber < MAX_EXPONENT_NUMBER) {
                        expNumber = ((10 * expNumber) + ch3) - 48;
                    }
                    index++;
                    ch3 = charAt(str, index, endIndex);
                } while (FastDoubleSwar.isDigit(ch3));
                if (isExponentNegative) {
                    expNumber = -expNumber;
                }
                exponent += expNumber;
            } else {
                exponentIndicatorIndex = endIndex;
            }
            if (illegal || index < endIndex || digitCount == 0 || digitCount > MAX_DIGIT_COUNT) {
                throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
            }
            if (exponent <= -2147483648L || exponent > MAX_EXPONENT_NUMBER) {
                throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
            }
            if (digitCount <= 18) {
                return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int) exponent);
            }
            return valueOfBigDecimalString(str, integerPartIndex, decimalPointIndex, decimalPointIndex + 1, exponentIndicatorIndex, isNegative, (int) exponent);
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    BigDecimal parseBigDecimalStringWithManyDigits(char[] str, int offset, int length) {
        int i;
        int digitCount;
        long exponent;
        int exponentIndicatorIndex;
        if (length > 1292782635) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        int nonZeroFractionalPartIndex = -1;
        int decimalPointIndex = -1;
        int endIndex = offset + length;
        int index = offset;
        char ch2 = charAt(str, index, endIndex);
        boolean illegal = false;
        boolean isNegative = ch2 == '-';
        if (isNegative || ch2 == '+') {
            index++;
            ch2 = charAt(str, index, endIndex);
            if (ch2 == 0) {
                throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
            }
        }
        int integerPartIndex = index;
        int swarLimit = Math.min(endIndex - 8, SSL.SSL_OP_NETSCAPE_DEMO_CIPHER_CHANGE_BUG);
        while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
            index += 8;
        }
        while (index < endIndex && str[index] == '0') {
            index++;
        }
        int nonZeroIntegerPartIndex = index;
        while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
            index += 8;
        }
        while (index < endIndex) {
            char c = str[index];
            ch2 = c;
            if (!FastDoubleSwar.isDigit(c)) {
                break;
            }
            index++;
        }
        if (ch2 == '.') {
            int i2 = index;
            index++;
            decimalPointIndex = i2;
            while (index < swarLimit && FastDoubleSwar.isEightZeroes(str, index)) {
                index += 8;
            }
            while (index < endIndex && str[index] == '0') {
                index++;
            }
            nonZeroFractionalPartIndex = index;
            while (index < swarLimit && FastDoubleSwar.isEightDigits(str, index)) {
                index += 8;
            }
            while (index < endIndex) {
                char c2 = str[index];
                ch2 = c2;
                if (!FastDoubleSwar.isDigit(c2)) {
                    break;
                }
                index++;
            }
        }
        int significandEndIndex = index;
        if (decimalPointIndex < 0) {
            digitCount = significandEndIndex - nonZeroIntegerPartIndex;
            decimalPointIndex = significandEndIndex;
            nonZeroFractionalPartIndex = significandEndIndex;
            exponent = 0;
        } else {
            if (nonZeroIntegerPartIndex == decimalPointIndex) {
                i = significandEndIndex - nonZeroFractionalPartIndex;
            } else {
                i = (significandEndIndex - nonZeroIntegerPartIndex) - 1;
            }
            digitCount = i;
            exponent = (decimalPointIndex - significandEndIndex) + 1;
        }
        long expNumber = 0;
        if ((ch2 | ' ') == 101) {
            exponentIndicatorIndex = index;
            index++;
            char ch3 = charAt(str, index, endIndex);
            boolean isExponentNegative = ch3 == '-';
            if (isExponentNegative || ch3 == '+') {
                index++;
                ch3 = charAt(str, index, endIndex);
            }
            illegal = !FastDoubleSwar.isDigit(ch3);
            do {
                if (expNumber < MAX_EXPONENT_NUMBER) {
                    expNumber = ((10 * expNumber) + ch3) - 48;
                }
                index++;
                ch3 = charAt(str, index, endIndex);
            } while (FastDoubleSwar.isDigit(ch3));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        } else {
            exponentIndicatorIndex = endIndex;
        }
        if (illegal || index < endIndex) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (exponentIndicatorIndex - integerPartIndex == 0) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (exponent < -2147483648L || exponent > MAX_EXPONENT_NUMBER || digitCount > MAX_DIGIT_COUNT) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
        return valueOfBigDecimalString(str, nonZeroIntegerPartIndex, decimalPointIndex, nonZeroFractionalPartIndex, exponentIndicatorIndex, isNegative, (int) exponent);
    }

    private BigDecimal valueOfBigDecimalString(char[] str, int integerPartIndex, int decimalPointIndex, int nonZeroFractionalPartIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        BigInteger integerPart;
        BigInteger significand;
        BigInteger fractionalPart;
        int integerExponent = (exponentIndicatorIndex - decimalPointIndex) - 1;
        int fractionDigitsCount = exponentIndicatorIndex - nonZeroFractionalPartIndex;
        int integerDigitsCount = decimalPointIndex - integerPartIndex;
        NavigableMap<Integer, BigInteger> powersOfTen = null;
        if (integerDigitsCount > 0) {
            if (integerDigitsCount > 400) {
                powersOfTen = FastIntegerMath.createPowersOfTenFloor16Map();
                FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, integerPartIndex, decimalPointIndex);
                integerPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, powersOfTen);
            } else {
                integerPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, null);
            }
        } else {
            integerPart = BigInteger.ZERO;
        }
        if (fractionDigitsCount > 0) {
            if (fractionDigitsCount > 400) {
                if (powersOfTen == null) {
                    powersOfTen = FastIntegerMath.createPowersOfTenFloor16Map();
                }
                FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, decimalPointIndex + 1, exponentIndicatorIndex);
                fractionalPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, decimalPointIndex + 1, exponentIndicatorIndex, powersOfTen);
            } else {
                fractionalPart = ParseDigitsTaskCharArray.parseDigitsRecursive(str, decimalPointIndex + 1, exponentIndicatorIndex, null);
            }
            if (integerPart.signum() == 0) {
                significand = fractionalPart;
            } else {
                BigInteger integerFactor = FastIntegerMath.computePowerOfTen(powersOfTen, integerExponent);
                significand = FftMultiplier.multiply(integerPart, integerFactor).add(fractionalPart);
            }
        } else {
            significand = integerPart;
        }
        return new BigDecimal(isNegative ? significand.negate() : significand, -exponent);
    }
}
