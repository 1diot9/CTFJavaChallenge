package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.InverseForeignKey;
import org.jooq.JoinType;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOptionalOnStep;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TableImpl.class */
public class TableImpl<R extends Record> extends AbstractTable<R> implements ScopeMappable, ScopeNestable, SimpleCheckQueryPart, QOM.UEmpty {
    private static final Clause[] CLAUSES_TABLE_REFERENCE = {Clause.TABLE, Clause.TABLE_REFERENCE};
    private static final Clause[] CLAUSES_TABLE_ALIAS = {Clause.TABLE, Clause.TABLE_ALIAS};
    private static final Set<SQLDialect> NO_SUPPORT_QUALIFIED_TVF_CALLS = SQLDialect.supportedBy(SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> REQUIRES_TVF_TABLE_CONSTRUCTOR = SQLDialect.supportedBy(SQLDialect.HSQLDB);
    final FieldsImpl<R> fields;
    final Alias<Table<R>> alias;
    final Condition where;
    protected final Field<?>[] parameters;
    final Table<?> path;
    final ForeignKey<?, R> childPath;
    final InverseForeignKey<?, R> parentPath;

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table whereNotExists(Select select) {
        return super.whereNotExists(select);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table whereExists(Select select) {
        return super.whereExists(select);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(String str, QueryPart[] queryPartArr) {
        return super.where(str, queryPartArr);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(String str, Object[] objArr) {
        return super.where(str, objArr);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(String str) {
        return super.where(str);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(SQL sql) {
        return super.where(sql);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(Field field) {
        return super.where((Field<Boolean>) field);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(Collection collection) {
        return super.where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(Condition[] conditionArr) {
        return super.where(conditionArr);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table where(Condition condition) {
        return super.where(condition);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table withOrdinality() {
        return super.withOrdinality();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table as(Table table) {
        return super.as((Table<?>) table);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Field rowid() {
        return super.rowid();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ List getChecks() {
        return super.getChecks();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ List getReferences() {
        return super.getReferences();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ List getUniqueKeys() {
        return super.getUniqueKeys();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ List getKeys() {
        return super.getKeys();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ List getIndexes() {
        return super.getIndexes();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ TableField getRecordTimestamp() {
        return super.getRecordTimestamp();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ TableField getRecordVersion() {
        return super.getRecordVersion();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ UniqueKey getPrimaryKey() {
        return super.getPrimaryKey();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Identity getIdentity() {
        return super.getIdentity();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Qualified
    public /* bridge */ /* synthetic */ Schema getSchema() {
        return super.getSchema();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public /* bridge */ /* synthetic */ Table as(String str) {
        return super.as(str);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public /* bridge */ /* synthetic */ Table $aliased() {
        return super.$aliased();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.QOM.Aliasable
    public /* bridge */ /* synthetic */ Name $alias() {
        return super.$alias();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public /* bridge */ /* synthetic */ Name getQualifiedName() {
        return super.getQualifiedName();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    @Deprecated
    public TableImpl(String name) {
        this(DSL.name(name));
    }

    @Deprecated
    public TableImpl(String name, Schema schema) {
        this(DSL.name(name), schema);
    }

    @Deprecated
    public TableImpl(String name, Schema schema, Table<R> aliased) {
        this(DSL.name(name), schema, aliased);
    }

    @Deprecated
    public TableImpl(String name, Schema schema, Table<R> aliased, Field<?>[] parameters) {
        this(DSL.name(name), schema, aliased, parameters);
    }

    @Deprecated
    public TableImpl(String name, Schema schema, Table<R> aliased, Field<?>[] parameters, String comment) {
        this(DSL.name(name), schema, aliased, parameters, comment);
    }

    public TableImpl(Name name) {
        this(name, (Schema) null, (Table<?>) null, (ForeignKey) null, (InverseForeignKey) null, (Table) null, (Field<?>[]) null, (Comment) null);
    }

    public TableImpl(Name name, Schema schema) {
        this(name, schema, (Table<?>) null, (ForeignKey) null, (InverseForeignKey) null, (Table) null, (Field<?>[]) null, (Comment) null);
    }

    public TableImpl(Name name, Schema schema, Table<R> aliased) {
        this(name, schema, (Table<?>) null, (ForeignKey) null, (InverseForeignKey) null, aliased, (Field<?>[]) null, (Comment) null);
    }

    public TableImpl(Name name, Schema schema, Table<R> aliased, Field<?>[] parameters) {
        this(name, schema, (Table<?>) null, (ForeignKey) null, (InverseForeignKey) null, aliased, parameters, (Comment) null);
    }

    @Deprecated
    public TableImpl(Name name, Schema schema, Table<R> aliased, Field<?>[] parameters, String comment) {
        this(name, schema, (Table<?>) null, (ForeignKey) null, (InverseForeignKey) null, aliased, parameters, DSL.comment(comment));
    }

    public TableImpl(Name name, Schema schema, Table<R> aliased, Field<?>[] parameters, Comment comment) {
        this(name, schema, (Table<?>) null, (ForeignKey) null, (InverseForeignKey) null, aliased, parameters, comment);
    }

    public TableImpl(Name name, Schema schema, Table<R> aliased, Field<?>[] parameters, Comment comment, TableOptions options) {
        this(name, schema, null, null, null, aliased, parameters, comment, options, null);
    }

    public TableImpl(Name name, Schema schema, Table<R> aliased, Field<?>[] parameters, Comment comment, TableOptions options, Condition where) {
        this(name, schema, null, null, null, aliased, parameters, comment, options, where);
    }

    @Deprecated
    public TableImpl(Table<?> path, ForeignKey<?, R> childPath, Table<R> aliased) {
        this(path, childPath, (InverseForeignKey) null, aliased);
    }

    public TableImpl(Table<?> path, ForeignKey<?, R> childPath, InverseForeignKey<?, R> parentPath, Table<R> aliased) {
        this(Internal.createPathAlias(path, childPath, parentPath), (Schema) null, path, childPath, parentPath, aliased, (Field<?>[]) null, aliased.getCommentPart());
    }

    @Deprecated
    public TableImpl(Name name, Schema schema, Table<?> path, ForeignKey<?, R> childPath, Table<R> aliased, Field<?>[] parameters, Comment comment) {
        this(name, schema, path, childPath, aliased, parameters, comment, TableOptions.table());
    }

    public TableImpl(Name name, Schema schema, Table<?> path, ForeignKey<?, R> childPath, InverseForeignKey<?, R> parentPath, Table<R> aliased, Field<?>[] parameters, Comment comment) {
        this(name, schema, path, childPath, parentPath, aliased, parameters, comment, TableOptions.table(), null);
    }

    @Deprecated
    public TableImpl(Name name, Schema schema, Table<?> path, ForeignKey<?, R> childPath, Table<R> aliased, Field<?>[] parameters, Comment comment, TableOptions options) {
        this(name, schema, path, childPath, null, aliased, parameters, comment, options);
    }

    @Deprecated
    public TableImpl(Name name, Schema schema, Table<?> path, ForeignKey<?, R> childPath, InverseForeignKey<?, R> parentPath, Table<R> aliased, Field<?>[] parameters, Comment comment, TableOptions options) {
        this(name, schema, path, childPath, parentPath, aliased, parameters, comment, options, null);
    }

    public TableImpl(Name name, Schema schema, Table<?> table, ForeignKey<?, R> foreignKey, InverseForeignKey<?, R> inverseForeignKey, Table<R> table2, Field<?>[] fieldArr, Comment comment, TableOptions tableOptions, Condition condition) {
        super(tableOptions, name, schema, comment);
        this.fields = new FieldsImpl<>((SelectField<?>[]) new SelectField[0]);
        if (foreignKey != null) {
            this.path = table;
            this.childPath = Tools.aliasedKey(foreignKey, table, this);
            this.parentPath = null;
        } else if (inverseForeignKey != null) {
            this.path = table;
            this.childPath = null;
            this.parentPath = Tools.aliasedKey(inverseForeignKey.getForeignKey(), this, table).getInverseKey();
        } else if (table2 instanceof TableImpl) {
            TableImpl tableImpl = (TableImpl) table2;
            this.path = tableImpl.path;
            this.childPath = tableImpl.childPath == null ? null : Tools.aliasedKey(tableImpl.childPath, tableImpl.path, this);
            this.parentPath = tableImpl.parentPath == null ? null : Tools.aliasedKey(tableImpl.parentPath.getForeignKey(), this, tableImpl.path).getInverseKey();
        } else {
            this.path = null;
            this.childPath = null;
            this.parentPath = null;
        }
        if (table2 != null) {
            Alias alias = Tools.alias(table2);
            if (alias != null) {
                this.alias = new Alias<>((Table<R>) alias.wrapped, this, name, alias.fieldAliases, alias.wrapInParentheses);
            } else {
                this.alias = new Alias<>(table2, this, name);
            }
        } else {
            this.alias = null;
        }
        this.parameters = fieldArr;
        this.where = condition;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Table<?> path(Table<?> t) {
        if (t instanceof TableImpl) {
            TableImpl<?> ti = (TableImpl) t;
            if (ti.path != null) {
                return ti.path;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Condition pathCondition() {
        if (this.childPath != null) {
            return wrapForImplicitJoin(((Join) new Join(this.path, this, null).onKey((ForeignKey<?, ?>) this.childPath)).condition.getWhere());
        }
        if (this.parentPath != null) {
            return wrapForImplicitJoin(new Join(this, this.path, null).onKey(this.parentPath.getForeignKey()).condition.getWhere());
        }
        return DSL.noCondition();
    }

    static final Condition wrapForImplicitJoin(Condition condition) {
        return CustomCondition.of(c -> {
            c.data(Tools.BooleanDataKey.DATA_RENDER_IMPLICIT_JOIN, true, c1 -> {
                c1.visit(condition);
            });
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public final Table<?> pathRoot() {
        Table<?> table = this.path;
        if (table instanceof TableImpl) {
            TableImpl<?> t = (TableImpl) table;
            if (t.path != null) {
                return t.pathRoot();
            }
            return t;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public Table<R> getAliasedTable() {
        if (this.alias != null) {
            return this.alias.wrapped();
        }
        return null;
    }

    @org.jooq.Internal
    protected boolean aliased() {
        return getAliasedTable() != null;
    }

    @org.jooq.Internal
    protected boolean isSynthetic() {
        return false;
    }

    @Override // org.jooq.impl.SimpleCheckQueryPart
    @org.jooq.Internal
    public final boolean isSimple(Context<?> ctx) {
        return this.alias == null && (this.parameters == null || this.parameters.length < 2);
    }

    @Override // org.jooq.impl.AbstractTable
    final FieldsImpl<R> fields0() {
        if (this.where != null) {
            return new InlineDerivedTable(this, this.where, false).fields0();
        }
        return this.fields;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Fields
    public Row fieldsRow() {
        return super.fieldsRow();
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return this.alias != null ? CLAUSES_TABLE_ALIAS : CLAUSES_TABLE_REFERENCE;
    }

    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v32, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        QueryPart as;
        if ((getTableType().isView() || getTableType().isFunction()) && isSynthetic() && ctx.declareTables()) {
            Select<?> s = getOptions().select();
            if (s != null) {
                as = s;
            } else {
                as = new DerivedTable(ctx.dsl().parser().parseSelect(getOptions().source(), this.parameters == null ? Tools.EMPTY_OBJECT : this.parameters)).as(getUnqualifiedName());
            }
            ctx.visit(as);
            return;
        }
        if (this.path != null) {
            ctx.scopeRegister(this);
        }
        if (this.alias != null) {
            ctx.visit(this.alias);
            return;
        }
        if (this.parameters != null && REQUIRES_TVF_TABLE_CONSTRUCTOR.contains(ctx.dialect()) && ctx.declareTables()) {
            ctx.visit(Keywords.K_TABLE).sql('(');
            accept0(ctx);
            ctx.sql(')');
            if (ctx.declareAliases()) {
                ctx.sql(' ').visit(Tools.getMappedTable(ctx, this).getUnqualifiedName());
                return;
            }
            return;
        }
        accept0(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        if (ctx.declareTables()) {
            ctx.scopeMarkStart(this);
        }
        if (ctx.qualifySchema() && (ctx.declareTables() || !NO_SUPPORT_QUALIFIED_TVF_CALLS.contains(ctx.dialect()) || this.parameters == null)) {
            QualifiedImpl.acceptMappedSchemaPrefix(ctx, getSchema());
        }
        ctx.visit(Tools.getMappedTable(ctx, this).getUnqualifiedName());
        if (this.parameters != null && ctx.declareTables()) {
            if (ctx.family() == SQLDialect.FIREBIRD && this.parameters.length == 0) {
                ctx.visit(QueryPartListView.wrap(this.parameters));
            } else {
                ctx.sql('(').visit(QueryPartListView.wrap(this.parameters)).sql(')');
            }
        }
        if (ctx.declareTables()) {
            ctx.scopeMarkEnd(this);
        }
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public Table<R> as(Name as) {
        if (this.alias != null) {
            return this.alias.wrapped().as(as);
        }
        return new TableAlias(this, as);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public Table<R> as(Name as, Name... fieldAliases) {
        if (this.alias != null) {
            return this.alias.wrapped().as(as, fieldAliases);
        }
        return new TableAlias(this, as, fieldAliases);
    }

    public Table<R> rename(String rename) {
        return rename(DSL.name(rename));
    }

    public Table<R> rename(Name rename) {
        return new TableImpl(rename, getSchema());
    }

    public Table<R> rename(Table<?> rename) {
        return rename(rename.getQualifiedName());
    }

    public Class<? extends R> getRecordType() {
        return RecordImplN.class;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public boolean declaresTables() {
        return true;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type) {
        return super.join(table, type);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type, QOM.JoinHint hint) {
        return super.join(table, type, hint);
    }

    @Override // org.jooq.Fields
    public Field<?> field(String name) {
        return super.field(name);
    }

    @Override // org.jooq.Fields
    public <T> Field<T> field(String name, Class<T> type) {
        return super.field(name, type);
    }

    @Override // org.jooq.Fields
    public <T> Field<T> field(String name, DataType<T> dataType) {
        return super.field(name, dataType);
    }

    @Override // org.jooq.Fields
    public Field<?> field(Name name) {
        return super.field(name);
    }

    @Override // org.jooq.Fields
    public <T> Field<T> field(Name name, Class<T> type) {
        return super.field(name, type);
    }

    @Override // org.jooq.Fields
    public <T> Field<T> field(Name name, DataType<T> dataType) {
        return super.field(name, dataType);
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return ((Schema) StringUtils.defaultIfNull(getSchema(), SchemaImpl.DEFAULT_SCHEMA.get())).getQualifiedName().append(getUnqualifiedName()).hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof TableImpl) {
            TableImpl<?> t = (TableImpl) that;
            return StringUtils.equals(StringUtils.defaultIfNull(getSchema(), SchemaImpl.DEFAULT_SCHEMA.get()), StringUtils.defaultIfNull(t.getSchema(), SchemaImpl.DEFAULT_SCHEMA.get())) && StringUtils.equals(getName(), t.getName()) && Arrays.equals(this.parameters, t.parameters);
        }
        if (that instanceof TableAlias) {
            TableAlias<?> t2 = (TableAlias) that;
            if ($alias() != null) {
                return t2.getUnqualifiedName().equals($alias());
            }
            if (getSchema() == null) {
                return t2.getUnqualifiedName().equals(getQualifiedName());
            }
        }
        return super.equals(that);
    }
}
