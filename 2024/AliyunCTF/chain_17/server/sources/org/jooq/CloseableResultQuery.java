package org.jooq;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.exception.DataTypeException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/CloseableResultQuery.class */
public interface CloseableResultQuery<R extends Record> extends ResultQuery<R>, CloseableQuery {
    @Override // org.jooq.ResultQuery, org.jooq.Query
    @NotNull
    CloseableResultQuery<R> bind(String str, Object obj) throws IllegalArgumentException, DataTypeException;

    @Override // org.jooq.ResultQuery, org.jooq.Query
    @NotNull
    CloseableResultQuery<R> bind(int i, Object obj) throws IllegalArgumentException, DataTypeException;

    @Override // org.jooq.ResultQuery, org.jooq.Query
    @NotNull
    CloseableResultQuery<R> poolable(boolean z);

    @Override // org.jooq.ResultQuery, org.jooq.Query
    @NotNull
    CloseableResultQuery<R> queryTimeout(int i);

    @Override // org.jooq.ResultQuery, org.jooq.Query
    @NotNull
    CloseableResultQuery<R> keepStatement(boolean z);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<R> maxRows(int i);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<R> fetchSize(int i);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<R> resultSetConcurrency(int i);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<R> resultSetType(int i);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<R> resultSetHoldability(int i);

    @Override // org.jooq.ResultQuery
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    CloseableResultQuery<R> intern(Field<?>... fieldArr);

    @Override // org.jooq.ResultQuery
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    CloseableResultQuery<R> intern(int... iArr);

    @Override // org.jooq.ResultQuery
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    CloseableResultQuery<R> intern(String... strArr);

    @Override // org.jooq.ResultQuery
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    CloseableResultQuery<R> intern(Name... nameArr);

    @Override // org.jooq.ResultQuery
    @NotNull
    <X extends Record> CloseableResultQuery<X> coerce(Table<X> table);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<Record> coerce(Field<?>... fieldArr);

    @Override // org.jooq.ResultQuery
    @NotNull
    CloseableResultQuery<Record> coerce(Collection<? extends Field<?>> collection);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1> CloseableResultQuery<Record1<T1>> coerce(Field<T1> field);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2> CloseableResultQuery<Record2<T1, T2>> coerce(Field<T1> field, Field<T2> field2);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3> CloseableResultQuery<Record3<T1, T2, T3>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4> CloseableResultQuery<Record4<T1, T2, T3, T4>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5> CloseableResultQuery<Record5<T1, T2, T3, T4, T5>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6> CloseableResultQuery<Record6<T1, T2, T3, T4, T5, T6>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7> CloseableResultQuery<Record7<T1, T2, T3, T4, T5, T6, T7>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8> CloseableResultQuery<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9> CloseableResultQuery<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> CloseableResultQuery<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> CloseableResultQuery<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> CloseableResultQuery<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> CloseableResultQuery<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> CloseableResultQuery<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> CloseableResultQuery<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> CloseableResultQuery<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> CloseableResultQuery<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> CloseableResultQuery<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> CloseableResultQuery<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> CloseableResultQuery<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> CloseableResultQuery<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21);

    @Override // org.jooq.ResultQuery
    @NotNull
    <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> CloseableResultQuery<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22);

    @Override // org.jooq.ResultQuery
    @NotNull
    /* bridge */ /* synthetic */ default ResultQuery coerce(Collection collection) {
        return coerce((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.ResultQuery
    @NotNull
    /* bridge */ /* synthetic */ default ResultQuery coerce(Field[] fieldArr) {
        return coerce((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.ResultQuery
    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    /* bridge */ /* synthetic */ default ResultQuery intern(Field[] fieldArr) {
        return intern((Field<?>[]) fieldArr);
    }
}
