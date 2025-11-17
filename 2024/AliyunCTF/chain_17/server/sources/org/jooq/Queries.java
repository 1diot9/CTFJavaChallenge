package org.jooq;

import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Queries.class */
public interface Queries extends AttachableQueryPart, Iterable<Query> {
    @NotNull
    Queries concat(Queries queries);

    @NotNull
    Query[] queries();

    @NotNull
    Block block();

    Batch batch();

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    Stream<Query> stream();

    @NotNull
    Stream<Query> queryStream();

    @Blocking
    @NotNull
    Results fetchMany();

    @Blocking
    int[] executeBatch();

    @ApiStatus.Experimental
    @NotNull
    QOM.UnmodifiableList<? extends Query> $queries();
}
