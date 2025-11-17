package com.fasterxml.jackson.core.json.async;

import com.fasterxml.jackson.core.async.ByteArrayFeeder;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/async/NonBlockingJsonParser.class */
public class NonBlockingJsonParser extends NonBlockingUtf8JsonParserBase implements ByteArrayFeeder {
    private byte[] _inputBuffer;

    public NonBlockingJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
        super(ctxt, parserFeatures, sym);
        this._inputBuffer = NO_BYTES;
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public ByteArrayFeeder getNonBlockingInputFeeder() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.async.ByteArrayFeeder
    public void feedInput(byte[] buf, int start, int end) throws IOException {
        if (this._inputPtr < this._inputEnd) {
            _reportError("Still have %d undecoded bytes, should not call 'feedInput'", Integer.valueOf(this._inputEnd - this._inputPtr));
        }
        if (end < start) {
            _reportError("Input end (%d) may not be before start (%d)", Integer.valueOf(end), Integer.valueOf(start));
        }
        if (this._endOfInput) {
            _reportError("Already closed, can not feed more input");
        }
        this._currInputProcessed += this._origBufferLen;
        this._currInputRowStart = start - (this._inputEnd - this._currInputRowStart);
        this._currBufferStart = start;
        this._inputBuffer = buf;
        this._inputPtr = start;
        this._inputEnd = end;
        this._origBufferLen = end - start;
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingJsonParserBase, com.fasterxml.jackson.core.JsonParser
    public int releaseBuffered(OutputStream out) throws IOException {
        int avail = this._inputEnd - this._inputPtr;
        if (avail > 0) {
            out.write(this._inputBuffer, this._inputPtr, avail);
        }
        return avail;
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase
    protected byte getNextSignedByteFromBuffer() {
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return bArr[i];
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase
    protected int getNextUnsignedByteFromBuffer() {
        byte[] bArr = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return bArr[i] & 255;
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase
    protected byte getByteFromBuffer(int ptr) {
        return this._inputBuffer[ptr];
    }
}
