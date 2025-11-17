package com.fasterxml.jackson.core.io.doubleparser;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/JavaDoubleParser.class */
public class JavaDoubleParser {
    private static final JavaDoubleBitsFromByteArray BYTE_ARRAY_PARSER = new JavaDoubleBitsFromByteArray();
    private static final JavaDoubleBitsFromCharArray CHAR_ARRAY_PARSER = new JavaDoubleBitsFromCharArray();
    private static final JavaDoubleBitsFromCharSequence CHAR_SEQUENCE_PARSER = new JavaDoubleBitsFromCharSequence();

    private JavaDoubleParser() {
    }

    public static double parseDouble(CharSequence str) throws NumberFormatException {
        return parseDouble(str, 0, str.length());
    }

    public static double parseDouble(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_SEQUENCE_PARSER.parseFloatingPointLiteral(str, offset, length);
        return Double.longBitsToDouble(bitPattern);
    }

    public static double parseDouble(byte[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    public static double parseDouble(byte[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = BYTE_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
        return Double.longBitsToDouble(bitPattern);
    }

    public static double parseDouble(char[] str) throws NumberFormatException {
        return parseDouble(str, 0, str.length);
    }

    public static double parseDouble(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = CHAR_ARRAY_PARSER.parseFloatingPointLiteral(str, offset, length);
        return Double.longBitsToDouble(bitPattern);
    }
}
