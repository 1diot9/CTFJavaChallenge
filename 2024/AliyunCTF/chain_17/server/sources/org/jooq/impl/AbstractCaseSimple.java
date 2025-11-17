package org.jooq.impl;

import java.util.List;
import java.util.Map;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.impl.AbstractCaseSimple;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractCaseSimple.class */
abstract class AbstractCaseSimple<V, T, CS extends AbstractCaseSimple<V, T, CS>> extends AbstractField<T> {
    final Field<V> value;
    final List<QOM.Tuple2<Field<V>, Field<T>>> when;
    Field<T> else_;

    abstract void accept0(Context<?> context);

    abstract CS construct(Field<V> field, DataType<T> dataType);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCaseSimple(Name name, Field<V> value, Field<V> compareValue, Field<T> result) {
        this(name, value, result.getDataType());
        when((Field) compareValue, (Field) result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCaseSimple(Name name, Field<V> value, Map<? extends Field<V>, ? extends Field<T>> map) {
        this(name, value, dataType(map));
        mapFields(map);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractCaseSimple(Name name, Field<V> value, DataType<T> type) {
        super(name, type);
        this.value = value;
        this.when = new QueryPartList();
    }

    private static final <T> DataType<T> dataType(Map<? extends Field<?>, ? extends Field<T>> map) {
        if (map.isEmpty()) {
            return (DataType<T>) SQLDataType.OTHER;
        }
        return map.entrySet().iterator().next().getValue().getDataType();
    }

    public final CS when(V compareValue, T result) {
        return when((Field) Tools.field(compareValue, this.value), (Field) Tools.field(result));
    }

    public final CS when(V compareValue, Field<T> result) {
        return when((Field) Tools.field(compareValue, this.value), (Field) result);
    }

    public final CS when(Field<V> compareValue, T result) {
        return when((Field) compareValue, (Field) Tools.field(result));
    }

    public final CS when(Field<V> compareValue, Field<T> result) {
        this.when.add(QOM.tuple(compareValue, result));
        return this;
    }

    public final CS mapValues(Map<V, T> values) {
        values.forEach((obj, obj2) -> {
            when((AbstractCaseSimple<V, T, CS>) obj, obj2);
        });
        return this;
    }

    public final CS mapFields(Map<? extends Field<V>, ? extends Field<T>> fields) {
        fields.forEach((k, v) -> {
            when(k, v);
        });
        return this;
    }

    public final Field<T> else_(T result) {
        return else_((Field) Tools.field(result));
    }

    public final Field<T> else_(Field<T> result) {
        this.else_ = result;
        return this;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.when.isEmpty()) {
            if (this.else_ != null) {
                ctx.visit((Field<?>) this.else_);
                return;
            } else {
                ctx.visit(DSL.inline((Object) null, getDataType()));
                return;
            }
        }
        accept0(ctx);
    }

    public final Function3<? super Field<V>, ? super QOM.UnmodifiableList<? extends QOM.Tuple2<Field<V>, Field<T>>>, ? super Field<T>, ? extends CS> $constructor() {
        return (v, w, e) -> {
            CS r = construct(v, getDataType());
            w.forEach(t -> {
                r.when((Field) t.$1(), (Field) t.$2());
            });
            r.else_(e);
            return r;
        };
    }

    public final Field<V> $arg1() {
        return this.value;
    }

    public final CS $arg1(Field<V> newArg1) {
        return $constructor().apply(newArg1, $arg2(), $arg3());
    }

    public final QOM.UnmodifiableList<? extends QOM.Tuple2<Field<V>, Field<T>>> $arg2() {
        return QOM.unmodifiable((List) this.when);
    }

    public final CS $arg2(QOM.UnmodifiableList<? extends QOM.Tuple2<Field<V>, Field<T>>> w) {
        return $constructor().apply($arg1(), w, $arg3());
    }

    public final Field<T> $arg3() {
        return this.else_;
    }

    public final CS $arg3(Field<T> e) {
        return $constructor().apply($arg1(), $arg2(), e);
    }
}
