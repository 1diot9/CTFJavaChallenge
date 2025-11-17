package org.jooq.impl;

import java.util.Collection;
import java.util.Map;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
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
import org.jooq.Row1;
import org.jooq.Row10;
import org.jooq.Row11;
import org.jooq.Row12;
import org.jooq.Row13;
import org.jooq.Row14;
import org.jooq.Row15;
import org.jooq.Row16;
import org.jooq.Row17;
import org.jooq.Row18;
import org.jooq.Row19;
import org.jooq.Row2;
import org.jooq.Row20;
import org.jooq.Row21;
import org.jooq.Row22;
import org.jooq.Row3;
import org.jooq.Row4;
import org.jooq.Row5;
import org.jooq.Row6;
import org.jooq.Row7;
import org.jooq.Row8;
import org.jooq.Row9;
import org.jooq.RowN;
import org.jooq.SQL;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.UpdateConditionStep;
import org.jooq.UpdateFromStep;
import org.jooq.UpdateLimitStep;
import org.jooq.UpdateResultStep;
import org.jooq.UpdateReturningStep;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.UpdateWhereStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UpdateImpl.class */
public final class UpdateImpl<R extends Record> extends AbstractDelegatingDMLQuery<R, UpdateQueryImpl<R>> implements UpdateSetFirstStep<R>, UpdateSetMoreStep<R>, UpdateConditionStep<R>, QOM.Update<R> {
    private boolean returningResult;

    @Override // org.jooq.UpdateSetStep
    public /* bridge */ /* synthetic */ UpdateSetMoreStep set(Map map) {
        return set((Map<?, ?>) map);
    }

    @Override // org.jooq.UpdateSetStep
    public /* bridge */ /* synthetic */ UpdateSetMoreStep set(Field field, Object obj) {
        return set((Field<Field>) field, (Field) obj);
    }

    @Override // org.jooq.UpdateWhereStep
    public /* bridge */ /* synthetic */ UpdateConditionStep whereNotExists(Select select) {
        return whereNotExists((Select<?>) select);
    }

    @Override // org.jooq.UpdateWhereStep
    public /* bridge */ /* synthetic */ UpdateConditionStep whereExists(Select select) {
        return whereExists((Select<?>) select);
    }

    @Override // org.jooq.UpdateWhereStep
    public /* bridge */ /* synthetic */ UpdateConditionStep where(Field field) {
        return where((Field<Boolean>) field);
    }

    @Override // org.jooq.UpdateWhereStep
    public /* bridge */ /* synthetic */ UpdateConditionStep where(Collection collection) {
        return where((Collection<? extends Condition>) collection);
    }

    @Override // org.jooq.UpdateOrderByStep
    public /* bridge */ /* synthetic */ UpdateLimitStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.UpdateOrderByStep
    public /* bridge */ /* synthetic */ UpdateLimitStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    @Override // org.jooq.UpdateLimitStep
    public /* bridge */ /* synthetic */ UpdateReturningStep limit(Field field) {
        return limit((Field<? extends Number>) field);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep orNotExists(Select select) {
        return orNotExists((Select<?>) select);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep orExists(Select select) {
        return orExists((Select<?>) select);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep orNot(Field field) {
        return orNot((Field<Boolean>) field);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep or(Field field) {
        return or((Field<Boolean>) field);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep andNotExists(Select select) {
        return andNotExists((Select<?>) select);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep andExists(Select select) {
        return andExists((Select<?>) select);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep andNot(Field field) {
        return andNot((Field<Boolean>) field);
    }

    @Override // org.jooq.UpdateConditionStep
    public /* bridge */ /* synthetic */ UpdateConditionStep and(Field field) {
        return and((Field<Boolean>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UpdateImpl(Configuration configuration, WithImpl with, Table<R> table) {
        super(new UpdateQueryImpl(configuration, with, table));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetStep
    public final <T> UpdateImpl<R> set(Field<T> field, T value) {
        ((UpdateQueryImpl) getDelegate()).addValue((Field<Field<T>>) field, (Field<T>) value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetStep
    public final <T> UpdateImpl<R> set(Field<T> field, Field<T> value) {
        ((UpdateQueryImpl) getDelegate()).addValue((Field) field, (Field) value);
        return this;
    }

    @Override // org.jooq.UpdateSetStep
    public final <T> UpdateImpl<R> set(Field<T> field, Select<? extends Record1<T>> value) {
        if (value == null) {
            return set((Field<Field<T>>) field, (Field<T>) null);
        }
        return set((Field) field, (Field) value.asField());
    }

    @Override // org.jooq.UpdateSetStep
    public final <T> UpdateImpl<R> setNull(Field<T> field) {
        return set((Field<Field<T>>) field, (Field<T>) null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetStep
    public final UpdateImpl<R> set(Map<?, ?> map) {
        ((UpdateQueryImpl) getDelegate()).addValues(map);
        return this;
    }

    @Override // org.jooq.UpdateSetStep
    public final UpdateImpl<R> set(Record record) {
        return set((Map<?, ?>) Tools.mapOfChangedValues(record));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final UpdateFromStep<R> set(RowN row, RowN value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1> UpdateFromStep<R> set(Row1<T1> row, Row1<T1> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2> UpdateFromStep<R> set(Row2<T1, T2> row, Row2<T1, T2> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3> UpdateFromStep<R> set(Row3<T1, T2, T3> row, Row3<T1, T2, T3> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4> UpdateFromStep<R> set(Row4<T1, T2, T3, T4> row, Row4<T1, T2, T3, T4> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5> UpdateFromStep<R> set(Row5<T1, T2, T3, T4, T5> row, Row5<T1, T2, T3, T4, T5> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6> UpdateFromStep<R> set(Row6<T1, T2, T3, T4, T5, T6> row, Row6<T1, T2, T3, T4, T5, T6> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7> UpdateFromStep<R> set(Row7<T1, T2, T3, T4, T5, T6, T7> row, Row7<T1, T2, T3, T4, T5, T6, T7> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> UpdateFromStep<R> set(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row, Row8<T1, T2, T3, T4, T5, T6, T7, T8> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> UpdateFromStep<R> set(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> UpdateFromStep<R> set(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> UpdateFromStep<R> set(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> UpdateFromStep<R> set(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> UpdateFromStep<R> set(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> UpdateFromStep<R> set(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row, Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> UpdateFromStep<R> set(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row, Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> UpdateFromStep<R> set(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row, Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> UpdateFromStep<R> set(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row, Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> UpdateFromStep<R> set(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row, Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> UpdateFromStep<R> set(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row, Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> UpdateFromStep<R> set(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row, Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> UpdateFromStep<R> set(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row, Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> UpdateFromStep<R> set(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row, Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> value) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, value);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final UpdateFromStep<R> set(RowN row, Select<? extends Record> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1> UpdateFromStep<R> set(Row1<T1> row, Select<? extends Record1<T1>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2> UpdateFromStep<R> set(Row2<T1, T2> row, Select<? extends Record2<T1, T2>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3> UpdateFromStep<R> set(Row3<T1, T2, T3> row, Select<? extends Record3<T1, T2, T3>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4> UpdateFromStep<R> set(Row4<T1, T2, T3, T4> row, Select<? extends Record4<T1, T2, T3, T4>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5> UpdateFromStep<R> set(Row5<T1, T2, T3, T4, T5> row, Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6> UpdateFromStep<R> set(Row6<T1, T2, T3, T4, T5, T6> row, Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7> UpdateFromStep<R> set(Row7<T1, T2, T3, T4, T5, T6, T7> row, Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> UpdateFromStep<R> set(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row, Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> UpdateFromStep<R> set(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row, Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> UpdateFromStep<R> set(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row, Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> UpdateFromStep<R> set(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row, Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> UpdateFromStep<R> set(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row, Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> UpdateFromStep<R> set(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row, Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> UpdateFromStep<R> set(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row, Select<? extends Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> UpdateFromStep<R> set(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row, Select<? extends Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> UpdateFromStep<R> set(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row, Select<? extends Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> UpdateFromStep<R> set(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row, Select<? extends Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> UpdateFromStep<R> set(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row, Select<? extends Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> UpdateFromStep<R> set(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row, Select<? extends Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> UpdateFromStep<R> set(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row, Select<? extends Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> UpdateFromStep<R> set(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row, Select<? extends Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateSetFirstStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> UpdateFromStep<R> set(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row, Select<? extends Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select) {
        ((UpdateQueryImpl) getDelegate()).addValues(row, select);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(TableLike<?> table) {
        ((UpdateQueryImpl) getDelegate()).addFrom(table);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(TableLike<?>... tables) {
        ((UpdateQueryImpl) getDelegate()).addFrom(tables);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(Collection<? extends TableLike<?>> tables) {
        ((UpdateQueryImpl) getDelegate()).addFrom(tables);
        return this;
    }

    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(SQL sql) {
        return from(DSL.table(sql));
    }

    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(String sql) {
        return from(DSL.table(sql));
    }

    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(String sql, Object... bindings) {
        return from(DSL.table(sql, bindings));
    }

    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(String sql, QueryPart... parts) {
        return from(DSL.table(sql, parts));
    }

    @Override // org.jooq.UpdateFromStep
    public final UpdateWhereStep<R> from(Name name) {
        return from(DSL.table(name));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(Condition conditions) {
        ((UpdateQueryImpl) getDelegate()).addConditions(conditions);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(Condition... conditions) {
        ((UpdateQueryImpl) getDelegate()).addConditions(conditions);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(Collection<? extends Condition> conditions) {
        ((UpdateQueryImpl) getDelegate()).addConditions(conditions);
        return this;
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(SQL sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(String sql) {
        return where(DSL.condition(sql));
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(String sql, Object... bindings) {
        return where(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> where(String sql, QueryPart... parts) {
        return where(DSL.condition(sql, parts));
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> whereExists(Select<?> select) {
        return andExists(select);
    }

    @Override // org.jooq.UpdateWhereStep
    public final UpdateImpl<R> whereNotExists(Select<?> select) {
        return andNotExists(select);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> and(Condition condition) {
        ((UpdateQueryImpl) getDelegate()).addConditions(condition);
        return this;
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> and(Field<Boolean> condition) {
        return and(DSL.condition(condition));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> and(SQL sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> and(String sql) {
        return and(DSL.condition(sql));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> and(String sql, Object... bindings) {
        return and(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> and(String sql, QueryPart... parts) {
        return and(DSL.condition(sql, parts));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> andNot(Condition condition) {
        return and(condition.not());
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> andNot(Field<Boolean> condition) {
        return andNot(DSL.condition(condition));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> andExists(Select<?> select) {
        return and(DSL.exists(select));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> andNotExists(Select<?> select) {
        return and(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> or(Condition condition) {
        ((UpdateQueryImpl) getDelegate()).addConditions(Operator.OR, condition);
        return this;
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> or(Field<Boolean> condition) {
        return or(DSL.condition(condition));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> or(SQL sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> or(String sql) {
        return or(DSL.condition(sql));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> or(String sql, Object... bindings) {
        return or(DSL.condition(sql, bindings));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> or(String sql, QueryPart... parts) {
        return or(DSL.condition(sql, parts));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> orNot(Condition condition) {
        return or(condition.not());
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> orNot(Field<Boolean> condition) {
        return orNot(DSL.condition(condition));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> orExists(Select<?> select) {
        return or(DSL.exists(select));
    }

    @Override // org.jooq.UpdateConditionStep
    public final UpdateImpl<R> orNotExists(Select<?> select) {
        return or(DSL.notExists(select));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateOrderByStep
    public final UpdateImpl<R> orderBy(OrderField<?>... fields) {
        ((UpdateQueryImpl) getDelegate()).addOrderBy(fields);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateOrderByStep
    public final UpdateImpl<R> orderBy(Collection<? extends OrderField<?>> fields) {
        ((UpdateQueryImpl) getDelegate()).addOrderBy(fields);
        return this;
    }

    @Override // org.jooq.UpdateOrderByStep
    public final UpdateImpl<R> orderBy(int... fieldIndexes) {
        return orderBy((Collection<? extends OrderField<?>>) Tools.map(fieldIndexes, v -> {
            return DSL.inline(v);
        }));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateLimitStep
    public final UpdateImpl<R> limit(Number limit) {
        ((UpdateQueryImpl) getDelegate()).addLimit(limit);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateLimitStep
    public final UpdateImpl<R> limit(Field<? extends Number> limit) {
        ((UpdateQueryImpl) getDelegate()).addLimit(limit);
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateReturningStep
    public final UpdateResultStep<R> returning() {
        ((UpdateQueryImpl) getDelegate()).setReturning();
        return new UpdateAsResultQuery((UpdateQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateReturningStep
    public final UpdateResultStep<R> returning(SelectFieldOrAsterisk... f) {
        ((UpdateQueryImpl) getDelegate()).setReturning(f);
        return new UpdateAsResultQuery((UpdateQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateReturningStep
    public final UpdateResultStep<R> returning(Collection<? extends SelectFieldOrAsterisk> f) {
        ((UpdateQueryImpl) getDelegate()).setReturning(f);
        return new UpdateAsResultQuery((UpdateQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateReturningStep
    public final UpdateResultStep<Record> returningResult(SelectFieldOrAsterisk... f) {
        this.returningResult = true;
        ((UpdateQueryImpl) getDelegate()).setReturning(f);
        return new UpdateAsResultQuery((UpdateQueryImpl) getDelegate(), this.returningResult);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.UpdateReturningStep
    public final UpdateResultStep<Record> returningResult(Collection<? extends SelectFieldOrAsterisk> f) {
        this.returningResult = true;
        ((UpdateQueryImpl) getDelegate()).setReturning(f);
        return new UpdateAsResultQuery((UpdateQueryImpl) getDelegate(), this.returningResult);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1> UpdateResultStep<Record1<T1>> returningResult(SelectField<T1> selectField) {
        return (UpdateResultStep<Record1<T1>>) returningResult(selectField);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2> UpdateResultStep<Record2<T1, T2>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2) {
        return (UpdateResultStep<Record2<T1, T2>>) returningResult(selectField, selectField2);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3> UpdateResultStep<Record3<T1, T2, T3>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3) {
        return (UpdateResultStep<Record3<T1, T2, T3>>) returningResult(selectField, selectField2, selectField3);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4> UpdateResultStep<Record4<T1, T2, T3, T4>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4) {
        return (UpdateResultStep<Record4<T1, T2, T3, T4>>) returningResult(selectField, selectField2, selectField3, selectField4);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5> UpdateResultStep<Record5<T1, T2, T3, T4, T5>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5) {
        return (UpdateResultStep<Record5<T1, T2, T3, T4, T5>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6> UpdateResultStep<Record6<T1, T2, T3, T4, T5, T6>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6) {
        return (UpdateResultStep<Record6<T1, T2, T3, T4, T5, T6>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7> UpdateResultStep<Record7<T1, T2, T3, T4, T5, T6, T7>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7) {
        return (UpdateResultStep<Record7<T1, T2, T3, T4, T5, T6, T7>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> UpdateResultStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8) {
        return (UpdateResultStep<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> UpdateResultStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9) {
        return (UpdateResultStep<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> UpdateResultStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10) {
        return (UpdateResultStep<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> UpdateResultStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11) {
        return (UpdateResultStep<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> UpdateResultStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12) {
        return (UpdateResultStep<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> UpdateResultStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13) {
        return (UpdateResultStep<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> UpdateResultStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14) {
        return (UpdateResultStep<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> UpdateResultStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15) {
        return (UpdateResultStep<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> UpdateResultStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16) {
        return (UpdateResultStep<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> UpdateResultStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17) {
        return (UpdateResultStep<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> UpdateResultStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18) {
        return (UpdateResultStep<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> UpdateResultStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19) {
        return (UpdateResultStep<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> UpdateResultStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20) {
        return (UpdateResultStep<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> UpdateResultStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21) {
        return (UpdateResultStep<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21);
    }

    @Override // org.jooq.UpdateReturningStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> UpdateResultStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> returningResult(SelectField<T1> selectField, SelectField<T2> selectField2, SelectField<T3> selectField3, SelectField<T4> selectField4, SelectField<T5> selectField5, SelectField<T6> selectField6, SelectField<T7> selectField7, SelectField<T8> selectField8, SelectField<T9> selectField9, SelectField<T10> selectField10, SelectField<T11> selectField11, SelectField<T12> selectField12, SelectField<T13> selectField13, SelectField<T14> selectField14, SelectField<T15> selectField15, SelectField<T16> selectField16, SelectField<T17> selectField17, SelectField<T18> selectField18, SelectField<T19> selectField19, SelectField<T20> selectField20, SelectField<T21> selectField21, SelectField<T22> selectField22) {
        return (UpdateResultStep<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) returningResult(selectField, selectField2, selectField3, selectField4, selectField5, selectField6, selectField7, selectField8, selectField9, selectField10, selectField11, selectField12, selectField13, selectField14, selectField15, selectField16, selectField17, selectField18, selectField19, selectField20, selectField21, selectField22);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.With $with() {
        return ((UpdateQueryImpl) getDelegate()).$with();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final Table<R> $table() {
        return ((UpdateQueryImpl) getDelegate()).$table();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<?> $table(Table<?> table) {
        return ((UpdateQueryImpl) getDelegate()).$table(table);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.UnmodifiableList<? extends Table<?>> $from() {
        return ((UpdateQueryImpl) getDelegate()).$from();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $from(Collection<? extends Table<?>> using) {
        return ((UpdateQueryImpl) getDelegate()).$from(using);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.UnmodifiableMap<? extends FieldOrRow, ? extends FieldOrRowOrSelect> $set() {
        return ((UpdateQueryImpl) getDelegate()).$set();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $set(Map<? extends FieldOrRow, ? extends FieldOrRowOrSelect> set) {
        return ((UpdateQueryImpl) getDelegate()).$set(set);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final Condition $where() {
        return ((UpdateQueryImpl) getDelegate()).$where();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $where(Condition condition) {
        return ((UpdateQueryImpl) getDelegate()).$where(condition);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return ((UpdateQueryImpl) getDelegate()).$orderBy();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $orderBy(Collection<? extends SortField<?>> orderBy) {
        return ((UpdateQueryImpl) getDelegate()).$orderBy(orderBy);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final Field<? extends Number> $limit() {
        return ((UpdateQueryImpl) getDelegate()).$limit();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $limit(Field<? extends Number> limit) {
        return ((UpdateQueryImpl) getDelegate()).$limit(limit);
    }
}
