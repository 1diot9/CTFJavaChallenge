package org.jooq.util.postgres;

import java.io.Serializable;
import java.sql.SQLException;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/postgres/PGobject.class */
public class PGobject implements Serializable, Cloneable {
    protected String type;
    protected String value;

    public final void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) throws SQLException {
        this.value = value;
    }

    public final String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isNull() {
        return getValue() != null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof PGobject) {
            Object otherValue = ((PGobject) obj).getValue();
            if (otherValue == null) {
                return getValue() == null;
            }
            return otherValue.equals(getValue());
        }
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return getValue();
    }

    public int hashCode() {
        String value = getValue();
        if (value != null) {
            return value.hashCode();
        }
        return 0;
    }

    protected static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
}
