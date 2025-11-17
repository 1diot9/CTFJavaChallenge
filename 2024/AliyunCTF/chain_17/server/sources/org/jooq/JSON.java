package org.jooq;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSON.class */
public final class JSON implements Serializable {
    private final String data;

    private JSON(String data) {
        this.data = String.valueOf(data);
    }

    @NotNull
    public final String data() {
        return this.data;
    }

    @NotNull
    public static final JSON valueOf(String data) {
        return new JSON(data);
    }

    @NotNull
    public static final JSON json(String data) {
        return new JSON(data);
    }

    @Nullable
    public static final JSON jsonOrNull(String data) {
        if (data == null) {
            return null;
        }
        return json(data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JSON) {
            JSON j = (JSON) obj;
            return this.data.equals(j.data);
        }
        return false;
    }

    public String toString() {
        return String.valueOf(this.data);
    }
}
