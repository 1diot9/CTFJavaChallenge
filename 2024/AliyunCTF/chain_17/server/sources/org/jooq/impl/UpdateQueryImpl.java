package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.Operator;
import org.jooq.OrderField;
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
import org.jooq.Row;
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
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.UniqueKey;
import org.jooq.UpdateQuery;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.FieldMapForUpdate;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/UpdateQueryImpl.class */
public final class UpdateQueryImpl<R extends Record> extends AbstractStoreQuery<R, FieldOrRow, FieldOrRowOrSelect> implements UpdateQuery<R>, QOM.Update<R> {
    private static final Clause[] CLAUSES = {Clause.UPDATE};
    private static final Set<SQLDialect> EMULATE_FROM_WITH_MERGE = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB);
    private static final Set<SQLDialect> EMULATE_RETURNING_WITH_UPSERT = SQLDialect.supportedBy(SQLDialect.MARIADB);
    private static final Set<SQLDialect> NO_SUPPORT_LIMIT = SQLDialect.supportedUntil(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.DUCKDB, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> NO_SUPPORT_ORDER_BY_LIMIT = SQLDialect.supportedBy(SQLDialect.IGNITE);
    static final Set<SQLDialect> NO_SUPPORT_UPDATE_JOIN = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.IGNITE, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.YUGABYTEDB);
    private final FieldMapForUpdate updateMap;
    private final TableList from;
    private final ConditionProviderImpl condition;
    private final SortFieldList orderBy;
    private Field<? extends Number> limit;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UpdateQueryImpl(Configuration configuration, WithImpl with, Table<R> table) {
        super(configuration, with, table);
        this.updateMap = new FieldMapForUpdate(table, FieldMapForUpdate.SetClause.UPDATE, Clause.UPDATE_SET_ASSIGNMENT);
        this.from = new TableList();
        this.condition = new ConditionProviderImpl();
        this.orderBy = new SortFieldList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.jooq.impl.AbstractStoreQuery
    /* renamed from: getValues, reason: merged with bridge method [inline-methods] */
    public final Map<FieldOrRow, FieldOrRowOrSelect> getValues2() {
        return this.updateMap;
    }

    @Override // org.jooq.UpdateQuery
    public final void addValues(RowN row, RowN value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1> void addValues(Row1<T1> row, Row1<T1> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2> void addValues(Row2<T1, T2> row, Row2<T1, T2> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3> void addValues(Row3<T1, T2, T3> row, Row3<T1, T2, T3> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4> void addValues(Row4<T1, T2, T3, T4> row, Row4<T1, T2, T3, T4> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5> void addValues(Row5<T1, T2, T3, T4, T5> row, Row5<T1, T2, T3, T4, T5> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6> void addValues(Row6<T1, T2, T3, T4, T5, T6> row, Row6<T1, T2, T3, T4, T5, T6> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7> void addValues(Row7<T1, T2, T3, T4, T5, T6, T7> row, Row7<T1, T2, T3, T4, T5, T6, T7> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8> void addValues(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row, Row8<T1, T2, T3, T4, T5, T6, T7, T8> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> void addValues(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> void addValues(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> void addValues(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> void addValues(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> void addValues(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> void addValues(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row, Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> void addValues(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row, Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> void addValues(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row, Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> void addValues(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row, Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> void addValues(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row, Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> void addValues(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row, Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> void addValues(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row, Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> void addValues(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row, Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> void addValues(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row, Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> value) {
        addValues0(row, value);
    }

    @Override // org.jooq.UpdateQuery
    public final void addValues(RowN row, Select<? extends Record> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1> void addValues(Row1<T1> row, Select<? extends Record1<T1>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2> void addValues(Row2<T1, T2> row, Select<? extends Record2<T1, T2>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3> void addValues(Row3<T1, T2, T3> row, Select<? extends Record3<T1, T2, T3>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4> void addValues(Row4<T1, T2, T3, T4> row, Select<? extends Record4<T1, T2, T3, T4>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5> void addValues(Row5<T1, T2, T3, T4, T5> row, Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6> void addValues(Row6<T1, T2, T3, T4, T5, T6> row, Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7> void addValues(Row7<T1, T2, T3, T4, T5, T6, T7> row, Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8> void addValues(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row, Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> void addValues(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row, Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> void addValues(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row, Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> void addValues(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row, Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> void addValues(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row, Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> void addValues(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row, Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> void addValues(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row, Select<? extends Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> void addValues(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row, Select<? extends Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> void addValues(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row, Select<? extends Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> void addValues(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row, Select<? extends Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> void addValues(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row, Select<? extends Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> void addValues(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row, Select<? extends Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> void addValues(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row, Select<? extends Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> void addValues(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row, Select<? extends Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select) {
        addValues0(row, select);
    }

    @Override // org.jooq.UpdateQuery
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> void addValues(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row, Select<? extends Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select) {
        addValues0(row, select);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addValues0(Row row, Row value) {
        this.updateMap.put((FieldOrRow) row, (FieldOrRowOrSelect) value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addValues0(Row row, Select<?> select) {
        this.updateMap.put((FieldOrRow) row, (FieldOrRowOrSelect) select);
    }

    @Override // org.jooq.StoreQuery
    public final void addValues(Map<?, ?> map) {
        this.updateMap.set(map);
    }

    @Override // org.jooq.UpdateQuery
    public final void addFrom(Collection<? extends TableLike<?>> f) {
        for (TableLike<?> provider : f) {
            this.from.add((TableList) provider.asTable());
        }
    }

    @Override // org.jooq.UpdateQuery
    public final void addFrom(TableLike<?> f) {
        addFrom(Arrays.asList(f));
    }

    @Override // org.jooq.UpdateQuery
    public final void addFrom(TableLike<?>... f) {
        addFrom(Arrays.asList(f));
    }

    @Override // org.jooq.UpdateQuery, org.jooq.ConditionProvider
    public final void addConditions(Collection<? extends Condition> conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.UpdateQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.UpdateQuery, org.jooq.ConditionProvider
    public final void addConditions(Condition... conditions) {
        this.condition.addConditions(conditions);
    }

    @Override // org.jooq.UpdateQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.UpdateQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Condition... conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.UpdateQuery, org.jooq.ConditionProvider
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        this.condition.addConditions(operator, conditions);
    }

    @Override // org.jooq.UpdateQuery
    public final void addOrderBy(OrderField<?>... fields) {
        addOrderBy(Arrays.asList(fields));
    }

    @Override // org.jooq.UpdateQuery
    public final void addOrderBy(Collection<? extends OrderField<?>> fields) {
        this.orderBy.addAll(Tools.sortFields(fields));
    }

    @Override // org.jooq.UpdateQuery
    public final void addLimit(Number l) {
        addLimit(DSL.val(l));
    }

    @Override // org.jooq.UpdateQuery
    public final void addLimit(Field<? extends Number> l) {
        if (l instanceof NoField) {
            return;
        }
        this.limit = l;
    }

    final Condition getWhere() {
        return this.condition.getWhere();
    }

    final boolean hasWhere() {
        return this.condition.hasWhere();
    }

    final TableList getFrom() {
        return this.from;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.scopeStart(this);
        Table<?> t = table(ctx);
        if (InlineDerivedTable.hasInlineDerivedTables(ctx, t) || InlineDerivedTable.hasInlineDerivedTables(ctx, this.from)) {
            ConditionProviderImpl where = new ConditionProviderImpl();
            TableList f = InlineDerivedTable.transformInlineDerivedTables(ctx, this.from, where);
            copy(d -> {
                if (f != this.from) {
                    d.from.clear();
                    d.from.addAll(f);
                }
                if (where.hasWhere()) {
                    d.addConditionsForInlineDerivedTable(where);
                }
            }, InlineDerivedTable.transformInlineDerivedTables0(ctx, t, where, false)).accept0(ctx);
        } else {
            accept0(ctx);
        }
        ctx.scopeEnd();
    }

    private final void addConditionsForInlineDerivedTable(Condition c) {
        addConditions(c);
    }

    @Override // org.jooq.impl.AbstractDMLQuery
    final void accept1(Context<?> ctx) {
        if (!this.from.isEmpty() && EMULATE_FROM_WITH_MERGE.contains(ctx.dialect())) {
            acceptFromAsMerge(ctx);
            return;
        }
        if (!this.returning.isEmpty() && EMULATE_RETURNING_WITH_UPSERT.contains(ctx.dialect()) && (this.table instanceof TableImpl) && (((TableImpl) this.table).fields.fields.length == 0 || !this.table.getKeys().isEmpty())) {
            acceptReturningAsUpsert(ctx);
        } else {
            accept2(ctx);
        }
    }

    private final void acceptReturningAsUpsert(Context<?> ctx) {
        ctx.visit(DSL.insertInto(this.table).select(DSL.selectFrom(this.table).where(hasWhere() ? getWhere() : DSL.noCondition()).orderBy(this.orderBy).limit(this.limit != null ? this.limit : DSL.noField(SQLDataType.INTEGER))).onDuplicateKeyUpdate().set(this.updateMap).returning(this.returning));
    }

    private final void acceptFromAsMerge(Context<?> ctx) {
        Table<?> s;
        boolean patchSource = true;
        Condition c = this.condition;
        FieldMapForUpdate um = this.updateMap;
        if (this.orderBy.isEmpty() && this.limit == null) {
            if (this.from.size() == 1 && (this.from.get(0) instanceof TableImpl)) {
                patchSource = false;
                if (0 == 0) {
                    s = (Table) this.from.get(0);
                }
            }
            s = DSL.select(new SelectFieldOrAsterisk[0]).from(this.from).asTable("s");
        } else {
            s = DSL.select(this.from.fields()).from(this.from).join(this.table).on((Condition) this.condition).orderBy(this.orderBy).limit(this.limit).asTable("s");
        }
        if (!patchSource || ctx.configuration().requireCommercial(() -> {
            return "The UPDATE .. FROM to MERGE transformation requires commercial only logic for non-trivial FROM clauses. Please upgrade to the jOOQ Professional Edition or jOOQ Enterprise Edition";
        })) {
        }
        ctx.visit(DSL.mergeInto(this.table).using(s).on(c).whenMatchedThenUpdate().set(um));
    }

    final boolean updatesField(Field<?> field) {
        return Tools.anyMatch(this.updateMap.keySet(), fr -> {
            return field.equals(fr) || ((fr instanceof Row) && ((Row) fr).field(field) != null);
        });
    }

    final boolean updatesAnyField(Collection<? extends Field<?>> fields) {
        return Tools.anyMatch(fields, this::updatesField);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public final UpdateQueryImpl<R> copy(Consumer<? super UpdateQueryImpl<R>> consumer) {
        return (UpdateQueryImpl<R>) copy(consumer, this.table);
    }

    final <O extends Record> UpdateQueryImpl<O> copy(Consumer<? super UpdateQueryImpl<O>> finisher, Table<O> t) {
        UpdateQueryImpl<O> u = new UpdateQueryImpl<>(configuration(), this.with, t);
        if (!this.returning.isEmpty()) {
            u.setReturning(this.returning);
        }
        u.updateMap.putAll(this.updateMap);
        u.from.addAll(this.from);
        u.condition.setWhere(this.condition.getWhere());
        u.orderBy.addAll(this.orderBy);
        u.limit = this.limit;
        finisher.accept(u);
        return u;
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v40, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v45, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v75, types: [org.jooq.Context] */
    final void accept2(Context<?> ctx) {
        UniqueKey<R> uniqueKey;
        Field<?>[] fieldsArray;
        boolean declareTables = ctx.declareTables();
        DSL.noCondition();
        Table<?> t = table(ctx);
        ctx.start(Clause.UPDATE_UPDATE).visit(Keywords.K_UPDATE).sql(' ').declareTables(true).visit(t).declareTables(declareTables).end(Clause.UPDATE_UPDATE);
        ctx.formatSeparator().start(Clause.UPDATE_SET).visit(Keywords.K_SET).separatorRequired(true).formatIndentStart().formatSeparator().visit(this.updateMap).formatIndentEnd().end(Clause.UPDATE_SET);
        acceptFrom(ctx);
        if ((this.limit != null && NO_SUPPORT_LIMIT.contains(ctx.dialect())) || (!this.orderBy.isEmpty() && NO_SUPPORT_ORDER_BY_LIMIT.contains(ctx.dialect()))) {
            if (table().getKeys().isEmpty()) {
                fieldsArray = new Field[]{table().rowid()};
            } else {
                if (table().getPrimaryKey() != null) {
                    uniqueKey = table().getPrimaryKey();
                } else {
                    uniqueKey = table().getKeys().get(0);
                }
                fieldsArray = uniqueKey.getFieldsArray();
            }
            Field<?>[] keyFields = fieldsArray;
            ctx.start(Clause.UPDATE_WHERE).formatSeparator().visit(Keywords.K_WHERE).sql(' ');
            if (keyFields.length == 1) {
                ctx.visit(keyFields[0].in(DSL.select(keyFields[0]).from(table()).where(getWhere()).orderBy(this.orderBy).limit(this.limit)));
            } else {
                ctx.visit(DSL.row((SelectField<?>[]) keyFields).in(DSL.select(keyFields).from(table()).where(getWhere()).orderBy(this.orderBy).limit(this.limit)));
            }
            ctx.end(Clause.UPDATE_WHERE);
        } else {
            ctx.start(Clause.UPDATE_WHERE);
            if (hasWhere()) {
                ctx.formatSeparator().visit(Keywords.K_WHERE).sql(' ').visit(getWhere());
            }
            ctx.end(Clause.UPDATE_WHERE);
            if (!this.orderBy.isEmpty()) {
                ctx.formatSeparator().visit(Keywords.K_ORDER_BY).sql(' ').visit(this.orderBy);
            }
            DeleteQueryImpl.acceptLimit(ctx, this.limit);
        }
        ctx.start(Clause.UPDATE_RETURNING);
        toSQLReturning(ctx);
        ctx.end(Clause.UPDATE_RETURNING);
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    private final void acceptFrom(Context<?> ctx) {
        ctx.start(Clause.UPDATE_FROM);
        TableList f = new TableList();
        f.addAll(this.from);
        if (!f.isEmpty()) {
            ctx.formatSeparator().visit(Keywords.K_FROM).sql(' ').declareTables(true, c -> {
                c.visit(f);
            });
        }
        ctx.end(Clause.UPDATE_FROM);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.AbstractQuery, org.jooq.Query
    public final boolean isExecutable() {
        if (!this.condition.hasWhere()) {
            executeWithoutWhere("UPDATE without WHERE", SettingsTools.getExecuteUpdateWithoutWhere(configuration().settings()));
        }
        return this.updateMap.size() > 0;
    }

    @Override // org.jooq.impl.QOM.Update
    public final WithImpl $with() {
        return this.with;
    }

    @Override // org.jooq.impl.QOM.Update
    public final Table<R> $table() {
        return this.table;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<?> $table(Table<?> table) {
        if ($table() == table) {
            return this;
        }
        return copy(d -> {
        }, table);
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.UnmodifiableList<? extends Table<?>> $from() {
        return QOM.unmodifiable((List) this.from);
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $from(Collection<? extends Table<?>> newFrom) {
        return copy(d -> {
            d.from.clear();
            d.from.addAll(newFrom);
        });
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.UnmodifiableMap<? extends FieldOrRow, ? extends FieldOrRowOrSelect> $set() {
        return QOM.unmodifiable(this.updateMap);
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $set(Map<? extends FieldOrRow, ? extends FieldOrRowOrSelect> newSet) {
        return copy(u -> {
            u.updateMap.clear();
            u.updateMap.putAll(newSet);
        });
    }

    @Override // org.jooq.impl.QOM.Update
    public final Condition $where() {
        return this.condition.getWhereOrNull();
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $where(Condition newWhere) {
        if ($where() == newWhere) {
            return this;
        }
        return copy(u -> {
            u.condition.setWhere(newWhere);
        });
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return QOM.unmodifiable((List) this.orderBy);
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $orderBy(Collection<? extends SortField<?>> newOrderBy) {
        return copy(u -> {
            u.orderBy.clear();
            u.orderBy.addAll(newOrderBy);
        });
    }

    @Override // org.jooq.impl.QOM.Update
    public final Field<? extends Number> $limit() {
        return this.limit;
    }

    @Override // org.jooq.impl.QOM.Update
    public final QOM.Update<R> $limit(Field<? extends Number> newLimit) {
        if ($limit() == newLimit) {
            return this;
        }
        return copy(s -> {
            s.limit = newLimit;
        });
    }
}
