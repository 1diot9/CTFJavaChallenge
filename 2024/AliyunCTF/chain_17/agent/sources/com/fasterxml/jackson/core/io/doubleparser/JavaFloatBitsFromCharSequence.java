package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaFloatBitsFromCharSequence.class */
public final class JavaFloatBitsFromCharSequence extends AbstractJavaFloatingPointBitsFromCharSequence {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long valueOfFloatLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float d = FastFloatMath.decFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.subSequence(startIndex, endIndex).toString()) : d);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long valueOfHexLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float d = FastFloatMath.hexFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(str.subSequence(startIndex, endIndex).toString()) : d);
    }
}
