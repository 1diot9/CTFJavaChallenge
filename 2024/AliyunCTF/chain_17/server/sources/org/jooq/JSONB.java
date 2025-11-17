package org.jooq;

import java.io.Serializable;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.JSONValue;
import org.jooq.tools.json.ParseException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/JSONB.class */
public final class JSONB implements Serializable {
    private final String data;
    private transient Object parsed;

    private JSONB(String data) {
        this.data = String.valueOf(data);
    }

    @NotNull
    public final String data() {
        return this.data;
    }

    @NotNull
    public static final JSONB valueOf(String data) {
        return new JSONB(data);
    }

    @NotNull
    public static final JSONB jsonb(String data) {
        return new JSONB(data);
    }

    @Nullable
    public static final JSONB jsonbOrNull(String data) {
        if (data == null) {
            return null;
        }
        return jsonb(data);
    }

    private final Object parsed() {
        if (this.parsed == null) {
            try {
                this.parsed = new JSONParser().parse(this.data);
            } catch (ParseException e) {
                this.parsed = this.data;
            }
        }
        return this.parsed;
    }

    public int hashCode() {
        Object p = parsed();
        if (p == null) {
            return 0;
        }
        return p.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JSONB) {
            JSONB j = (JSONB) obj;
            return Objects.equals(parsed(), j.parsed());
        }
        return false;
    }

    public String toString() {
        return JSONValue.toJSONString(parsed());
    }
}
