package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ReadConstrainedTextBuffer;
import com.fasterxml.jackson.core.util.TextBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/IOContext.class */
public class IOContext {
    protected final ContentReference _contentReference;

    @Deprecated
    protected final Object _sourceRef;
    protected JsonEncoding _encoding;
    protected final boolean _managedResource;
    protected final BufferRecycler _bufferRecycler;
    protected final StreamReadConstraints _streamReadConstraints;
    protected byte[] _readIOBuffer;
    protected byte[] _writeEncodingBuffer;
    protected byte[] _base64Buffer;
    protected char[] _tokenCBuffer;
    protected char[] _concatCBuffer;
    protected char[] _nameCopyBuffer;

    public IOContext(StreamReadConstraints src, BufferRecycler br, ContentReference contentRef, boolean managedResource) {
        this._streamReadConstraints = src == null ? StreamReadConstraints.defaults() : src;
        this._bufferRecycler = br;
        this._contentReference = contentRef;
        this._sourceRef = contentRef.getRawContent();
        this._managedResource = managedResource;
    }

    @Deprecated
    public IOContext(BufferRecycler br, ContentReference contentRef, boolean managedResource) {
        this(null, br, contentRef, managedResource);
    }

    @Deprecated
    public IOContext(BufferRecycler br, Object rawContent, boolean managedResource) {
        this(br, ContentReference.rawReference(rawContent), managedResource);
    }

    public StreamReadConstraints streamReadConstraints() {
        return this._streamReadConstraints;
    }

    public void setEncoding(JsonEncoding enc) {
        this._encoding = enc;
    }

    public IOContext withEncoding(JsonEncoding enc) {
        this._encoding = enc;
        return this;
    }

    public JsonEncoding getEncoding() {
        return this._encoding;
    }

    public boolean isResourceManaged() {
        return this._managedResource;
    }

    public ContentReference contentReference() {
        return this._contentReference;
    }

    @Deprecated
    public Object getSourceReference() {
        return this._sourceRef;
    }

    public TextBuffer constructTextBuffer() {
        return new TextBuffer(this._bufferRecycler);
    }

    public TextBuffer constructReadConstrainedTextBuffer() {
        return new ReadConstrainedTextBuffer(this._streamReadConstraints, this._bufferRecycler);
    }

    public byte[] allocReadIOBuffer() {
        _verifyAlloc(this._readIOBuffer);
        byte[] allocByteBuffer = this._bufferRecycler.allocByteBuffer(0);
        this._readIOBuffer = allocByteBuffer;
        return allocByteBuffer;
    }

    public byte[] allocReadIOBuffer(int minSize) {
        _verifyAlloc(this._readIOBuffer);
        byte[] allocByteBuffer = this._bufferRecycler.allocByteBuffer(0, minSize);
        this._readIOBuffer = allocByteBuffer;
        return allocByteBuffer;
    }

    public byte[] allocWriteEncodingBuffer() {
        _verifyAlloc(this._writeEncodingBuffer);
        byte[] allocByteBuffer = this._bufferRecycler.allocByteBuffer(1);
        this._writeEncodingBuffer = allocByteBuffer;
        return allocByteBuffer;
    }

    public byte[] allocWriteEncodingBuffer(int minSize) {
        _verifyAlloc(this._writeEncodingBuffer);
        byte[] allocByteBuffer = this._bufferRecycler.allocByteBuffer(1, minSize);
        this._writeEncodingBuffer = allocByteBuffer;
        return allocByteBuffer;
    }

    public byte[] allocBase64Buffer() {
        _verifyAlloc(this._base64Buffer);
        byte[] allocByteBuffer = this._bufferRecycler.allocByteBuffer(3);
        this._base64Buffer = allocByteBuffer;
        return allocByteBuffer;
    }

    public byte[] allocBase64Buffer(int minSize) {
        _verifyAlloc(this._base64Buffer);
        byte[] allocByteBuffer = this._bufferRecycler.allocByteBuffer(3, minSize);
        this._base64Buffer = allocByteBuffer;
        return allocByteBuffer;
    }

    public char[] allocTokenBuffer() {
        _verifyAlloc(this._tokenCBuffer);
        char[] allocCharBuffer = this._bufferRecycler.allocCharBuffer(0);
        this._tokenCBuffer = allocCharBuffer;
        return allocCharBuffer;
    }

    public char[] allocTokenBuffer(int minSize) {
        _verifyAlloc(this._tokenCBuffer);
        char[] allocCharBuffer = this._bufferRecycler.allocCharBuffer(0, minSize);
        this._tokenCBuffer = allocCharBuffer;
        return allocCharBuffer;
    }

    public char[] allocConcatBuffer() {
        _verifyAlloc(this._concatCBuffer);
        char[] allocCharBuffer = this._bufferRecycler.allocCharBuffer(1);
        this._concatCBuffer = allocCharBuffer;
        return allocCharBuffer;
    }

    public char[] allocNameCopyBuffer(int minSize) {
        _verifyAlloc(this._nameCopyBuffer);
        char[] allocCharBuffer = this._bufferRecycler.allocCharBuffer(3, minSize);
        this._nameCopyBuffer = allocCharBuffer;
        return allocCharBuffer;
    }

    public void releaseReadIOBuffer(byte[] buf) {
        if (buf != null) {
            _verifyRelease(buf, this._readIOBuffer);
            this._readIOBuffer = null;
            this._bufferRecycler.releaseByteBuffer(0, buf);
        }
    }

    public void releaseWriteEncodingBuffer(byte[] buf) {
        if (buf != null) {
            _verifyRelease(buf, this._writeEncodingBuffer);
            this._writeEncodingBuffer = null;
            this._bufferRecycler.releaseByteBuffer(1, buf);
        }
    }

    public void releaseBase64Buffer(byte[] buf) {
        if (buf != null) {
            _verifyRelease(buf, this._base64Buffer);
            this._base64Buffer = null;
            this._bufferRecycler.releaseByteBuffer(3, buf);
        }
    }

    public void releaseTokenBuffer(char[] buf) {
        if (buf != null) {
            _verifyRelease(buf, this._tokenCBuffer);
            this._tokenCBuffer = null;
            this._bufferRecycler.releaseCharBuffer(0, buf);
        }
    }

    public void releaseConcatBuffer(char[] buf) {
        if (buf != null) {
            _verifyRelease(buf, this._concatCBuffer);
            this._concatCBuffer = null;
            this._bufferRecycler.releaseCharBuffer(1, buf);
        }
    }

    public void releaseNameCopyBuffer(char[] buf) {
        if (buf != null) {
            _verifyRelease(buf, this._nameCopyBuffer);
            this._nameCopyBuffer = null;
            this._bufferRecycler.releaseCharBuffer(3, buf);
        }
    }

    protected final void _verifyAlloc(Object buffer) {
        if (buffer != null) {
            throw new IllegalStateException("Trying to call same allocXxx() method second time");
        }
    }

    protected final void _verifyRelease(byte[] toRelease, byte[] src) {
        if (toRelease != src && toRelease.length < src.length) {
            throw wrongBuf();
        }
    }

    protected final void _verifyRelease(char[] toRelease, char[] src) {
        if (toRelease != src && toRelease.length < src.length) {
            throw wrongBuf();
        }
    }

    private IllegalArgumentException wrongBuf() {
        return new IllegalArgumentException("Trying to release buffer smaller than original");
    }
}
