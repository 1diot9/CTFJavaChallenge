package org.jooq;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ForeignKey.class */
public interface ForeignKey<CHILD extends Record, PARENT extends Record> extends Key<CHILD> {
    @NotNull
    InverseForeignKey<PARENT, CHILD> getInverseKey();

    @NotNull
    UniqueKey<PARENT> getKey();

    @NotNull
    List<TableField<PARENT, ?>> getKeyFields();

    @NotNull
    TableField<PARENT, ?>[] getKeyFieldsArray();

    @Blocking
    @Nullable
    PARENT fetchParent(CHILD child) throws DataAccessException;

    @Blocking
    @NotNull
    Result<PARENT> fetchParents(CHILD... childArr) throws DataAccessException;

    @Blocking
    @NotNull
    Result<PARENT> fetchParents(Collection<? extends CHILD> collection) throws DataAccessException;

    @Blocking
    @NotNull
    Result<CHILD> fetchChildren(PARENT parent) throws DataAccessException;

    @Blocking
    @NotNull
    Result<CHILD> fetchChildren(PARENT... parentArr) throws DataAccessException;

    @Blocking
    @NotNull
    Result<CHILD> fetchChildren(Collection<? extends PARENT> collection) throws DataAccessException;

    @NotNull
    Table<PARENT> parent(CHILD child);

    @NotNull
    Table<PARENT> parents(CHILD... childArr);

    @NotNull
    Table<PARENT> parents(Collection<? extends CHILD> collection);

    @NotNull
    Table<CHILD> children(PARENT parent);

    @NotNull
    Table<CHILD> children(PARENT... parentArr);

    @NotNull
    Table<CHILD> children(Collection<? extends PARENT> collection);
}
