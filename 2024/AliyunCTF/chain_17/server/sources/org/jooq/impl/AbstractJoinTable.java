package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableOnConditionStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.TableOptions;
import org.jooq.TableOuterJoinStep;
import org.jooq.TablePartitionByStep;
import org.jooq.impl.AbstractJoinTable;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractJoinTable.class */
public abstract class AbstractJoinTable<J extends AbstractJoinTable<J>> extends AbstractTable<Record> implements TableOuterJoinStep<Record>, TableOptionalOnStep<Record>, TablePartitionByStep<Record>, TableOnConditionStep<Record> {
    abstract J partitionBy0(Collection<? extends Field<?>> collection);

    @Override // org.jooq.TableOnStep
    public abstract J on(Condition condition);

    @Override // org.jooq.TableOnStep
    public abstract J on(Condition... conditionArr);

    @Override // org.jooq.TableOnStep
    public abstract J using(Collection<? extends Field<?>> collection);

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: and */
    public abstract TableOnConditionStep<Record> and2(Condition condition);

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: or */
    public abstract TableOnConditionStep<Record> or2(Condition condition);

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ TableOptionalOnStep join(TableLike tableLike, JoinType joinType, QOM.JoinHint joinHint) {
        return join((TableLike<?>) tableLike, joinType, joinHint);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ TableOptionalOnStep join(TableLike tableLike, JoinType joinType) {
        return join((TableLike<?>) tableLike, joinType);
    }

    @Override // org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ Table using(Collection collection) {
        return using((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ Table using(Field[] fieldArr) {
        return using((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.TableOnStep
    public /* bridge */ /* synthetic */ TableOnConditionStep on(Field field) {
        return on((Field<Boolean>) field);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: orNotExists, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> orNotExists2(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: orExists, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> orExists2(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: orNot, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> orNot2(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: or, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> or2(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: andNotExists, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> andNotExists2(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: andExists, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> andExists2(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: andNot, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> andNot2(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: and, reason: avoid collision after fix types in other method */
    public /* bridge */ /* synthetic */ TableOnConditionStep<Record> and2(Field field) {
        return and((Field<Boolean>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractJoinTable(TableOptions options, Name name) {
        super(options, name);
    }

    @Override // org.jooq.TableOnStep
    public final J on(Field<Boolean> c) {
        return on(DSL.condition(c));
    }

    @Override // org.jooq.TableOnStep
    public final J on(SQL sql) {
        and2(sql);
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final J on(String sql) {
        and2(sql);
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final J on(String sql, Object... bindings) {
        and2(sql, bindings);
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final J on(String sql, QueryPart... parts) {
        and2(sql, parts);
        return this;
    }

    @Override // org.jooq.TableOnStep
    public final J using(Field<?>... fields) {
        return using((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> and(Field<Boolean> c) {
        return and2(DSL.condition(c));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: and, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> and2(SQL sql) {
        return and2(DSL.condition(sql));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: and, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> and2(String sql) {
        return and2(DSL.condition(sql));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: and, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> and2(String sql, Object... bindings) {
        return and2(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: and, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> and2(String sql, QueryPart... parts) {
        return and2(DSL.condition(sql, parts));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: andNot, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> andNot2(Condition c) {
        return and2(c.not());
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> andNot(Field<Boolean> c) {
        return andNot2(DSL.condition(c));
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> andExists(Select<?> select) {
        return and2(DSL.exists(select));
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> andNotExists(Select<?> select) {
        return and2(DSL.notExists(select));
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> or(Field<Boolean> c) {
        return or2(DSL.condition(c));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: or, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> or2(SQL sql) {
        return or2(DSL.condition(sql));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: or, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> or2(String sql) {
        return or2(DSL.condition(sql));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: or, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> or2(String sql, Object... bindings) {
        return or2(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: or, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> or2(String sql, QueryPart... parts) {
        return or2(DSL.condition(sql, parts));
    }

    @Override // org.jooq.TableOnConditionStep
    /* renamed from: orNot, reason: merged with bridge method [inline-methods] */
    public final TableOnConditionStep<Record> orNot2(Condition c) {
        return or2(c.not());
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> orNot(Field<Boolean> c) {
        return orNot2(DSL.condition(c));
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> orExists(Select<?> select) {
        return or2(DSL.exists(select));
    }

    @Override // org.jooq.TableOnConditionStep
    public final TableOnConditionStep<Record> orNotExists(Select<?> select) {
        return or2(DSL.notExists(select));
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final J join(TableLike<?> table, JoinType type) {
        return (J) super.join(table, type);
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table
    public final J join(TableLike<?> table, JoinType type, QOM.JoinHint hint) {
        return (J) super.join(table, type, hint);
    }
}
