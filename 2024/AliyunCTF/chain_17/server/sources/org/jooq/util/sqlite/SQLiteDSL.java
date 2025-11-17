package org.jooq.util.sqlite;

import org.jooq.Constants;
import org.jooq.Field;
import org.jooq.impl.DSL;

@Deprecated(forRemoval = true, since = Constants.VERSION_3_15)
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/util/sqlite/SQLiteDSL.class */
public class SQLiteDSL extends DSL {
    protected SQLiteDSL() {
    }

    public static Field<Long> rowid() {
        return field("_rowid_", Long.class);
    }
}
