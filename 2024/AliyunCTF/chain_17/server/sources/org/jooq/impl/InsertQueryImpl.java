package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.GeneratorStatementType;
import org.jooq.InsertQuery;
import org.jooq.MergeNotMatchedStep;
import org.jooq.MergeNotMatchedWhereStep;
import org.jooq.MergeOnConditionStep;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.conf.ParamType;
import org.jooq.impl.FieldMapForUpdate;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InsertQueryImpl.class */
public final class InsertQueryImpl<R extends Record> extends AbstractStoreQuery<R, Field<?>, Field<?>> implements InsertQuery<R>, QOM.Insert<R> {
    static final Clause[] CLAUSES = {Clause.INSERT};
    static final Set<SQLDialect> SUPPORT_INSERT_IGNORE = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    static final Set<SQLDialect> SUPPORTS_OPTIONAL_DO_UPDATE_CONFLICT_TARGETS = SQLDialect.supportedBy(SQLDialect.SQLITE);
    static final Set<SQLDialect> NO_SUPPORT_DERIVED_COLUMN_LIST_IN_MERGE_USING = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.H2);
    static final Set<SQLDialect> NO_SUPPORT_SUBQUERY_IN_MERGE_USING = SQLDialect.supportedBy(SQLDialect.DERBY);
    static final Set<SQLDialect> REQUIRE_NEW_MYSQL_EXCLUDED_EMULATION = SQLDialect.supportedBy(SQLDialect.MYSQL);
    static final Set<SQLDialect> NO_SUPPORT_INSERT_ALIASED_TABLE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.TRINO);
    final FieldMapsForInsert insertMaps;
    Select<?> select;
    boolean defaultValues;
    boolean onDuplicateKeyUpdate;
    boolean onDuplicateKeyIgnore;
    Constraint onConstraint;
    UniqueKey<R> onConstraintUniqueKey;
    QueryPartList<Field<?>> onConflict;
    final ConditionProviderImpl onConflictWhere;
    final FieldMapForUpdate updateMap;
    final ConditionProviderImpl updateWhere;

    /* JADX INFO: Access modifiers changed from: package-private */
    public InsertQueryImpl(Configuration configuration, WithImpl with, Table<R> into) {
        super(configuration, with, into);
        this.insertMaps = new FieldMapsForInsert(into);
        this.onConflictWhere = new ConditionProviderImpl();
        this.updateMap = new FieldMapForUpdate(into, FieldMapForUpdate.SetClause.INSERT, Clause.INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT);
        this.updateWhere = new ConditionProviderImpl();
    }

    @Override // org.jooq.InsertQuery
    public final void newRecord() {
        this.insertMaps.newRecord();
    }

    @Override // org.jooq.impl.AbstractStoreQuery
    /* renamed from: getValues */
    protected final Map<Field<?>, Field<?>> getValues2() {
        return this.insertMaps.lastMap();
    }

    final FieldMapsForInsert getInsertMaps() {
        return this.insertMaps;
    }

    final Select<?> getSelect() {
        return this.select;
    }

    @Override // org.jooq.InsertQuery
    public final void addRecord(R record) {
        newRecord();
        setRecord(record);
    }

    @Override // org.jooq.InsertQuery
    public final void onConflict(Field<?>... fields) {
        onConflict(Arrays.asList(fields));
    }

    @Override // org.jooq.InsertQuery
    public final void onConflict(Collection<? extends Field<?>> fields) {
        this.onConflict = new QueryPartList(fields).qualify(false);
    }

    @Override // org.jooq.InsertQuery
    public final void onConflictWhere(Condition conditions) {
        this.onConflictWhere.addConditions(conditions);
    }

    @Override // org.jooq.InsertQuery
    public final void onConflictOnConstraint(Constraint constraint) {
        onConflictOnConstraint0(constraint);
    }

    @Override // org.jooq.InsertQuery
    public void onConflictOnConstraint(UniqueKey<R> constraint) {
        if (StringUtils.isEmpty(constraint.getName())) {
            throw new IllegalArgumentException("UniqueKey's name is not specified");
        }
        this.onConstraintUniqueKey = constraint;
        onConflictOnConstraint0(DSL.constraint(DSL.name(constraint.getName())));
    }

    @Override // org.jooq.InsertQuery
    public final void onConflictOnConstraint(Name constraint) {
        onConflictOnConstraint0(DSL.constraint(constraint));
    }

    private final void onConflictOnConstraint0(Constraint constraint) {
        this.onConstraint = constraint;
        if (this.onConstraintUniqueKey == null) {
            this.onConstraintUniqueKey = (UniqueKey) Tools.findAny(table().getKeys(), key -> {
                return constraint.getName().equals(key.getName());
            });
        }
    }

    @Override // org.jooq.InsertQuery
    public final void onDuplicateKeyUpdate(boolean flag) {
        this.onDuplicateKeyUpdate = flag;
        if (flag) {
            this.onDuplicateKeyIgnore = false;
        }
    }

    @Override // org.jooq.InsertQuery
    public final void onDuplicateKeyIgnore(boolean flag) {
        this.onDuplicateKeyIgnore = flag;
        if (flag) {
            this.onDuplicateKeyUpdate = false;
            this.updateMap.clear();
            this.updateWhere.setWhere(null);
        }
    }

    @Override // org.jooq.InsertQuery
    public final <T> void addValueForUpdate(Field<T> field, T value) {
        this.updateMap.put((FieldOrRow) field, (FieldOrRowOrSelect) Tools.field(value, field));
    }

    @Override // org.jooq.InsertQuery
    public final <T> void addValueForUpdate(Field<T> field, Field<T> value) {
        this.updateMap.put((FieldOrRow) field, (FieldOrRowOrSelect) Tools.field(value, field));
    }

    @Override // org.jooq.InsertQuery
    public final void addValuesForUpdate(Map<?, ?> map) {
        this.updateMap.set(map);
    }

    @Override // org.jooq.InsertQuery
    public final void setRecordForUpdate(R record) {
        for (int i = 0; i < record.size(); i++) {
            if (record.changed(i)) {
                addValueForUpdate((Field<Field>) record.field(i), (Field) record.get(i));
            }
        }
    }

    @Override // org.jooq.InsertQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition conditions) {
        this.updateWhere.addConditions(conditions);
    }

    @Override // org.jooq.InsertQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition... conditions) {
        this.updateWhere.addConditions(conditions);
    }

    @Override // org.jooq.InsertQuery, org.jooq.ConditionProvider
    public final void addConditions(Collection<? extends Condition> conditions) {
        this.updateWhere.addConditions(conditions);
    }

    @Override // org.jooq.InsertQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition conditions) {
        this.updateWhere.addConditions(operator, conditions);
    }

    @Override // org.jooq.InsertQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition... conditions) {
        this.updateWhere.addConditions(operator, conditions);
    }

    @Override // org.jooq.InsertQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        this.updateWhere.addConditions(operator, conditions);
    }

    @Override // org.jooq.InsertQuery
    public final void setDefaultValues() {
        this.defaultValues = true;
        this.select = null;
    }

    private final boolean defaultValues(Configuration c) {
        return this.defaultValues;
    }

    @Override // org.jooq.InsertQuery
    public final void setSelect(Field<?>[] f, Select<?> s) {
        setSelect(Arrays.asList(f), s);
    }

    @Override // org.jooq.InsertQuery
    public final void setSelect(Collection<? extends Field<?>> f, Select<?> s) {
        this.defaultValues = false;
        this.insertMaps.clear();
        this.insertMaps.addFields(f);
        this.select = s;
    }

    @Override // org.jooq.StoreQuery
    public final void addValues(Map<?, ?> map) {
        this.insertMaps.set(map);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.scopeStart(this);
        Table<?> t = InlineDerivedTable.inlineDerivedTable(ctx, table(ctx));
        if (t instanceof InlineDerivedTable) {
            InlineDerivedTable<?> i = (InlineDerivedTable) t;
            copy(d -> {
                if (!d.insertMaps.values.isEmpty()) {
                    Table<?> m = DSL.table(DSL.name("t"));
                    if ((this.onDuplicateKeyIgnore || this.onDuplicateKeyUpdate) && ctx.configuration().requireCommercial(() -> {
                        return "InlineDerivedTable emulation for INSERT .. ON DUPLICATE KEY clauses is available in the commercial jOOQ editions only";
                    })) {
                    }
                    d.select = DSL.selectFrom((d.select != null ? d.select : d.insertMaps.insertSelect(ctx, null)).asTable(m, d.insertMaps.keysFlattened(ctx, GeneratorStatementType.INSERT))).where((Condition) CustomCondition.of(c1 -> {
                        c1.scopeRegister(i.table, false, m).visit(i.condition).scopeRegister(i.table, false, null);
                    }));
                }
            }, i.table).accept0(ctx);
        } else {
            accept0(ctx);
        }
        ctx.scopeEnd();
    }

    /* JADX WARN: Type inference failed for: r0v101, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v113, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v130, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v144, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v156, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v161, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v175, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v182, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v30, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v39, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v50, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v55, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v85, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractDMLQuery
    final void accept1(Context<?> ctx) {
        if (this.onDuplicateKeyUpdate) {
            switch (ctx.family()) {
                case DUCKDB:
                case POSTGRES:
                case SQLITE:
                case YUGABYTEDB:
                    if (ctx.dialect().supports(SQLDialect.POSTGRES) && this.onConstraint == null && this.onConflict == null && this.returning.isEmpty() && table().getKeys().size() > 1) {
                        acceptMerge(ctx);
                        break;
                    } else {
                        ctx.data(Tools.BooleanDataKey.DATA_MANDATORY_WHERE_CLAUSE, Boolean.valueOf(ctx.family() == SQLDialect.SQLITE), c -> {
                            toSQLInsert(c, false);
                        });
                        ctx.formatSeparator().start(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE).visit(Keywords.K_ON_CONFLICT).sql(' ');
                        if (this.onConstraint != null) {
                            ctx.data(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE, true);
                            ctx.visit(Keywords.K_ON_CONSTRAINT).sql(' ').visit(this.onConstraint);
                            ctx.data().remove(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE);
                        } else if (this.onConflict != null && this.onConflict.size() > 0) {
                            ctx.sql('(').visit(this.onConflict).sql(')');
                        } else if (!SUPPORTS_OPTIONAL_DO_UPDATE_CONFLICT_TARGETS.contains(ctx.dialect()) || this.onConflictWhere.hasWhere()) {
                            if (table().getPrimaryKey() == null) {
                                ctx.sql("[unknown primary key]");
                            } else {
                                ctx.sql('(').qualify(false, c2 -> {
                                    c2.visit(new FieldsImpl(table().getPrimaryKey().getFields()));
                                }).sql(')');
                            }
                        }
                        acceptOnConflictWhere(ctx);
                        ctx.formatSeparator().visit(Keywords.K_DO_UPDATE).formatSeparator().visit(Keywords.K_SET).formatIndentStart().formatSeparator().visit(updateMapComputedOnClientStored(ctx)).formatIndentEnd();
                        if (this.updateWhere.hasWhere()) {
                            ctx.formatSeparator().visit(Keywords.K_WHERE).sql(' ').visit((Condition) this.updateWhere);
                        }
                        ctx.end(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE);
                        break;
                    }
                    break;
                case DERBY:
                case FIREBIRD:
                case H2:
                case HSQLDB:
                    acceptMerge(ctx);
                    break;
                default:
                    boolean oldQualify = ctx.qualify();
                    boolean newQualify = ctx.family() != SQLDialect.H2 && oldQualify;
                    FieldMapForUpdate um = updateMapComputedOnClientStored(ctx);
                    boolean requireNewMySQLExcludedEmulation = REQUIRE_NEW_MYSQL_EXCLUDED_EMULATION.contains(ctx.dialect()) && Tools.anyMatch(um.values(), v -> {
                        return v instanceof Excluded;
                    });
                    Set<Field<?>> keys = toSQLInsert(ctx, requireNewMySQLExcludedEmulation);
                    if (requireNewMySQLExcludedEmulation && this.select == null) {
                        ctx.formatSeparator().visit(Keywords.K_AS).sql(' ').visit(DSL.name("t"));
                    }
                    ctx.formatSeparator().start(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE).visit(Keywords.K_ON_DUPLICATE_KEY_UPDATE).formatIndentStart().formatSeparator().qualify(newQualify);
                    if (this.updateWhere.hasWhere()) {
                        ctx.data(Tools.SimpleDataKey.DATA_ON_DUPLICATE_KEY_WHERE, this.updateWhere.getWhere());
                    }
                    if (requireNewMySQLExcludedEmulation) {
                        um.replaceAll((k, v2) -> {
                            if (!(v2 instanceof Excluded)) {
                                return v2;
                            }
                            Excluded<?> e = (Excluded) v2;
                            return keys.contains(e.$field()) ? v2 : Tools.qualify((Table<?>) table(), (Field) e.$field());
                        });
                    }
                    ctx.visit(um);
                    if (this.updateWhere.hasWhere()) {
                        ctx.data().remove(Tools.SimpleDataKey.DATA_ON_DUPLICATE_KEY_WHERE);
                    }
                    ctx.qualify(oldQualify).formatIndentEnd().end(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
            }
        } else if (this.onDuplicateKeyIgnore) {
            switch (ctx.family()) {
                case DUCKDB:
                case POSTGRES:
                case SQLITE:
                case YUGABYTEDB:
                    ctx.data(Tools.BooleanDataKey.DATA_MANDATORY_WHERE_CLAUSE, Boolean.valueOf(ctx.family() == SQLDialect.SQLITE), c3 -> {
                        toSQLInsert(c3, false);
                    });
                    ctx.formatSeparator().start(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE).visit(Keywords.K_ON_CONFLICT);
                    if (this.onConstraint != null) {
                        ctx.data(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE, true, c4 -> {
                            c4.sql(' ').visit(Keywords.K_ON_CONSTRAINT).sql(' ').visit(this.onConstraint);
                        });
                    } else if (this.onConflict != null && this.onConflict.size() > 0) {
                        ctx.sql(" (").visit(this.onConflict).sql(')');
                        acceptOnConflictWhere(ctx);
                    }
                    ctx.formatSeparator().visit(Keywords.K_DO_NOTHING).end(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
                case DERBY:
                    if (this.select != null) {
                        acceptInsertSelect(ctx);
                        break;
                    } else {
                        acceptMerge(ctx);
                        break;
                    }
                case FIREBIRD:
                    acceptMerge(ctx);
                    break;
                case H2:
                case HSQLDB:
                    acceptMerge(ctx);
                    break;
                case IGNITE:
                case TRINO:
                    acceptInsertSelect(ctx);
                    break;
                case CUBRID:
                    FieldMapForUpdate update = new FieldMapForUpdate(table(), FieldMapForUpdate.SetClause.INSERT, Clause.INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT);
                    Field<?> field = table().field(0);
                    update.put((FieldOrRow) field, (FieldOrRowOrSelect) field);
                    toSQLInsert(ctx, false);
                    ctx.formatSeparator().start(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE).visit(Keywords.K_ON_DUPLICATE_KEY_UPDATE).sql(' ').visit(update).end(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
                default:
                    toSQLInsert(ctx, false);
                    ctx.start(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE).end(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
            }
        } else {
            toSQLInsert(ctx, false);
            ctx.start(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE).end(Clause.INSERT_ON_DUPLICATE_KEY_UPDATE);
        }
        ctx.start(Clause.INSERT_RETURNING);
        toSQLReturning(ctx);
        ctx.end(Clause.INSERT_RETURNING);
    }

    private final void acceptOnConflictWhere(Context<?> ctx) {
        if (this.onConflictWhere.hasWhere()) {
            ctx.paramType(ParamType.INLINED, c1 -> {
                c1.qualify(false, c2 -> {
                    c2.formatSeparator().visit(Keywords.K_WHERE).sql(' ').visit(this.onConflictWhere.getWhere());
                });
            });
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v53, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private final Set<Field<?>> toSQLInsert(Context<?> ctx, boolean requireNewMySQLExcludedEmulation) {
        ctx.start(Clause.INSERT_INSERT_INTO).visit(Keywords.K_INSERT).sql(' ');
        if (this.onDuplicateKeyIgnore && SUPPORT_INSERT_IGNORE.contains(ctx.dialect())) {
            ctx.visit(Keywords.K_IGNORE).sql(' ');
        }
        ctx.visit(Keywords.K_INTO).sql(' ').declareTables(true, c -> {
            Table<?> t = table(c);
            if (NO_SUPPORT_INSERT_ALIASED_TABLE.contains(ctx.dialect())) {
                ctx.visit((QueryPart) StringUtils.defaultIfNull(Tools.aliased(t), t));
            } else {
                c.visit(t);
            }
        });
        Set<Field<?>> fields = this.insertMaps.toSQLReferenceKeys(ctx);
        ctx.end(Clause.INSERT_INSERT_INTO);
        if (this.select != null) {
            Set<Field<?>> keysFlattened = this.insertMaps.keysFlattened(ctx, GeneratorStatementType.INSERT);
            if (keysFlattened.size() == 0) {
                ctx.data(Tools.BooleanDataKey.DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST, true);
            }
            ctx.data(Tools.BooleanDataKey.DATA_INSERT_SELECT, true);
            Select<?> s = this.select;
            if (requireNewMySQLExcludedEmulation) {
                s = DSL.selectFrom(s.asTable(DSL.table(DSL.name("t")), keysFlattened));
            }
            FieldMapsForInsert.toSQLInsertSelect(ctx, s);
            ctx.data().remove(Tools.BooleanDataKey.DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST);
            ctx.data().remove(Tools.BooleanDataKey.DATA_INSERT_SELECT);
        } else if (defaultValues(ctx.configuration())) {
            switch (ctx.family()) {
                case DERBY:
                case MARIADB:
                case MYSQL:
                    acceptDefaultValuesEmulation(ctx, table().fields().length);
                    break;
                default:
                    ctx.formatSeparator().visit(Keywords.K_DEFAULT_VALUES);
                    break;
            }
        } else {
            ctx.visit(this.insertMaps);
        }
        return fields;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void acceptDefaultValuesEmulation(Context<?> ctx, int length) {
        ctx.formatSeparator().visit(Keywords.K_VALUES).sql(" (").visit(QueryPartListView.wrap(Collections.nCopies(length, Keywords.K_DEFAULT))).sql(')');
    }

    private final List<List<? extends Field<?>>> conflictingKeys(Context<?> ctx) {
        if (this.onConflict != null && this.onConflict.size() > 0) {
            return Collections.singletonList(this.onConflict);
        }
        if (this.onConstraintUniqueKey != null) {
            return Collections.singletonList(this.onConstraintUniqueKey.getFields());
        }
        if (Boolean.TRUE.equals(Tools.settings(ctx.configuration()).isEmulateOnDuplicateKeyUpdateOnPrimaryKeyOnly())) {
            return Collections.singletonList(table().getPrimaryKey().getFields());
        }
        return Tools.map(table().getKeys(), k -> {
            return k.getFields();
        });
    }

    private final void acceptInsertSelect(Context<?> ctx) {
        List<List<? extends Field<?>>> keys = conflictingKeys(ctx);
        if (!keys.isEmpty()) {
            Select<Record> rows = null;
            Set<Field<?>> fields = this.insertMaps.keysFlattened(ctx, GeneratorStatementType.INSERT);
            if (this.select != null) {
                Map<Field<?>, Field<?>> map = new HashMap<>();
                Field<?>[] names = Tools.fields(Tools.degree(this.select));
                List<Field<?>> f = new ArrayList<>(fields);
                for (int i = 0; i < fields.size() && i < names.length; i++) {
                    map.put(f.get(i), names[i]);
                }
                rows = DSL.selectFrom(this.select.asTable(DSL.table(DSL.name("t")), names)).whereNotExists(DSL.selectOne().from(table()).where(matchByConflictingKeys(ctx, map)));
            } else {
                for (Map<Field<?>, Field<?>> map2 : this.insertMaps.maps()) {
                    Select<Record> row = DSL.select(Tools.aliasedFields((Collection) map2.entrySet().stream().filter(e -> {
                        return fields.contains(e.getKey());
                    }).map((v0) -> {
                        return v0.getValue();
                    }).collect(Collectors.toList()))).whereNotExists(DSL.selectOne().from(table()).where(matchByConflictingKeys(ctx, map2)));
                    if (rows == null) {
                        rows = row;
                    } else {
                        rows = rows.unionAll(row);
                    }
                }
            }
            ctx.visit(ctx.dsl().insertInto(table()).columns(fields).select(DSL.selectFrom(rows.asTable("t"))));
            return;
        }
        ctx.sql("[ The ON DUPLICATE KEY IGNORE/UPDATE clause cannot be emulated when inserting into tables without any known keys : " + String.valueOf(table()) + " ]");
    }

    private final void acceptMerge(Context<?> ctx) {
        ctx.data(Tools.ExtendedDataKey.DATA_INSERT_ON_DUPLICATE_KEY_UPDATE, this, c -> {
            acceptMerge0(c);
        });
    }

    private final void acceptMerge0(Context<?> ctx) {
        Select<?> insertSelect;
        Table<?> t;
        MergeOnConditionStep<R> on;
        MergeNotMatchedWhereStep<R> values;
        MergeNotMatchedStep<R> mergeNotMatchedStep;
        if ((this.onConflict != null && this.onConflict.size() > 0) || this.onConstraint != null || !table().getKeys().isEmpty()) {
            Set<Field<?>> k = this.insertMaps.keysFlattened(ctx, null);
            Collection<? extends Field<?>> f = null;
            if (!NO_SUPPORT_SUBQUERY_IN_MERGE_USING.contains(ctx.dialect()) || this.select != null || this.insertMaps.rows > 1) {
                f = k.isEmpty() ? Arrays.asList(table().fields()) : k;
                if (this.select != null) {
                    insertSelect = this.select;
                } else {
                    insertSelect = this.insertMaps.insertSelect(ctx, null);
                }
                Select<?> s = insertSelect;
                if (s == null) {
                    s = DSL.select(Tools.map(f, x -> {
                        return x.getDataType().defaulted() ? x.getDataType().default_() : DSL.inline((Object) null, x);
                    }));
                }
                if (NO_SUPPORT_DERIVED_COLUMN_LIST_IN_MERGE_USING.contains(ctx.dialect())) {
                    t = new AliasedSelect(s, true, true, false, (Name[]) Tools.map(f, (v0) -> {
                        return v0.getUnqualifiedName();
                    }, x$0 -> {
                        return new Name[x$0];
                    })).as("t");
                } else {
                    t = s.asTable("t", (String[]) Tools.map(f, (v0) -> {
                        return v0.getName();
                    }, x$02 -> {
                        return new String[x$02];
                    }));
                }
            } else {
                t = null;
            }
            if (t != null) {
                on = ctx.dsl().mergeInto(table()).using(t).on(matchByConflictingKeys(ctx, t));
            } else {
                on = ctx.dsl().mergeInto(table()).usingDual().on(matchByConflictingKeys(ctx, this.insertMaps.lastMap()));
            }
            MergeOnConditionStep<R> on2 = on;
            MergeNotMatchedStep<R> notMatched = on2;
            if (this.onDuplicateKeyUpdate) {
                FieldMapForUpdate um = new FieldMapForUpdate(this.updateMap, FieldMapForUpdate.SetClause.INSERT);
                Table<?> table = t;
                um.replaceAll((key, v) -> {
                    if (v instanceof Excluded) {
                        Excluded<?> e = (Excluded) v;
                        if (table != null) {
                            return (FieldOrRowOrSelect) Tools.orElse(table.field(e.$field()), () -> {
                                return Tools.qualify((Table<?>) table(), (Field) e.$field());
                            });
                        }
                        return (FieldOrRowOrSelect) Tools.orElse(this.insertMaps.lastMap().get(e.$field()), () -> {
                            return Tools.qualify((Table<?>) table(), (Field) e.$field());
                        });
                    }
                    return v;
                });
                FieldMapForUpdate um2 = updateMapComputedOnClientStored(ctx, um);
                if (this.updateWhere.hasWhere()) {
                    mergeNotMatchedStep = on2.whenMatchedAnd(this.updateWhere.getWhere()).thenUpdate().set(um2);
                } else {
                    mergeNotMatchedStep = on2.whenMatchedThenUpdate().set(um2);
                }
                notMatched = mergeNotMatchedStep;
            }
            if (t != null) {
                values = notMatched.whenNotMatchedThenInsert(f).values(t.fields());
            } else {
                values = notMatched.whenNotMatchedThenInsert(k).values((Collection<?>) this.insertMaps.lastMap().entrySet().stream().filter(e -> {
                    return k.contains(e.getKey());
                }).map((v0) -> {
                    return v0.getValue();
                }).collect(Collectors.toList()));
            }
            ctx.visit(values);
            return;
        }
        ctx.sql("[ The ON DUPLICATE KEY IGNORE/UPDATE clause cannot be emulated when inserting into non-updatable tables : " + String.valueOf(table()) + " ]");
    }

    private final FieldMapForUpdate updateMapComputedOnClientStored(Context<?> ctx) {
        return updateMapComputedOnClientStored(ctx, new FieldMapForUpdate(this.updateMap, FieldMapForUpdate.SetClause.INSERT));
    }

    private final FieldMapForUpdate updateMapComputedOnClientStored(Context<?> ctx, FieldMapForUpdate um) {
        return um;
    }

    private final Condition matchByConflictingKeys(Context<?> ctx, Map<Field<?>, Field<?>> map) {
        Condition or = null;
        if (this.onConstraint != null && this.onConstraintUniqueKey == null) {
            return DSL.condition("[ cannot create predicate from constraint with unknown columns ]");
        }
        for (List<? extends Field<?>> fields : conflictingKeys(ctx)) {
            Condition and = null;
            Iterator<? extends Field<?>> it = fields.iterator();
            while (it.hasNext()) {
                Field<T> field = (Field) it.next();
                Condition other = matchByConflictingKey(ctx, field, (Field) map.get(field));
                and = and == null ? other : and.and(other);
            }
            or = or == null ? and : or.or(and);
        }
        return or;
    }

    private final Condition matchByConflictingKeys(Context<?> ctx, Table<?> s) {
        Condition or = null;
        if (this.onConstraint != null && this.onConstraintUniqueKey == null) {
            return DSL.condition("[ cannot create predicate from constraint with unknown columns ]");
        }
        for (List<? extends Field<?>> fields : conflictingKeys(ctx)) {
            Condition and = null;
            Iterator<? extends Field<?>> it = fields.iterator();
            while (it.hasNext()) {
                Field<T> field = (Field) it.next();
                Condition other = matchByConflictingKey(ctx, field, s.field(field));
                and = and == null ? other : and.and(other);
            }
            or = or == null ? and : or.or(and);
        }
        return or;
    }

    private final <T> Condition matchByConflictingKey(Context<?> ctx, Field<T> f, Field<T> v) {
        return f.eq((Field) v);
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.Query
    public final boolean isExecutable() {
        return this.insertMaps.isExecutable() || defaultValues(configuration()) || this.select != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public final InsertQueryImpl<R> copy(Consumer<? super InsertQueryImpl<R>> consumer) {
        return (InsertQueryImpl<R>) copy(consumer, this.table);
    }

    final <O extends Record> InsertQueryImpl<O> copy(Consumer<? super InsertQueryImpl<O>> finisher, Table<O> t) {
        InsertQueryImpl<O> i = new InsertQueryImpl<>(configuration(), this.with, t);
        if (!this.returning.isEmpty()) {
            i.setReturning(this.returning);
        }
        i.insertMaps.from(this.insertMaps);
        i.defaultValues = this.defaultValues;
        i.select = this.select;
        if (this.onConflict != null) {
            i.onConflict(this.onConflict);
        }
        if (this.onConflictWhere.hasWhere()) {
            i.onConflictWhere.setWhere(ConditionProviderImpl.extractCondition(this.onConflictWhere));
        }
        i.onConstraint = this.onConstraint;
        i.onConstraintUniqueKey = this.onConstraintUniqueKey;
        i.onDuplicateKeyIgnore = this.onDuplicateKeyIgnore;
        i.onDuplicateKeyUpdate = this.onDuplicateKeyUpdate;
        i.updateWhere.setWhere(this.updateWhere.getWhere());
        i.updateMap.putAll(this.updateMap);
        finisher.accept(i);
        return i;
    }

    @Override // org.jooq.impl.QOM.Insert
    public final WithImpl $with() {
        return this.with;
    }

    @Override // org.jooq.impl.QOM.Insert
    public final Table<R> $into() {
        return this.table;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $into(Table<?> table) {
        if ($into() == table) {
            return this;
        }
        return copy(i -> {
        }, table);
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableList<? extends Field<?>> $columns() {
        return QOM.unmodifiable((List) new ArrayList(this.insertMaps.values.keySet()));
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $columns(Collection<? extends Field<?>> columns) {
        return copy(i -> {
            Map<Field<?>, List<Field<?>>> v = new LinkedHashMap<>();
            Iterator it = columns.iterator();
            while (it.hasNext()) {
                Field<?> c = (Field) it.next();
                if (i.insertMaps.values.get(c) == null) {
                    v.put(c, new ArrayList<>(Collections.nCopies(i.insertMaps.rows, DSL.inline((Object) null, c))));
                } else {
                    v.put(c, i.insertMaps.values.get(c));
                }
            }
            i.insertMaps.values.clear();
            i.insertMaps.values.putAll(v);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final Select<?> $select() {
        return this.select;
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $select(Select<?> newSelect) {
        if ($select() == newSelect) {
            return this;
        }
        return copy(i -> {
            i.setSelect($columns(), (Select<?>) newSelect);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final boolean $defaultValues() {
        return this.defaultValues;
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $defaultValues(boolean newDefaultValues) {
        if ($defaultValues() == newDefaultValues) {
            return this;
        }
        return copy(i -> {
            if (newDefaultValues) {
                i.setDefaultValues();
            } else {
                i.defaultValues = false;
            }
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableList<? extends Row> $values() {
        return QOM.unmodifiable((List) this.insertMaps.rows());
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $values(Collection<? extends Row> values) {
        return copy(i -> {
            i.insertMaps.rows = values.size();
            int index = 0;
            for (Map.Entry<Field<?>, List<Field<?>>> e : i.insertMaps.values.entrySet()) {
                int c = index;
                Field<?> n = DSL.inline((Object) null, e.getKey());
                e.getValue().clear();
                e.getValue().addAll(Tools.map(values, v -> {
                    return (Field) StringUtils.defaultIfNull(v.field(c), n);
                }));
                index++;
            }
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final boolean $onDuplicateKeyIgnore() {
        return this.onDuplicateKeyIgnore;
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onDuplicateKeyIgnore(boolean newOnDuplicateKeyIgnore) {
        if ($onDuplicateKeyIgnore() == newOnDuplicateKeyIgnore) {
            return this;
        }
        return copy(i -> {
            i.onDuplicateKeyIgnore(newOnDuplicateKeyIgnore);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final boolean $onDuplicateKeyUpdate() {
        return this.onDuplicateKeyUpdate;
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onDuplicateKeyUpdate(boolean newOnDuplicateKeyUpdate) {
        if ($onDuplicateKeyUpdate() == newOnDuplicateKeyUpdate) {
            return this;
        }
        return copy(i -> {
            i.onDuplicateKeyUpdate(newOnDuplicateKeyUpdate);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableList<? extends Field<?>> $onConflict() {
        return QOM.unmodifiable(this.onConflict == null ? new ArrayList() : this.onConflict);
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onConflict(Collection<? extends Field<?>> newOnConflict) {
        if ($onConflict() == newOnConflict) {
            return this;
        }
        return copy(i -> {
            i.onConflict((Collection<? extends Field<?>>) newOnConflict);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final Condition $onConflictWhere() {
        return this.onConflictWhere.getWhereOrNull();
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $onConflictWhere(Condition newWhere) {
        if ($onConflictWhere() == newWhere) {
            return this;
        }
        return copy(i -> {
            i.onConflictWhere.setWhere(newWhere);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.UnmodifiableMap<? extends FieldOrRow, ? extends FieldOrRowOrSelect> $updateSet() {
        return QOM.unmodifiable(this.updateMap);
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $updateSet(Map<? extends FieldOrRow, ? extends FieldOrRowOrSelect> newUpdateSet) {
        if ($updateSet() == newUpdateSet) {
            return this;
        }
        return copy(i -> {
            i.addValuesForUpdate(newUpdateSet);
        });
    }

    @Override // org.jooq.impl.QOM.Insert
    public final Condition $updateWhere() {
        return this.updateWhere.getWhereOrNull();
    }

    @Override // org.jooq.impl.QOM.Insert
    public final QOM.Insert<?> $updateWhere(Condition newWhere) {
        if ($updateWhere() == newWhere) {
            return this;
        }
        return copy(i -> {
            i.updateWhere.setWhere(newWhere);
        });
    }
}
