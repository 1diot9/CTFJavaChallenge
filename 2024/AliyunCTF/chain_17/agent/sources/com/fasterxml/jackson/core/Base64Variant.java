package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import java.io.Serializable;
import java.util.Arrays;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/Base64Variant.class */
public final class Base64Variant implements Serializable {
    private static final int INT_SPACE = 32;
    private static final long serialVersionUID = 1;
    protected static final char PADDING_CHAR_NONE = 0;
    public static final int BASE64_VALUE_INVALID = -1;
    public static final int BASE64_VALUE_PADDING = -2;
    private final transient int[] _asciiToBase64;
    private final transient char[] _base64ToAsciiC;
    private final transient byte[] _base64ToAsciiB;
    final String _name;
    private final char _paddingChar;
    private final int _maxLineLength;
    private final boolean _writePadding;
    private final PaddingReadBehaviour _paddingReadBehaviour;

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/Base64Variant$PaddingReadBehaviour.class */
    public enum PaddingReadBehaviour {
        PADDING_FORBIDDEN,
        PADDING_REQUIRED,
        PADDING_ALLOWED
    }

    public Base64Variant(String name, String base64Alphabet, boolean writePadding, char paddingChar, int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        this._writePadding = writePadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        int alphaLen = base64Alphabet.length();
        if (alphaLen != 64) {
            throw new IllegalArgumentException("Base64Alphabet length must be exactly 64 (was " + alphaLen + ")");
        }
        base64Alphabet.getChars(0, alphaLen, this._base64ToAsciiC, 0);
        Arrays.fill(this._asciiToBase64, -1);
        for (int i = 0; i < alphaLen; i++) {
            char alpha = this._base64ToAsciiC[i];
            this._base64ToAsciiB[i] = (byte) alpha;
            this._asciiToBase64[alpha] = i;
        }
        if (writePadding) {
            this._asciiToBase64[paddingChar] = -2;
        }
        this._paddingReadBehaviour = writePadding ? PaddingReadBehaviour.PADDING_REQUIRED : PaddingReadBehaviour.PADDING_FORBIDDEN;
    }

    public Base64Variant(Base64Variant base, String name, int maxLineLength) {
        this(base, name, base._writePadding, base._paddingChar, maxLineLength);
    }

    public Base64Variant(Base64Variant base, String name, boolean writePadding, char paddingChar, int maxLineLength) {
        this(base, name, writePadding, paddingChar, base._paddingReadBehaviour, maxLineLength);
    }

    private Base64Variant(Base64Variant base, String name, boolean writePadding, char paddingChar, PaddingReadBehaviour paddingReadBehaviour, int maxLineLength) {
        this._asciiToBase64 = new int[128];
        this._base64ToAsciiC = new char[64];
        this._base64ToAsciiB = new byte[64];
        this._name = name;
        byte[] srcB = base._base64ToAsciiB;
        System.arraycopy(srcB, 0, this._base64ToAsciiB, 0, srcB.length);
        char[] srcC = base._base64ToAsciiC;
        System.arraycopy(srcC, 0, this._base64ToAsciiC, 0, srcC.length);
        int[] srcV = base._asciiToBase64;
        System.arraycopy(srcV, 0, this._asciiToBase64, 0, srcV.length);
        this._writePadding = writePadding;
        this._paddingChar = paddingChar;
        this._maxLineLength = maxLineLength;
        this._paddingReadBehaviour = paddingReadBehaviour;
    }

    private Base64Variant(Base64Variant base, PaddingReadBehaviour paddingReadBehaviour) {
        this(base, base._name, base._writePadding, base._paddingChar, paddingReadBehaviour, base._maxLineLength);
    }

    public Base64Variant withPaddingAllowed() {
        return withReadPadding(PaddingReadBehaviour.PADDING_ALLOWED);
    }

    public Base64Variant withPaddingRequired() {
        return withReadPadding(PaddingReadBehaviour.PADDING_REQUIRED);
    }

    public Base64Variant withPaddingForbidden() {
        return withReadPadding(PaddingReadBehaviour.PADDING_FORBIDDEN);
    }

    public Base64Variant withReadPadding(PaddingReadBehaviour readPadding) {
        return readPadding == this._paddingReadBehaviour ? this : new Base64Variant(this, readPadding);
    }

    public Base64Variant withWritePadding(boolean writePadding) {
        return writePadding == this._writePadding ? this : new Base64Variant(this, this._name, writePadding, this._paddingChar, this._maxLineLength);
    }

    protected Object readResolve() {
        Base64Variant base = Base64Variants.valueOf(this._name);
        if (this._writePadding != base._writePadding || this._paddingChar != base._paddingChar || this._paddingReadBehaviour != base._paddingReadBehaviour || this._maxLineLength != base._maxLineLength || this._writePadding != base._writePadding) {
            return new Base64Variant(base, this._name, this._writePadding, this._paddingChar, this._paddingReadBehaviour, this._maxLineLength);
        }
        return base;
    }

    public String getName() {
        return this._name;
    }

    public boolean usesPadding() {
        return this._writePadding;
    }

    public boolean requiresPaddingOnRead() {
        return this._paddingReadBehaviour == PaddingReadBehaviour.PADDING_REQUIRED;
    }

    public boolean acceptsPaddingOnRead() {
        return this._paddingReadBehaviour != PaddingReadBehaviour.PADDING_FORBIDDEN;
    }

    public boolean usesPaddingChar(char c) {
        return c == this._paddingChar;
    }

    public boolean usesPaddingChar(int ch2) {
        return ch2 == this._paddingChar;
    }

    public PaddingReadBehaviour paddingReadBehaviour() {
        return this._paddingReadBehaviour;
    }

    public char getPaddingChar() {
        return this._paddingChar;
    }

    public byte getPaddingByte() {
        return (byte) this._paddingChar;
    }

    public int getMaxLineLength() {
        return this._maxLineLength;
    }

    public int decodeBase64Char(char c) {
        if (c <= 127) {
            return this._asciiToBase64[c];
        }
        return -1;
    }

    public int decodeBase64Char(int ch2) {
        if (ch2 <= 127) {
            return this._asciiToBase64[ch2];
        }
        return -1;
    }

    public int decodeBase64Byte(byte b) {
        if (b >= 0) {
            return this._asciiToBase64[b];
        }
        return -1;
    }

    public char encodeBase64BitsAsChar(int value) {
        return this._base64ToAsciiC[value];
    }

    public int encodeBase64Chunk(int b24, char[] buffer, int outPtr) {
        int outPtr2 = outPtr + 1;
        buffer[outPtr] = this._base64ToAsciiC[(b24 >> 18) & 63];
        int outPtr3 = outPtr2 + 1;
        buffer[outPtr2] = this._base64ToAsciiC[(b24 >> 12) & 63];
        int outPtr4 = outPtr3 + 1;
        buffer[outPtr3] = this._base64ToAsciiC[(b24 >> 6) & 63];
        int outPtr5 = outPtr4 + 1;
        buffer[outPtr4] = this._base64ToAsciiC[b24 & 63];
        return outPtr5;
    }

    public void encodeBase64Chunk(StringBuilder sb, int b24) {
        sb.append(this._base64ToAsciiC[(b24 >> 18) & 63]);
        sb.append(this._base64ToAsciiC[(b24 >> 12) & 63]);
        sb.append(this._base64ToAsciiC[(b24 >> 6) & 63]);
        sb.append(this._base64ToAsciiC[b24 & 63]);
    }

    public int encodeBase64Partial(int bits, int outputBytes, char[] buffer, int outPtr) {
        int outPtr2 = outPtr + 1;
        buffer[outPtr] = this._base64ToAsciiC[(bits >> 18) & 63];
        int outPtr3 = outPtr2 + 1;
        buffer[outPtr2] = this._base64ToAsciiC[(bits >> 12) & 63];
        if (usesPadding()) {
            int outPtr4 = outPtr3 + 1;
            buffer[outPtr3] = outputBytes == 2 ? this._base64ToAsciiC[(bits >> 6) & 63] : this._paddingChar;
            outPtr3 = outPtr4 + 1;
            buffer[outPtr4] = this._paddingChar;
        } else if (outputBytes == 2) {
            outPtr3++;
            buffer[outPtr3] = this._base64ToAsciiC[(bits >> 6) & 63];
        }
        return outPtr3;
    }

    public void encodeBase64Partial(StringBuilder sb, int bits, int outputBytes) {
        sb.append(this._base64ToAsciiC[(bits >> 18) & 63]);
        sb.append(this._base64ToAsciiC[(bits >> 12) & 63]);
        if (usesPadding()) {
            sb.append(outputBytes == 2 ? this._base64ToAsciiC[(bits >> 6) & 63] : this._paddingChar);
            sb.append(this._paddingChar);
        } else if (outputBytes == 2) {
            sb.append(this._base64ToAsciiC[(bits >> 6) & 63]);
        }
    }

    public byte encodeBase64BitsAsByte(int value) {
        return this._base64ToAsciiB[value];
    }

    public int encodeBase64Chunk(int b24, byte[] buffer, int outPtr) {
        int outPtr2 = outPtr + 1;
        buffer[outPtr] = this._base64ToAsciiB[(b24 >> 18) & 63];
        int outPtr3 = outPtr2 + 1;
        buffer[outPtr2] = this._base64ToAsciiB[(b24 >> 12) & 63];
        int outPtr4 = outPtr3 + 1;
        buffer[outPtr3] = this._base64ToAsciiB[(b24 >> 6) & 63];
        int outPtr5 = outPtr4 + 1;
        buffer[outPtr4] = this._base64ToAsciiB[b24 & 63];
        return outPtr5;
    }

    public int encodeBase64Partial(int bits, int outputBytes, byte[] buffer, int outPtr) {
        int outPtr2 = outPtr + 1;
        buffer[outPtr] = this._base64ToAsciiB[(bits >> 18) & 63];
        int outPtr3 = outPtr2 + 1;
        buffer[outPtr2] = this._base64ToAsciiB[(bits >> 12) & 63];
        if (usesPadding()) {
            byte pb = (byte) this._paddingChar;
            int outPtr4 = outPtr3 + 1;
            buffer[outPtr3] = outputBytes == 2 ? this._base64ToAsciiB[(bits >> 6) & 63] : pb;
            outPtr3 = outPtr4 + 1;
            buffer[outPtr4] = pb;
        } else if (outputBytes == 2) {
            outPtr3++;
            buffer[outPtr3] = this._base64ToAsciiB[(bits >> 6) & 63];
        }
        return outPtr3;
    }

    public String encode(byte[] input) {
        return encode(input, false);
    }

    public String encode(byte[] input, boolean addQuotes) {
        int inputEnd = input.length;
        StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
        if (addQuotes) {
            sb.append('\"');
        }
        int chunksBeforeLF = getMaxLineLength() >> 2;
        int inputPtr = 0;
        int safeInputEnd = inputEnd - 3;
        while (inputPtr <= safeInputEnd) {
            int i = inputPtr;
            int inputPtr2 = inputPtr + 1;
            int inputPtr3 = inputPtr2 + 1;
            int i2 = ((input[i] << 8) | (input[inputPtr2] & 255)) << 8;
            inputPtr = inputPtr3 + 1;
            encodeBase64Chunk(sb, i2 | (input[inputPtr3] & 255));
            chunksBeforeLF--;
            if (chunksBeforeLF <= 0) {
                sb.append('\\');
                sb.append('n');
                chunksBeforeLF = getMaxLineLength() >> 2;
            }
        }
        int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            int i3 = inputPtr;
            int inputPtr4 = inputPtr + 1;
            int b24 = input[i3] << 16;
            if (inputLeft == 2) {
                int i4 = inputPtr4 + 1;
                b24 |= (input[inputPtr4] & 255) << 8;
            }
            encodeBase64Partial(sb, b24, inputLeft);
        }
        if (addQuotes) {
            sb.append('\"');
        }
        return sb.toString();
    }

    public String encode(byte[] input, boolean addQuotes, String linefeed) {
        int inputEnd = input.length;
        StringBuilder sb = new StringBuilder(inputEnd + (inputEnd >> 2) + (inputEnd >> 3));
        if (addQuotes) {
            sb.append('\"');
        }
        int chunksBeforeLF = getMaxLineLength() >> 2;
        int inputPtr = 0;
        int safeInputEnd = inputEnd - 3;
        while (inputPtr <= safeInputEnd) {
            int i = inputPtr;
            int inputPtr2 = inputPtr + 1;
            int inputPtr3 = inputPtr2 + 1;
            int i2 = ((input[i] << 8) | (input[inputPtr2] & 255)) << 8;
            inputPtr = inputPtr3 + 1;
            encodeBase64Chunk(sb, i2 | (input[inputPtr3] & 255));
            chunksBeforeLF--;
            if (chunksBeforeLF <= 0) {
                sb.append(linefeed);
                chunksBeforeLF = getMaxLineLength() >> 2;
            }
        }
        int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            int i3 = inputPtr;
            int inputPtr4 = inputPtr + 1;
            int b24 = input[i3] << 16;
            if (inputLeft == 2) {
                int i4 = inputPtr4 + 1;
                b24 |= (input[inputPtr4] & 255) << 8;
            }
            encodeBase64Partial(sb, b24, inputLeft);
        }
        if (addQuotes) {
            sb.append('\"');
        }
        return sb.toString();
    }

    public byte[] decode(String input) throws IllegalArgumentException {
        ByteArrayBuilder b = new ByteArrayBuilder();
        decode(input, b);
        return b.toByteArray();
    }

    public void decode(String str, ByteArrayBuilder builder) throws IllegalArgumentException {
        int ptr = 0;
        int len = str.length();
        while (ptr < len) {
            int i = ptr;
            ptr++;
            char ch2 = str.charAt(i);
            if (ch2 > ' ') {
                int bits = decodeBase64Char(ch2);
                if (bits < 0) {
                    _reportInvalidBase64(ch2, 0, null);
                }
                if (ptr >= len) {
                    _reportBase64EOF();
                }
                int ptr2 = ptr + 1;
                char ch3 = str.charAt(ptr);
                int bits2 = decodeBase64Char(ch3);
                if (bits2 < 0) {
                    _reportInvalidBase64(ch3, 1, null);
                }
                int decodedData = (bits << 6) | bits2;
                if (ptr2 >= len) {
                    if (!requiresPaddingOnRead()) {
                        builder.append(decodedData >> 4);
                        return;
                    }
                    _reportBase64EOF();
                }
                int ptr3 = ptr2 + 1;
                char ch4 = str.charAt(ptr2);
                int bits3 = decodeBase64Char(ch4);
                if (bits3 < 0) {
                    if (bits3 != -2) {
                        _reportInvalidBase64(ch4, 2, null);
                    }
                    if (!acceptsPaddingOnRead()) {
                        _reportBase64UnexpectedPadding();
                    }
                    if (ptr3 >= len) {
                        _reportBase64EOF();
                    }
                    ptr = ptr3 + 1;
                    char ch5 = str.charAt(ptr3);
                    if (!usesPaddingChar(ch5)) {
                        _reportInvalidBase64(ch5, 3, "expected padding character '" + getPaddingChar() + "'");
                    }
                    builder.append(decodedData >> 4);
                } else {
                    int decodedData2 = (decodedData << 6) | bits3;
                    if (ptr3 >= len) {
                        if (!requiresPaddingOnRead()) {
                            builder.appendTwoBytes(decodedData2 >> 2);
                            return;
                        }
                        _reportBase64EOF();
                    }
                    ptr = ptr3 + 1;
                    char ch6 = str.charAt(ptr3);
                    int bits4 = decodeBase64Char(ch6);
                    if (bits4 < 0) {
                        if (bits4 != -2) {
                            _reportInvalidBase64(ch6, 3, null);
                        }
                        if (!acceptsPaddingOnRead()) {
                            _reportBase64UnexpectedPadding();
                        }
                        builder.appendTwoBytes(decodedData2 >> 2);
                    } else {
                        builder.appendThreeBytes((decodedData2 << 6) | bits4);
                    }
                }
            }
        }
    }

    public String toString() {
        return this._name;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Base64Variant other = (Base64Variant) o;
        return other._paddingChar == this._paddingChar && other._maxLineLength == this._maxLineLength && other._writePadding == this._writePadding && other._paddingReadBehaviour == this._paddingReadBehaviour && this._name.equals(other._name);
    }

    public int hashCode() {
        return this._name.hashCode();
    }

    protected void _reportInvalidBase64(char ch2, int bindex, String msg) throws IllegalArgumentException {
        String base;
        if (ch2 <= ' ') {
            base = "Illegal white space character (code 0x" + Integer.toHexString(ch2) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
        } else if (usesPaddingChar(ch2)) {
            base = "Unexpected padding character ('" + getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        } else if (!Character.isDefined(ch2) || Character.isISOControl(ch2)) {
            base = "Illegal character (code 0x" + Integer.toHexString(ch2) + ") in base64 content";
        } else {
            base = "Illegal character '" + ch2 + "' (code 0x" + Integer.toHexString(ch2) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        throw new IllegalArgumentException(base);
    }

    protected void _reportBase64EOF() throws IllegalArgumentException {
        throw new IllegalArgumentException(missingPaddingMessage());
    }

    protected void _reportBase64UnexpectedPadding() throws IllegalArgumentException {
        throw new IllegalArgumentException(unexpectedPaddingMessage());
    }

    protected String unexpectedPaddingMessage() {
        return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects no padding at the end while decoding. This Base64Variant might have been incorrectly configured", getName());
    }

    public String missingPaddingMessage() {
        return String.format("Unexpected end of base64-encoded String: base64 variant '%s' expects padding (one or more '%c' characters) at the end. This Base64Variant might have been incorrectly configured", getName(), Character.valueOf(getPaddingChar()));
    }
}
