package com.fasterxml.jackson.core.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.RequestPayload;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/exc/StreamReadException.class */
public abstract class StreamReadException extends JsonProcessingException {
    static final long serialVersionUID = 2;
    protected transient JsonParser _processor;
    protected RequestPayload _requestPayload;

    public abstract StreamReadException withParser(JsonParser jsonParser);

    public abstract StreamReadException withRequestPayload(RequestPayload requestPayload);

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamReadException(JsonParser p, String msg) {
        super(msg, p == null ? null : p.getCurrentLocation());
        this._processor = p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamReadException(JsonParser p, String msg, Throwable root) {
        super(msg, p == null ? null : p.getCurrentLocation(), root);
        this._processor = p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamReadException(JsonParser p, String msg, JsonLocation loc) {
        super(msg, loc, null);
        this._processor = p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamReadException(JsonParser p, String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
        this._processor = p;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamReadException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StreamReadException(String msg) {
        super(msg);
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException, com.fasterxml.jackson.core.JacksonException
    public JsonParser getProcessor() {
        return this._processor;
    }

    public RequestPayload getRequestPayload() {
        return this._requestPayload;
    }

    public String getRequestPayloadAsString() {
        if (this._requestPayload != null) {
            return this._requestPayload.toString();
        }
        return null;
    }

    @Override // com.fasterxml.jackson.core.JsonProcessingException, java.lang.Throwable
    public String getMessage() {
        String msg = super.getMessage();
        if (this._requestPayload != null) {
            msg = msg + "\nRequest payload : " + this._requestPayload.toString();
        }
        return msg;
    }
}
