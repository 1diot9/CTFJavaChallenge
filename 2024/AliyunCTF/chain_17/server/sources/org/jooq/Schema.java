package org.jooq;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Schema.class */
public interface Schema extends Named {
    @Nullable
    Catalog getCatalog();

    @NotNull
    Stream<Table<?>> tableStream();

    @NotNull
    List<Table<?>> getTables();

    @Nullable
    Table<?> getTable(String str);

    @Nullable
    Table<?> getTable(Name name);

    @NotNull
    Stream<UniqueKey<?>> primaryKeyStream();

    @NotNull
    List<UniqueKey<?>> getPrimaryKeys();

    @NotNull
    List<UniqueKey<?>> getPrimaryKeys(String str);

    @NotNull
    List<UniqueKey<?>> getPrimaryKeys(Name name);

    @NotNull
    Stream<UniqueKey<?>> uniqueKeyStream();

    @NotNull
    List<UniqueKey<?>> getUniqueKeys();

    @NotNull
    List<UniqueKey<?>> getUniqueKeys(String str);

    @NotNull
    List<UniqueKey<?>> getUniqueKeys(Name name);

    @NotNull
    Stream<ForeignKey<?, ?>> foreignKeyStream();

    @NotNull
    List<ForeignKey<?, ?>> getForeignKeys();

    @NotNull
    List<ForeignKey<?, ?>> getForeignKeys(String str);

    @NotNull
    List<ForeignKey<?, ?>> getForeignKeys(Name name);

    @NotNull
    Stream<Index> indexStream();

    @NotNull
    List<Index> getIndexes();

    @NotNull
    List<Index> getIndexes(String str);

    @NotNull
    List<Index> getIndexes(Name name);

    @NotNull
    Stream<UDT<?>> udtStream();

    @NotNull
    List<UDT<?>> getUDTs();

    @Nullable
    UDT<?> getUDT(String str);

    @Nullable
    UDT<?> getUDT(Name name);

    @NotNull
    Stream<Domain<?>> domainStream();

    @NotNull
    List<Domain<?>> getDomains();

    @Nullable
    Domain<?> getDomain(String str);

    @Nullable
    Domain<?> getDomain(Name name);

    @NotNull
    Stream<Sequence<?>> sequenceStream();

    @NotNull
    List<Sequence<?>> getSequences();

    @Nullable
    Sequence<?> getSequence(String str);

    @Nullable
    Sequence<?> getSequence(Name name);
}
