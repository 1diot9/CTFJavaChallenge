package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.exc.StreamWriteException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonGenerationException.class */
public class JsonGenerationException extends StreamWriteException {
    private static final long serialVersionUID = 123;

    @Deprecated
    public JsonGenerationException(Throwable rootCause) {
        super(rootCause, (JsonGenerator) null);
    }

    @Deprecated
    public JsonGenerationException(String msg) {
        super(msg, (JsonGenerator) null);
    }

    @Deprecated
    public JsonGenerationException(String msg, Throwable rootCause) {
        super(msg, rootCause, null);
    }

    public JsonGenerationException(Throwable rootCause, JsonGenerator g) {
        super(rootCause, g);
    }

    public JsonGenerationException(String msg, JsonGenerator g) {
        super(msg, g);
        this._processor = g;
    }

    public JsonGenerationException(String msg, Throwable rootCause, JsonGenerator g) {
        super(msg, rootCause, g);
        this._processor = g;
    }

    @Override // com.fasterxml.jackson.core.exc.StreamWriteException
    public JsonGenerationException withGenerator(JsonGenerator g) {
        this._processor = g;
        return this;
    }

    @Override // com.fasterxml.jackson.core.exc.StreamWriteException, com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.core.JacksonException
    public JsonGenerator getProcessor() {
        return this._processor;
    }
}
