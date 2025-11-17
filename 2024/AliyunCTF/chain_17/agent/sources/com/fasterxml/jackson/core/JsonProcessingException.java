package com.fasterxml.jackson.core;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/JsonProcessingException.class */
public class JsonProcessingException extends JacksonException {
    private static final long serialVersionUID = 123;
    protected JsonLocation _location;

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, rootCause);
        this._location = loc;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonProcessingException(String msg) {
        super(msg);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonProcessingException(String msg, JsonLocation loc) {
        this(msg, loc, null);
    }

    protected JsonProcessingException(String msg, Throwable rootCause) {
        this(msg, null, rootCause);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JsonProcessingException(Throwable rootCause) {
        this(null, null, rootCause);
    }

    @Override // com.fasterxml.jackson.core.JacksonException
    public JsonLocation getLocation() {
        return this._location;
    }

    public void clearLocation() {
        this._location = null;
    }

    @Override // com.fasterxml.jackson.core.JacksonException
    public String getOriginalMessage() {
        return super.getMessage();
    }

    @Override // com.fasterxml.jackson.core.JacksonException
    public Object getProcessor() {
        return null;
    }

    protected String getMessageSuffix() {
        return null;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String msg = super.getMessage();
        if (msg == null) {
            msg = "N/A";
        }
        JsonLocation loc = getLocation();
        String suffix = getMessageSuffix();
        if (loc != null || suffix != null) {
            StringBuilder sb = new StringBuilder(100);
            sb.append(msg);
            if (suffix != null) {
                sb.append(suffix);
            }
            if (loc != null) {
                sb.append('\n');
                sb.append(" at ");
                sb.append(loc.toString());
            }
            msg = sb.toString();
        }
        return msg;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return getClass().getName() + ": " + getMessage();
    }
}
