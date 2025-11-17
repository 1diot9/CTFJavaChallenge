package com.fasterxml.jackson.core.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/exc/StreamConstraintsException.class */
public class StreamConstraintsException extends JsonProcessingException {
    private static final long serialVersionUID = 2;

    public StreamConstraintsException(String msg) {
        super(msg);
    }

    public StreamConstraintsException(String msg, JsonLocation loc) {
        super(msg, loc);
    }
}
