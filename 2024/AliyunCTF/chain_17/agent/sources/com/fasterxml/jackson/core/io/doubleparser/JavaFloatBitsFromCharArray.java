package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaFloatBitsFromCharArray.class */
public final class JavaFloatBitsFromCharArray extends AbstractJavaFloatingPointBitsFromCharArray {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    long valueOfFloatLiteral(char[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float result = FastFloatMath.decFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Float.isNaN(result) ? Float.floatToRawIntBits(Float.parseFloat(new String(str, startIndex, endIndex - startIndex))) : Float.floatToRawIntBits(result);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    long valueOfHexLiteral(char[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float d = FastFloatMath.hexFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex)) : d);
    }
}
