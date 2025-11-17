package org.jooq.impl;

import java.util.Map;
import org.jooq.CaseConditionStep;
import org.jooq.CaseWhenStep;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CaseSimple.class */
final class CaseSimple<V, T> extends AbstractCaseSimple<V, T, CaseSimple<V, T>> implements CaseWhenStep<V, T>, QOM.CaseSimple<V, T> {
    @Override // org.jooq.CaseWhenStep
    public /* bridge */ /* synthetic */ CaseWhenStep mapFields(Map map) {
        return (CaseWhenStep) super.mapFields(map);
    }

    @Override // org.jooq.CaseWhenStep
    public /* bridge */ /* synthetic */ CaseWhenStep mapValues(Map map) {
        return (CaseWhenStep) super.mapValues(map);
    }

    @Override // org.jooq.CaseWhenStep
    public /* bridge */ /* synthetic */ CaseWhenStep when(Field field, Field field2) {
        return (CaseWhenStep) super.when(field, field2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CaseWhenStep
    public /* bridge */ /* synthetic */ CaseWhenStep when(Field field, Object obj) {
        return (CaseWhenStep) super.when(field, (Field) obj);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CaseWhenStep
    public /* bridge */ /* synthetic */ CaseWhenStep when(Object obj, Field field) {
        return (CaseWhenStep) super.when((CaseSimple<V, T>) obj, field);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CaseWhenStep
    public /* bridge */ /* synthetic */ CaseWhenStep when(Object obj, Object obj2) {
        return (CaseWhenStep) super.when((CaseSimple<V, T>) obj, obj2);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ QOM.UOperator3 $arg3(Object obj) {
        return (QOM.UOperator3) super.$arg3((Field) obj);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ QOM.UOperator3 $arg2(Object obj) {
        return (QOM.UOperator3) super.$arg2((QOM.UnmodifiableList) obj);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ QOM.UOperator3 $arg1(Object obj) {
        return (QOM.UOperator3) super.$arg1((Field) obj);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ Object $arg3() {
        return super.$arg3();
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ Object $arg2() {
        return super.$arg2();
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public /* bridge */ /* synthetic */ Object $arg1() {
        return super.$arg1();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaseSimple(Field<V> value, Field<V> compareValue, Field<T> result) {
        super(Names.NQ_CASE, value, compareValue, result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaseSimple(Field<V> value, Map<? extends Field<V>, ? extends Field<T>> map) {
        super(Names.NQ_CASE, value, map);
    }

    CaseSimple(Field<V> value, DataType<T> type) {
        super(Names.NQ_CASE, value, type);
    }

    @Override // org.jooq.CaseWhenStep
    public final Field<T> otherwise(T result) {
        return else_((CaseSimple<V, T>) result);
    }

    @Override // org.jooq.CaseWhenStep
    public final Field<T> otherwise(Field<T> result) {
        return else_((Field) result);
    }

    @Override // org.jooq.impl.AbstractCaseSimple
    final void accept0(Context<?> ctx) {
        switch (ctx.family()) {
            case DERBY:
                acceptSearched(ctx);
                return;
            default:
                acceptNative(ctx);
                return;
        }
    }

    private final void acceptSearched(Context<?> ctx) {
        CaseConditionStep<T> w = null;
        for (QOM.Tuple2<Field<V>, Field<T>> e : this.when) {
            if (w == null) {
                w = DSL.when(this.value.eq(e.$1()), (Field) e.$2());
            } else {
                w = w.when(this.value.eq(e.$1()), (Field) e.$2());
            }
        }
        if (w != null) {
            if (this.else_ != null) {
                ctx.visit(w.else_((Field) this.else_));
            } else {
                ctx.visit((Field<?>) w);
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    private final void acceptNative(Context<?> ctx) {
        ctx.visit(Keywords.K_CASE);
        ctx.sql(' ').visit(this.value).formatIndentStart();
        for (QOM.Tuple2<Field<V>, Field<T>> e : this.when) {
            ctx.formatSeparator().visit(Keywords.K_WHEN).sql(' ').visit((Field<?>) e.$1()).sql(' ').visit(Keywords.K_THEN).sql(' ').visit((Field<?>) e.$2());
        }
        if (this.else_ != null) {
            ctx.formatSeparator().visit(Keywords.K_ELSE).sql(' ').visit((Field<?>) this.else_);
        } else if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_FORCE_CASE_ELSE_NULL))) {
            ctx.formatSeparator().visit(Keywords.K_ELSE).sql(' ').visit(Keywords.K_NULL);
        }
        ctx.formatIndentEnd().formatSeparator().visit(Keywords.K_END);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.jooq.impl.AbstractCaseSimple
    public final CaseSimple<V, T> construct(Field<V> v, DataType<T> t) {
        return new CaseSimple<>(v, t);
    }
}
