package org.jooq.impl;

import java.util.Collection;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DeleteConditionStep;
import org.jooq.DeleteLimitStep;
import org.jooq.DeleteResultStep;
import org.jooq.DeleteReturningStep;
import org.jooq.DeleteUsingStep;
import org.jooq.DeleteWhereStep;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DeleteImpl.class */
public final class DeleteImpl<R extends Record> extends AbstractDelegatingDMLQuery<R, DeleteQueryImpl<R>> implements DeleteUsingStep<R>, DeleteConditionStep<R>, QOM.Delete<R> {
    private boolean returningResult;

    @Override // org.jooq.DeleteUsingStep
    public /* bridge */ /* synthetic */ DeleteWhereStep using(Collection collection) {
        return using((Collection<? extends TableLike<?>>) collection);
    }

    @Override // org.jooq.DeleteUsingStep
    public /* bridge */ /* synthetic */ DeleteWhereStep using(TableLike[] tableLikeArr) {
        return using((TableLike<?>[]) tableLikeArr);
    }

    @Override // org.jooq.DeleteUsingStep
    public /* bridge */ /* synthetic */ DeleteWhereStep using(TableLike tableLike) {
        return using((TableLike<?>) tableLike);
    }

    @Override // org.jooq.DeleteWhereStep
    public /* bridge */ /* synthetic */ DeleteConditionStep whereNotExists(Select select) {
        return whereNotExists((Select<?>) select);
    }

    @Override // org.jooq.DeleteWhereStep
    public /* bridge */ /* synthetic */ DeleteConditionStep whereExists(Select select) {
        return whereExists((Select<?>) select);
    }

    @Override // org.jooq.DeleteWhereStep
    public /* bridge */ /* synthetic */ DeleteConditionStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    @Override // org.jooq.DeleteWhereStep
    public /* bridge */ /* synthetic */ DeleteConditionStep where(Collection collection) {
        return where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.DeleteOrderByStep
    public /* bridge */ /* synthetic */ DeleteLimitStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.DeleteOrderByStep
    public /* bridge */ /* synthetic */ DeleteLimitStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.DeleteLimitStep
    public /* bridge */ /* synthetic */ DeleteReturningStep limit(Field field) {
        return limit((Field<? extends Number>) field);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.DeleteConditionStep
    public /* bridge */ /* synthetic */ DeleteConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DeleteImpl(Configuration configuration, WithImpl with, Table<R> table) {
        super(new DeleteQueryImpl(configuration, with, table));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(TableLike<?> table) {
        ((DeleteQueryImpl) getDelegate()).addUsing(table);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(TableLike<?>... tables) {
        ((DeleteQueryImpl) getDelegate()).addUsing(tables);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(Collection<? extends TableLike<?>> tables) {
        ((DeleteQueryImpl) getDelegate()).addUsing(tables);
        return this;
    }

    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(SQL sql) {
        return using((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(String sql) {
        return using((TableLike<?>) DSL.table(sql));
    }

    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(String sql, Object... bindings) {
        return using((TableLike<?>) DSL.table(sql, bindings));
    }

    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(String sql, QueryPart... parts) {
        return using((TableLike<?>) DSL.table(sql, parts));
    }

    @Override // org.jooq.DeleteUsingStep
    public final DeleteImpl<R> using(Name name) {
        return using((TableLike<?>) DSL.table(name));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(Condition conditions) {
        ((DeleteQueryImpl) getDelegate()).addConditions(conditions);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(Condition... conditions) {
        ((DeleteQueryImpl) getDelegate()).addConditions(conditions);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(Collection<? extends Condition> conditions) {
        ((DeleteQueryImpl) getDelegate()).addConditions(conditions);
        return this;
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(SQL sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(String sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(String sql, Object... bindings) {
        return where(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> where(String sql, QueryPart... parts) {
        return where(DSL.condition(sql, parts));
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> whereExists(Select<?> select) {
        return andExists(select);
    }

    @Override // org.jooq.DeleteWhereStep
    public final DeleteImpl<R> whereNotExists(Select<?> select) {
        return andNotExists(select);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> and(Condition condition) {
        ((DeleteQueryImpl) getDelegate()).addConditions(condition);
        return this;
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> and(Field<Boolean> condition) {
        return and(DSL.condition(condition));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> andNot(Condition condition) {
        return and(condition.not());
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> andNot(Field<Boolean> condition) {
        return andNot(DSL.condition(condition));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> or(Condition condition) {
        ((DeleteQueryImpl) getDelegate()).addConditions(Operator.OR, condition);
        return this;
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> or(Field<Boolean> condition) {
        return or(DSL.condition(condition));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> orNot(Condition condition) {
        return or(condition.not());
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> orNot(Field<Boolean> condition) {
        return orNot(DSL.condition(condition));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.DeleteConditionStep
    public final DeleteImpl<R> orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteOrderByStep
    public final DeleteImpl<R> orderBy(OrderField<?>... fields) {
        ((DeleteQueryImpl) getDelegate()).addOrderBy(fields);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteOrderByStep
    public final DeleteImpl<R> orderBy(Collection<? extends OrderField<?>> fields) {
        ((DeleteQueryImpl) getDelegate()).addOrderBy(fields);
        return this;
    }

    @Override // org.jooq.DeleteOrderByStep
    public final DeleteImpl<R> orderBy(int... fieldIndexes) {
        return orderBy((Collection<? extends OrderField<?>>) Tools.map(fieldIndexes, v -> {
            return DSL.inline(v);
        }));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteLimitStep
    public final DeleteImpl<R> limit(Number numberOfRows) {
        ((DeleteQueryImpl) getDelegate()).addLimit(numberOfRows);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteLimitStep
    public final DeleteImpl<R> limit(Field<? extends Number> numberOfRows) {
        ((DeleteQueryImpl) getDelegate()).addLimit(numberOfRows);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteReturningStep
    public final DeleteResultStep<R> returning() {
        ((DeleteQueryImpl) getDelegate()).setReturning();
        return new DeleteAsResultQuery((DeleteQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteReturningStep
    public final DeleteResultStep<R> returning(SelectFieldOrAsterisk... f) {
        ((DeleteQueryImpl) getDelegate()).setReturning(f);
        return new DeleteAsResultQuery((DeleteQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteReturningStep
    public final DeleteResultStep<R> returning(Collection<? extends SelectFieldOrAsterisk> f) {
        ((DeleteQueryImpl) getDelegate()).setReturning(f);
        return new DeleteAsResultQuery((DeleteQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteReturningStep
    public final DeleteResultStep<Record> returningResult(SelectFieldOrAsterisk... f) {
        this.returningResult = true;
        ((DeleteQueryImpl) getDelegate()).setReturning(f);
        return new DeleteAsResultQuery((DeleteQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.DeleteReturningStep
    public final DeleteResultStep<Record> returningResult(Collection<? extends SelectFieldOrAsterisk> f) {
        this.returningResult = true;
        ((DeleteQueryImpl) getDelegate()).setReturning(f);
        return new DeleteAsResultQuery((DeleteQueryImpl) getDelegate(), this.returningResult);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1> DeleteResultStep<Record1<T1>> returningResult(SelectField<T1> selectField) {
        return (DeleteResultStep<Record1<T1>>) returningResult(selectField);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2> DeleteResultStep<Record2<T1, T2>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (DeleteResultStep<Record2<T1, T2>>) returningResult(selectField, selectField2);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3> DeleteResultStep<Record3<T1, T2, T3>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (DeleteResultStep<Record3<T1, T2, T3>>) returningResult(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4> DeleteResultStep<Record4<T1, T2, T3, T4>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (DeleteResultStep<Record4<T1, T2, T3, T4>>) returningResult(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5> DeleteResultStep<Record5<T1, T2, T3, T4, T5>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (DeleteResultStep<Record5<T1, T2, T3, T4, T5>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6> DeleteResultStep<Record6<T1, T2, T3, T4, T5, T6>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (DeleteResultStep<Record6<T1, T2, T3, T4, T5, T6>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7> DeleteResultStep<Record7<T1, T2, T3, T4, T5, T6, T7>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (DeleteResultStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> DeleteResultStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (DeleteResultStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> DeleteResultStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (DeleteResultStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> DeleteResultStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (DeleteResultStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> DeleteResultStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (DeleteResultStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> DeleteResultStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (DeleteResultStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> DeleteResultStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (DeleteResultStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> DeleteResultStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (DeleteResultStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> DeleteResultStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (DeleteResultStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> DeleteResultStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (DeleteResultStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> DeleteResultStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (DeleteResultStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> DeleteResultStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (DeleteResultStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> DeleteResultStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (DeleteResultStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> DeleteResultStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (DeleteResultStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> DeleteResultStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (DeleteResultStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.DeleteReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> DeleteResultStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (DeleteResultStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.With $with() {
        return ((DeleteQueryImpl) getDelegate()).$with();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final Table<R> $from() {
        return ((DeleteQueryImpl) getDelegate()).$from();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<?> $from(Table<?> from) {
        return ((DeleteQueryImpl) getDelegate()).$from(from);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.UnmodifiableList<? extends Table<?>> $using() {
        return ((DeleteQueryImpl) getDelegate()).$using();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $using(Collection<? extends Table<?>> using) {
        return ((DeleteQueryImpl) getDelegate()).$using(using);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final Condition $where() {
        return ((DeleteQueryImpl) getDelegate()).$where();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $where(Condition condition) {
        return ((DeleteQueryImpl) getDelegate()).$where(condition);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return ((DeleteQueryImpl) getDelegate()).$orderBy();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $orderBy(Collection<? extends SortField<?>> orderBy) {
        return ((DeleteQueryImpl) getDelegate()).$orderBy(orderBy);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final Field<? extends Number> $limit() {
        return ((DeleteQueryImpl) getDelegate()).$limit();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Delete
    public final QOM.Delete<R> $limit(Field<? extends Number> limit) {
        return ((DeleteQueryImpl) getDelegate()).$limit(limit);
    }
}
