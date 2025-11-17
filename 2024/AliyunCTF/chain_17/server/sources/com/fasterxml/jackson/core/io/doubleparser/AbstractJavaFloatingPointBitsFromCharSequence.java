package com.fasterxml.jackson.core.io.doubleparser;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/AbstractJavaFloatingPointBitsFromCharSequence.class */
abstract class AbstractJavaFloatingPointBitsFromCharSequence extends AbstractFloatValueParser {
    abstract long nan();

    abstract long negativeInfinity();

    abstract long positiveInfinity();

    abstract long valueOfFloatLiteral(CharSequence charSequence, int i, int i2, boolean z, long j, int i3, boolean z2, int i4);

    abstract long valueOfHexLiteral(CharSequence charSequence, int i, int i2, boolean z, long j, int i3, boolean z2, int i4);

    private static int skipWhitespace(CharSequence str, int index, int endIndex) {
        while (index < endIndex && str.charAt(index) <= ' ') {
            index++;
        }
        return index;
    }

    private long parseDecFloatLiteral(CharSequence str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        int digitCount;
        int exponent;
        boolean isSignificandTruncated;
        int exponentOfTruncatedSignificand;
        long significand = 0;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch2 = 0;
        while (index < endIndex) {
            ch2 = str.charAt(index);
            if (FastDoubleSwar.isDigit(ch2)) {
                significand = ((10 * significand) + ch2) - 48;
            } else {
                if (ch2 != '.') {
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
            exponent = 0;
        } else {
            digitCount = (significandEndIndex - index) - 1;
            exponent = (virtualIndexOfPoint - significandEndIndex) + 1;
        }
        int expNumber = 0;
        if ((ch2 | ' ') == 101) {
            index++;
            ch2 = charAt(str, index, endIndex);
            boolean isExponentNegative = ch2 == '-';
            if (isExponentNegative || ch2 == '+') {
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
        if ((ch2 == 'd') | (ch2 == 'D') | (ch2 == 'f') | (ch2 == 'F')) {
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
                char ch3 = str.charAt(index3);
                if (ch3 == '.') {
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

    public final long parseFloatingPointLiteral(CharSequence str, int offset, int length) {
        int endIndex = offset + length;
        if (offset < 0 || endIndex < offset || endIndex > str.length() || length > 2147483643) {
            throw new IllegalArgumentException(AbstractNumberParser.ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
        }
        int index = skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        char ch2 = str.charAt(index);
        boolean isNegative = ch2 == '-';
        if (isNegative || ch2 == '+') {
            index++;
            ch2 = charAt(str, index, endIndex);
            if (ch2 == 0) {
                throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
            }
        }
        if (ch2 >= 'I') {
            return parseNaNOrInfinity(str, index, endIndex, isNegative);
        }
        boolean hasLeadingZero = ch2 == '0';
        if (hasLeadingZero) {
            index++;
            char ch3 = charAt(str, index, endIndex);
            if (ch3 == 'x' || ch3 == 'X') {
                return parseHexFloatLiteral(str, index + 1, offset, endIndex, isNegative);
            }
        }
        return parseDecFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
    }

    private long parseHexFloatLiteral(CharSequence str, int index, int startIndex, int endIndex, boolean isNegative) {
        int digitCount;
        boolean isSignificandTruncated;
        long significand = 0;
        int exponent = 0;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch2 = 0;
        while (index < endIndex) {
            ch2 = str.charAt(index);
            int hexValue = lookupHex(ch2);
            if (hexValue >= 0) {
                significand = (significand << 4) | hexValue;
            } else {
                if (hexValue != -4) {
                    break;
                }
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                while (index < endIndex - 8) {
                    long parsed = FastDoubleSwar.tryToParseEightHexDigits(str, index + 1);
                    if (parsed >= 0) {
                        significand = (significand << 32) + parsed;
                        index += 8;
                    }
                }
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
        boolean hasExponent = (ch2 | ' ') == 112;
        if (hasExponent) {
            index++;
            ch2 = charAt(str, index, endIndex);
            boolean isExponentNegative = ch2 == '-';
            if (isExponentNegative || ch2 == '+') {
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
        if ((ch2 == 'd') | (ch2 == 'D') | (ch2 == 'f') | (ch2 == 'F')) {
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
                char ch3 = str.charAt(index2);
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

    private long parseNaNOrInfinity(CharSequence str, int index, int endIndex, boolean isNegative) {
        if (str.charAt(index) == 'N') {
            if (index + 2 < endIndex && str.charAt(index + 1) == 'a' && str.charAt(index + 2) == 'N' && skipWhitespace(str, index + 3, endIndex) == endIndex) {
                return nan();
            }
        } else if (index + 7 < endIndex && str.charAt(index) == 'I' && str.charAt(index + 1) == 'n' && str.charAt(index + 2) == 'f' && str.charAt(index + 3) == 'i' && str.charAt(index + 4) == 'n' && str.charAt(index + 5) == 'i' && str.charAt(index + 6) == 't' && str.charAt(index + 7) == 'y' && skipWhitespace(str, index + 8, endIndex) == endIndex) {
            return isNegative ? negativeInfinity() : positiveInfinity();
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }
}
