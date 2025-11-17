package com.fasterxml.jackson.core.exc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.RequestPayload;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/exc/InputCoercionException.class */
public class InputCoercionException extends StreamReadException {
    private static final long serialVersionUID = 1;
    protected final JsonToken _inputType;
    protected final Class<?> _targetType;

    public InputCoercionException(JsonParser p, String msg, JsonToken inputType, Class<?> targetType) {
        super(p, msg);
        this._inputType = inputType;
        this._targetType = targetType;
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException
    public InputCoercionException withParser(JsonParser p) {
        this._processor = p;
        return this;
    }

    @Override // com.fasterxml.jackson.core.exc.StreamReadException
    public InputCoercionException withRequestPayload(RequestPayload p) {
        this._requestPayload = p;
        return this;
    }

    public JsonToken getInputType() {
        return this._inputType;
    }

    public Class<?> getTargetType() {
        return this._targetType;
    }
}
