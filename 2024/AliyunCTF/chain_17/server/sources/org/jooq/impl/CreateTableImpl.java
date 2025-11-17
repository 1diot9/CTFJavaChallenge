package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jooq.Clause;
import org.jooq.Comment;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Context;
import org.jooq.CreateTableAsStep;
import org.jooq.CreateTableCommentStep;
import org.jooq.CreateTableElementListStep;
import org.jooq.CreateTableFinalStep;
import org.jooq.CreateTableOnCommitStep;
import org.jooq.CreateTableStorageStep;
import org.jooq.CreateTableWithDataStep;
import org.jooq.DataType;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Function9;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Nullability;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableElement;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateTableImpl.class */
public final class CreateTableImpl extends AbstractDDLQuery implements QOM.CreateTable, CreateTableElementListStep, CreateTableAsStep, CreateTableWithDataStep, CreateTableOnCommitStep, CreateTableCommentStep, CreateTableStorageStep, CreateTableFinalStep {
    final Table<?> table;
    final boolean temporary;
    final boolean ifNotExists;
    QueryPartListView<? extends TableElement> tableElements;
    Select<?> select;
    QOM.WithOrWithoutData withData;
    QOM.TableCommitAction onCommit;
    Comment comment;
    SQL storage;
    static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    static final Set<SQLDialect> NO_SUPPORT_WITH_DATA = SQLDialect.supportedBy(SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.SQLITE);
    static final Set<SQLDialect> NO_SUPPORT_CTAS_COLUMN_NAMES = SQLDialect.supportedBy(SQLDialect.H2);
    static final Set<SQLDialect> EMULATE_INDEXES_IN_BLOCK = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_SOME_ENUM_TYPES_AS_CHECK = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_STORED_ENUM_TYPES_AS_CHECK = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.SQLITE);
    static final Set<SQLDialect> REQUIRES_WITH_DATA = SQLDialect.supportedBy(SQLDialect.HSQLDB);
    static final Set<SQLDialect> WRAP_SELECT_IN_PARENS = SQLDialect.supportedBy(SQLDialect.HSQLDB);
    static final Set<SQLDialect> SUPPORT_TEMPORARY = SQLDialect.supportedBy(SQLDialect.DUCKDB, SQLDialect.MARIADB, SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_TABLE_COMMENT_IN_BLOCK = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> EMULATE_COLUMN_COMMENT_IN_BLOCK = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> REQUIRE_EXECUTE_IMMEDIATE = SQLDialect.supportedBy(SQLDialect.FIREBIRD);
    static final Set<SQLDialect> NO_SUPPORT_NULLABLE_PRIMARY_KEY = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    static final Set<SQLDialect> REQUIRE_NON_PK_COLUMNS = SQLDialect.supportedBy(SQLDialect.IGNITE);

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep indexes(Collection collection) {
        return indexes((Collection<? extends Index>) collection);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep unique(Collection collection) {
        return unique((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep unique(Field[] fieldArr) {
        return unique((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep primaryKey(Collection collection) {
        return primaryKey((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep primaryKey(Field[] fieldArr) {
        return primaryKey((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep constraints(Collection collection) {
        return constraints((Collection<? extends Constraint>) collection);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep column(Field field, DataType dataType) {
        return column((Field<?>) field, (DataType<?>) dataType);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep column(Name name, DataType dataType) {
        return column(name, (DataType<?>) dataType);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep column(String str, DataType dataType) {
        return column(str, (DataType<?>) dataType);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep column(Field field) {
        return column((Field<?>) field);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep columns(Collection collection) {
        return columns((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep columns(Field[] fieldArr) {
        return columns((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.CreateTableElementListStep
    public /* bridge */ /* synthetic */ CreateTableElementListStep tableElements(Collection collection) {
        return tableElements((Collection<? extends TableElement>) collection);
    }

    @Override // org.jooq.CreateTableAsStep
    public /* bridge */ /* synthetic */ CreateTableWithDataStep as(Select select) {
        return as((Select<?>) select);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateTableImpl(Configuration configuration, Table<?> table, boolean temporary, boolean ifNotExists) {
        this(configuration, table, temporary, ifNotExists, null, null, null, null, null, null);
    }

    CreateTableImpl(Configuration configuration, Table<?> table, boolean temporary, boolean ifNotExists, Collection<? extends TableElement> tableElements, Select<?> select, QOM.WithOrWithoutData withData, QOM.TableCommitAction onCommit, Comment comment, SQL storage) {
        super(configuration);
        this.table = table;
        this.temporary = temporary;
        this.ifNotExists = ifNotExists;
        this.tableElements = new QueryPartList(tableElements);
        this.select = select;
        this.withData = withData;
        this.onCommit = onCommit;
        this.comment = comment;
        this.storage = storage;
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl tableElements(TableElement... tableElements) {
        return tableElements((Collection<? extends TableElement>) Arrays.asList(tableElements));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl tableElements(Collection<? extends TableElement> tableElements) {
        if (this.tableElements == null) {
            this.tableElements = new QueryPartList(tableElements);
        } else {
            this.tableElements.addAll(tableElements);
        }
        return this;
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl columns(String... columns) {
        return columns((Collection<? extends Field<?>>) Tools.fieldsByName(columns));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl columns(Name... columns) {
        return columns((Collection<? extends Field<?>>) Tools.fieldsByName(columns));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl columns(Field<?>... columns) {
        return columns((Collection<? extends Field<?>>) Arrays.asList(columns));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl columns(Collection<? extends Field<?>> columns) {
        return tableElements((Collection<? extends TableElement>) new QueryPartList(columns));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl column(Field<?> column) {
        return tableElements(column);
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl column(String field, DataType<?> type) {
        return column(DSL.name(field), type);
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl column(Name field, DataType<?> type) {
        return tableElements(DSL.field(field, type));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl column(Field<?> field, DataType<?> type) {
        return tableElements(DSL.field(field.getQualifiedName(), type));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl constraints(Constraint... constraints) {
        return constraints((Collection<? extends Constraint>) Arrays.asList(constraints));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl constraints(Collection<? extends Constraint> constraints) {
        return tableElements((Collection<? extends TableElement>) new QueryPartList(constraints));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl constraint(Constraint constraint) {
        return tableElements(constraint);
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl primaryKey(String... fields) {
        return primaryKey((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl primaryKey(Name... fields) {
        return primaryKey((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl primaryKey(Field<?>... fields) {
        return primaryKey((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl primaryKey(Collection<? extends Field<?>> fields) {
        return tableElements(DSL.primaryKey(new QueryPartList(fields)));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl unique(String... fields) {
        return unique((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl unique(Name... fields) {
        return unique((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl unique(Field<?>... fields) {
        return unique((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl unique(Collection<? extends Field<?>> fields) {
        return tableElements(DSL.unique(new QueryPartList(fields)));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl check(Condition condition) {
        return tableElements(DSL.check(condition));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl indexes(Index... indexes) {
        return indexes((Collection<? extends Index>) Arrays.asList(indexes));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl indexes(Collection<? extends Index> indexes) {
        return tableElements((Collection<? extends TableElement>) new QueryPartList(indexes));
    }

    @Override // org.jooq.CreateTableElementListStep
    public final CreateTableImpl index(Index index) {
        return tableElements(index);
    }

    @Override // org.jooq.CreateTableAsStep
    public final CreateTableImpl as(Select<?> select) {
        this.select = select;
        return this;
    }

    @Override // org.jooq.CreateTableWithDataStep
    public final CreateTableImpl withData() {
        this.withData = QOM.WithOrWithoutData.WITH_DATA;
        return this;
    }

    @Override // org.jooq.CreateTableWithDataStep
    public final CreateTableImpl withNoData() {
        this.withData = QOM.WithOrWithoutData.WITH_NO_DATA;
        return this;
    }

    @Override // org.jooq.CreateTableOnCommitStep
    public final CreateTableImpl onCommitDeleteRows() {
        this.onCommit = QOM.TableCommitAction.DELETE_ROWS;
        return this;
    }

    @Override // org.jooq.CreateTableOnCommitStep
    public final CreateTableImpl onCommitPreserveRows() {
        this.onCommit = QOM.TableCommitAction.PRESERVE_ROWS;
        return this;
    }

    @Override // org.jooq.CreateTableOnCommitStep
    public final CreateTableImpl onCommitDrop() {
        this.onCommit = QOM.TableCommitAction.DROP;
        return this;
    }

    @Override // org.jooq.CreateTableCommentStep
    public final CreateTableImpl comment(String comment) {
        return comment(DSL.comment(comment));
    }

    @Override // org.jooq.CreateTableCommentStep
    public final CreateTableImpl comment(Comment comment) {
        this.comment = comment;
        return this;
    }

    @Override // org.jooq.CreateTableStorageStep
    public final CreateTableImpl storage(SQL storage) {
        this.storage = storage;
        return this;
    }

    @Override // org.jooq.CreateTableStorageStep
    public final CreateTableImpl storage(String storage, QueryPart... parts) {
        return storage(DSL.sql(storage, parts));
    }

    @Override // org.jooq.CreateTableStorageStep
    public final CreateTableImpl storage(String storage, Object... bindings) {
        return storage(DSL.sql(storage, bindings));
    }

    @Override // org.jooq.CreateTableStorageStep
    public final CreateTableImpl storage(String storage) {
        return storage(DSL.sql(storage));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final QOM.UnmodifiableList<? extends Field<?>> $columns() {
        return QOM.unmodifiable((List) this.tableElements.stream().filter(e -> {
            return e instanceof Field;
        }).map(e2 -> {
            return (Field) e2;
        }).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final QOM.UnmodifiableList<? extends Constraint> $constraints() {
        return QOM.unmodifiable((List) this.tableElements.stream().filter(e -> {
            return e instanceof Constraint;
        }).map(e2 -> {
            return (Constraint) e2;
        }).collect(Collectors.toList()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final QOM.UnmodifiableList<? extends Index> $indexes() {
        return QOM.unmodifiable((List) this.tableElements.stream().filter(e -> {
            return e instanceof Index;
        }).map(e2 -> {
            return (Index) e2;
        }).collect(Collectors.toList()));
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_TABLE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    private final void accept0(Context<?> ctx) {
        boolean btc = this.comment != null && EMULATE_TABLE_COMMENT_IN_BLOCK.contains(ctx.dialect());
        boolean bcc = EMULATE_COLUMN_COMMENT_IN_BLOCK.contains(ctx.dialect()) && Tools.anyMatch($columns(), c -> {
            return !c.getComment().isEmpty();
        });
        boolean bi = !$indexes().isEmpty() && EMULATE_INDEXES_IN_BLOCK.contains(ctx.dialect());
        if (btc || bcc || bi) {
            Tools.begin(ctx, c1 -> {
                Tools.executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(c1.dialect()), c1, c2 -> {
                    accept1(c2);
                });
                if (btc) {
                    c1.formatSeparator();
                    Tools.executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(ctx.dialect()), c1, c22 -> {
                        c22.visit(DSL.commentOnTable(this.table).is(this.comment));
                    });
                }
                if (bcc) {
                    c1.formatSeparator();
                    for (Field<?> c3 : $columns()) {
                        if (!c3.getComment().isEmpty()) {
                            Tools.executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(ctx.dialect()), c1, c23 -> {
                                c23.visit(DSL.commentOnColumn(this.table.getQualifiedName().append(c3.getUnqualifiedName())).is(c3.getComment()));
                            });
                        }
                    }
                }
                if (bi) {
                    for (Index index : $indexes()) {
                        c1.formatSeparator();
                        Tools.executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(c1.dialect()), c1, c24 -> {
                            if ("".equals(index.getName())) {
                                c24.visit(DSL.createIndex().on(index.getTable(), index.getFields()));
                            } else {
                                c24.visit(DSL.createIndex(index.getUnqualifiedName()).on(index.getTable(), index.getFields()));
                            }
                        });
                    }
                }
            });
        } else {
            accept1(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    private final void accept1(Context<?> ctx) {
        ctx.start(Clause.CREATE_TABLE);
        if (this.select != null) {
            acceptCreateTableAsSelect(ctx);
        } else {
            toSQLCreateTable(ctx);
            toSQLOnCommit(ctx);
        }
        if (this.comment != null && !EMULATE_TABLE_COMMENT_IN_BLOCK.contains(ctx.dialect())) {
            ctx.formatSeparator().visit(Keywords.K_COMMENT).sql(' ');
            ctx.visit(this.comment);
        }
        if (this.storage != null && ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null) {
            ctx.formatSeparator().visit(this.storage);
        }
        ctx.end(Clause.CREATE_TABLE);
    }

    /* JADX WARN: Type inference failed for: r0v101, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v129, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v49, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v58, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v62, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v65, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v87, types: [org.jooq.Context] */
    private void toSQLCreateTable(Context<?> ctx) {
        toSQLCreateTableName(ctx);
        QOM.UnmodifiableList<? extends Field<?>> columns = $columns();
        if (!columns.isEmpty()) {
            if (this.select == null || !NO_SUPPORT_CTAS_COLUMN_NAMES.contains(ctx.dialect())) {
                ctx.sqlIndentStart(" (").start(Clause.CREATE_TABLE_COLUMNS);
                Field<?> identity = null;
                boolean qualify = ctx.qualify();
                boolean first = true;
                for (int i = 0; i < columns.size(); i++) {
                    Field<?> field = (Field) columns.get(i);
                    DataType<?> type = columnType(ctx, field);
                    if (!type.computedOnClientVirtual(ctx.configuration())) {
                        if (identity == null && type.identity()) {
                            identity = field;
                        }
                        if (!first) {
                            ctx.sql(',').formatSeparator();
                        }
                        ctx.qualify(false);
                        ctx.visit(Tools.uncollate(field));
                        ctx.qualify(qualify);
                        if (this.select == null) {
                            ctx.sql(' ');
                            Tools.toSQLDDLTypeDeclarationForAddition(ctx, type);
                            acceptColumnComment(ctx, field);
                        }
                        first = false;
                    }
                }
                toSQLDummyColumns(ctx);
                ctx.end(Clause.CREATE_TABLE_COLUMNS).start(Clause.CREATE_TABLE_CONSTRAINTS);
                for (Constraint constraint : $constraints()) {
                    if (((ConstraintImpl) constraint).supported(ctx) && (ctx.family() != SQLDialect.SQLITE || !matchingPrimaryKey(constraint, identity))) {
                        ctx.sql(',').formatSeparator().visit(constraint);
                    }
                }
                if (EMULATE_SOME_ENUM_TYPES_AS_CHECK.contains(ctx.dialect())) {
                    for (Field<?> field2 : $columns()) {
                        DataType<?> type2 = field2.getDataType();
                        if (EnumType.class.isAssignableFrom(type2.getType()) && (EMULATE_STORED_ENUM_TYPES_AS_CHECK.contains(ctx.dialect()) || !Tools.storedEnumType(type2))) {
                            List<Field<String>> literals = Tools.map(Tools.enums(type2.getType()), e -> {
                                return DSL.inline(e.getLiteral());
                            });
                            ctx.sql(',').formatSeparator().visit(DSL.constraint(this.table.getName() + "_" + field2.getName() + "_chk").check(field2.in(literals)));
                        }
                    }
                }
                ctx.end(Clause.CREATE_TABLE_CONSTRAINTS);
                if (!$indexes().isEmpty() && !EMULATE_INDEXES_IN_BLOCK.contains(ctx.dialect())) {
                    ctx.qualify(false);
                    for (Index index : $indexes()) {
                        ctx.sql(',').formatSeparator();
                        if (index.getUnique()) {
                            ctx.visit(Keywords.K_UNIQUE).sql(' ');
                        }
                        ctx.visit(Keywords.K_INDEX);
                        if (!"".equals(index.getName())) {
                            ctx.sql(' ').visit(index.getUnqualifiedName());
                        }
                        ctx.sql(" (").visit(new SortFieldList(index.getFields())).sql(')');
                    }
                    ctx.qualify(qualify);
                }
                ctx.sqlIndentEnd(')');
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    public static void acceptColumnComment(Context<?> ctx, Field<?> field) {
        if (!field.getComment().isEmpty() && !EMULATE_COLUMN_COMMENT_IN_BLOCK.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_COMMENT).sql(' ').visit((Field<?>) DSL.inline(field.getComment()));
        }
    }

    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    private final void toSQLDummyColumns(Context<?> ctx) {
        Field<?>[] primaryKeyColumns;
        if (REQUIRE_NON_PK_COLUMNS.contains(ctx.dialect()) && (primaryKeyColumns = primaryKeyColumns()) != null && primaryKeyColumns.length == $columns().size()) {
            ctx.sql(',').formatSeparator();
            ctx.visit(DSL.field(DSL.name("dummy")));
            if (this.select == null) {
                ctx.sql(' ');
                Tools.toSQLDDLTypeDeclarationForAddition(ctx, SQLDataType.INTEGER);
            }
        }
    }

    private final DataType<?> columnType(Context<?> ctx, Field<?> field) {
        DataType<?> type = field.getDataType();
        if (NO_SUPPORT_NULLABLE_PRIMARY_KEY.contains(ctx.dialect()) && type.nullability() == Nullability.DEFAULT && isPrimaryKey(field)) {
            type = type.nullable(false);
        }
        return type;
    }

    private final Field<?>[] primaryKeyColumns() {
        return (Field[]) Tools.findAny($constraints(), c -> {
            return (c instanceof ConstraintImpl) && ((ConstraintImpl) c).$primaryKey() != null;
        }, c2 -> {
            return ((ConstraintImpl) c2).$primaryKey();
        });
    }

    private final boolean isPrimaryKey(Field<?> field) {
        Field<?>[] primaryKeyColumns = primaryKeyColumns();
        Objects.requireNonNull(field);
        return Tools.anyMatch(primaryKeyColumns, (v1) -> {
            return r1.equals(v1);
        });
    }

    private final boolean matchingPrimaryKey(Constraint constraint, Field<?> identity) {
        if (constraint instanceof ConstraintImpl) {
            ConstraintImpl c = (ConstraintImpl) constraint;
            return c.matchingPrimaryKey(identity);
        }
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v32, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v37, types: [org.jooq.Context] */
    private final void acceptCreateTableAsSelect(Context<?> ctx) {
        toSQLCreateTable(ctx);
        toSQLOnCommit(ctx);
        ctx.formatSeparator().visit(Keywords.K_AS);
        if (WRAP_SELECT_IN_PARENS.contains(ctx.dialect())) {
            ctx.sqlIndentStart(" (");
        } else {
            ctx.formatSeparator();
        }
        if (QOM.WithOrWithoutData.WITH_NO_DATA == this.withData && NO_SUPPORT_WITH_DATA.contains(ctx.dialect())) {
            ctx.data(Tools.BooleanDataKey.DATA_SELECT_NO_DATA, true);
        }
        ctx.start(Clause.CREATE_TABLE_AS);
        if (!$columns().isEmpty() && NO_SUPPORT_CTAS_COLUMN_NAMES.contains(ctx.dialect())) {
            ctx.visit(DSL.select(DSL.asterisk()).from(this.select.asTable(DSL.table(DSL.name("t")), $columns())));
        } else {
            ctx.visit(this.select);
        }
        ctx.end(Clause.CREATE_TABLE_AS);
        if (QOM.WithOrWithoutData.WITH_NO_DATA == this.withData && NO_SUPPORT_WITH_DATA.contains(ctx.dialect())) {
            ctx.data().remove(Tools.BooleanDataKey.DATA_SELECT_NO_DATA);
        }
        if (WRAP_SELECT_IN_PARENS.contains(ctx.dialect())) {
            ctx.sqlIndentEnd(')');
        }
        if (QOM.WithOrWithoutData.WITH_NO_DATA == this.withData && !NO_SUPPORT_WITH_DATA.contains(ctx.dialect())) {
            ctx.formatSeparator().visit(Keywords.K_WITH_NO_DATA);
            return;
        }
        if (QOM.WithOrWithoutData.WITH_DATA == this.withData && !NO_SUPPORT_WITH_DATA.contains(ctx.dialect())) {
            ctx.formatSeparator().visit(Keywords.K_WITH_DATA);
        } else if (REQUIRES_WITH_DATA.contains(ctx.dialect())) {
            ctx.formatSeparator().visit(Keywords.K_WITH_DATA);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private final void toSQLCreateTableName(Context<?> ctx) {
        ctx.start(Clause.CREATE_TABLE_NAME).visit(Keywords.K_CREATE).sql(' ');
        if (this.temporary) {
            if (SUPPORT_TEMPORARY.contains(ctx.dialect())) {
                ctx.visit(Keywords.K_TEMPORARY).sql(' ');
            } else {
                ctx.visit(Keywords.K_GLOBAL_TEMPORARY).sql(' ');
            }
        }
        ctx.visit(Keywords.K_TABLE).sql(' ');
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.visit(Keywords.K_IF_NOT_EXISTS).sql(' ');
        }
        ctx.visit(this.table).end(Clause.CREATE_TABLE_NAME);
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    private final void toSQLOnCommit(Context<?> ctx) {
        if (this.temporary && this.onCommit != null) {
            switch (this.onCommit) {
                case DELETE_ROWS:
                    ctx.formatSeparator().visit(Keywords.K_ON_COMMIT_DELETE_ROWS);
                    return;
                case PRESERVE_ROWS:
                    ctx.formatSeparator().visit(Keywords.K_ON_COMMIT_PRESERVE_ROWS);
                    return;
                case DROP:
                    ctx.formatSeparator().visit(Keywords.K_ON_COMMIT_DROP);
                    return;
                default:
                    return;
            }
        }
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final Table<?> $table() {
        return this.table;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final boolean $temporary() {
        return this.temporary;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.UnmodifiableList<? extends TableElement> $tableElements() {
        return QOM.unmodifiable((List) this.tableElements);
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final Select<?> $select() {
        return this.select;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.WithOrWithoutData $withData() {
        return this.withData;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.TableCommitAction $onCommit() {
        return this.onCommit;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final Comment $comment() {
        return this.comment;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final SQL $storage() {
        return this.storage;
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $table(Table<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), $tableElements(), $select(), $withData(), $onCommit(), $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $temporary(boolean newValue) {
        return $constructor().apply($table(), Boolean.valueOf(newValue), Boolean.valueOf($ifNotExists()), $tableElements(), $select(), $withData(), $onCommit(), $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $ifNotExists(boolean newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf(newValue), $tableElements(), $select(), $withData(), $onCommit(), $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $tableElements(Collection<? extends TableElement> newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), newValue, $select(), $withData(), $onCommit(), $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $select(Select<?> newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), $tableElements(), newValue, $withData(), $onCommit(), $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $withData(QOM.WithOrWithoutData newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), $tableElements(), $select(), newValue, $onCommit(), $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $onCommit(QOM.TableCommitAction newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), $tableElements(), $select(), $withData(), newValue, $comment(), $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $comment(Comment newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), $tableElements(), $select(), $withData(), $onCommit(), newValue, $storage());
    }

    @Override // org.jooq.impl.QOM.CreateTable
    public final QOM.CreateTable $storage(SQL newValue) {
        return $constructor().apply($table(), Boolean.valueOf($temporary()), Boolean.valueOf($ifNotExists()), $tableElements(), $select(), $withData(), $onCommit(), $comment(), newValue);
    }

    public final Function9<? super Table<?>, ? super Boolean, ? super Boolean, ? super Collection<? extends TableElement>, ? super Select<?>, ? super QOM.WithOrWithoutData, ? super QOM.TableCommitAction, ? super Comment, ? super SQL, ? extends QOM.CreateTable> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7, a8, a9) -> {
            return new CreateTableImpl(configuration(), a1, a2.booleanValue(), a3.booleanValue(), a4, a5, a6, a7, a8, a9);
        };
    }
}
