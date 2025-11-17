package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Nullability.class */
public enum Nullability {
    NULL,
    NOT_NULL,
    DEFAULT;

    @NotNull
    public static Nullability of(boolean nullability) {
        return nullability ? NULL : NOT_NULL;
    }

    public boolean nullable() {
        return this != NOT_NULL;
    }
}
