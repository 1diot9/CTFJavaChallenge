package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Geography.class */
public final class Geography implements Spatial {
    private final String data;

    private Geography(String data) {
        this.data = String.valueOf(data);
    }

    @Override // org.jooq.Spatial
    @NotNull
    public final String data() {
        return this.data;
    }

    @NotNull
    public static final Geography valueOf(String data) {
        return new Geography(data);
    }

    @NotNull
    public static final Geography geography(String data) {
        return new Geography(data);
    }

    @Nullable
    public static final Geography geographyOrNull(String data) {
        if (data == null) {
            return null;
        }
        return geography(data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Geography) {
            Geography j = (Geography) obj;
            return this.data.equals(j.data);
        }
        return false;
    }

    public String toString() {
        return String.valueOf(this.data);
    }
}
