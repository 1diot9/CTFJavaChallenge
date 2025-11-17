package org.jooq.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jooq.Context;
import org.jooq.GroupField;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.SortField;
import org.jooq.WindowDefinition;
import org.jooq.WindowSpecification;
import org.jooq.WindowSpecificationExcludeStep;
import org.jooq.WindowSpecificationRowsAndStep;
import org.jooq.WindowSpecificationRowsStep;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WindowDefinitionImpl.class */
public final class WindowDefinitionImpl extends AbstractQueryPart implements WindowDefinition {
    private final Name name;
    private final WindowSpecification window;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowDefinitionImpl(Name name, WindowSpecification window) {
        this.name = name;
        this.window = window;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Name getName() {
        return this.name;
    }

    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx.declareWindows()) {
            ctx.visit(this.name).sql(' ').visit(Keywords.K_AS).sql(" (");
            if (this.window != null) {
                ctx.visit(this.window);
            }
            ctx.sql(')');
            return;
        }
        if (!SelectQueryImpl.NO_SUPPORT_WINDOW_CLAUSE.contains(ctx.dialect())) {
            ctx.visit(this.name);
            return;
        }
        if (this.window != null) {
            ctx.visit(this.window);
            return;
        }
        QueryPartList<WindowDefinition> windows = (QueryPartList) ctx.data(Tools.SimpleDataKey.DATA_WINDOW_DEFINITIONS);
        if (windows != null) {
            Iterator<WindowDefinition> it = windows.iterator();
            while (it.hasNext()) {
                WindowDefinition w = it.next();
                if (((WindowDefinitionImpl) w).getName().equals(this.name)) {
                    if (w != this) {
                        ctx.visit(w);
                        return;
                    }
                    return;
                }
            }
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean declaresWindows() {
        return true;
    }

    @Override // org.jooq.WindowSpecificationOrderByStep
    public final WindowSpecificationRowsStep orderBy(OrderField<?>... fields) {
        return new WindowSpecificationImpl(this).orderBy(fields);
    }

    @Override // org.jooq.WindowSpecificationOrderByStep
    public final WindowSpecificationRowsStep orderBy(Collection<? extends OrderField<?>> fields) {
        return new WindowSpecificationImpl(this).orderBy(fields);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsUnboundedPreceding() {
        return new WindowSpecificationImpl(this).rowsUnboundedPreceding();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsPreceding(int number) {
        return new WindowSpecificationImpl(this).rowsPreceding(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsCurrentRow() {
        return new WindowSpecificationImpl(this).rowsCurrentRow();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsUnboundedFollowing() {
        return new WindowSpecificationImpl(this).rowsUnboundedFollowing();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsFollowing(int number) {
        return new WindowSpecificationImpl(this).rowsFollowing(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenUnboundedPreceding() {
        return new WindowSpecificationImpl(this).rowsBetweenUnboundedPreceding();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenPreceding(int number) {
        return new WindowSpecificationImpl(this).rowsBetweenPreceding(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenCurrentRow() {
        return new WindowSpecificationImpl(this).rowsBetweenCurrentRow();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenUnboundedFollowing() {
        return new WindowSpecificationImpl(this).rowsBetweenUnboundedFollowing();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenFollowing(int number) {
        return new WindowSpecificationImpl(this).rowsBetweenFollowing(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeUnboundedPreceding() {
        return new WindowSpecificationImpl(this).rangeUnboundedPreceding();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangePreceding(int number) {
        return new WindowSpecificationImpl(this).rangePreceding(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeCurrentRow() {
        return new WindowSpecificationImpl(this).rangeCurrentRow();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeUnboundedFollowing() {
        return new WindowSpecificationImpl(this).rangeUnboundedFollowing();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeFollowing(int number) {
        return new WindowSpecificationImpl(this).rangeFollowing(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenUnboundedPreceding() {
        return new WindowSpecificationImpl(this).rangeBetweenUnboundedPreceding();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenPreceding(int number) {
        return new WindowSpecificationImpl(this).rangeBetweenPreceding(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenCurrentRow() {
        return new WindowSpecificationImpl(this).rangeBetweenCurrentRow();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenUnboundedFollowing() {
        return new WindowSpecificationImpl(this).rangeBetweenUnboundedFollowing();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenFollowing(int number) {
        return new WindowSpecificationImpl(this).rangeBetweenFollowing(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsUnboundedPreceding() {
        return new WindowSpecificationImpl(this).groupsUnboundedPreceding();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsPreceding(int number) {
        return new WindowSpecificationImpl(this).groupsPreceding(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsCurrentRow() {
        return new WindowSpecificationImpl(this).groupsCurrentRow();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsUnboundedFollowing() {
        return new WindowSpecificationImpl(this).groupsUnboundedFollowing();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsFollowing(int number) {
        return new WindowSpecificationImpl(this).groupsFollowing(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenUnboundedPreceding() {
        return new WindowSpecificationImpl(this).groupsBetweenUnboundedPreceding();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenPreceding(int number) {
        return new WindowSpecificationImpl(this).groupsBetweenPreceding(number);
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenCurrentRow() {
        return new WindowSpecificationImpl(this).groupsBetweenCurrentRow();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenUnboundedFollowing() {
        return new WindowSpecificationImpl(this).groupsBetweenUnboundedFollowing();
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenFollowing(int number) {
        return new WindowSpecificationImpl(this).groupsBetweenFollowing(number);
    }

    @Override // org.jooq.WindowDefinition
    public final Name $name() {
        return this.name;
    }

    @Override // org.jooq.WindowDefinition
    public final WindowSpecification $windowSpecification() {
        return this.window;
    }

    @Override // org.jooq.WindowSpecification
    public final WindowDefinition $windowDefinition() {
        return this;
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.UnmodifiableList<? extends GroupField> $partitionBy() {
        return QOM.unmodifiable((List) ($windowSpecification() == null ? new QueryPartList<>() : $windowSpecification().$partitionBy()));
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return QOM.unmodifiable((List) ($windowSpecification() == null ? new QueryPartList<>() : $windowSpecification().$orderBy()));
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.FrameUnits $frameUnits() {
        return (QOM.FrameUnits) Tools.apply($windowSpecification(), t -> {
            return t.$frameUnits();
        });
    }

    @Override // org.jooq.WindowSpecification
    public final Integer $frameStart() {
        return (Integer) Tools.apply($windowSpecification(), t -> {
            return t.$frameStart();
        });
    }

    @Override // org.jooq.WindowSpecification
    public final Integer $frameEnd() {
        return (Integer) Tools.apply($windowSpecification(), t -> {
            return t.$frameEnd();
        });
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.FrameExclude $exclude() {
        return (QOM.FrameExclude) Tools.apply($windowSpecification(), t -> {
            return t.$exclude();
        });
    }
}
