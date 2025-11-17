package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.jooq.types.UShort;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/UTF8JsonGenerator.class */
public class UTF8JsonGenerator extends JsonGeneratorImpl {
    private static final byte BYTE_u = 117;
    private static final byte BYTE_0 = 48;
    private static final byte BYTE_LBRACKET = 91;
    private static final byte BYTE_RBRACKET = 93;
    private static final byte BYTE_LCURLY = 123;
    private static final byte BYTE_RCURLY = 125;
    private static final byte BYTE_BACKSLASH = 92;
    private static final byte BYTE_COMMA = 44;
    private static final byte BYTE_COLON = 58;
    private static final int MAX_BYTES_TO_BUFFER = 512;
    private static final byte[] HEX_BYTES_UPPER = CharTypes.copyHexBytes(true);
    private static final byte[] HEX_BYTES_LOWER = CharTypes.copyHexBytes(false);
    private static final byte[] NULL_BYTES = {110, 117, 108, 108};
    private static final byte[] TRUE_BYTES = {116, 114, 117, 101};
    private static final byte[] FALSE_BYTES = {102, 97, 108, 115, 101};
    protected final OutputStream _outputStream;
    protected byte _quoteChar;
    protected byte[] _outputBuffer;
    protected int _outputTail;
    protected final int _outputEnd;
    protected final int _outputMaxContiguous;
    protected char[] _charBuffer;
    protected final int _charBufferLength;
    protected byte[] _entityBuffer;
    protected boolean _bufferRecyclable;

    public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, char quoteChar) {
        super(ctxt, features, codec);
        this._outputStream = out;
        this._quoteChar = (byte) quoteChar;
        if (quoteChar != '\"') {
            this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
        }
        this._bufferRecyclable = true;
        this._outputBuffer = ctxt.allocWriteEncodingBuffer();
        this._outputEnd = this._outputBuffer.length;
        this._outputMaxContiguous = this._outputEnd >> 3;
        this._charBuffer = ctxt.allocConcatBuffer();
        this._charBufferLength = this._charBuffer.length;
        if (isEnabled(JsonGenerator.Feature.ESCAPE_NON_ASCII)) {
            setHighestNonEscapedChar(Opcodes.LAND);
        }
    }

    public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, char quoteChar, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
        super(ctxt, features, codec);
        this._outputStream = out;
        this._quoteChar = (byte) quoteChar;
        if (quoteChar != '\"') {
            this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
        }
        this._bufferRecyclable = bufferRecyclable;
        this._outputTail = outputOffset;
        this._outputBuffer = outputBuffer;
        this._outputEnd = this._outputBuffer.length;
        this._outputMaxContiguous = this._outputEnd >> 3;
        this._charBuffer = ctxt.allocConcatBuffer();
        this._charBufferLength = this._charBuffer.length;
    }

    @Deprecated
    public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
        this(ctxt, features, codec, out, '\"');
    }

    @Deprecated
    public UTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out, byte[] outputBuffer, int outputOffset, boolean bufferRecyclable) {
        this(ctxt, features, codec, out, '\"', outputBuffer, outputOffset, bufferRecyclable);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public Object getOutputTarget() {
        return this._outputStream;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getOutputBuffered() {
        return this._outputTail;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(String name) throws IOException {
        if (this._cfgPrettyPrinter != null) {
            _writePPFieldName(name);
            return;
        }
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        if (status == 1) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = 44;
        }
        if (this._cfgUnqNames) {
            _writeStringSegments(name, false);
            return;
        }
        int len = name.length();
        if (len > this._charBufferLength) {
            _writeStringSegments(name, true);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
        if (len <= this._outputMaxContiguous) {
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(name, 0, len);
        } else {
            _writeStringSegments(name, 0, len);
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr3 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        bArr3[i3] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(SerializableString name) throws IOException {
        if (this._cfgPrettyPrinter != null) {
            _writePPFieldName(name);
            return;
        }
        int status = this._writeContext.writeFieldName(name.getValue());
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        if (status == 1) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = 44;
        }
        if (this._cfgUnqNames) {
            _writeUnq(name);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
        int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeBytes(name.asQuotedUTF8());
        } else {
            this._outputTail += len;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr3 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        bArr3[i3] = this._quoteChar;
    }

    private final void _writeUnq(SerializableString name) throws IOException {
        int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeBytes(name.asQuotedUTF8());
        } else {
            this._outputTail += len;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartArray() throws IOException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 91;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartArray(Object currentValue) throws IOException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext(currentValue);
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 91;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object currentValue, int size) throws IOException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext(currentValue);
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 91;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeEndArray() throws IOException {
        if (!this._writeContext.inArray()) {
            _reportError("Current context not Array but " + this._writeContext.typeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
        } else {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = 93;
        }
        this._writeContext = this._writeContext.clearAndGetParent();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeStartObject() throws IOException {
        _verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 123;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue) throws IOException {
        _verifyValueWrite("start an object");
        JsonWriteContext ctxt = this._writeContext.createChildObjectContext(forValue);
        this._writeContext = ctxt;
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = 123;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue, int size) throws IOException {
        writeStartObject(forValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeEndObject() throws IOException {
        if (!this._writeContext.inObject()) {
            _reportError("Current context not Object but " + this._writeContext.typeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
        } else {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = 125;
        }
        this._writeContext = this._writeContext.clearAndGetParent();
    }

    protected final void _writePPFieldName(String name) throws IOException {
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        if (status == 1) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        if (this._cfgUnqNames) {
            _writeStringSegments(name, false);
            return;
        }
        int len = name.length();
        if (len > this._charBufferLength) {
            _writeStringSegments(name, true);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        name.getChars(0, len, this._charBuffer, 0);
        if (len <= this._outputMaxContiguous) {
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(this._charBuffer, 0, len);
        } else {
            _writeStringSegments(this._charBuffer, 0, len);
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    protected final void _writePPFieldName(SerializableString name) throws IOException {
        int status = this._writeContext.writeFieldName(name.getValue());
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        if (status == 1) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        boolean addQuotes = !this._cfgUnqNames;
        if (addQuotes) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = this._quoteChar;
        }
        int len = name.appendQuotedUTF8(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeBytes(name.asQuotedUTF8());
        } else {
            this._outputTail += len;
        }
        if (addQuotes) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            bArr2[i2] = this._quoteChar;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(String text) throws IOException {
        _verifyValueWrite("write a string");
        if (text == null) {
            _writeNull();
            return;
        }
        int len = text.length();
        if (len > this._outputMaxContiguous) {
            _writeStringSegments(text, true);
            return;
        }
        if (this._outputTail + len >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        _writeStringSegment(text, 0, len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(Reader reader, int len) throws IOException {
        _verifyValueWrite("write a string");
        if (reader == null) {
            _reportError("null reader");
            return;
        }
        int toRead = len >= 0 ? len : Integer.MAX_VALUE;
        char[] buf = this._charBuffer;
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        while (toRead > 0) {
            int toReadNow = Math.min(toRead, buf.length);
            int numRead = reader.read(buf, 0, toReadNow);
            if (numRead <= 0) {
                break;
            }
            if (this._outputTail + len >= this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegments(buf, 0, numRead);
            toRead -= numRead;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
        if (toRead > 0 && len >= 0) {
            _reportError("Didn't read enough from reader");
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(char[] text, int offset, int len) throws IOException {
        _verifyValueWrite("write a string");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        if (len <= this._outputMaxContiguous) {
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(text, offset, len);
        } else {
            _writeStringSegments(text, offset, len);
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public final void writeString(SerializableString text) throws IOException {
        _verifyValueWrite("write a string");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        int len = text.appendQuotedUTF8(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeBytes(text.asQuotedUTF8());
        } else {
            this._outputTail += len;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawUTF8String(byte[] text, int offset, int len) throws IOException {
        _checkRangeBoundsForByteArray(text, offset, len);
        _verifyValueWrite("write a string");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        _writeBytes(text, offset, len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeUTF8String(byte[] text, int offset, int len) throws IOException {
        _checkRangeBoundsForByteArray(text, offset, len);
        _verifyValueWrite("write a string");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        if (len <= this._outputMaxContiguous) {
            _writeUTF8Segment(text, offset, len);
        } else {
            _writeUTF8Segments(text, offset, len);
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text) throws IOException {
        int len = text.length();
        char[] buf = this._charBuffer;
        if (len <= buf.length) {
            text.getChars(0, len, buf, 0);
            writeRaw(buf, 0, len);
        } else {
            writeRaw(text, 0, len);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text, int offset, int len) throws IOException {
        char ch2;
        _checkRangeBoundsForString(text, offset, len);
        char[] buf = this._charBuffer;
        int cbufLen = buf.length;
        if (len <= cbufLen) {
            text.getChars(offset, offset + len, buf, 0);
            writeRaw(buf, 0, len);
            return;
        }
        int maxChunk = Math.min(cbufLen, (this._outputEnd >> 2) + (this._outputEnd >> 4));
        int maxBytes = maxChunk * 3;
        while (len > 0) {
            int len2 = Math.min(maxChunk, len);
            text.getChars(offset, offset + len2, buf, 0);
            if (this._outputTail + maxBytes > this._outputEnd) {
                _flushBuffer();
            }
            if (len2 > 1 && (ch2 = buf[len2 - 1]) >= 55296 && ch2 <= 56319) {
                len2--;
            }
            _writeRawSegment(buf, 0, len2);
            offset += len2;
            len -= len2;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(SerializableString text) throws IOException {
        int len = text.appendUnquotedUTF8(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeBytes(text.asUnquotedUTF8());
        } else {
            this._outputTail += len;
        }
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public void writeRawValue(SerializableString text) throws IOException {
        _verifyValueWrite("write a raw (unencoded) value");
        int len = text.appendUnquotedUTF8(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeBytes(text.asUnquotedUTF8());
        } else {
            this._outputTail += len;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public final void writeRaw(char[] cbuf, int offset, int len) throws IOException {
        _checkRangeBoundsForCharArray(cbuf, offset, len);
        int len3 = len + len + len;
        if (this._outputTail + len3 > this._outputEnd) {
            if (this._outputEnd < len3) {
                _writeSegmentedRaw(cbuf, offset, len);
                return;
            }
            _flushBuffer();
        }
        int len2 = len + offset;
        while (offset < len2) {
            do {
                char c = cbuf[offset];
                if (c <= 127) {
                    byte[] bArr = this._outputBuffer;
                    int i = this._outputTail;
                    this._outputTail = i + 1;
                    bArr[i] = (byte) c;
                    offset++;
                } else {
                    int i2 = offset;
                    offset++;
                    char ch2 = cbuf[i2];
                    if (ch2 < 2048) {
                        byte[] bArr2 = this._outputBuffer;
                        int i3 = this._outputTail;
                        this._outputTail = i3 + 1;
                        bArr2[i3] = (byte) (192 | (ch2 >> 6));
                        byte[] bArr3 = this._outputBuffer;
                        int i4 = this._outputTail;
                        this._outputTail = i4 + 1;
                        bArr3[i4] = (byte) (128 | (ch2 & '?'));
                    } else {
                        offset = _outputRawMultiByteChar(ch2, cbuf, offset, len2);
                    }
                }
            } while (offset < len2);
            return;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char ch2) throws IOException {
        if (this._outputTail + 3 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bbuf = this._outputBuffer;
        if (ch2 <= 127) {
            int i = this._outputTail;
            this._outputTail = i + 1;
            bbuf[i] = (byte) ch2;
        } else {
            if (ch2 < 2048) {
                int i2 = this._outputTail;
                this._outputTail = i2 + 1;
                bbuf[i2] = (byte) (192 | (ch2 >> 6));
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                bbuf[i3] = (byte) (128 | (ch2 & '?'));
                return;
            }
            _outputRawMultiByteChar(ch2, null, 0, 0);
        }
    }

    private final void _writeSegmentedRaw(char[] cbuf, int offset, int len) throws IOException {
        int end = this._outputEnd;
        byte[] bbuf = this._outputBuffer;
        int inputEnd = offset + len;
        while (offset < inputEnd) {
            do {
                char c = cbuf[offset];
                if (c < 128) {
                    if (this._outputTail >= end) {
                        _flushBuffer();
                    }
                    int i = this._outputTail;
                    this._outputTail = i + 1;
                    bbuf[i] = (byte) c;
                    offset++;
                } else {
                    if (this._outputTail + 3 >= this._outputEnd) {
                        _flushBuffer();
                    }
                    int i2 = offset;
                    offset++;
                    char ch2 = cbuf[i2];
                    if (ch2 < 2048) {
                        int i3 = this._outputTail;
                        this._outputTail = i3 + 1;
                        bbuf[i3] = (byte) (192 | (ch2 >> 6));
                        int i4 = this._outputTail;
                        this._outputTail = i4 + 1;
                        bbuf[i4] = (byte) (128 | (ch2 & '?'));
                    } else {
                        offset = _outputRawMultiByteChar(ch2, cbuf, offset, inputEnd);
                    }
                }
            } while (offset < inputEnd);
            return;
        }
    }

    private void _writeRawSegment(char[] cbuf, int offset, int end) throws IOException {
        while (offset < end) {
            do {
                char c = cbuf[offset];
                if (c <= 127) {
                    byte[] bArr = this._outputBuffer;
                    int i = this._outputTail;
                    this._outputTail = i + 1;
                    bArr[i] = (byte) c;
                    offset++;
                } else {
                    int i2 = offset;
                    offset++;
                    char ch2 = cbuf[i2];
                    if (ch2 < 2048) {
                        byte[] bArr2 = this._outputBuffer;
                        int i3 = this._outputTail;
                        this._outputTail = i3 + 1;
                        bArr2[i3] = (byte) (192 | (ch2 >> 6));
                        byte[] bArr3 = this._outputBuffer;
                        int i4 = this._outputTail;
                        this._outputTail = i4 + 1;
                        bArr3[i4] = (byte) (128 | (ch2 & '?'));
                    } else {
                        offset = _outputRawMultiByteChar(ch2, cbuf, offset, end);
                    }
                }
            } while (offset < end);
            return;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        _checkRangeBoundsForByteArray(data, offset, len);
        _verifyValueWrite("write a binary value");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        _writeBinary(b64variant, data, offset, offset + len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
        int bytes;
        _verifyValueWrite("write a binary value");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        byte[] encodingBuffer = this._ioContext.allocBase64Buffer();
        try {
            if (dataLength < 0) {
                bytes = _writeBinary(b64variant, data, encodingBuffer);
            } else {
                int missing = _writeBinary(b64variant, data, encodingBuffer, dataLength);
                if (missing > 0) {
                    _reportError("Too few bytes available: missing " + missing + " bytes (out of " + dataLength + ")");
                }
                bytes = dataLength;
            }
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            bArr2[i2] = this._quoteChar;
            return bytes;
        } finally {
            this._ioContext.releaseBase64Buffer(encodingBuffer);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(short s) throws IOException {
        _verifyValueWrite("write a number");
        if (this._outputTail + 6 >= this._outputEnd) {
            _flushBuffer();
        }
        if (this._cfgNumbersAsStrings) {
            _writeQuotedShort(s);
        } else {
            this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
        }
    }

    private final void _writeQuotedShort(short s) throws IOException {
        if (this._outputTail + 8 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(int i) throws IOException {
        _verifyValueWrite("write a number");
        if (this._outputTail + 11 >= this._outputEnd) {
            _flushBuffer();
        }
        if (this._cfgNumbersAsStrings) {
            _writeQuotedInt(i);
        } else {
            this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        }
    }

    private final void _writeQuotedInt(int i) throws IOException {
        if (this._outputTail + 13 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr[i2] = this._quoteChar;
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        byte[] bArr2 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        bArr2[i3] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(long l) throws IOException {
        _verifyValueWrite("write a number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedLong(l);
            return;
        }
        if (this._outputTail + 21 >= this._outputEnd) {
            _flushBuffer();
        }
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
    }

    private final void _writeQuotedLong(long l) throws IOException {
        if (this._outputTail + 23 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigInteger value) throws IOException {
        _verifyValueWrite("write a number");
        if (value == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(value.toString());
        } else {
            writeRaw(value.toString());
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(double d) throws IOException {
        if (this._cfgNumbersAsStrings || (NumberOutput.notFinite(d) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS.enabledIn(this._features))) {
            writeString(NumberOutput.toString(d, isEnabled(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)));
        } else {
            _verifyValueWrite("write a number");
            writeRaw(NumberOutput.toString(d, isEnabled(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(float f) throws IOException {
        if (this._cfgNumbersAsStrings || (NumberOutput.notFinite(f) && JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS.enabledIn(this._features))) {
            writeString(NumberOutput.toString(f, isEnabled(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)));
        } else {
            _verifyValueWrite("write a number");
            writeRaw(NumberOutput.toString(f, isEnabled(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(BigDecimal value) throws IOException {
        _verifyValueWrite("write a number");
        if (value == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(_asString(value));
        } else {
            writeRaw(_asString(value));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(String encodedValue) throws IOException {
        _verifyValueWrite("write a number");
        if (encodedValue == null) {
            _writeNull();
        } else if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(encodedValue);
        } else {
            writeRaw(encodedValue);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(char[] encodedValueBuffer, int offset, int length) throws IOException {
        _verifyValueWrite("write a number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedRaw(encodedValueBuffer, offset, length);
        } else {
            writeRaw(encodedValueBuffer, offset, length);
        }
    }

    private final void _writeQuotedRaw(String value) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        writeRaw(value);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    private void _writeQuotedRaw(char[] text, int offset, int length) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = this._quoteChar;
        writeRaw(text, offset, length);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBoolean(boolean state) throws IOException {
        _verifyValueWrite("write a boolean value");
        if (this._outputTail + 5 >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] keyword = state ? TRUE_BYTES : FALSE_BYTES;
        int len = keyword.length;
        System.arraycopy(keyword, 0, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNull() throws IOException {
        _verifyValueWrite("write a null");
        _writeNull();
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    protected final void _verifyValueWrite(String typeMsg) throws IOException {
        byte b;
        int status = this._writeContext.writeValue();
        if (this._cfgPrettyPrinter != null) {
            _verifyPrettyValueWrite(typeMsg, status);
            return;
        }
        switch (status) {
            case 0:
            case 4:
            default:
                return;
            case 1:
                b = 44;
                break;
            case 2:
                b = 58;
                break;
            case 3:
                if (this._rootValueSeparator != null) {
                    byte[] raw = this._rootValueSeparator.asUnquotedUTF8();
                    if (raw.length > 0) {
                        _writeBytes(raw);
                        return;
                    }
                    return;
                }
                return;
            case 5:
                _reportCantWriteValueExpectName(typeMsg);
                return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        byte[] bArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bArr[i] = b;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator, java.io.Flushable
    public void flush() throws IOException {
        _flushBuffer();
        if (this._outputStream != null && isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._outputStream.flush();
        }
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
        Throwable flushFail = null;
        try {
            if (this._outputBuffer != null && isEnabled(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)) {
                while (true) {
                    JsonStreamContext ctxt = getOutputContext();
                    if (ctxt.inArray()) {
                        writeEndArray();
                    } else if (!ctxt.inObject()) {
                        break;
                    } else {
                        writeEndObject();
                    }
                }
            }
            _flushBuffer();
        } catch (IOException e) {
            flushFail = e;
        }
        this._outputTail = 0;
        if (this._outputStream != null) {
            try {
                if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                    this._outputStream.close();
                } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
                    this._outputStream.flush();
                }
            } catch (IOException | RuntimeException e2) {
                if (flushFail != null) {
                    e2.addSuppressed(flushFail);
                }
                throw e2;
            }
        }
        _releaseBuffers();
        if (flushFail != null) {
            throw flushFail;
        }
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    protected void _releaseBuffers() {
        byte[] buf = this._outputBuffer;
        if (buf != null && this._bufferRecyclable) {
            this._outputBuffer = null;
            this._ioContext.releaseWriteEncodingBuffer(buf);
        }
        char[] cbuf = this._charBuffer;
        if (cbuf != null) {
            this._charBuffer = null;
            this._ioContext.releaseConcatBuffer(cbuf);
        }
    }

    private final void _writeBytes(byte[] bytes) throws IOException {
        int len = bytes.length;
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
            if (len > 512) {
                this._outputStream.write(bytes, 0, len);
                return;
            }
        }
        System.arraycopy(bytes, 0, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    private final void _writeBytes(byte[] bytes, int offset, int len) throws IOException {
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
            if (len > 512) {
                this._outputStream.write(bytes, offset, len);
                return;
            }
        }
        System.arraycopy(bytes, offset, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    private final void _writeStringSegments(String text, boolean addQuotes) throws IOException {
        if (addQuotes) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            bArr[i] = this._quoteChar;
        }
        int left = text.length();
        int offset = 0;
        while (left > 0) {
            int len = Math.min(this._outputMaxContiguous, left);
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(text, offset, len);
            offset += len;
            left -= len;
        }
        if (addQuotes) {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            byte[] bArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            bArr2[i2] = this._quoteChar;
        }
    }

    private final void _writeStringSegments(char[] cbuf, int offset, int totalLen) throws IOException {
        do {
            int len = Math.min(this._outputMaxContiguous, totalLen);
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(cbuf, offset, len);
            offset += len;
            totalLen -= len;
        } while (totalLen > 0);
    }

    private final void _writeStringSegments(String text, int offset, int totalLen) throws IOException {
        do {
            int len = Math.min(this._outputMaxContiguous, totalLen);
            if (this._outputTail + len > this._outputEnd) {
                _flushBuffer();
            }
            _writeStringSegment(text, offset, len);
            offset += len;
            totalLen -= len;
        } while (totalLen > 0);
    }

    private final void _writeStringSegment(char[] cbuf, int offset, int len) throws IOException {
        char c;
        int len2 = len + offset;
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        while (offset < len2 && (c = cbuf[offset]) <= 127 && escCodes[c] == 0) {
            int i = outputPtr;
            outputPtr++;
            outputBuffer[i] = (byte) c;
            offset++;
        }
        this._outputTail = outputPtr;
        if (offset < len2) {
            if (this._characterEscapes != null) {
                _writeCustomStringSegment2(cbuf, offset, len2);
            } else if (this._maximumNonEscapedChar == 0) {
                _writeStringSegment2(cbuf, offset, len2);
            } else {
                _writeStringSegmentASCII2(cbuf, offset, len2);
            }
        }
    }

    private final void _writeStringSegment(String text, int offset, int len) throws IOException {
        int ch2;
        int len2 = len + offset;
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        while (offset < len2 && (ch2 = text.charAt(offset)) <= 127 && escCodes[ch2] == 0) {
            int i = outputPtr;
            outputPtr++;
            outputBuffer[i] = (byte) ch2;
            offset++;
        }
        this._outputTail = outputPtr;
        if (offset < len2) {
            if (this._characterEscapes != null) {
                _writeCustomStringSegment2(text, offset, len2);
            } else if (this._maximumNonEscapedChar == 0) {
                _writeStringSegment2(text, offset, len2);
            } else {
                _writeStringSegmentASCII2(text, offset, len2);
            }
        }
    }

    private final void _writeStringSegment2(char[] cbuf, int offset, int end) throws IOException {
        if (this._outputTail + (6 * (end - offset)) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        while (offset < end) {
            int i = offset;
            offset++;
            char c = cbuf[i];
            if (c <= 127) {
                if (escCodes[c] == 0) {
                    int i2 = outputPtr;
                    outputPtr++;
                    outputBuffer[i2] = (byte) c;
                } else {
                    int escape = escCodes[c];
                    if (escape > 0) {
                        int i3 = outputPtr;
                        int outputPtr2 = outputPtr + 1;
                        outputBuffer[i3] = 92;
                        outputPtr = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = (byte) escape;
                    } else {
                        outputPtr = _writeGenericEscape(c, outputPtr);
                    }
                }
            } else if (c <= 2047) {
                int i4 = outputPtr;
                int outputPtr3 = outputPtr + 1;
                outputBuffer[i4] = (byte) (192 | (c >> 6));
                outputPtr = outputPtr3 + 1;
                outputBuffer[outputPtr3] = (byte) (128 | (c & '?'));
            } else {
                outputPtr = _outputMultiByteChar(c, outputPtr);
            }
        }
        this._outputTail = outputPtr;
    }

    private final void _writeStringSegment2(String text, int offset, int end) throws IOException {
        if (this._outputTail + (6 * (end - offset)) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        while (offset < end) {
            int i = offset;
            offset++;
            int ch2 = text.charAt(i);
            if (ch2 <= 127) {
                if (escCodes[ch2] == 0) {
                    int i2 = outputPtr;
                    outputPtr++;
                    outputBuffer[i2] = (byte) ch2;
                } else {
                    int escape = escCodes[ch2];
                    if (escape > 0) {
                        int i3 = outputPtr;
                        int outputPtr2 = outputPtr + 1;
                        outputBuffer[i3] = 92;
                        outputPtr = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = (byte) escape;
                    } else {
                        outputPtr = _writeGenericEscape(ch2, outputPtr);
                    }
                }
            } else if (ch2 <= 2047) {
                int i4 = outputPtr;
                int outputPtr3 = outputPtr + 1;
                outputBuffer[i4] = (byte) (192 | (ch2 >> 6));
                outputPtr = outputPtr3 + 1;
                outputBuffer[outputPtr3] = (byte) (128 | (ch2 & 63));
            } else {
                outputPtr = _outputMultiByteChar(ch2, outputPtr);
            }
        }
        this._outputTail = outputPtr;
    }

    private final void _writeStringSegmentASCII2(char[] cbuf, int offset, int end) throws IOException {
        if (this._outputTail + (6 * (end - offset)) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int maxUnescaped = this._maximumNonEscapedChar;
        while (offset < end) {
            int i = offset;
            offset++;
            char c = cbuf[i];
            if (c <= 127) {
                if (escCodes[c] == 0) {
                    int i2 = outputPtr;
                    outputPtr++;
                    outputBuffer[i2] = (byte) c;
                } else {
                    int escape = escCodes[c];
                    if (escape > 0) {
                        int i3 = outputPtr;
                        int outputPtr2 = outputPtr + 1;
                        outputBuffer[i3] = 92;
                        outputPtr = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = (byte) escape;
                    } else {
                        outputPtr = _writeGenericEscape(c, outputPtr);
                    }
                }
            } else if (c > maxUnescaped) {
                outputPtr = _writeGenericEscape(c, outputPtr);
            } else if (c <= 2047) {
                int i4 = outputPtr;
                int outputPtr3 = outputPtr + 1;
                outputBuffer[i4] = (byte) (192 | (c >> 6));
                outputPtr = outputPtr3 + 1;
                outputBuffer[outputPtr3] = (byte) (128 | (c & '?'));
            } else {
                outputPtr = _outputMultiByteChar(c, outputPtr);
            }
        }
        this._outputTail = outputPtr;
    }

    private final void _writeStringSegmentASCII2(String text, int offset, int end) throws IOException {
        if (this._outputTail + (6 * (end - offset)) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int maxUnescaped = this._maximumNonEscapedChar;
        while (offset < end) {
            int i = offset;
            offset++;
            int ch2 = text.charAt(i);
            if (ch2 <= 127) {
                if (escCodes[ch2] == 0) {
                    int i2 = outputPtr;
                    outputPtr++;
                    outputBuffer[i2] = (byte) ch2;
                } else {
                    int escape = escCodes[ch2];
                    if (escape > 0) {
                        int i3 = outputPtr;
                        int outputPtr2 = outputPtr + 1;
                        outputBuffer[i3] = 92;
                        outputPtr = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = (byte) escape;
                    } else {
                        outputPtr = _writeGenericEscape(ch2, outputPtr);
                    }
                }
            } else if (ch2 > maxUnescaped) {
                outputPtr = _writeGenericEscape(ch2, outputPtr);
            } else if (ch2 <= 2047) {
                int i4 = outputPtr;
                int outputPtr3 = outputPtr + 1;
                outputBuffer[i4] = (byte) (192 | (ch2 >> 6));
                outputPtr = outputPtr3 + 1;
                outputBuffer[outputPtr3] = (byte) (128 | (ch2 & 63));
            } else {
                outputPtr = _outputMultiByteChar(ch2, outputPtr);
            }
        }
        this._outputTail = outputPtr;
    }

    private final void _writeCustomStringSegment2(char[] cbuf, int offset, int end) throws IOException {
        if (this._outputTail + (6 * (end - offset)) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int maxUnescaped = this._maximumNonEscapedChar <= 0 ? UShort.MAX_VALUE : this._maximumNonEscapedChar;
        CharacterEscapes customEscapes = this._characterEscapes;
        while (offset < end) {
            int i = offset;
            offset++;
            char c = cbuf[i];
            if (c <= 127) {
                if (escCodes[c] == 0) {
                    int i2 = outputPtr;
                    outputPtr++;
                    outputBuffer[i2] = (byte) c;
                } else {
                    int escape = escCodes[c];
                    if (escape > 0) {
                        int i3 = outputPtr;
                        int outputPtr2 = outputPtr + 1;
                        outputBuffer[i3] = 92;
                        outputPtr = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = (byte) escape;
                    } else if (escape == -2) {
                        SerializableString esc = customEscapes.getEscapeSequence(c);
                        if (esc == null) {
                            _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(c) + ", although was supposed to have one");
                        }
                        outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
                    } else {
                        outputPtr = _writeGenericEscape(c, outputPtr);
                    }
                }
            } else if (c > maxUnescaped) {
                outputPtr = _writeGenericEscape(c, outputPtr);
            } else {
                SerializableString esc2 = customEscapes.getEscapeSequence(c);
                if (esc2 != null) {
                    outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc2, end - offset);
                } else if (c <= 2047) {
                    int i4 = outputPtr;
                    int outputPtr3 = outputPtr + 1;
                    outputBuffer[i4] = (byte) (192 | (c >> 6));
                    outputPtr = outputPtr3 + 1;
                    outputBuffer[outputPtr3] = (byte) (128 | (c & '?'));
                } else {
                    outputPtr = _outputMultiByteChar(c, outputPtr);
                }
            }
        }
        this._outputTail = outputPtr;
    }

    private final void _writeCustomStringSegment2(String text, int offset, int end) throws IOException {
        if (this._outputTail + (6 * (end - offset)) > this._outputEnd) {
            _flushBuffer();
        }
        int outputPtr = this._outputTail;
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int maxUnescaped = this._maximumNonEscapedChar <= 0 ? UShort.MAX_VALUE : this._maximumNonEscapedChar;
        CharacterEscapes customEscapes = this._characterEscapes;
        while (offset < end) {
            int i = offset;
            offset++;
            int ch2 = text.charAt(i);
            if (ch2 <= 127) {
                if (escCodes[ch2] == 0) {
                    int i2 = outputPtr;
                    outputPtr++;
                    outputBuffer[i2] = (byte) ch2;
                } else {
                    int escape = escCodes[ch2];
                    if (escape > 0) {
                        int i3 = outputPtr;
                        int outputPtr2 = outputPtr + 1;
                        outputBuffer[i3] = 92;
                        outputPtr = outputPtr2 + 1;
                        outputBuffer[outputPtr2] = (byte) escape;
                    } else if (escape == -2) {
                        SerializableString esc = customEscapes.getEscapeSequence(ch2);
                        if (esc == null) {
                            _reportError("Invalid custom escape definitions; custom escape not found for character code 0x" + Integer.toHexString(ch2) + ", although was supposed to have one");
                        }
                        outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc, end - offset);
                    } else {
                        outputPtr = _writeGenericEscape(ch2, outputPtr);
                    }
                }
            } else if (ch2 > maxUnescaped) {
                outputPtr = _writeGenericEscape(ch2, outputPtr);
            } else {
                SerializableString esc2 = customEscapes.getEscapeSequence(ch2);
                if (esc2 != null) {
                    outputPtr = _writeCustomEscape(outputBuffer, outputPtr, esc2, end - offset);
                } else if (ch2 <= 2047) {
                    int i4 = outputPtr;
                    int outputPtr3 = outputPtr + 1;
                    outputBuffer[i4] = (byte) (192 | (ch2 >> 6));
                    outputPtr = outputPtr3 + 1;
                    outputBuffer[outputPtr3] = (byte) (128 | (ch2 & 63));
                } else {
                    outputPtr = _outputMultiByteChar(ch2, outputPtr);
                }
            }
        }
        this._outputTail = outputPtr;
    }

    private final int _writeCustomEscape(byte[] outputBuffer, int outputPtr, SerializableString esc, int remainingChars) throws IOException, JsonGenerationException {
        byte[] raw = esc.asUnquotedUTF8();
        int len = raw.length;
        if (len > 6) {
            return _handleLongCustomEscape(outputBuffer, outputPtr, this._outputEnd, raw, remainingChars);
        }
        System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
        return outputPtr + len;
    }

    private final int _handleLongCustomEscape(byte[] outputBuffer, int outputPtr, int outputEnd, byte[] raw, int remainingChars) throws IOException, JsonGenerationException {
        int len = raw.length;
        if (outputPtr + len > outputEnd) {
            this._outputTail = outputPtr;
            _flushBuffer();
            outputPtr = this._outputTail;
            if (len > outputBuffer.length) {
                this._outputStream.write(raw, 0, len);
                return outputPtr;
            }
        }
        System.arraycopy(raw, 0, outputBuffer, outputPtr, len);
        int outputPtr2 = outputPtr + len;
        if (outputPtr2 + (6 * remainingChars) > outputEnd) {
            this._outputTail = outputPtr2;
            _flushBuffer();
            return this._outputTail;
        }
        return outputPtr2;
    }

    private final void _writeUTF8Segments(byte[] utf8, int offset, int totalLen) throws IOException, JsonGenerationException {
        do {
            int len = Math.min(this._outputMaxContiguous, totalLen);
            _writeUTF8Segment(utf8, offset, len);
            offset += len;
            totalLen -= len;
        } while (totalLen > 0);
    }

    private final void _writeUTF8Segment(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
        int[] escCodes = this._outputEscapes;
        int ptr = offset;
        int end = offset + len;
        while (ptr < end) {
            int i = ptr;
            ptr++;
            byte b = utf8[i];
            if (b >= 0 && escCodes[b] != 0) {
                _writeUTF8Segment2(utf8, offset, len);
                return;
            }
        }
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
        }
        System.arraycopy(utf8, offset, this._outputBuffer, this._outputTail, len);
        this._outputTail += len;
    }

    private final void _writeUTF8Segment2(byte[] utf8, int offset, int len) throws IOException, JsonGenerationException {
        int outputPtr = this._outputTail;
        if (outputPtr + (len * 6) > this._outputEnd) {
            _flushBuffer();
            outputPtr = this._outputTail;
        }
        byte[] outputBuffer = this._outputBuffer;
        int[] escCodes = this._outputEscapes;
        int len2 = len + offset;
        while (offset < len2) {
            int i = offset;
            offset++;
            byte b = utf8[i];
            if (b < 0 || escCodes[b] == 0) {
                int i2 = outputPtr;
                outputPtr++;
                outputBuffer[i2] = b;
            } else {
                int escape = escCodes[b];
                if (escape > 0) {
                    int i3 = outputPtr;
                    int outputPtr2 = outputPtr + 1;
                    outputBuffer[i3] = 92;
                    outputPtr = outputPtr2 + 1;
                    outputBuffer[outputPtr2] = (byte) escape;
                } else {
                    outputPtr = _writeGenericEscape(b, outputPtr);
                }
            }
        }
        this._outputTail = outputPtr;
    }

    protected final void _writeBinary(Base64Variant b64variant, byte[] input, int inputPtr, int inputEnd) throws IOException, JsonGenerationException {
        int safeInputEnd = inputEnd - 3;
        int safeOutputEnd = this._outputEnd - 6;
        int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
        while (inputPtr <= safeInputEnd) {
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int i = inputPtr;
            int inputPtr2 = inputPtr + 1;
            int inputPtr3 = inputPtr2 + 1;
            int i2 = ((input[i] << 8) | (input[inputPtr2] & 255)) << 8;
            inputPtr = inputPtr3 + 1;
            this._outputTail = b64variant.encodeBase64Chunk(i2 | (input[inputPtr3] & 255), this._outputBuffer, this._outputTail);
            chunksBeforeLF--;
            if (chunksBeforeLF <= 0) {
                byte[] bArr = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                bArr[i3] = 92;
                byte[] bArr2 = this._outputBuffer;
                int i4 = this._outputTail;
                this._outputTail = i4 + 1;
                bArr2[i4] = 110;
                chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
            }
        }
        int inputLeft = inputEnd - inputPtr;
        if (inputLeft > 0) {
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int i5 = inputPtr;
            int inputPtr4 = inputPtr + 1;
            int b24 = input[i5] << 16;
            if (inputLeft == 2) {
                int i6 = inputPtr4 + 1;
                b24 |= (input[inputPtr4] & 255) << 8;
            }
            this._outputTail = b64variant.encodeBase64Partial(b24, inputLeft, this._outputBuffer, this._outputTail);
        }
    }

    protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer, int bytesLeft) throws IOException, JsonGenerationException {
        int inputEnd;
        int amount;
        int inputPtr = 0;
        int inputEnd2 = 0;
        int lastFullOffset = -3;
        int safeOutputEnd = this._outputEnd - 6;
        int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
        while (bytesLeft > 2) {
            if (inputPtr > lastFullOffset) {
                inputEnd2 = _readMore(data, readBuffer, inputPtr, inputEnd2, bytesLeft);
                inputPtr = 0;
                if (inputEnd2 < 3) {
                    break;
                }
                lastFullOffset = inputEnd2 - 3;
            }
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int i = inputPtr;
            int inputPtr2 = inputPtr + 1;
            int inputPtr3 = inputPtr2 + 1;
            int i2 = ((readBuffer[i] << 8) | (readBuffer[inputPtr2] & 255)) << 8;
            inputPtr = inputPtr3 + 1;
            bytesLeft -= 3;
            this._outputTail = b64variant.encodeBase64Chunk(i2 | (readBuffer[inputPtr3] & 255), this._outputBuffer, this._outputTail);
            chunksBeforeLF--;
            if (chunksBeforeLF <= 0) {
                byte[] bArr = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                bArr[i3] = 92;
                byte[] bArr2 = this._outputBuffer;
                int i4 = this._outputTail;
                this._outputTail = i4 + 1;
                bArr2[i4] = 110;
                chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
            }
        }
        if (bytesLeft > 0 && (inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd2, bytesLeft)) > 0) {
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int inputPtr4 = 0 + 1;
            int b24 = readBuffer[0] << 16;
            if (inputPtr4 < inputEnd) {
                b24 |= (readBuffer[inputPtr4] & 255) << 8;
                amount = 2;
            } else {
                amount = 1;
            }
            this._outputTail = b64variant.encodeBase64Partial(b24, amount, this._outputBuffer, this._outputTail);
            bytesLeft -= amount;
        }
        return bytesLeft;
    }

    protected final int _writeBinary(Base64Variant b64variant, InputStream data, byte[] readBuffer) throws IOException, JsonGenerationException {
        int inputPtr = 0;
        int inputEnd = 0;
        int lastFullOffset = -3;
        int bytesDone = 0;
        int safeOutputEnd = this._outputEnd - 6;
        int chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
        while (true) {
            if (inputPtr > lastFullOffset) {
                inputEnd = _readMore(data, readBuffer, inputPtr, inputEnd, readBuffer.length);
                inputPtr = 0;
                if (inputEnd < 3) {
                    break;
                }
                lastFullOffset = inputEnd - 3;
            }
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int i = inputPtr;
            int inputPtr2 = inputPtr + 1;
            int b24 = readBuffer[i] << 8;
            int inputPtr3 = inputPtr2 + 1;
            int i2 = (b24 | (readBuffer[inputPtr2] & 255)) << 8;
            inputPtr = inputPtr3 + 1;
            int b242 = i2 | (readBuffer[inputPtr3] & 255);
            bytesDone += 3;
            this._outputTail = b64variant.encodeBase64Chunk(b242, this._outputBuffer, this._outputTail);
            chunksBeforeLF--;
            if (chunksBeforeLF <= 0) {
                byte[] bArr = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                bArr[i3] = 92;
                byte[] bArr2 = this._outputBuffer;
                int i4 = this._outputTail;
                this._outputTail = i4 + 1;
                bArr2[i4] = 110;
                chunksBeforeLF = b64variant.getMaxLineLength() >> 2;
            }
        }
        if (0 < inputEnd) {
            if (this._outputTail > safeOutputEnd) {
                _flushBuffer();
            }
            int inputPtr4 = 0 + 1;
            int b243 = readBuffer[0] << 16;
            int amount = 1;
            if (inputPtr4 < inputEnd) {
                b243 |= (readBuffer[inputPtr4] & 255) << 8;
                amount = 2;
            }
            bytesDone += amount;
            this._outputTail = b64variant.encodeBase64Partial(b243, amount, this._outputBuffer, this._outputTail);
        }
        return bytesDone;
    }

    private final int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
        int i = 0;
        while (inputPtr < inputEnd) {
            int i2 = i;
            i++;
            int i3 = inputPtr;
            inputPtr++;
            readBuffer[i2] = readBuffer[i3];
        }
        int inputEnd2 = i;
        int maxRead2 = Math.min(maxRead, readBuffer.length);
        do {
            int length = maxRead2 - inputEnd2;
            if (length == 0) {
                break;
            }
            int count = in.read(readBuffer, inputEnd2, length);
            if (count < 0) {
                return inputEnd2;
            }
            inputEnd2 += count;
        } while (inputEnd2 < 3);
        return inputEnd2;
    }

    private final int _outputRawMultiByteChar(int ch2, char[] cbuf, int inputOffset, int inputEnd) throws IOException {
        if (ch2 >= 55296 && ch2 <= 57343) {
            if (inputOffset >= inputEnd || cbuf == null) {
                _reportError(String.format("Split surrogate on writeRaw() input (last character): first character 0x%4x", Integer.valueOf(ch2)));
            } else {
                _outputSurrogates(ch2, cbuf[inputOffset]);
            }
            return inputOffset + 1;
        }
        byte[] bbuf = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bbuf[i] = (byte) (224 | (ch2 >> 12));
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bbuf[i2] = (byte) (128 | ((ch2 >> 6) & 63));
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        bbuf[i3] = (byte) (128 | (ch2 & 63));
        return inputOffset;
    }

    protected final void _outputSurrogates(int surr1, int surr2) throws IOException {
        int c = _decodeSurrogate(surr1, surr2);
        if (this._outputTail + 4 > this._outputEnd) {
            _flushBuffer();
        }
        byte[] bbuf = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        bbuf[i] = (byte) (240 | (c >> 18));
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        bbuf[i2] = (byte) (128 | ((c >> 12) & 63));
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        bbuf[i3] = (byte) (128 | ((c >> 6) & 63));
        int i4 = this._outputTail;
        this._outputTail = i4 + 1;
        bbuf[i4] = (byte) (128 | (c & 63));
    }

    private final int _outputMultiByteChar(int ch2, int outputPtr) throws IOException {
        int outputPtr2;
        byte[] HEX_CHARS = getHexBytes();
        byte[] bbuf = this._outputBuffer;
        if (ch2 >= 55296 && ch2 <= 57343) {
            int outputPtr3 = outputPtr + 1;
            bbuf[outputPtr] = 92;
            int outputPtr4 = outputPtr3 + 1;
            bbuf[outputPtr3] = 117;
            int outputPtr5 = outputPtr4 + 1;
            bbuf[outputPtr4] = HEX_CHARS[(ch2 >> 12) & 15];
            int outputPtr6 = outputPtr5 + 1;
            bbuf[outputPtr5] = HEX_CHARS[(ch2 >> 8) & 15];
            int outputPtr7 = outputPtr6 + 1;
            bbuf[outputPtr6] = HEX_CHARS[(ch2 >> 4) & 15];
            outputPtr2 = outputPtr7 + 1;
            bbuf[outputPtr7] = HEX_CHARS[ch2 & 15];
        } else {
            int outputPtr8 = outputPtr + 1;
            bbuf[outputPtr] = (byte) (224 | (ch2 >> 12));
            int outputPtr9 = outputPtr8 + 1;
            bbuf[outputPtr8] = (byte) (128 | ((ch2 >> 6) & 63));
            outputPtr2 = outputPtr9 + 1;
            bbuf[outputPtr9] = (byte) (128 | (ch2 & 63));
        }
        return outputPtr2;
    }

    private final void _writeNull() throws IOException {
        if (this._outputTail + 4 >= this._outputEnd) {
            _flushBuffer();
        }
        System.arraycopy(NULL_BYTES, 0, this._outputBuffer, this._outputTail, 4);
        this._outputTail += 4;
    }

    private int _writeGenericEscape(int charToEscape, int outputPtr) throws IOException {
        int outputPtr2;
        byte[] bbuf = this._outputBuffer;
        byte[] HEX_CHARS = getHexBytes();
        int outputPtr3 = outputPtr + 1;
        bbuf[outputPtr] = 92;
        int outputPtr4 = outputPtr3 + 1;
        bbuf[outputPtr3] = 117;
        if (charToEscape > 255) {
            int hi = (charToEscape >> 8) & 255;
            int outputPtr5 = outputPtr4 + 1;
            bbuf[outputPtr4] = HEX_CHARS[hi >> 4];
            outputPtr2 = outputPtr5 + 1;
            bbuf[outputPtr5] = HEX_CHARS[hi & 15];
            charToEscape &= 255;
        } else {
            int outputPtr6 = outputPtr4 + 1;
            bbuf[outputPtr4] = 48;
            outputPtr2 = outputPtr6 + 1;
            bbuf[outputPtr6] = 48;
        }
        int i = outputPtr2;
        int outputPtr7 = outputPtr2 + 1;
        bbuf[i] = HEX_CHARS[charToEscape >> 4];
        int outputPtr8 = outputPtr7 + 1;
        bbuf[outputPtr7] = HEX_CHARS[charToEscape & 15];
        return outputPtr8;
    }

    protected final void _flushBuffer() throws IOException {
        int len = this._outputTail;
        if (len > 0) {
            this._outputTail = 0;
            this._outputStream.write(this._outputBuffer, 0, len);
        }
    }

    private byte[] getHexBytes() {
        return this._cfgWriteHexUppercase ? HEX_BYTES_UPPER : HEX_BYTES_LOWER;
    }
}
