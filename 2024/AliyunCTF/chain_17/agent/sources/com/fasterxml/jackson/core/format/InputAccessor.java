package com.fasterxml.jackson.core.format;

import com.fasterxml.jackson.core.JsonFactory;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/format/InputAccessor.class */
public interface InputAccessor {
    boolean hasMoreBytes() throws IOException;

    byte nextByte() throws IOException;

    void reset();

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/format/InputAccessor$Std.class */
    public static class Std implements InputAccessor {
        protected final InputStream _in;
        protected final byte[] _buffer;
        protected final int _bufferedStart;
        protected int _bufferedEnd;
        protected int _ptr;

        public Std(InputStream in, byte[] buffer) {
            this._in = in;
            this._buffer = buffer;
            this._bufferedStart = 0;
            this._ptr = 0;
            this._bufferedEnd = 0;
        }

        public Std(byte[] inputDocument) {
            this(inputDocument, 0, inputDocument.length);
        }

        public Std(byte[] inputDocument, int start, int len) {
            this._in = null;
            this._buffer = inputDocument;
            this._ptr = start;
            this._bufferedStart = start;
            this._bufferedEnd = start + len;
        }

        @Override // com.fasterxml.jackson.core.format.InputAccessor
        public boolean hasMoreBytes() throws IOException {
            int amount;
            int count;
            if (this._ptr < this._bufferedEnd) {
                return true;
            }
            if (this._in == null || (amount = this._buffer.length - this._ptr) < 1 || (count = this._in.read(this._buffer, this._ptr, amount)) <= 0) {
                return false;
            }
            this._bufferedEnd += count;
            return true;
        }

        @Override // com.fasterxml.jackson.core.format.InputAccessor
        public byte nextByte() throws IOException {
            if (this._ptr >= this._bufferedEnd && !hasMoreBytes()) {
                throw new EOFException("Failed auto-detect: could not read more than " + this._ptr + " bytes (max buffer size: " + this._buffer.length + ")");
            }
            byte[] bArr = this._buffer;
            int i = this._ptr;
            this._ptr = i + 1;
            return bArr[i];
        }

        @Override // com.fasterxml.jackson.core.format.InputAccessor
        public void reset() {
            this._ptr = this._bufferedStart;
        }

        public DataFormatMatcher createMatcher(JsonFactory match, MatchStrength matchStrength) {
            return new DataFormatMatcher(this._in, this._buffer, this._bufferedStart, this._bufferedEnd - this._bufferedStart, match, matchStrength);
        }
    }
}
