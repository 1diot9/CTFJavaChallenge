package com.fasterxml.jackson.core.json;

import com.alibaba.com.caucho.hessian.io.Hessian2Constants;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/UTF8StreamJsonParser.class */
public class UTF8StreamJsonParser extends ParserBase {
    static final byte BYTE_LF = 10;
    private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
    private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
    private static final int FEAT_MASK_NON_NUM_NUMBERS = JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS.getMask();
    private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
    private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
    private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
    private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
    private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
    private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
    protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
    protected ObjectCodec _objectCodec;
    protected final ByteQuadsCanonicalizer _symbols;
    protected int[] _quadBuffer;
    protected boolean _tokenIncomplete;
    private int _quad1;
    protected int _nameStartOffset;
    protected int _nameStartRow;
    protected int _nameStartCol;
    protected InputStream _inputStream;
    protected byte[] _inputBuffer;
    protected boolean _bufferRecyclable;

    @Deprecated
    public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
        this(ctxt, features, in, codec, sym, inputBuffer, start, end, 0, bufferRecyclable);
    }

    public UTF8StreamJsonParser(IOContext ctxt, int features, InputStream in, ObjectCodec codec, ByteQuadsCanonicalizer sym, byte[] inputBuffer, int start, int end, int bytesPreProcessed, boolean bufferRecyclable) {
        super(ctxt, features);
        this._quadBuffer = new int[16];
        this._inputStream = in;
        this._objectCodec = codec;
        this._symbols = sym;
        this._inputBuffer = inputBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._currInputRowStart = start - bytesPreProcessed;
        this._currInputProcessed = (-start) + bytesPreProcessed;
        this._bufferRecyclable = bufferRecyclable;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
        return JSON_READ_CAPABILITIES;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int releaseBuffered(OutputStream out) throws IOException {
        int count = this._inputEnd - this._inputPtr;
        if (count < 1) {
            return 0;
        }
        int origPtr = this._inputPtr;
        this._inputPtr += count;
        out.write(this._inputBuffer, origPtr, count);
        return count;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getInputSource() {
        return this._inputStream;
    }

    protected final boolean _loadMore() throws IOException {
        int space;
        if (this._inputStream == null || (space = this._inputBuffer.length) == 0) {
            return false;
        }
        int count = this._inputStream.read(this._inputBuffer, 0, space);
        if (count > 0) {
            int bufSize = this._inputEnd;
            this._currInputProcessed += bufSize;
            this._currInputRowStart -= bufSize;
            this._nameStartOffset -= bufSize;
            this._inputPtr = 0;
            this._inputEnd = count;
            return true;
        }
        _closeInput();
        if (count == 0) {
            throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
        }
        return false;
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected void _closeInput() throws IOException {
        if (this._inputStream != null) {
            if (this._ioContext.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                this._inputStream.close();
            }
            this._inputStream = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserBase
    public void _releaseBuffers() throws IOException {
        byte[] buf;
        super._releaseBuffers();
        this._symbols.release();
        if (this._bufferRecyclable && (buf = this._inputBuffer) != null && buf != NO_BYTES) {
            this._inputBuffer = NO_BYTES;
            this._ioContext.releaseReadIOBuffer(buf);
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public String getText() throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                return _finishAndReturnString();
            }
            return this._textBuffer.contentsAsString();
        }
        return _getText2(this._currToken);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getText(Writer writer) throws IOException {
        JsonToken t = this._currToken;
        if (t == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.contentsToWriter(writer);
        }
        if (t == JsonToken.FIELD_NAME) {
            String n = this._parsingContext.getCurrentName();
            writer.write(n);
            return n.length();
        }
        if (t != null) {
            if (t.isNumeric()) {
                return this._textBuffer.contentsToWriter(writer);
            }
            char[] ch2 = t.asCharArray();
            writer.write(ch2);
            return ch2.length;
        }
        return 0;
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public String getValueAsString() throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                return _finishAndReturnString();
            }
            return this._textBuffer.contentsAsString();
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return super.getValueAsString(null);
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public String getValueAsString(String defValue) throws IOException {
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                return _finishAndReturnString();
            }
            return this._textBuffer.contentsAsString();
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return getCurrentName();
        }
        return super.getValueAsString(defValue);
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public int getValueAsInt() throws IOException {
        JsonToken t = this._currToken;
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((this._numTypesValid & 1) == 0) {
                if (this._numTypesValid == 0) {
                    return _parseIntValue();
                }
                if ((this._numTypesValid & 1) == 0) {
                    convertNumberToInt();
                }
            }
            return this._numberInt;
        }
        return super.getValueAsInt(0);
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public int getValueAsInt(int defValue) throws IOException {
        JsonToken t = this._currToken;
        if (t == JsonToken.VALUE_NUMBER_INT || t == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((this._numTypesValid & 1) == 0) {
                if (this._numTypesValid == 0) {
                    return _parseIntValue();
                }
                if ((this._numTypesValid & 1) == 0) {
                    convertNumberToInt();
                }
            }
            return this._numberInt;
        }
        return super.getValueAsInt(defValue);
    }

    protected final String _getText2(JsonToken t) throws IOException {
        if (t == null) {
            return null;
        }
        switch (t.id()) {
            case 5:
                return this._parsingContext.getCurrentName();
            case 6:
            case 7:
            case 8:
                return this._textBuffer.contentsAsString();
            default:
                return t.asString();
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public char[] getTextCharacters() throws IOException {
        if (this._currToken != null) {
            switch (this._currToken.id()) {
                case 5:
                    if (!this._nameCopied) {
                        String name = this._parsingContext.getCurrentName();
                        int nameLen = name.length();
                        if (this._nameCopyBuffer == null) {
                            this._nameCopyBuffer = this._ioContext.allocNameCopyBuffer(nameLen);
                        } else if (this._nameCopyBuffer.length < nameLen) {
                            this._nameCopyBuffer = new char[nameLen];
                        }
                        name.getChars(0, nameLen, this._nameCopyBuffer, 0);
                        this._nameCopied = true;
                    }
                    return this._nameCopyBuffer;
                case 6:
                    if (this._tokenIncomplete) {
                        this._tokenIncomplete = false;
                        _finishString();
                        break;
                    }
                    break;
                case 7:
                case 8:
                    break;
                default:
                    return this._currToken.asCharArray();
            }
            return this._textBuffer.getTextBuffer();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public int getTextLength() throws IOException {
        if (this._currToken != null) {
            switch (this._currToken.id()) {
                case 5:
                    return this._parsingContext.getCurrentName().length();
                case 6:
                    if (this._tokenIncomplete) {
                        this._tokenIncomplete = false;
                        _finishString();
                        break;
                    }
                    break;
                case 7:
                case 8:
                    break;
                default:
                    return this._currToken.asCharArray().length;
            }
            return this._textBuffer.size();
        }
        return 0;
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public int getTextOffset() throws IOException {
        if (this._currToken != null) {
            switch (this._currToken.id()) {
                case 5:
                    return 0;
                case 6:
                    if (this._tokenIncomplete) {
                        this._tokenIncomplete = false;
                        _finishString();
                        break;
                    }
                    break;
                case 7:
                case 8:
                    break;
                default:
                    return 0;
            }
            return this._textBuffer.getTextOffset();
        }
        return 0;
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
        if (this._currToken != JsonToken.VALUE_STRING && (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT || this._binaryValue == null)) {
            _reportError("Current token (" + this._currToken + ") not VALUE_STRING or VALUE_EMBEDDED_OBJECT, can not access as binary");
        }
        if (this._tokenIncomplete) {
            try {
                this._binaryValue = _decodeBase64(b64variant);
                this._tokenIncomplete = false;
            } catch (IllegalArgumentException iae) {
                throw _constructError("Failed to decode VALUE_STRING as base64 (" + b64variant + "): " + iae.getMessage());
            }
        } else if (this._binaryValue == null) {
            ByteArrayBuilder builder = _getByteArrayBuilder();
            _decodeBase64(getText(), builder, b64variant);
            this._binaryValue = builder.toByteArray();
        }
        return this._binaryValue;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
        if (!this._tokenIncomplete || this._currToken != JsonToken.VALUE_STRING) {
            byte[] b = getBinaryValue(b64variant);
            out.write(b);
            return b.length;
        }
        byte[] buf = this._ioContext.allocBase64Buffer();
        try {
            int _readBinary = _readBinary(b64variant, out, buf);
            this._ioContext.releaseBase64Buffer(buf);
            return _readBinary;
        } catch (Throwable th) {
            this._ioContext.releaseBase64Buffer(buf);
            throw th;
        }
    }

    protected int _readBinary(Base64Variant b64variant, OutputStream out, byte[] buffer) throws IOException {
        int outputPtr = 0;
        int outputEnd = buffer.length - 3;
        int outputCount = 0;
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                _loadMoreGuaranteed();
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int ch2 = bArr[i] & 255;
            if (ch2 > 32) {
                int bits = b64variant.decodeBase64Char(ch2);
                if (bits < 0) {
                    if (ch2 == 34) {
                        break;
                    }
                    bits = _decodeBase64Escape(b64variant, ch2, 0);
                    if (bits < 0) {
                        continue;
                    }
                }
                if (outputPtr > outputEnd) {
                    outputCount += outputPtr;
                    out.write(buffer, 0, outputPtr);
                    outputPtr = 0;
                }
                int decodedData = bits;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                int ch3 = bArr2[i2] & 255;
                int bits2 = b64variant.decodeBase64Char(ch3);
                if (bits2 < 0) {
                    bits2 = _decodeBase64Escape(b64variant, ch3, 1);
                }
                int decodedData2 = (decodedData << 6) | bits2;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                int ch4 = bArr3[i3] & 255;
                int bits3 = b64variant.decodeBase64Char(ch4);
                if (bits3 < 0) {
                    if (bits3 != -2) {
                        if (ch4 == 34) {
                            int i4 = outputPtr;
                            outputPtr++;
                            buffer[i4] = (byte) (decodedData2 >> 4);
                            if (b64variant.requiresPaddingOnRead()) {
                                this._inputPtr--;
                                _handleBase64MissingPadding(b64variant);
                            }
                        } else {
                            bits3 = _decodeBase64Escape(b64variant, ch4, 2);
                        }
                    }
                    if (bits3 == -2) {
                        if (this._inputPtr >= this._inputEnd) {
                            _loadMoreGuaranteed();
                        }
                        byte[] bArr4 = this._inputBuffer;
                        int i5 = this._inputPtr;
                        this._inputPtr = i5 + 1;
                        int ch5 = bArr4[i5] & 255;
                        if (!b64variant.usesPaddingChar(ch5) && _decodeBase64Escape(b64variant, ch5, 3) != -2) {
                            throw reportInvalidBase64Char(b64variant, ch5, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                        }
                        int i6 = outputPtr;
                        outputPtr++;
                        buffer[i6] = (byte) (decodedData2 >> 4);
                    }
                }
                int decodedData3 = (decodedData2 << 6) | bits3;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr5 = this._inputBuffer;
                int i7 = this._inputPtr;
                this._inputPtr = i7 + 1;
                int ch6 = bArr5[i7] & 255;
                int bits4 = b64variant.decodeBase64Char(ch6);
                if (bits4 < 0) {
                    if (bits4 != -2) {
                        if (ch6 == 34) {
                            int decodedData4 = decodedData3 >> 2;
                            int i8 = outputPtr;
                            int outputPtr2 = outputPtr + 1;
                            buffer[i8] = (byte) (decodedData4 >> 8);
                            outputPtr = outputPtr2 + 1;
                            buffer[outputPtr2] = (byte) decodedData4;
                            if (b64variant.requiresPaddingOnRead()) {
                                this._inputPtr--;
                                _handleBase64MissingPadding(b64variant);
                            }
                        } else {
                            bits4 = _decodeBase64Escape(b64variant, ch6, 3);
                        }
                    }
                    if (bits4 == -2) {
                        int decodedData5 = decodedData3 >> 2;
                        int i9 = outputPtr;
                        int outputPtr3 = outputPtr + 1;
                        buffer[i9] = (byte) (decodedData5 >> 8);
                        outputPtr = outputPtr3 + 1;
                        buffer[outputPtr3] = (byte) decodedData5;
                    }
                }
                int decodedData6 = (decodedData3 << 6) | bits4;
                int i10 = outputPtr;
                int outputPtr4 = outputPtr + 1;
                buffer[i10] = (byte) (decodedData6 >> 16);
                int outputPtr5 = outputPtr4 + 1;
                buffer[outputPtr4] = (byte) (decodedData6 >> 8);
                outputPtr = outputPtr5 + 1;
                buffer[outputPtr5] = (byte) decodedData6;
            }
        }
        this._tokenIncomplete = false;
        if (outputPtr > 0) {
            outputCount += outputPtr;
            out.write(buffer, 0, outputPtr);
        }
        return outputCount;
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public JsonToken nextToken() throws IOException {
        JsonToken t;
        if (this._currToken == JsonToken.FIELD_NAME) {
            return _nextAfterName();
        }
        this._numTypesValid = 0;
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            this._currToken = null;
            return null;
        }
        this._binaryValue = null;
        if (i == 93) {
            _closeArrayScope();
            JsonToken jsonToken = JsonToken.END_ARRAY;
            this._currToken = jsonToken;
            return jsonToken;
        }
        if (i == 125) {
            _closeObjectScope();
            JsonToken jsonToken2 = JsonToken.END_OBJECT;
            this._currToken = jsonToken2;
            return jsonToken2;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
            }
            i = _skipWS();
            if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                return _closeScope(i);
            }
        }
        if (!this._parsingContext.inObject()) {
            _updateLocation();
            return _nextTokenNotInObject(i);
        }
        _updateNameLocation();
        String n = _parseName(i);
        this._parsingContext.setCurrentName(n);
        this._currToken = JsonToken.FIELD_NAME;
        int i2 = _skipColon();
        _updateLocation();
        if (i2 == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return this._currToken;
        }
        switch (i2) {
            case 43:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    t = _parseSignedNumber(false);
                    break;
                } else {
                    t = _handleUnexpectedValue(i2);
                    break;
                }
            case 45:
                t = _parseSignedNumber(true);
                break;
            case 46:
                t = _parseFloatThatStartsWithPeriod(false, false);
                break;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                t = _parseUnsignedNumber(i2);
                break;
            case 91:
                t = JsonToken.START_ARRAY;
                break;
            case 102:
                _matchFalse();
                t = JsonToken.VALUE_FALSE;
                break;
            case Opcodes.FDIV /* 110 */:
                _matchNull();
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchTrue();
                t = JsonToken.VALUE_TRUE;
                break;
            case 123:
                t = JsonToken.START_OBJECT;
                break;
            default:
                t = _handleUnexpectedValue(i2);
                break;
        }
        this._nextToken = t;
        return this._currToken;
    }

    private final JsonToken _nextTokenNotInObject(int i) throws IOException {
        if (i == 34) {
            this._tokenIncomplete = true;
            JsonToken jsonToken = JsonToken.VALUE_STRING;
            this._currToken = jsonToken;
            return jsonToken;
        }
        switch (i) {
            case 43:
                if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    JsonToken _handleUnexpectedValue = _handleUnexpectedValue(i);
                    this._currToken = _handleUnexpectedValue;
                    return _handleUnexpectedValue;
                }
                JsonToken _parseSignedNumber = _parseSignedNumber(false);
                this._currToken = _parseSignedNumber;
                return _parseSignedNumber;
            case 45:
                JsonToken _parseSignedNumber2 = _parseSignedNumber(true);
                this._currToken = _parseSignedNumber2;
                return _parseSignedNumber2;
            case 46:
                JsonToken _parseFloatThatStartsWithPeriod = _parseFloatThatStartsWithPeriod(false, false);
                this._currToken = _parseFloatThatStartsWithPeriod;
                return _parseFloatThatStartsWithPeriod;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                JsonToken _parseUnsignedNumber = _parseUnsignedNumber(i);
                this._currToken = _parseUnsignedNumber;
                return _parseUnsignedNumber;
            case 91:
                createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                JsonToken jsonToken2 = JsonToken.START_ARRAY;
                this._currToken = jsonToken2;
                return jsonToken2;
            case 102:
                _matchFalse();
                JsonToken jsonToken3 = JsonToken.VALUE_FALSE;
                this._currToken = jsonToken3;
                return jsonToken3;
            case Opcodes.FDIV /* 110 */:
                _matchNull();
                JsonToken jsonToken4 = JsonToken.VALUE_NULL;
                this._currToken = jsonToken4;
                return jsonToken4;
            case 116:
                _matchTrue();
                JsonToken jsonToken5 = JsonToken.VALUE_TRUE;
                this._currToken = jsonToken5;
                return jsonToken5;
            case 123:
                createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                JsonToken jsonToken6 = JsonToken.START_OBJECT;
                this._currToken = jsonToken6;
                return jsonToken6;
            default:
                JsonToken _handleUnexpectedValue2 = _handleUnexpectedValue(i);
                this._currToken = _handleUnexpectedValue2;
                return _handleUnexpectedValue2;
        }
    }

    private final JsonToken _nextAfterName() throws IOException {
        this._nameCopied = false;
        JsonToken t = this._nextToken;
        this._nextToken = null;
        if (t == JsonToken.START_ARRAY) {
            createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (t == JsonToken.START_OBJECT) {
            createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        this._currToken = t;
        return t;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void finishToken() throws IOException {
        if (this._tokenIncomplete) {
            this._tokenIncomplete = false;
            _finishString();
        }
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean nextFieldName(SerializableString str) throws IOException {
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            _nextAfterName();
            return false;
        }
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            this._currToken = null;
            return false;
        }
        this._binaryValue = null;
        if (i == 93) {
            _closeArrayScope();
            this._currToken = JsonToken.END_ARRAY;
            return false;
        }
        if (i == 125) {
            _closeObjectScope();
            this._currToken = JsonToken.END_OBJECT;
            return false;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
            }
            i = _skipWS();
            if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                _closeScope(i);
                return false;
            }
        }
        if (!this._parsingContext.inObject()) {
            _updateLocation();
            _nextTokenNotInObject(i);
            return false;
        }
        _updateNameLocation();
        if (i == 34) {
            byte[] nameBytes = str.asQuotedUTF8();
            int len = nameBytes.length;
            if (this._inputPtr + len + 4 < this._inputEnd) {
                int end = this._inputPtr + len;
                if (this._inputBuffer[end] == 34) {
                    int offset = 0;
                    int ptr = this._inputPtr;
                    while (ptr != end) {
                        if (nameBytes[offset] == this._inputBuffer[ptr]) {
                            offset++;
                            ptr++;
                        }
                    }
                    this._parsingContext.setCurrentName(str.getValue());
                    _isNextTokenNameYes(_skipColonFast(ptr + 1));
                    return true;
                }
            }
        }
        return _isNextTokenNameMaybe(i, str);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String nextFieldName() throws IOException {
        JsonToken t;
        this._numTypesValid = 0;
        if (this._currToken == JsonToken.FIELD_NAME) {
            _nextAfterName();
            return null;
        }
        if (this._tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            this._currToken = null;
            return null;
        }
        this._binaryValue = null;
        if (i == 93) {
            _closeArrayScope();
            this._currToken = JsonToken.END_ARRAY;
            return null;
        }
        if (i == 125) {
            _closeObjectScope();
            this._currToken = JsonToken.END_OBJECT;
            return null;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
            }
            i = _skipWS();
            if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                _closeScope(i);
                return null;
            }
        }
        if (!this._parsingContext.inObject()) {
            _updateLocation();
            _nextTokenNotInObject(i);
            return null;
        }
        _updateNameLocation();
        String nameStr = _parseName(i);
        this._parsingContext.setCurrentName(nameStr);
        this._currToken = JsonToken.FIELD_NAME;
        int i2 = _skipColon();
        _updateLocation();
        if (i2 == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return nameStr;
        }
        switch (i2) {
            case 43:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    t = _parseSignedNumber(false);
                    break;
                } else {
                    t = _handleUnexpectedValue(i2);
                    break;
                }
            case 45:
                t = _parseSignedNumber(true);
                break;
            case 46:
                t = _parseFloatThatStartsWithPeriod(false, false);
                break;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                t = _parseUnsignedNumber(i2);
                break;
            case 91:
                t = JsonToken.START_ARRAY;
                break;
            case 102:
                _matchFalse();
                t = JsonToken.VALUE_FALSE;
                break;
            case Opcodes.FDIV /* 110 */:
                _matchNull();
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchTrue();
                t = JsonToken.VALUE_TRUE;
                break;
            case 123:
                t = JsonToken.START_OBJECT;
                break;
            default:
                t = _handleUnexpectedValue(i2);
                break;
        }
        this._nextToken = t;
        return nameStr;
    }

    private final int _skipColonFast(int ptr) throws IOException {
        int ptr2 = ptr + 1;
        int i = this._inputBuffer[ptr];
        if (i == 58) {
            int ptr3 = ptr2 + 1;
            byte b = this._inputBuffer[ptr2];
            if (b > 32) {
                if (b != 47 && b != 35) {
                    this._inputPtr = ptr3;
                    return b;
                }
            } else if (b == 32 || b == 9) {
                ptr3++;
                byte b2 = this._inputBuffer[ptr3];
                if (b2 > 32 && b2 != 47 && b2 != 35) {
                    this._inputPtr = ptr3;
                    return b2;
                }
            }
            this._inputPtr = ptr3 - 1;
            return _skipColon2(true);
        }
        if (i == 32 || i == 9) {
            ptr2++;
            i = this._inputBuffer[ptr2];
        }
        if (i == 58) {
            int i2 = ptr2;
            int ptr4 = ptr2 + 1;
            byte b3 = this._inputBuffer[i2];
            if (b3 > 32) {
                if (b3 != 47 && b3 != 35) {
                    this._inputPtr = ptr4;
                    return b3;
                }
            } else if (b3 == 32 || b3 == 9) {
                ptr4++;
                byte b4 = this._inputBuffer[ptr4];
                if (b4 > 32 && b4 != 47 && b4 != 35) {
                    this._inputPtr = ptr4;
                    return b4;
                }
            }
            this._inputPtr = ptr4 - 1;
            return _skipColon2(true);
        }
        this._inputPtr = ptr2 - 1;
        return _skipColon2(false);
    }

    private final void _isNextTokenNameYes(int i) throws IOException {
        this._currToken = JsonToken.FIELD_NAME;
        _updateLocation();
        switch (i) {
            case 34:
                this._tokenIncomplete = true;
                this._nextToken = JsonToken.VALUE_STRING;
                return;
            case 43:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    this._nextToken = _parseSignedNumber(false);
                    return;
                } else {
                    this._nextToken = _handleUnexpectedValue(i);
                    return;
                }
            case 45:
                this._nextToken = _parseSignedNumber(true);
                return;
            case 46:
                this._nextToken = _parseFloatThatStartsWithPeriod(false, false);
                return;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                this._nextToken = _parseUnsignedNumber(i);
                return;
            case 91:
                this._nextToken = JsonToken.START_ARRAY;
                return;
            case 102:
                _matchFalse();
                this._nextToken = JsonToken.VALUE_FALSE;
                return;
            case Opcodes.FDIV /* 110 */:
                _matchNull();
                this._nextToken = JsonToken.VALUE_NULL;
                return;
            case 116:
                _matchTrue();
                this._nextToken = JsonToken.VALUE_TRUE;
                return;
            case 123:
                this._nextToken = JsonToken.START_OBJECT;
                return;
            default:
                this._nextToken = _handleUnexpectedValue(i);
                return;
        }
    }

    private final boolean _isNextTokenNameMaybe(int i, SerializableString str) throws IOException {
        JsonToken t;
        String n = _parseName(i);
        this._parsingContext.setCurrentName(n);
        boolean match = n.equals(str.getValue());
        this._currToken = JsonToken.FIELD_NAME;
        int i2 = _skipColon();
        _updateLocation();
        if (i2 == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return match;
        }
        switch (i2) {
            case 43:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    t = _parseSignedNumber(false);
                    break;
                } else {
                    t = _handleUnexpectedValue(i2);
                    break;
                }
            case 45:
                t = _parseSignedNumber(true);
                break;
            case 46:
                t = _parseFloatThatStartsWithPeriod(false, false);
                break;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                t = _parseUnsignedNumber(i2);
                break;
            case 91:
                t = JsonToken.START_ARRAY;
                break;
            case 102:
                _matchFalse();
                t = JsonToken.VALUE_FALSE;
                break;
            case Opcodes.FDIV /* 110 */:
                _matchNull();
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchTrue();
                t = JsonToken.VALUE_TRUE;
                break;
            case 123:
                t = JsonToken.START_OBJECT;
                break;
            default:
                t = _handleUnexpectedValue(i2);
                break;
        }
        this._nextToken = t;
        return match;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public String nextTextValue() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_STRING) {
                if (this._tokenIncomplete) {
                    this._tokenIncomplete = false;
                    return _finishAndReturnString();
                }
                return this._textBuffer.contentsAsString();
            }
            if (t == JsonToken.START_ARRAY) {
                createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
            if (t == JsonToken.START_OBJECT) {
                createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
            return null;
        }
        if (nextToken() == JsonToken.VALUE_STRING) {
            return getText();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int nextIntValue(int defaultValue) throws IOException {
        if (this._currToken != JsonToken.FIELD_NAME) {
            return nextToken() == JsonToken.VALUE_NUMBER_INT ? getIntValue() : defaultValue;
        }
        this._nameCopied = false;
        JsonToken t = this._nextToken;
        this._nextToken = null;
        this._currToken = t;
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return getIntValue();
        }
        if (t == JsonToken.START_ARRAY) {
            createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (t == JsonToken.START_OBJECT) {
            createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        return defaultValue;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public long nextLongValue(long defaultValue) throws IOException {
        if (this._currToken != JsonToken.FIELD_NAME) {
            return nextToken() == JsonToken.VALUE_NUMBER_INT ? getLongValue() : defaultValue;
        }
        this._nameCopied = false;
        JsonToken t = this._nextToken;
        this._nextToken = null;
        this._currToken = t;
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return getLongValue();
        }
        if (t == JsonToken.START_ARRAY) {
            createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
        } else if (t == JsonToken.START_OBJECT) {
            createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
        }
        return defaultValue;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Boolean nextBooleanValue() throws IOException {
        if (this._currToken == JsonToken.FIELD_NAME) {
            this._nameCopied = false;
            JsonToken t = this._nextToken;
            this._nextToken = null;
            this._currToken = t;
            if (t == JsonToken.VALUE_TRUE) {
                return Boolean.TRUE;
            }
            if (t == JsonToken.VALUE_FALSE) {
                return Boolean.FALSE;
            }
            if (t == JsonToken.START_ARRAY) {
                createChildArrayContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
            if (t == JsonToken.START_OBJECT) {
                createChildObjectContext(this._tokenInputRow, this._tokenInputCol);
                return null;
            }
            return null;
        }
        JsonToken t2 = nextToken();
        if (t2 == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
        }
        if (t2 == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Deprecated
    protected final JsonToken _parseFloatThatStartsWithPeriod() throws IOException {
        return _parseFloatThatStartsWithPeriod(false, false);
    }

    protected final JsonToken _parseFloatThatStartsWithPeriod(boolean neg, boolean hasSign) throws IOException {
        if (!isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
            return _handleUnexpectedValue(46);
        }
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;
        if (neg) {
            outPtr = 0 + 1;
            outBuf[0] = '-';
        }
        return _parseFloat(outBuf, outPtr, 46, neg, 0);
    }

    protected JsonToken _parseUnsignedNumber(int c) throws IOException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        if (c == 48) {
            c = _verifyNoLeadingZeroes();
        }
        outBuf[0] = (char) c;
        int intLen = 1;
        int outPtr = 1;
        int end = Math.min(this._inputEnd, (this._inputPtr + outBuf.length) - 1);
        while (this._inputPtr < end) {
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int c2 = bArr[i] & 255;
            if (c2 >= 48 && c2 <= 57) {
                intLen++;
                int i2 = outPtr;
                outPtr++;
                outBuf[i2] = (char) c2;
            } else {
                if (c2 == 46 || c2 == 101 || c2 == 69) {
                    return _parseFloat(outBuf, outPtr, c2, false, intLen);
                }
                this._inputPtr--;
                this._textBuffer.setCurrentLength(outPtr);
                if (this._parsingContext.inRoot()) {
                    _verifyRootSpace(c2);
                }
                return resetInt(false, intLen);
            }
        }
        return _parseNumber2(outBuf, outPtr, false, intLen);
    }

    private final JsonToken _parseSignedNumber(boolean negative) throws IOException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;
        if (negative) {
            outPtr = 0 + 1;
            outBuf[0] = '-';
        }
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i] & 255;
        if (c <= 48) {
            if (c != 48) {
                if (c == 46) {
                    return _parseFloatThatStartsWithPeriod(negative, true);
                }
                return _handleInvalidNumberStart(c, negative, true);
            }
            c = _verifyNoLeadingZeroes();
        } else if (c > 57) {
            return _handleInvalidNumberStart(c, negative, true);
        }
        int i2 = outPtr;
        int outPtr2 = outPtr + 1;
        outBuf[i2] = (char) c;
        int intLen = 1;
        int end = Math.min(this._inputEnd, (this._inputPtr + outBuf.length) - outPtr2);
        while (this._inputPtr < end) {
            byte[] bArr2 = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int c2 = bArr2[i3] & 255;
            if (c2 >= 48 && c2 <= 57) {
                intLen++;
                int i4 = outPtr2;
                outPtr2++;
                outBuf[i4] = (char) c2;
            } else {
                if (c2 == 46 || c2 == 101 || c2 == 69) {
                    return _parseFloat(outBuf, outPtr2, c2, negative, intLen);
                }
                this._inputPtr--;
                this._textBuffer.setCurrentLength(outPtr2);
                if (this._parsingContext.inRoot()) {
                    _verifyRootSpace(c2);
                }
                return resetInt(negative, intLen);
            }
        }
        return _parseNumber2(outBuf, outPtr2, negative, intLen);
    }

    private final JsonToken _parseNumber2(char[] outBuf, int outPtr, boolean negative, int intPartLength) throws IOException {
        int c;
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                this._textBuffer.setCurrentLength(outPtr);
                return resetInt(negative, intPartLength);
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            c = bArr[i] & 255;
            if (c > 57 || c < 48) {
                break;
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int i2 = outPtr;
            outPtr++;
            outBuf[i2] = (char) c;
            intPartLength++;
        }
        if (c == 46 || c == 101 || c == 69) {
            return _parseFloat(outBuf, outPtr, c, negative, intPartLength);
        }
        this._inputPtr--;
        this._textBuffer.setCurrentLength(outPtr);
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace(this._inputBuffer[this._inputPtr] & 255);
        }
        return resetInt(negative, intPartLength);
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0050, code lost:            if (r5 == 48) goto L19;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x005b, code lost:            if (r4._inputPtr < r4._inputEnd) goto L23;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0062, code lost:            if (_loadMore() == false) goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0065, code lost:            r5 = r4._inputBuffer[r4._inputPtr] & 255;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0076, code lost:            if (r5 < 48) goto L35;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x007c, code lost:            if (r5 <= 57) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0082, code lost:            r4._inputPtr++;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x008f, code lost:            if (r5 == 48) goto L38;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:?, code lost:            return 48;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x007f, code lost:            return 48;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0096, code lost:            return r5;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final int _verifyNoLeadingZeroes() throws java.io.IOException {
        /*
            r4 = this;
            r0 = r4
            int r0 = r0._inputPtr
            r1 = r4
            int r1 = r1._inputEnd
            if (r0 < r1) goto L15
            r0 = r4
            boolean r0 = r0._loadMore()
            if (r0 != 0) goto L15
            r0 = 48
            return r0
        L15:
            r0 = r4
            byte[] r0 = r0._inputBuffer
            r1 = r4
            int r1 = r1._inputPtr
            r0 = r0[r1]
            r1 = 255(0xff, float:3.57E-43)
            r0 = r0 & r1
            r5 = r0
            r0 = r5
            r1 = 48
            if (r0 < r1) goto L2f
            r0 = r5
            r1 = 57
            if (r0 <= r1) goto L32
        L2f:
            r0 = 48
            return r0
        L32:
            r0 = r4
            int r0 = r0._features
            int r1 = com.fasterxml.jackson.core.json.UTF8StreamJsonParser.FEAT_MASK_LEADING_ZEROS
            r0 = r0 & r1
            if (r0 != 0) goto L43
            r0 = r4
            java.lang.String r1 = "Leading zeroes not allowed"
            r0.reportInvalidNumber(r1)
        L43:
            r0 = r4
            r1 = r0
            int r1 = r1._inputPtr
            r2 = 1
            int r1 = r1 + r2
            r0._inputPtr = r1
            r0 = r5
            r1 = 48
            if (r0 != r1) goto L95
        L53:
            r0 = r4
            int r0 = r0._inputPtr
            r1 = r4
            int r1 = r1._inputEnd
            if (r0 < r1) goto L65
            r0 = r4
            boolean r0 = r0._loadMore()
            if (r0 == 0) goto L95
        L65:
            r0 = r4
            byte[] r0 = r0._inputBuffer
            r1 = r4
            int r1 = r1._inputPtr
            r0 = r0[r1]
            r1 = 255(0xff, float:3.57E-43)
            r0 = r0 & r1
            r5 = r0
            r0 = r5
            r1 = 48
            if (r0 < r1) goto L7f
            r0 = r5
            r1 = 57
            if (r0 <= r1) goto L82
        L7f:
            r0 = 48
            return r0
        L82:
            r0 = r4
            r1 = r0
            int r1 = r1._inputPtr
            r2 = 1
            int r1 = r1 + r2
            r0._inputPtr = r1
            r0 = r5
            r1 = 48
            if (r0 == r1) goto L53
            goto L95
        L95:
            r0 = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser._verifyNoLeadingZeroes():int");
    }

    private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
        int fractLen = 0;
        boolean eof = false;
        if (c == 46) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) c;
            while (true) {
                if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                    eof = true;
                    break;
                }
                byte[] bArr = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                c = bArr[i2] & 255;
                if (c < 48 || c > 57) {
                    break;
                }
                fractLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int i3 = outPtr;
                outPtr++;
                outBuf[i3] = (char) c;
            }
            if (fractLen == 0 && !isEnabled(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
                _reportUnexpectedNumberChar(c, "Decimal point not followed by a digit");
            }
        }
        int expLen = 0;
        if (c == 101 || c == 69) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int i4 = outPtr;
            outPtr++;
            outBuf[i4] = (char) c;
            if (this._inputPtr >= this._inputEnd) {
                _loadMoreGuaranteed();
            }
            byte[] bArr2 = this._inputBuffer;
            int i5 = this._inputPtr;
            this._inputPtr = i5 + 1;
            c = bArr2[i5] & 255;
            if (c == 45 || c == 43) {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int i6 = outPtr;
                outPtr++;
                outBuf[i6] = (char) c;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr3 = this._inputBuffer;
                int i7 = this._inputPtr;
                this._inputPtr = i7 + 1;
                c = bArr3[i7] & 255;
            }
            while (true) {
                if (c < 48 || c > 57) {
                    break;
                }
                expLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int i8 = outPtr;
                outPtr++;
                outBuf[i8] = (char) c;
                if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                    eof = true;
                    break;
                }
                byte[] bArr4 = this._inputBuffer;
                int i9 = this._inputPtr;
                this._inputPtr = i9 + 1;
                c = bArr4[i9] & 255;
            }
            if (expLen == 0) {
                _reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
            }
        }
        if (!eof) {
            this._inputPtr--;
            if (this._parsingContext.inRoot()) {
                _verifyRootSpace(c);
            }
        }
        this._textBuffer.setCurrentLength(outPtr);
        return resetFloat(negative, integerPartLength, fractLen, expLen);
    }

    private final void _verifyRootSpace(int ch2) throws IOException {
        this._inputPtr++;
        switch (ch2) {
            case 9:
            case 32:
                return;
            case 10:
                this._currInputRow++;
                this._currInputRowStart = this._inputPtr;
                return;
            case 13:
                this._inputPtr--;
                return;
            default:
                _reportMissingRootWS(ch2);
                return;
        }
    }

    protected final String _parseName(int i) throws IOException {
        if (i != 34) {
            return _handleOddName(i);
        }
        if (this._inputPtr + 13 > this._inputEnd) {
            return slowParseName();
        }
        byte[] input = this._inputBuffer;
        int[] codes = _icLatin1;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        int q = input[i2] & 255;
        if (codes[q] == 0) {
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int i4 = input[i3] & 255;
            if (codes[i4] == 0) {
                int q2 = (q << 8) | i4;
                int i5 = this._inputPtr;
                this._inputPtr = i5 + 1;
                int i6 = input[i5] & 255;
                if (codes[i6] == 0) {
                    int q3 = (q2 << 8) | i6;
                    int i7 = this._inputPtr;
                    this._inputPtr = i7 + 1;
                    int i8 = input[i7] & 255;
                    if (codes[i8] == 0) {
                        int q4 = (q3 << 8) | i8;
                        int i9 = this._inputPtr;
                        this._inputPtr = i9 + 1;
                        int i10 = input[i9] & 255;
                        if (codes[i10] == 0) {
                            this._quad1 = q4;
                            return parseMediumName(i10);
                        }
                        if (i10 == 34) {
                            return findName(q4, 4);
                        }
                        return parseName(q4, i10, 4);
                    }
                    if (i8 == 34) {
                        return findName(q3, 3);
                    }
                    return parseName(q3, i8, 3);
                }
                if (i6 == 34) {
                    return findName(q2, 2);
                }
                return parseName(q2, i6, 2);
            }
            if (i4 == 34) {
                return findName(q, 1);
            }
            return parseName(q, i4, 1);
        }
        if (q == 34) {
            return "";
        }
        return parseName(0, q, 0);
    }

    protected final String parseMediumName(int q2) throws IOException {
        byte[] input = this._inputBuffer;
        int[] codes = _icLatin1;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = input[i] & 255;
        if (codes[i2] != 0) {
            if (i2 == 34) {
                return findName(this._quad1, q2, 1);
            }
            return parseName(this._quad1, q2, i2, 1);
        }
        int q22 = (q2 << 8) | i2;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int i4 = input[i3] & 255;
        if (codes[i4] != 0) {
            if (i4 == 34) {
                return findName(this._quad1, q22, 2);
            }
            return parseName(this._quad1, q22, i4, 2);
        }
        int q23 = (q22 << 8) | i4;
        int i5 = this._inputPtr;
        this._inputPtr = i5 + 1;
        int i6 = input[i5] & 255;
        if (codes[i6] != 0) {
            if (i6 == 34) {
                return findName(this._quad1, q23, 3);
            }
            return parseName(this._quad1, q23, i6, 3);
        }
        int q24 = (q23 << 8) | i6;
        int i7 = this._inputPtr;
        this._inputPtr = i7 + 1;
        int i8 = input[i7] & 255;
        if (codes[i8] != 0) {
            if (i8 == 34) {
                return findName(this._quad1, q24, 4);
            }
            return parseName(this._quad1, q24, i8, 4);
        }
        return parseMediumName2(i8, q24);
    }

    protected final String parseMediumName2(int q3, int q2) throws IOException {
        byte[] input = this._inputBuffer;
        int[] codes = _icLatin1;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = input[i] & 255;
        if (codes[i2] != 0) {
            if (i2 == 34) {
                return findName(this._quad1, q2, q3, 1);
            }
            return parseName(this._quad1, q2, q3, i2, 1);
        }
        int q32 = (q3 << 8) | i2;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        int i4 = input[i3] & 255;
        if (codes[i4] != 0) {
            if (i4 == 34) {
                return findName(this._quad1, q2, q32, 2);
            }
            return parseName(this._quad1, q2, q32, i4, 2);
        }
        int q33 = (q32 << 8) | i4;
        int i5 = this._inputPtr;
        this._inputPtr = i5 + 1;
        int i6 = input[i5] & 255;
        if (codes[i6] != 0) {
            if (i6 == 34) {
                return findName(this._quad1, q2, q33, 3);
            }
            return parseName(this._quad1, q2, q33, i6, 3);
        }
        int q34 = (q33 << 8) | i6;
        int i7 = this._inputPtr;
        this._inputPtr = i7 + 1;
        int i8 = input[i7] & 255;
        if (codes[i8] != 0) {
            if (i8 == 34) {
                return findName(this._quad1, q2, q34, 4);
            }
            return parseName(this._quad1, q2, q34, i8, 4);
        }
        return parseLongName(i8, q2, q34);
    }

    protected final String parseLongName(int q, int q2, int q3) throws IOException {
        this._quadBuffer[0] = this._quad1;
        this._quadBuffer[1] = q2;
        this._quadBuffer[2] = q3;
        byte[] input = this._inputBuffer;
        int[] codes = _icLatin1;
        int qlen = 3;
        while (this._inputPtr + 4 <= this._inputEnd) {
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = input[i] & 255;
            if (codes[i2] != 0) {
                if (i2 == 34) {
                    return findName(this._quadBuffer, qlen, q, 1);
                }
                return parseEscapedName(this._quadBuffer, qlen, q, i2, 1);
            }
            int q4 = (q << 8) | i2;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int i4 = input[i3] & 255;
            if (codes[i4] != 0) {
                if (i4 == 34) {
                    return findName(this._quadBuffer, qlen, q4, 2);
                }
                return parseEscapedName(this._quadBuffer, qlen, q4, i4, 2);
            }
            int q5 = (q4 << 8) | i4;
            int i5 = this._inputPtr;
            this._inputPtr = i5 + 1;
            int i6 = input[i5] & 255;
            if (codes[i6] != 0) {
                if (i6 == 34) {
                    return findName(this._quadBuffer, qlen, q5, 3);
                }
                return parseEscapedName(this._quadBuffer, qlen, q5, i6, 3);
            }
            int q6 = (q5 << 8) | i6;
            int i7 = this._inputPtr;
            this._inputPtr = i7 + 1;
            int i8 = input[i7] & 255;
            if (codes[i8] != 0) {
                if (i8 == 34) {
                    return findName(this._quadBuffer, qlen, q6, 4);
                }
                return parseEscapedName(this._quadBuffer, qlen, q6, i8, 4);
            }
            if (qlen >= this._quadBuffer.length) {
                this._quadBuffer = growArrayBy(this._quadBuffer, qlen);
            }
            int i9 = qlen;
            qlen++;
            this._quadBuffer[i9] = q6;
            q = i8;
        }
        return parseEscapedName(this._quadBuffer, qlen, 0, q, 0);
    }

    protected String slowParseName() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(": was expecting closing '\"' for name", JsonToken.FIELD_NAME);
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & 255;
        if (i2 == 34) {
            return "";
        }
        return parseEscapedName(this._quadBuffer, 0, 0, i2, 0);
    }

    private final String parseName(int q1, int ch2, int lastQuadBytes) throws IOException {
        return parseEscapedName(this._quadBuffer, 0, q1, ch2, lastQuadBytes);
    }

    private final String parseName(int q1, int q2, int ch2, int lastQuadBytes) throws IOException {
        this._quadBuffer[0] = q1;
        return parseEscapedName(this._quadBuffer, 1, q2, ch2, lastQuadBytes);
    }

    private final String parseName(int q1, int q2, int q3, int ch2, int lastQuadBytes) throws IOException {
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q2;
        return parseEscapedName(this._quadBuffer, 2, q3, ch2, lastQuadBytes);
    }

    protected final String parseEscapedName(int[] quads, int qlen, int currQuad, int ch2, int currQuadBytes) throws IOException {
        int[] codes = _icLatin1;
        while (true) {
            if (codes[ch2] != 0) {
                if (ch2 == 34) {
                    break;
                }
                if (ch2 != 92) {
                    _throwUnquotedSpace(ch2, "name");
                } else {
                    ch2 = _decodeEscaped();
                }
                if (ch2 > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            int[] growArrayBy = growArrayBy(quads, quads.length);
                            quads = growArrayBy;
                            this._quadBuffer = growArrayBy;
                        }
                        int i = qlen;
                        qlen++;
                        quads[i] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    if (ch2 < 2048) {
                        currQuad = (currQuad << 8) | 192 | (ch2 >> 6);
                        currQuadBytes++;
                    } else {
                        int currQuad2 = (currQuad << 8) | 224 | (ch2 >> 12);
                        int currQuadBytes2 = currQuadBytes + 1;
                        if (currQuadBytes2 >= 4) {
                            if (qlen >= quads.length) {
                                int[] growArrayBy2 = growArrayBy(quads, quads.length);
                                quads = growArrayBy2;
                                this._quadBuffer = growArrayBy2;
                            }
                            int i2 = qlen;
                            qlen++;
                            quads[i2] = currQuad2;
                            currQuad2 = 0;
                            currQuadBytes2 = 0;
                        }
                        currQuad = (currQuad2 << 8) | 128 | ((ch2 >> 6) & 63);
                        currQuadBytes = currQuadBytes2 + 1;
                    }
                    ch2 = 128 | (ch2 & 63);
                }
            }
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch2;
            } else {
                if (qlen >= quads.length) {
                    int[] growArrayBy3 = growArrayBy(quads, quads.length);
                    quads = growArrayBy3;
                    this._quadBuffer = growArrayBy3;
                }
                int i3 = qlen;
                qlen++;
                quads[i3] = currQuad;
                currQuad = ch2;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }
            byte[] bArr = this._inputBuffer;
            int i4 = this._inputPtr;
            this._inputPtr = i4 + 1;
            ch2 = bArr[i4] & 255;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                int[] growArrayBy4 = growArrayBy(quads, quads.length);
                quads = growArrayBy4;
                this._quadBuffer = growArrayBy4;
            }
            int i5 = qlen;
            qlen++;
            quads[i5] = _padLastQuad(currQuad, currQuadBytes);
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            name = addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected String _handleOddName(int ch2) throws IOException {
        if (ch2 == 39 && (this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
            return _parseAposName();
        }
        if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
            char c = (char) _decodeCharForError(ch2);
            _reportUnexpectedChar(c, "was expecting double-quote to start field name");
        }
        int[] codes = CharTypes.getInputCodeUtf8JsNames();
        if (codes[ch2] != 0) {
            _reportUnexpectedChar(ch2, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        int[] quads = this._quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;
        while (true) {
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch2;
            } else {
                if (qlen >= quads.length) {
                    int[] growArrayBy = growArrayBy(quads, quads.length);
                    quads = growArrayBy;
                    this._quadBuffer = growArrayBy;
                }
                int i = qlen;
                qlen++;
                quads[i] = currQuad;
                currQuad = ch2;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }
            ch2 = this._inputBuffer[this._inputPtr] & 255;
            if (codes[ch2] != 0) {
                break;
            }
            this._inputPtr++;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                int[] growArrayBy2 = growArrayBy(quads, quads.length);
                quads = growArrayBy2;
                this._quadBuffer = growArrayBy2;
            }
            int i2 = qlen;
            qlen++;
            quads[i2] = currQuad;
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            name = addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    protected String _parseAposName() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(": was expecting closing ''' for field name", JsonToken.FIELD_NAME);
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int ch2 = bArr[i] & 255;
        if (ch2 == 39) {
            return "";
        }
        int[] quads = this._quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;
        int[] codes = _icLatin1;
        while (ch2 != 39) {
            if (codes[ch2] != 0 && ch2 != 34) {
                if (ch2 != 92) {
                    _throwUnquotedSpace(ch2, "name");
                } else {
                    ch2 = _decodeEscaped();
                }
                if (ch2 > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            int[] growArrayBy = growArrayBy(quads, quads.length);
                            quads = growArrayBy;
                            this._quadBuffer = growArrayBy;
                        }
                        int i2 = qlen;
                        qlen++;
                        quads[i2] = currQuad;
                        currQuad = 0;
                        currQuadBytes = 0;
                    }
                    if (ch2 < 2048) {
                        currQuad = (currQuad << 8) | 192 | (ch2 >> 6);
                        currQuadBytes++;
                    } else {
                        int currQuad2 = (currQuad << 8) | 224 | (ch2 >> 12);
                        int currQuadBytes2 = currQuadBytes + 1;
                        if (currQuadBytes2 >= 4) {
                            if (qlen >= quads.length) {
                                int[] growArrayBy2 = growArrayBy(quads, quads.length);
                                quads = growArrayBy2;
                                this._quadBuffer = growArrayBy2;
                            }
                            int i3 = qlen;
                            qlen++;
                            quads[i3] = currQuad2;
                            currQuad2 = 0;
                            currQuadBytes2 = 0;
                        }
                        currQuad = (currQuad2 << 8) | 128 | ((ch2 >> 6) & 63);
                        currQuadBytes = currQuadBytes2 + 1;
                    }
                    ch2 = 128 | (ch2 & 63);
                }
            }
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch2;
            } else {
                if (qlen >= quads.length) {
                    int[] growArrayBy3 = growArrayBy(quads, quads.length);
                    quads = growArrayBy3;
                    this._quadBuffer = growArrayBy3;
                }
                int i4 = qlen;
                qlen++;
                quads[i4] = currQuad;
                currQuad = ch2;
                currQuadBytes = 1;
            }
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
            }
            byte[] bArr2 = this._inputBuffer;
            int i5 = this._inputPtr;
            this._inputPtr = i5 + 1;
            ch2 = bArr2[i5] & 255;
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                int[] growArrayBy4 = growArrayBy(quads, quads.length);
                quads = growArrayBy4;
                this._quadBuffer = growArrayBy4;
            }
            int i6 = qlen;
            qlen++;
            quads[i6] = _padLastQuad(currQuad, currQuadBytes);
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            name = addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    private final String findName(int q1, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int q12 = _padLastQuad(q1, lastQuadBytes);
        String name = this._symbols.findName(q12);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q12;
        return addName(this._quadBuffer, 1, lastQuadBytes);
    }

    private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int q22 = _padLastQuad(q2, lastQuadBytes);
        String name = this._symbols.findName(q1, q22);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q22;
        return addName(this._quadBuffer, 2, lastQuadBytes);
    }

    private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int q32 = _padLastQuad(q3, lastQuadBytes);
        String name = this._symbols.findName(q1, q2, q32);
        if (name != null) {
            return name;
        }
        int[] quads = this._quadBuffer;
        quads[0] = q1;
        quads[1] = q2;
        quads[2] = _padLastQuad(q32, lastQuadBytes);
        return addName(quads, 3, lastQuadBytes);
    }

    private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        if (qlen >= quads.length) {
            int[] growArrayBy = growArrayBy(quads, quads.length);
            quads = growArrayBy;
            this._quadBuffer = growArrayBy;
        }
        int qlen2 = qlen + 1;
        quads[qlen] = _padLastQuad(lastQuad, lastQuadBytes);
        String name = this._symbols.findName(quads, qlen2);
        if (name == null) {
            return addName(quads, qlen2, lastQuadBytes);
        }
        return name;
    }

    private final String addName(int[] quads, int qlen, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int lastQuad;
        int ch2;
        int needed;
        int byteLen = ((qlen << 2) - 4) + lastQuadBytes;
        if (lastQuadBytes < 4) {
            lastQuad = quads[qlen - 1];
            quads[qlen - 1] = lastQuad << ((4 - lastQuadBytes) << 3);
        } else {
            lastQuad = 0;
        }
        char[] cbuf = this._textBuffer.emptyAndGetCurrentSegment();
        int cix = 0;
        int ix = 0;
        while (ix < byteLen) {
            int byteIx = ix & 3;
            int ch3 = (quads[ix >> 2] >> ((3 - byteIx) << 3)) & Const.MAX_ARRAY_DIMENSIONS;
            ix++;
            if (ch3 > 127) {
                if ((ch3 & 224) == 192) {
                    ch2 = ch3 & 31;
                    needed = 1;
                } else if ((ch3 & 240) == 224) {
                    ch2 = ch3 & 15;
                    needed = 2;
                } else if ((ch3 & Hessian2Constants.BC_LONG_BYTE_ZERO) == 240) {
                    ch2 = ch3 & 7;
                    needed = 3;
                } else {
                    _reportInvalidInitial(ch3);
                    ch2 = 1;
                    needed = 1;
                }
                if (ix + needed > byteLen) {
                    _reportInvalidEOF(" in field name", JsonToken.FIELD_NAME);
                }
                int byteIx2 = ix & 3;
                int ch22 = quads[ix >> 2] >> ((3 - byteIx2) << 3);
                ix++;
                if ((ch22 & Opcodes.CHECKCAST) != 128) {
                    _reportInvalidOther(ch22);
                }
                ch3 = (ch2 << 6) | (ch22 & 63);
                if (needed > 1) {
                    int byteIx3 = ix & 3;
                    int ch23 = quads[ix >> 2] >> ((3 - byteIx3) << 3);
                    ix++;
                    if ((ch23 & Opcodes.CHECKCAST) != 128) {
                        _reportInvalidOther(ch23);
                    }
                    ch3 = (ch3 << 6) | (ch23 & 63);
                    if (needed > 2) {
                        int byteIx4 = ix & 3;
                        int ch24 = quads[ix >> 2] >> ((3 - byteIx4) << 3);
                        ix++;
                        if ((ch24 & Opcodes.CHECKCAST) != 128) {
                            _reportInvalidOther(ch24 & Const.MAX_ARRAY_DIMENSIONS);
                        }
                        ch3 = (ch3 << 6) | (ch24 & 63);
                    }
                }
                if (needed > 2) {
                    int ch4 = ch3 - 65536;
                    if (cix >= cbuf.length) {
                        cbuf = this._textBuffer.expandCurrentSegment();
                    }
                    int i = cix;
                    cix++;
                    cbuf[i] = (char) (GeneratorBase.SURR1_FIRST + (ch4 >> 10));
                    ch3 = 56320 | (ch4 & 1023);
                }
            }
            if (cix >= cbuf.length) {
                cbuf = this._textBuffer.expandCurrentSegment();
            }
            int i2 = cix;
            cix++;
            cbuf[i2] = (char) ch3;
        }
        String baseName = new String(cbuf, 0, cix);
        if (lastQuadBytes < 4) {
            quads[qlen - 1] = lastQuad;
        }
        return this._symbols.addName(baseName, quads, qlen);
    }

    private static final int _padLastQuad(int q, int bytes) {
        return bytes == 4 ? q : q | ((-1) << (bytes << 3));
    }

    protected void _loadMoreGuaranteed() throws IOException {
        if (!_loadMore()) {
            _reportInvalidEOF();
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected void _finishString() throws IOException {
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            _loadMoreGuaranteed();
            ptr = this._inputPtr;
        }
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int max = Math.min(this._inputEnd, ptr + outBuf.length);
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            if (ptr >= max) {
                break;
            }
            int c = inputBuffer[ptr] & 255;
            if (codes[c] != 0) {
                if (c == 34) {
                    this._inputPtr = ptr + 1;
                    this._textBuffer.setCurrentLength(outPtr);
                    return;
                }
            } else {
                ptr++;
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) c;
            }
        }
        this._inputPtr = ptr;
        _finishString2(outBuf, outPtr);
    }

    protected String _finishAndReturnString() throws IOException {
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            _loadMoreGuaranteed();
            ptr = this._inputPtr;
        }
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int max = Math.min(this._inputEnd, ptr + outBuf.length);
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            if (ptr >= max) {
                break;
            }
            int c = inputBuffer[ptr] & 255;
            if (codes[c] != 0) {
                if (c == 34) {
                    this._inputPtr = ptr + 1;
                    return this._textBuffer.setCurrentAndReturn(outPtr);
                }
            } else {
                ptr++;
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) c;
            }
        }
        this._inputPtr = ptr;
        _finishString2(outBuf, outPtr);
        return this._textBuffer.contentsAsString();
    }

    private final void _finishString2(char[] outBuf, int outPtr) throws IOException {
        int[] codes = _icUTF8;
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            int ptr = this._inputPtr;
            if (ptr >= this._inputEnd) {
                _loadMoreGuaranteed();
                ptr = this._inputPtr;
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr));
            while (true) {
                if (ptr < max) {
                    int i = ptr;
                    ptr++;
                    int c = inputBuffer[i] & Const.MAX_ARRAY_DIMENSIONS;
                    if (codes[c] != 0) {
                        this._inputPtr = ptr;
                        if (c != 34) {
                            switch (codes[c]) {
                                case 1:
                                    c = _decodeEscaped();
                                    break;
                                case 2:
                                    c = _decodeUtf8_2(c);
                                    break;
                                case 3:
                                    if (this._inputEnd - this._inputPtr >= 2) {
                                        c = _decodeUtf8_3fast(c);
                                        break;
                                    } else {
                                        c = _decodeUtf8_3(c);
                                        break;
                                    }
                                case 4:
                                    int c2 = _decodeUtf8_4(c);
                                    int i2 = outPtr;
                                    outPtr++;
                                    outBuf[i2] = (char) (55296 | (c2 >> 10));
                                    if (outPtr >= outBuf.length) {
                                        outBuf = this._textBuffer.finishCurrentSegment();
                                        outPtr = 0;
                                    }
                                    c = 56320 | (c2 & 1023);
                                    break;
                                default:
                                    if (c < 32) {
                                        _throwUnquotedSpace(c, "string value");
                                        break;
                                    } else {
                                        _reportInvalidChar(c);
                                        break;
                                    }
                            }
                            if (outPtr >= outBuf.length) {
                                outBuf = this._textBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int i3 = outPtr;
                            outPtr++;
                            outBuf[i3] = (char) c;
                        } else {
                            this._textBuffer.setCurrentLength(outPtr);
                            return;
                        }
                    } else {
                        int i4 = outPtr;
                        outPtr++;
                        outBuf[i4] = (char) c;
                    }
                } else {
                    this._inputPtr = ptr;
                }
            }
        }
    }

    protected void _skipString() throws IOException {
        this._tokenIncomplete = false;
        int[] codes = _icUTF8;
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            int ptr = this._inputPtr;
            int max = this._inputEnd;
            if (ptr >= max) {
                _loadMoreGuaranteed();
                ptr = this._inputPtr;
                max = this._inputEnd;
            }
            while (true) {
                if (ptr < max) {
                    int i = ptr;
                    ptr++;
                    int c = inputBuffer[i] & 255;
                    if (codes[c] != 0) {
                        this._inputPtr = ptr;
                        if (c != 34) {
                            switch (codes[c]) {
                                case 1:
                                    _decodeEscaped();
                                    break;
                                case 2:
                                    _skipUtf8_2();
                                    break;
                                case 3:
                                    _skipUtf8_3();
                                    break;
                                case 4:
                                    _skipUtf8_4(c);
                                    break;
                                default:
                                    if (c < 32) {
                                        _throwUnquotedSpace(c, "string value");
                                        break;
                                    } else {
                                        _reportInvalidChar(c);
                                        break;
                                    }
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    this._inputPtr = ptr;
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x004b, code lost:            if (r7._parsingContext.inArray() == false) goto L35;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0001. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0086  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0111  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected com.fasterxml.jackson.core.JsonToken _handleUnexpectedValue(int r8) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 331
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8StreamJsonParser._handleUnexpectedValue(int):com.fasterxml.jackson.core.JsonToken");
    }

    protected JsonToken _handleApos() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        byte[] inputBuffer = this._inputBuffer;
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                _loadMoreGuaranteed();
            }
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int max = this._inputEnd;
            int max2 = this._inputPtr + (outBuf.length - outPtr);
            if (max2 < max) {
                max = max2;
            }
            while (true) {
                if (this._inputPtr < max) {
                    int i = this._inputPtr;
                    this._inputPtr = i + 1;
                    int c = inputBuffer[i] & Const.MAX_ARRAY_DIMENSIONS;
                    if (c != 39) {
                        if (codes[c] == 0 || c == 34) {
                            int i2 = outPtr;
                            outPtr++;
                            outBuf[i2] = (char) c;
                        } else {
                            switch (codes[c]) {
                                case 1:
                                    c = _decodeEscaped();
                                    break;
                                case 2:
                                    c = _decodeUtf8_2(c);
                                    break;
                                case 3:
                                    if (this._inputEnd - this._inputPtr >= 2) {
                                        c = _decodeUtf8_3fast(c);
                                        break;
                                    } else {
                                        c = _decodeUtf8_3(c);
                                        break;
                                    }
                                case 4:
                                    int c2 = _decodeUtf8_4(c);
                                    int i3 = outPtr;
                                    outPtr++;
                                    outBuf[i3] = (char) (55296 | (c2 >> 10));
                                    if (outPtr >= outBuf.length) {
                                        outBuf = this._textBuffer.finishCurrentSegment();
                                        outPtr = 0;
                                    }
                                    c = 56320 | (c2 & 1023);
                                    break;
                                default:
                                    if (c < 32) {
                                        _throwUnquotedSpace(c, "string value");
                                    }
                                    _reportInvalidChar(c);
                                    break;
                            }
                            if (outPtr >= outBuf.length) {
                                outBuf = this._textBuffer.finishCurrentSegment();
                                outPtr = 0;
                            }
                            int i4 = outPtr;
                            outPtr++;
                            outBuf[i4] = (char) c;
                        }
                    } else {
                        this._textBuffer.setCurrentLength(outPtr);
                        return JsonToken.VALUE_STRING;
                    }
                }
            }
        }
    }

    protected JsonToken _handleInvalidNumberStart(int ch2, boolean neg) throws IOException {
        return _handleInvalidNumberStart(ch2, neg, false);
    }

    protected JsonToken _handleInvalidNumberStart(int ch2, boolean neg, boolean hasSign) throws IOException {
        String match;
        while (ch2 == 73) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                _reportInvalidEOFInValue(JsonToken.VALUE_NUMBER_FLOAT);
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            ch2 = bArr[i];
            if (ch2 == 78) {
                match = neg ? "-INF" : "+INF";
            } else {
                if (ch2 != 110) {
                    break;
                }
                match = neg ? "-Infinity" : "+Infinity";
            }
            _matchToken(match, 3);
            if ((this._features & FEAT_MASK_NON_NUM_NUMBERS) != 0) {
                return resetAsNaN(match, neg ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            }
            _reportError("Non-standard token '%s': enable `JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS` to allow", match);
        }
        if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature()) && hasSign && !neg) {
            _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
        }
        String message = neg ? "expected digit (0-9) to follow minus sign, for valid numeric value" : "expected digit (0-9) for valid numeric value";
        _reportUnexpectedNumberChar(ch2, message);
        return null;
    }

    protected final void _matchTrue() throws IOException {
        int ch2;
        int ptr = this._inputPtr;
        if (ptr + 3 < this._inputEnd) {
            byte[] buf = this._inputBuffer;
            int ptr2 = ptr + 1;
            if (buf[ptr] == 114) {
                int ptr3 = ptr2 + 1;
                if (buf[ptr2] == 117) {
                    int ptr4 = ptr3 + 1;
                    if (buf[ptr3] == 101 && ((ch2 = buf[ptr4] & Const.MAX_ARRAY_DIMENSIONS) < 48 || ch2 == 93 || ch2 == 125)) {
                        this._inputPtr = ptr4;
                        return;
                    }
                }
            }
        }
        _matchToken2("true", 1);
    }

    protected final void _matchFalse() throws IOException {
        int ch2;
        int ptr = this._inputPtr;
        if (ptr + 4 < this._inputEnd) {
            byte[] buf = this._inputBuffer;
            int ptr2 = ptr + 1;
            if (buf[ptr] == 97) {
                int ptr3 = ptr2 + 1;
                if (buf[ptr2] == 108) {
                    int ptr4 = ptr3 + 1;
                    if (buf[ptr3] == 115) {
                        int ptr5 = ptr4 + 1;
                        if (buf[ptr4] == 101 && ((ch2 = buf[ptr5] & Const.MAX_ARRAY_DIMENSIONS) < 48 || ch2 == 93 || ch2 == 125)) {
                            this._inputPtr = ptr5;
                            return;
                        }
                    }
                }
            }
        }
        _matchToken2("false", 1);
    }

    protected final void _matchNull() throws IOException {
        int ch2;
        int ptr = this._inputPtr;
        if (ptr + 3 < this._inputEnd) {
            byte[] buf = this._inputBuffer;
            int ptr2 = ptr + 1;
            if (buf[ptr] == 117) {
                int ptr3 = ptr2 + 1;
                if (buf[ptr2] == 108) {
                    int ptr4 = ptr3 + 1;
                    if (buf[ptr3] == 108 && ((ch2 = buf[ptr4] & Const.MAX_ARRAY_DIMENSIONS) < 48 || ch2 == 93 || ch2 == 125)) {
                        this._inputPtr = ptr4;
                        return;
                    }
                }
            }
        }
        _matchToken2("null", 1);
    }

    protected final void _matchToken(String matchStr, int i) throws IOException {
        int len = matchStr.length();
        if (this._inputPtr + len >= this._inputEnd) {
            _matchToken2(matchStr, i);
            return;
        }
        do {
            if (this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
                _reportInvalidToken(matchStr.substring(0, i));
            }
            this._inputPtr++;
            i++;
        } while (i < len);
        int ch2 = this._inputBuffer[this._inputPtr] & 255;
        if (ch2 >= 48 && ch2 != 93 && ch2 != 125) {
            _checkMatchEnd(matchStr, i, ch2);
        }
    }

    private final void _matchToken2(String matchStr, int i) throws IOException {
        int ch2;
        int len = matchStr.length();
        do {
            if ((this._inputPtr >= this._inputEnd && !_loadMore()) || this._inputBuffer[this._inputPtr] != matchStr.charAt(i)) {
                _reportInvalidToken(matchStr.substring(0, i));
            }
            this._inputPtr++;
            i++;
        } while (i < len);
        if ((this._inputPtr < this._inputEnd || _loadMore()) && (ch2 = this._inputBuffer[this._inputPtr] & 255) >= 48 && ch2 != 93 && ch2 != 125) {
            _checkMatchEnd(matchStr, i, ch2);
        }
    }

    private final void _checkMatchEnd(String matchStr, int i, int ch2) throws IOException {
        char c = (char) _decodeCharForError(ch2);
        if (Character.isJavaIdentifierPart(c)) {
            _reportInvalidToken(matchStr.substring(0, i));
        }
    }

    private final int _skipWS() throws IOException {
        while (this._inputPtr < this._inputEnd) {
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int i2 = bArr[i] & 255;
            if (i2 > 32) {
                if (i2 == 47 || i2 == 35) {
                    this._inputPtr--;
                    return _skipWS2();
                }
                return i2;
            }
            if (i2 != 32) {
                if (i2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (i2 == 13) {
                    _skipCR();
                } else if (i2 != 9) {
                    _throwInvalidSpace(i2);
                }
            }
        }
        return _skipWS2();
    }

    private final int _skipWS2() throws IOException {
        int i;
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                i = bArr[i2] & 255;
                if (i > 32) {
                    if (i == 47) {
                        _skipComment();
                    } else if (i != 35 || !_skipYAMLComment()) {
                        break;
                    }
                } else if (i != 32) {
                    if (i == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i == 13) {
                        _skipCR();
                    } else if (i != 9) {
                        _throwInvalidSpace(i);
                    }
                }
            } else {
                throw _constructError("Unexpected end-of-input within/between " + this._parsingContext.typeDesc() + " entries");
            }
        }
        return i;
    }

    private final int _skipWSOrEnd() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            return _eofAsNextChar();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int i2 = bArr[i] & 255;
        if (i2 > 32) {
            if (i2 == 47 || i2 == 35) {
                this._inputPtr--;
                return _skipWSOrEnd2();
            }
            return i2;
        }
        if (i2 != 32) {
            if (i2 == 10) {
                this._currInputRow++;
                this._currInputRowStart = this._inputPtr;
            } else if (i2 == 13) {
                _skipCR();
            } else if (i2 != 9) {
                _throwInvalidSpace(i2);
            }
        }
        while (this._inputPtr < this._inputEnd) {
            byte[] bArr2 = this._inputBuffer;
            int i3 = this._inputPtr;
            this._inputPtr = i3 + 1;
            int i4 = bArr2[i3] & 255;
            if (i4 > 32) {
                if (i4 == 47 || i4 == 35) {
                    this._inputPtr--;
                    return _skipWSOrEnd2();
                }
                return i4;
            }
            if (i4 != 32) {
                if (i4 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (i4 == 13) {
                    _skipCR();
                } else if (i4 != 9) {
                    _throwInvalidSpace(i4);
                }
            }
        }
        return _skipWSOrEnd2();
    }

    private final int _skipWSOrEnd2() throws IOException {
        int i;
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                i = bArr[i2] & 255;
                if (i > 32) {
                    if (i == 47) {
                        _skipComment();
                    } else if (i != 35 || !_skipYAMLComment()) {
                        break;
                    }
                } else if (i != 32) {
                    if (i == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i == 13) {
                        _skipCR();
                    } else if (i != 9) {
                        _throwInvalidSpace(i);
                    }
                }
            } else {
                return _eofAsNextChar();
            }
        }
        return i;
    }

    private final int _skipColon() throws IOException {
        if (this._inputPtr + 4 >= this._inputEnd) {
            return _skipColon2(false);
        }
        int i = this._inputBuffer[this._inputPtr];
        if (i == 58) {
            byte[] bArr = this._inputBuffer;
            int i2 = this._inputPtr + 1;
            this._inputPtr = i2;
            byte b = bArr[i2];
            if (b > 32) {
                if (b == 47 || b == 35) {
                    return _skipColon2(true);
                }
                this._inputPtr++;
                return b;
            }
            if (b == 32 || b == 9) {
                byte[] bArr2 = this._inputBuffer;
                int i3 = this._inputPtr + 1;
                this._inputPtr = i3;
                byte b2 = bArr2[i3];
                if (b2 > 32) {
                    if (b2 == 47 || b2 == 35) {
                        return _skipColon2(true);
                    }
                    this._inputPtr++;
                    return b2;
                }
            }
            return _skipColon2(true);
        }
        if (i == 32 || i == 9) {
            byte[] bArr3 = this._inputBuffer;
            int i4 = this._inputPtr + 1;
            this._inputPtr = i4;
            i = bArr3[i4];
        }
        if (i == 58) {
            byte[] bArr4 = this._inputBuffer;
            int i5 = this._inputPtr + 1;
            this._inputPtr = i5;
            byte b3 = bArr4[i5];
            if (b3 > 32) {
                if (b3 == 47 || b3 == 35) {
                    return _skipColon2(true);
                }
                this._inputPtr++;
                return b3;
            }
            if (b3 == 32 || b3 == 9) {
                byte[] bArr5 = this._inputBuffer;
                int i6 = this._inputPtr + 1;
                this._inputPtr = i6;
                byte b4 = bArr5[i6];
                if (b4 > 32) {
                    if (b4 == 47 || b4 == 35) {
                        return _skipColon2(true);
                    }
                    this._inputPtr++;
                    return b4;
                }
            }
            return _skipColon2(true);
        }
        return _skipColon2(false);
    }

    private final int _skipColon2(boolean gotColon) throws IOException {
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & 255;
                if (i2 > 32) {
                    if (i2 == 47) {
                        _skipComment();
                    } else if (i2 != 35 || !_skipYAMLComment()) {
                        if (gotColon) {
                            return i2;
                        }
                        if (i2 != 58) {
                            _reportUnexpectedChar(i2, "was expecting a colon to separate field name and value");
                        }
                        gotColon = true;
                    }
                } else if (i2 != 32) {
                    if (i2 == 10) {
                        this._currInputRow++;
                        this._currInputRowStart = this._inputPtr;
                    } else if (i2 == 13) {
                        _skipCR();
                    } else if (i2 != 9) {
                        _throwInvalidSpace(i2);
                    }
                }
            } else {
                _reportInvalidEOF(" within/between " + this._parsingContext.typeDesc() + " entries", null);
                return -1;
            }
        }
    }

    private final void _skipComment() throws IOException {
        if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(" in a comment", null);
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        int c = bArr[i] & 255;
        if (c == 47) {
            _skipLine();
        } else if (c == 42) {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:14:0x0034. Please report as an issue. */
    private final void _skipCComment() throws IOException {
        int[] codes = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & 255;
                int code = codes[i2];
                if (code != 0) {
                    switch (code) {
                        case 2:
                            _skipUtf8_2();
                            break;
                        case 3:
                            _skipUtf8_3();
                            break;
                        case 4:
                            _skipUtf8_4(i2);
                            break;
                        case 10:
                            this._currInputRow++;
                            this._currInputRowStart = this._inputPtr;
                            break;
                        case 13:
                            _skipCR();
                            break;
                        case 42:
                            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                                break;
                            } else if (this._inputBuffer[this._inputPtr] != 47) {
                                break;
                            } else {
                                this._inputPtr++;
                                return;
                            }
                            break;
                        default:
                            _reportInvalidChar(i2);
                            break;
                    }
                }
            }
        }
        _reportInvalidEOF(" in a comment", null);
    }

    private final boolean _skipYAMLComment() throws IOException {
        if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
            return false;
        }
        _skipLine();
        return true;
    }

    private final void _skipLine() throws IOException {
        int[] codes = CharTypes.getInputCodeComment();
        while (true) {
            if (this._inputPtr < this._inputEnd || _loadMore()) {
                byte[] bArr = this._inputBuffer;
                int i = this._inputPtr;
                this._inputPtr = i + 1;
                int i2 = bArr[i] & 255;
                int code = codes[i2];
                if (code != 0) {
                    switch (code) {
                        case 2:
                            _skipUtf8_2();
                            break;
                        case 3:
                            _skipUtf8_3();
                            break;
                        case 4:
                            _skipUtf8_4(i2);
                            break;
                        case 10:
                            this._currInputRow++;
                            this._currInputRowStart = this._inputPtr;
                            return;
                        case 13:
                            _skipCR();
                            return;
                        case 42:
                            break;
                        default:
                            if (code >= 0) {
                                break;
                            } else {
                                _reportInvalidChar(i2);
                                break;
                            }
                    }
                }
            } else {
                return;
            }
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected char _decodeEscaped() throws IOException {
        if (this._inputPtr >= this._inputEnd && !_loadMore()) {
            _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        switch (b) {
            case 34:
            case 47:
            case 92:
                return (char) b;
            case 98:
                return '\b';
            case 102:
                return '\f';
            case Opcodes.FDIV /* 110 */:
                return '\n';
            case Opcodes.FREM /* 114 */:
                return '\r';
            case 116:
                return '\t';
            case Opcodes.LNEG /* 117 */:
                int value = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                    if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                        _reportInvalidEOF(" in character escape sequence", JsonToken.VALUE_STRING);
                    }
                    byte[] bArr2 = this._inputBuffer;
                    int i3 = this._inputPtr;
                    this._inputPtr = i3 + 1;
                    byte b2 = bArr2[i3];
                    int digit = CharTypes.charToHex(b2);
                    if (digit < 0) {
                        _reportUnexpectedChar(b2 & 255, "expected a hex-digit for character escape sequence");
                    }
                    value = (value << 4) | digit;
                }
                return (char) value;
            default:
                return _handleUnrecognizedCharacterEscape((char) _decodeCharForError(b));
        }
    }

    protected int _decodeCharForError(int firstByte) throws IOException {
        int needed;
        int c = firstByte & Const.MAX_ARRAY_DIMENSIONS;
        if (c > 127) {
            if ((c & 224) == 192) {
                c &= 31;
                needed = 1;
            } else if ((c & 240) == 224) {
                c &= 15;
                needed = 2;
            } else if ((c & Hessian2Constants.BC_LONG_BYTE_ZERO) == 240) {
                c &= 7;
                needed = 3;
            } else {
                _reportInvalidInitial(c & Const.MAX_ARRAY_DIMENSIONS);
                needed = 1;
            }
            int d = nextByte();
            if ((d & Opcodes.CHECKCAST) != 128) {
                _reportInvalidOther(d & Const.MAX_ARRAY_DIMENSIONS);
            }
            c = (c << 6) | (d & 63);
            if (needed > 1) {
                int d2 = nextByte();
                if ((d2 & Opcodes.CHECKCAST) != 128) {
                    _reportInvalidOther(d2 & Const.MAX_ARRAY_DIMENSIONS);
                }
                c = (c << 6) | (d2 & 63);
                if (needed > 2) {
                    int d3 = nextByte();
                    if ((d3 & Opcodes.CHECKCAST) != 128) {
                        _reportInvalidOther(d3 & Const.MAX_ARRAY_DIMENSIONS);
                    }
                    c = (c << 6) | (d3 & 63);
                }
            }
        }
        return c;
    }

    private final int _decodeUtf8_2(int c) throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
        return ((c & 31) << 6) | (b & 63);
    }

    private final int _decodeUtf8_3(int c1) throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        int c12 = c1 & 15;
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
        int c = (c12 << 6) | (b & 63);
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b2 = bArr2[i2];
        if ((b2 & 192) != 128) {
            _reportInvalidOther(b2 & 255, this._inputPtr);
        }
        return (c << 6) | (b2 & 63);
    }

    private final int _decodeUtf8_3fast(int c1) throws IOException {
        int c12 = c1 & 15;
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
        int c = (c12 << 6) | (b & 63);
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b2 = bArr2[i2];
        if ((b2 & 192) != 128) {
            _reportInvalidOther(b2 & 255, this._inputPtr);
        }
        return (c << 6) | (b2 & 63);
    }

    private final int _decodeUtf8_4(int c) throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
        int c2 = ((c & 7) << 6) | (b & 63);
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b2 = bArr2[i2];
        if ((b2 & 192) != 128) {
            _reportInvalidOther(b2 & 255, this._inputPtr);
        }
        int c3 = (c2 << 6) | (b2 & 63);
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr3 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        byte b3 = bArr3[i3];
        if ((b3 & 192) != 128) {
            _reportInvalidOther(b3 & 255, this._inputPtr);
        }
        return ((c3 << 6) | (b3 & 63)) - 65536;
    }

    private final void _skipUtf8_2() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
    }

    private final void _skipUtf8_3() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b2 = bArr2[i2];
        if ((b2 & 192) != 128) {
            _reportInvalidOther(b2 & 255, this._inputPtr);
        }
    }

    private final void _skipUtf8_4(int c) throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        byte b = bArr[i];
        if ((b & 192) != 128) {
            _reportInvalidOther(b & 255, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr2 = this._inputBuffer;
        int i2 = this._inputPtr;
        this._inputPtr = i2 + 1;
        byte b2 = bArr2[i2];
        if ((b2 & 192) != 128) {
            _reportInvalidOther(b2 & 255, this._inputPtr);
        }
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr3 = this._inputBuffer;
        int i3 = this._inputPtr;
        this._inputPtr = i3 + 1;
        byte b3 = bArr3[i3];
        if ((b3 & 192) != 128) {
            _reportInvalidOther(b3 & 255, this._inputPtr);
        }
    }

    protected final void _skipCR() throws IOException {
        if ((this._inputPtr < this._inputEnd || _loadMore()) && this._inputBuffer[this._inputPtr] == 10) {
            this._inputPtr++;
        }
        this._currInputRow++;
        this._currInputRowStart = this._inputPtr;
    }

    private int nextByte() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            _loadMoreGuaranteed();
        }
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return bArr[i] & 255;
    }

    protected void _reportInvalidToken(String matchedPart, int ptr) throws IOException {
        this._inputPtr = ptr;
        _reportInvalidToken(matchedPart, _validJsonTokenList());
    }

    protected void _reportInvalidToken(String matchedPart) throws IOException {
        _reportInvalidToken(matchedPart, _validJsonTokenList());
    }

    protected void _reportInvalidToken(String matchedPart, String msg) throws IOException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            if (this._inputPtr >= this._inputEnd && !_loadMore()) {
                break;
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            char c = (char) _decodeCharForError(bArr[i]);
            if (!Character.isJavaIdentifierPart(c)) {
                break;
            }
            sb.append(c);
            if (sb.length() >= 256) {
                sb.append("...");
                break;
            }
        }
        _reportError("Unrecognized token '%s': was expecting %s", sb, msg);
    }

    protected void _reportInvalidChar(int c) throws JsonParseException {
        if (c < 32) {
            _throwInvalidSpace(c);
        }
        _reportInvalidInitial(c);
    }

    protected void _reportInvalidInitial(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
        this._inputPtr = ptr;
        _reportInvalidOther(mask);
    }

    protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
        ByteArrayBuilder builder = _getByteArrayBuilder();
        while (true) {
            if (this._inputPtr >= this._inputEnd) {
                _loadMoreGuaranteed();
            }
            byte[] bArr = this._inputBuffer;
            int i = this._inputPtr;
            this._inputPtr = i + 1;
            int ch2 = bArr[i] & 255;
            if (ch2 > 32) {
                int bits = b64variant.decodeBase64Char(ch2);
                if (bits < 0) {
                    if (ch2 == 34) {
                        return builder.toByteArray();
                    }
                    bits = _decodeBase64Escape(b64variant, ch2, 0);
                    if (bits < 0) {
                        continue;
                    }
                }
                int decodedData = bits;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr2 = this._inputBuffer;
                int i2 = this._inputPtr;
                this._inputPtr = i2 + 1;
                int ch3 = bArr2[i2] & 255;
                int bits2 = b64variant.decodeBase64Char(ch3);
                if (bits2 < 0) {
                    bits2 = _decodeBase64Escape(b64variant, ch3, 1);
                }
                int decodedData2 = (decodedData << 6) | bits2;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr3 = this._inputBuffer;
                int i3 = this._inputPtr;
                this._inputPtr = i3 + 1;
                int ch4 = bArr3[i3] & 255;
                int bits3 = b64variant.decodeBase64Char(ch4);
                if (bits3 < 0) {
                    if (bits3 != -2) {
                        if (ch4 == 34) {
                            builder.append(decodedData2 >> 4);
                            if (b64variant.requiresPaddingOnRead()) {
                                this._inputPtr--;
                                _handleBase64MissingPadding(b64variant);
                            }
                            return builder.toByteArray();
                        }
                        bits3 = _decodeBase64Escape(b64variant, ch4, 2);
                    }
                    if (bits3 == -2) {
                        if (this._inputPtr >= this._inputEnd) {
                            _loadMoreGuaranteed();
                        }
                        byte[] bArr4 = this._inputBuffer;
                        int i4 = this._inputPtr;
                        this._inputPtr = i4 + 1;
                        int ch5 = bArr4[i4] & 255;
                        if (!b64variant.usesPaddingChar(ch5) && _decodeBase64Escape(b64variant, ch5, 3) != -2) {
                            throw reportInvalidBase64Char(b64variant, ch5, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
                        }
                        builder.append(decodedData2 >> 4);
                    }
                }
                int decodedData3 = (decodedData2 << 6) | bits3;
                if (this._inputPtr >= this._inputEnd) {
                    _loadMoreGuaranteed();
                }
                byte[] bArr5 = this._inputBuffer;
                int i5 = this._inputPtr;
                this._inputPtr = i5 + 1;
                int ch6 = bArr5[i5] & 255;
                int bits4 = b64variant.decodeBase64Char(ch6);
                if (bits4 < 0) {
                    if (bits4 != -2) {
                        if (ch6 == 34) {
                            builder.appendTwoBytes(decodedData3 >> 2);
                            if (b64variant.requiresPaddingOnRead()) {
                                this._inputPtr--;
                                _handleBase64MissingPadding(b64variant);
                            }
                            return builder.toByteArray();
                        }
                        bits4 = _decodeBase64Escape(b64variant, ch6, 3);
                    }
                    if (bits4 == -2) {
                        builder.appendTwoBytes(decodedData3 >> 2);
                    }
                }
                builder.appendThreeBytes((decodedData3 << 6) | bits4);
            }
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.JsonParser
    public JsonLocation getTokenLocation() {
        if (this._currToken == JsonToken.FIELD_NAME) {
            long total = this._currInputProcessed + (this._nameStartOffset - 1);
            return new JsonLocation(_contentReference(), total, -1L, this._nameStartRow, this._nameStartCol);
        }
        return new JsonLocation(_contentReference(), this._tokenInputTotal - 1, -1L, this._tokenInputRow, this._tokenInputCol);
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.JsonParser
    public JsonLocation getCurrentLocation() {
        int col = (this._inputPtr - this._currInputRowStart) + 1;
        return new JsonLocation(_contentReference(), this._currInputProcessed + this._inputPtr, -1L, this._currInputRow, col);
    }

    private final void _updateLocation() {
        this._tokenInputRow = this._currInputRow;
        int ptr = this._inputPtr;
        this._tokenInputTotal = this._currInputProcessed + ptr;
        this._tokenInputCol = ptr - this._currInputRowStart;
    }

    private final void _updateNameLocation() {
        this._nameStartRow = this._currInputRow;
        int ptr = this._inputPtr;
        this._nameStartOffset = ptr;
        this._nameStartCol = ptr - this._currInputRowStart;
    }

    private final JsonToken _closeScope(int i) throws JsonParseException {
        if (i == 125) {
            _closeObjectScope();
            JsonToken jsonToken = JsonToken.END_OBJECT;
            this._currToken = jsonToken;
            return jsonToken;
        }
        _closeArrayScope();
        JsonToken jsonToken2 = JsonToken.END_ARRAY;
        this._currToken = jsonToken2;
        return jsonToken2;
    }

    private final void _closeArrayScope() throws JsonParseException {
        _updateLocation();
        if (!this._parsingContext.inArray()) {
            _reportMismatchedEndMarker(93, '}');
        }
        this._parsingContext = this._parsingContext.clearAndGetParent();
    }

    private final void _closeObjectScope() throws JsonParseException {
        _updateLocation();
        if (!this._parsingContext.inObject()) {
            _reportMismatchedEndMarker(125, ']');
        }
        this._parsingContext = this._parsingContext.clearAndGetParent();
    }
}
