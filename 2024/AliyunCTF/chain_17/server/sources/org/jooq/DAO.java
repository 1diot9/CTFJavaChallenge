package org.jooq;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.TableRecord;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;

@Blocking
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DAO.class */
public interface DAO<R extends TableRecord<R>, P, T> {
    @NotNull
    Configuration configuration();

    @NotNull
    Settings settings();

    @NotNull
    SQLDialect dialect();

    @NotNull
    SQLDialect family();

    @NotNull
    RecordMapper<R, P> mapper();

    @Support
    void insert(P p) throws DataAccessException;

    @Support
    void insert(P... pArr) throws DataAccessException;

    @Support
    void insert(Collection<P> collection) throws DataAccessException;

    @Support
    void update(P p) throws DataAccessException;

    @Support
    void update(P... pArr) throws DataAccessException;

    @Support
    void update(Collection<P> collection) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    void merge(P p) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    void merge(P... pArr) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE})
    void merge(Collection<P> collection) throws DataAccessException;

    @Support
    void delete(P p) throws DataAccessException;

    @Support
    void delete(P... pArr) throws DataAccessException;

    @Support
    void delete(Collection<P> collection) throws DataAccessException;

    @Support
    void deleteById(T t) throws DataAccessException;

    @Support
    void deleteById(T... tArr) throws DataAccessException;

    @Support
    void deleteById(Collection<T> collection) throws DataAccessException;

    @Support
    boolean exists(P p) throws DataAccessException;

    @Support
    boolean existsById(T t) throws DataAccessException;

    @Support
    long count() throws DataAccessException;

    @Support
    @NotNull
    List<P> findAll() throws DataAccessException;

    @Support
    @Nullable
    P findById(T t) throws DataAccessException;

    @Support
    @NotNull
    Optional<P> findOptionalById(T t) throws DataAccessException;

    @Support
    @NotNull
    <Z> List<P> fetch(Field<Z> field, Z... zArr) throws DataAccessException;

    @Support
    @NotNull
    <Z> List<P> fetch(Field<Z> field, Collection<? extends Z> collection) throws DataAccessException;

    @Support
    @NotNull
    <Z> List<P> fetchRange(Field<Z> field, Z z, Z z2) throws DataAccessException;

    @Support
    @Nullable
    <Z> P fetchOne(Field<Z> field, Z z) throws DataAccessException;

    @Support
    @NotNull
    <Z> Optional<P> fetchOptional(Field<Z> field, Z z) throws DataAccessException;

    @NotNull
    Table<R> getTable();

    @NotNull
    Class<P> getType();

    T getId(P p);
}
