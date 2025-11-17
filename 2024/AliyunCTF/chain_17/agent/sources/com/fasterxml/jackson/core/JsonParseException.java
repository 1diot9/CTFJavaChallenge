package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.util.RequestPayload;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonParseException.class */
public class JsonParseException extends StreamReadException {
    private static final long serialVersionUID = 2;

    @Deprecated
    public JsonParseException(String msg, JsonLocation loc) {
        super(msg, loc, (Throwable) null);
    }

    @Deprecated
    public JsonParseException(String msg, JsonLocation loc, Throwable root) {
        super(msg, loc, root);
    }

    public JsonParseException(JsonParser p, String msg) {
        super(p, msg);
    }

    public JsonParseException(JsonParser p, String msg, Throwable root) {
        super(p, msg, root);
    }

    public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
        super(p, msg, loc);
    }

    public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable root) {
        super(p, msg, loc, root);
    }

    public JsonParseException(String msg) {
        super(msg);
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException
    public JsonParseException withParser(JsonParser p) {
        this._processor = p;
        return this;
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException
    public JsonParseException withRequestPayload(RequestPayload payload) {
        this._requestPayload = payload;
        return this;
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException, com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.core.JacksonException
    public JsonParser getProcessor() {
        return super.getProcessor();
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException
    public RequestPayload getRequestPayload() {
        return super.getRequestPayload();
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException
    public String getRequestPayloadAsString() {
        return super.getRequestPayloadAsString();
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException, com.fasterxml.jackson.core.JsonProcessingException, java.lang.Throwable
    public String getMessage() {
        return super.getMessage();
    }
}
