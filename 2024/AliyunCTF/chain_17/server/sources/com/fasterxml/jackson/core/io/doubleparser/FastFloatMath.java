package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.FastIntegerMath;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FastFloatMath.class */
class FastFloatMath {
    private static final int FLOAT_EXPONENT_BIAS = 127;
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TEN = -45;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TEN = 38;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TWO = -126;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TWO = 127;
    private static final float[] FLOAT_POWER_OF_TEN = {1.0f, 10.0f, 100.0f, 1000.0f, 10000.0f, 100000.0f, 1000000.0f, 1.0E7f, 1.0E8f, 1.0E9f, 1.0E10f};

    private FastFloatMath() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float decFloatLiteralToFloat(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float result;
        if (significand == 0) {
            return isNegative ? -0.0f : 0.0f;
        }
        if (isSignificandTruncated) {
            if (FLOAT_MIN_EXPONENT_POWER_OF_TEN <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 38) {
                float withoutRounding = tryDecToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
                float roundedUp = tryDecToFloatWithFastAlgorithm(isNegative, significand + 1, exponentOfTruncatedSignificand);
                if (!Float.isNaN(withoutRounding) && roundedUp == withoutRounding) {
                    return withoutRounding;
                }
            }
            result = Float.NaN;
        } else if (FLOAT_MIN_EXPONENT_POWER_OF_TEN <= exponent && exponent <= 38) {
            result = tryDecToFloatWithFastAlgorithm(isNegative, significand, exponent);
        } else {
            result = Float.NaN;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float hexFloatLiteralToFloat(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        int power = isSignificandTruncated ? exponentOfTruncatedSignificand : exponent;
        if (FLOAT_MIN_EXPONENT_POWER_OF_TWO <= power && power <= 127) {
            float d = Math.abs((float) significand) * Math.scalb(1.0f, power);
            if (isNegative) {
                d = -d;
            }
            return d;
        }
        return Float.NaN;
    }

    static float tryDecToFloatWithFastAlgorithm(boolean isNegative, long significand, int power) {
        float d;
        if (-10 <= power && power <= 10 && Long.compareUnsigned(significand, 16777215L) <= 0) {
            float d2 = (float) significand;
            if (power < 0) {
                d = d2 / FLOAT_POWER_OF_TEN[-power];
            } else {
                d = d2 * FLOAT_POWER_OF_TEN[power];
            }
            return isNegative ? -d : d;
        }
        long factorMantissa = FastDoubleMath.MANTISSA_64[power - (-325)];
        long exponent = ((217706 * power) >> 16) + 127 + 64;
        int lz = Long.numberOfLeadingZeros(significand);
        long shiftedSignificand = significand << lz;
        FastIntegerMath.UInt128 product = FastIntegerMath.fullMultiplication(shiftedSignificand, factorMantissa);
        long upper = product.high;
        long upperbit = upper >>> 63;
        long mantissa = upper >>> ((int) (upperbit + 38));
        int lz2 = lz + ((int) (1 ^ upperbit));
        if ((upper & 274877906943L) == 274877906943L) {
            return Float.NaN;
        }
        if ((upper & 274877906943L) == 0 && (mantissa & 3) == 1) {
            return Float.NaN;
        }
        long mantissa2 = (mantissa + 1) >>> 1;
        if (mantissa2 >= 16777216) {
            mantissa2 = 8388608;
            lz2--;
        }
        long mantissa3 = mantissa2 & (-8388609);
        long real_exponent = exponent - lz2;
        if (real_exponent < 1 || real_exponent > 254) {
            return Float.NaN;
        }
        int bits = (int) (mantissa3 | (real_exponent << 23) | (isNegative ? 2147483648L : 0L));
        return Float.intBitsToFloat(bits);
    }
}
