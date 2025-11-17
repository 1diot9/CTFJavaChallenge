package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataChangedException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.exception.TooManyRowsException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/UpdatableRecord.class */
public interface UpdatableRecord<R extends UpdatableRecord<R>> extends TableRecord<R> {
    @NotNull
    Record key();

    @Support
    int store() throws DataAccessException, DataChangedException;

    @Support
    int store(Field<?>... fieldArr) throws DataAccessException, DataChangedException;

    @Support
    int store(Collection<? extends Field<?>> collection) throws DataAccessException, DataChangedException;

    @Override // org.jooq.TableRecord
    @Support
    int insert() throws DataAccessException;

    @Override // org.jooq.TableRecord
    @Support
    int insert(Field<?>... fieldArr) throws DataAccessException;

    @Override // org.jooq.TableRecord
    @Support
    int insert(Collection<? extends Field<?>> collection) throws DataAccessException;

    @Support
    int update() throws DataAccessException, DataChangedException;

    @Support
    int update(Field<?>... fieldArr) throws DataAccessException, DataChangedException;

    @Support
    int update(Collection<? extends Field<?>> collection) throws DataAccessException, DataChangedException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    int merge() throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    int merge(Field<?>... fieldArr) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB})
    int merge(Collection<? extends Field<?>> collection) throws DataAccessException;

    @Support
    int delete() throws DataAccessException, DataChangedException;

    @Support
    void refresh() throws DataAccessException;

    @Support
    void refresh(Field<?>... fieldArr) throws DataAccessException, NoDataFoundException;

    @Support
    void refresh(Collection<? extends Field<?>> collection) throws DataAccessException, NoDataFoundException;

    @NotNull
    R copy();

    @Blocking
    @Support
    @Nullable
    <O extends TableRecord<O>> O fetchChild(ForeignKey<O, R> foreignKey) throws TooManyRowsException, DataAccessException;

    @Blocking
    @Support
    @NotNull
    <O extends TableRecord<O>> Result<O> fetchChildren(ForeignKey<O, R> foreignKey) throws DataAccessException;

    @Support
    @NotNull
    <O extends TableRecord<O>> Table<O> children(ForeignKey<O, R> foreignKey);
}
