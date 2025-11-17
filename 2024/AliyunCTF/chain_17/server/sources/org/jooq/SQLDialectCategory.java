package org.jooq;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/SQLDialectCategory.class */
public enum SQLDialectCategory {
    OTHER,
    MYSQL,
    POSTGRES;

    private EnumSet<SQLDialect> dialects;
    private EnumSet<SQLDialect> families;

    @NotNull
    public final Set<SQLDialect> dialects() {
        if (this.dialects == null) {
            this.dialects = filter(SQLDialect.values());
        }
        return Collections.unmodifiableSet(this.dialects);
    }

    @NotNull
    public final Set<SQLDialect> families() {
        if (this.families == null) {
            this.families = filter(SQLDialect.values());
        }
        return Collections.unmodifiableSet(this.families);
    }

    private final EnumSet<SQLDialect> filter(SQLDialect[] values) {
        EnumSet<SQLDialect> set = EnumSet.noneOf(SQLDialect.class);
        for (SQLDialect family : values) {
            if (family.category() == this) {
                set.add(family);
            }
        }
        return set;
    }
}
