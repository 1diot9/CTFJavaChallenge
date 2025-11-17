package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.JSONEntry;
import org.jooq.JSONObjectNullStep;
import org.jooq.JSONObjectReturningStep;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.util.ClassUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONObject.class */
public final class JSONObject<T> extends AbstractField<T> implements QOM.JSONObject<T>, JSONObjectNullStep<T>, JSONObjectReturningStep<T> {
    final DataType<T> type;
    final QueryPartListView<? extends JSONEntry<?>> entries;
    QOM.JSONOnNull onNull;
    DataType<?> returning;

    @Override // org.jooq.JSONObjectReturningStep
    public /* bridge */ /* synthetic */ Field returning(DataType dataType) {
        return returning((DataType<?>) dataType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONObject(DataType<T> type, Collection<? extends JSONEntry<?>> entries) {
        this(type, entries, null, null);
    }

    JSONObject(DataType<T> type, Collection<? extends JSONEntry<?>> entries, QOM.JSONOnNull onNull, DataType<?> returning) {
        super(Names.N_JSON_OBJECT, type);
        this.type = type;
        this.entries = new QueryPartList(entries);
        this.onNull = onNull;
        this.returning = returning;
    }

    @Override // org.jooq.JSONObjectNullStep
    public final JSONObject<T> nullOnNull() {
        this.onNull = QOM.JSONOnNull.NULL_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONObjectNullStep
    public final JSONObject<T> absentOnNull() {
        this.onNull = QOM.JSONOnNull.ABSENT_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONObjectReturningStep
    public final JSONObject<T> returning(DataType<?> returning) {
        this.returning = returning;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX WARN: Type inference failed for: r0v39, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v48, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                    ctx.visit(DSL.unquotedName(getDataType().getType() == JSONB.class ? "jsonb_strip_nulls" : "json_strip_nulls")).sql('(');
                }
                ctx.visit(DSL.unquotedName(getDataType().getType() == JSONB.class ? "jsonb_build_object" : "json_build_object")).sql('(').visit(QueryPartCollectionView.wrap(this.entries)).sql(')');
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                    ctx.sql(')');
                    return;
                }
                return;
            case MARIADB:
                if (this.entries.size() > 1) {
                    ctx.visit(JSONEntryImpl.jsonMerge(ctx, "{}", (Field[]) Tools.map(this.entries, e -> {
                        return DSL.jsonObject((JSONEntry<?>[]) new JSONEntry[]{e});
                    }, x$0 -> {
                        return new Field[x$0];
                    })));
                    return;
                }
                if (!this.entries.isEmpty()) {
                    JSONEntry<?> first = this.entries.iterator().next();
                    if (isJSONArray(first.value())) {
                        ctx.visit((Field<?>) DSL.jsonObject((JSONEntry<?>[]) new JSONEntry[]{DSL.key(first.key()).value((Field) JSONEntryImpl.jsonMerge(ctx, ClassUtils.ARRAY_SUFFIX, first.value()))}));
                        return;
                    }
                }
                acceptStandard(ctx);
                return;
            case MYSQL:
                if (this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
                    Field<String> k = DSL.field(DSL.name("jt", "k"), SQLDataType.VARCHAR);
                    Field<JSON> o = DSL.field(DSL.name("j", "o"), SQLDataType.JSON);
                    ctx.visit(DSL.field(DSL.select(DSL.coalesce((Field) DSL.jsonObjectAgg(k, (Field<?>) DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSON, (Field<?>[]) new Field[]{o, DSL.concat((Field<?>[]) new Field[]{DSL.inline("$.\""), k, DSL.inline("\"")})})), (Field<?>[]) new Field[]{DSL.jsonObject()})).from(DSL.select(CustomField.of("o", SQLDataType.JSON, (Consumer<? super Context<?>>) c -> {
                        acceptStandard(c);
                    }).as((Field<?>) o)).asTable("j"), DSL.jsonTable((Field<JSON>) DSL.function(Names.N_JSON_KEYS, SQLDataType.JSON, o), DSL.inline("$[*]")).column("k", SQLDataType.VARCHAR).path(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX).asTable("jt")).where(DSL.function(Names.N_JSON_EXTRACT, SQLDataType.JSON, (Field<?>[]) new Field[]{o, DSL.concat((Field<?>[]) new Field[]{DSL.inline("$.\""), k, DSL.inline("\"")})}).ne((Field) DSL.inline("null").cast(SQLDataType.JSON)))));
                    return;
                }
                acceptStandard(ctx);
                return;
            case TRINO:
                ctx.visit(DSL.function(Names.N_MAP_FROM_ENTRIES, SQLDataType.JSON, absentOnNullIf(() -> {
                    return this.onNull == QOM.JSONOnNull.ABSENT_ON_NULL;
                }, e2 -> {
                    return DSL.field("{0}[2]", (DataType) e2.getDataType(), e2);
                }, DSL.array(Tools.map(this.entries, e3 -> {
                    return DSL.function(Names.N_ROW, SQLDataType.JSON, (Field<?>[]) new Field[]{e3.key(), JSONEntryImpl.jsonCast(ctx, e3.value()).cast(SQLDataType.JSON)});
                })))).cast(SQLDataType.JSON));
                return;
            default:
                acceptStandard(ctx);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> absentOnNullIf(BooleanSupplier test, org.jooq.Function1<Field<?>, Field<?>> e, Field<?> array) {
        if (test.getAsBoolean()) {
            return DSL.function(Names.N_FILTER, array.getDataType(), (Field<?>[]) new Field[]{array, DSL.field("e -> {0}", (DataType) SQLDataType.BOOLEAN, e.apply(DSL.field(DSL.raw("e"), array.getDataType())).isNotNull())});
        }
        return array;
    }

    private static final boolean isJSONArray(Field<?> field) {
        return (field instanceof JSONArray) || (field instanceof JSONArrayAgg) || ((field instanceof ScalarSubquery) && isJSONArray(((ScalarSubquery) field).query.getSelect().get(0)));
    }

    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    private final void acceptStandard(Context<?> ctx) {
        JSONNull jsonNull;
        if (this.entries.isEmpty() && ctx.family() == SQLDialect.H2) {
            jsonNull = new JSONNull(QOM.JSONOnNull.NULL_ON_NULL);
        } else if (this.entries.isEmpty() && JSONNull.NO_SUPPORT_NULL_ON_EMPTY.contains(ctx.dialect())) {
            jsonNull = new JSONNull(null);
        } else {
            jsonNull = new JSONNull(this.onNull);
        }
        JSONReturning jsonReturning = new JSONReturning(this.returning);
        ctx.visit(Names.N_JSON_OBJECT).sql('(').visit(QueryPartListView.wrap(QueryPartCollectionView.wrap(this.entries), jsonNull, jsonReturning).separator("")).sql(')');
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final DataType<T> $arg1() {
        return this.type;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.UnmodifiableList<? extends JSONEntry<?>> $arg2() {
        return QOM.unmodifiable((List) this.entries);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONOnNull $arg3() {
        return this.onNull;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final DataType<?> $arg4() {
        return this.returning;
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONObject<T> $arg1(DataType<T> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONObject<T> $arg2(QOM.UnmodifiableList<? extends JSONEntry<?>> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONObject<T> $arg3(QOM.JSONOnNull newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue, $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONObject<T> $arg4(DataType<?> newValue) {
        return $constructor().apply($arg1(), $arg2(), $arg3(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Function4<? super DataType<T>, ? super Collection<? extends JSONEntry<?>>, ? super QOM.JSONOnNull, ? super DataType<?>, ? extends QOM.JSONObject<T>> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new JSONObject(a1, a2, a3, a4);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONObject)) {
            return super.equals(that);
        }
        QOM.JSONObject<?> o = (QOM.JSONObject) that;
        return StringUtils.equals($type(), o.$type()) && StringUtils.equals($entries(), o.$entries()) && StringUtils.equals($onNull(), o.$onNull()) && StringUtils.equals($returning(), o.$returning());
    }
}
