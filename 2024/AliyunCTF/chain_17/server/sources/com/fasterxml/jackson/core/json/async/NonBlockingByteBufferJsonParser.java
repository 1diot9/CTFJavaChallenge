package com.fasterxml.jackson.core.json.async;

import com.fasterxml.jackson.core.async.ByteBufferFeeder;
import com.fasterxml.jackson.core.async.NonBlockingInputFeeder;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/json/async/NonBlockingByteBufferJsonParser.class */
public class NonBlockingByteBufferJsonParser extends NonBlockingUtf8JsonParserBase implements ByteBufferFeeder {
    private ByteBuffer _inputBuffer;

    public NonBlockingByteBufferJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
        super(ctxt, parserFeatures, sym);
        this._inputBuffer = ByteBuffer.wrap(NO_BYTES);
    }

    @Override // com.fasterxml.jackson.core.JsonParser
    public NonBlockingInputFeeder getNonBlockingInputFeeder() {
        return this;
    }

    @Override // com.fasterxml.jackson.core.async.ByteBufferFeeder
    public void feedInput(ByteBuffer byteBuffer) throws IOException {
        if (this._inputPtr < this._inputEnd) {
            _reportError("Still have %d undecoded bytes, should not call 'feedInput'", Integer.valueOf(this._inputEnd - this._inputPtr));
        }
        int start = byteBuffer.position();
        int end = byteBuffer.limit();
        if (end < start) {
            _reportError("Input end (%d) may not be before start (%d)", Integer.valueOf(end), Integer.valueOf(start));
        }
        if (this._endOfInput) {
            _reportError("Already closed, can not feed more input");
        }
        this._currInputProcessed += this._origBufferLen;
        this._currInputRowStart = start - (this._inputEnd - this._currInputRowStart);
        this._currBufferStart = start;
        this._inputBuffer = byteBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._origBufferLen = end - start;
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingJsonParserBase, com.fasterxml.jackson.core.JsonParser
    public int releaseBuffered(OutputStream out) throws IOException {
        int avail = this._inputEnd - this._inputPtr;
        if (avail > 0) {
            WritableByteChannel channel = Channels.newChannel(out);
            channel.write(this._inputBuffer);
        }
        return avail;
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase
    protected byte getNextSignedByteFromBuffer() {
        ByteBuffer byteBuffer = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return byteBuffer.get(i);
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase
    protected int getNextUnsignedByteFromBuffer() {
        ByteBuffer byteBuffer = this._inputBuffer;
        int i = this._inputPtr;
        this._inputPtr = i + 1;
        return byteBuffer.get(i) & 255;
    }

    @Override // com.fasterxml.jackson.core.json.async.NonBlockingUtf8JsonParserBase
    protected byte getByteFromBuffer(int ptr) {
        return this._inputBuffer.get(ptr);
    }
}
