package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Geometry.class */
public final class Geometry implements Spatial {
    private final String data;

    private Geometry(String data) {
        this.data = String.valueOf(data);
    }

    @Override // org.jooq.Spatial
    @NotNull
    public final String data() {
        return this.data;
    }

    @NotNull
    public static final Geometry valueOf(String data) {
        return new Geometry(data);
    }

    @NotNull
    public static final Geometry geometry(String data) {
        return new Geometry(data);
    }

    @Nullable
    public static final Geometry geometryOrNull(String data) {
        if (data == null) {
            return null;
        }
        return geometry(data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Geometry) {
            Geometry j = (Geometry) obj;
            return this.data.equals(j.data);
        }
        return false;
    }

    public String toString() {
        return String.valueOf(this.data);
    }
}
