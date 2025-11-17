package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/WriterBasedJsonGenerator.class */
public class WriterBasedJsonGenerator extends JsonGeneratorImpl {
    protected static final int SHORT_WRITE = 32;
    protected static final char[] HEX_CHARS_UPPER = CharTypes.copyHexChars(true);
    protected static final char[] HEX_CHARS_LOWER = CharTypes.copyHexChars(false);
    protected final Writer _writer;
    protected char _quoteChar;
    protected char[] _outputBuffer;
    protected int _outputHead;
    protected int _outputTail;
    protected int _outputEnd;
    protected char[] _entityBuffer;
    protected SerializableString _currentEscape;
    protected char[] _copyBuffer;

    private char[] getHexChars() {
        return this._cfgWriteHexUppercase ? HEX_CHARS_UPPER : HEX_CHARS_LOWER;
    }

    @Deprecated
    public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w) {
        this(ctxt, features, codec, w, '\"');
    }

    public WriterBasedJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer w, char quoteChar) {
        super(ctxt, features, codec);
        this._writer = w;
        this._outputBuffer = ctxt.allocConcatBuffer();
        this._outputEnd = this._outputBuffer.length;
        this._quoteChar = quoteChar;
        if (quoteChar != '\"') {
            this._outputEscapes = CharTypes.get7BitOutputEscapes(quoteChar);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public Object getOutputTarget() {
        return this._writer;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public int getOutputBuffered() {
        int len = this._outputTail - this._outputHead;
        return Math.max(0, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public boolean canWriteFormattedNumbers() {
        return true;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(String name) throws IOException {
        int status = this._writeContext.writeFieldName(name);
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        _writeFieldName(name, status == 1);
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public void writeFieldName(SerializableString name) throws IOException {
        int status = this._writeContext.writeFieldName(name.getValue());
        if (status == 4) {
            _reportError("Can not write a field name, expecting a value");
        }
        _writeFieldName(name, status == 1);
    }

    protected final void _writeFieldName(String name, boolean commaBefore) throws IOException {
        if (this._cfgPrettyPrinter != null) {
            _writePPFieldName(name, commaBefore);
            return;
        }
        if (this._outputTail + 1 >= this._outputEnd) {
            _flushBuffer();
        }
        if (commaBefore) {
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = ',';
        }
        if (this._cfgUnqNames) {
            _writeString(name);
            return;
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
        _writeString(name);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr3 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        cArr3[i3] = this._quoteChar;
    }

    protected final void _writeFieldName(SerializableString name, boolean commaBefore) throws IOException {
        if (this._cfgPrettyPrinter != null) {
            _writePPFieldName(name, commaBefore);
            return;
        }
        if (this._outputTail + 1 >= this._outputEnd) {
            _flushBuffer();
        }
        if (commaBefore) {
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = ',';
        }
        if (this._cfgUnqNames) {
            char[] ch2 = name.asQuotedChars();
            writeRaw(ch2, 0, ch2.length);
            return;
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
        int len = name.appendQuoted(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeFieldNameTail(name);
            return;
        }
        this._outputTail += len;
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr3 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        cArr3[i3] = this._quoteChar;
    }

    private final void _writeFieldNameTail(SerializableString name) throws IOException {
        char[] quoted = name.asQuotedChars();
        writeRaw(quoted, 0, quoted.length);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray() throws IOException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '[';
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartArray(Object currentValue) throws IOException {
        _verifyValueWrite("start an array");
        this._writeContext = this._writeContext.createChildArrayContext(currentValue);
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartArray(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '[';
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '[';
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeEndArray() throws IOException {
        if (!this._writeContext.inArray()) {
            _reportError("Current context not Array but " + this._writeContext.typeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndArray(this, this._writeContext.getEntryCount());
        } else {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = ']';
        }
        this._writeContext = this._writeContext.clearAndGetParent();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject() throws IOException {
        _verifyValueWrite("start an object");
        this._writeContext = this._writeContext.createChildObjectContext();
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeStartObject(this);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '{';
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = '{';
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeStartObject(Object forValue, int size) throws IOException {
        writeStartObject(forValue);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeEndObject() throws IOException {
        if (!this._writeContext.inObject()) {
            _reportError("Current context not Object but " + this._writeContext.typeDesc());
        }
        if (this._cfgPrettyPrinter != null) {
            this._cfgPrettyPrinter.writeEndObject(this, this._writeContext.getEntryCount());
        } else {
            if (this._outputTail >= this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = '}';
        }
        this._writeContext = this._writeContext.clearAndGetParent();
    }

    protected final void _writePPFieldName(String name, boolean commaBefore) throws IOException {
        if (commaBefore) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        if (this._cfgUnqNames) {
            _writeString(name);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        _writeString(name);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    protected final void _writePPFieldName(SerializableString name, boolean commaBefore) throws IOException {
        if (commaBefore) {
            this._cfgPrettyPrinter.writeObjectEntrySeparator(this);
        } else {
            this._cfgPrettyPrinter.beforeObjectEntries(this);
        }
        char[] quoted = name.asQuotedChars();
        if (this._cfgUnqNames) {
            writeRaw(quoted, 0, quoted.length);
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        writeRaw(quoted, 0, quoted.length);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(String text) throws IOException {
        _verifyValueWrite("write a string");
        if (text == null) {
            _writeNull();
            return;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        _writeString(text);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeString(Reader reader, int len) throws IOException {
        _verifyValueWrite("write a string");
        if (reader == null) {
            _reportError("null reader");
            return;
        }
        int toRead = len >= 0 ? len : Integer.MAX_VALUE;
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        char[] buf = _allocateCopyBuffer();
        while (toRead > 0) {
            int toReadNow = Math.min(toRead, buf.length);
            int numRead = reader.read(buf, 0, toReadNow);
            if (numRead <= 0) {
                break;
            }
            _writeString(buf, 0, numRead);
            toRead -= numRead;
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        _writeString(text, offset, len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public void writeString(SerializableString sstr) throws IOException {
        _verifyValueWrite("write a string");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        int len = sstr.appendQuoted(this._outputBuffer, this._outputTail);
        if (len < 0) {
            _writeString2(sstr);
            return;
        }
        this._outputTail += len;
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    private void _writeString2(SerializableString sstr) throws IOException {
        char[] text = sstr.asQuotedChars();
        int len = text.length;
        if (len < 32) {
            int room = this._outputEnd - this._outputTail;
            if (len > room) {
                _flushBuffer();
            }
            System.arraycopy(text, 0, this._outputBuffer, this._outputTail, len);
            this._outputTail += len;
        } else {
            _flushBuffer();
            this._writer.write(text, 0, len);
        }
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        _reportUnsupportedOperation();
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text) throws IOException {
        int len = text.length();
        int room = this._outputEnd - this._outputTail;
        if (room == 0) {
            _flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(0, len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
        } else {
            writeRawLong(text);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(String text, int offset, int len) throws IOException {
        _checkRangeBoundsForString(text, offset, len);
        int room = this._outputEnd - this._outputTail;
        if (room < len) {
            _flushBuffer();
            room = this._outputEnd - this._outputTail;
        }
        if (room >= len) {
            text.getChars(offset, offset + len, this._outputBuffer, this._outputTail);
            this._outputTail += len;
        } else {
            writeRawLong(text.substring(offset, offset + len));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(SerializableString text) throws IOException {
        int len = text.appendUnquoted(this._outputBuffer, this._outputTail);
        if (len < 0) {
            writeRaw(text.getValue());
        } else {
            this._outputTail += len;
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char[] cbuf, int offset, int len) throws IOException {
        _checkRangeBoundsForCharArray(cbuf, offset, len);
        if (len < 32) {
            int room = this._outputEnd - this._outputTail;
            if (len > room) {
                _flushBuffer();
            }
            System.arraycopy(cbuf, offset, this._outputBuffer, this._outputTail, len);
            this._outputTail += len;
            return;
        }
        _flushBuffer();
        this._writer.write(cbuf, offset, len);
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeRaw(char c) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = c;
    }

    private void writeRawLong(String text) throws IOException {
        int room = this._outputEnd - this._outputTail;
        text.getChars(0, room, this._outputBuffer, this._outputTail);
        this._outputTail += room;
        _flushBuffer();
        int offset = room;
        int length = text.length();
        int i = room;
        while (true) {
            int len = length - i;
            if (len > this._outputEnd) {
                int amount = this._outputEnd;
                text.getChars(offset, offset + amount, this._outputBuffer, 0);
                this._outputHead = 0;
                this._outputTail = amount;
                _flushBuffer();
                offset += amount;
                length = len;
                i = amount;
            } else {
                text.getChars(offset, offset + len, this._outputBuffer, 0);
                this._outputHead = 0;
                this._outputTail = len;
                return;
            }
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBinary(Base64Variant b64variant, byte[] data, int offset, int len) throws IOException, JsonGenerationException {
        _checkRangeBoundsForByteArray(data, offset, len);
        _verifyValueWrite("write a binary value");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        _writeBinary(b64variant, data, offset, offset + len);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator
    public int writeBinary(Base64Variant b64variant, InputStream data, int dataLength) throws IOException, JsonGenerationException {
        int bytes;
        _verifyValueWrite("write a binary value");
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
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
            char[] cArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            cArr2[i2] = this._quoteChar;
            return bytes;
        } finally {
            this._ioContext.releaseBase64Buffer(encodingBuffer);
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(short s) throws IOException {
        _verifyValueWrite("write a number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedShort(s);
            return;
        }
        if (this._outputTail + 6 >= this._outputEnd) {
            _flushBuffer();
        }
        this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
    }

    private void _writeQuotedShort(short s) throws IOException {
        if (this._outputTail + 8 >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        this._outputTail = NumberOutput.outputInt(s, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(int i) throws IOException {
        _verifyValueWrite("write a number");
        if (this._cfgNumbersAsStrings) {
            _writeQuotedInt(i);
            return;
        }
        if (this._outputTail + 11 >= this._outputEnd) {
            _flushBuffer();
        }
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
    }

    private void _writeQuotedInt(int i) throws IOException {
        if (this._outputTail + 13 >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr[i2] = this._quoteChar;
        this._outputTail = NumberOutput.outputInt(i, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i3 = this._outputTail;
        this._outputTail = i3 + 1;
        cArr2[i3] = this._quoteChar;
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

    private void _writeQuotedLong(long l) throws IOException {
        if (this._outputTail + 23 >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        this._outputTail = NumberOutput.outputLong(l, this._outputBuffer, this._outputTail);
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
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
        if (this._cfgNumbersAsStrings || (NumberOutput.notFinite(d) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
            writeString(NumberOutput.toString(d, isEnabled(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)));
        } else {
            _verifyValueWrite("write a number");
            writeRaw(NumberOutput.toString(d, isEnabled(JsonGenerator.Feature.USE_FAST_DOUBLE_WRITER)));
        }
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNumber(float f) throws IOException {
        if (this._cfgNumbersAsStrings || (NumberOutput.notFinite(f) && isEnabled(JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS))) {
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

    private void _writeQuotedRaw(String value) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        writeRaw(value);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    private void _writeQuotedRaw(char[] text, int offset, int length) throws IOException {
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = this._quoteChar;
        writeRaw(text, offset, length);
        if (this._outputTail >= this._outputEnd) {
            _flushBuffer();
        }
        char[] cArr2 = this._outputBuffer;
        int i2 = this._outputTail;
        this._outputTail = i2 + 1;
        cArr2[i2] = this._quoteChar;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeBoolean(boolean state) throws IOException {
        int ptr;
        _verifyValueWrite("write a boolean value");
        if (this._outputTail + 5 >= this._outputEnd) {
            _flushBuffer();
        }
        int ptr2 = this._outputTail;
        char[] buf = this._outputBuffer;
        if (state) {
            buf[ptr2] = 't';
            int ptr3 = ptr2 + 1;
            buf[ptr3] = 'r';
            int ptr4 = ptr3 + 1;
            buf[ptr4] = 'u';
            ptr = ptr4 + 1;
            buf[ptr] = 'e';
        } else {
            buf[ptr2] = 'f';
            int ptr5 = ptr2 + 1;
            buf[ptr5] = 'a';
            int ptr6 = ptr5 + 1;
            buf[ptr6] = 'l';
            int ptr7 = ptr6 + 1;
            buf[ptr7] = 's';
            ptr = ptr7 + 1;
            buf[ptr] = 'e';
        }
        this._outputTail = ptr + 1;
    }

    @Override // com.fasterxml.jackson.core.JsonGenerator
    public void writeNull() throws IOException {
        _verifyValueWrite("write a null");
        _writeNull();
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase
    protected final void _verifyValueWrite(String typeMsg) throws IOException {
        char c;
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
                c = ',';
                break;
            case 2:
                c = ':';
                break;
            case 3:
                if (this._rootValueSeparator != null) {
                    writeRaw(this._rootValueSeparator.getValue());
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
        char[] cArr = this._outputBuffer;
        int i = this._outputTail;
        this._outputTail = i + 1;
        cArr[i] = c;
    }

    @Override // com.fasterxml.jackson.core.base.GeneratorBase, com.fasterxml.jackson.core.JsonGenerator, java.io.Flushable
    public void flush() throws IOException {
        _flushBuffer();
        if (this._writer != null && isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
            this._writer.flush();
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
        this._outputHead = 0;
        this._outputTail = 0;
        if (this._writer != null) {
            try {
                if (this._ioContext.isResourceManaged() || isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)) {
                    this._writer.close();
                } else if (isEnabled(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM)) {
                    this._writer.flush();
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
        char[] buf = this._outputBuffer;
        if (buf != null) {
            this._outputBuffer = null;
            this._ioContext.releaseConcatBuffer(buf);
        }
        char[] buf2 = this._copyBuffer;
        if (buf2 != null) {
            this._copyBuffer = null;
            this._ioContext.releaseNameCopyBuffer(buf2);
        }
    }

    private void _writeString(String text) throws IOException {
        int len = text.length();
        if (len > this._outputEnd) {
            _writeLongString(text);
            return;
        }
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
        }
        text.getChars(0, len, this._outputBuffer, this._outputTail);
        if (this._characterEscapes != null) {
            _writeStringCustom(len);
        } else if (this._maximumNonEscapedChar != 0) {
            _writeStringASCII(len, this._maximumNonEscapedChar);
        } else {
            _writeString2(len);
        }
    }

    private void _writeString2(int len) throws IOException {
        int i;
        int end = this._outputTail + len;
        int[] escCodes = this._outputEscapes;
        int escLen = escCodes.length;
        while (this._outputTail < end) {
            do {
                char c = this._outputBuffer[this._outputTail];
                if (c >= escLen || escCodes[c] == 0) {
                    i = this._outputTail + 1;
                    this._outputTail = i;
                } else {
                    int flushLen = this._outputTail - this._outputHead;
                    if (flushLen > 0) {
                        this._writer.write(this._outputBuffer, this._outputHead, flushLen);
                    }
                    char[] cArr = this._outputBuffer;
                    int i2 = this._outputTail;
                    this._outputTail = i2 + 1;
                    char c2 = cArr[i2];
                    _prependOrWriteCharacterEscape(c2, escCodes[c2]);
                }
            } while (i < end);
            return;
        }
    }

    private void _writeLongString(String text) throws IOException {
        _flushBuffer();
        int textLen = text.length();
        int offset = 0;
        do {
            int max = this._outputEnd;
            int segmentLen = offset + max > textLen ? textLen - offset : max;
            text.getChars(offset, offset + segmentLen, this._outputBuffer, 0);
            if (this._characterEscapes != null) {
                _writeSegmentCustom(segmentLen);
            } else if (this._maximumNonEscapedChar != 0) {
                _writeSegmentASCII(segmentLen, this._maximumNonEscapedChar);
            } else {
                _writeSegment(segmentLen);
            }
            offset += segmentLen;
        } while (offset < textLen);
    }

    private void _writeSegment(int end) throws IOException {
        char c;
        int[] escCodes = this._outputEscapes;
        int escLen = escCodes.length;
        int ptr = 0;
        int i = 0;
        while (true) {
            int start = i;
            if (ptr >= end) {
                return;
            }
            do {
                c = this._outputBuffer[ptr];
                if (c < escLen && escCodes[c] != 0) {
                    break;
                } else {
                    ptr++;
                }
            } while (ptr < end);
            int flushLen = ptr - start;
            if (flushLen > 0) {
                this._writer.write(this._outputBuffer, start, flushLen);
                if (ptr >= end) {
                    return;
                }
            }
            ptr++;
            i = _prependOrWriteCharacterEscape(this._outputBuffer, ptr, end, c, escCodes[c]);
        }
    }

    private void _writeString(char[] text, int offset, int len) throws IOException {
        if (this._characterEscapes != null) {
            _writeStringCustom(text, offset, len);
            return;
        }
        if (this._maximumNonEscapedChar != 0) {
            _writeStringASCII(text, offset, len, this._maximumNonEscapedChar);
            return;
        }
        int len2 = len + offset;
        int[] escCodes = this._outputEscapes;
        int escLen = escCodes.length;
        while (offset < len2) {
            int start = offset;
            do {
                char c = text[offset];
                if (c < escLen && escCodes[c] != 0) {
                    break;
                } else {
                    offset++;
                }
            } while (offset < len2);
            int newAmount = offset - start;
            if (newAmount < 32) {
                if (this._outputTail + newAmount > this._outputEnd) {
                    _flushBuffer();
                }
                if (newAmount > 0) {
                    System.arraycopy(text, start, this._outputBuffer, this._outputTail, newAmount);
                    this._outputTail += newAmount;
                }
            } else {
                _flushBuffer();
                this._writer.write(text, start, newAmount);
            }
            if (offset < len2) {
                int i = offset;
                offset++;
                char c2 = text[i];
                _appendCharacterEscape(c2, escCodes[c2]);
            } else {
                return;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0083 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeStringASCII(int r6, int r7) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            r5 = this;
            r0 = r5
            int r0 = r0._outputTail
            r1 = r6
            int r0 = r0 + r1
            r8 = r0
            r0 = r5
            int[] r0 = r0._outputEscapes
            r9 = r0
            r0 = r9
            int r0 = r0.length
            r1 = r7
            r2 = 1
            int r1 = r1 + r2
            int r0 = java.lang.Math.min(r0, r1)
            r10 = r0
            r0 = 0
            r11 = r0
        L1b:
            r0 = r5
            int r0 = r0._outputTail
            r1 = r8
            if (r0 >= r1) goto L98
        L23:
            r0 = r5
            char[] r0 = r0._outputBuffer
            r1 = r5
            int r1 = r1._outputTail
            char r0 = r0[r1]
            r12 = r0
            r0 = r12
            r1 = r10
            if (r0 >= r1) goto L44
            r0 = r9
            r1 = r12
            r0 = r0[r1]
            r11 = r0
            r0 = r11
            if (r0 == 0) goto L50
            goto L62
        L44:
            r0 = r12
            r1 = r7
            if (r0 <= r1) goto L50
            r0 = -1
            r11 = r0
            goto L62
        L50:
            r0 = r5
            r1 = r0
            int r1 = r1._outputTail
            r2 = 1
            int r1 = r1 + r2
            r2 = r1; r1 = r0; r0 = r2; 
            r1._outputTail = r2
            r1 = r8
            if (r0 < r1) goto L23
            goto L98
        L62:
            r0 = r5
            int r0 = r0._outputTail
            r1 = r5
            int r1 = r1._outputHead
            int r0 = r0 - r1
            r13 = r0
            r0 = r13
            if (r0 <= 0) goto L83
            r0 = r5
            java.io.Writer r0 = r0._writer
            r1 = r5
            char[] r1 = r1._outputBuffer
            r2 = r5
            int r2 = r2._outputHead
            r3 = r13
            r0.write(r1, r2, r3)
        L83:
            r0 = r5
            r1 = r0
            int r1 = r1._outputTail
            r2 = 1
            int r1 = r1 + r2
            r0._outputTail = r1
            r0 = r5
            r1 = r12
            r2 = r11
            r0._prependOrWriteCharacterEscape(r1, r2)
            goto L1b
        L98:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeStringASCII(int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0055 A[EDGE_INSN: B:10:0x0055->B:11:0x0055 BREAK  A[LOOP:1: B:4:0x001f->B:21:?], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:21:? A[LOOP:1: B:4:0x001f->B:21:?, LOOP_END, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeSegmentASCII(int r8, int r9) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            r7 = this;
            r0 = r7
            int[] r0 = r0._outputEscapes
            r10 = r0
            r0 = r10
            int r0 = r0.length
            r1 = r9
            r2 = 1
            int r1 = r1 + r2
            int r0 = java.lang.Math.min(r0, r1)
            r11 = r0
            r0 = 0
            r12 = r0
            r0 = 0
            r13 = r0
            r0 = r12
            r14 = r0
        L19:
            r0 = r12
            r1 = r8
            if (r0 >= r1) goto L90
        L1f:
            r0 = r7
            char[] r0 = r0._outputBuffer
            r1 = r12
            char r0 = r0[r1]
            r15 = r0
            r0 = r15
            r1 = r11
            if (r0 >= r1) goto L3d
            r0 = r10
            r1 = r15
            r0 = r0[r1]
            r13 = r0
            r0 = r13
            if (r0 == 0) goto L49
            goto L55
        L3d:
            r0 = r15
            r1 = r9
            if (r0 <= r1) goto L49
            r0 = -1
            r13 = r0
            goto L55
        L49:
            int r12 = r12 + 1
            r0 = r12
            r1 = r8
            if (r0 < r1) goto L1f
            goto L55
        L55:
            r0 = r12
            r1 = r14
            int r0 = r0 - r1
            r16 = r0
            r0 = r16
            if (r0 <= 0) goto L79
            r0 = r7
            java.io.Writer r0 = r0._writer
            r1 = r7
            char[] r1 = r1._outputBuffer
            r2 = r14
            r3 = r16
            r0.write(r1, r2, r3)
            r0 = r12
            r1 = r8
            if (r0 < r1) goto L79
            goto L90
        L79:
            int r12 = r12 + 1
            r0 = r7
            r1 = r7
            char[] r1 = r1._outputBuffer
            r2 = r12
            r3 = r8
            r4 = r15
            r5 = r13
            int r0 = r0._prependOrWriteCharacterEscape(r1, r2, r3, r4, r5)
            r14 = r0
            goto L19
        L90:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeSegmentASCII(int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0054 A[EDGE_INSN: B:11:0x0054->B:12:0x0054 BREAK  A[LOOP:1: B:5:0x0021->B:27:?], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:27:? A[LOOP:1: B:5:0x0021->B:27:?, LOOP_END, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeStringASCII(char[] r7, int r8, int r9, int r10) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            r6 = this;
            r0 = r9
            r1 = r8
            int r0 = r0 + r1
            r9 = r0
            r0 = r6
            int[] r0 = r0._outputEscapes
            r11 = r0
            r0 = r11
            int r0 = r0.length
            r1 = r10
            r2 = 1
            int r1 = r1 + r2
            int r0 = java.lang.Math.min(r0, r1)
            r12 = r0
            r0 = 0
            r13 = r0
        L19:
            r0 = r8
            r1 = r9
            if (r0 >= r1) goto Lbc
            r0 = r8
            r14 = r0
        L21:
            r0 = r7
            r1 = r8
            char r0 = r0[r1]
            r15 = r0
            r0 = r15
            r1 = r12
            if (r0 >= r1) goto L3c
            r0 = r11
            r1 = r15
            r0 = r0[r1]
            r13 = r0
            r0 = r13
            if (r0 == 0) goto L49
            goto L54
        L3c:
            r0 = r15
            r1 = r10
            if (r0 <= r1) goto L49
            r0 = -1
            r13 = r0
            goto L54
        L49:
            int r8 = r8 + 1
            r0 = r8
            r1 = r9
            if (r0 < r1) goto L21
            goto L54
        L54:
            r0 = r8
            r1 = r14
            int r0 = r0 - r1
            r16 = r0
            r0 = r16
            r1 = 32
            if (r0 >= r1) goto L96
            r0 = r6
            int r0 = r0._outputTail
            r1 = r16
            int r0 = r0 + r1
            r1 = r6
            int r1 = r1._outputEnd
            if (r0 <= r1) goto L73
            r0 = r6
            r0._flushBuffer()
        L73:
            r0 = r16
            if (r0 <= 0) goto La6
            r0 = r7
            r1 = r14
            r2 = r6
            char[] r2 = r2._outputBuffer
            r3 = r6
            int r3 = r3._outputTail
            r4 = r16
            java.lang.System.arraycopy(r0, r1, r2, r3, r4)
            r0 = r6
            r1 = r0
            int r1 = r1._outputTail
            r2 = r16
            int r1 = r1 + r2
            r0._outputTail = r1
            goto La6
        L96:
            r0 = r6
            r0._flushBuffer()
            r0 = r6
            java.io.Writer r0 = r0._writer
            r1 = r7
            r2 = r14
            r3 = r16
            r0.write(r1, r2, r3)
        La6:
            r0 = r8
            r1 = r9
            if (r0 < r1) goto Lae
            goto Lbc
        Lae:
            int r8 = r8 + 1
            r0 = r6
            r1 = r15
            r2 = r13
            r0._appendCharacterEscape(r1, r2)
            goto L19
        Lbc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeStringASCII(char[], int, int, int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x00b1 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeStringCustom(int r6) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            Method dump skipped, instructions count: 199
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeStringCustom(int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0083 A[EDGE_INSN: B:13:0x0083->B:14:0x0083 BREAK  A[LOOP:1: B:7:0x0037->B:24:?], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:24:? A[LOOP:1: B:7:0x0037->B:24:?, LOOP_END, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeSegmentCustom(int r8) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            r7 = this;
            r0 = r7
            int[] r0 = r0._outputEscapes
            r9 = r0
            r0 = r7
            int r0 = r0._maximumNonEscapedChar
            r1 = 1
            if (r0 >= r1) goto L12
            r0 = 65535(0xffff, float:9.1834E-41)
            goto L16
        L12:
            r0 = r7
            int r0 = r0._maximumNonEscapedChar
        L16:
            r10 = r0
            r0 = r9
            int r0 = r0.length
            r1 = r10
            r2 = 1
            int r1 = r1 + r2
            int r0 = java.lang.Math.min(r0, r1)
            r11 = r0
            r0 = r7
            com.fasterxml.jackson.core.io.CharacterEscapes r0 = r0._characterEscapes
            r12 = r0
            r0 = 0
            r13 = r0
            r0 = 0
            r14 = r0
            r0 = r13
            r15 = r0
        L31:
            r0 = r13
            r1 = r8
            if (r0 >= r1) goto Lbe
        L37:
            r0 = r7
            char[] r0 = r0._outputBuffer
            r1 = r13
            char r0 = r0[r1]
            r16 = r0
            r0 = r16
            r1 = r11
            if (r0 >= r1) goto L55
            r0 = r9
            r1 = r16
            r0 = r0[r1]
            r14 = r0
            r0 = r14
            if (r0 == 0) goto L77
            goto L83
        L55:
            r0 = r16
            r1 = r10
            if (r0 <= r1) goto L61
            r0 = -1
            r14 = r0
            goto L83
        L61:
            r0 = r7
            r1 = r12
            r2 = r16
            com.fasterxml.jackson.core.SerializableString r1 = r1.getEscapeSequence(r2)
            r2 = r1; r1 = r0; r0 = r2; 
            r1._currentEscape = r2
            if (r0 == 0) goto L77
            r0 = -2
            r14 = r0
            goto L83
        L77:
            int r13 = r13 + 1
            r0 = r13
            r1 = r8
            if (r0 < r1) goto L37
            goto L83
        L83:
            r0 = r13
            r1 = r15
            int r0 = r0 - r1
            r17 = r0
            r0 = r17
            if (r0 <= 0) goto La7
            r0 = r7
            java.io.Writer r0 = r0._writer
            r1 = r7
            char[] r1 = r1._outputBuffer
            r2 = r15
            r3 = r17
            r0.write(r1, r2, r3)
            r0 = r13
            r1 = r8
            if (r0 < r1) goto La7
            goto Lbe
        La7:
            int r13 = r13 + 1
            r0 = r7
            r1 = r7
            char[] r1 = r1._outputBuffer
            r2 = r13
            r3 = r8
            r4 = r16
            r5 = r14
            int r0 = r0._prependOrWriteCharacterEscape(r1, r2, r3, r4, r5)
            r15 = r0
            goto L31
        Lbe:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeSegmentCustom(int):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0083 A[EDGE_INSN: B:14:0x0083->B:15:0x0083 BREAK  A[LOOP:1: B:8:0x003a->B:30:?], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:30:? A[LOOP:1: B:8:0x003a->B:30:?, LOOP_END, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void _writeStringCustom(char[] r7, int r8, int r9) throws java.io.IOException, com.fasterxml.jackson.core.JsonGenerationException {
        /*
            Method dump skipped, instructions count: 236
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.WriterBasedJsonGenerator._writeStringCustom(char[], int, int):void");
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
                char[] cArr = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                cArr[i3] = '\\';
                char[] cArr2 = this._outputBuffer;
                int i4 = this._outputTail;
                this._outputTail = i4 + 1;
                cArr2[i4] = 'n';
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
                char[] cArr = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                cArr[i3] = '\\';
                char[] cArr2 = this._outputBuffer;
                int i4 = this._outputTail;
                this._outputTail = i4 + 1;
                cArr2[i4] = 'n';
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
                char[] cArr = this._outputBuffer;
                int i3 = this._outputTail;
                this._outputTail = i3 + 1;
                cArr[i3] = '\\';
                char[] cArr2 = this._outputBuffer;
                int i4 = this._outputTail;
                this._outputTail = i4 + 1;
                cArr2[i4] = 'n';
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

    private int _readMore(InputStream in, byte[] readBuffer, int inputPtr, int inputEnd, int maxRead) throws IOException {
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

    private final void _writeNull() throws IOException {
        if (this._outputTail + 4 >= this._outputEnd) {
            _flushBuffer();
        }
        int ptr = this._outputTail;
        char[] buf = this._outputBuffer;
        buf[ptr] = 'n';
        int ptr2 = ptr + 1;
        buf[ptr2] = 'u';
        int ptr3 = ptr2 + 1;
        buf[ptr3] = 'l';
        int ptr4 = ptr3 + 1;
        buf[ptr4] = 'l';
        this._outputTail = ptr4 + 1;
    }

    private void _prependOrWriteCharacterEscape(char ch2, int escCode) throws IOException, JsonGenerationException {
        String escape;
        int ptr;
        if (escCode >= 0) {
            if (this._outputTail >= 2) {
                int ptr2 = this._outputTail - 2;
                this._outputHead = ptr2;
                this._outputBuffer[ptr2] = '\\';
                this._outputBuffer[ptr2 + 1] = (char) escCode;
                return;
            }
            char[] buf = this._entityBuffer;
            if (buf == null) {
                buf = _allocateEntityBuffer();
            }
            this._outputHead = this._outputTail;
            buf[1] = (char) escCode;
            this._writer.write(buf, 0, 2);
            return;
        }
        if (escCode != -2) {
            char[] HEX_CHARS = getHexChars();
            if (this._outputTail >= 6) {
                char[] buf2 = this._outputBuffer;
                int ptr3 = this._outputTail - 6;
                this._outputHead = ptr3;
                buf2[ptr3] = '\\';
                int ptr4 = ptr3 + 1;
                buf2[ptr4] = 'u';
                if (ch2 > 255) {
                    int hi = (ch2 >> '\b') & 255;
                    int ptr5 = ptr4 + 1;
                    buf2[ptr5] = HEX_CHARS[hi >> 4];
                    ptr = ptr5 + 1;
                    buf2[ptr] = HEX_CHARS[hi & 15];
                    ch2 = (char) (ch2 & 255);
                } else {
                    int ptr6 = ptr4 + 1;
                    buf2[ptr6] = '0';
                    ptr = ptr6 + 1;
                    buf2[ptr] = '0';
                }
                int ptr7 = ptr + 1;
                buf2[ptr7] = HEX_CHARS[ch2 >> 4];
                buf2[ptr7 + 1] = HEX_CHARS[ch2 & 15];
                return;
            }
            char[] buf3 = this._entityBuffer;
            if (buf3 == null) {
                buf3 = _allocateEntityBuffer();
            }
            this._outputHead = this._outputTail;
            if (ch2 > 255) {
                int hi2 = (ch2 >> '\b') & 255;
                int lo = ch2 & 255;
                buf3[10] = HEX_CHARS[hi2 >> 4];
                buf3[11] = HEX_CHARS[hi2 & 15];
                buf3[12] = HEX_CHARS[lo >> 4];
                buf3[13] = HEX_CHARS[lo & 15];
                this._writer.write(buf3, 8, 6);
                return;
            }
            buf3[6] = HEX_CHARS[ch2 >> 4];
            buf3[7] = HEX_CHARS[ch2 & 15];
            this._writer.write(buf3, 2, 6);
            return;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch2).getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (this._outputTail >= len) {
            int ptr8 = this._outputTail - len;
            this._outputHead = ptr8;
            escape.getChars(0, len, this._outputBuffer, ptr8);
        } else {
            this._outputHead = this._outputTail;
            this._writer.write(escape);
        }
    }

    private int _prependOrWriteCharacterEscape(char[] buffer, int ptr, int end, char ch2, int escCode) throws IOException, JsonGenerationException {
        String escape;
        int ptr2;
        if (escCode >= 0) {
            if (ptr > 1 && ptr < end) {
                ptr -= 2;
                buffer[ptr] = '\\';
                buffer[ptr + 1] = (char) escCode;
            } else {
                char[] ent = this._entityBuffer;
                if (ent == null) {
                    ent = _allocateEntityBuffer();
                }
                ent[1] = (char) escCode;
                this._writer.write(ent, 0, 2);
            }
            return ptr;
        }
        if (escCode != -2) {
            char[] HEX_CHARS = getHexChars();
            if (ptr > 5 && ptr < end) {
                int ptr3 = ptr - 6;
                int ptr4 = ptr3 + 1;
                buffer[ptr3] = '\\';
                int ptr5 = ptr4 + 1;
                buffer[ptr4] = 'u';
                if (ch2 > 255) {
                    int hi = (ch2 >> '\b') & 255;
                    int ptr6 = ptr5 + 1;
                    buffer[ptr5] = HEX_CHARS[hi >> 4];
                    ptr2 = ptr6 + 1;
                    buffer[ptr6] = HEX_CHARS[hi & 15];
                    ch2 = (char) (ch2 & 255);
                } else {
                    int ptr7 = ptr5 + 1;
                    buffer[ptr5] = '0';
                    ptr2 = ptr7 + 1;
                    buffer[ptr7] = '0';
                }
                int i = ptr2;
                int ptr8 = ptr2 + 1;
                buffer[i] = HEX_CHARS[ch2 >> 4];
                buffer[ptr8] = HEX_CHARS[ch2 & 15];
                ptr = ptr8 - 5;
            } else {
                char[] ent2 = this._entityBuffer;
                if (ent2 == null) {
                    ent2 = _allocateEntityBuffer();
                }
                this._outputHead = this._outputTail;
                if (ch2 > 255) {
                    int hi2 = (ch2 >> '\b') & 255;
                    int lo = ch2 & 255;
                    ent2[10] = HEX_CHARS[hi2 >> 4];
                    ent2[11] = HEX_CHARS[hi2 & 15];
                    ent2[12] = HEX_CHARS[lo >> 4];
                    ent2[13] = HEX_CHARS[lo & 15];
                    this._writer.write(ent2, 8, 6);
                } else {
                    ent2[6] = HEX_CHARS[ch2 >> 4];
                    ent2[7] = HEX_CHARS[ch2 & 15];
                    this._writer.write(ent2, 2, 6);
                }
            }
            return ptr;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch2).getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (ptr >= len && ptr < end) {
            ptr -= len;
            escape.getChars(0, len, buffer, ptr);
        } else {
            this._writer.write(escape);
        }
        return ptr;
    }

    private void _appendCharacterEscape(char ch2, int escCode) throws IOException, JsonGenerationException {
        String escape;
        int ptr;
        if (escCode >= 0) {
            if (this._outputTail + 2 > this._outputEnd) {
                _flushBuffer();
            }
            char[] cArr = this._outputBuffer;
            int i = this._outputTail;
            this._outputTail = i + 1;
            cArr[i] = '\\';
            char[] cArr2 = this._outputBuffer;
            int i2 = this._outputTail;
            this._outputTail = i2 + 1;
            cArr2[i2] = (char) escCode;
            return;
        }
        if (escCode != -2) {
            if (this._outputTail + 5 >= this._outputEnd) {
                _flushBuffer();
            }
            int ptr2 = this._outputTail;
            char[] buf = this._outputBuffer;
            char[] HEX_CHARS = getHexChars();
            int ptr3 = ptr2 + 1;
            buf[ptr2] = '\\';
            int ptr4 = ptr3 + 1;
            buf[ptr3] = 'u';
            if (ch2 > 255) {
                int hi = (ch2 >> '\b') & 255;
                int ptr5 = ptr4 + 1;
                buf[ptr4] = HEX_CHARS[hi >> 4];
                ptr = ptr5 + 1;
                buf[ptr5] = HEX_CHARS[hi & 15];
                ch2 = (char) (ch2 & 255);
            } else {
                int ptr6 = ptr4 + 1;
                buf[ptr4] = '0';
                ptr = ptr6 + 1;
                buf[ptr6] = '0';
            }
            int i3 = ptr;
            int ptr7 = ptr + 1;
            buf[i3] = HEX_CHARS[ch2 >> 4];
            buf[ptr7] = HEX_CHARS[ch2 & 15];
            this._outputTail = ptr7 + 1;
            return;
        }
        if (this._currentEscape == null) {
            escape = this._characterEscapes.getEscapeSequence(ch2).getValue();
        } else {
            escape = this._currentEscape.getValue();
            this._currentEscape = null;
        }
        int len = escape.length();
        if (this._outputTail + len > this._outputEnd) {
            _flushBuffer();
            if (len > this._outputEnd) {
                this._writer.write(escape);
                return;
            }
        }
        escape.getChars(0, len, this._outputBuffer, this._outputTail);
        this._outputTail += len;
    }

    private char[] _allocateEntityBuffer() {
        char[] buf = {'\\', 0, '\\', 'u', '0', '0', 0, 0, '\\', 'u'};
        this._entityBuffer = buf;
        return buf;
    }

    private char[] _allocateCopyBuffer() {
        if (this._copyBuffer == null) {
            this._copyBuffer = this._ioContext.allocNameCopyBuffer(2000);
        }
        return this._copyBuffer;
    }

    protected void _flushBuffer() throws IOException {
        int len = this._outputTail - this._outputHead;
        if (len > 0) {
            int offset = this._outputHead;
            this._outputHead = 0;
            this._outputTail = 0;
            this._writer.write(this._outputBuffer, offset, len);
        }
    }
}
