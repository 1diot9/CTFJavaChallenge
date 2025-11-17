package org.jooq;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Index.class */
public interface Index extends TableElement {
    @Nullable
    Table<?> getTable();

    @NotNull
    List<SortField<?>> getFields();

    @Nullable
    Condition getWhere();

    boolean getUnique();

    @ApiStatus.Experimental
    @Nullable
    Table<?> $table();
}
