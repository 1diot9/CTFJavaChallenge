package org.jooq;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;
import org.jooq.util.xml.jaxb.InformationSchema;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Meta.class */
public interface Meta extends Scope {
    @Support
    @NotNull
    List<Catalog> getCatalogs() throws DataAccessException;

    @Support
    @NotNull
    Catalog getCatalog(String str) throws DataAccessException;

    @Support
    @NotNull
    Catalog getCatalog(Name name) throws DataAccessException;

    @Support
    @NotNull
    List<Schema> getSchemas() throws DataAccessException;

    @Support
    @NotNull
    List<Schema> getSchemas(String str) throws DataAccessException;

    @Support
    @NotNull
    List<Schema> getSchemas(Name name) throws DataAccessException;

    @Support
    @NotNull
    List<Table<?>> getTables() throws DataAccessException;

    @Support
    @NotNull
    List<Table<?>> getTables(String str) throws DataAccessException;

    @Support
    @NotNull
    List<Table<?>> getTables(Name name) throws DataAccessException;

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    List<Domain<?>> getDomains() throws DataAccessException;

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    List<Domain<?>> getDomains(String str) throws DataAccessException;

    @Support({SQLDialect.H2, SQLDialect.POSTGRES})
    @NotNull
    List<Domain<?>> getDomains(Name name) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    List<Sequence<?>> getSequences() throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    List<Sequence<?>> getSequences(String str) throws DataAccessException;

    @Support({SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @NotNull
    List<Sequence<?>> getSequences(Name name) throws DataAccessException;

    @Support
    @NotNull
    List<UniqueKey<?>> getPrimaryKeys() throws DataAccessException;

    @Support
    @NotNull
    List<UniqueKey<?>> getPrimaryKeys(String str) throws DataAccessException;

    @Support
    @NotNull
    List<UniqueKey<?>> getPrimaryKeys(Name name) throws DataAccessException;

    @Support
    @NotNull
    List<UniqueKey<?>> getUniqueKeys() throws DataAccessException;

    @Support
    @NotNull
    List<UniqueKey<?>> getUniqueKeys(String str) throws DataAccessException;

    @Support
    @NotNull
    List<UniqueKey<?>> getUniqueKeys(Name name) throws DataAccessException;

    @Support
    @NotNull
    List<ForeignKey<?, ?>> getForeignKeys() throws DataAccessException;

    @Support
    @NotNull
    List<ForeignKey<?, ?>> getForeignKeys(String str) throws DataAccessException;

    @Support
    @NotNull
    List<ForeignKey<?, ?>> getForeignKeys(Name name) throws DataAccessException;

    @Support
    @NotNull
    List<Index> getIndexes() throws DataAccessException;

    @Support
    @NotNull
    List<Index> getIndexes(String str) throws DataAccessException;

    @Support
    @NotNull
    List<Index> getIndexes(Name name) throws DataAccessException;

    @NotNull
    Meta filterCatalogs(Predicate<? super Catalog> predicate);

    @NotNull
    Meta filterSchemas(Predicate<? super Schema> predicate);

    @NotNull
    Meta filterTables(Predicate<? super Table<?>> predicate);

    @NotNull
    Meta filterDomains(Predicate<? super Domain<?>> predicate);

    @NotNull
    Meta filterSequences(Predicate<? super Sequence<?>> predicate);

    @NotNull
    Meta filterPrimaryKeys(Predicate<? super UniqueKey<?>> predicate);

    @NotNull
    Meta filterUniqueKeys(Predicate<? super UniqueKey<?>> predicate);

    @NotNull
    Meta filterForeignKeys(Predicate<? super ForeignKey<?, ?>> predicate);

    @NotNull
    Meta filterIndexes(Predicate<? super Index> predicate);

    @NotNull
    Meta snapshot() throws DataAccessException;

    @NotNull
    Queries ddl() throws DataAccessException;

    @NotNull
    Queries ddl(DDLExportConfiguration dDLExportConfiguration) throws DataAccessException;

    @NotNull
    Meta apply(String str) throws DataAccessException;

    @NotNull
    Meta apply(Query... queryArr) throws DataAccessException;

    @NotNull
    Meta apply(Collection<? extends Query> collection) throws DataAccessException;

    @NotNull
    Meta apply(Queries queries) throws DataAccessException;

    @NotNull
    Queries migrateTo(Meta meta) throws DataAccessException;

    @NotNull
    Queries migrateTo(Meta meta, MigrationConfiguration migrationConfiguration) throws DataAccessException;

    @NotNull
    InformationSchema informationSchema() throws DataAccessException;
}
