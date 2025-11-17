package com.fasterxml.jackson.core.json;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.base.ParserBase;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import org.apache.el.parser.ELParserConstants;
import org.springframework.asm.Opcodes;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/UTF8DataInputJsonParser.class */
public class UTF8DataInputJsonParser extends ParserBase {
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
    protected DataInput _inputData;
    protected int _nextByte;

    public UTF8DataInputJsonParser(IOContext ctxt, int features, DataInput inputData, ObjectCodec codec, ByteQuadsCanonicalizer sym, int firstByte) {
        super(ctxt, features);
        this._quadBuffer = new int[16];
        this._nextByte = -1;
        this._objectCodec = codec;
        this._symbols = sym;
        this._inputData = inputData;
        this._nextByte = firstByte;
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
        return 0;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getInputSource() {
        return this._inputData;
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected void _closeInput() throws IOException {
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected void _releaseBuffers() throws IOException {
        super._releaseBuffers();
        this._symbols.release();
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
        if (this._currToken == JsonToken.VALUE_STRING) {
            if (this._tokenIncomplete) {
                this._tokenIncomplete = false;
                _finishString();
            }
            return this._textBuffer.size();
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._parsingContext.getCurrentName().length();
        }
        if (this._currToken != null) {
            if (this._currToken.isNumeric()) {
                return this._textBuffer.size();
            }
            return this._currToken.asCharArray().length;
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

    /* JADX WARN: Code restructure failed: missing block: B:29:0x0203, code lost:            r7._tokenIncomplete = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x020a, code lost:            if (r11 <= 0) goto L57;     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x020d, code lost:            r13 = r13 + r11;        r9.write(r10, 0, r11);     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x021e, code lost:            return r13;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected int _readBinary(com.fasterxml.jackson.core.Base64Variant r8, java.io.OutputStream r9, byte[] r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 543
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8DataInputJsonParser._readBinary(com.fasterxml.jackson.core.Base64Variant, java.io.OutputStream, byte[]):int");
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public JsonToken nextToken() throws IOException {
        JsonToken t;
        if (this._closed) {
            return null;
        }
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
        this._tokenInputRow = this._currInputRow;
        if (i == 93 || i == 125) {
            _closeScope(i);
            return this._currToken;
        }
        if (this._parsingContext.expectComma()) {
            if (i != 44) {
                _reportUnexpectedChar(i, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
            }
            i = _skipWS();
            if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0 && (i == 93 || i == 125)) {
                _closeScope(i);
                return this._currToken;
            }
        }
        if (!this._parsingContext.inObject()) {
            return _nextTokenNotInObject(i);
        }
        String n = _parseName(i);
        this._parsingContext.setCurrentName(n);
        this._currToken = JsonToken.FIELD_NAME;
        int i2 = _skipColon();
        if (i2 == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return this._currToken;
        }
        switch (i2) {
            case ELParserConstants.EMPTY /* 43 */:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    t = _parsePosNumber();
                    break;
                } else {
                    t = _handleUnexpectedValue(i2);
                    break;
                }
            case 45:
                t = _parseNegNumber();
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
            case Opcodes.FSUB /* 102 */:
                _matchToken("false", 1);
                t = JsonToken.VALUE_FALSE;
                break;
            case Opcodes.FDIV /* 110 */:
                _matchToken("null", 1);
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchToken("true", 1);
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
            case ELParserConstants.EMPTY /* 43 */:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    JsonToken _parsePosNumber = _parsePosNumber();
                    this._currToken = _parsePosNumber;
                    return _parsePosNumber;
                }
                JsonToken _handleUnexpectedValue = _handleUnexpectedValue(i);
                this._currToken = _handleUnexpectedValue;
                return _handleUnexpectedValue;
            case 45:
                JsonToken _parseNegNumber = _parseNegNumber();
                this._currToken = _parseNegNumber;
                return _parseNegNumber;
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
            case Opcodes.FSUB /* 102 */:
                _matchToken("false", 1);
                JsonToken jsonToken3 = JsonToken.VALUE_FALSE;
                this._currToken = jsonToken3;
                return jsonToken3;
            case Opcodes.FDIV /* 110 */:
                _matchToken("null", 1);
                JsonToken jsonToken4 = JsonToken.VALUE_NULL;
                this._currToken = jsonToken4;
                return jsonToken4;
            case 116:
                _matchToken("true", 1);
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
        int i = _skipWS();
        this._binaryValue = null;
        this._tokenInputRow = this._currInputRow;
        if (i == 93 || i == 125) {
            _closeScope(i);
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
            _nextTokenNotInObject(i);
            return null;
        }
        String nameStr = _parseName(i);
        this._parsingContext.setCurrentName(nameStr);
        this._currToken = JsonToken.FIELD_NAME;
        int i2 = _skipColon();
        if (i2 == 34) {
            this._tokenIncomplete = true;
            this._nextToken = JsonToken.VALUE_STRING;
            return nameStr;
        }
        switch (i2) {
            case ELParserConstants.EMPTY /* 43 */:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    t = _parsePosNumber();
                    break;
                } else {
                    t = _handleUnexpectedValue(i2);
                    break;
                }
            case 45:
                t = _parseNegNumber();
                break;
            case 46:
                _parseFloatThatStartsWithPeriod(false, false);
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
            case Opcodes.FSUB /* 102 */:
                _matchToken("false", 1);
                t = JsonToken.VALUE_FALSE;
                break;
            case Opcodes.FDIV /* 110 */:
                _matchToken("null", 1);
                t = JsonToken.VALUE_NULL;
                break;
            case 116:
                _matchToken("true", 1);
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
        int c2;
        int outPtr;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        if (c == 48) {
            c2 = _handleLeadingZeroes();
            if (c2 <= 57 && c2 >= 48) {
                outPtr = 0;
            } else {
                if (c2 == 120 || c2 == 88) {
                    return _handleInvalidNumberStart(c2, false);
                }
                outBuf[0] = '0';
                outPtr = 1;
            }
        } else {
            outBuf[0] = (char) c;
            c2 = this._inputData.readUnsignedByte();
            outPtr = 1;
        }
        int intLen = outPtr;
        while (c2 <= 57 && c2 >= 48) {
            intLen++;
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) c2;
            c2 = this._inputData.readUnsignedByte();
        }
        if (c2 == 46 || c2 == 101 || c2 == 69) {
            return _parseFloat(outBuf, outPtr, c2, false, intLen);
        }
        this._textBuffer.setCurrentLength(outPtr);
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace();
        } else {
            this._nextByte = c2;
        }
        return resetInt(false, intLen);
    }

    protected final JsonToken _parsePosNumber() throws IOException {
        return _parseSignedNumber(false);
    }

    protected final JsonToken _parseNegNumber() throws IOException {
        return _parseSignedNumber(true);
    }

    private final JsonToken _parseSignedNumber(boolean negative) throws IOException {
        int c;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;
        if (negative) {
            outPtr = 0 + 1;
            outBuf[0] = '-';
        }
        int c2 = this._inputData.readUnsignedByte();
        int i = outPtr;
        int outPtr2 = outPtr + 1;
        outBuf[i] = (char) c2;
        if (c2 <= 48) {
            if (c2 == 48) {
                c = _handleLeadingZeroes();
            } else {
                if (c2 == 46) {
                    return _parseFloatThatStartsWithPeriod(negative, true);
                }
                return _handleInvalidNumberStart(c2, negative, true);
            }
        } else {
            if (c2 > 57) {
                return _handleInvalidNumberStart(c2, negative, true);
            }
            c = this._inputData.readUnsignedByte();
        }
        int intLen = 1;
        while (c <= 57 && c >= 48) {
            intLen++;
            if (outPtr2 >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr2 = 0;
            }
            int i2 = outPtr2;
            outPtr2++;
            outBuf[i2] = (char) c;
            c = this._inputData.readUnsignedByte();
        }
        if (c == 46 || c == 101 || c == 69) {
            return _parseFloat(outBuf, outPtr2, c, negative, intLen);
        }
        this._textBuffer.setCurrentLength(outPtr2);
        this._nextByte = c;
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace();
        }
        return resetInt(negative, intLen);
    }

    private final int _handleLeadingZeroes() throws IOException {
        int ch2 = this._inputData.readUnsignedByte();
        if (ch2 < 48 || ch2 > 57) {
            return ch2;
        }
        if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
            reportInvalidNumber("Leading zeroes not allowed");
        }
        while (ch2 == 48) {
            ch2 = this._inputData.readUnsignedByte();
        }
        return ch2;
    }

    private final JsonToken _parseFloat(char[] outBuf, int outPtr, int c, boolean negative, int integerPartLength) throws IOException {
        int fractLen = 0;
        if (c == 46) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) c;
            while (true) {
                c = this._inputData.readUnsignedByte();
                if (c < 48 || c > 57) {
                    break;
                }
                fractLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int i2 = outPtr;
                outPtr++;
                outBuf[i2] = (char) c;
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
            int i3 = outPtr;
            outPtr++;
            outBuf[i3] = (char) c;
            c = this._inputData.readUnsignedByte();
            if (c == 45 || c == 43) {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int i4 = outPtr;
                outPtr++;
                outBuf[i4] = (char) c;
                c = this._inputData.readUnsignedByte();
            }
            while (c <= 57 && c >= 48) {
                expLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
                int i5 = outPtr;
                outPtr++;
                outBuf[i5] = (char) c;
                c = this._inputData.readUnsignedByte();
            }
            if (expLen == 0) {
                _reportUnexpectedNumberChar(c, "Exponent indicator not followed by a digit");
            }
        }
        this._nextByte = c;
        if (this._parsingContext.inRoot()) {
            _verifyRootSpace();
        }
        this._textBuffer.setCurrentLength(outPtr);
        return resetFloat(negative, integerPartLength, fractLen, expLen);
    }

    private final void _verifyRootSpace() throws IOException {
        int ch2 = this._nextByte;
        if (ch2 <= 32) {
            this._nextByte = -1;
            if (ch2 == 13 || ch2 == 10) {
                this._currInputRow++;
                return;
            }
            return;
        }
        _reportMissingRootWS(ch2);
    }

    protected final String _parseName(int i) throws IOException {
        if (i != 34) {
            return _handleOddName(i);
        }
        int[] codes = _icLatin1;
        int q = this._inputData.readUnsignedByte();
        if (codes[q] == 0) {
            int i2 = this._inputData.readUnsignedByte();
            if (codes[i2] == 0) {
                int q2 = (q << 8) | i2;
                int i3 = this._inputData.readUnsignedByte();
                if (codes[i3] == 0) {
                    int q3 = (q2 << 8) | i3;
                    int i4 = this._inputData.readUnsignedByte();
                    if (codes[i4] == 0) {
                        int q4 = (q3 << 8) | i4;
                        int i5 = this._inputData.readUnsignedByte();
                        if (codes[i5] == 0) {
                            this._quad1 = q4;
                            return _parseMediumName(i5);
                        }
                        if (i5 == 34) {
                            return findName(q4, 4);
                        }
                        return parseName(q4, i5, 4);
                    }
                    if (i4 == 34) {
                        return findName(q3, 3);
                    }
                    return parseName(q3, i4, 3);
                }
                if (i3 == 34) {
                    return findName(q2, 2);
                }
                return parseName(q2, i3, 2);
            }
            if (i2 == 34) {
                return findName(q, 1);
            }
            return parseName(q, i2, 1);
        }
        if (q == 34) {
            return "";
        }
        return parseName(0, q, 0);
    }

    private final String _parseMediumName(int q2) throws IOException {
        int[] codes = _icLatin1;
        int i = this._inputData.readUnsignedByte();
        if (codes[i] != 0) {
            if (i == 34) {
                return findName(this._quad1, q2, 1);
            }
            return parseName(this._quad1, q2, i, 1);
        }
        int q22 = (q2 << 8) | i;
        int i2 = this._inputData.readUnsignedByte();
        if (codes[i2] != 0) {
            if (i2 == 34) {
                return findName(this._quad1, q22, 2);
            }
            return parseName(this._quad1, q22, i2, 2);
        }
        int q23 = (q22 << 8) | i2;
        int i3 = this._inputData.readUnsignedByte();
        if (codes[i3] != 0) {
            if (i3 == 34) {
                return findName(this._quad1, q23, 3);
            }
            return parseName(this._quad1, q23, i3, 3);
        }
        int q24 = (q23 << 8) | i3;
        int i4 = this._inputData.readUnsignedByte();
        if (codes[i4] != 0) {
            if (i4 == 34) {
                return findName(this._quad1, q24, 4);
            }
            return parseName(this._quad1, q24, i4, 4);
        }
        return _parseMediumName2(i4, q24);
    }

    private final String _parseMediumName2(int q3, int q2) throws IOException {
        int[] codes = _icLatin1;
        int i = this._inputData.readUnsignedByte();
        if (codes[i] != 0) {
            if (i == 34) {
                return findName(this._quad1, q2, q3, 1);
            }
            return parseName(this._quad1, q2, q3, i, 1);
        }
        int q32 = (q3 << 8) | i;
        int i2 = this._inputData.readUnsignedByte();
        if (codes[i2] != 0) {
            if (i2 == 34) {
                return findName(this._quad1, q2, q32, 2);
            }
            return parseName(this._quad1, q2, q32, i2, 2);
        }
        int q33 = (q32 << 8) | i2;
        int i3 = this._inputData.readUnsignedByte();
        if (codes[i3] != 0) {
            if (i3 == 34) {
                return findName(this._quad1, q2, q33, 3);
            }
            return parseName(this._quad1, q2, q33, i3, 3);
        }
        int q34 = (q33 << 8) | i3;
        int i4 = this._inputData.readUnsignedByte();
        if (codes[i4] != 0) {
            if (i4 == 34) {
                return findName(this._quad1, q2, q34, 4);
            }
            return parseName(this._quad1, q2, q34, i4, 4);
        }
        return _parseLongName(i4, q2, q34);
    }

    private final String _parseLongName(int q, int q2, int q3) throws IOException {
        this._quadBuffer[0] = this._quad1;
        this._quadBuffer[1] = q2;
        this._quadBuffer[2] = q3;
        int[] codes = _icLatin1;
        int qlen = 3;
        while (true) {
            int i = this._inputData.readUnsignedByte();
            if (codes[i] != 0) {
                if (i == 34) {
                    return findName(this._quadBuffer, qlen, q, 1);
                }
                return parseEscapedName(this._quadBuffer, qlen, q, i, 1);
            }
            int q4 = (q << 8) | i;
            int i2 = this._inputData.readUnsignedByte();
            if (codes[i2] != 0) {
                if (i2 == 34) {
                    return findName(this._quadBuffer, qlen, q4, 2);
                }
                return parseEscapedName(this._quadBuffer, qlen, q4, i2, 2);
            }
            int q5 = (q4 << 8) | i2;
            int i3 = this._inputData.readUnsignedByte();
            if (codes[i3] != 0) {
                if (i3 == 34) {
                    return findName(this._quadBuffer, qlen, q5, 3);
                }
                return parseEscapedName(this._quadBuffer, qlen, q5, i3, 3);
            }
            int q6 = (q5 << 8) | i3;
            int i4 = this._inputData.readUnsignedByte();
            if (codes[i4] != 0) {
                if (i4 == 34) {
                    return findName(this._quadBuffer, qlen, q6, 4);
                }
                return parseEscapedName(this._quadBuffer, qlen, q6, i4, 4);
            }
            if (qlen >= this._quadBuffer.length) {
                this._quadBuffer = _growArrayBy(this._quadBuffer, qlen);
            }
            int i5 = qlen;
            qlen++;
            this._quadBuffer[i5] = q6;
            q = i4;
        }
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
                            int[] _growArrayBy = _growArrayBy(quads, quads.length);
                            quads = _growArrayBy;
                            this._quadBuffer = _growArrayBy;
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
                                int[] _growArrayBy2 = _growArrayBy(quads, quads.length);
                                quads = _growArrayBy2;
                                this._quadBuffer = _growArrayBy2;
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
                    int[] _growArrayBy3 = _growArrayBy(quads, quads.length);
                    quads = _growArrayBy3;
                    this._quadBuffer = _growArrayBy3;
                }
                int i3 = qlen;
                qlen++;
                quads[i3] = currQuad;
                currQuad = ch2;
                currQuadBytes = 1;
            }
            ch2 = this._inputData.readUnsignedByte();
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                int[] _growArrayBy4 = _growArrayBy(quads, quads.length);
                quads = _growArrayBy4;
                this._quadBuffer = _growArrayBy4;
            }
            int i4 = qlen;
            qlen++;
            quads[i4] = pad(currQuad, currQuadBytes);
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
        do {
            if (currQuadBytes < 4) {
                currQuadBytes++;
                currQuad = (currQuad << 8) | ch2;
            } else {
                if (qlen >= quads.length) {
                    int[] _growArrayBy = _growArrayBy(quads, quads.length);
                    quads = _growArrayBy;
                    this._quadBuffer = _growArrayBy;
                }
                int i = qlen;
                qlen++;
                quads[i] = currQuad;
                currQuad = ch2;
                currQuadBytes = 1;
            }
            ch2 = this._inputData.readUnsignedByte();
        } while (codes[ch2] == 0);
        this._nextByte = ch2;
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                int[] _growArrayBy2 = _growArrayBy(quads, quads.length);
                quads = _growArrayBy2;
                this._quadBuffer = _growArrayBy2;
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
        int ch2 = this._inputData.readUnsignedByte();
        if (ch2 == 39) {
            return "";
        }
        int[] quads = this._quadBuffer;
        int qlen = 0;
        int currQuad = 0;
        int currQuadBytes = 0;
        int[] codes = _icLatin1;
        while (ch2 != 39) {
            if (ch2 != 34 && codes[ch2] != 0) {
                if (ch2 != 92) {
                    _throwUnquotedSpace(ch2, "name");
                } else {
                    ch2 = _decodeEscaped();
                }
                if (ch2 > 127) {
                    if (currQuadBytes >= 4) {
                        if (qlen >= quads.length) {
                            int[] _growArrayBy = _growArrayBy(quads, quads.length);
                            quads = _growArrayBy;
                            this._quadBuffer = _growArrayBy;
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
                                int[] _growArrayBy2 = _growArrayBy(quads, quads.length);
                                quads = _growArrayBy2;
                                this._quadBuffer = _growArrayBy2;
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
                    int[] _growArrayBy3 = _growArrayBy(quads, quads.length);
                    quads = _growArrayBy3;
                    this._quadBuffer = _growArrayBy3;
                }
                int i3 = qlen;
                qlen++;
                quads[i3] = currQuad;
                currQuad = ch2;
                currQuadBytes = 1;
            }
            ch2 = this._inputData.readUnsignedByte();
        }
        if (currQuadBytes > 0) {
            if (qlen >= quads.length) {
                int[] _growArrayBy4 = _growArrayBy(quads, quads.length);
                quads = _growArrayBy4;
                this._quadBuffer = _growArrayBy4;
            }
            int i4 = qlen;
            qlen++;
            quads[i4] = pad(currQuad, currQuadBytes);
        }
        String name = this._symbols.findName(quads, qlen);
        if (name == null) {
            name = addName(quads, qlen, currQuadBytes);
        }
        return name;
    }

    private final String findName(int q1, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int q12 = pad(q1, lastQuadBytes);
        String name = this._symbols.findName(q12);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q12;
        return addName(this._quadBuffer, 1, lastQuadBytes);
    }

    private final String findName(int q1, int q2, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int q22 = pad(q2, lastQuadBytes);
        String name = this._symbols.findName(q1, q22);
        if (name != null) {
            return name;
        }
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q22;
        return addName(this._quadBuffer, 2, lastQuadBytes);
    }

    private final String findName(int q1, int q2, int q3, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        int q32 = pad(q3, lastQuadBytes);
        String name = this._symbols.findName(q1, q2, q32);
        if (name != null) {
            return name;
        }
        int[] quads = this._quadBuffer;
        quads[0] = q1;
        quads[1] = q2;
        quads[2] = pad(q32, lastQuadBytes);
        return addName(quads, 3, lastQuadBytes);
    }

    private final String findName(int[] quads, int qlen, int lastQuad, int lastQuadBytes) throws JsonParseException, StreamConstraintsException {
        if (qlen >= quads.length) {
            int[] _growArrayBy = _growArrayBy(quads, quads.length);
            quads = _growArrayBy;
            this._quadBuffer = _growArrayBy;
        }
        int qlen2 = qlen + 1;
        quads[qlen] = pad(lastQuad, lastQuadBytes);
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
            int ch3 = (quads[ix >> 2] >> ((3 - byteIx) << 3)) & 255;
            ix++;
            if (ch3 > 127) {
                if ((ch3 & 224) == 192) {
                    ch2 = ch3 & 31;
                    needed = 1;
                } else if ((ch3 & 240) == 224) {
                    ch2 = ch3 & 15;
                    needed = 2;
                } else if ((ch3 & 248) == 240) {
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
                            _reportInvalidOther(ch24 & 255);
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
                    ch3 = 56320 | (ch4 & FastDoubleMath.DOUBLE_EXPONENT_BIAS);
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

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected void _finishString() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int outEnd = outBuf.length;
        do {
            int c = this._inputData.readUnsignedByte();
            if (codes[c] != 0) {
                if (c == 34) {
                    this._textBuffer.setCurrentLength(outPtr);
                    return;
                } else {
                    _finishString2(outBuf, outPtr, c);
                    return;
                }
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) c;
        } while (outPtr < outEnd);
        _finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
    }

    private String _finishAndReturnString() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int outEnd = outBuf.length;
        do {
            int c = this._inputData.readUnsignedByte();
            if (codes[c] != 0) {
                if (c == 34) {
                    return this._textBuffer.setCurrentAndReturn(outPtr);
                }
                _finishString2(outBuf, outPtr, c);
                return this._textBuffer.contentsAsString();
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) c;
        } while (outPtr < outEnd);
        _finishString2(outBuf, outPtr, this._inputData.readUnsignedByte());
        return this._textBuffer.contentsAsString();
    }

    private final void _finishString2(char[] outBuf, int outPtr, int c) throws IOException {
        int[] codes = _icUTF8;
        int outEnd = outBuf.length;
        while (true) {
            if (codes[c] == 0) {
                if (outPtr >= outEnd) {
                    outBuf = this._textBuffer.finishCurrentSegment();
                    outPtr = 0;
                    outEnd = outBuf.length;
                }
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) c;
                c = this._inputData.readUnsignedByte();
            } else if (c != 34) {
                switch (codes[c]) {
                    case 1:
                        c = _decodeEscaped();
                        break;
                    case 2:
                        c = _decodeUtf8_2(c);
                        break;
                    case 3:
                        c = _decodeUtf8_3(c);
                        break;
                    case 4:
                        int c2 = _decodeUtf8_4(c);
                        if (outPtr >= outBuf.length) {
                            outBuf = this._textBuffer.finishCurrentSegment();
                            outPtr = 0;
                            outEnd = outBuf.length;
                        }
                        int i2 = outPtr;
                        outPtr++;
                        outBuf[i2] = (char) (55296 | (c2 >> 10));
                        c = 56320 | (c2 & FastDoubleMath.DOUBLE_EXPONENT_BIAS);
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
                    outEnd = outBuf.length;
                }
                int i3 = outPtr;
                outPtr++;
                outBuf[i3] = (char) c;
                c = this._inputData.readUnsignedByte();
            } else {
                this._textBuffer.setCurrentLength(outPtr);
                return;
            }
        }
    }

    protected void _skipString() throws IOException {
        this._tokenIncomplete = false;
        int[] codes = _icUTF8;
        while (true) {
            int c = this._inputData.readUnsignedByte();
            if (codes[c] != 0) {
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
                            _skipUtf8_4();
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
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x004b, code lost:            if (r5._parsingContext.inArray() == false) goto L30;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0001. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected com.fasterxml.jackson.core.JsonToken _handleUnexpectedValue(int r6) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 289
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8DataInputJsonParser._handleUnexpectedValue(int):com.fasterxml.jackson.core.JsonToken");
    }

    protected JsonToken _handleApos() throws IOException {
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        while (true) {
            int outEnd = outBuf.length;
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
                outEnd = outBuf.length;
            }
            while (true) {
                int c = this._inputData.readUnsignedByte();
                if (c != 39) {
                    if (codes[c] == 0 || c == 34) {
                        int i = outPtr;
                        outPtr++;
                        outBuf[i] = (char) c;
                        if (outPtr >= outEnd) {
                            break;
                        }
                    } else {
                        switch (codes[c]) {
                            case 1:
                                c = _decodeEscaped();
                                break;
                            case 2:
                                c = _decodeUtf8_2(c);
                                break;
                            case 3:
                                c = _decodeUtf8_3(c);
                                break;
                            case 4:
                                int c2 = _decodeUtf8_4(c);
                                int i2 = outPtr;
                                outPtr++;
                                outBuf[i2] = (char) (55296 | (c2 >> 10));
                                if (outPtr >= outBuf.length) {
                                    outBuf = this._textBuffer.finishCurrentSegment();
                                    outPtr = 0;
                                }
                                c = 56320 | (c2 & FastDoubleMath.DOUBLE_EXPONENT_BIAS);
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
                        int i3 = outPtr;
                        outPtr++;
                        outBuf[i3] = (char) c;
                    }
                } else {
                    this._textBuffer.setCurrentLength(outPtr);
                    return JsonToken.VALUE_STRING;
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
            ch2 = this._inputData.readUnsignedByte();
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
            _reportError("Non-standard token '" + match + "': enable `JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS` to allow");
        }
        if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature()) && hasSign && !neg) {
            _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
        }
        String message = neg ? "expected digit (0-9) to follow minus sign, for valid numeric value" : "expected digit (0-9) for valid numeric value";
        _reportUnexpectedNumberChar(ch2, message);
        return null;
    }

    protected final void _matchToken(String matchStr, int i) throws IOException {
        int len = matchStr.length();
        do {
            int ch2 = this._inputData.readUnsignedByte();
            if (ch2 != matchStr.charAt(i)) {
                _reportInvalidToken(ch2, matchStr.substring(0, i));
            }
            i++;
        } while (i < len);
        int ch3 = this._inputData.readUnsignedByte();
        if (ch3 >= 48 && ch3 != 93 && ch3 != 125) {
            _checkMatchEnd(matchStr, i, ch3);
        }
        this._nextByte = ch3;
    }

    private final void _checkMatchEnd(String matchStr, int i, int ch2) throws IOException {
        char c = (char) _decodeCharForError(ch2);
        if (Character.isJavaIdentifierPart(c)) {
            _reportInvalidToken(c, matchStr.substring(0, i));
        }
    }

    private final int _skipWS() throws IOException {
        int i = this._nextByte;
        if (i < 0) {
            i = this._inputData.readUnsignedByte();
        } else {
            this._nextByte = -1;
        }
        while (i <= 32) {
            if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            i = this._inputData.readUnsignedByte();
        }
        if (i == 47 || i == 35) {
            return _skipWSComment(i);
        }
        return i;
    }

    private final int _skipWSOrEnd() throws IOException {
        int i = this._nextByte;
        if (i < 0) {
            try {
                i = this._inputData.readUnsignedByte();
            } catch (EOFException e) {
                return _eofAsNextChar();
            }
        } else {
            this._nextByte = -1;
        }
        while (i <= 32) {
            if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            try {
                i = this._inputData.readUnsignedByte();
            } catch (EOFException e2) {
                return _eofAsNextChar();
            }
        }
        if (i == 47 || i == 35) {
            return _skipWSComment(i);
        }
        return i;
    }

    private final int _skipWSComment(int i) throws IOException {
        while (true) {
            if (i > 32) {
                if (i == 47) {
                    _skipComment();
                } else if (i == 35) {
                    if (!_skipYAMLComment()) {
                        return i;
                    }
                } else {
                    return i;
                }
            } else if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            i = this._inputData.readUnsignedByte();
        }
    }

    private final int _skipColon() throws IOException {
        int i = this._nextByte;
        if (i < 0) {
            i = this._inputData.readUnsignedByte();
        } else {
            this._nextByte = -1;
        }
        if (i == 58) {
            int i2 = this._inputData.readUnsignedByte();
            if (i2 > 32) {
                if (i2 == 47 || i2 == 35) {
                    return _skipColon2(i2, true);
                }
                return i2;
            }
            if (i2 == 32 || i2 == 9) {
                i2 = this._inputData.readUnsignedByte();
                if (i2 > 32) {
                    if (i2 == 47 || i2 == 35) {
                        return _skipColon2(i2, true);
                    }
                    return i2;
                }
            }
            return _skipColon2(i2, true);
        }
        if (i == 32 || i == 9) {
            i = this._inputData.readUnsignedByte();
        }
        if (i == 58) {
            int i3 = this._inputData.readUnsignedByte();
            if (i3 > 32) {
                if (i3 == 47 || i3 == 35) {
                    return _skipColon2(i3, true);
                }
                return i3;
            }
            if (i3 == 32 || i3 == 9) {
                i3 = this._inputData.readUnsignedByte();
                if (i3 > 32) {
                    if (i3 == 47 || i3 == 35) {
                        return _skipColon2(i3, true);
                    }
                    return i3;
                }
            }
            return _skipColon2(i3, true);
        }
        return _skipColon2(i, false);
    }

    private final int _skipColon2(int i, boolean gotColon) throws IOException {
        while (true) {
            if (i > 32) {
                if (i == 47) {
                    _skipComment();
                } else if (i != 35 || !_skipYAMLComment()) {
                    if (gotColon) {
                        return i;
                    }
                    if (i != 58) {
                        _reportUnexpectedChar(i, "was expecting a colon to separate field name and value");
                    }
                    gotColon = true;
                }
            } else if (i == 13 || i == 10) {
                this._currInputRow++;
            }
            i = this._inputData.readUnsignedByte();
        }
    }

    private final void _skipComment() throws IOException {
        if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        int c = this._inputData.readUnsignedByte();
        if (c == 47) {
            _skipLine();
        } else if (c == 42) {
            _skipCComment();
        } else {
            _reportUnexpectedChar(c, "was expecting either '*' or '/' for a comment");
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0017. Please report as an issue. */
    private final void _skipCComment() throws IOException {
        int[] codes = CharTypes.getInputCodeComment();
        int i = this._inputData.readUnsignedByte();
        while (true) {
            int code = codes[i];
            if (code != 0) {
                switch (code) {
                    case 2:
                        _skipUtf8_2();
                        break;
                    case 3:
                        _skipUtf8_3();
                        break;
                    case 4:
                        _skipUtf8_4();
                        break;
                    case 10:
                    case 13:
                        this._currInputRow++;
                        break;
                    case 42:
                        break;
                    default:
                        _reportInvalidChar(i);
                        break;
                }
                i = this._inputData.readUnsignedByte();
                if (i == 47) {
                    return;
                }
            }
            i = this._inputData.readUnsignedByte();
        }
    }

    private final boolean _skipYAMLComment() throws IOException {
        if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
            return false;
        }
        _skipLine();
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0050, code lost:            r4._currInputRow++;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x005a, code lost:            return;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void _skipLine() throws java.io.IOException {
        /*
            r4 = this;
            int[] r0 = com.fasterxml.jackson.core.io.CharTypes.getInputCodeComment()
            r5 = r0
        L4:
            r0 = r4
            java.io.DataInput r0 = r0._inputData
            int r0 = r0.readUnsignedByte()
            r6 = r0
            r0 = r5
            r1 = r6
            r0 = r0[r1]
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L7c
            r0 = r7
            switch(r0) {
                case 2: goto L5e;
                case 3: goto L65;
                case 4: goto L6c;
                case 10: goto L50;
                case 13: goto L50;
                case 42: goto L5b;
                default: goto L73;
            }
        L50:
            r0 = r4
            r1 = r0
            int r1 = r1._currInputRow
            r2 = 1
            int r1 = r1 + r2
            r0._currInputRow = r1
            return
        L5b:
            goto L7c
        L5e:
            r0 = r4
            r0._skipUtf8_2()
            goto L7c
        L65:
            r0 = r4
            r0._skipUtf8_3()
            goto L7c
        L6c:
            r0 = r4
            r0._skipUtf8_4()
            goto L7c
        L73:
            r0 = r7
            if (r0 >= 0) goto L7c
            r0 = r4
            r1 = r6
            r0._reportInvalidChar(r1)
        L7c:
            goto L4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.UTF8DataInputJsonParser._skipLine():void");
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected char _decodeEscaped() throws IOException {
        int c = this._inputData.readUnsignedByte();
        switch (c) {
            case 34:
            case 47:
            case 92:
                return (char) c;
            case Opcodes.FADD /* 98 */:
                return '\b';
            case Opcodes.FSUB /* 102 */:
                return '\f';
            case Opcodes.FDIV /* 110 */:
                return '\n';
            case Opcodes.FREM /* 114 */:
                return '\r';
            case 116:
                return '\t';
            case Opcodes.LNEG /* 117 */:
                int value = 0;
                for (int i = 0; i < 4; i++) {
                    int ch2 = this._inputData.readUnsignedByte();
                    int digit = CharTypes.charToHex(ch2);
                    if (digit < 0) {
                        _reportUnexpectedChar(ch2, "expected a hex-digit for character escape sequence");
                    }
                    value = (value << 4) | digit;
                }
                return (char) value;
            default:
                return _handleUnrecognizedCharacterEscape((char) _decodeCharForError(c));
        }
    }

    protected int _decodeCharForError(int firstByte) throws IOException {
        int needed;
        int c = firstByte & 255;
        if (c > 127) {
            if ((c & 224) == 192) {
                c &= 31;
                needed = 1;
            } else if ((c & 240) == 224) {
                c &= 15;
                needed = 2;
            } else if ((c & 248) == 240) {
                c &= 7;
                needed = 3;
            } else {
                _reportInvalidInitial(c & 255);
                needed = 1;
            }
            int d = this._inputData.readUnsignedByte();
            if ((d & Opcodes.CHECKCAST) != 128) {
                _reportInvalidOther(d & 255);
            }
            c = (c << 6) | (d & 63);
            if (needed > 1) {
                int d2 = this._inputData.readUnsignedByte();
                if ((d2 & Opcodes.CHECKCAST) != 128) {
                    _reportInvalidOther(d2 & 255);
                }
                c = (c << 6) | (d2 & 63);
                if (needed > 2) {
                    int d3 = this._inputData.readUnsignedByte();
                    if ((d3 & Opcodes.CHECKCAST) != 128) {
                        _reportInvalidOther(d3 & 255);
                    }
                    c = (c << 6) | (d3 & 63);
                }
            }
        }
        return c;
    }

    private final int _decodeUtf8_2(int c) throws IOException {
        int d = this._inputData.readUnsignedByte();
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & 255);
        }
        return ((c & 31) << 6) | (d & 63);
    }

    private final int _decodeUtf8_3(int c1) throws IOException {
        int c12 = c1 & 15;
        int d = this._inputData.readUnsignedByte();
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & 255);
        }
        int c = (c12 << 6) | (d & 63);
        int d2 = this._inputData.readUnsignedByte();
        if ((d2 & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d2 & 255);
        }
        return (c << 6) | (d2 & 63);
    }

    private final int _decodeUtf8_4(int c) throws IOException {
        int d = this._inputData.readUnsignedByte();
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & 255);
        }
        int c2 = ((c & 7) << 6) | (d & 63);
        int d2 = this._inputData.readUnsignedByte();
        if ((d2 & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d2 & 255);
        }
        int c3 = (c2 << 6) | (d2 & 63);
        int d3 = this._inputData.readUnsignedByte();
        if ((d3 & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d3 & 255);
        }
        return ((c3 << 6) | (d3 & 63)) - 65536;
    }

    private final void _skipUtf8_2() throws IOException {
        int c = this._inputData.readUnsignedByte();
        if ((c & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(c & 255);
        }
    }

    private final void _skipUtf8_3() throws IOException {
        int c = this._inputData.readUnsignedByte();
        if ((c & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(c & 255);
        }
        int c2 = this._inputData.readUnsignedByte();
        if ((c2 & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(c2 & 255);
        }
    }

    private final void _skipUtf8_4() throws IOException {
        int d = this._inputData.readUnsignedByte();
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & 255);
        }
        int d2 = this._inputData.readUnsignedByte();
        if ((d2 & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d2 & 255);
        }
        int d3 = this._inputData.readUnsignedByte();
        if ((d3 & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d3 & 255);
        }
    }

    protected void _reportInvalidToken(int ch2, String matchedPart) throws IOException {
        _reportInvalidToken(ch2, matchedPart, _validJsonTokenList());
    }

    protected void _reportInvalidToken(int ch2, String matchedPart, String msg) throws IOException {
        StringBuilder sb = new StringBuilder(matchedPart);
        while (true) {
            char c = (char) _decodeCharForError(ch2);
            if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
                ch2 = this._inputData.readUnsignedByte();
            } else {
                _reportError("Unrecognized token '" + sb.toString() + "': was expecting " + msg);
                return;
            }
        }
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

    private void _reportInvalidOther(int mask) throws JsonParseException {
        _reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    private static int[] _growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }

    protected final byte[] _decodeBase64(Base64Variant b64variant) throws IOException {
        int ch2;
        ByteArrayBuilder builder = _getByteArrayBuilder();
        while (true) {
            int ch3 = this._inputData.readUnsignedByte();
            if (ch3 > 32) {
                int bits = b64variant.decodeBase64Char(ch3);
                if (bits < 0) {
                    if (ch3 == 34) {
                        return builder.toByteArray();
                    }
                    bits = _decodeBase64Escape(b64variant, ch3, 0);
                    if (bits < 0) {
                        continue;
                    }
                }
                int decodedData = bits;
                int ch4 = this._inputData.readUnsignedByte();
                int bits2 = b64variant.decodeBase64Char(ch4);
                if (bits2 < 0) {
                    bits2 = _decodeBase64Escape(b64variant, ch4, 1);
                }
                int decodedData2 = (decodedData << 6) | bits2;
                int ch5 = this._inputData.readUnsignedByte();
                int bits3 = b64variant.decodeBase64Char(ch5);
                if (bits3 < 0) {
                    if (bits3 != -2) {
                        if (ch5 == 34) {
                            builder.append(decodedData2 >> 4);
                            if (b64variant.requiresPaddingOnRead()) {
                                _handleBase64MissingPadding(b64variant);
                            }
                            return builder.toByteArray();
                        }
                        bits3 = _decodeBase64Escape(b64variant, ch5, 2);
                    }
                    if (bits3 == -2) {
                        ch2 = this._inputData.readUnsignedByte();
                        if (b64variant.usesPaddingChar(ch2) || (ch2 == 92 && _decodeBase64Escape(b64variant, ch2, 3) == -2)) {
                            builder.append(decodedData2 >> 4);
                        }
                    }
                }
                int decodedData3 = (decodedData2 << 6) | bits3;
                int ch6 = this._inputData.readUnsignedByte();
                int bits4 = b64variant.decodeBase64Char(ch6);
                if (bits4 < 0) {
                    if (bits4 != -2) {
                        if (ch6 == 34) {
                            builder.appendTwoBytes(decodedData3 >> 2);
                            if (b64variant.requiresPaddingOnRead()) {
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
        throw reportInvalidBase64Char(b64variant, ch2, 3, "expected padding character '" + b64variant.getPaddingChar() + "'");
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.JsonParser
    public JsonLocation getTokenLocation() {
        return new JsonLocation(_contentReference(), -1L, -1L, this._tokenInputRow, -1);
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase, com.fasterxml.jackson.core.JsonParser
    public JsonLocation getCurrentLocation() {
        return new JsonLocation(_contentReference(), -1L, -1L, this._currInputRow, -1);
    }

    private void _closeScope(int i) throws JsonParseException {
        if (i == 93) {
            if (!this._parsingContext.inArray()) {
                _reportMismatchedEndMarker(i, '}');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            this._currToken = JsonToken.END_ARRAY;
        }
        if (i == 125) {
            if (!this._parsingContext.inObject()) {
                _reportMismatchedEndMarker(i, ']');
            }
            this._parsingContext = this._parsingContext.clearAndGetParent();
            this._currToken = JsonToken.END_OBJECT;
        }
    }

    private static final int pad(int q, int bytes) {
        return bytes == 4 ? q : q | ((-1) << (bytes << 3));
    }
}
