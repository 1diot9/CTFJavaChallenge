package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/ReadConstrainedTextBuffer.class */
public final class ReadConstrainedTextBuffer extends TextBuffer {
    private final StreamReadConstraints _streamReadConstraints;

    public ReadConstrainedTextBuffer(StreamReadConstraints streamReadConstraints, BufferRecycler bufferRecycler) {
        super(bufferRecycler);
        this._streamReadConstraints = streamReadConstraints;
    }

    @Override // com.fasterxml.jackson.core.util.TextBuffer
    protected void validateStringLength(int length) throws StreamConstraintsException {
        this._streamReadConstraints.validateStringLength(length);
    }
}
