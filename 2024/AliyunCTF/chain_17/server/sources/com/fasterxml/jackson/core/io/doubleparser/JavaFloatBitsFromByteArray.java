package com.fasterxml.jackson.core.io.doubleparser;

import java.nio.charset.StandardCharsets;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaFloatBitsFromByteArray.class */
public final class JavaFloatBitsFromByteArray extends AbstractJavaFloatingPointBitsFromByteArray {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    long valueOfFloatLiteral(byte[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float result = FastFloatMath.decFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Float.floatToRawIntBits(Float.isNaN(result) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex, StandardCharsets.ISO_8859_1)) : result);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    long valueOfHexLiteral(byte[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float d = FastFloatMath.hexFloatLiteralToFloat(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Float.floatToRawIntBits(Float.isNaN(d) ? Float.parseFloat(new String(str, startIndex, endIndex - startIndex, StandardCharsets.ISO_8859_1)) : d);
    }
}
