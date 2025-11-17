package com.fasterxml.jackson.core.base;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.json.JsonReadContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.core.util.TextBuffer;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/base/ParserBase.class */
public abstract class ParserBase extends ParserMinimalBase {
    protected static final JacksonFeatureSet<StreamReadCapability> JSON_READ_CAPABILITIES = DEFAULT_READ_CAPABILITIES;
    protected final IOContext _ioContext;
    protected final StreamReadConstraints _streamReadConstraints;
    protected boolean _closed;
    protected int _inputPtr;
    protected int _inputEnd;
    protected long _currInputProcessed;
    protected int _currInputRow;
    protected int _currInputRowStart;
    protected long _tokenInputTotal;
    protected int _tokenInputRow;
    protected int _tokenInputCol;
    protected JsonReadContext _parsingContext;
    protected JsonToken _nextToken;
    protected final TextBuffer _textBuffer;
    protected char[] _nameCopyBuffer;
    protected boolean _nameCopied;
    protected ByteArrayBuilder _byteArrayBuilder;
    protected byte[] _binaryValue;
    protected int _numTypesValid;
    protected int _numberInt;
    protected long _numberLong;
    protected float _numberFloat;
    protected double _numberDouble;
    protected BigInteger _numberBigInt;
    protected BigDecimal _numberBigDecimal;
    protected String _numberString;
    protected boolean _numberNegative;
    protected int _intLength;
    protected int _fractLength;
    protected int _expLength;

    protected abstract void _closeInput() throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public ParserBase(IOContext ctxt, int features) {
        super(features);
        this._currInputRow = 1;
        this._tokenInputRow = 1;
        this._numTypesValid = 0;
        this._ioContext = ctxt;
        StreamReadConstraints streamReadConstraints = ctxt.streamReadConstraints();
        this._streamReadConstraints = streamReadConstraints == null ? StreamReadConstraints.defaults() : streamReadConstraints;
        this._textBuffer = ctxt.constructReadConstrainedTextBuffer();
        DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(features) ? DupDetector.rootDetector(this) : null;
        this._parsingContext = JsonReadContext.createRootContext(dups);
    }

    @Override // com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getCurrentValue() {
        return this._parsingContext.getCurrentValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public void setCurrentValue(Object v) {
        this._parsingContext.setCurrentValue(v);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser enable(JsonParser.Feature f) {
        this._features |= f.getMask();
        if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION && this._parsingContext.getDupDetector() == null) {
            this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser disable(JsonParser.Feature f) {
        this._features &= f.getMask() ^ (-1);
        if (f == JsonParser.Feature.STRICT_DUPLICATE_DETECTION) {
            this._parsingContext = this._parsingContext.withDupDetector(null);
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    @Deprecated
    public JsonParser setFeatureMask(int newMask) {
        int changes = this._features ^ newMask;
        if (changes != 0) {
            this._features = newMask;
            _checkStdFeatureChanges(newMask, changes);
        }
        return this;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser overrideStdFeatures(int values, int mask) {
        int oldState = this._features;
        int newState = (oldState & (mask ^ (-1))) | (values & mask);
        int changed = oldState ^ newState;
        if (changed != 0) {
            this._features = newState;
            _checkStdFeatureChanges(newState, changed);
        }
        return this;
    }

    protected void _checkStdFeatureChanges(int newFeatureFlags, int changedFeatures) {
        int f = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.getMask();
        if ((changedFeatures & f) != 0 && (newFeatureFlags & f) != 0) {
            if (this._parsingContext.getDupDetector() == null) {
                this._parsingContext = this._parsingContext.withDupDetector(DupDetector.rootDetector(this));
            } else {
                this._parsingContext = this._parsingContext.withDupDetector(null);
            }
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public String getCurrentName() throws IOException {
        JsonReadContext parent;
        if ((this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) && (parent = this._parsingContext.getParent()) != null) {
            return parent.getCurrentName();
        }
        return this._parsingContext.getCurrentName();
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public void overrideCurrentName(String name) {
        JsonReadContext ctxt = this._parsingContext;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            ctxt = ctxt.getParent();
        }
        try {
            ctxt.setCurrentName(name);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this._closed) {
            this._inputPtr = Math.max(this._inputPtr, this._inputEnd);
            this._closed = true;
            try {
                _closeInput();
            } finally {
                _releaseBuffers();
            }
        }
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public boolean isClosed() {
        return this._closed;
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public JsonReadContext getParsingContext() {
        return this._parsingContext;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonLocation getTokenLocation() {
        return new JsonLocation(_contentReference(), -1L, getTokenCharacterOffset(), getTokenLineNr(), getTokenColumnNr());
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonLocation getCurrentLocation() {
        int col = (this._inputPtr - this._currInputRowStart) + 1;
        return new JsonLocation(_contentReference(), -1L, this._currInputProcessed + this._inputPtr, this._currInputRow, col);
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public boolean hasTextCharacters() {
        if (this._currToken == JsonToken.VALUE_STRING) {
            return true;
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._nameCopied;
        }
        return false;
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public byte[] getBinaryValue(Base64Variant variant) throws IOException {
        if (this._binaryValue == null) {
            if (this._currToken != JsonToken.VALUE_STRING) {
                _reportError("Current token (" + this._currToken + ") not VALUE_STRING, can not access as binary");
            }
            ByteArrayBuilder builder = _getByteArrayBuilder();
            _decodeBase64(getText(), builder, variant);
            this._binaryValue = builder.toByteArray();
        }
        return this._binaryValue;
    }

    public long getTokenCharacterOffset() {
        return this._tokenInputTotal;
    }

    public int getTokenLineNr() {
        return this._tokenInputRow;
    }

    public int getTokenColumnNr() {
        int col = this._tokenInputCol;
        return col < 0 ? col : col + 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _releaseBuffers() throws IOException {
        this._textBuffer.releaseBuffers();
        char[] buf = this._nameCopyBuffer;
        if (buf != null) {
            this._nameCopyBuffer = null;
            this._ioContext.releaseNameCopyBuffer(buf);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase
    public void _handleEOF() throws JsonParseException {
        if (!this._parsingContext.inRoot()) {
            String marker = this._parsingContext.inArray() ? "Array" : "Object";
            _reportInvalidEOF(String.format(": expected close marker for %s (start marker at %s)", marker, this._parsingContext.startLocation(_contentReference())), null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int _eofAsNextChar() throws JsonParseException {
        _handleEOF();
        return -1;
    }

    public ByteArrayBuilder _getByteArrayBuilder() {
        if (this._byteArrayBuilder == null) {
            this._byteArrayBuilder = new ByteArrayBuilder();
        } else {
            this._byteArrayBuilder.reset();
        }
        return this._byteArrayBuilder;
    }

    protected final JsonToken reset(boolean negative, int intLen, int fractLen, int expLen) throws IOException {
        if (fractLen < 1 && expLen < 1) {
            return resetInt(negative, intLen);
        }
        return resetFloat(negative, intLen, fractLen, expLen);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JsonToken resetInt(boolean negative, int intLen) throws IOException {
        this._streamReadConstraints.validateIntegerLength(intLen);
        this._numberNegative = negative;
        this._intLength = intLen;
        this._fractLength = 0;
        this._expLength = 0;
        this._numTypesValid = 0;
        return JsonToken.VALUE_NUMBER_INT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JsonToken resetFloat(boolean negative, int intLen, int fractLen, int expLen) throws IOException {
        this._streamReadConstraints.validateFPLength(intLen + fractLen + expLen);
        this._numberNegative = negative;
        this._intLength = intLen;
        this._fractLength = fractLen;
        this._expLength = expLen;
        this._numTypesValid = 0;
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final JsonToken resetAsNaN(String valueStr, double value) throws IOException {
        this._textBuffer.resetWithString(valueStr);
        this._numberDouble = value;
        this._numTypesValid = 8;
        return JsonToken.VALUE_NUMBER_FLOAT;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public boolean isNaN() throws IOException {
        return (this._currToken != JsonToken.VALUE_NUMBER_FLOAT || (this._numTypesValid & 8) == 0 || Double.isFinite(_getNumberDouble())) ? false : true;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Number getNumberValue() throws IOException {
        if (this._numTypesValid == 0) {
            _parseNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return Integer.valueOf(this._numberInt);
            }
            if ((this._numTypesValid & 2) != 0) {
                return Long.valueOf(this._numberLong);
            }
            if ((this._numTypesValid & 4) != 0) {
                return _getBigInteger();
            }
            _throwInternal();
        }
        if ((this._numTypesValid & 16) != 0) {
            return _getBigDecimal();
        }
        if ((this._numTypesValid & 32) != 0) {
            return Float.valueOf(_getNumberFloat());
        }
        if ((this._numTypesValid & 8) == 0) {
            _throwInternal();
        }
        return Double.valueOf(_getNumberDouble());
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Number getNumberValueExact() throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(0);
            }
            if ((this._numTypesValid & 1) != 0) {
                return Integer.valueOf(this._numberInt);
            }
            if ((this._numTypesValid & 2) != 0) {
                return Long.valueOf(this._numberLong);
            }
            if ((this._numTypesValid & 4) != 0) {
                return _getBigInteger();
            }
            _throwInternal();
        }
        if (this._numTypesValid == 0) {
            _parseNumericValue(16);
        }
        if ((this._numTypesValid & 16) != 0) {
            return _getBigDecimal();
        }
        if ((this._numTypesValid & 32) != 0) {
            return Float.valueOf(_getNumberFloat());
        }
        if ((this._numTypesValid & 8) == 0) {
            _throwInternal();
        }
        return Double.valueOf(_getNumberDouble());
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public Object getNumberValueDeferred() throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(0);
            }
            if ((this._numTypesValid & 1) != 0) {
                return Integer.valueOf(this._numberInt);
            }
            if ((this._numTypesValid & 2) != 0) {
                return Long.valueOf(this._numberLong);
            }
            if ((this._numTypesValid & 4) != 0) {
                if (this._numberBigInt != null) {
                    return this._numberBigInt;
                }
                if (this._numberString != null) {
                    return this._numberString;
                }
                return _getBigInteger();
            }
            _throwInternal();
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((this._numTypesValid & 16) != 0) {
                return _getBigDecimal();
            }
            if ((this._numTypesValid & 8) != 0) {
                return Double.valueOf(_getNumberDouble());
            }
            if ((this._numTypesValid & 32) != 0) {
                return Float.valueOf(_getNumberFloat());
            }
            return this._textBuffer.contentsAsString();
        }
        return getNumberValue();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public JsonParser.NumberType getNumberType() throws IOException {
        if (this._numTypesValid == 0) {
            _parseNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return JsonParser.NumberType.INT;
            }
            if ((this._numTypesValid & 2) != 0) {
                return JsonParser.NumberType.LONG;
            }
            return JsonParser.NumberType.BIG_INTEGER;
        }
        if ((this._numTypesValid & 16) != 0) {
            return JsonParser.NumberType.BIG_DECIMAL;
        }
        if ((this._numTypesValid & 32) != 0) {
            return JsonParser.NumberType.FLOAT;
        }
        return JsonParser.NumberType.DOUBLE;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public int getIntValue() throws IOException {
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

    @Override // com.fasterxml.jackson.core.JsonParser
    public long getLongValue() throws IOException {
        if ((this._numTypesValid & 2) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(2);
            }
            if ((this._numTypesValid & 2) == 0) {
                convertNumberToLong();
            }
        }
        return this._numberLong;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public BigInteger getBigIntegerValue() throws IOException {
        if ((this._numTypesValid & 4) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(4);
            }
            if ((this._numTypesValid & 4) == 0) {
                convertNumberToBigInteger();
            }
        }
        return _getBigInteger();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public float getFloatValue() throws IOException {
        if ((this._numTypesValid & 32) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(32);
            }
            if ((this._numTypesValid & 32) == 0) {
                convertNumberToFloat();
            }
        }
        return _getNumberFloat();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public double getDoubleValue() throws IOException {
        if ((this._numTypesValid & 8) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(8);
            }
            if ((this._numTypesValid & 8) == 0) {
                convertNumberToDouble();
            }
        }
        return _getNumberDouble();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public BigDecimal getDecimalValue() throws IOException {
        if ((this._numTypesValid & 16) == 0) {
            if (this._numTypesValid == 0) {
                _parseNumericValue(16);
            }
            if ((this._numTypesValid & 16) == 0) {
                convertNumberToBigDecimal();
            }
        }
        return _getBigDecimal();
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public StreamReadConstraints streamReadConstraints() {
        return this._streamReadConstraints;
    }

    protected void _parseNumericValue(int expType) throws IOException {
        if (this._closed) {
            _reportError("Internal error: _parseNumericValue called when parser instance closed");
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            int len = this._intLength;
            if (len <= 9) {
                this._numberInt = this._textBuffer.contentsAsInt(this._numberNegative);
                this._numTypesValid = 1;
                return;
            }
            if (len <= 18) {
                long l = this._textBuffer.contentsAsLong(this._numberNegative);
                if (len == 10) {
                    if (this._numberNegative) {
                        if (l >= -2147483648L) {
                            this._numberInt = (int) l;
                            this._numTypesValid = 1;
                            return;
                        }
                    } else if (l <= 2147483647L) {
                        this._numberInt = (int) l;
                        this._numTypesValid = 1;
                        return;
                    }
                }
                this._numberLong = l;
                this._numTypesValid = 2;
                return;
            }
            if (len == 19) {
                char[] buf = this._textBuffer.getTextBuffer();
                int offset = this._textBuffer.getTextOffset();
                if (this._numberNegative) {
                    offset++;
                }
                if (NumberInput.inLongRange(buf, offset, len, this._numberNegative)) {
                    this._numberLong = NumberInput.parseLong19(buf, offset, this._numberNegative);
                    this._numTypesValid = 2;
                    return;
                }
            }
            _parseSlowInt(expType);
            return;
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            _parseSlowFloat(expType);
        } else {
            _reportError("Current token (%s) not numeric, can not use numeric value accessors", this._currToken);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int _parseIntValue() throws IOException {
        if (this._closed) {
            _reportError("Internal error: _parseNumericValue called when parser instance closed");
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT && this._intLength <= 9) {
            int i = this._textBuffer.contentsAsInt(this._numberNegative);
            this._numberInt = i;
            this._numTypesValid = 1;
            return i;
        }
        _parseNumericValue(1);
        if ((this._numTypesValid & 1) == 0) {
            convertNumberToInt();
        }
        return this._numberInt;
    }

    private void _parseSlowFloat(int expType) throws IOException {
        if (expType == 16) {
            this._numberBigDecimal = null;
            this._numberString = this._textBuffer.contentsAsString();
            this._numTypesValid = 16;
        } else if (expType == 32) {
            this._numberFloat = 0.0f;
            this._numberString = this._textBuffer.contentsAsString();
            this._numTypesValid = 32;
        } else {
            this._numberDouble = 0.0d;
            this._numberString = this._textBuffer.contentsAsString();
            this._numTypesValid = 8;
        }
    }

    private void _parseSlowInt(int expType) throws IOException {
        String numStr = this._textBuffer.contentsAsString();
        if (expType == 1 || expType == 2) {
            _reportTooLongIntegral(expType, numStr);
        }
        if (expType == 8 || expType == 32) {
            this._numberString = numStr;
            this._numTypesValid = 8;
        } else {
            this._numberBigInt = null;
            this._numberString = numStr;
            this._numTypesValid = 4;
        }
    }

    protected void _reportTooLongIntegral(int expType, String rawNum) throws IOException {
        if (expType == 1) {
            reportOverflowInt(rawNum);
        } else {
            reportOverflowLong(rawNum);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void convertNumberToInt() throws IOException {
        if ((this._numTypesValid & 2) != 0) {
            int result = (int) this._numberLong;
            if (result != this._numberLong) {
                reportOverflowInt(getText(), currentToken());
            }
            this._numberInt = result;
        } else if ((this._numTypesValid & 4) != 0) {
            BigInteger bigInteger = _getBigInteger();
            if (BI_MIN_INT.compareTo(bigInteger) > 0 || BI_MAX_INT.compareTo(bigInteger) < 0) {
                reportOverflowInt();
            }
            this._numberInt = bigInteger.intValue();
        } else if ((this._numTypesValid & 8) != 0) {
            double d = _getNumberDouble();
            if (d < -2.147483648E9d || d > 2.147483647E9d) {
                reportOverflowInt();
            }
            this._numberInt = (int) d;
        } else if ((this._numTypesValid & 16) != 0) {
            BigDecimal bigDecimal = _getBigDecimal();
            if (BD_MIN_INT.compareTo(bigDecimal) > 0 || BD_MAX_INT.compareTo(bigDecimal) < 0) {
                reportOverflowInt();
            }
            this._numberInt = bigDecimal.intValue();
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 1;
    }

    protected void convertNumberToLong() throws IOException {
        if ((this._numTypesValid & 1) != 0) {
            this._numberLong = this._numberInt;
        } else if ((this._numTypesValid & 4) != 0) {
            BigInteger bigInteger = _getBigInteger();
            if (BI_MIN_LONG.compareTo(bigInteger) > 0 || BI_MAX_LONG.compareTo(bigInteger) < 0) {
                reportOverflowLong();
            }
            this._numberLong = bigInteger.longValue();
        } else if ((this._numTypesValid & 8) != 0) {
            double d = _getNumberDouble();
            if (d < -9.223372036854776E18d || d > 9.223372036854776E18d) {
                reportOverflowLong();
            }
            this._numberLong = (long) d;
        } else if ((this._numTypesValid & 16) != 0) {
            BigDecimal bigDecimal = _getBigDecimal();
            if (BD_MIN_LONG.compareTo(bigDecimal) > 0 || BD_MAX_LONG.compareTo(bigDecimal) < 0) {
                reportOverflowLong();
            }
            this._numberLong = bigDecimal.longValue();
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 2;
    }

    protected void convertNumberToBigInteger() throws IOException {
        if ((this._numTypesValid & 16) != 0) {
            this._numberBigInt = _convertBigDecimalToBigInteger(_getBigDecimal());
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberInt);
        } else if ((this._numTypesValid & 8) != 0) {
            if (this._numberString != null) {
                this._numberBigInt = _convertBigDecimalToBigInteger(_getBigDecimal());
            } else {
                this._numberBigInt = _convertBigDecimalToBigInteger(BigDecimal.valueOf(_getNumberDouble()));
            }
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 4;
    }

    protected void convertNumberToDouble() throws IOException {
        if ((this._numTypesValid & 16) != 0) {
            if (this._numberString != null) {
                this._numberDouble = _getNumberDouble();
            } else {
                this._numberDouble = _getBigDecimal().doubleValue();
            }
        } else if ((this._numTypesValid & 4) != 0) {
            if (this._numberString != null) {
                this._numberDouble = _getNumberDouble();
            } else {
                this._numberDouble = _getBigInteger().doubleValue();
            }
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberDouble = this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberDouble = this._numberInt;
        } else if ((this._numTypesValid & 32) != 0) {
            if (this._numberString != null) {
                this._numberDouble = _getNumberDouble();
            } else {
                this._numberDouble = _getNumberFloat();
            }
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 8;
    }

    protected void convertNumberToFloat() throws IOException {
        if ((this._numTypesValid & 16) != 0) {
            if (this._numberString != null) {
                this._numberFloat = _getNumberFloat();
            } else {
                this._numberFloat = _getBigDecimal().floatValue();
            }
        } else if ((this._numTypesValid & 4) != 0) {
            if (this._numberString != null) {
                this._numberFloat = _getNumberFloat();
            } else {
                this._numberFloat = _getBigInteger().floatValue();
            }
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberFloat = (float) this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberFloat = this._numberInt;
        } else if ((this._numTypesValid & 8) != 0) {
            if (this._numberString != null) {
                this._numberFloat = _getNumberFloat();
            } else {
                this._numberFloat = (float) _getNumberDouble();
            }
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 32;
    }

    protected void convertNumberToBigDecimal() throws IOException {
        if ((this._numTypesValid & 8) != 0) {
            String numStr = this._numberString == null ? getText() : this._numberString;
            this._numberBigDecimal = NumberInput.parseBigDecimal(numStr, isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberBigDecimal = new BigDecimal(_getBigInteger());
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
        } else {
            _throwInternal();
        }
        this._numTypesValid |= 16;
    }

    protected BigInteger _convertBigDecimalToBigInteger(BigDecimal bigDec) throws IOException {
        this._streamReadConstraints.validateBigIntegerScale(bigDec.scale());
        return bigDec.toBigInteger();
    }

    protected BigInteger _getBigInteger() throws JsonParseException {
        if (this._numberBigInt != null) {
            return this._numberBigInt;
        }
        if (this._numberString == null) {
            throw new IllegalStateException("cannot get BigInteger from current parser state");
        }
        try {
            this._numberBigInt = NumberInput.parseBigInteger(this._numberString, isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
        } catch (NumberFormatException nex) {
            _wrapError("Malformed numeric value (" + _longNumberDesc(this._numberString) + ")", nex);
        }
        this._numberString = null;
        return this._numberBigInt;
    }

    protected BigDecimal _getBigDecimal() throws JsonParseException {
        if (this._numberBigDecimal != null) {
            return this._numberBigDecimal;
        }
        if (this._numberString == null) {
            throw new IllegalStateException("cannot get BigDecimal from current parser state");
        }
        try {
            this._numberBigDecimal = NumberInput.parseBigDecimal(this._numberString, isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
        } catch (NumberFormatException nex) {
            _wrapError("Malformed numeric value (" + _longNumberDesc(this._numberString) + ")", nex);
        }
        this._numberString = null;
        return this._numberBigDecimal;
    }

    protected double _getNumberDouble() throws JsonParseException {
        if (this._numberString != null) {
            try {
                this._numberDouble = NumberInput.parseDouble(this._numberString, isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
            } catch (NumberFormatException nex) {
                _wrapError("Malformed numeric value (" + _longNumberDesc(this._numberString) + ")", nex);
            }
            this._numberString = null;
        }
        return this._numberDouble;
    }

    protected float _getNumberFloat() throws JsonParseException {
        if (this._numberString != null) {
            try {
                this._numberFloat = NumberInput.parseFloat(this._numberString, isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER));
            } catch (NumberFormatException nex) {
                _wrapError("Malformed numeric value (" + _longNumberDesc(this._numberString) + ")", nex);
            }
            this._numberString = null;
        }
        return this._numberFloat;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void createChildArrayContext(int lineNr, int colNr) throws IOException {
        this._parsingContext = this._parsingContext.createChildArrayContext(lineNr, colNr);
        this._streamReadConstraints.validateNestingDepth(this._parsingContext.getNestingDepth());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void createChildObjectContext(int lineNr, int colNr) throws IOException {
        this._parsingContext = this._parsingContext.createChildObjectContext(lineNr, colNr);
        this._streamReadConstraints.validateNestingDepth(this._parsingContext.getNestingDepth());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _reportMismatchedEndMarker(int actCh, char expCh) throws JsonParseException {
        JsonReadContext ctxt = getParsingContext();
        _reportError(String.format("Unexpected close marker '%s': expected '%c' (for %s starting at %s)", Character.valueOf((char) actCh), Character.valueOf(expCh), ctxt.typeDesc(), ctxt.startLocation(_contentReference())));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public char _handleUnrecognizedCharacterEscape(char ch2) throws JsonProcessingException {
        if (isEnabled(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
            return ch2;
        }
        if (ch2 == '\'' && isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)) {
            return ch2;
        }
        _reportError("Unrecognized character escape " + _getCharDesc(ch2));
        return ch2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _throwUnquotedSpace(int i, String ctxtDesc) throws JsonParseException {
        if (!isEnabled(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
            char c = (char) i;
            String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
            _reportError(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String _validJsonTokenList() throws IOException {
        return _validJsonValueList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String _validJsonValueList() throws IOException {
        if (isEnabled(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS)) {
            return "(JSON String, Number (or 'NaN'/'INF'/'+INF'), Array, Object or token 'null', 'true' or 'false')";
        }
        return "(JSON String, Number, Array, Object or token 'null', 'true' or 'false')";
    }

    protected char _decodeEscaped() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int _decodeBase64Escape(Base64Variant b64variant, int ch2, int index) throws IOException {
        if (ch2 != 92) {
            throw reportInvalidBase64Char(b64variant, ch2, index);
        }
        int unescaped = _decodeEscaped();
        if (unescaped <= 32 && index == 0) {
            return -1;
        }
        int bits = b64variant.decodeBase64Char(unescaped);
        if (bits < 0 && bits != -2) {
            throw reportInvalidBase64Char(b64variant, unescaped, index);
        }
        return bits;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int _decodeBase64Escape(Base64Variant b64variant, char ch2, int index) throws IOException {
        if (ch2 != '\\') {
            throw reportInvalidBase64Char(b64variant, ch2, index);
        }
        char unescaped = _decodeEscaped();
        if (unescaped <= ' ' && index == 0) {
            return -1;
        }
        int bits = b64variant.decodeBase64Char(unescaped);
        if (bits < 0 && (bits != -2 || index < 2)) {
            throw reportInvalidBase64Char(b64variant, unescaped, index);
        }
        return bits;
    }

    protected IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch2, int bindex) throws IllegalArgumentException {
        return reportInvalidBase64Char(b64variant, ch2, bindex, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IllegalArgumentException reportInvalidBase64Char(Base64Variant b64variant, int ch2, int bindex, String msg) throws IllegalArgumentException {
        String base;
        if (ch2 <= 32) {
            base = String.format("Illegal white space character (code 0x%s) as character #%d of 4-char base64 unit: can only used between units", Integer.toHexString(ch2), Integer.valueOf(bindex + 1));
        } else if (b64variant.usesPaddingChar(ch2)) {
            base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        } else if (!Character.isDefined(ch2) || Character.isISOControl(ch2)) {
            base = "Illegal character (code 0x" + Integer.toHexString(ch2) + ") in base64 content";
        } else {
            base = "Illegal character '" + ((char) ch2) + "' (code 0x" + Integer.toHexString(ch2) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        return new IllegalArgumentException(base);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _handleBase64MissingPadding(Base64Variant b64variant) throws IOException {
        _reportError(b64variant.missingPaddingMessage());
    }

    @Deprecated
    protected Object _getSourceReference() {
        if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
            return this._ioContext.contentReference().getRawContent();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ContentReference _contentReference() {
        if (JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION.enabledIn(this._features)) {
            return this._ioContext.contentReference();
        }
        return ContentReference.unknown();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int[] growArrayBy(int[] arr, int more) {
        if (arr == null) {
            return new int[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }

    @Deprecated
    protected void loadMoreGuaranteed() throws IOException {
        if (!loadMore()) {
            _reportInvalidEOF();
        }
    }

    @Deprecated
    protected boolean loadMore() throws IOException {
        return false;
    }

    protected void _finishString() throws IOException {
    }
}
