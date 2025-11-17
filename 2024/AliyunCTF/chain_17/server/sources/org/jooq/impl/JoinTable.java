package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JoinType;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnConditionStep;
import org.jooq.TableOptions;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.JoinTable;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JoinTable.class */
public abstract class JoinTable<J extends JoinTable<J>> extends AbstractJoinTable<J> {
    static final Clause[] CLAUSES = {Clause.TABLE, Clause.TABLE_JOIN};
    static final Set<SQLDialect> EMULATE_NATURAL_JOIN = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.TRINO);
    static final Set<SQLDialect> EMULATE_NATURAL_OUTER_JOIN = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.H2, SQLDialect.IGNITE, SQLDialect.TRINO);
    static final Set<SQLDialect> EMULATE_JOIN_USING = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.IGNITE);
    static final Set<SQLDialect> EMULATE_APPLY = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    final Table<?> lhs;
    final Table<?> rhs;
    final QueryPartList<Field<?>> lhsPartitionBy;
    final QueryPartList<Field<?>> rhsPartitionBy;
    final JoinType type;
    final QOM.JoinHint hint;
    final ConditionProviderImpl condition;
    final QueryPartList<Field<?>> using;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JoinTable$OnKeyExceptionReason.class */
    public enum OnKeyExceptionReason {
        AMBIGUOUS,
        NOT_FOUND
    }

    abstract J construct(Table<?> table, Collection<? extends Field<?>> collection, Collection<? extends Field<?>> collection2, Table<?> table2, Condition condition, Collection<? extends Field<?>> collection3, QOM.JoinHint joinHint);

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ AbstractJoinTable using(Collection collection) {
        return using((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.impl.AbstractJoinTable
    /* bridge */ /* synthetic */ AbstractJoinTable partitionBy0(Collection collection) {
        return partitionBy0((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ TableOnConditionStep onKey(ForeignKey foreignKey) {
        return onKey((ForeignKey<?, ?>) foreignKey);
    }

    @Override // org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ TableOnConditionStep onKey(TableField[] tableFieldArr) throws DataAccessException {
        return onKey((TableField<?, ?>[]) tableFieldArr);
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ Table using(Collection collection) {
        return using((Collection<? extends Field<?>>) collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JoinTable(TableLike<?> lhs, TableLike<?> rhs, JoinType type, QOM.JoinHint hint) {
        this(lhs, rhs, type, hint, Collections.emptyList());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JoinTable(TableLike<?> lhs, TableLike<?> rhs, JoinType type, QOM.JoinHint hint, Collection<? extends Field<?>> lhsPartitionBy) {
        super(TableOptions.expression(), Names.N_JOIN);
        this.lhs = lhs.asTable();
        this.rhs = rhs.asTable();
        this.lhsPartitionBy = new QueryPartList<>(lhsPartitionBy);
        this.rhsPartitionBy = new QueryPartList<>();
        this.type = type;
        this.hint = hint;
        this.condition = new ConditionProviderImpl();
        this.using = new QueryPartList<>();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public final J transform(Table<?> newLhs, Table<?> newRhs) {
        return transform(newLhs, newRhs, this.condition);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public final J transform(Table<?> newLhs, Table<?> newRhs, ConditionProviderImpl newCondition) {
        if (this.lhs == newLhs && this.rhs == newRhs && this.condition == newCondition) {
            return this;
        }
        return construct(newLhs, this.lhsPartitionBy, this.rhsPartitionBy, newRhs, newCondition, this.using, this.hint);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final List<ForeignKey<Record, ?>> getReferences() {
        List<ForeignKey<?, ?>> references = this.lhs.getReferences();
        List<ForeignKey<?, ?>> references2 = this.rhs.getReferences();
        ArrayList arrayList = new ArrayList(references.size() + references2.size());
        arrayList.addAll(references);
        arrayList.addAll(references2);
        return arrayList;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        boolean lpath = TableImpl.path(this.lhs) != null;
        boolean rpath = TableImpl.path(this.rhs) != null;
        boolean path = lpath || rpath;
        if (((this instanceof CrossApply) || (this instanceof OuterApply)) && rpath) {
            ctx.visit($table2(DSL.selectFrom(this.rhs).asTable(this.rhs)));
            return;
        }
        if ((this.rhs instanceof QOM.Lateral) && TableImpl.path(((QOM.Lateral) this.rhs).$arg1()) != null) {
            ctx.visit($table2(DSL.lateral(DSL.selectFrom(((QOM.Lateral) this.rhs).$arg1()).asTable((Table<?>) ((QOM.Lateral) this.rhs).$arg1()))));
            return;
        }
        if (this.type == JoinType.NATURAL_JOIN && path) {
            ctx.visit(this.lhs.join(this.rhs, JoinType.JOIN, this.hint).on(naturalCondition()));
            return;
        }
        if (this.type == JoinType.NATURAL_LEFT_OUTER_JOIN && path) {
            ctx.visit(this.lhs.join(this.rhs, JoinType.LEFT_OUTER_JOIN, this.hint).on(naturalCondition()));
            return;
        }
        if (this.type == JoinType.NATURAL_RIGHT_OUTER_JOIN && path) {
            ctx.visit(this.lhs.join(this.rhs, JoinType.RIGHT_OUTER_JOIN, this.hint).on(naturalCondition()));
            return;
        }
        if (this.type == JoinType.NATURAL_FULL_OUTER_JOIN && path) {
            ctx.visit(this.lhs.join(this.rhs, JoinType.FULL_OUTER_JOIN, this.hint).on(naturalCondition()));
            return;
        }
        if (!this.using.isEmpty() && path) {
            ctx.visit(this.lhs.join(this.rhs, this.type, this.hint).on(usingCondition()));
            return;
        }
        if ((this instanceof CrossApply) && EMULATE_APPLY.contains(ctx.dialect())) {
            ctx.visit(this.lhs.crossJoin(DSL.lateral(this.rhs)));
        } else if ((this instanceof OuterApply) && EMULATE_APPLY.contains(ctx.dialect())) {
            ctx.visit(this.lhs.leftJoin(DSL.lateral(this.rhs)).on(DSL.noCondition()));
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        JoinType translatedType = translateType(ctx);
        Clause translatedClause = translateClause(translatedType);
        Keyword keyword = translateKeyword(ctx, translatedType);
        toSQLTable(ctx, this.lhs);
        switch (translatedType) {
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_COLLECT_SEMI_ANTI_JOIN))) {
                    List<Condition> semiAntiJoinPredicates = (List) ctx.data(Tools.SimpleDataKey.DATA_COLLECTED_SEMI_ANTI_JOIN);
                    if (semiAntiJoinPredicates == null) {
                        semiAntiJoinPredicates = new ArrayList<>();
                        ctx.data(Tools.SimpleDataKey.DATA_COLLECTED_SEMI_ANTI_JOIN, semiAntiJoinPredicates);
                    }
                    Condition c = ConditionProviderImpl.extractCondition(!this.using.isEmpty() ? usingCondition() : this.condition);
                    switch (translatedType) {
                        case LEFT_SEMI_JOIN:
                            semiAntiJoinPredicates.add(DSL.exists(DSL.selectOne().from(this.rhs).where(c)));
                            return;
                        case LEFT_ANTI_JOIN:
                            semiAntiJoinPredicates.add(DSL.notExists(DSL.selectOne().from(this.rhs).where(c)));
                            return;
                        default:
                            return;
                    }
                }
                break;
        }
        ctx.formatIndentStart().formatSeparator().start(translatedClause).visit(keyword).sql(' ');
        toSQLTable(ctx, this.rhs);
        if (translatedType.qualified()) {
            ctx.formatIndentStart();
            toSQLJoinCondition(ctx);
            ctx.formatIndentEnd();
        }
        ctx.end(translatedClause).formatIndentEnd();
    }

    private final Keyword translateKeyword(Context<?> ctx, JoinType translatedType) {
        Keyword keyword;
        switch (translatedType) {
            case JOIN:
            case NATURAL_JOIN:
                if (ctx.settings().getRenderOptionalInnerKeyword() == RenderOptionalKeyword.ON) {
                    keyword = translatedType.toKeyword(true);
                    break;
                } else {
                    keyword = translatedType.toKeyword();
                    break;
                }
            case LEFT_OUTER_JOIN:
            case NATURAL_LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
            case NATURAL_RIGHT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case NATURAL_FULL_OUTER_JOIN:
                if (ctx.settings().getRenderOptionalOuterKeyword() == RenderOptionalKeyword.OFF) {
                    keyword = translatedType.toKeyword(false);
                    break;
                } else {
                    keyword = translatedType.toKeyword();
                    break;
                }
            default:
                keyword = translatedType.toKeyword();
                break;
        }
        return keyword;
    }

    private void toSQLTable(Context<?> ctx, Table<?> table) {
        boolean wrap = (table instanceof JoinTable) && table == this.rhs;
        if (wrap) {
            ctx.sqlIndentStart('(');
        }
        Tools.visitAutoAliased(ctx, table, (v0) -> {
            return v0.declareTables();
        }, (c, t) -> {
            c.visit(t);
        });
        if (wrap) {
            ctx.sqlIndentEnd(')');
        }
    }

    final Clause translateClause(JoinType translatedType) {
        switch (translatedType) {
            case LEFT_SEMI_JOIN:
                return Clause.TABLE_JOIN_SEMI_LEFT;
            case LEFT_ANTI_JOIN:
                return Clause.TABLE_JOIN_ANTI_LEFT;
            case JOIN:
                return Clause.TABLE_JOIN_INNER;
            case NATURAL_JOIN:
                return Clause.TABLE_JOIN_NATURAL;
            case LEFT_OUTER_JOIN:
                return Clause.TABLE_JOIN_OUTER_LEFT;
            case NATURAL_LEFT_OUTER_JOIN:
                return Clause.TABLE_JOIN_NATURAL_OUTER_LEFT;
            case RIGHT_OUTER_JOIN:
                return Clause.TABLE_JOIN_OUTER_RIGHT;
            case NATURAL_RIGHT_OUTER_JOIN:
                return Clause.TABLE_JOIN_NATURAL_OUTER_RIGHT;
            case FULL_OUTER_JOIN:
                return Clause.TABLE_JOIN_OUTER_FULL;
            case NATURAL_FULL_OUTER_JOIN:
                return Clause.TABLE_JOIN_NATURAL_OUTER_FULL;
            case CROSS_JOIN:
                return Clause.TABLE_JOIN_CROSS;
            case CROSS_APPLY:
                return Clause.TABLE_JOIN_CROSS_APPLY;
            case OUTER_APPLY:
                return Clause.TABLE_JOIN_OUTER_APPLY;
            case STRAIGHT_JOIN:
                return Clause.TABLE_JOIN_STRAIGHT;
            default:
                throw new IllegalArgumentException("Bad join type: " + String.valueOf(translatedType));
        }
    }

    final JoinType translateType(Context<?> ctx) {
        if (emulateCrossJoin(ctx)) {
            return JoinType.JOIN;
        }
        if (emulateNaturalJoin(ctx)) {
            return JoinType.JOIN;
        }
        if (emulateNaturalLeftOuterJoin(ctx)) {
            return JoinType.LEFT_OUTER_JOIN;
        }
        if (emulateNaturalRightOuterJoin(ctx)) {
            return JoinType.RIGHT_OUTER_JOIN;
        }
        if (emulateNaturalFullOuterJoin(ctx)) {
            return JoinType.FULL_OUTER_JOIN;
        }
        return this.type;
    }

    private final boolean emulateCrossJoin(Context<?> ctx) {
        return false;
    }

    private final boolean emulateNaturalJoin(Context<?> ctx) {
        return this.type == JoinType.NATURAL_JOIN && EMULATE_NATURAL_JOIN.contains(ctx.dialect());
    }

    private final boolean emulateNaturalLeftOuterJoin(Context<?> ctx) {
        return this.type == JoinType.NATURAL_LEFT_OUTER_JOIN && EMULATE_NATURAL_OUTER_JOIN.contains(ctx.dialect());
    }

    private final boolean emulateNaturalRightOuterJoin(Context<?> ctx) {
        return this.type == JoinType.NATURAL_RIGHT_OUTER_JOIN && EMULATE_NATURAL_OUTER_JOIN.contains(ctx.dialect());
    }

    private final boolean emulateNaturalFullOuterJoin(Context<?> ctx) {
        return this.type == JoinType.NATURAL_FULL_OUTER_JOIN && EMULATE_NATURAL_OUTER_JOIN.contains(ctx.dialect());
    }

    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    private final void toSQLJoinCondition(Context<?> ctx) {
        Condition noCondition;
        Condition noCondition2;
        if (!this.using.isEmpty()) {
            if (EMULATE_JOIN_USING.contains(ctx.dialect())) {
                toSQLJoinCondition(ctx, usingCondition());
                return;
            } else {
                ctx.formatSeparator().start(Clause.TABLE_JOIN_USING).visit(Keywords.K_USING).sql(" (").visit(QueryPartListView.wrap((List) this.using).qualify(false)).sql(')').end(Clause.TABLE_JOIN_USING);
                return;
            }
        }
        if (emulateNaturalJoin(ctx) || emulateNaturalLeftOuterJoin(ctx) || emulateNaturalRightOuterJoin(ctx) || emulateNaturalFullOuterJoin(ctx)) {
            toSQLJoinCondition(ctx, naturalCondition());
            return;
        }
        if ((TableImpl.path(this.lhs) != null || TableImpl.path(this.rhs) != null) && ctx.data(Tools.BooleanDataKey.DATA_RENDER_IMPLICIT_JOIN) == null) {
            Condition[] conditionArr = new Condition[3];
            Table<?> table = this.lhs;
            if (table instanceof TableImpl) {
                TableImpl<?> ti = (TableImpl) table;
                noCondition = ti.pathCondition();
            } else {
                noCondition = DSL.noCondition();
            }
            conditionArr[0] = noCondition;
            Table<?> table2 = this.rhs;
            if (table2 instanceof TableImpl) {
                TableImpl<?> ti2 = (TableImpl) table2;
                noCondition2 = ti2.pathCondition();
            } else {
                noCondition2 = DSL.noCondition();
            }
            conditionArr[1] = noCondition2;
            conditionArr[2] = this.condition.getWhere();
            toSQLJoinCondition(ctx, DSL.and(conditionArr));
            return;
        }
        toSQLJoinCondition(ctx, this.condition);
    }

    final Condition naturalCondition() {
        List<Condition> conditions = new ArrayList<>(this.using.size());
        for (Field<?> field : this.lhs.fields()) {
            Field<?> other = this.rhs.field(field);
            if (other != null) {
                conditions.add(field.eq(other));
            }
        }
        return DSL.and(conditions);
    }

    final Condition usingCondition() {
        return DSL.and(Tools.map(this.using, f -> {
            return Tools.qualify(this.lhs, f).eq(Tools.qualify(this.rhs, f));
        }));
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void toSQLJoinCondition(Context<?> context, Condition c) {
        context.formatSeparator().start(Clause.TABLE_JOIN_ON).visit(Keywords.K_ON).sql(' ').visit(c).end(Clause.TABLE_JOIN_ON);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public final Table<Record> as(Name alias) {
        return new TableAlias(this, alias, (Predicate<Context<?>>) c -> {
            return true;
        });
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final Table<Record> as(Name alias, Name... fieldAliases) {
        return new TableAlias(this, alias, fieldAliases, c -> {
            return true;
        });
    }

    @Override // org.jooq.RecordQualifier
    public final Class<? extends Record> getRecordType() {
        if (this.type == JoinType.LEFT_SEMI_JOIN || this.type == JoinType.LEFT_ANTI_JOIN) {
            return this.lhs.getRecordType();
        }
        return RecordImplN.class;
    }

    @Override // org.jooq.impl.AbstractTable
    final FieldsImpl<Record> fields0() {
        if (this.type == JoinType.LEFT_SEMI_JOIN || this.type == JoinType.LEFT_ANTI_JOIN) {
            return new FieldsImpl<>(this.lhs.asTable().fields());
        }
        Field<?>[] l = this.lhs.asTable().fields();
        Field<?>[] r = this.rhs.asTable().fields();
        Field<?>[] all = new Field[l.length + r.length];
        System.arraycopy(l, 0, all, 0, l.length);
        System.arraycopy(r, 0, all, l.length, r.length);
        return new FieldsImpl<>(all);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresTables() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractJoinTable
    public final J partitionBy0(Collection<? extends Field<?>> fields) {
        this.rhsPartitionBy.addAll(fields);
        return this;
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public final J on(Condition conditions) {
        this.condition.addConditions(conditions);
        return this;
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public final J on(Condition... conditions) {
        this.condition.addConditions(conditions);
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final J onKey() throws DataAccessException {
        List<?> leftToRight = this.lhs.getReferencesTo(this.rhs);
        List<?> rightToLeft = this.rhs.getReferencesTo(this.lhs);
        if (leftToRight.size() == 1 && rightToLeft.size() == 0) {
            return onKey((ForeignKey) leftToRight.get(0), this.lhs, this.rhs);
        }
        if (rightToLeft.size() == 1 && leftToRight.size() == 0) {
            return onKey((ForeignKey) rightToLeft.get(0), this.rhs, this.lhs);
        }
        if (rightToLeft.isEmpty() && leftToRight.isEmpty()) {
            throw onKeyException(OnKeyExceptionReason.NOT_FOUND, null, null);
        }
        throw onKeyException(OnKeyExceptionReason.AMBIGUOUS, leftToRight, rightToLeft);
    }

    @Override // org.jooq.TableOnStep
    public final J onKey(TableField<?, ?>... keyFields) throws DataAccessException {
        if (keyFields != null && keyFields.length > 0) {
            List<TableField<?, ?>> unaliased = new ArrayList<>(Arrays.asList(keyFields));
            for (int i = 0; i < unaliased.size(); i++) {
                TableField<?, ?> f = unaliased.get(i);
                Alias<? extends Table<?>> alias = Tools.alias(f.getTable());
                if (alias != null) {
                    unaliased.set(i, (TableField) ((Table) alias.wrapped()).field(f));
                }
            }
            for (boolean unalias : new boolean[]{false, true}) {
                if (Tools.containsTable(this.lhs, keyFields[0].getTable(), unalias)) {
                    for (ForeignKey<?, ?> key : this.lhs.getReferences()) {
                        if (key.getFields().containsAll(unaliased) && unaliased.containsAll(key.getFields())) {
                            return onKey(key, this.lhs, this.rhs);
                        }
                    }
                    for (ForeignKey<?, ?> key2 : this.lhs.getReferences()) {
                        if (key2.getFields().containsAll(unaliased)) {
                            return onKey(key2, this.lhs, this.rhs);
                        }
                    }
                } else if (Tools.containsTable(this.rhs, keyFields[0].getTable(), unalias)) {
                    for (ForeignKey<?, ?> key3 : this.rhs.getReferences()) {
                        if (key3.getFields().containsAll(unaliased) && unaliased.containsAll(key3.getFields())) {
                            return onKey(key3, this.rhs, this.lhs);
                        }
                    }
                    for (ForeignKey<?, ?> key4 : this.rhs.getReferences()) {
                        if (key4.getFields().containsAll(unaliased)) {
                            return onKey(key4, this.rhs, this.lhs);
                        }
                    }
                } else {
                    continue;
                }
            }
        }
        throw onKeyException(OnKeyExceptionReason.NOT_FOUND, null, null);
    }

    @Override // org.jooq.TableOnStep
    public final J onKey(ForeignKey<?, ?> key) {
        if (Tools.containsUnaliasedTable(this.lhs, (Table<?>) key.getTable()) && Tools.containsUnaliasedTable(this.rhs, key.getKey().getTable())) {
            return onKey(key, this.lhs, this.rhs);
        }
        if (Tools.containsUnaliasedTable(this.rhs, (Table<?>) key.getTable()) && Tools.containsUnaliasedTable(this.lhs, key.getKey().getTable())) {
            return onKey(key, this.rhs, this.lhs);
        }
        throw onKeyException(OnKeyExceptionReason.NOT_FOUND, null, null);
    }

    private final J onKey(ForeignKey<?, ?> foreignKey, Table<?> table, Table<?> table2) {
        return (J) and2(onKey0(foreignKey, table, table2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final Condition onKey0(ForeignKey<?, ?> key, Table<?> fk, Table<?> pk) {
        Condition result = DSL.noCondition();
        TableField<CHILD, ?>[] fieldsArray = key.getFieldsArray();
        TableField<?, ?>[] referenced = key.getKeyFieldsArray();
        for (int i = 0; i < fieldsArray.length; i++) {
            Field f1 = fk.field(fieldsArray[i]);
            Field f2 = pk.field(referenced[i]);
            result = result.and(f1.equal(f2));
        }
        return result;
    }

    private final DataAccessException onKeyException(OnKeyExceptionReason reason, List<?> leftToRight, List<?> rightToLeft) {
        switch (reason) {
            case AMBIGUOUS:
                return new DataAccessException("Key ambiguous between tables [" + String.valueOf(this.lhs) + "] and [" + String.valueOf(this.rhs) + "]. Found: " + String.valueOf(leftToRight) + " and " + String.valueOf(rightToLeft));
            case NOT_FOUND:
            default:
                return new DataAccessException("No matching Key found between tables [" + String.valueOf(this.lhs) + "] and [" + String.valueOf(this.rhs) + "]");
        }
    }

    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnStep
    public final J using(Collection<? extends Field<?>> fields) {
        this.using.addAll(fields);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnConditionStep
    /* renamed from: and, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> and2(Condition c) {
        this.condition.addConditions(c);
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.AbstractJoinTable, org.jooq.TableOnConditionStep
    /* renamed from: or, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> or2(Condition c) {
        this.condition.addConditions(Operator.OR, c);
        return this;
    }

    public final Table<?> $table1() {
        return this.lhs;
    }

    public final J $table1(Table<?> t1) {
        return construct(t1, $partitionBy1(), $partitionBy2(), $table2(), $on(), $using(), $hint());
    }

    public final QOM.UnmodifiableList<Field<?>> $partitionBy1() {
        return QOM.unmodifiable((List) this.lhsPartitionBy);
    }

    public final J $partitionBy1(Collection<? extends Field<?>> p1) {
        return construct($table1(), p1, $partitionBy2(), $table2(), $on(), $using(), $hint());
    }

    public final QOM.UnmodifiableList<Field<?>> $partitionBy2() {
        return QOM.unmodifiable((List) this.rhsPartitionBy);
    }

    public final J $partitionBy2(Collection<? extends Field<?>> p2) {
        return construct($table1(), $partitionBy1(), p2, $table2(), $on(), $using(), $hint());
    }

    public final Table<?> $table2() {
        return this.rhs;
    }

    public final J $table2(Table<?> t2) {
        return construct($table1(), $partitionBy1(), $partitionBy2(), t2, $on(), $using(), $hint());
    }

    public final QOM.JoinHint $hint() {
        return this.hint;
    }

    public final J $hint(QOM.JoinHint newHint) {
        return construct($table1(), $partitionBy1(), $partitionBy2(), $table2(), $on(), $using(), newHint);
    }

    public final Condition $on() {
        return this.condition.getWhereOrNull();
    }

    public final J $on(Condition o) {
        return construct($table1(), $partitionBy1(), $partitionBy2(), $table2(), o, Collections.emptyList(), $hint());
    }

    public final QOM.UnmodifiableList<Field<?>> $using() {
        return QOM.unmodifiable((List) this.using);
    }

    public final J $using(Collection<? extends Field<?>> u) {
        return construct($table1(), $partitionBy1(), $partitionBy2(), $table2(), null, u, $hint());
    }
}
