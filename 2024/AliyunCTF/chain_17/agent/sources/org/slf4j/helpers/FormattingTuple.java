package org.slf4j.helpers;

/* loaded from: agent.jar:BOOT-INF/lib/slf4j-api-2.0.11.jar:org/slf4j/helpers/FormattingTuple.class */
public class FormattingTuple {
    public static FormattingTuple NULL = new FormattingTuple(null);
    private final String message;
    private final Throwable throwable;
    private final Object[] argArray;

    public FormattingTuple(String message) {
        this(message, null, null);
    }

    public FormattingTuple(String message, Object[] argArray, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
        this.argArray = argArray;
    }

    public String getMessage() {
        return this.message;
    }

    public Object[] getArgArray() {
        return this.argArray;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}
