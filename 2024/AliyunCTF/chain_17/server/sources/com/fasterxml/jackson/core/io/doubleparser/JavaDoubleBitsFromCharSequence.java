package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaDoubleBitsFromCharSequence.class */
public final class JavaDoubleBitsFromCharSequence extends AbstractJavaFloatingPointBitsFromCharSequence {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long valueOfFloatLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        double d;
        double d2 = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        if (Double.isNaN(d2)) {
            d = Double.parseDouble(str.subSequence(startIndex, endIndex).toString());
        } else {
            d = d2;
        }
        return Double.doubleToRawLongBits(d);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    long valueOfHexLiteral(CharSequence str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        double d;
        double d2 = FastDoubleMath.tryHexFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        if (Double.isNaN(d2)) {
            d = Double.parseDouble(str.subSequence(startIndex, endIndex).toString());
        } else {
            d = d2;
        }
        return Double.doubleToRawLongBits(d);
    }
}
