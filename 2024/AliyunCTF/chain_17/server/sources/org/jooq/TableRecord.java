package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.TableRecord;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/TableRecord.class */
public interface TableRecord<R extends TableRecord<R>> extends QualifiedRecord<R> {
    @NotNull
    Table<R> getTable();

    @Override // org.jooq.Record
    @NotNull
    R original();

    @Support
    int insert() throws DataAccessException;

    @Support
    int insert(Field<?>... fieldArr) throws DataAccessException;

    @Support
    int insert(Collection<? extends Field<?>> collection) throws DataAccessException;

    @Blocking
    @Support
    @Nullable
    <O extends UpdatableRecord<O>> O fetchParent(ForeignKey<R, O> foreignKey) throws DataAccessException;

    @Support
    @NotNull
    <O extends UpdatableRecord<O>> Table<O> parent(ForeignKey<R, O> foreignKey);
}
