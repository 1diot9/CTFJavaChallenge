package org.jooq.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.GroupField;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.WindowDefinition;
import org.jooq.WindowExcludeStep;
import org.jooq.WindowFinalStep;
import org.jooq.WindowFromFirstLastStep;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowOrderByStep;
import org.jooq.WindowOverStep;
import org.jooq.WindowPartitionByStep;
import org.jooq.WindowRowsAndStep;
import org.jooq.WindowRowsStep;
import org.jooq.WindowSpecification;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractWindowFunction.class */
abstract class AbstractWindowFunction<T> extends AbstractField<T> implements WindowFromFirstLastStep<T>, WindowPartitionByStep<T>, WindowRowsStep<T>, WindowRowsAndStep<T>, WindowExcludeStep<T>, QOM.WindowFunction<T>, ScopeMappable {
    private static final Set<SQLDialect> SUPPORT_NO_PARENS_WINDOW_REFERENCE = SQLDialect.supportedBy(SQLDialect.MYSQL, SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    WindowSpecificationImpl windowSpecification;
    WindowDefinitionImpl windowDefinition;
    Name windowName;
    QOM.NullTreatment nullTreatment;
    QOM.FromFirstOrLast fromFirstOrLast;

    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    public /* bridge */ /* synthetic */ WindowRowsStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractWindowFunction(Name name, DataType<T> type) {
        super(name, type);
    }

    final QueryPart window(Context<?> ctx) {
        if (this.windowSpecification != null) {
            return CustomQueryPart.of(c -> {
                c.sql('(').visit(this.windowSpecification).sql(')');
            });
        }
        if (this.windowDefinition != null) {
            if (SUPPORT_NO_PARENS_WINDOW_REFERENCE.contains(ctx.dialect())) {
                return this.windowDefinition;
            }
            return CustomQueryPart.of(c2 -> {
                c2.sql('(').visit(this.windowDefinition).sql(')');
            });
        }
        if (this.windowName != null) {
            if (!SelectQueryImpl.NO_SUPPORT_WINDOW_CLAUSE.contains(ctx.dialect())) {
                return this.windowName;
            }
            QueryPartList<WindowDefinition> windows = (QueryPartList) ctx.data(Tools.SimpleDataKey.DATA_WINDOW_DEFINITIONS);
            if (windows != null) {
                Iterator<T> it = windows.iterator();
                while (it.hasNext()) {
                    WindowDefinition window = (WindowDefinition) it.next();
                    if (((WindowDefinitionImpl) window).getName().equals(this.windowName)) {
                        return CustomQueryPart.of(c3 -> {
                            c3.sql('(').visit(window).sql(')');
                        });
                    }
                }
                return null;
            }
            return this.windowName;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isWindow() {
        return (this.windowSpecification == null && this.windowDefinition == null && this.windowName == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x0024, code lost:            r6.sql(' ').visit(org.jooq.impl.Keywords.K_OVER).sql(' ');        r6.data(org.jooq.impl.Tools.ExtendedDataKey.DATA_WINDOW_FUNCTION, r5, (v1) -> { // java.util.function.Consumer.accept(java.lang.Object):void            lambda$acceptOverClause$3(r3, v1);        });     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x004d, code lost:            return;     */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void acceptOverClause(org.jooq.Context<?> r6) {
        /*
            r5 = this;
            r0 = r5
            r1 = r6
            org.jooq.QueryPart r0 = r0.window(r1)
            r7 = r0
            r0 = r7
            if (r0 != 0) goto Lb
            return
        Lb:
            int[] r0 = org.jooq.impl.AbstractWindowFunction.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r6
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L24;
            }
        L24:
            r0 = r6
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            org.jooq.Keyword r1 = org.jooq.impl.Keywords.K_OVER
            org.jooq.Context r0 = r0.visit(r1)
            r1 = 32
            org.jooq.Context r0 = r0.sql(r1)
            r0 = r6
            org.jooq.impl.Tools$ExtendedDataKey r1 = org.jooq.impl.Tools.ExtendedDataKey.DATA_WINDOW_FUNCTION
            r2 = r5
            r3 = r7
            void r3 = (v1) -> { // java.util.function.Consumer.accept(java.lang.Object):void
                lambda$acceptOverClause$3(r3, v1);
            }
            org.jooq.Context r0 = r0.data(r1, r2, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractWindowFunction.acceptOverClause(org.jooq.Context):void");
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    final void acceptNullTreatmentAsArgumentKeywords(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                if (this.nullTreatment == QOM.NullTreatment.IGNORE_NULLS) {
                    ctx.sql(' ').visit(Keywords.K_IGNORE_NULLS);
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void acceptNullTreatment(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                return;
            default:
                acceptNullTreatmentStandard(ctx);
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    public final void acceptNullTreatmentStandard(Context<?> ctx) {
        switch (ctx.family()) {
            case DUCKDB:
                return;
            default:
                if (this.nullTreatment == QOM.NullTreatment.IGNORE_NULLS) {
                    ctx.sql(' ').visit(Keywords.K_IGNORE_NULLS);
                    return;
                } else {
                    if (this.nullTreatment == QOM.NullTreatment.RESPECT_NULLS) {
                        ctx.sql(' ').visit(Keywords.K_RESPECT_NULLS);
                        return;
                    }
                    return;
                }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    public final void acceptFromFirstOrLast(Context<?> ctx) {
        switch (ctx.family()) {
            default:
                if (this.fromFirstOrLast == QOM.FromFirstOrLast.FROM_LAST) {
                    ctx.sql(' ').visit(Keywords.K_FROM).sql(' ').visit(Keywords.K_LAST);
                    return;
                } else {
                    if (this.fromFirstOrLast == QOM.FromFirstOrLast.FROM_FIRST) {
                        ctx.sql(' ').visit(Keywords.K_FROM).sql(' ').visit(Keywords.K_FIRST);
                        return;
                    }
                    return;
                }
        }
    }

    @Override // org.jooq.WindowIgnoreNullsStep
    public final WindowOverStep<T> ignoreNulls() {
        this.nullTreatment = QOM.NullTreatment.IGNORE_NULLS;
        return this;
    }

    @Override // org.jooq.WindowIgnoreNullsStep
    public final WindowOverStep<T> respectNulls() {
        this.nullTreatment = QOM.NullTreatment.RESPECT_NULLS;
        return this;
    }

    @Override // org.jooq.WindowFromFirstLastStep
    public final WindowIgnoreNullsStep<T> fromFirst() {
        this.fromFirstOrLast = QOM.FromFirstOrLast.FROM_FIRST;
        return this;
    }

    @Override // org.jooq.WindowFromFirstLastStep
    public final WindowIgnoreNullsStep<T> fromLast() {
        this.fromFirstOrLast = QOM.FromFirstOrLast.FROM_LAST;
        return this;
    }

    @Override // org.jooq.WindowOverStep
    public final WindowPartitionByStep<T> over() {
        this.windowSpecification = new WindowSpecificationImpl();
        return this;
    }

    @Override // org.jooq.WindowOverStep
    public final WindowFinalStep<T> over(WindowSpecification specification) {
        WindowSpecificationImpl windowSpecificationImpl;
        if (specification instanceof WindowSpecificationImpl) {
            WindowSpecificationImpl w = (WindowSpecificationImpl) specification;
            windowSpecificationImpl = w;
        } else {
            windowSpecificationImpl = new WindowSpecificationImpl((WindowDefinitionImpl) specification);
        }
        this.windowSpecification = windowSpecificationImpl;
        return this;
    }

    @Override // org.jooq.WindowOverStep
    public final WindowFinalStep<T> over(WindowDefinition definition) {
        this.windowDefinition = (WindowDefinitionImpl) definition;
        return this;
    }

    @Override // org.jooq.WindowOverStep
    public final WindowFinalStep<T> over(String n) {
        return over(DSL.name(n));
    }

    @Override // org.jooq.WindowOverStep
    public final WindowFinalStep<T> over(Name n) {
        this.windowName = n;
        return this;
    }

    @Override // org.jooq.WindowPartitionByStep
    public final WindowOrderByStep<T> partitionBy(GroupField... fields) {
        this.windowSpecification.partitionBy(fields);
        return this;
    }

    @Override // org.jooq.WindowPartitionByStep
    public final WindowOrderByStep<T> partitionBy(Collection<? extends GroupField> fields) {
        this.windowSpecification.partitionBy(fields);
        return this;
    }

    @Override // org.jooq.WindowPartitionByStep
    @Deprecated
    public final WindowOrderByStep<T> partitionByOne() {
        this.windowSpecification.partitionByOne();
        return this;
    }

    public AbstractWindowFunction<T> orderBy(OrderField<?>... fields) {
        this.windowSpecification.orderBy(fields);
        return this;
    }

    public AbstractWindowFunction<T> orderBy(Collection<? extends OrderField<?>> fields) {
        this.windowSpecification.orderBy(fields);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rowsUnboundedPreceding() {
        this.windowSpecification.rowsUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rowsPreceding(int number) {
        this.windowSpecification.rowsPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rowsCurrentRow() {
        this.windowSpecification.rowsCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rowsUnboundedFollowing() {
        this.windowSpecification.rowsUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rowsFollowing(int number) {
        this.windowSpecification.rowsFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rowsBetweenUnboundedPreceding() {
        this.windowSpecification.rowsBetweenUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rowsBetweenPreceding(int number) {
        this.windowSpecification.rowsBetweenPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rowsBetweenCurrentRow() {
        this.windowSpecification.rowsBetweenCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rowsBetweenUnboundedFollowing() {
        this.windowSpecification.rowsBetweenUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rowsBetweenFollowing(int number) {
        this.windowSpecification.rowsBetweenFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rangeUnboundedPreceding() {
        this.windowSpecification.rangeUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rangePreceding(int number) {
        this.windowSpecification.rangePreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rangeCurrentRow() {
        this.windowSpecification.rangeCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rangeUnboundedFollowing() {
        this.windowSpecification.rangeUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> rangeFollowing(int number) {
        this.windowSpecification.rangeFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rangeBetweenUnboundedPreceding() {
        this.windowSpecification.rangeBetweenUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rangeBetweenPreceding(int number) {
        this.windowSpecification.rangeBetweenPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rangeBetweenCurrentRow() {
        this.windowSpecification.rangeBetweenCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rangeBetweenUnboundedFollowing() {
        this.windowSpecification.rangeBetweenUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> rangeBetweenFollowing(int number) {
        this.windowSpecification.rangeBetweenFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> groupsUnboundedPreceding() {
        this.windowSpecification.groupsUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> groupsPreceding(int number) {
        this.windowSpecification.groupsPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> groupsCurrentRow() {
        this.windowSpecification.groupsCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> groupsUnboundedFollowing() {
        this.windowSpecification.groupsUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowExcludeStep<T> groupsFollowing(int number) {
        this.windowSpecification.groupsFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> groupsBetweenUnboundedPreceding() {
        this.windowSpecification.groupsBetweenUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> groupsBetweenPreceding(int number) {
        this.windowSpecification.groupsBetweenPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> groupsBetweenCurrentRow() {
        this.windowSpecification.groupsBetweenCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> groupsBetweenUnboundedFollowing() {
        this.windowSpecification.groupsBetweenUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsStep
    public final WindowRowsAndStep<T> groupsBetweenFollowing(int number) {
        this.windowSpecification.groupsBetweenFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowRowsAndStep
    public final WindowExcludeStep<T> andUnboundedPreceding() {
        this.windowSpecification.andUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowRowsAndStep
    public final WindowExcludeStep<T> andPreceding(int number) {
        this.windowSpecification.andPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowRowsAndStep
    public final WindowExcludeStep<T> andCurrentRow() {
        this.windowSpecification.andCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowRowsAndStep
    public final WindowExcludeStep<T> andUnboundedFollowing() {
        this.windowSpecification.andUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowRowsAndStep
    public final WindowExcludeStep<T> andFollowing(int number) {
        this.windowSpecification.andFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowExcludeStep
    public final WindowFinalStep<T> excludeCurrentRow() {
        this.windowSpecification.excludeCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowExcludeStep
    public final WindowFinalStep<T> excludeGroup() {
        this.windowSpecification.excludeGroup();
        return this;
    }

    @Override // org.jooq.WindowExcludeStep
    public final WindowFinalStep<T> excludeTies() {
        this.windowSpecification.excludeTies();
        return this;
    }

    @Override // org.jooq.WindowExcludeStep
    public final WindowFinalStep<T> excludeNoOthers() {
        this.windowSpecification.excludeNoOthers();
        return this;
    }

    @Override // org.jooq.impl.QOM.WindowFunction
    public final WindowSpecification $windowSpecification() {
        return this.windowSpecification;
    }

    final AbstractWindowFunction<T> $windowSpecification(WindowSpecification s) {
        this.windowSpecification = (WindowSpecificationImpl) s;
        return this;
    }

    @Override // org.jooq.impl.QOM.WindowFunction
    public final WindowDefinition $windowDefinition() {
        return this.windowDefinition;
    }

    final AbstractWindowFunction<T> $windowDefinition(WindowDefinition d) {
        this.windowDefinition = (WindowDefinitionImpl) d;
        return this;
    }

    public final QOM.NullTreatment $nullTreatment() {
        return this.nullTreatment;
    }

    final AbstractWindowFunction<T> $nullTreatment(QOM.NullTreatment n) {
        this.nullTreatment = n;
        return this;
    }

    public final QOM.FromFirstOrLast $fromFirstOrLast() {
        return this.fromFirstOrLast;
    }

    final AbstractWindowFunction<T> $fromFirstOrLast(QOM.FromFirstOrLast f) {
        this.fromFirstOrLast = f;
        return this;
    }
}
