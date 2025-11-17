package org.jooq.impl;

import java.util.List;
import org.jooq.CaseConditionStep;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function2;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CaseSearched.class */
public final class CaseSearched<T> extends AbstractField<T> implements CaseConditionStep<T>, QOM.CaseSearched<T> {
    private final List<QOM.Tuple2<Condition, Field<T>>> when;
    private Field<T> else_;

    /* renamed from: org.jooq.impl.CaseSearched$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CaseSearched$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaseSearched(DataType<T> type) {
        super(Names.NQ_CASE, type);
        this.when = new QueryPartList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaseSearched(Condition condition, Field<T> result) {
        this(result.getDataType());
        when(condition, (Field) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final CaseConditionStep<T> when(Condition condition, T result) {
        return when(condition, (Field) Tools.field(result));
    }

    @Override // org.jooq.CaseConditionStep
    public final CaseConditionStep<T> when(Condition condition, Field<T> result) {
        this.when.add(QOM.tuple(condition, result));
        return this;
    }

    @Override // org.jooq.CaseConditionStep
    public final CaseConditionStep<T> when(Condition condition, Select<? extends Record1<T>> result) {
        return when(condition, (Field) DSL.field(result));
    }

    @Override // org.jooq.CaseConditionStep
    public final CaseConditionStep<T> when(Field<Boolean> condition, T result) {
        return when(DSL.condition(condition), (Condition) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final CaseConditionStep<T> when(Field<Boolean> condition, Field<T> result) {
        return when(DSL.condition(condition), (Field) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final CaseConditionStep<T> when(Field<Boolean> condition, Select<? extends Record1<T>> result) {
        return when(DSL.condition(condition), (Select) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final Field<T> otherwise(T result) {
        return else_((CaseSearched<T>) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final Field<T> otherwise(Field<T> result) {
        return else_((Field) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final Field<T> otherwise(Select<? extends Record1<T>> result) {
        return else_((Select) result);
    }

    @Override // org.jooq.CaseConditionStep
    public final Field<T> else_(T result) {
        return else_((Field) Tools.field(result));
    }

    @Override // org.jooq.CaseConditionStep
    public final Field<T> else_(Field<T> result) {
        this.else_ = result;
        return this;
    }

    @Override // org.jooq.CaseConditionStep
    public final Field<T> else_(Select<? extends Record1<T>> result) {
        return else_((Field) DSL.field(result));
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x004c, code lost:            acceptNative(r5);     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0051, code lost:            return;     */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void accept(org.jooq.Context<?> r5) {
        /*
            r4 = this;
            r0 = r4
            java.util.List<org.jooq.impl.QOM$Tuple2<org.jooq.Condition, org.jooq.Field<T>>> r0 = r0.when
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L33
            r0 = r4
            org.jooq.Field<T> r0 = r0.else_
            if (r0 == 0) goto L21
            r0 = r5
            r1 = r4
            org.jooq.Field<T> r1 = r1.else_
            org.jooq.Context r0 = r0.visit(r1)
            goto L51
        L21:
            r0 = r5
            r1 = 0
            r2 = r4
            org.jooq.DataType r2 = r2.getDataType()
            org.jooq.Param r1 = org.jooq.impl.DSL.inline(r1, r2)
            org.jooq.Context r0 = r0.visit(r1)
            goto L51
        L33:
            int[] r0 = org.jooq.impl.CaseSearched.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r5
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L4c;
            }
        L4c:
            r0 = r4
            r1 = r5
            r0.acceptNative(r1)
        L51:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.CaseSearched.accept(org.jooq.Context):void");
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    private final void acceptNative(Context<?> ctx) {
        ctx.visit(Keywords.K_CASE).formatIndentStart();
        for (QOM.Tuple2<Condition, Field<T>> e : this.when) {
            Condition c = e.$1();
            ctx.formatSeparator().visit(Keywords.K_WHEN).sql(' ').visit(c).sql(' ').visit(Keywords.K_THEN).sql(' ').visit((Field<?>) e.$2());
        }
        if (this.else_ != null) {
            ctx.formatSeparator().visit(Keywords.K_ELSE).sql(' ').visit((Field<?>) this.else_);
        } else if (Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_FORCE_CASE_ELSE_NULL))) {
            ctx.formatSeparator().visit(Keywords.K_ELSE).sql(' ').visit(Keywords.K_NULL);
        }
        ctx.formatIndentEnd().formatSeparator().visit(Keywords.K_END);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Function2<? super QOM.UnmodifiableList<? extends QOM.Tuple2<Condition, Field<T>>>, ? super Field<T>, ? extends CaseSearched<T>> $constructor() {
        return (w, e) -> {
            CaseSearched<T> r = new CaseSearched<>(getDataType());
            w.forEach(t -> {
                r.when((Condition) t.$1(), (Field) t.$2());
            });
            r.else_(e);
            return r;
        };
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final QOM.UnmodifiableList<? extends QOM.Tuple2<Condition, Field<T>>> $arg1() {
        return QOM.unmodifiable((List) this.when);
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final CaseSearched<T> $arg1(QOM.UnmodifiableList<? extends QOM.Tuple2<Condition, Field<T>>> w) {
        return $constructor().apply(w, $else());
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final Field<T> $arg2() {
        return this.else_;
    }

    @Override // org.jooq.impl.QOM.UOperator2
    public final CaseSearched<T> $arg2(Field<T> e) {
        return $constructor().apply($when(), e);
    }
}
