package org.jooq;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/XML.class */
public final class XML implements Serializable {
    private final String data;

    private XML(String data) {
        this.data = String.valueOf(data);
    }

    @NotNull
    public final String data() {
        return this.data;
    }

    @NotNull
    public static final XML valueOf(String data) {
        return new XML(data);
    }

    @NotNull
    public static final XML xml(String data) {
        return new XML(data);
    }

    @Nullable
    public static final XML xmlOrNull(String data) {
        if (data == null) {
            return null;
        }
        return xml(data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof XML) {
            XML x = (XML) obj;
            return this.data.equals(x.data);
        }
        return false;
    }

    public String toString() {
        return String.valueOf(this.data);
    }
}
