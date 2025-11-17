package org.jooq.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.jooq.Catalog;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.Source;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.conf.ParseUnknownFunctions;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataDefinitionException;
import org.jooq.exception.DataTypeException;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MetaImpl.class */
public final class MetaImpl extends AbstractMeta {
    private final DatabaseMetaData databaseMetaData;
    private final boolean inverseSchemaCatalog;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) MetaImpl.class);
    private static final Set<SQLDialect> INVERSE_SCHEMA_CATALOG = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> CURRENT_TIMESTAMP_COLUMN_DEFAULT = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> EXPRESSION_COLUMN_DEFAULT = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_SCHEMAS = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.SQLITE);
    private static final Set<SQLDialect> NO_SUPPORT_INDEXES = SQLDialect.supportedBy(SQLDialect.TRINO);
    private static final Pattern P_SYSINDEX_DERBY = Pattern.compile("^(?i:SQL\\d{10,}).*$");
    private static final Pattern P_SYSINDEX_H2 = Pattern.compile("^(?i:PRIMARY_KEY_|UK_INDEX_|FK_INDEX_).*$");
    private static final Pattern P_SYSINDEX_HSQLDB = Pattern.compile("^(?i:SYS_IDX_).*$");
    private static final Pattern P_SYSINDEX_SQLITE = Pattern.compile("^(?i:sqlite_autoindex_).*$");
    private static final Class<?>[] GET_COLUMNS_SHORT = {String.class, String.class, String.class, String.class, Integer.TYPE, String.class, Integer.TYPE, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, String.class};
    private static final Class<?>[] GET_COLUMNS_EXTENDED = {String.class, String.class, String.class, String.class, Integer.TYPE, String.class, Integer.TYPE, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, String.class, String.class, String.class, String.class, String.class};

    /* JADX INFO: Access modifiers changed from: package-private */
    public MetaImpl(Configuration configuration, DatabaseMetaData databaseMetaData) {
        super(configuration);
        this.databaseMetaData = databaseMetaData;
        this.inverseSchemaCatalog = INVERSE_SCHEMA_CATALOG.contains(dialect());
    }

    final boolean hasCatalog(Catalog catalog) {
        return (catalog == null || StringUtils.isEmpty(catalog.getName())) ? false : true;
    }

    final <R> R catalogSchema(Catalog catalog, Schema schema, ThrowingBiFunction<String, String, R, SQLException> throwingBiFunction) throws SQLException {
        return (R) catalogSchema(catalog != null ? catalog.getName() : null, schema != null ? schema.getName() : null, throwingBiFunction);
    }

    final <R> R catalogSchema(String catalog, String schema, ThrowingBiFunction<String, String, R, SQLException> function) throws SQLException {
        String c = StringUtils.defaultIfEmpty(catalog, null);
        String s = StringUtils.defaultIfEmpty(schema, null);
        if (this.inverseSchemaCatalog) {
            return function.apply(s, c);
        }
        return function.apply(c, s);
    }

    private final Result<Record> meta(ThrowingFunction<DatabaseMetaData, Result<Record>, SQLException> function) {
        if (this.databaseMetaData == null) {
            return (Result) dsl().connectionResult(connection -> {
                return (Result) function.apply(connection.getMetaData());
            });
        }
        try {
            return function.apply(this.databaseMetaData);
        } catch (SQLException e) {
            throw new DataAccessException("Error while running MetaFunction", e);
        }
    }

    private static final <T, E extends Exception> T withCatalog(Catalog catalog, DSLContext ctx, ThrowingFunction<DSLContext, T, E> supplier) throws Exception {
        try {
            return supplier.apply(ctx);
        } catch (Exception x) {
            throw x;
        }
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Catalog> getCatalogs0() {
        List<Catalog> result = new ArrayList<>();
        if (result.isEmpty()) {
            result.add(new MetaCatalog(""));
        }
        return result;
    }

    final Table<?> lookupTable(Schema schema, String tableName) {
        switch (family()) {
            case SQLITE:
                return (Table) AbstractNamed.findIgnoreCase(tableName, schema.getTables());
            default:
                return schema.getTable(tableName);
        }
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Schema> getSchemas0() {
        return Tools.flatMap(getCatalogs(), c -> {
            return c.getSchemas();
        });
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<Table<?>> getTables0() {
        return Tools.flatMap(getSchemas(), s -> {
            return s.getTables();
        });
    }

    @Override // org.jooq.impl.AbstractMeta
    final List<UniqueKey<?>> getPrimaryKeys0() {
        List<UniqueKey<?>> result = new ArrayList<>();
        for (Table<?> table : getTables()) {
            UniqueKey<?> pk = table.getPrimaryKey();
            if (pk != null) {
                result.add(pk);
            }
        }
        return result;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MetaImpl$MetaCatalog.class */
    private final class MetaCatalog extends CatalogImpl {
        MetaCatalog(String name) {
            super(name);
        }

        @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
        public final List<Schema> getSchemas() {
            List<Schema> result = new ArrayList<>();
            if (!MetaImpl.this.inverseSchemaCatalog) {
                Result<Record> schemas = MetaImpl.this.meta(meta -> {
                    return MetaImpl.this.dsl().fetch(meta.getSchemas(), SQLDataType.VARCHAR);
                });
                for (U name : schemas.getValues(0, String.class)) {
                    result.add(new MetaSchema(name, this));
                }
            } else {
                Result<Record> schemas2 = MetaImpl.this.meta(meta2 -> {
                    return MetaImpl.this.dsl().fetch(meta2.getCatalogs(), SQLDataType.VARCHAR);
                });
                for (U name2 : schemas2.getValues(0, String.class)) {
                    result.add(new MetaSchema(name2, this));
                }
            }
            if (result.isEmpty()) {
                result.add(new MetaSchema("", this));
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MetaImpl$MetaSchema.class */
    public final class MetaSchema extends SchemaImpl {
        private volatile transient Map<Name, Result<Record>> columnCache;
        private volatile transient Map<Name, Result<Record>> ukCache;
        private volatile transient Map<Name, Result<Record>> sequenceCache;
        private volatile transient Map<Name, String> sourceCache;
        private volatile transient Map<Name, String> commentCache;

        MetaSchema(String name, Catalog catalog) {
            super(name, catalog);
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public final synchronized List<Table<?>> getTables() {
            Result<Record> tables = MetaImpl.this.meta(meta -> {
                String[] types;
                switch (MetaImpl.this.family()) {
                    case SQLITE:
                        types = new String[]{"TABLE", "VIEW"};
                        break;
                    case POSTGRES:
                    case YUGABYTEDB:
                        types = new String[]{"FOREIGN TABLE", "MATERIALIZED VIEW", "PARTITIONED TABLE", "SYSTEM_TABLE", "SYSTEM TABLE", "SYSTEM_VIEW", "SYSTEM VIEW", "TABLE", "TEMPORARY TABLE", "TEMPORARY VIEW", "VIEW"};
                        break;
                    default:
                        types = null;
                        break;
                }
                String[] strArr = types;
                ResultSet rs = (ResultSet) MetaImpl.this.catalogSchema(getCatalog(), this, (c, s) -> {
                    return meta.getTables(c, s, QuickTargetSourceCreator.PREFIX_THREAD_LOCAL, strArr);
                });
                try {
                    Result<Record> fetch = MetaImpl.this.dsl().fetch(rs, SQLDataType.VARCHAR, SQLDataType.VARCHAR, SQLDataType.VARCHAR, SQLDataType.VARCHAR, SQLDataType.VARCHAR);
                    if (rs != null) {
                        rs.close();
                    }
                    return fetch;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            });
            return Tools.map(tables, table -> {
                TableOptions.TableType tableType;
                String catalog = (String) table.get(0, String.class);
                String schema = (String) table.get(1, String.class);
                String name = (String) table.get(2, String.class);
                String type = (String) table.get(3, String.class);
                String remarks = (String) table.get(4, String.class);
                if ("VIEW".equals(type)) {
                    tableType = TableOptions.TableType.VIEW;
                } else if ("TEMPORARY VIEW".equals(type)) {
                    tableType = TableOptions.TableType.VIEW;
                } else if ("SYSTEM_VIEW".equals(type) || "SYSTEM VIEW".equals(type)) {
                    tableType = TableOptions.TableType.VIEW;
                } else if ("GLOBAL TEMPORARY".equals(type)) {
                    tableType = TableOptions.TableType.TEMPORARY;
                } else if ("LOCAL TEMPORARY".equals(type)) {
                    tableType = TableOptions.TableType.TEMPORARY;
                } else if ("TEMPORARY".equals(type)) {
                    tableType = TableOptions.TableType.TEMPORARY;
                } else if ("MATERIALIZED VIEW".equals(type)) {
                    tableType = TableOptions.TableType.MATERIALIZED_VIEW;
                } else {
                    tableType = TableOptions.TableType.TABLE;
                }
                TableOptions.TableType tableType2 = tableType;
                return new MetaTable(name, this, getColumns(catalog, schema, name), getUks(catalog, schema, name), remarks, tableType2);
            });
        }

        private final Result<Record> getUks(String catalog, String schema, String table) {
            if (this.ukCache == null) {
                if (MetaImpl.this.family() == SQLDialect.SQLITE) {
                    initUksSQLite(catalog, schema);
                } else {
                    initUks(catalog, schema);
                }
            }
            if (this.ukCache != null) {
                return this.ukCache.get(DSL.name(catalog, schema, table));
            }
            return null;
        }

        private final void initUks(String catalog, String schema) {
            init0(c -> {
                this.ukCache = c;
            }, () -> {
                return this.ukCache;
            }, catalog, schema, MetaSQL::M_UNIQUE_KEYS, r -> {
                return r.field(0);
            }, r2 -> {
                return r2.field(1);
            }, r3 -> {
                return r3.field(2);
            });
        }

        private final void init0(Consumer<? super Map<Name, Result<Record>>> cacheInit, Supplier<? extends Map<Name, Result<Record>>> cache, String catalog, String schema, java.util.function.Function<? super SQLDialect, ? extends String> sqlF, java.util.function.Function<? super Result<?>, ? extends Field<?>> objectCatalog, java.util.function.Function<? super Result<?>, ? extends Field<?>> objectSchema, java.util.function.Function<? super Result<?>, ? extends Field<?>> objectName) {
            String sql = sqlF.apply(MetaImpl.this.dialect());
            if (sql != null) {
                Result<Record> result = MetaImpl.this.meta(meta -> {
                    return (Result) MetaImpl.withCatalog(DSL.catalog(catalog), DSL.using(meta.getConnection(), MetaImpl.this.dialect()), ctx -> {
                        Object[] objArr;
                        if (MetaImpl.NO_SUPPORT_SCHEMAS.contains(MetaImpl.this.dialect())) {
                            objArr = Tools.EMPTY_OBJECT;
                        } else if (MetaImpl.this.inverseSchemaCatalog) {
                            objArr = new Object[]{catalog};
                        } else {
                            objArr = new Object[]{schema};
                        }
                        return ctx.resultQuery(sql, objArr).fetch();
                    });
                });
                Map<Record, Result<Record>> groups = result.intoGroups(new Field[]{objectCatalog.apply(result), objectSchema.apply(result), objectName.apply(result)});
                cacheInit.accept(new LinkedHashMap());
                groups.forEach((k, v) -> {
                    Map map = (Map) cache.get();
                    String[] strArr = new String[3];
                    strArr[0] = catalog == null ? null : (String) k.get(0, String.class);
                    strArr[1] = (String) k.get(1, String.class);
                    strArr[2] = (String) k.get(2, String.class);
                    map.put(DSL.name(strArr), v);
                });
            }
        }

        private final void initUksSQLite(String catalog, String schema) {
            this.ukCache = new LinkedHashMap();
            MetaImpl.this.dsl().resultQuery("select m.tbl_name, m.sql\nfrom sqlite_master as m\nwhere m.type = 'table'\nand exists (\n  select 1\n  from pragma_index_list(m.name) as il\n  where il.origin = 'u'\n)\norder by m.tbl_name\n").fetchMap(DSL.field("tbl_name", SQLDataType.VARCHAR), DSL.field("sql", SQLDataType.VARCHAR)).forEach((table, sql) -> {
                try {
                    Field<String> fCatalogName = DSL.field("catalog_name", SQLDataType.VARCHAR);
                    Field<String> fSchemaName = DSL.field("schema_name", SQLDataType.VARCHAR);
                    Field<String> fTableName = DSL.field("table_name", SQLDataType.VARCHAR);
                    Field<String> fConstraintName = DSL.field("constraint_name", SQLDataType.VARCHAR);
                    Field<String> fColumnName = DSL.field("column_name", SQLDataType.VARCHAR);
                    Field<Integer> fSequenceNo = DSL.field("sequence_no", SQLDataType.INTEGER);
                    Field<?>[] fields = {fCatalogName, fSchemaName, fTableName, fConstraintName, fColumnName, fSequenceNo};
                    for (Table<?> t : MetaImpl.this.dsl().meta(Source.of(sql)).getTables(table)) {
                        Result<Record> result = MetaImpl.this.dsl().newResult(fields);
                        int i = 0;
                        for (UniqueKey<?> uk : t.getUniqueKeys()) {
                            for (Field<?> ukField : uk.getFields()) {
                                int i2 = i;
                                i++;
                                result.add(MetaImpl.this.dsl().newRecord(fCatalogName, fSchemaName, fTableName, fConstraintName, fColumnName, fSequenceNo).values(catalog, schema, table, uk.getName(), ukField.getName(), Integer.valueOf(i2)));
                            }
                        }
                        this.ukCache.put(DSL.name(catalog, schema, table), result);
                    }
                } catch (DataDefinitionException | ParserException e) {
                    MetaImpl.log.info((Object) ("Cannot parse or interpret sql for table " + table + ": " + sql), (Throwable) e);
                }
            });
        }

        private final Result<Record> getColumns(String catalog, String schema, String table) {
            if (this.columnCache == null && MetaImpl.this.family() != SQLDialect.SQLITE) {
                Result<Record> columns = getColumns0(catalog, schema, QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
                Field<?> field = columns.field(0);
                Field<?> field2 = columns.field(1);
                Field<?> field3 = columns.field(2);
                Map<Record, Result<Record>> groups = columns.intoGroups(new Field[]{field, field2, field3});
                this.columnCache = new LinkedHashMap();
                groups.forEach((k, v) -> {
                    this.columnCache.put(DSL.name((String) k.get(field), (String) k.get(field2), (String) k.get(field3)), v);
                });
            }
            return this.columnCache != null ? this.columnCache.get(DSL.name(catalog, schema, table)) : getColumns0(catalog, schema, table);
        }

        private final Result<Record> getColumns0(String catalog, String schema, String table) {
            return MetaImpl.this.meta(meta -> {
                Result<Record> fetch;
                ResultSet rs = (ResultSet) MetaImpl.this.catalogSchema(catalog, schema, (c, s) -> {
                    return meta.getColumns(c, s, table, QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
                });
                try {
                    if (rs.getMetaData().getColumnCount() < MetaImpl.GET_COLUMNS_EXTENDED.length) {
                        fetch = MetaImpl.this.dsl().fetch(rs, MetaImpl.GET_COLUMNS_SHORT);
                    } else {
                        fetch = MetaImpl.this.dsl().fetch(rs, MetaImpl.GET_COLUMNS_EXTENDED);
                    }
                    Result<Record> result = fetch;
                    if (rs != null) {
                        rs.close();
                    }
                    return result;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            });
        }

        @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
        public List<Sequence<?>> getSequences() {
            Result<Record> result = getSequences0();
            if (result != null) {
                return Tools.map(result, r -> {
                    return Internal.createSequence((String) r.get(2, String.class), this, DefaultDataType.getDataType(MetaImpl.this.family(), (String) r.get(3, String.class), ((Integer) r.get(4, Integer.TYPE)).intValue(), ((Integer) r.get(5, Integer.TYPE)).intValue(), !Boolean.FALSE.equals(MetaImpl.this.settings().isForceIntegerTypesOnZeroScaleDecimals())), (Number) r.get(6, Long.class), (Number) r.get(7, Long.class), (Number) r.get(8, Long.class), (Number) r.get(9, Long.class), ((Boolean) r.get(10, Boolean.TYPE)).booleanValue(), (Number) r.get(11, Long.class));
                });
            }
            return new ArrayList();
        }

        private final Result<Record> getSequences0() {
            String M_SEQUENCES;
            if (this.sequenceCache == null) {
                if (Boolean.TRUE.equals(MetaImpl.this.settings().isMetaIncludeSystemSequences())) {
                    M_SEQUENCES = MetaSQL.M_SEQUENCES_INCLUDING_SYSTEM_SEQUENCES(MetaImpl.this.dialect());
                } else {
                    M_SEQUENCES = MetaSQL.M_SEQUENCES(MetaImpl.this.dialect());
                }
                String sql = M_SEQUENCES;
                if (sql != null) {
                    Result<Record> result = MetaImpl.this.meta(meta -> {
                        return DSL.using(meta.getConnection(), MetaImpl.this.dialect()).resultQuery(sql, getName()).fetch();
                    });
                    Map<Record, Result<Record>> groups = result.intoGroups(new Field[]{result.field(0), result.field(1)});
                    this.sequenceCache = new LinkedHashMap();
                    groups.forEach((k, v) -> {
                        this.sequenceCache.put(DSL.name((String) k.get(0, String.class), (String) k.get(1, String.class)), v);
                    });
                }
            }
            if (this.sequenceCache != null) {
                return this.sequenceCache.get(DSL.name(getCatalog().getName(), getName()));
            }
            return null;
        }

        final String source(TableOptions.TableType type, String tableName) {
            String sql;
            if (this.sourceCache == null && (sql = MetaSQL.M_SOURCES(MetaImpl.this.dialect())) != null) {
                Result<Record> result = MetaImpl.this.meta(meta -> {
                    return (Result) MetaImpl.withCatalog(getCatalog(), DSL.using(meta.getConnection(), MetaImpl.this.dialect()), ctx -> {
                        return ctx.resultQuery(patchSchema(sql), getName()).fetch();
                    });
                });
                Map<Record, Result<Record>> groups = result.intoGroups(new Field[]{result.field(0), result.field(1), result.field(2)});
                this.sourceCache = new LinkedHashMap();
                groups.forEach((k, v) -> {
                    this.sourceCache.put(DSL.name((String) k.get(1, String.class), (String) k.get(2, String.class)), (String) Tools.apply((String) ((Record) v.get(0)).get(3, String.class), s -> {
                        if (s.toLowerCase().startsWith("create")) {
                            return s;
                        }
                        return (type == TableOptions.TableType.VIEW ? "create view " : "create materialized view ") + MetaImpl.this.dsl().render(DSL.name((String) k.get(2, String.class))) + " as " + s;
                    }));
                });
            }
            if (this.sourceCache != null) {
                return this.sourceCache.get(DSL.name(getName(), tableName));
            }
            return null;
        }

        final String comment(String tableName) {
            return comment(tableName, null);
        }

        final String comment(String tableName, String columnName) {
            String sql;
            if (this.commentCache == null && (sql = MetaSQL.M_COMMENTS(MetaImpl.this.dialect())) != null) {
                Result<Record> result = MetaImpl.this.meta(meta -> {
                    return (Result) MetaImpl.withCatalog(getCatalog(), DSL.using(meta.getConnection(), MetaImpl.this.dialect()), ctx -> {
                        return ctx.resultQuery(sql, getName()).fetch();
                    });
                });
                Map<Record, Result<Record>> groups = result.intoGroups(new Field[]{result.field(0), result.field(1), result.field(2), result.field(3)});
                this.commentCache = new LinkedHashMap();
                groups.forEach((k, v) -> {
                    this.commentCache.put(DSL.name((String) k.get(1, String.class), (String) k.get(2, String.class), (String) k.get(3, String.class)), (String) ((Record) v.get(0)).get(4, String.class));
                });
            }
            if (this.commentCache != null) {
                return this.commentCache.get(DSL.name(getName(), tableName, columnName));
            }
            return null;
        }

        private final String patchSchema(String sql) {
            return sql;
        }
    }

    private static final TableOptions tableOption(DSLContext ctx, MetaSchema schema, String tableName, TableOptions.TableType tableType) {
        String sql = MetaSQL.M_SOURCES(ctx.dialect());
        if (sql != null) {
            if (tableType == TableOptions.TableType.MATERIALIZED_VIEW) {
                return TableOptions.materializedView(schema.source(tableType, tableName));
            }
            if (tableType == TableOptions.TableType.VIEW) {
                return TableOptions.view(schema.source(tableType, tableName));
            }
        }
        return TableOptions.of(tableType);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MetaImpl$MetaTable.class */
    private final class MetaTable extends TableImpl<Record> {
        private final MetaSchema schema;
        private final Result<Record> uks;

        MetaTable(String name, MetaSchema schema, Result<Record> columns, Result<Record> uks, String remarks, TableOptions.TableType tableType) {
            super(DSL.name(name), schema, null, (ForeignKey) null, (InverseForeignKey) null, null, null, DSL.comment(remarks != null ? remarks : schema.comment(name, null)), MetaImpl.tableOption(MetaImpl.this.dsl(), schema, name, tableType), null);
            this.schema = schema;
            this.uks = uks;
            if (columns != null) {
                initColumns(columns);
            }
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<Index> getIndexes() {
            if (MetaImpl.NO_SUPPORT_INDEXES.contains(MetaImpl.this.dsl().dialect())) {
                return Collections.emptyList();
            }
            return (List) Tools.ignoreNPE(() -> {
                Result<Record> result = removeSystemIndexes(MetaImpl.this.meta(meta -> {
                    ResultSet rs = (ResultSet) MetaImpl.this.catalogSchema(getCatalog(), getSchema(), (c, s) -> {
                        return meta.getIndexInfo(c, s, getName(), false, true);
                    });
                    try {
                        Result<Record> fetch = MetaImpl.this.dsl().fetch(rs, String.class, String.class, String.class, Boolean.TYPE, String.class, String.class, Integer.TYPE, Integer.TYPE, String.class, String.class, Long.TYPE, Long.TYPE, String.class);
                        if (rs != null) {
                            rs.close();
                        }
                        return fetch;
                    } catch (Throwable th) {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                }));
                result.sortAsc(7).sortAsc(5);
                return createIndexes(result);
            }, () -> {
                return Collections.emptyList();
            });
        }

        private final Result<Record> removeSystemIndexes(Result<Record> result) {
            if (Boolean.TRUE.equals(MetaImpl.this.settings().isMetaIncludeSystemIndexes())) {
                return result;
            }
            Set<String> constraints = new HashSet<>();
            for (UniqueKey<?> key : getKeys()) {
                constraints.add(key.getName());
            }
            for (ForeignKey<?, ?> key2 : getReferences()) {
                constraints.add(key2.getName());
            }
            Iterator<R> it = result.iterator();
            while (it.hasNext()) {
                String indexName = (String) ((Record) it.next()).get(5, String.class);
                if (constraints.contains(indexName)) {
                    it.remove();
                } else {
                    switch (MetaImpl.this.family()) {
                        case SQLITE:
                            if (MetaImpl.P_SYSINDEX_SQLITE.matcher(indexName).matches()) {
                                it.remove();
                                break;
                            } else {
                                break;
                            }
                        case DERBY:
                            if (MetaImpl.P_SYSINDEX_DERBY.matcher(indexName).matches()) {
                                it.remove();
                                break;
                            } else {
                                break;
                            }
                        case H2:
                            if (MetaImpl.P_SYSINDEX_H2.matcher(indexName).matches()) {
                                it.remove();
                                break;
                            } else {
                                break;
                            }
                        case HSQLDB:
                            if (MetaImpl.P_SYSINDEX_HSQLDB.matcher(indexName).matches()) {
                                it.remove();
                                break;
                            } else {
                                break;
                            }
                    }
                }
            }
            return result;
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<UniqueKey<Record>> getUniqueKeys() {
            List<UniqueKey<Record>> result = new ArrayList<>();
            if (this.uks != null) {
                this.uks.intoGroups(this.uks.field(3)).forEach((k, v) -> {
                    v.sortAsc(5);
                    result.add(createUniqueKey(v, 4, 3, false));
                });
            }
            return result;
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final UniqueKey<Record> getPrimaryKey() {
            Result<Record> result = MetaImpl.this.meta(meta -> {
                ResultSet rs = (ResultSet) MetaImpl.this.catalogSchema(getCatalog(), getSchema(), (c, s) -> {
                    return meta.getPrimaryKeys(c, s, getName());
                });
                try {
                    Result<Record> fetch = MetaImpl.this.dsl().fetch(rs, String.class, String.class, String.class, String.class, Integer.TYPE, String.class);
                    if (rs != null) {
                        rs.close();
                    }
                    return fetch;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            });
            result.sortAsc(4);
            return createUniqueKey(result, 3, 5, true);
        }

        @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
        public final List<ForeignKey<Record, ?>> getReferences() {
            Result<Record> result = MetaImpl.this.meta(meta -> {
                ResultSet rs = (ResultSet) MetaImpl.this.catalogSchema(getCatalog(), getSchema(), (c, s) -> {
                    return meta.getImportedKeys(c, s, getName());
                });
                try {
                    Result<Record> fetch = MetaImpl.this.dsl().fetch(rs, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Short.class, Short.class, Short.class, String.class, String.class);
                    if (rs != null) {
                        rs.close();
                    }
                    return fetch;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            });
            Field<?>[] fieldArr = new Field[5];
            fieldArr[0] = result.field(MetaImpl.this.inverseSchemaCatalog ? 1 : 0);
            fieldArr[1] = result.field(MetaImpl.this.inverseSchemaCatalog ? 0 : 1);
            fieldArr[2] = result.field(2);
            fieldArr[3] = result.field(11);
            fieldArr[4] = result.field(12);
            Map<Record, Result<Record>> groups = result.intoGroups(fieldArr);
            Map<Name, Schema> schemas = new HashMap<>();
            for (Schema schema : MetaImpl.this.getSchemas()) {
                schemas.put(schema.getQualifiedName(), schema);
            }
            List<ForeignKey<Record, ?>> references = new ArrayList<>(groups.size());
            groups.forEach((k, v) -> {
                Name name;
                if (MetaImpl.this.hasCatalog(getCatalog())) {
                    name = DSL.name(StringUtils.defaultString((String) k.get(0, String.class)), StringUtils.defaultString((String) k.get(1, String.class)));
                } else {
                    name = DSL.name(StringUtils.defaultString((String) k.get(1, String.class)));
                }
                Schema schema2 = (Schema) schemas.get(name);
                String fkName = (String) k.get(3, String.class);
                String pkName = (String) k.get(4, String.class);
                Table<?> lookupTable = MetaImpl.this.lookupTable(schema2, (String) k.get(2, String.class));
                TableField<Record, ?>[] pkFields = new TableField[v.size()];
                TableField<Record, ?>[] fkFields = new TableField[v.size()];
                for (int i = 0; i < v.size(); i++) {
                    Record record = (Record) v.get(i);
                    String pkFieldName = (String) record.get(3, String.class);
                    String fkFieldName = (String) record.get(7, String.class);
                    pkFields[i] = (TableField) lookupTable.field(pkFieldName);
                    fkFields[i] = (TableField) field(fkFieldName);
                    if (pkFields[i] == null) {
                        TableField<Record, ?> lookup = lookup(lookupTable, pkFieldName);
                        pkFields[i] = lookup;
                        if (lookup == null) {
                            return;
                        }
                    }
                    if (fkFields[i] == null) {
                        TableField<Record, ?> lookup2 = lookup(this, fkFieldName);
                        fkFields[i] = lookup2;
                        if (lookup2 == null) {
                            return;
                        }
                    }
                }
                references.add(new ReferenceImpl(this, DSL.name(fkName), fkFields, new MetaUniqueKey(lookupTable, pkName, pkFields, true), pkFields, true));
            });
            return references;
        }

        private final TableField<Record, ?> lookup(Table<?> table, String fieldName) {
            for (Field<?> field : table.fields()) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    return (TableField) field;
                }
            }
            MetaImpl.log.info("Could not look up key field : " + fieldName + " in table : " + String.valueOf(table));
            return null;
        }

        private final UniqueKey<Record> createUniqueKey(Result<Record> result, int columnName, int keyName, boolean isPrimary) {
            if (result.size() > 0) {
                TableField<Record, ?>[] f = new TableField[result.size()];
                for (int i = 0; i < f.length; i++) {
                    String name = (String) ((Record) result.get(i)).get(columnName, String.class);
                    f[i] = (TableField) field(name);
                    if (f[i] == null && MetaImpl.this.family() == SQLDialect.SQLITE) {
                        for (Field<?> field : fields()) {
                            if (field.getName().equalsIgnoreCase(name)) {
                                f[i] = (TableField) field;
                            }
                        }
                    }
                }
                String indexName = (String) ((Record) result.get(0)).get(keyName, String.class);
                return new MetaUniqueKey(this, indexName, f, isPrimary);
            }
            return null;
        }

        private final List<Index> createIndexes(Result<Record> result) {
            List<Index> indexes = new ArrayList<>();
            List<SortField<?>> sortFields = new ArrayList<>();
            String previousIndexName = null;
            Name name = null;
            Condition where = null;
            boolean unique = false;
            for (int i = 0; i < result.size(); i++) {
                Record record = (Record) result.get(i);
                String indexName = (String) record.get(5, String.class);
                if (indexName != null) {
                    if (!indexName.equals(previousIndexName)) {
                        previousIndexName = indexName;
                        sortFields.clear();
                        name = DSL.name((String) record.get(0, String.class), (String) record.get(1, String.class), indexName);
                        String filter = (String) record.get(12, String.class);
                        where = !StringUtils.isBlank(filter) ? DSL.condition(filter) : null;
                        unique = !((Boolean) record.get(3, Boolean.TYPE)).booleanValue();
                    }
                    String columnName = (String) record.get(8, String.class);
                    Field<?> field = field(columnName);
                    if (field == null) {
                        field = DSL.field(columnName);
                    }
                    boolean desc = "D".equalsIgnoreCase((String) record.get(9, String.class));
                    sortFields.add(desc ? field.desc() : field.asc());
                    if (i + 1 == result.size() || !((String) ((Record) result.get(i + 1)).get(5, String.class)).equals(previousIndexName)) {
                        indexes.add(new IndexImpl(name, this, (OrderField[]) sortFields.toArray(Tools.EMPTY_SORTFIELD), where, unique));
                    }
                }
            }
            return indexes;
        }

        private final void initColumns(Result<Record> columns) {
            boolean z;
            DataType type;
            boolean hasAutoIncrement = false;
            for (Record column : columns) {
                String columnName = (String) column.get(3, String.class);
                String typeName = (String) column.get(5, String.class);
                int precision = ((Integer) column.get(6, Integer.TYPE)).intValue();
                int scale = ((Integer) column.get(8, Integer.TYPE)).intValue();
                int nullable = ((Integer) column.get(10, Integer.TYPE)).intValue();
                String remarks = (String) column.get(11, String.class);
                String defaultValue = (String) column.get(12, String.class);
                if ("null".equalsIgnoreCase(defaultValue)) {
                    defaultValue = null;
                }
                if (column.size() >= 23) {
                    z = ((Boolean) column.get(22, Boolean.TYPE)).booleanValue();
                } else {
                    z = false;
                }
                boolean isAutoIncrement = z;
                try {
                    type = DefaultDataType.getDataType(MetaImpl.this.family(), typeName, precision, scale, !Boolean.FALSE.equals(MetaImpl.this.settings().isForceIntegerTypesOnZeroScaleDecimals()));
                } catch (SQLDialectNotSupportedException e) {
                    if (MetaImpl.log.isDebugEnabled()) {
                        MetaImpl.log.debug("Unknown type", "Registering unknown data type: " + typeName);
                    }
                    type = new DefaultDataType(MetaImpl.this.family(), Object.class, typeName);
                }
                if (isAutoIncrement) {
                    if (!hasAutoIncrement) {
                        hasAutoIncrement = isAutoIncrement;
                        type = type.identity(isAutoIncrement);
                    } else {
                        MetaImpl.log.info("Multiple identities", "jOOQ does not support tables with multiple identities. Identity is ignored on column: " + columnName);
                    }
                }
                if (nullable == 0) {
                    type = type.nullable(false);
                }
                if (!isAutoIncrement && !StringUtils.isEmpty(defaultValue)) {
                    try {
                        if (MetaImpl.EXPRESSION_COLUMN_DEFAULT.contains(MetaImpl.this.dialect())) {
                            if (Boolean.FALSE.equals(MetaImpl.this.settings().isParseMetaDefaultExpressions())) {
                                type = type.defaultValue(DSL.field(defaultValue, type));
                            } else {
                                try {
                                    type = type.defaultValue((Field) MetaImpl.this.dsl().configuration().deriveSettings(s -> {
                                        return s.withParseUnknownFunctions(ParseUnknownFunctions.IGNORE);
                                    }).dsl().parser().parseField(defaultValue));
                                } catch (ParserException e2) {
                                    MetaImpl.log.info((Object) ("Cannot parse default expression (to skip parsing, use Settings.parseMetaViewDefaultExpressions): " + defaultValue), (Throwable) e2);
                                    type = type.defaultValue(DSL.field(defaultValue, type));
                                }
                            }
                        } else if (MetaImpl.CURRENT_TIMESTAMP_COLUMN_DEFAULT.contains(MetaImpl.this.dialect()) && "CURRENT_TIMESTAMP".equalsIgnoreCase(defaultValue)) {
                            type = type.defaultValue(DSL.field(defaultValue, type));
                        } else {
                            type = type.defaultValue((Field) DSL.inline(defaultValue, type));
                        }
                    } catch (DataTypeException e3) {
                        MetaImpl.log.warn("Default value", "Could not load default value: " + defaultValue + " for type: " + String.valueOf(type), e3);
                    }
                }
                createField(DSL.name(columnName), type, this, remarks != null ? remarks : this.schema.comment(getName(), columnName));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MetaImpl$MetaUniqueKey.class */
    public final class MetaUniqueKey extends AbstractKey<Record> implements UniqueKey<Record> {
        private final boolean isPrimary;

        MetaUniqueKey(Table<Record> table, String name, TableField<Record, ?>[] fields, boolean isPrimary) {
            super(table, name == null ? null : DSL.name(name), fields, true);
            this.isPrimary = isPrimary;
        }

        @Override // org.jooq.UniqueKey
        public final boolean isPrimary() {
            return this.isPrimary;
        }

        @Override // org.jooq.UniqueKey
        public final List<ForeignKey<?, Record>> getReferences() {
            Result<Record> result = MetaImpl.this.meta(meta -> {
                ResultSet rs = (ResultSet) MetaImpl.this.catalogSchema(getTable().getCatalog(), getTable().getSchema(), (c, s) -> {
                    return meta.getExportedKeys(c, s, getTable().getName());
                });
                try {
                    Result<Record> fetch = MetaImpl.this.dsl().fetch(rs, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Short.class, Short.class, Short.class, String.class, String.class);
                    if (rs != null) {
                        rs.close();
                    }
                    return fetch;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            });
            Field<?>[] fieldArr = new Field[5];
            fieldArr[0] = result.field(MetaImpl.this.inverseSchemaCatalog ? 5 : 4);
            fieldArr[1] = result.field(MetaImpl.this.inverseSchemaCatalog ? 4 : 5);
            fieldArr[2] = result.field(6);
            fieldArr[3] = result.field(11);
            fieldArr[4] = result.field(12);
            Map<Record, Result<Record>> groups = result.intoGroups(fieldArr);
            Map<String, Schema> schemas = new HashMap<>();
            for (Schema schema : MetaImpl.this.getSchemas()) {
                schemas.put(schema.getName(), schema);
            }
            List<ForeignKey<?, Record>> references = new ArrayList<>(groups.size());
            groups.forEach((k, v) -> {
                Schema schema2 = (Schema) schemas.get(StringUtils.defaultString((String) k.get(1, String.class)));
                Table<?> lookupTable = MetaImpl.this.lookupTable(schema2, (String) k.get(2, String.class));
                references.add(new ReferenceImpl(lookupTable, DSL.name((String) k.get(3, String.class)), (TableField[]) Tools.map(v, f -> {
                    return (TableField) lookupTable.field((String) f.get(7, String.class));
                }, x$0 -> {
                    return new TableField[x$0];
                }), this, (TableField[]) Tools.map(v, f2 -> {
                    return (TableField) getTable().field((String) f2.get(3, String.class));
                }, x$02 -> {
                    return new TableField[x$02];
                }), true));
            });
            return references;
        }

        @Override // org.jooq.impl.AbstractKey
        final ConstraintEnforcementStep constraint0() {
            if (isPrimary()) {
                return DSL.constraint(getName()).primaryKey(getFieldsArray());
            }
            return DSL.constraint(getName()).unique(getFieldsArray());
        }
    }

    @Override // org.jooq.impl.AbstractMeta
    public String toString() {
        return "MetaImpl";
    }
}
