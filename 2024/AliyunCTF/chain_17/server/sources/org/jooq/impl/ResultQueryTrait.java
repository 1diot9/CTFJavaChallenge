package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.CloseableResultQuery;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Cursor;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.QueryPartInternal;
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
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.Records;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.Row;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.R2DBC;
import org.jooq.tools.jdbc.JDBCUtils;
import org.reactivestreams.Subscriber;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ResultQueryTrait.class */
public interface ResultQueryTrait<R extends Record> extends QueryPartInternal, CloseableResultQuery<R>, Mappable<R>, FieldsTrait {
    Field<?>[] getFields();

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    /* bridge */ /* synthetic */ default ResultQuery coerce(Field[] fieldArr) {
        return coerce((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default CloseableResultQuery<Record> coerce(Field<?>... fields) {
        return coerce((Collection<? extends Field<?>>) Arrays.asList(fields));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1> CloseableResultQuery<Record1<T1>> coerce(Field<T1> field) {
        return (CloseableResultQuery<Record1<T1>>) coerce((Field<?>[]) new Field[]{field});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2> CloseableResultQuery<Record2<T1, T2>> coerce(Field<T1> field, Field<T2> field2) {
        return (CloseableResultQuery<Record2<T1, T2>>) coerce((Field<?>[]) new Field[]{field, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3> CloseableResultQuery<Record3<T1, T2, T3>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3) {
        return (CloseableResultQuery<Record3<T1, T2, T3>>) coerce((Field<?>[]) new Field[]{field, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4> CloseableResultQuery<Record4<T1, T2, T3, T4>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return (CloseableResultQuery<Record4<T1, T2, T3, T4>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5> CloseableResultQuery<Record5<T1, T2, T3, T4, T5>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return (CloseableResultQuery<Record5<T1, T2, T3, T4, T5>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6> CloseableResultQuery<Record6<T1, T2, T3, T4, T5, T6>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return (CloseableResultQuery<Record6<T1, T2, T3, T4, T5, T6>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7> CloseableResultQuery<Record7<T1, T2, T3, T4, T5, T6, T7>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return (CloseableResultQuery<Record7<T1, T2, T3, T4, T5, T6, T7>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8> CloseableResultQuery<Record8<T1, T2, T3, T4, T5, T6, T7, T8>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return (CloseableResultQuery<Record8<T1, T2, T3, T4, T5, T6, T7, T8>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9> CloseableResultQuery<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return (CloseableResultQuery<Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> CloseableResultQuery<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return (CloseableResultQuery<Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> CloseableResultQuery<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return (CloseableResultQuery<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> CloseableResultQuery<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return (CloseableResultQuery<Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> CloseableResultQuery<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return (CloseableResultQuery<Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> CloseableResultQuery<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return (CloseableResultQuery<Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> CloseableResultQuery<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return (CloseableResultQuery<Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> CloseableResultQuery<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return (CloseableResultQuery<Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> CloseableResultQuery<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return (CloseableResultQuery<Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> CloseableResultQuery<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return (CloseableResultQuery<Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> CloseableResultQuery<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return (CloseableResultQuery<Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> CloseableResultQuery<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return (CloseableResultQuery<Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> CloseableResultQuery<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return (CloseableResultQuery<Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CloseableResultQuery, org.jooq.ResultQuery
    default <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> CloseableResultQuery<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> coerce(Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return (CloseableResultQuery<Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>>) coerce((Field<?>[]) new Field[]{field, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.ResultQuery
    default Cursor<R> fetchLazy() throws DataAccessException {
        return new ResultAsCursor(fetch());
    }

    @Override // org.jooq.ResultQuery
    default Results fetchMany() throws DataAccessException {
        throw new DataAccessException("Attempt to call fetchMany() on " + String.valueOf(getClass()));
    }

    default Cursor<R> fetchLazyNonAutoClosing() {
        return fetchLazy();
    }

    @Override // org.jooq.ResultQuery
    default ResultSet fetchResultSet() {
        if (SettingsTools.fetchIntermediateResult(Tools.configuration(this))) {
            return fetch().intoResultSet();
        }
        return fetchLazy().resultSet();
    }

    @Override // org.jooq.ResultQuery, java.lang.Iterable
    default Iterator<R> iterator() {
        return (Iterator<R>) fetch().iterator();
    }

    @Override // org.jooq.ResultQuery
    default CompletionStage<Result<R>> fetchAsync() {
        return fetchAsync(Tools.configuration(this).executorProvider().provide());
    }

    @Override // org.jooq.ResultQuery
    default CompletionStage<Result<R>> fetchAsync(Executor executor) {
        return ExecutorProviderCompletionStage.of(CompletableFuture.supplyAsync(Tools.blocking(this::fetch), executor), () -> {
            return executor;
        });
    }

    @Override // org.jooq.ResultQuery
    default Stream<R> fetchStream() {
        if (SettingsTools.fetchIntermediateResult(Tools.configuration(this))) {
            return fetch().stream();
        }
        AtomicReference<Cursor<R>> r = new AtomicReference<>();
        return (Stream) StreamSupport.stream(() -> {
            Cursor<R> c = fetchLazy();
            r.set(c);
            return c.spliterator();
        }, 1296, false).onClose(() -> {
            JDBCUtils.safeClose((AutoCloseable) r.get());
        });
    }

    @Override // org.jooq.ResultQuery
    default <E> Stream<E> fetchStreamInto(Class<? extends E> cls) {
        return (Stream<E>) fetchStream().map(mapper(Tools.configuration(this), cls));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <Z extends Record> Stream<Z> fetchStreamInto(Table<Z> table) {
        return (Stream<Z>) fetchStream().map(mapper(table));
    }

    @Override // org.jooq.ResultQuery
    default Stream<R> stream() {
        return fetchStream();
    }

    @Override // org.jooq.ResultQuery
    default <X, A> X collect(Collector<? super R, A, X> collector) {
        if (SettingsTools.fetchIntermediateResult(Tools.configuration(this))) {
            return (X) ((Result) DelayedArrayCollector.patch(collector, fetch())).collect(collector);
        }
        Cursor<R> fetchLazyNonAutoClosing = fetchLazyNonAutoClosing();
        try {
            X x = (X) ((Cursor) DelayedArrayCollector.patch(collector, fetchLazyNonAutoClosing)).collect(collector);
            if (fetchLazyNonAutoClosing != null) {
                fetchLazyNonAutoClosing.close();
            }
            return x;
        } catch (Throwable th) {
            if (fetchLazyNonAutoClosing != null) {
                try {
                    fetchLazyNonAutoClosing.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.reactivestreams.Publisher
    default void subscribe(Subscriber<? super R> subscriber) {
        ConnectionFactory cf = configuration().connectionFactory();
        if (!(cf instanceof NoConnectionFactory)) {
            subscriber.onSubscribe(new R2DBC.QuerySubscription(this, subscriber, R2DBC.ResultSubscriber::new));
        } else {
            subscriber.onSubscribe(new R2DBC.BlockingRecordSubscription(this, subscriber));
        }
    }

    @Override // org.jooq.ResultQuery
    default <T> List<T> fetch(Field<T> field) {
        return (List) collect(Records.intoList(mapper(field)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(Field<?> field, Class<? extends U> type) {
        return (List) collect(Records.intoList(mapper(field, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <T, U> List<U> fetch(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (List) collect(Records.intoList(mapper(field, converter)));
    }

    @Override // org.jooq.ResultQuery
    default List<?> fetch(int fieldIndex) {
        return (List) collect(Records.intoList(mapper(fieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(int fieldIndex, Class<? extends U> type) {
        return (List) collect(Records.intoList(mapper(fieldIndex, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(int fieldIndex, Converter<?, ? extends U> converter) {
        return (List) collect(Records.intoList(mapper(fieldIndex, converter)));
    }

    @Override // org.jooq.ResultQuery
    default List<?> fetch(String fieldName) {
        return (List) collect(Records.intoList(mapper(fieldName)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(String fieldName, Class<? extends U> type) {
        return (List) collect(Records.intoList(mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(String fieldName, Converter<?, ? extends U> converter) {
        return (List) collect(Records.intoList(mapper(fieldName, converter)));
    }

    @Override // org.jooq.ResultQuery
    default List<?> fetch(Name fieldName) {
        return (List) collect(Records.intoList(mapper(fieldName)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(Name fieldName, Class<? extends U> type) {
        return (List) collect(Records.intoList(mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <U> List<U> fetch(Name fieldName, Converter<?, ? extends U> converter) {
        return (List) collect(Records.intoList(mapper(fieldName, converter)));
    }

    @Override // org.jooq.ResultQuery
    default <T> T fetchOne(Field<T> field) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (T) fetchOne.get(field);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(Field<?> field, Class<? extends U> cls) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(field, cls);
    }

    @Override // org.jooq.ResultQuery
    default <T, U> U fetchOne(Field<T> field, Converter<? super T, ? extends U> converter) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(field, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchOne(int fieldIndex) {
        R record = fetchOne();
        if (record == null) {
            return null;
        }
        return record.get(fieldIndex);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(int i, Class<? extends U> cls) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(i, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(int i, Converter<?, ? extends U> converter) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(i, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchOne(String fieldName) {
        R record = fetchOne();
        if (record == null) {
            return null;
        }
        return record.get(fieldName);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(String str, Class<? extends U> cls) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(str, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(String str, Converter<?, ? extends U> converter) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(str, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchOne(Name fieldName) {
        R record = fetchOne();
        if (record == null) {
            return null;
        }
        return record.get(fieldName);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(Name name, Class<? extends U> cls) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(name, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchOne(Name name, Converter<?, ? extends U> converter) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (U) fetchOne.get(name, converter);
    }

    @Override // org.jooq.ResultQuery
    default R fetchOne() {
        return (R) Tools.fetchOne(fetchLazyNonAutoClosing(), hasLimit1());
    }

    @Override // org.jooq.ResultQuery
    default <E> E fetchOne(RecordMapper<? super R, E> mapper) {
        R record = fetchOne();
        if (record == null) {
            return null;
        }
        return mapper.map(record);
    }

    @Override // org.jooq.ResultQuery
    default Map<String, Object> fetchOneMap() {
        R record = fetchOne();
        if (record == null) {
            return null;
        }
        return record.intoMap();
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[] fetchOneArray() {
        R record = fetchOne();
        if (record == null) {
            return null;
        }
        return record.intoArray();
    }

    @Override // org.jooq.ResultQuery
    default <E> E fetchOneInto(Class<? extends E> cls) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (E) fetchOne.into(cls);
    }

    @Override // org.jooq.ResultQuery
    default <Z extends Record> Z fetchOneInto(Table<Z> table) {
        R fetchOne = fetchOne();
        if (fetchOne == null) {
            return null;
        }
        return (Z) fetchOne.into(table);
    }

    @Override // org.jooq.ResultQuery
    default <T> T fetchSingle(Field<T> field) {
        return (T) fetchSingle().get(field);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(Field<?> field, Class<? extends U> cls) {
        return (U) fetchSingle().get(field, cls);
    }

    @Override // org.jooq.ResultQuery
    default <T, U> U fetchSingle(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (U) fetchSingle().get(field, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchSingle(int fieldIndex) {
        return fetchSingle().get(fieldIndex);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(int i, Class<? extends U> cls) {
        return (U) fetchSingle().get(i, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(int i, Converter<?, ? extends U> converter) {
        return (U) fetchSingle().get(i, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchSingle(String fieldName) {
        return fetchSingle().get(fieldName);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(String str, Class<? extends U> cls) {
        return (U) fetchSingle().get(str, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(String str, Converter<?, ? extends U> converter) {
        return (U) fetchSingle().get(str, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchSingle(Name fieldName) {
        return fetchSingle().get(fieldName);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(Name name, Class<? extends U> cls) {
        return (U) fetchSingle().get(name, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchSingle(Name name, Converter<?, ? extends U> converter) {
        return (U) fetchSingle().get(name, converter);
    }

    @Override // org.jooq.ResultQuery
    default R fetchSingle() {
        return (R) Tools.fetchSingle(fetchLazyNonAutoClosing(), hasLimit1());
    }

    @Override // org.jooq.ResultQuery
    default <E> E fetchSingle(RecordMapper<? super R, E> mapper) {
        return mapper.map(fetchSingle());
    }

    @Override // org.jooq.ResultQuery
    default Map<String, Object> fetchSingleMap() {
        return fetchSingle().intoMap();
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[] fetchSingleArray() {
        return fetchSingle().intoArray();
    }

    @Override // org.jooq.ResultQuery
    default <E> E fetchSingleInto(Class<? extends E> cls) {
        return (E) fetchSingle().into(cls);
    }

    @Override // org.jooq.ResultQuery
    default <Z extends Record> Z fetchSingleInto(Table<Z> table) {
        return (Z) fetchSingle().into(table);
    }

    @Override // org.jooq.ResultQuery
    default <T> Optional<T> fetchOptional(Field<T> field) {
        return Optional.ofNullable(fetchOne(field));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(Field<?> field, Class<? extends U> type) {
        return Optional.ofNullable(fetchOne(field, type));
    }

    @Override // org.jooq.ResultQuery
    default <T, U> Optional<U> fetchOptional(Field<T> field, Converter<? super T, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(field, converter));
    }

    @Override // org.jooq.ResultQuery
    default Optional<?> fetchOptional(int fieldIndex) {
        return Optional.ofNullable(fetchOne(fieldIndex));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(int fieldIndex, Class<? extends U> type) {
        return Optional.ofNullable(fetchOne(fieldIndex, type));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(int fieldIndex, Converter<?, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(fieldIndex, converter));
    }

    @Override // org.jooq.ResultQuery
    default Optional<?> fetchOptional(String fieldName) {
        return Optional.ofNullable(fetchOne(fieldName));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(String fieldName, Class<? extends U> type) {
        return Optional.ofNullable(fetchOne(fieldName, type));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(String fieldName, Converter<?, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(fieldName, converter));
    }

    @Override // org.jooq.ResultQuery
    default Optional<?> fetchOptional(Name fieldName) {
        return Optional.ofNullable(fetchOne(fieldName));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(Name fieldName, Class<? extends U> type) {
        return Optional.ofNullable(fetchOne(fieldName, type));
    }

    @Override // org.jooq.ResultQuery
    default <U> Optional<U> fetchOptional(Name fieldName, Converter<?, ? extends U> converter) {
        return Optional.ofNullable(fetchOne(fieldName, converter));
    }

    @Override // org.jooq.ResultQuery
    default Optional<R> fetchOptional() {
        return Optional.ofNullable(fetchOne());
    }

    @Override // org.jooq.ResultQuery
    default <E> Optional<E> fetchOptional(RecordMapper<? super R, E> mapper) {
        return Optional.ofNullable(fetchOne(mapper));
    }

    @Override // org.jooq.ResultQuery
    default Optional<Map<String, Object>> fetchOptionalMap() {
        return Optional.ofNullable(fetchOneMap());
    }

    @Override // org.jooq.ResultQuery
    default Optional<Object[]> fetchOptionalArray() {
        return Optional.ofNullable(fetchOneArray());
    }

    @Override // org.jooq.ResultQuery
    default <E> Optional<E> fetchOptionalInto(Class<? extends E> type) {
        return Optional.ofNullable(fetchOneInto(type));
    }

    @Override // org.jooq.ResultQuery
    default <Z extends Record> Optional<Z> fetchOptionalInto(Table<Z> table) {
        return Optional.ofNullable(fetchOneInto(table));
    }

    @Override // org.jooq.ResultQuery
    default <T> T fetchAny(Field<T> field) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (T) fetchAny.get(field);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(Field<?> field, Class<? extends U> cls) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(field, cls);
    }

    @Override // org.jooq.ResultQuery
    default <T, U> U fetchAny(Field<T> field, Converter<? super T, ? extends U> converter) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(field, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchAny(int fieldIndex) {
        R record = fetchAny();
        if (record == null) {
            return null;
        }
        return record.get(fieldIndex);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(int i, Class<? extends U> cls) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(i, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(int i, Converter<?, ? extends U> converter) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(i, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchAny(String fieldName) {
        R record = fetchAny();
        if (record == null) {
            return null;
        }
        return record.get(fieldName);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(String str, Class<? extends U> cls) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(str, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(String str, Converter<?, ? extends U> converter) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(str, converter);
    }

    @Override // org.jooq.ResultQuery
    default Object fetchAny(Name fieldName) {
        R record = fetchAny();
        if (record == null) {
            return null;
        }
        return record.get(fieldName);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(Name name, Class<? extends U> cls) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(name, cls);
    }

    @Override // org.jooq.ResultQuery
    default <U> U fetchAny(Name name, Converter<?, ? extends U> converter) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (U) fetchAny.get(name, converter);
    }

    @Override // org.jooq.ResultQuery
    default R fetchAny() {
        Cursor<R> c = fetchLazyNonAutoClosing();
        try {
            R fetchNext = c.fetchNext();
            if (c != null) {
                c.close();
            }
            return fetchNext;
        } catch (Throwable th) {
            if (c != null) {
                try {
                    c.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.jooq.ResultQuery
    default <E> E fetchAny(RecordMapper<? super R, E> mapper) {
        R record = fetchAny();
        if (record == null) {
            return null;
        }
        return mapper.map(record);
    }

    @Override // org.jooq.ResultQuery
    default Map<String, Object> fetchAnyMap() {
        R record = fetchAny();
        if (record == null) {
            return null;
        }
        return record.intoMap();
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[] fetchAnyArray() {
        R record = fetchAny();
        if (record == null) {
            return null;
        }
        return record.intoArray();
    }

    @Override // org.jooq.ResultQuery
    default <E> E fetchAnyInto(Class<? extends E> cls) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (E) fetchAny.into(cls);
    }

    @Override // org.jooq.ResultQuery
    default <Z extends Record> Z fetchAnyInto(Table<Z> table) {
        R fetchAny = fetchAny();
        if (fetchAny == null) {
            return null;
        }
        return (Z) fetchAny.into(table);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K> Map<K, R> fetchMap(Field<K> field) {
        return (Map) collect(Records.intoMap(mapper(field)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, R> fetchMap(int keyFieldIndex) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, R> fetchMap(String keyFieldName) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, R> fetchMap(Name keyFieldName) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, V> fetchMap(Field<K> field, Field<V> field2) {
        return (Map) collect(Records.intoMap(mapper(field), mapper(field2)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, ?> fetchMap(int keyFieldIndex, int valueFieldIndex) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndex), mapper(valueFieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, ?> fetchMap(String keyFieldName, String valueFieldName) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName), mapper(valueFieldName)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, ?> fetchMap(Name keyFieldName, Name valueFieldName) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName), mapper(valueFieldName)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, E> Map<K, E> fetchMap(Field<K> field, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(field), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, E> fetchMap(int keyFieldIndex, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndex), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, E> fetchMap(String keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, E> fetchMap(Name keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName), mapper(Tools.configuration(this), type)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, E> Map<K, E> fetchMap(Field<K> field, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(field), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, E> fetchMap(int keyFieldIndex, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndex), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, E> fetchMap(String keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, E> fetchMap(Name keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, R> fetchMap(Field<?>[] keys) {
        return (Map) collect(Records.intoMap(mapper(keys)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, R> fetchMap(int[] keyFieldIndexes) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndexes)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, R> fetchMap(String[] keyFieldNames) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, R> fetchMap(Name[] keyFieldNames) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Record> fetchMap(Field<?>[] keys, Field<?>[] values) {
        return (Map) collect(Records.intoMap(mapper(keys), mapper(values)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Record> fetchMap(int[] keyFieldIndexes, int[] valueFieldIndexes) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndexes), mapper(valueFieldIndexes)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Record> fetchMap(String[] keyFieldNames, String[] valueFieldNames) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames), mapper(valueFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Record> fetchMap(Name[] keyFieldNames, Name[] valueFieldNames) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames), mapper(valueFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(Field<?>[] keys, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keys).andThen((v0) -> {
            return v0.intoList();
        }), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(int[] keyFieldIndexes, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndexes).andThen((v0) -> {
            return v0.intoList();
        }), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(String[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(Name[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(Field<?>[] keys, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keys).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(int[] keyFieldIndexes, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keyFieldIndexes).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(String[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<List<?>, E> fetchMap(Name[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(keyFieldNames).andThen((v0) -> {
            return v0.intoList();
        }), mapper));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K> Map<K, R> fetchMap(Class<? extends K> cls) {
        return (Map) collect(Records.intoMap(mapper(Tools.configuration(this), cls)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, V> fetchMap(Class<? extends K> cls, Class<? extends V> cls2) {
        return (Map) collect(Records.intoMap(mapper(Tools.configuration(this), cls), mapper(Tools.configuration(this), cls2)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, V> fetchMap(Class<? extends K> cls, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoMap(mapper(Tools.configuration(this), cls), valueMapper));
    }

    @Override // org.jooq.ResultQuery
    default <K> Map<K, R> fetchMap(RecordMapper<? super R, K> keyMapper) {
        return (Map) collect(Records.intoMap(keyMapper));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, V> fetchMap(RecordMapper<? super R, K> keyMapper, Class<V> cls) {
        return (Map) collect(Records.intoMap(keyMapper, mapper(Tools.configuration(this), cls)));
    }

    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, V> fetchMap(RecordMapper<? super R, K> keyMapper, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoMap(keyMapper, valueMapper));
    }

    @Override // org.jooq.ResultQuery
    default <S extends Record> Map<S, R> fetchMap(Table<S> table) {
        return (Map) collect(Records.intoMap(mapper(table)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <S extends Record, T extends Record> Map<S, T> fetchMap(Table<S> keyTable, Table<T> table) {
        return (Map) collect(Records.intoMap(mapper(keyTable), mapper(table)));
    }

    @Override // org.jooq.ResultQuery
    default <E, S extends Record> Map<S, E> fetchMap(Table<S> table, Class<? extends E> type) {
        return (Map) collect(Records.intoMap(mapper(table), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E, S extends Record> Map<S, E> fetchMap(Table<S> table, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoMap(mapper(table), mapper));
    }

    @Override // org.jooq.ResultQuery
    default List<Map<String, Object>> fetchMaps() {
        if (SettingsTools.fetchIntermediateResult(Tools.configuration(this))) {
            return fetch().intoMaps();
        }
        Cursor<R> c = fetchLazy();
        try {
            List<Map<String, Object>> list = (List) c.stream().collect(ArrayList::new, (l, r) -> {
                l.add(r.intoMap());
            }, (v0, v1) -> {
                v0.addAll(v1);
            });
            if (c != null) {
                c.close();
            }
            return list;
        } catch (Throwable th) {
            if (c != null) {
                try {
                    c.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K> Map<K, Result<R>> fetchGroups(Field<K> field) {
        return (Map) collect(Records.intoResultGroups(mapper(field)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, Result<R>> fetchGroups(int keyFieldIndex) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, Result<R>> fetchGroups(String keyFieldName) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldName)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, Result<R>> fetchGroups(Name keyFieldName) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldName)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, List<V>> fetchGroups(Field<K> field, Field<V> field2) {
        return (Map) collect(Records.intoGroups(mapper(field), mapper(field2)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, List<?>> fetchGroups(int keyFieldIndex, int valueFieldIndex) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldIndex), mapper(valueFieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, List<?>> fetchGroups(String keyFieldName, String valueFieldName) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldName), mapper(valueFieldName)));
    }

    @Override // org.jooq.ResultQuery
    default Map<?, List<?>> fetchGroups(Name keyFieldName, Name valueFieldName) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldName), mapper(valueFieldName)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, E> Map<K, List<E>> fetchGroups(Field<K> field, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(field), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, List<E>> fetchGroups(int keyFieldIndex, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldIndex), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, List<E>> fetchGroups(String keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldName), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, List<E>> fetchGroups(Name keyFieldName, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldName), mapper(Tools.configuration(this), type)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, E> Map<K, List<E>> fetchGroups(Field<K> field, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(field), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, List<E>> fetchGroups(int keyFieldIndex, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldIndex), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, List<E>> fetchGroups(String keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<?, List<E>> fetchGroups(Name keyFieldName, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldName), mapper));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<R>> fetchGroups(Field<?>[] keys) {
        return (Map) collect(Records.intoResultGroups(mapper(keys)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<R>> fetchGroups(int[] keyFieldIndexes) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldIndexes)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<R>> fetchGroups(String[] keyFieldNames) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<R>> fetchGroups(Name[] keyFieldNames) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<Record>> fetchGroups(Field<?>[] keys, Field<?>[] values) {
        return (Map) collect(Records.intoResultGroups(mapper(keys), mapper(values)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<Record>> fetchGroups(int[] keyFieldIndexes, int[] valueFieldIndexes) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldIndexes), mapper(valueFieldIndexes)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<Record>> fetchGroups(String[] keyFieldNames, String[] valueFieldNames) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldNames), mapper(valueFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default Map<Record, Result<Record>> fetchGroups(Name[] keyFieldNames, Name[] valueFieldNames) {
        return (Map) collect(Records.intoResultGroups(mapper(keyFieldNames), mapper(valueFieldNames)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(Field<?>[] keys, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keys), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(int[] keyFieldIndexes, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldIndexes), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(String[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldNames), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(Name[] keyFieldNames, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldNames), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(int[] keyFieldIndexes, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldIndexes), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(String[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldNames), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(Name[] keyFieldNames, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keyFieldNames), mapper));
    }

    @Override // org.jooq.ResultQuery
    default <E> Map<Record, List<E>> fetchGroups(Field<?>[] keys, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(keys), mapper));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K> Map<K, Result<R>> fetchGroups(Class<? extends K> cls) {
        return (Map) collect(Records.intoResultGroups(mapper(Tools.configuration(this), cls)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, List<V>> fetchGroups(Class<? extends K> cls, Class<? extends V> cls2) {
        return (Map) collect(Records.intoGroups(mapper(Tools.configuration(this), cls), mapper(Tools.configuration(this), cls2)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, List<V>> fetchGroups(Class<? extends K> cls, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoGroups(mapper(Tools.configuration(this), cls), valueMapper));
    }

    @Override // org.jooq.ResultQuery
    default <K> Map<K, Result<R>> fetchGroups(RecordMapper<? super R, K> keyMapper) {
        return (Map) collect(Records.intoResultGroups(keyMapper));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, List<V>> fetchGroups(RecordMapper<? super R, K> keyMapper, Class<V> cls) {
        return (Map) collect(Records.intoGroups(keyMapper, mapper(Tools.configuration(this), cls)));
    }

    @Override // org.jooq.ResultQuery
    default <K, V> Map<K, List<V>> fetchGroups(RecordMapper<? super R, K> keyMapper, RecordMapper<? super R, V> valueMapper) {
        return (Map) collect(Records.intoGroups(keyMapper, valueMapper));
    }

    @Override // org.jooq.ResultQuery
    default <S extends Record> Map<S, Result<R>> fetchGroups(Table<S> table) {
        return (Map) collect(Records.intoResultGroups(mapper(table)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <S extends Record, T extends Record> Map<S, Result<T>> fetchGroups(Table<S> keyTable, Table<T> table) {
        return (Map) collect(Records.intoResultGroups(mapper(keyTable), mapper(table)));
    }

    @Override // org.jooq.ResultQuery
    default <E, S extends Record> Map<S, List<E>> fetchGroups(Table<S> table, Class<? extends E> type) {
        return (Map) collect(Records.intoGroups(mapper(table), mapper(Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <E, S extends Record> Map<S, List<E>> fetchGroups(Table<S> table, RecordMapper<? super R, E> mapper) {
        return (Map) collect(Records.intoGroups(mapper(table), mapper));
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[][] fetchArrays() {
        return (Object[][]) collect(Records.intoArray(new Object[0], (v0) -> {
            return v0.intoArray();
        }));
    }

    @Override // org.jooq.ResultQuery
    @NotNull
    default R[] fetchArray() {
        Class<? extends R> recordType;
        Result<R> fetch = fetch();
        if (fetch.isNotEmpty()) {
            return (R[]) ((Record[]) fetch.toArray((Record[]) java.lang.reflect.Array.newInstance(((Record) fetch.get(0)).getClass(), fetch.size())));
        }
        if (this instanceof AbstractResultQuery) {
            recordType = ((AbstractResultQuery) this).getRecordType();
        } else if (this instanceof SelectImpl) {
            recordType = ((SelectImpl) this).getRecordType();
        } else {
            throw new DataAccessException("Attempt to call fetchArray() on " + String.valueOf(getClass()));
        }
        return (R[]) ((Record[]) fetch.toArray((Record[]) java.lang.reflect.Array.newInstance(recordType, fetch.size())));
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[] fetchArray(int fieldIndex) {
        return (Object[]) collect(new DelayedArrayCollector(fields -> {
            return (Object[]) java.lang.reflect.Array.newInstance(fields.field(Tools.indexOrFail(fields, fieldIndex)).getType(), 0);
        }, mapper(fieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(int i, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, mapper(i, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(int i, Converter<?, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), mapper(i, converter))));
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[] fetchArray(String fieldName) {
        return (Object[]) collect(new DelayedArrayCollector(fields -> {
            return (Object[]) java.lang.reflect.Array.newInstance(fields.field(Tools.indexOrFail(fields, fieldName)).getType(), 0);
        }, mapper(fieldName)));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(String str, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, mapper(str, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(String str, Converter<?, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), mapper(str, converter))));
    }

    @Override // org.jooq.ResultQuery
    @Nullable
    default Object[] fetchArray(Name fieldName) {
        return (Object[]) collect(new DelayedArrayCollector(fields -> {
            return (Object[]) java.lang.reflect.Array.newInstance(fields.field(Tools.indexOrFail(fields, fieldName)).getType(), 0);
        }, mapper(fieldName)));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(Name name, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, mapper(name, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(Name name, Converter<?, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), mapper(name, converter))));
    }

    @Override // org.jooq.ResultQuery
    default <T> T[] fetchArray(Field<T> field) {
        return (T[]) ((Object[]) collect(Records.intoArray(field.getType(), mapper(field))));
    }

    @Override // org.jooq.ResultQuery
    default <U> U[] fetchArray(Field<?> field, Class<? extends U> cls) {
        return (U[]) ((Object[]) collect(Records.intoArray(cls, mapper(field, Tools.configuration(this), cls))));
    }

    @Override // org.jooq.ResultQuery
    default <T, U> U[] fetchArray(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (U[]) ((Object[]) collect(Records.intoArray(converter.toType(), mapper(field, converter))));
    }

    @Override // org.jooq.ResultQuery
    default <E> Set<E> fetchSet(RecordMapper<? super R, E> mapper) {
        return (Set) collect(Records.intoSet(mapper));
    }

    @Override // org.jooq.ResultQuery
    default Set<?> fetchSet(int fieldIndex) {
        return (Set) collect(Records.intoSet(mapper(fieldIndex)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(int fieldIndex, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(mapper(fieldIndex, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(int fieldIndex, Converter<?, ? extends U> converter) {
        return (Set) collect(Records.intoSet(mapper(fieldIndex, converter)));
    }

    @Override // org.jooq.ResultQuery
    default Set<?> fetchSet(String fieldName) {
        return (Set) collect(Records.intoSet(mapper(fieldName)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(String fieldName, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(String fieldName, Converter<?, ? extends U> converter) {
        return (Set) collect(Records.intoSet(mapper(fieldName, converter)));
    }

    @Override // org.jooq.ResultQuery
    default Set<?> fetchSet(Name fieldName) {
        return (Set) collect(Records.intoSet(mapper(fieldName)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(Name fieldName, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(mapper(fieldName, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(Name fieldName, Converter<?, ? extends U> converter) {
        return (Set) collect(Records.intoSet(mapper(fieldName, converter)));
    }

    @Override // org.jooq.ResultQuery
    default <T> Set<T> fetchSet(Field<T> field) {
        return (Set) collect(Records.intoSet(mapper(field)));
    }

    @Override // org.jooq.ResultQuery
    default <U> Set<U> fetchSet(Field<?> field, Class<? extends U> type) {
        return (Set) collect(Records.intoSet(mapper(field, Tools.configuration(this), type)));
    }

    @Override // org.jooq.ResultQuery
    default <T, U> Set<U> fetchSet(Field<T> field, Converter<? super T, ? extends U> converter) {
        return (Set) collect(Records.intoSet(mapper(field, converter)));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery
    default <U> List<U> fetchInto(Class<? extends U> cls) {
        return (List) collect(Records.intoList(mapper(Tools.configuration(this), cls)));
    }

    @Override // org.jooq.ResultQuery
    default <Z extends Record> Result<Z> fetchInto(Table<Z> table) {
        if (SettingsTools.fetchIntermediateResult(Tools.configuration(this))) {
            return fetch().into(table);
        }
        Cursor<R> c = fetchLazy();
        try {
            Result<Z> fetchInto = c.fetchInto(table);
            if (c != null) {
                c.close();
            }
            return fetchInto;
        } catch (Throwable th) {
            if (c != null) {
                try {
                    c.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.jooq.ResultQuery
    default <H extends RecordHandler<? super R>> H fetchInto(H handler) {
        forEach(handler);
        return handler;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ResultQuery, java.lang.Iterable
    default void forEach(Consumer<? super R> consumer) {
        if (SettingsTools.fetchIntermediateResult(Tools.configuration(this))) {
            fetch().forEach(consumer);
            return;
        }
        Cursor<R> c = fetchLazy();
        try {
            c.forEach(consumer);
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                try {
                    c.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @Override // org.jooq.ResultQuery
    default <E> List<E> fetch(RecordMapper<? super R, E> mapper) {
        return (List) collect(Collectors.mapping(mapper, Collectors.toList()));
    }

    default boolean hasLimit1() {
        if (this instanceof Select) {
            Select<?> q = (Select) this;
            SelectQueryImpl<?> s = Tools.selectQueryImpl(q);
            if (s != null) {
                Limit l = s.getLimit();
                return (l.withTies() || l.percent() || !l.limitOne()) ? false : true;
            }
            return false;
        }
        return false;
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, ?> mapper(int fieldIndex) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldIndex);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(int fieldIndex, Configuration configuration, Class<? extends U> type) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldIndex, configuration, type);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(int fieldIndex, Converter<?, ? extends U> converter) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldIndex, converter);
        });
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, Record> mapper(int[] fieldIndexes) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldIndexes);
        });
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, ?> mapper(String fieldName) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldName);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(String fieldName, Configuration configuration, Class<? extends U> type) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldName, configuration, type);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(String fieldName, Converter<?, ? extends U> converter) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldName, converter);
        });
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, Record> mapper(String[] fieldNames) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldNames);
        });
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, ?> mapper(Name fieldName) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldName);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(Name fieldName, Configuration configuration, Class<? extends U> type) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldName, configuration, type);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(Name fieldName, Converter<?, ? extends U> converter) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldName, converter);
        });
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, Record> mapper(Name[] fieldNames) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(fieldNames);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <T> RecordMapper<R, T> mapper(Field<T> field) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(field);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <U> RecordMapper<R, U> mapper(Field<?> field, Configuration configuration, Class<? extends U> type) {
        return new DelayedRecordMapper(t -> {
            return t.mapper((Field<?>) field, configuration, type);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <T, U> RecordMapper<R, U> mapper(Field<T> field, Converter<? super T, ? extends U> converter) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(field, converter);
        });
    }

    @Override // org.jooq.impl.Mappable
    default RecordMapper<R, Record> mapper(Field<?>[] fields) {
        return new DelayedRecordMapper(t -> {
            return t.mapper((Field<?>[]) fields);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <S extends Record> RecordMapper<R, S> mapper(Table<S> table) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(table);
        });
    }

    @Override // org.jooq.impl.Mappable
    default <E> RecordMapper<R, E> mapper(Configuration configuration, Class<? extends E> type) {
        return new DelayedRecordMapper(t -> {
            return t.mapper(configuration, type);
        });
    }

    default Field<?>[] getFields(ThrowingSupplier<? extends ResultSetMetaData, SQLException> rs) throws SQLException {
        return getFields();
    }

    @Override // org.jooq.Fields
    default Row fieldsRow() {
        return Tools.row0(getFields());
    }
}
