package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.JSONArrayNullStep;
import org.jooq.JSONArrayReturningStep;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONArray.class */
public final class JSONArray<T> extends AbstractField<T> implements QOM.JSONArray<T>, JSONArrayNullStep<T>, JSONArrayReturningStep<T> {
    final DataType<T> type;
    final QueryPartListView<? extends Field<?>> fields;
    QOM.JSONOnNull onNull;
    DataType<?> returning;

    @Override // org.jooq.JSONArrayReturningStep
    public /* bridge */ /* synthetic */ Field returning(DataType dataType) {
        return returning((DataType<?>) dataType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONArray(DataType<T> type, Collection<? extends Field<?>> fields) {
        this(type, fields, null, null);
    }

    JSONArray(DataType<T> type, Collection<? extends Field<?>> fields, QOM.JSONOnNull onNull, DataType<?> returning) {
        super(Names.N_JSON_ARRAY, type);
        this.type = type;
        this.fields = new QueryPartList(fields);
        this.onNull = onNull;
        this.returning = returning;
    }

    @Override // org.jooq.JSONArrayNullStep
    public final JSONArray<T> nullOnNull() {
        this.onNull = QOM.JSONOnNull.NULL_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONArrayNullStep
    public final JSONArray<T> absentOnNull() {
        this.onNull = QOM.JSONOnNull.ABSENT_ON_NULL;
        return this;
    }

    @Override // org.jooq.JSONArrayReturningStep
    public final JSONArray<T> returning(DataType<?> returning) {
        this.returning = returning;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractField
    public final boolean isNullable() {
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x01ac, code lost:            r8.visit((org.jooq.Field<?>) r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:30:?, code lost:            return;     */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void accept(org.jooq.Context<?> r8) {
        /*
            Method dump skipped, instructions count: 441
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.JSONArray.accept(org.jooq.Context):void");
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final DataType<T> $arg1() {
        return this.type;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.UnmodifiableList<? extends Field<?>> $arg2() {
        return QOM.unmodifiable((List) this.fields);
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
    public final QOM.JSONArray<T> $arg1(DataType<T> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONArray<T> $arg2(QOM.UnmodifiableList<? extends Field<?>> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3(), $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONArray<T> $arg3(QOM.JSONOnNull newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue, $arg4());
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final QOM.JSONArray<T> $arg4(DataType<?> newValue) {
        return $constructor().apply($arg1(), $arg2(), $arg3(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator4
    public final Function4<? super DataType<T>, ? super Collection<? extends Field<?>>, ? super QOM.JSONOnNull, ? super DataType<?>, ? extends QOM.JSONArray<T>> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new JSONArray(a1, a2, a3, a4);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.JSONArray)) {
            return super.equals(that);
        }
        QOM.JSONArray<?> o = (QOM.JSONArray) that;
        return StringUtils.equals($type(), o.$type()) && StringUtils.equals($fields(), o.$fields()) && StringUtils.equals($onNull(), o.$onNull()) && StringUtils.equals($returning(), o.$returning());
    }
}
