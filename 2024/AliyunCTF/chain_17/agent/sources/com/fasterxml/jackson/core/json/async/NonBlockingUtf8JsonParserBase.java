package com.fasterxml.jackson.core.json.async;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.CharTypes;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.VersionUtil;
import java.io.IOException;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/async/NonBlockingUtf8JsonParserBase.class */
public abstract class NonBlockingUtf8JsonParserBase extends NonBlockingJsonParserBase {
    private static final int FEAT_MASK_TRAILING_COMMA = JsonParser.Feature.ALLOW_TRAILING_COMMA.getMask();
    private static final int FEAT_MASK_LEADING_ZEROS = JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS.getMask();
    private static final int FEAT_MASK_ALLOW_MISSING = JsonParser.Feature.ALLOW_MISSING_VALUES.getMask();
    private static final int FEAT_MASK_ALLOW_SINGLE_QUOTES = JsonParser.Feature.ALLOW_SINGLE_QUOTES.getMask();
    private static final int FEAT_MASK_ALLOW_UNQUOTED_NAMES = JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES.getMask();
    private static final int FEAT_MASK_ALLOW_JAVA_COMMENTS = JsonParser.Feature.ALLOW_COMMENTS.getMask();
    private static final int FEAT_MASK_ALLOW_YAML_COMMENTS = JsonParser.Feature.ALLOW_YAML_COMMENTS.getMask();
    private static final int[] _icUTF8 = CharTypes.getInputCodeUtf8();
    protected static final int[] _icLatin1 = CharTypes.getInputCodeLatin1();
    protected int _origBufferLen;

    protected abstract byte getNextSignedByteFromBuffer();

    protected abstract int getNextUnsignedByteFromBuffer();

    protected abstract byte getByteFromBuffer(int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public NonBlockingUtf8JsonParserBase(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
        super(ctxt, parserFeatures, sym);
    }

    public final boolean needMoreInput() {
        return this._inputPtr >= this._inputEnd && !this._endOfInput;
    }

    public void endOfInput() {
        this._endOfInput = true;
    }

    @Override // com.fasterxml.jackson.core.base.ParserBase
    protected char _decodeEscaped() throws IOException {
        VersionUtil.throwInternal();
        return ' ';
    }

    @Override // com.fasterxml.jackson.core.base.ParserMinimalBase, com.fasterxml.jackson.core.JsonParser
    public JsonToken nextToken() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            if (this._closed) {
                return null;
            }
            if (this._endOfInput) {
                if (this._currToken == JsonToken.NOT_AVAILABLE) {
                    return _finishTokenWithEOF();
                }
                return _eofAsNextToken();
            }
            return JsonToken.NOT_AVAILABLE;
        }
        if (this._currToken == JsonToken.NOT_AVAILABLE) {
            return _finishToken();
        }
        this._numTypesValid = 0;
        this._tokenInputTotal = this._currInputProcessed + this._inputPtr;
        this._binaryValue = null;
        int ch2 = getNextUnsignedByteFromBuffer();
        switch (this._majorState) {
            case 0:
                return _startDocument(ch2);
            case 1:
                return _startValue(ch2);
            case 2:
                return _startFieldName(ch2);
            case 3:
                return _startFieldNameAfterComma(ch2);
            case 4:
                return _startValueExpectColon(ch2);
            case 5:
                return _startValue(ch2);
            case 6:
                return _startValueExpectComma(ch2);
            default:
                VersionUtil.throwInternal();
                return null;
        }
    }

    protected final JsonToken _finishToken() throws IOException {
        switch (this._minorState) {
            case 1:
                return _finishBOM(this._pending32);
            case 2:
            case 3:
            case 6:
            case 11:
            case 20:
            case 21:
            case 27:
            case 28:
            case 29:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 46:
            case 47:
            case 48:
            case 49:
            default:
                VersionUtil.throwInternal();
                return null;
            case 4:
                return _startFieldName(getNextUnsignedByteFromBuffer());
            case 5:
                return _startFieldNameAfterComma(getNextUnsignedByteFromBuffer());
            case 7:
                return _parseEscapedName(this._quadLength, this._pending32, this._pendingBytes);
            case 8:
                return _finishFieldWithEscape();
            case 9:
                return _finishAposName(this._quadLength, this._pending32, this._pendingBytes);
            case 10:
                return _finishUnquotedName(this._quadLength, this._pending32, this._pendingBytes);
            case 12:
                return _startValue(getNextUnsignedByteFromBuffer());
            case 13:
                return _startValueExpectComma(getNextUnsignedByteFromBuffer());
            case 14:
                return _startValueExpectColon(getNextUnsignedByteFromBuffer());
            case 15:
                return _startValueAfterComma(getNextUnsignedByteFromBuffer());
            case 16:
                return _finishKeywordToken("null", this._pending32, JsonToken.VALUE_NULL);
            case 17:
                return _finishKeywordToken("true", this._pending32, JsonToken.VALUE_TRUE);
            case 18:
                return _finishKeywordToken("false", this._pending32, JsonToken.VALUE_FALSE);
            case 19:
                return _finishNonStdToken(this._nonStdTokenType, this._pending32);
            case 22:
                return _finishNumberPlus(getNextUnsignedByteFromBuffer());
            case 23:
                return _finishNumberMinus(getNextUnsignedByteFromBuffer());
            case 24:
                return _finishNumberLeadingZeroes();
            case 25:
                return _finishNumberLeadingNegZeroes();
            case 26:
                return _finishNumberIntegralPart(this._textBuffer.getBufferWithoutReset(), this._textBuffer.getCurrentSegmentSize());
            case 30:
                return _finishFloatFraction();
            case 31:
                return _finishFloatExponent(true, getNextUnsignedByteFromBuffer());
            case 32:
                return _finishFloatExponent(false, getNextUnsignedByteFromBuffer());
            case 40:
                return _finishRegularString();
            case 41:
                int c = _decodeSplitEscaped(this._quoted32, this._quotedDigits);
                if (c < 0) {
                    return JsonToken.NOT_AVAILABLE;
                }
                this._textBuffer.append((char) c);
                if (this._minorStateAfterSplit == 45) {
                    return _finishAposString();
                }
                return _finishRegularString();
            case 42:
                this._textBuffer.append((char) _decodeUTF8_2(this._pending32, getNextSignedByteFromBuffer()));
                if (this._minorStateAfterSplit == 45) {
                    return _finishAposString();
                }
                return _finishRegularString();
            case 43:
                if (!_decodeSplitUTF8_3(this._pending32, this._pendingBytes, getNextSignedByteFromBuffer())) {
                    return JsonToken.NOT_AVAILABLE;
                }
                if (this._minorStateAfterSplit == 45) {
                    return _finishAposString();
                }
                return _finishRegularString();
            case 44:
                if (!_decodeSplitUTF8_4(this._pending32, this._pendingBytes, getNextSignedByteFromBuffer())) {
                    return JsonToken.NOT_AVAILABLE;
                }
                if (this._minorStateAfterSplit == 45) {
                    return _finishAposString();
                }
                return _finishRegularString();
            case 45:
                return _finishAposString();
            case 50:
                return _finishErrorToken();
            case 51:
                return _startSlashComment(this._pending32);
            case 52:
                return _finishCComment(this._pending32, true);
            case 53:
                return _finishCComment(this._pending32, false);
            case 54:
                return _finishCppComment(this._pending32);
            case 55:
                return _finishHashComment(this._pending32);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0009. Please report as an issue. */
    protected final JsonToken _finishTokenWithEOF() throws IOException {
        JsonToken t = this._currToken;
        switch (this._minorState) {
            case 3:
                return _eofAsNextToken();
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 20:
            case 21:
            case 22:
            case 23:
            case 27:
            case 28:
            case 29:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 51:
            default:
                _reportInvalidEOF(": was expecting rest of token (internal state: " + this._minorState + ")", this._currToken);
                return t;
            case 12:
                return _eofAsNextToken();
            case 16:
                return _finishKeywordTokenWithEOF("null", this._pending32, JsonToken.VALUE_NULL);
            case 17:
                return _finishKeywordTokenWithEOF("true", this._pending32, JsonToken.VALUE_TRUE);
            case 18:
                return _finishKeywordTokenWithEOF("false", this._pending32, JsonToken.VALUE_FALSE);
            case 19:
                return _finishNonStdTokenWithEOF(this._nonStdTokenType, this._pending32);
            case 24:
            case 25:
                return _valueCompleteInt(0, CustomBooleanEditor.VALUE_0);
            case 26:
                int len = this._textBuffer.getCurrentSegmentSize();
                if (this._numberNegative) {
                    len--;
                }
                this._intLength = len;
                return _valueComplete(JsonToken.VALUE_NUMBER_INT);
            case 30:
                this._expLength = 0;
                return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
            case 31:
                _reportInvalidEOF(": was expecting fraction after exponent marker", JsonToken.VALUE_NUMBER_FLOAT);
                _reportInvalidEOF(": was expecting closing '*/' for comment", JsonToken.NOT_AVAILABLE);
                return _eofAsNextToken();
            case 32:
                return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
            case 50:
                return _finishErrorTokenWithEOF();
            case 52:
            case 53:
                _reportInvalidEOF(": was expecting closing '*/' for comment", JsonToken.NOT_AVAILABLE);
                return _eofAsNextToken();
            case 54:
            case 55:
                return _eofAsNextToken();
        }
    }

    private final JsonToken _startDocument(int ch2) throws IOException {
        int ch3 = ch2 & Const.MAX_ARRAY_DIMENSIONS;
        if (ch3 == 239 && this._minorState != 1) {
            return _finishBOM(1);
        }
        while (ch3 <= 32) {
            if (ch3 != 32) {
                if (ch3 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch3 == 13) {
                    this._currInputRowAlt++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch3 != 9) {
                    _throwInvalidSpace(ch3);
                }
            }
            if (this._inputPtr >= this._inputEnd) {
                this._minorState = 3;
                if (this._closed) {
                    return null;
                }
                if (this._endOfInput) {
                    return _eofAsNextToken();
                }
                return JsonToken.NOT_AVAILABLE;
            }
            ch3 = getNextUnsignedByteFromBuffer();
        }
        return _startValue(ch3);
    }

    private final JsonToken _finishBOM(int bytesHandled) throws IOException {
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            switch (bytesHandled) {
                case 1:
                    if (ch2 == 187) {
                        break;
                    } else {
                        _reportError("Unexpected byte 0x%02x following 0xEF; should get 0xBB as second byte UTF-8 BOM", Integer.valueOf(ch2));
                        break;
                    }
                case 2:
                    if (ch2 == 191) {
                        break;
                    } else {
                        _reportError("Unexpected byte 0x%02x following 0xEF 0xBB; should get 0xBF as third byte of UTF-8 BOM", Integer.valueOf(ch2));
                        break;
                    }
                case 3:
                    this._currInputProcessed -= 3;
                    return _startDocument(ch2);
            }
            bytesHandled++;
        }
        this._pending32 = bytesHandled;
        this._minorState = 1;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    private final JsonToken _startFieldName(int ch2) throws IOException {
        String n;
        if (ch2 <= 32) {
            ch2 = _skipWS(ch2);
            if (ch2 <= 0) {
                this._minorState = 4;
                return this._currToken;
            }
        }
        _updateTokenLocation();
        if (ch2 != 34) {
            if (ch2 == 125) {
                return _closeObjectScope();
            }
            return _handleOddName(ch2);
        }
        if (this._inputPtr + 13 <= this._inputEnd && (n = _fastParseName()) != null) {
            return _fieldComplete(n);
        }
        return _parseEscapedName(0, 0, 0);
    }

    private final JsonToken _startFieldNameAfterComma(int ch2) throws IOException {
        String n;
        if (ch2 <= 32) {
            ch2 = _skipWS(ch2);
            if (ch2 <= 0) {
                this._minorState = 5;
                return this._currToken;
            }
        }
        if (ch2 != 44) {
            if (ch2 == 125) {
                return _closeObjectScope();
            }
            if (ch2 == 35) {
                return _finishHashComment(5);
            }
            if (ch2 == 47) {
                return _startSlashComment(5);
            }
            _reportUnexpectedChar(ch2, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
        }
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            this._minorState = 4;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch3 = getByteFromBuffer(ptr);
        this._inputPtr = ptr + 1;
        if (ch3 <= 32) {
            ch3 = _skipWS(ch3);
            if (ch3 <= 0) {
                this._minorState = 4;
                return this._currToken;
            }
        }
        _updateTokenLocation();
        if (ch3 != 34) {
            if (ch3 == 125 && (this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
                return _closeObjectScope();
            }
            return _handleOddName(ch3);
        }
        if (this._inputPtr + 13 <= this._inputEnd && (n = _fastParseName()) != null) {
            return _fieldComplete(n);
        }
        return _parseEscapedName(0, 0, 0);
    }

    private final JsonToken _startValue(int ch2) throws IOException {
        if (ch2 <= 32) {
            ch2 = _skipWS(ch2);
            if (ch2 <= 0) {
                this._minorState = 12;
                return this._currToken;
            }
        }
        _updateTokenLocation();
        this._parsingContext.expectComma();
        if (ch2 == 34) {
            return _startString();
        }
        switch (ch2) {
            case 35:
                return _finishHashComment(12);
            case 43:
                return _startPositiveNumber();
            case 45:
                return _startNegativeNumber();
            case 46:
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
                    return _startFloatThatStartsWithPeriod();
                }
                break;
            case 47:
                return _startSlashComment(12);
            case 48:
                return _startNumberLeadingZero();
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                return _startPositiveNumber(ch2);
            case 91:
                return _startArrayScope();
            case 93:
                return _closeArrayScope();
            case 102:
                return _startFalseToken();
            case Opcodes.FDIV /* 110 */:
                return _startNullToken();
            case 116:
                return _startTrueToken();
            case 123:
                return _startObjectScope();
            case 125:
                return _closeObjectScope();
        }
        return _startUnexpectedValue(false, ch2);
    }

    private final JsonToken _startValueExpectComma(int ch2) throws IOException {
        if (ch2 <= 32) {
            ch2 = _skipWS(ch2);
            if (ch2 <= 0) {
                this._minorState = 13;
                return this._currToken;
            }
        }
        if (ch2 != 44) {
            if (ch2 == 93) {
                return _closeArrayScope();
            }
            if (ch2 == 125) {
                return _closeObjectScope();
            }
            if (ch2 == 47) {
                return _startSlashComment(13);
            }
            if (ch2 == 35) {
                return _finishHashComment(13);
            }
            _reportUnexpectedChar(ch2, "was expecting comma to separate " + this._parsingContext.typeDesc() + " entries");
        }
        this._parsingContext.expectComma();
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            this._minorState = 15;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch3 = getByteFromBuffer(ptr);
        this._inputPtr = ptr + 1;
        if (ch3 <= 32) {
            ch3 = _skipWS(ch3);
            if (ch3 <= 0) {
                this._minorState = 15;
                return this._currToken;
            }
        }
        _updateTokenLocation();
        if (ch3 == 34) {
            return _startString();
        }
        switch (ch3) {
            case 35:
                return _finishHashComment(15);
            case 43:
                return _startPositiveNumber();
            case 45:
                return _startNegativeNumber();
            case 47:
                return _startSlashComment(15);
            case 48:
                return _startNumberLeadingZero();
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                return _startPositiveNumber(ch3);
            case 91:
                return _startArrayScope();
            case 93:
                if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
                    return _closeArrayScope();
                }
                break;
            case 102:
                return _startFalseToken();
            case Opcodes.FDIV /* 110 */:
                return _startNullToken();
            case 116:
                return _startTrueToken();
            case 123:
                return _startObjectScope();
            case 125:
                if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
                    return _closeObjectScope();
                }
                break;
        }
        return _startUnexpectedValue(true, ch3);
    }

    private final JsonToken _startValueExpectColon(int ch2) throws IOException {
        if (ch2 <= 32) {
            ch2 = _skipWS(ch2);
            if (ch2 <= 0) {
                this._minorState = 14;
                return this._currToken;
            }
        }
        if (ch2 != 58) {
            if (ch2 == 47) {
                return _startSlashComment(14);
            }
            if (ch2 == 35) {
                return _finishHashComment(14);
            }
            _reportUnexpectedChar(ch2, "was expecting a colon to separate field name and value");
        }
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            this._minorState = 12;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch3 = getByteFromBuffer(ptr);
        this._inputPtr = ptr + 1;
        if (ch3 <= 32) {
            ch3 = _skipWS(ch3);
            if (ch3 <= 0) {
                this._minorState = 12;
                return this._currToken;
            }
        }
        _updateTokenLocation();
        if (ch3 == 34) {
            return _startString();
        }
        switch (ch3) {
            case 35:
                return _finishHashComment(12);
            case 43:
                return _startPositiveNumber();
            case 45:
                return _startNegativeNumber();
            case 47:
                return _startSlashComment(12);
            case 48:
                return _startNumberLeadingZero();
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                return _startPositiveNumber(ch3);
            case 91:
                return _startArrayScope();
            case 102:
                return _startFalseToken();
            case Opcodes.FDIV /* 110 */:
                return _startNullToken();
            case 116:
                return _startTrueToken();
            case 123:
                return _startObjectScope();
            default:
                return _startUnexpectedValue(false, ch3);
        }
    }

    private final JsonToken _startValueAfterComma(int ch2) throws IOException {
        if (ch2 <= 32) {
            ch2 = _skipWS(ch2);
            if (ch2 <= 0) {
                this._minorState = 15;
                return this._currToken;
            }
        }
        _updateTokenLocation();
        if (ch2 == 34) {
            return _startString();
        }
        switch (ch2) {
            case 35:
                return _finishHashComment(15);
            case 43:
                return _startPositiveNumber();
            case 45:
                return _startNegativeNumber();
            case 47:
                return _startSlashComment(15);
            case 48:
                return _startNumberLeadingZero();
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                return _startPositiveNumber(ch2);
            case 91:
                return _startArrayScope();
            case 93:
                if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
                    return _closeArrayScope();
                }
                break;
            case 102:
                return _startFalseToken();
            case Opcodes.FDIV /* 110 */:
                return _startNullToken();
            case 116:
                return _startTrueToken();
            case 123:
                return _startObjectScope();
            case 125:
                if ((this._features & FEAT_MASK_TRAILING_COMMA) != 0) {
                    return _closeObjectScope();
                }
                break;
        }
        return _startUnexpectedValue(true, ch2);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x004b, code lost:            if (r5._parsingContext.inArray() == false) goto L24;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0001. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected com.fasterxml.jackson.core.JsonToken _startUnexpectedValue(boolean r6, int r7) throws java.io.IOException {
        /*
            r5 = this;
            r0 = r7
            switch(r0) {
                case 39: goto L7b;
                case 43: goto L8b;
                case 44: goto L51;
                case 73: goto L99;
                case 78: goto L92;
                case 93: goto L44;
                case 125: goto L78;
                default: goto La0;
            }
        L44:
            r0 = r5
            com.fasterxml.jackson.core.json.JsonReadContext r0 = r0._parsingContext
            boolean r0 = r0.inArray()
            if (r0 != 0) goto L51
            goto La0
        L51:
            r0 = r5
            com.fasterxml.jackson.core.json.JsonReadContext r0 = r0._parsingContext
            boolean r0 = r0.inRoot()
            if (r0 != 0) goto L78
            r0 = r5
            int r0 = r0._features
            int r1 = com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase.FEAT_MASK_ALLOW_MISSING
            r0 = r0 & r1
            if (r0 == 0) goto L78
            r0 = r5
            r1 = r0
            int r1 = r1._inputPtr
            r2 = 1
            int r1 = r1 - r2
            r0._inputPtr = r1
            r0 = r5
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.VALUE_NULL
            com.fasterxml.jackson.core.JsonToken r0 = r0._valueComplete(r1)
            return r0
        L78:
            goto La0
        L7b:
            r0 = r5
            int r0 = r0._features
            int r1 = com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase.FEAT_MASK_ALLOW_SINGLE_QUOTES
            r0 = r0 & r1
            if (r0 == 0) goto La0
            r0 = r5
            com.fasterxml.jackson.core.JsonToken r0 = r0._startAposString()
            return r0
        L8b:
            r0 = r5
            r1 = 2
            r2 = 1
            com.fasterxml.jackson.core.JsonToken r0 = r0._finishNonStdToken(r1, r2)
            return r0
        L92:
            r0 = r5
            r1 = 0
            r2 = 1
            com.fasterxml.jackson.core.JsonToken r0 = r0._finishNonStdToken(r1, r2)
            return r0
        L99:
            r0 = r5
            r1 = 1
            r2 = 1
            com.fasterxml.jackson.core.JsonToken r0 = r0._finishNonStdToken(r1, r2)
            return r0
        La0:
            r0 = r5
            r1 = r7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = r2
            r3.<init>()
            java.lang.String r3 = "expected a valid value "
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r5
            java.lang.String r3 = r3._validJsonValueList()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0._reportUnexpectedChar(r1, r2)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase._startUnexpectedValue(boolean, int):com.fasterxml.jackson.core.JsonToken");
    }

    private final int _skipWS(int ch2) throws IOException {
        do {
            if (ch2 != 32) {
                if (ch2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 == 13) {
                    this._currInputRowAlt++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 != 9) {
                    _throwInvalidSpace(ch2);
                }
            }
            if (this._inputPtr >= this._inputEnd) {
                this._currToken = JsonToken.NOT_AVAILABLE;
                return 0;
            }
            ch2 = getNextUnsignedByteFromBuffer();
        } while (ch2 <= 32);
        return ch2;
    }

    private final JsonToken _startSlashComment(int fromMinorState) throws IOException {
        if ((this._features & FEAT_MASK_ALLOW_JAVA_COMMENTS) == 0) {
            _reportUnexpectedChar(47, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_COMMENTS' not enabled for parser)");
        }
        if (this._inputPtr >= this._inputEnd) {
            this._pending32 = fromMinorState;
            this._minorState = 51;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch2 = getNextSignedByteFromBuffer();
        if (ch2 == 42) {
            return _finishCComment(fromMinorState, false);
        }
        if (ch2 == 47) {
            return _finishCppComment(fromMinorState);
        }
        _reportUnexpectedChar(ch2 & Const.MAX_ARRAY_DIMENSIONS, "was expecting either '*' or '/' for a comment");
        return null;
    }

    private final JsonToken _finishHashComment(int fromMinorState) throws IOException {
        if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) == 0) {
            _reportUnexpectedChar(35, "maybe a (non-standard) comment? (not recognized as one since Feature 'ALLOW_YAML_COMMENTS' not enabled for parser)");
        }
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (ch2 < 32) {
                if (ch2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 == 13) {
                    this._currInputRowAlt++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 != 9) {
                    _throwInvalidSpace(ch2);
                }
                return _startAfterComment(fromMinorState);
            }
        }
        this._minorState = 55;
        this._pending32 = fromMinorState;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    private final JsonToken _finishCppComment(int fromMinorState) throws IOException {
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (ch2 < 32) {
                if (ch2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 == 13) {
                    this._currInputRowAlt++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 != 9) {
                    _throwInvalidSpace(ch2);
                }
                return _startAfterComment(fromMinorState);
            }
        }
        this._minorState = 54;
        this._pending32 = fromMinorState;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    private final JsonToken _finishCComment(int fromMinorState, boolean gotStar) throws IOException {
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (ch2 < 32) {
                if (ch2 == 10) {
                    this._currInputRow++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 == 13) {
                    this._currInputRowAlt++;
                    this._currInputRowStart = this._inputPtr;
                } else if (ch2 != 9) {
                    _throwInvalidSpace(ch2);
                }
            } else if (ch2 == 42) {
                gotStar = true;
            } else if (ch2 == 47 && gotStar) {
                return _startAfterComment(fromMinorState);
            }
            gotStar = false;
        }
        this._minorState = gotStar ? 52 : 53;
        this._pending32 = fromMinorState;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    private final JsonToken _startAfterComment(int fromMinorState) throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            this._minorState = fromMinorState;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch2 = getNextUnsignedByteFromBuffer();
        switch (fromMinorState) {
            case 4:
                return _startFieldName(ch2);
            case 5:
                return _startFieldNameAfterComma(ch2);
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                VersionUtil.throwInternal();
                return null;
            case 12:
                return _startValue(ch2);
            case 13:
                return _startValueExpectComma(ch2);
            case 14:
                return _startValueExpectColon(ch2);
            case 15:
                return _startValueAfterComma(ch2);
        }
    }

    protected JsonToken _startFalseToken() throws IOException {
        int ch2;
        int ptr = this._inputPtr;
        if (ptr + 4 < this._inputEnd) {
            int ptr2 = ptr + 1;
            if (getByteFromBuffer(ptr) == 97) {
                int ptr3 = ptr2 + 1;
                if (getByteFromBuffer(ptr2) == 108) {
                    int ptr4 = ptr3 + 1;
                    if (getByteFromBuffer(ptr3) == 115) {
                        int ptr5 = ptr4 + 1;
                        if (getByteFromBuffer(ptr4) == 101 && ((ch2 = getByteFromBuffer(ptr5) & Const.MAX_ARRAY_DIMENSIONS) < 48 || ch2 == 93 || ch2 == 125)) {
                            this._inputPtr = ptr5;
                            return _valueComplete(JsonToken.VALUE_FALSE);
                        }
                    }
                }
            }
        }
        this._minorState = 18;
        return _finishKeywordToken("false", 1, JsonToken.VALUE_FALSE);
    }

    protected JsonToken _startTrueToken() throws IOException {
        int ch2;
        int ptr = this._inputPtr;
        if (ptr + 3 < this._inputEnd) {
            int ptr2 = ptr + 1;
            if (getByteFromBuffer(ptr) == 114) {
                int ptr3 = ptr2 + 1;
                if (getByteFromBuffer(ptr2) == 117) {
                    int ptr4 = ptr3 + 1;
                    if (getByteFromBuffer(ptr3) == 101 && ((ch2 = getByteFromBuffer(ptr4) & Const.MAX_ARRAY_DIMENSIONS) < 48 || ch2 == 93 || ch2 == 125)) {
                        this._inputPtr = ptr4;
                        return _valueComplete(JsonToken.VALUE_TRUE);
                    }
                }
            }
        }
        this._minorState = 17;
        return _finishKeywordToken("true", 1, JsonToken.VALUE_TRUE);
    }

    protected JsonToken _startNullToken() throws IOException {
        int ch2;
        int ptr = this._inputPtr;
        if (ptr + 3 < this._inputEnd) {
            int ptr2 = ptr + 1;
            if (getByteFromBuffer(ptr) == 117) {
                int ptr3 = ptr2 + 1;
                if (getByteFromBuffer(ptr2) == 108) {
                    int ptr4 = ptr3 + 1;
                    if (getByteFromBuffer(ptr3) == 108 && ((ch2 = getByteFromBuffer(ptr4) & Const.MAX_ARRAY_DIMENSIONS) < 48 || ch2 == 93 || ch2 == 125)) {
                        this._inputPtr = ptr4;
                        return _valueComplete(JsonToken.VALUE_NULL);
                    }
                }
            }
        }
        this._minorState = 16;
        return _finishKeywordToken("null", 1, JsonToken.VALUE_NULL);
    }

    protected JsonToken _finishKeywordToken(String expToken, int matched, JsonToken result) throws IOException {
        int end = expToken.length();
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getByteFromBuffer(this._inputPtr);
            if (matched == end) {
                if (ch2 < 48 || ch2 == 93 || ch2 == 125) {
                    return _valueComplete(result);
                }
            } else if (ch2 == expToken.charAt(matched)) {
                matched++;
                this._inputPtr++;
            }
            this._minorState = 50;
            this._textBuffer.resetWithCopy(expToken, 0, matched);
            return _finishErrorToken();
        }
        this._pending32 = matched;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    protected JsonToken _finishKeywordTokenWithEOF(String expToken, int matched, JsonToken result) throws IOException {
        if (matched == expToken.length()) {
            this._currToken = result;
            return result;
        }
        this._textBuffer.resetWithCopy(expToken, 0, matched);
        return _finishErrorTokenWithEOF();
    }

    protected JsonToken _finishNonStdToken(int type, int matched) throws IOException {
        String expToken = _nonStdToken(type);
        int end = expToken.length();
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getByteFromBuffer(this._inputPtr);
            if (matched == end) {
                if (ch2 < 48 || ch2 == 93 || ch2 == 125) {
                    return _valueNonStdNumberComplete(type);
                }
            } else if (ch2 == expToken.charAt(matched)) {
                matched++;
                this._inputPtr++;
            }
            this._minorState = 50;
            this._textBuffer.resetWithCopy(expToken, 0, matched);
            return _finishErrorToken();
        }
        this._nonStdTokenType = type;
        this._pending32 = matched;
        this._minorState = 19;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    protected JsonToken _finishNonStdTokenWithEOF(int type, int matched) throws IOException {
        String expToken = _nonStdToken(type);
        if (matched == expToken.length()) {
            return _valueNonStdNumberComplete(type);
        }
        this._textBuffer.resetWithCopy(expToken, 0, matched);
        return _finishErrorTokenWithEOF();
    }

    protected JsonToken _finishErrorToken() throws IOException {
        while (this._inputPtr < this._inputEnd) {
            int i = getNextSignedByteFromBuffer();
            char ch2 = (char) i;
            if (Character.isJavaIdentifierPart(ch2)) {
                this._textBuffer.append(ch2);
                if (this._textBuffer.size() < 256) {
                }
            }
            return _reportErrorToken(this._textBuffer.contentsAsString());
        }
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    protected JsonToken _finishErrorTokenWithEOF() throws IOException {
        return _reportErrorToken(this._textBuffer.contentsAsString());
    }

    protected JsonToken _reportErrorToken(String actualToken) throws IOException {
        _reportError("Unrecognized token '%s': was expecting %s", this._textBuffer.contentsAsString(), _validJsonTokenList());
        return JsonToken.NOT_AVAILABLE;
    }

    protected JsonToken _startFloatThatStartsWithPeriod() throws IOException {
        this._numberNegative = false;
        this._intLength = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        return _startFloat(outBuf, 0, 46);
    }

    protected JsonToken _startPositiveNumber(int ch2) throws IOException {
        this._numberNegative = false;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = (char) ch2;
        if (this._inputPtr >= this._inputEnd) {
            this._minorState = 26;
            this._textBuffer.setCurrentLength(1);
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int outPtr = 1;
        byte byteFromBuffer = getByteFromBuffer(this._inputPtr);
        while (true) {
            int ch3 = byteFromBuffer & 255;
            if (ch3 < 48) {
                if (ch3 == 46) {
                    this._intLength = outPtr;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch3);
                }
            } else if (ch3 > 57) {
                if (ch3 == 101 || ch3 == 69) {
                    this._intLength = outPtr;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch3);
                }
            } else {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) ch3;
                int i2 = this._inputPtr + 1;
                this._inputPtr = i2;
                if (i2 >= this._inputEnd) {
                    this._minorState = 26;
                    this._textBuffer.setCurrentLength(outPtr);
                    JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
                    this._currToken = jsonToken2;
                    return jsonToken2;
                }
                byteFromBuffer = getByteFromBuffer(this._inputPtr);
            }
        }
        this._intLength = outPtr;
        this._textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _startNegativeNumber() throws IOException {
        this._numberNegative = true;
        if (this._inputPtr >= this._inputEnd) {
            this._minorState = 23;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch2 = getNextUnsignedByteFromBuffer();
        if (ch2 <= 48) {
            if (ch2 == 48) {
                return _finishNumberLeadingNegZeroes();
            }
            _reportUnexpectedNumberChar(ch2, "expected digit (0-9) to follow minus sign, for valid numeric value");
        } else if (ch2 > 57) {
            if (ch2 == 73) {
                return _finishNonStdToken(3, 2);
            }
            _reportUnexpectedNumberChar(ch2, "expected digit (0-9) to follow minus sign, for valid numeric value");
        }
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = '-';
        outBuf[1] = (char) ch2;
        if (this._inputPtr >= this._inputEnd) {
            this._minorState = 26;
            this._textBuffer.setCurrentLength(2);
            this._intLength = 1;
            JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken2;
            return jsonToken2;
        }
        int ch3 = getByteFromBuffer(this._inputPtr);
        int outPtr = 2;
        while (true) {
            if (ch3 < 48) {
                if (ch3 == 46) {
                    this._intLength = outPtr - 1;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch3);
                }
            } else if (ch3 > 57) {
                if (ch3 == 101 || ch3 == 69) {
                    this._intLength = outPtr - 1;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch3);
                }
            } else {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) ch3;
                int i2 = this._inputPtr + 1;
                this._inputPtr = i2;
                if (i2 >= this._inputEnd) {
                    this._minorState = 26;
                    this._textBuffer.setCurrentLength(outPtr);
                    JsonToken jsonToken3 = JsonToken.NOT_AVAILABLE;
                    this._currToken = jsonToken3;
                    return jsonToken3;
                }
                ch3 = getByteFromBuffer(this._inputPtr) & Const.MAX_ARRAY_DIMENSIONS;
            }
        }
        this._intLength = outPtr - 1;
        this._textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _startPositiveNumber() throws IOException {
        this._numberNegative = false;
        if (this._inputPtr >= this._inputEnd) {
            this._minorState = 22;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ch2 = getNextUnsignedByteFromBuffer();
        if (ch2 <= 48) {
            if (ch2 == 48) {
                if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
                }
                return _finishNumberLeadingPosZeroes();
            }
            _reportUnexpectedNumberChar(ch2, "expected digit (0-9) to follow plus sign, for valid numeric value");
        } else if (ch2 > 57) {
            if (ch2 == 73) {
                return _finishNonStdToken(2, 2);
            }
            _reportUnexpectedNumberChar(ch2, "expected digit (0-9) to follow plus sign, for valid numeric value");
        }
        if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
            _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
        }
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = '+';
        outBuf[1] = (char) ch2;
        if (this._inputPtr >= this._inputEnd) {
            this._minorState = 26;
            this._textBuffer.setCurrentLength(2);
            this._intLength = 1;
            JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken2;
            return jsonToken2;
        }
        int ch3 = getByteFromBuffer(this._inputPtr);
        int outPtr = 2;
        while (true) {
            if (ch3 < 48) {
                if (ch3 == 46) {
                    this._intLength = outPtr - 1;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch3);
                }
            } else if (ch3 > 57) {
                if (ch3 == 101 || ch3 == 69) {
                    this._intLength = outPtr - 1;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch3);
                }
            } else {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) ch3;
                int i2 = this._inputPtr + 1;
                this._inputPtr = i2;
                if (i2 >= this._inputEnd) {
                    this._minorState = 26;
                    this._textBuffer.setCurrentLength(outPtr);
                    JsonToken jsonToken3 = JsonToken.NOT_AVAILABLE;
                    this._currToken = jsonToken3;
                    return jsonToken3;
                }
                ch3 = getByteFromBuffer(this._inputPtr) & Const.MAX_ARRAY_DIMENSIONS;
            }
        }
        this._intLength = outPtr - 1;
        this._textBuffer.setCurrentLength(outPtr);
        return _valueComplete(JsonToken.VALUE_NUMBER_INT);
    }

    protected JsonToken _startNumberLeadingZero() throws IOException {
        int ptr = this._inputPtr;
        if (ptr >= this._inputEnd) {
            this._minorState = 24;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        int ptr2 = ptr + 1;
        int ch2 = getByteFromBuffer(ptr) & 255;
        if (ch2 < 48) {
            if (ch2 == 46) {
                this._inputPtr = ptr2;
                this._intLength = 1;
                char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
                outBuf[0] = '0';
                return _startFloat(outBuf, 1, ch2);
            }
        } else if (ch2 > 57) {
            if (ch2 == 101 || ch2 == 69) {
                this._inputPtr = ptr2;
                this._intLength = 1;
                char[] outBuf2 = this._textBuffer.emptyAndGetCurrentSegment();
                outBuf2[0] = '0';
                return _startFloat(outBuf2, 1, ch2);
            }
            if (ch2 != 93 && ch2 != 125) {
                _reportUnexpectedNumberChar(ch2, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
            }
        } else {
            return _finishNumberLeadingZeroes();
        }
        return _valueCompleteInt(0, CustomBooleanEditor.VALUE_0);
    }

    protected JsonToken _finishNumberMinus(int ch2) throws IOException {
        return _finishNumberPlusMinus(ch2, true);
    }

    protected JsonToken _finishNumberPlus(int ch2) throws IOException {
        return _finishNumberPlusMinus(ch2, false);
    }

    protected JsonToken _finishNumberPlusMinus(int ch2, boolean negative) throws IOException {
        if (ch2 <= 48) {
            if (ch2 == 48) {
                if (negative) {
                    return _finishNumberLeadingNegZeroes();
                }
                if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
                }
                return _finishNumberLeadingPosZeroes();
            }
            if (ch2 == 46 && isEnabled(JsonReadFeature.ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
                if (negative) {
                    this._inputPtr--;
                    return _finishNumberLeadingNegZeroes();
                }
                if (!isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
                    _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
                }
                this._inputPtr--;
                return _finishNumberLeadingPosZeroes();
            }
            String message = negative ? "expected digit (0-9) to follow minus sign, for valid numeric value" : "expected digit (0-9) for valid numeric value";
            _reportUnexpectedNumberChar(ch2, message);
        } else if (ch2 > 57) {
            if (ch2 == 73) {
                int token = negative ? 3 : 2;
                return _finishNonStdToken(token, 2);
            }
            String message2 = negative ? "expected digit (0-9) to follow minus sign, for valid numeric value" : "expected digit (0-9) for valid numeric value";
            _reportUnexpectedNumberChar(ch2, message2);
        }
        if (!negative && !isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.mappedFeature())) {
            _reportUnexpectedNumberChar(43, "JSON spec does not allow numbers to have plus signs: enable `JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS` to allow");
        }
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        outBuf[0] = negative ? '-' : '+';
        outBuf[1] = (char) ch2;
        this._intLength = 1;
        return _finishNumberIntegralPart(outBuf, 2);
    }

    protected JsonToken _finishNumberLeadingZeroes() throws IOException {
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (ch2 < 48) {
                if (ch2 == 46) {
                    char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
                    outBuf[0] = '0';
                    this._intLength = 1;
                    return _startFloat(outBuf, 1, ch2);
                }
            } else if (ch2 > 57) {
                if (ch2 == 101 || ch2 == 69) {
                    char[] outBuf2 = this._textBuffer.emptyAndGetCurrentSegment();
                    outBuf2[0] = '0';
                    this._intLength = 1;
                    return _startFloat(outBuf2, 1, ch2);
                }
                if (ch2 != 93 && ch2 != 125) {
                    _reportUnexpectedNumberChar(ch2, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
                }
            } else {
                if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
                    reportInvalidNumber("Leading zeroes not allowed");
                }
                if (ch2 != 48) {
                    char[] outBuf3 = this._textBuffer.emptyAndGetCurrentSegment();
                    outBuf3[0] = (char) ch2;
                    this._intLength = 1;
                    return _finishNumberIntegralPart(outBuf3, 1);
                }
            }
            this._inputPtr--;
            return _valueCompleteInt(0, CustomBooleanEditor.VALUE_0);
        }
        this._minorState = 24;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    protected JsonToken _finishNumberLeadingNegZeroes() throws IOException {
        return _finishNumberLeadingPosNegZeroes(true);
    }

    protected JsonToken _finishNumberLeadingPosZeroes() throws IOException {
        return _finishNumberLeadingPosNegZeroes(false);
    }

    protected JsonToken _finishNumberLeadingPosNegZeroes(boolean negative) throws IOException {
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (ch2 < 48) {
                if (ch2 == 46) {
                    char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
                    outBuf[0] = negative ? '-' : '+';
                    outBuf[1] = '0';
                    this._intLength = 1;
                    return _startFloat(outBuf, 2, ch2);
                }
            } else if (ch2 > 57) {
                if (ch2 == 101 || ch2 == 69) {
                    char[] outBuf2 = this._textBuffer.emptyAndGetCurrentSegment();
                    outBuf2[0] = negative ? '-' : '+';
                    outBuf2[1] = '0';
                    this._intLength = 1;
                    return _startFloat(outBuf2, 2, ch2);
                }
                if (ch2 != 93 && ch2 != 125) {
                    _reportUnexpectedNumberChar(ch2, "expected digit (0-9), decimal point (.) or exponent indicator (e/E) to follow '0'");
                }
            } else {
                if ((this._features & FEAT_MASK_LEADING_ZEROS) == 0) {
                    reportInvalidNumber("Leading zeroes not allowed");
                }
                if (ch2 != 48) {
                    char[] outBuf3 = this._textBuffer.emptyAndGetCurrentSegment();
                    outBuf3[0] = negative ? '-' : '+';
                    outBuf3[1] = (char) ch2;
                    this._intLength = 1;
                    return _finishNumberIntegralPart(outBuf3, 2);
                }
            }
            this._inputPtr--;
            return _valueCompleteInt(0, CustomBooleanEditor.VALUE_0);
        }
        this._minorState = negative ? 25 : 24;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    protected JsonToken _finishNumberIntegralPart(char[] outBuf, int outPtr) throws IOException {
        int negMod = this._numberNegative ? -1 : 0;
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getByteFromBuffer(this._inputPtr) & 255;
            if (ch2 < 48) {
                if (ch2 == 46) {
                    this._intLength = outPtr + negMod;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch2);
                }
            } else if (ch2 > 57) {
                if (ch2 == 101 || ch2 == 69) {
                    this._intLength = outPtr + negMod;
                    this._inputPtr++;
                    return _startFloat(outBuf, outPtr, ch2);
                }
            } else {
                this._inputPtr++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) ch2;
            }
            this._intLength = outPtr + negMod;
            this._textBuffer.setCurrentLength(outPtr);
            return _valueComplete(JsonToken.VALUE_NUMBER_INT);
        }
        this._minorState = 26;
        this._textBuffer.setCurrentLength(outPtr);
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    protected JsonToken _startFloat(char[] outBuf, int outPtr, int ch2) throws IOException {
        int fractLen = 0;
        if (ch2 == 46) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.expandCurrentSegment();
            }
            outPtr++;
            outBuf[outPtr] = '.';
            while (this._inputPtr < this._inputEnd) {
                int ch3 = getNextSignedByteFromBuffer();
                if (ch3 < 48 || ch3 > 57) {
                    ch2 = ch3 & Const.MAX_ARRAY_DIMENSIONS;
                    if (fractLen == 0 && !isEnabled(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
                        _reportUnexpectedNumberChar(ch2, "Decimal point not followed by a digit");
                    }
                } else {
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.expandCurrentSegment();
                    }
                    int i = outPtr;
                    outPtr++;
                    outBuf[i] = (char) ch3;
                    fractLen++;
                }
            }
            this._textBuffer.setCurrentLength(outPtr);
            this._minorState = 30;
            this._fractLength = fractLen;
            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
            this._currToken = jsonToken;
            return jsonToken;
        }
        this._fractLength = fractLen;
        int expLen = 0;
        if (ch2 == 101 || ch2 == 69) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.expandCurrentSegment();
            }
            int i2 = outPtr;
            outPtr++;
            outBuf[i2] = (char) ch2;
            if (this._inputPtr >= this._inputEnd) {
                this._textBuffer.setCurrentLength(outPtr);
                this._minorState = 31;
                this._expLength = 0;
                JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
                this._currToken = jsonToken2;
                return jsonToken2;
            }
            int ch4 = getNextSignedByteFromBuffer();
            if (ch4 == 45 || ch4 == 43) {
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                outPtr++;
                outBuf[outPtr] = (char) ch4;
                if (this._inputPtr >= this._inputEnd) {
                    this._textBuffer.setCurrentLength(outPtr);
                    this._minorState = 32;
                    this._expLength = 0;
                    JsonToken jsonToken3 = JsonToken.NOT_AVAILABLE;
                    this._currToken = jsonToken3;
                    return jsonToken3;
                }
                ch4 = getNextSignedByteFromBuffer();
            }
            while (ch4 >= 48 && ch4 <= 57) {
                expLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                int i3 = outPtr;
                outPtr++;
                outBuf[i3] = (char) ch4;
                if (this._inputPtr >= this._inputEnd) {
                    this._textBuffer.setCurrentLength(outPtr);
                    this._minorState = 32;
                    this._expLength = expLen;
                    JsonToken jsonToken4 = JsonToken.NOT_AVAILABLE;
                    this._currToken = jsonToken4;
                    return jsonToken4;
                }
                ch4 = getNextSignedByteFromBuffer();
            }
            int ch5 = ch4 & Const.MAX_ARRAY_DIMENSIONS;
            if (expLen == 0) {
                _reportUnexpectedNumberChar(ch5, "Exponent indicator not followed by a digit");
            }
        }
        this._inputPtr--;
        this._textBuffer.setCurrentLength(outPtr);
        this._expLength = expLen;
        return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    }

    protected JsonToken _finishFloatFraction() throws IOException {
        int fractLen = this._fractLength;
        char[] outBuf = this._textBuffer.getBufferWithoutReset();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        int ch2 = getNextSignedByteFromBuffer();
        boolean loop = true;
        while (loop) {
            if (ch2 >= 48 && ch2 <= 57) {
                fractLen++;
                if (outPtr >= outBuf.length) {
                    outBuf = this._textBuffer.expandCurrentSegment();
                }
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) ch2;
                if (this._inputPtr >= this._inputEnd) {
                    this._textBuffer.setCurrentLength(outPtr);
                    this._fractLength = fractLen;
                    return JsonToken.NOT_AVAILABLE;
                }
                ch2 = getNextSignedByteFromBuffer();
            } else if (ch2 == 102 || ch2 == 100 || ch2 == 70 || ch2 == 68) {
                _reportUnexpectedNumberChar(ch2, "JSON does not support parsing numbers that have 'f' or 'd' suffixes");
            } else if (ch2 == 46) {
                _reportUnexpectedNumberChar(ch2, "Cannot parse number with more than one decimal point");
            } else {
                loop = false;
            }
        }
        if (fractLen == 0 && !isEnabled(JsonReadFeature.ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.mappedFeature())) {
            _reportUnexpectedNumberChar(ch2, "Decimal point not followed by a digit");
        }
        this._fractLength = fractLen;
        this._textBuffer.setCurrentLength(outPtr);
        if (ch2 == 101 || ch2 == 69) {
            this._textBuffer.append((char) ch2);
            this._expLength = 0;
            if (this._inputPtr >= this._inputEnd) {
                this._minorState = 31;
                return JsonToken.NOT_AVAILABLE;
            }
            this._minorState = 32;
            return _finishFloatExponent(true, getNextUnsignedByteFromBuffer());
        }
        this._inputPtr--;
        this._textBuffer.setCurrentLength(outPtr);
        this._expLength = 0;
        return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    }

    protected JsonToken _finishFloatExponent(boolean checkSign, int ch2) throws IOException {
        if (checkSign) {
            this._minorState = 32;
            if (ch2 == 45 || ch2 == 43) {
                this._textBuffer.append((char) ch2);
                if (this._inputPtr >= this._inputEnd) {
                    this._minorState = 32;
                    this._expLength = 0;
                    return JsonToken.NOT_AVAILABLE;
                }
                ch2 = getNextSignedByteFromBuffer();
            }
        }
        char[] outBuf = this._textBuffer.getBufferWithoutReset();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        int expLen = this._expLength;
        while (ch2 >= 48 && ch2 <= 57) {
            expLen++;
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.expandCurrentSegment();
            }
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) ch2;
            if (this._inputPtr >= this._inputEnd) {
                this._textBuffer.setCurrentLength(outPtr);
                this._expLength = expLen;
                return JsonToken.NOT_AVAILABLE;
            }
            ch2 = getNextSignedByteFromBuffer();
        }
        int ch3 = ch2 & Const.MAX_ARRAY_DIMENSIONS;
        if (expLen == 0) {
            _reportUnexpectedNumberChar(ch3, "Exponent indicator not followed by a digit");
        }
        this._inputPtr--;
        this._textBuffer.setCurrentLength(outPtr);
        this._expLength = expLen;
        return _valueComplete(JsonToken.VALUE_NUMBER_FLOAT);
    }

    private final String _fastParseName() throws IOException {
        int[] codes = _icLatin1;
        int ptr = this._inputPtr;
        int ptr2 = ptr + 1;
        int q0 = getByteFromBuffer(ptr) & 255;
        if (codes[q0] == 0) {
            int ptr3 = ptr2 + 1;
            int i = getByteFromBuffer(ptr2) & 255;
            if (codes[i] == 0) {
                int q = (q0 << 8) | i;
                int ptr4 = ptr3 + 1;
                int i2 = getByteFromBuffer(ptr3) & 255;
                if (codes[i2] == 0) {
                    int q2 = (q << 8) | i2;
                    int ptr5 = ptr4 + 1;
                    int i3 = getByteFromBuffer(ptr4) & 255;
                    if (codes[i3] == 0) {
                        int q3 = (q2 << 8) | i3;
                        int ptr6 = ptr5 + 1;
                        int i4 = getByteFromBuffer(ptr5) & 255;
                        if (codes[i4] == 0) {
                            this._quad1 = q3;
                            return _parseMediumName(ptr6, i4);
                        }
                        if (i4 == 34) {
                            this._inputPtr = ptr6;
                            return _findName(q3, 4);
                        }
                        return null;
                    }
                    if (i3 == 34) {
                        this._inputPtr = ptr5;
                        return _findName(q2, 3);
                    }
                    return null;
                }
                if (i2 == 34) {
                    this._inputPtr = ptr4;
                    return _findName(q, 2);
                }
                return null;
            }
            if (i == 34) {
                this._inputPtr = ptr3;
                return _findName(q0, 1);
            }
            return null;
        }
        if (q0 == 34) {
            this._inputPtr = ptr2;
            return "";
        }
        return null;
    }

    private final String _parseMediumName(int ptr, int q2) throws IOException {
        int[] codes = _icLatin1;
        int ptr2 = ptr + 1;
        int i = getByteFromBuffer(ptr) & 255;
        if (codes[i] == 0) {
            int q22 = (q2 << 8) | i;
            int ptr3 = ptr2 + 1;
            int i2 = getByteFromBuffer(ptr2) & 255;
            if (codes[i2] == 0) {
                int q23 = (q22 << 8) | i2;
                int ptr4 = ptr3 + 1;
                int i3 = getByteFromBuffer(ptr3) & 255;
                if (codes[i3] == 0) {
                    int q24 = (q23 << 8) | i3;
                    int ptr5 = ptr4 + 1;
                    int i4 = getByteFromBuffer(ptr4) & 255;
                    if (codes[i4] == 0) {
                        return _parseMediumName2(ptr5, i4, q24);
                    }
                    if (i4 == 34) {
                        this._inputPtr = ptr5;
                        return _findName(this._quad1, q24, 4);
                    }
                    return null;
                }
                if (i3 == 34) {
                    this._inputPtr = ptr4;
                    return _findName(this._quad1, q23, 3);
                }
                return null;
            }
            if (i2 == 34) {
                this._inputPtr = ptr3;
                return _findName(this._quad1, q22, 2);
            }
            return null;
        }
        if (i == 34) {
            this._inputPtr = ptr2;
            return _findName(this._quad1, q2, 1);
        }
        return null;
    }

    private final String _parseMediumName2(int ptr, int q3, int q2) throws IOException {
        int[] codes = _icLatin1;
        int ptr2 = ptr + 1;
        int i = getByteFromBuffer(ptr) & 255;
        if (codes[i] != 0) {
            if (i == 34) {
                this._inputPtr = ptr2;
                return _findName(this._quad1, q2, q3, 1);
            }
            return null;
        }
        int q32 = (q3 << 8) | i;
        int ptr3 = ptr2 + 1;
        int i2 = getByteFromBuffer(ptr2) & 255;
        if (codes[i2] != 0) {
            if (i2 == 34) {
                this._inputPtr = ptr3;
                return _findName(this._quad1, q2, q32, 2);
            }
            return null;
        }
        int q33 = (q32 << 8) | i2;
        int ptr4 = ptr3 + 1;
        int i3 = getByteFromBuffer(ptr3) & 255;
        if (codes[i3] != 0) {
            if (i3 == 34) {
                this._inputPtr = ptr4;
                return _findName(this._quad1, q2, q33, 3);
            }
            return null;
        }
        int q34 = (q33 << 8) | i3;
        int ptr5 = ptr4 + 1;
        if ((getByteFromBuffer(ptr4) & Const.MAX_ARRAY_DIMENSIONS) == 34) {
            this._inputPtr = ptr5;
            return _findName(this._quad1, q2, q34, 4);
        }
        return null;
    }

    private final JsonToken _parseEscapedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
        int[] quads = this._quadBuffer;
        int[] codes = _icLatin1;
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (codes[ch2] == 0) {
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
            } else if (ch2 != 34) {
                if (ch2 != 92) {
                    _throwUnquotedSpace(ch2, "name");
                } else {
                    ch2 = _decodeCharEscape();
                    if (ch2 < 0) {
                        this._minorState = 8;
                        this._minorStateAfterSplit = 7;
                        this._quadLength = qlen;
                        this._pending32 = currQuad;
                        this._pendingBytes = currQuadBytes;
                        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
                        this._currToken = jsonToken;
                        return jsonToken;
                    }
                }
                if (qlen >= quads.length) {
                    int[] growArrayBy2 = growArrayBy(quads, quads.length);
                    quads = growArrayBy2;
                    this._quadBuffer = growArrayBy2;
                }
                if (ch2 > 127) {
                    if (currQuadBytes >= 4) {
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
                if (currQuadBytes < 4) {
                    currQuadBytes++;
                    currQuad = (currQuad << 8) | ch2;
                } else {
                    int i4 = qlen;
                    qlen++;
                    quads[i4] = currQuad;
                    currQuad = ch2;
                    currQuadBytes = 1;
                }
            } else {
                if (currQuadBytes > 0) {
                    if (qlen >= quads.length) {
                        int[] growArrayBy3 = growArrayBy(quads, quads.length);
                        quads = growArrayBy3;
                        this._quadBuffer = growArrayBy3;
                    }
                    int i5 = qlen;
                    qlen++;
                    quads[i5] = _padLastQuad(currQuad, currQuadBytes);
                } else if (qlen == 0) {
                    return _fieldComplete("");
                }
                String name = this._symbols.findName(quads, qlen);
                if (name == null) {
                    name = _addName(quads, qlen, currQuadBytes);
                }
                return _fieldComplete(name);
            }
        }
        this._quadLength = qlen;
        this._pending32 = currQuad;
        this._pendingBytes = currQuadBytes;
        this._minorState = 7;
        JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken2;
        return jsonToken2;
    }

    private JsonToken _handleOddName(int ch2) throws IOException {
        switch (ch2) {
            case 35:
                if ((this._features & FEAT_MASK_ALLOW_YAML_COMMENTS) != 0) {
                    return _finishHashComment(4);
                }
                break;
            case 39:
                if ((this._features & FEAT_MASK_ALLOW_SINGLE_QUOTES) != 0) {
                    return _finishAposName(0, 0, 0);
                }
                break;
            case 47:
                return _startSlashComment(4);
            case 93:
                return _closeArrayScope();
        }
        if ((this._features & FEAT_MASK_ALLOW_UNQUOTED_NAMES) == 0) {
            char c = (char) ch2;
            _reportUnexpectedChar(c, "was expecting double-quote to start field name");
        }
        int[] codes = CharTypes.getInputCodeUtf8JsNames();
        if (codes[ch2] != 0) {
            _reportUnexpectedChar(ch2, "was expecting either valid name character (for unquoted name) or double-quote (for quoted) to start field name");
        }
        return _finishUnquotedName(0, ch2, 1);
    }

    private JsonToken _finishUnquotedName(int qlen, int currQuad, int currQuadBytes) throws IOException {
        int[] quads = this._quadBuffer;
        int[] codes = CharTypes.getInputCodeUtf8JsNames();
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getByteFromBuffer(this._inputPtr) & 255;
            if (codes[ch2] == 0) {
                this._inputPtr++;
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
            } else {
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
                    name = _addName(quads, qlen, currQuadBytes);
                }
                return _fieldComplete(name);
            }
        }
        this._quadLength = qlen;
        this._pending32 = currQuad;
        this._pendingBytes = currQuadBytes;
        this._minorState = 10;
        JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken;
        return jsonToken;
    }

    private JsonToken _finishAposName(int qlen, int currQuad, int currQuadBytes) throws IOException {
        int[] quads = this._quadBuffer;
        int[] codes = _icLatin1;
        while (this._inputPtr < this._inputEnd) {
            int ch2 = getNextUnsignedByteFromBuffer();
            if (ch2 != 39) {
                if (ch2 != 34 && codes[ch2] != 0) {
                    if (ch2 != 92) {
                        _throwUnquotedSpace(ch2, "name");
                    } else {
                        ch2 = _decodeCharEscape();
                        if (ch2 < 0) {
                            this._minorState = 8;
                            this._minorStateAfterSplit = 9;
                            this._quadLength = qlen;
                            this._pending32 = currQuad;
                            this._pendingBytes = currQuadBytes;
                            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
                            this._currToken = jsonToken;
                            return jsonToken;
                        }
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
            } else {
                if (currQuadBytes > 0) {
                    if (qlen >= quads.length) {
                        int[] growArrayBy4 = growArrayBy(quads, quads.length);
                        quads = growArrayBy4;
                        this._quadBuffer = growArrayBy4;
                    }
                    int i4 = qlen;
                    qlen++;
                    quads[i4] = _padLastQuad(currQuad, currQuadBytes);
                } else if (qlen == 0) {
                    return _fieldComplete("");
                }
                String name = this._symbols.findName(quads, qlen);
                if (name == null) {
                    name = _addName(quads, qlen, currQuadBytes);
                }
                return _fieldComplete(name);
            }
        }
        this._quadLength = qlen;
        this._pending32 = currQuad;
        this._pendingBytes = currQuadBytes;
        this._minorState = 9;
        JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken2;
        return jsonToken2;
    }

    protected final JsonToken _finishFieldWithEscape() throws IOException {
        int currQuad;
        int currQuadBytes;
        int ch2 = _decodeSplitEscaped(this._quoted32, this._quotedDigits);
        if (ch2 < 0) {
            this._minorState = 8;
            return JsonToken.NOT_AVAILABLE;
        }
        if (this._quadLength >= this._quadBuffer.length) {
            this._quadBuffer = growArrayBy(this._quadBuffer, 32);
        }
        int currQuad2 = this._pending32;
        int currQuadBytes2 = this._pendingBytes;
        if (ch2 > 127) {
            if (currQuadBytes2 >= 4) {
                int[] iArr = this._quadBuffer;
                int i = this._quadLength;
                this._quadLength = i + 1;
                iArr[i] = currQuad2;
                currQuad2 = 0;
                currQuadBytes2 = 0;
            }
            if (ch2 < 2048) {
                currQuad2 = (currQuad2 << 8) | 192 | (ch2 >> 6);
                currQuadBytes2++;
            } else {
                int currQuad3 = (currQuad2 << 8) | 224 | (ch2 >> 12);
                int currQuadBytes3 = currQuadBytes2 + 1;
                if (currQuadBytes3 >= 4) {
                    int[] iArr2 = this._quadBuffer;
                    int i2 = this._quadLength;
                    this._quadLength = i2 + 1;
                    iArr2[i2] = currQuad3;
                    currQuad3 = 0;
                    currQuadBytes3 = 0;
                }
                currQuad2 = (currQuad3 << 8) | 128 | ((ch2 >> 6) & 63);
                currQuadBytes2 = currQuadBytes3 + 1;
            }
            ch2 = 128 | (ch2 & 63);
        }
        if (currQuadBytes2 < 4) {
            currQuadBytes = currQuadBytes2 + 1;
            currQuad = (currQuad2 << 8) | ch2;
        } else {
            int[] iArr3 = this._quadBuffer;
            int i3 = this._quadLength;
            this._quadLength = i3 + 1;
            iArr3[i3] = currQuad2;
            currQuad = ch2;
            currQuadBytes = 1;
        }
        if (this._minorStateAfterSplit == 9) {
            return _finishAposName(this._quadLength, currQuad, currQuadBytes);
        }
        return _parseEscapedName(this._quadLength, currQuad, currQuadBytes);
    }

    private int _decodeSplitEscaped(int value, int bytesRead) throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            this._quoted32 = value;
            this._quotedDigits = bytesRead;
            return -1;
        }
        int c = getNextSignedByteFromBuffer();
        if (bytesRead == -1) {
            switch (c) {
                case 34:
                case 47:
                case 92:
                    return c;
                case 98:
                    return 8;
                case 102:
                    return 12;
                case Opcodes.FDIV /* 110 */:
                    return 10;
                case Opcodes.FREM /* 114 */:
                    return 13;
                case 116:
                    return 9;
                case Opcodes.LNEG /* 117 */:
                    if (this._inputPtr >= this._inputEnd) {
                        this._quotedDigits = 0;
                        this._quoted32 = 0;
                        return -1;
                    }
                    c = getNextSignedByteFromBuffer();
                    bytesRead = 0;
                    break;
                default:
                    char ch2 = (char) c;
                    return _handleUnrecognizedCharacterEscape(ch2);
            }
        }
        int i = c & Const.MAX_ARRAY_DIMENSIONS;
        while (true) {
            int c2 = i;
            int digit = CharTypes.charToHex(c2);
            if (digit < 0) {
                _reportUnexpectedChar(c2 & Const.MAX_ARRAY_DIMENSIONS, "expected a hex-digit for character escape sequence");
            }
            value = (value << 4) | digit;
            bytesRead++;
            if (bytesRead == 4) {
                return value;
            }
            if (this._inputPtr >= this._inputEnd) {
                this._quotedDigits = bytesRead;
                this._quoted32 = value;
                return -1;
            }
            i = getNextUnsignedByteFromBuffer();
        }
    }

    protected JsonToken _startString() throws IOException {
        int ptr = this._inputPtr;
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int max = Math.min(this._inputEnd, ptr + outBuf.length);
        while (true) {
            if (ptr >= max) {
                break;
            }
            int c = getByteFromBuffer(ptr) & 255;
            if (codes[c] != 0) {
                if (c == 34) {
                    this._inputPtr = ptr + 1;
                    this._textBuffer.setCurrentLength(outPtr);
                    return _valueComplete(JsonToken.VALUE_STRING);
                }
            } else {
                ptr++;
                int i = outPtr;
                outPtr++;
                outBuf[i] = (char) c;
            }
        }
        this._textBuffer.setCurrentLength(outPtr);
        this._inputPtr = ptr;
        return _finishRegularString();
    }

    private final JsonToken _finishRegularString() throws IOException {
        int[] codes = _icUTF8;
        char[] outBuf = this._textBuffer.getBufferWithoutReset();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        int ptr = this._inputPtr;
        int safeEnd = this._inputEnd - 5;
        while (ptr < this._inputEnd) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr));
            while (true) {
                if (ptr < max) {
                    int i = ptr;
                    ptr++;
                    int c = getByteFromBuffer(i) & 255;
                    if (codes[c] == 0) {
                        int i2 = outPtr;
                        outPtr++;
                        outBuf[i2] = (char) c;
                    } else {
                        if (c == 34) {
                            this._inputPtr = ptr;
                            this._textBuffer.setCurrentLength(outPtr);
                            return _valueComplete(JsonToken.VALUE_STRING);
                        }
                        if (ptr >= safeEnd) {
                            this._inputPtr = ptr;
                            this._textBuffer.setCurrentLength(outPtr);
                            if (!_decodeSplitMultiByte(c, codes[c], ptr < this._inputEnd)) {
                                this._minorStateAfterSplit = 40;
                                JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
                                this._currToken = jsonToken;
                                return jsonToken;
                            }
                            outBuf = this._textBuffer.getBufferWithoutReset();
                            outPtr = this._textBuffer.getCurrentSegmentSize();
                            ptr = this._inputPtr;
                        } else {
                            switch (codes[c]) {
                                case 1:
                                    this._inputPtr = ptr;
                                    c = _decodeFastCharEscape();
                                    ptr = this._inputPtr;
                                    break;
                                case 2:
                                    ptr++;
                                    c = _decodeUTF8_2(c, getByteFromBuffer(ptr));
                                    break;
                                case 3:
                                    int ptr2 = ptr + 1;
                                    byte byteFromBuffer = getByteFromBuffer(ptr);
                                    ptr = ptr2 + 1;
                                    c = _decodeUTF8_3(c, byteFromBuffer, getByteFromBuffer(ptr2));
                                    break;
                                case 4:
                                    int ptr3 = ptr + 1;
                                    byte byteFromBuffer2 = getByteFromBuffer(ptr);
                                    int ptr4 = ptr3 + 1;
                                    byte byteFromBuffer3 = getByteFromBuffer(ptr3);
                                    ptr = ptr4 + 1;
                                    int c2 = _decodeUTF8_4(c, byteFromBuffer2, byteFromBuffer3, getByteFromBuffer(ptr4));
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
                            int i4 = outPtr;
                            outPtr++;
                            outBuf[i4] = (char) c;
                        }
                    }
                }
            }
        }
        this._inputPtr = ptr;
        this._minorState = 40;
        this._textBuffer.setCurrentLength(outPtr);
        JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken2;
        return jsonToken2;
    }

    protected JsonToken _startAposString() throws IOException {
        int ptr = this._inputPtr;
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int[] codes = _icUTF8;
        int max = Math.min(this._inputEnd, ptr + outBuf.length);
        while (ptr < max) {
            int c = getByteFromBuffer(ptr) & 255;
            if (c == 39) {
                this._inputPtr = ptr + 1;
                this._textBuffer.setCurrentLength(outPtr);
                return _valueComplete(JsonToken.VALUE_STRING);
            }
            if (codes[c] != 0) {
                break;
            }
            ptr++;
            int i = outPtr;
            outPtr++;
            outBuf[i] = (char) c;
        }
        this._textBuffer.setCurrentLength(outPtr);
        this._inputPtr = ptr;
        return _finishAposString();
    }

    private final JsonToken _finishAposString() throws IOException {
        int[] codes = _icUTF8;
        char[] outBuf = this._textBuffer.getBufferWithoutReset();
        int outPtr = this._textBuffer.getCurrentSegmentSize();
        int ptr = this._inputPtr;
        int safeEnd = this._inputEnd - 5;
        while (ptr < this._inputEnd) {
            if (outPtr >= outBuf.length) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
            }
            int max = Math.min(this._inputEnd, ptr + (outBuf.length - outPtr));
            while (true) {
                if (ptr < max) {
                    int i = ptr;
                    ptr++;
                    int c = getByteFromBuffer(i) & 255;
                    if (codes[c] == 0 || c == 34) {
                        if (c == 39) {
                            this._inputPtr = ptr;
                            this._textBuffer.setCurrentLength(outPtr);
                            return _valueComplete(JsonToken.VALUE_STRING);
                        }
                        int i2 = outPtr;
                        outPtr++;
                        outBuf[i2] = (char) c;
                    } else if (ptr >= safeEnd) {
                        this._inputPtr = ptr;
                        this._textBuffer.setCurrentLength(outPtr);
                        if (!_decodeSplitMultiByte(c, codes[c], ptr < this._inputEnd)) {
                            this._minorStateAfterSplit = 45;
                            JsonToken jsonToken = JsonToken.NOT_AVAILABLE;
                            this._currToken = jsonToken;
                            return jsonToken;
                        }
                        outBuf = this._textBuffer.getBufferWithoutReset();
                        outPtr = this._textBuffer.getCurrentSegmentSize();
                        ptr = this._inputPtr;
                    } else {
                        switch (codes[c]) {
                            case 1:
                                this._inputPtr = ptr;
                                c = _decodeFastCharEscape();
                                ptr = this._inputPtr;
                                break;
                            case 2:
                                ptr++;
                                c = _decodeUTF8_2(c, getByteFromBuffer(ptr));
                                break;
                            case 3:
                                int ptr2 = ptr + 1;
                                byte byteFromBuffer = getByteFromBuffer(ptr);
                                ptr = ptr2 + 1;
                                c = _decodeUTF8_3(c, byteFromBuffer, getByteFromBuffer(ptr2));
                                break;
                            case 4:
                                int ptr3 = ptr + 1;
                                byte byteFromBuffer2 = getByteFromBuffer(ptr);
                                int ptr4 = ptr3 + 1;
                                byte byteFromBuffer3 = getByteFromBuffer(ptr3);
                                ptr = ptr4 + 1;
                                int c2 = _decodeUTF8_4(c, byteFromBuffer2, byteFromBuffer3, getByteFromBuffer(ptr4));
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
                        int i4 = outPtr;
                        outPtr++;
                        outBuf[i4] = (char) c;
                    }
                }
            }
        }
        this._inputPtr = ptr;
        this._minorState = 45;
        this._textBuffer.setCurrentLength(outPtr);
        JsonToken jsonToken2 = JsonToken.NOT_AVAILABLE;
        this._currToken = jsonToken2;
        return jsonToken2;
    }

    private final boolean _decodeSplitMultiByte(int c, int type, boolean gotNext) throws IOException {
        switch (type) {
            case 1:
                int c2 = _decodeSplitEscaped(0, -1);
                if (c2 < 0) {
                    this._minorState = 41;
                    return false;
                }
                this._textBuffer.append((char) c2);
                return true;
            case 2:
                if (gotNext) {
                    this._textBuffer.append((char) _decodeUTF8_2(c, getNextSignedByteFromBuffer()));
                    return true;
                }
                this._minorState = 42;
                this._pending32 = c;
                return false;
            case 3:
                int c3 = c & 15;
                if (gotNext) {
                    return _decodeSplitUTF8_3(c3, 1, getNextSignedByteFromBuffer());
                }
                this._minorState = 43;
                this._pending32 = c3;
                this._pendingBytes = 1;
                return false;
            case 4:
                int c4 = c & 7;
                if (gotNext) {
                    return _decodeSplitUTF8_4(c4, 1, getNextSignedByteFromBuffer());
                }
                this._pending32 = c4;
                this._pendingBytes = 1;
                this._minorState = 44;
                return false;
            default:
                if (c < 32) {
                    _throwUnquotedSpace(c, "string value");
                } else {
                    _reportInvalidChar(c);
                }
                this._textBuffer.append((char) c);
                return true;
        }
    }

    private final boolean _decodeSplitUTF8_3(int prev, int prevCount, int next) throws IOException {
        if (prevCount == 1) {
            if ((next & Opcodes.CHECKCAST) != 128) {
                _reportInvalidOther(next & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
            }
            prev = (prev << 6) | (next & 63);
            if (this._inputPtr >= this._inputEnd) {
                this._minorState = 43;
                this._pending32 = prev;
                this._pendingBytes = 2;
                return false;
            }
            next = getNextSignedByteFromBuffer();
        }
        if ((next & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(next & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        this._textBuffer.append((char) ((prev << 6) | (next & 63)));
        return true;
    }

    private final boolean _decodeSplitUTF8_4(int prev, int prevCount, int next) throws IOException {
        if (prevCount == 1) {
            if ((next & Opcodes.CHECKCAST) != 128) {
                _reportInvalidOther(next & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
            }
            prev = (prev << 6) | (next & 63);
            if (this._inputPtr >= this._inputEnd) {
                this._minorState = 44;
                this._pending32 = prev;
                this._pendingBytes = 2;
                return false;
            }
            prevCount = 2;
            next = getNextSignedByteFromBuffer();
        }
        if (prevCount == 2) {
            if ((next & Opcodes.CHECKCAST) != 128) {
                _reportInvalidOther(next & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
            }
            prev = (prev << 6) | (next & 63);
            if (this._inputPtr >= this._inputEnd) {
                this._minorState = 44;
                this._pending32 = prev;
                this._pendingBytes = 3;
                return false;
            }
            next = getNextSignedByteFromBuffer();
        }
        if ((next & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(next & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        int c = ((prev << 6) | (next & 63)) - 65536;
        this._textBuffer.append((char) (55296 | (c >> 10)));
        this._textBuffer.append((char) (56320 | (c & 1023)));
        return true;
    }

    private final int _decodeCharEscape() throws IOException {
        int left = this._inputEnd - this._inputPtr;
        if (left < 5) {
            return _decodeSplitEscaped(0, -1);
        }
        return _decodeFastCharEscape();
    }

    private final int _decodeFastCharEscape() throws IOException {
        int c = getNextSignedByteFromBuffer();
        switch (c) {
            case 34:
            case 47:
            case 92:
                return (char) c;
            case 98:
                return 8;
            case 102:
                return 12;
            case Opcodes.FDIV /* 110 */:
                return 10;
            case Opcodes.FREM /* 114 */:
                return 13;
            case 116:
                return 9;
            case Opcodes.LNEG /* 117 */:
                int ch2 = getNextSignedByteFromBuffer();
                int digit = CharTypes.charToHex(ch2);
                if (digit >= 0) {
                    ch2 = getNextSignedByteFromBuffer();
                    int digit2 = CharTypes.charToHex(ch2);
                    if (digit2 >= 0) {
                        int result = (digit << 4) | digit2;
                        ch2 = getNextSignedByteFromBuffer();
                        int digit3 = CharTypes.charToHex(ch2);
                        if (digit3 >= 0) {
                            int result2 = (result << 4) | digit3;
                            ch2 = getNextSignedByteFromBuffer();
                            int digit4 = CharTypes.charToHex(ch2);
                            if (digit4 >= 0) {
                                return (result2 << 4) | digit4;
                            }
                        }
                    }
                }
                _reportUnexpectedChar(ch2 & Const.MAX_ARRAY_DIMENSIONS, "expected a hex-digit for character escape sequence");
                return -1;
            default:
                return _handleUnrecognizedCharacterEscape((char) c);
        }
    }

    private final int _decodeUTF8_2(int c, int d) throws IOException {
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        return ((c & 31) << 6) | (d & 63);
    }

    private final int _decodeUTF8_3(int c, int d, int e) throws IOException {
        int c2 = c & 15;
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        int c3 = (c2 << 6) | (d & 63);
        if ((e & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(e & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        return (c3 << 6) | (e & 63);
    }

    private final int _decodeUTF8_4(int c, int d, int e, int f) throws IOException {
        if ((d & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(d & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        int c2 = ((c & 7) << 6) | (d & 63);
        if ((e & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(e & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        int c3 = (c2 << 6) | (e & 63);
        if ((f & Opcodes.CHECKCAST) != 128) {
            _reportInvalidOther(f & Const.MAX_ARRAY_DIMENSIONS, this._inputPtr);
        }
        return ((c3 << 6) | (f & 63)) - 65536;
    }
}
