package com.fasterxml.jackson.annotation;

/* loaded from: server.jar:BOOT-INF/lib/jackson-annotations-2.15.3.jar:com/fasterxml/jackson/annotation/OptBoolean.class */
public enum OptBoolean {
    TRUE,
    FALSE,
    DEFAULT;

    public Boolean asBoolean() {
        if (this == DEFAULT) {
            return null;
        }
        return this == TRUE ? Boolean.TRUE : Boolean.FALSE;
    }

    public boolean asPrimitive() {
        return this == TRUE;
    }

    public static OptBoolean fromBoolean(Boolean b) {
        if (b == null) {
            return DEFAULT;
        }
        return b.booleanValue() ? TRUE : FALSE;
    }

    public static boolean equals(Boolean b1, Boolean b2) {
        if (b1 == null) {
            return b2 == null;
        }
        return b1.equals(b2);
    }
}
