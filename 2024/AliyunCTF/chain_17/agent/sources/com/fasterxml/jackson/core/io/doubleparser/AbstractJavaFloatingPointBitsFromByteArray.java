package com.fasterxml.jackson.core.io.doubleparser;

import ch.qos.logback.core.spi.AbstractComponentTracker;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/AbstractJavaFloatingPointBitsFromByteArray.class */
abstract class AbstractJavaFloatingPointBitsFromByteArray extends AbstractFloatValueParser {
    abstract long nan();

    abstract long negativeInfinity();

    abstract long positiveInfinity();

    abstract long valueOfFloatLiteral(byte[] bArr, int i, int i2, boolean z, long j, int i3, boolean z2, int i4);

    abstract long valueOfHexLiteral(byte[] bArr, int i, int i2, boolean z, long j, int i3, boolean z2, int i4);

    private static int skipWhitespace(byte[] str, int index, int endIndex) {
        while (index < endIndex && (str[index] & 255) <= 32) {
            index++;
        }
        return index;
    }

    private long parseDecFloatLiteral(byte[] str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        int digitCount;
        int exponent;
        boolean isSignificandTruncated;
        int exponentOfTruncatedSignificand;
        int digits;
        long significand = 0;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        byte ch2 = 0;
        while (index < endIndex) {
            ch2 = str[index];
            if (FastDoubleSwar.isDigit(ch2)) {
                significand = ((10 * significand) + ch2) - 48;
            } else {
                if (ch2 != 46) {
                    break;
                }
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                while (index < endIndex - 4 && (digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1)) >= 0) {
                    significand = (AbstractComponentTracker.LINGERING_TIMEOUT * significand) + digits;
                    index += 4;
                }
            }
            index++;
        }
        int significandEndIndex = index;
        if (virtualIndexOfPoint < 0) {
            digitCount = index - index;
            virtualIndexOfPoint = index;
            exponent = 0;
        } else {
            digitCount = (index - index) - 1;
            exponent = (virtualIndexOfPoint - index) + 1;
        }
        int expNumber = 0;
        if ((ch2 | 32) == 101) {
            index++;
            ch2 = charAt(str, index, endIndex);
            boolean isExponentNegative = ch2 == 45;
            if (isExponentNegative || ch2 == 43) {
                index++;
                ch2 = charAt(str, index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch2);
            do {
                if (expNumber < 1024) {
                    expNumber = ((10 * expNumber) + ch2) - 48;
                }
                index++;
                ch2 = charAt(str, index, endIndex);
            } while (FastDoubleSwar.isDigit(ch2));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if ((ch2 == 100) | (ch2 == 68) | (ch2 == 102) | (ch2 == 70)) {
            index++;
        }
        int index2 = skipWhitespace(str, index, endIndex);
        if (illegal || index2 < endIndex || (!hasLeadingZero && digitCount == 0)) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 19) {
            significand = 0;
            int index3 = index;
            while (index3 < significandEndIndex) {
                byte ch3 = str[index3];
                if (ch3 == 46) {
                    skipCountInTruncatedDigits++;
                } else {
                    if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) {
                        break;
                    }
                    significand = ((10 * significand) + ch3) - 48;
                }
                index3++;
            }
            isSignificandTruncated = index3 < significandEndIndex;
            exponentOfTruncatedSignificand = (virtualIndexOfPoint - index3) + skipCountInTruncatedDigits + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return valueOfFloatLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
    }

    public long parseFloatingPointLiteral(byte[] str, int offset, int length) {
        int endIndex = offset + length;
        if (offset < 0 || endIndex < offset || endIndex > str.length || length > 2147483643) {
            throw new IllegalArgumentException(AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
        }
        int index = skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        byte ch2 = str[index];
        boolean isNegative = ch2 == 45;
        if (isNegative || ch2 == 43) {
            index++;
            ch2 = charAt(str, index, endIndex);
            if (ch2 == 0) {
                throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
            }
        }
        if (ch2 >= 73) {
            return parseNaNOrInfinity(str, index, endIndex, isNegative);
        }
        boolean hasLeadingZero = ch2 == 48;
        if (hasLeadingZero) {
            index++;
            byte ch3 = charAt(str, index, endIndex);
            if (ch3 == 120 || ch3 == 88) {
                return parseHexFloatingPointLiteral(str, index + 1, offset, endIndex, isNegative);
            }
        }
        return parseDecFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
    }

    private long parseHexFloatingPointLiteral(byte[] str, int index, int startIndex, int endIndex, boolean isNegative) {
        int digitCount;
        boolean isSignificandTruncated;
        long significand = 0;
        int exponent = 0;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        byte ch2 = 0;
        while (index < endIndex) {
            ch2 = str[index];
            int hexValue = lookupHex(ch2);
            if (hexValue >= 0) {
                significand = (significand << 4) | hexValue;
            } else {
                if (hexValue != -4) {
                    break;
                }
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
            }
            index++;
        }
        int significandEndIndex = index;
        if (virtualIndexOfPoint < 0) {
            digitCount = significandEndIndex - index;
            virtualIndexOfPoint = significandEndIndex;
        } else {
            digitCount = (significandEndIndex - index) - 1;
            exponent = Math.min((virtualIndexOfPoint - index) + 1, 1024) * 4;
        }
        int expNumber = 0;
        boolean hasExponent = (ch2 | 32) == 112;
        if (hasExponent) {
            index++;
            ch2 = charAt(str, index, endIndex);
            boolean isExponentNegative = ch2 == 45;
            if (isExponentNegative || ch2 == 43) {
                index++;
                ch2 = charAt(str, index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch2);
            do {
                if (expNumber < 1024) {
                    expNumber = ((10 * expNumber) + ch2) - 48;
                }
                index++;
                ch2 = charAt(str, index, endIndex);
            } while (FastDoubleSwar.isDigit(ch2));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if ((ch2 == 100) | (ch2 == 68) | (ch2 == 102) | (ch2 == 70)) {
            index++;
        }
        int index2 = skipWhitespace(str, index, endIndex);
        if (illegal || index2 < endIndex || digitCount == 0 || !hasExponent) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 16) {
            significand = 0;
            index2 = index;
            while (index2 < significandEndIndex) {
                byte ch3 = str[index2];
                int hexValue2 = lookupHex(ch3);
                if (hexValue2 >= 0) {
                    if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) {
                        break;
                    }
                    significand = (significand << 4) | hexValue2;
                } else {
                    skipCountInTruncatedDigits++;
                }
                index2++;
            }
            isSignificandTruncated = index2 < significandEndIndex;
        } else {
            isSignificandTruncated = false;
        }
        return valueOfHexLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, (((virtualIndexOfPoint - index2) + skipCountInTruncatedDigits) * 4) + expNumber);
    }

    private long parseNaNOrInfinity(byte[] str, int index, int endIndex, boolean isNegative) {
        if (str[index] == 78) {
            if (index + 2 < endIndex && str[index + 1] == 97 && str[index + 2] == 78 && skipWhitespace(str, index + 3, endIndex) == endIndex) {
                return nan();
            }
        } else if (index + 7 < endIndex && FastDoubleSwar.readLongLE(str, index) == 8751735898823355977L && skipWhitespace(str, index + 8, endIndex) == endIndex) {
            return isNegative ? negativeInfinity() : positiveInfinity();
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }
}
