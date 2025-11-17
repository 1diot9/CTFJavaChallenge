package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/DatabindException.class */
public abstract class DatabindException extends JsonProcessingException {
    private static final long serialVersionUID = 3;

    public abstract void prependPath(Object obj, String str);

    public abstract void prependPath(Object obj, int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public DatabindException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DatabindException(String msg) {
        super(msg);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DatabindException(String msg, JsonLocation loc) {
        this(msg, loc, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DatabindException(String msg, Throwable rootCause) {
        this(msg, null, rootCause);
    }
}
