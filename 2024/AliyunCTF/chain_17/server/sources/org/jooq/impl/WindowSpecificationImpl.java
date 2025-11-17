package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.GroupField;
import org.jooq.OrderField;
import org.jooq.SQLDialect;
import org.jooq.SortField;
import org.jooq.WindowSpecificationExcludeStep;
import org.jooq.WindowSpecificationFinalStep;
import org.jooq.WindowSpecificationOrderByStep;
import org.jooq.WindowSpecificationPartitionByStep;
import org.jooq.WindowSpecificationRowsAndStep;
import org.jooq.WindowSpecificationRowsStep;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WindowSpecificationImpl.class */
public final class WindowSpecificationImpl extends AbstractQueryPart implements WindowSpecificationPartitionByStep, WindowSpecificationRowsAndStep, WindowSpecificationExcludeStep {
    private static final Set<SQLDialect> OMIT_PARTITION_BY_ONE = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.MYSQL, SQLDialect.SQLITE);
    private static final Set<SQLDialect> REQUIRES_ORDER_BY_IN_LEAD_LAG = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.TRINO);
    private static final Set<SQLDialect> REQUIRES_ORDER_BY_IN_NTILE = SQLDialect.supportedBy(SQLDialect.H2);
    private static final Set<SQLDialect> REQUIRES_ORDER_BY_IN_RANK_DENSE_RANK = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.MARIADB);
    private static final Set<SQLDialect> REQUIRES_ORDER_BY_IN_PERCENT_RANK_CUME_DIST = SQLDialect.supportedBy(SQLDialect.MARIADB);
    private final WindowDefinitionImpl windowDefinition;
    private final GroupFieldList partitionBy;
    private final SortFieldList orderBy;
    private Integer frameStart;
    private Integer frameEnd;
    private QOM.FrameUnits frameUnits;
    private QOM.FrameExclude exclude;
    private boolean partitionByOne;

    /* renamed from: org.jooq.impl.WindowSpecificationImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/WindowSpecificationImpl$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.WindowSpecificationPartitionByStep
    public /* bridge */ /* synthetic */ WindowSpecificationOrderByStep partitionBy(Collection collection) {
        return partitionBy((Collection<? extends GroupField>) collection);
    }

    @Override // org.jooq.WindowSpecificationOrderByStep
    public /* bridge */ /* synthetic */ WindowSpecificationRowsStep orderBy(Collection collection) {
        return orderBy((Collection<? extends OrderField<?>>) collection);
    }

    @Override // org.jooq.WindowSpecificationOrderByStep
    public /* bridge */ /* synthetic */ WindowSpecificationRowsStep orderBy(OrderField[] orderFieldArr) {
        return orderBy((OrderField<?>[]) orderFieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowSpecificationImpl() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowSpecificationImpl(WindowDefinitionImpl windowDefinition) {
        this.windowDefinition = windowDefinition;
        this.partitionBy = new GroupFieldList();
        this.orderBy = new SortFieldList();
    }

    WindowSpecificationImpl copy() {
        WindowSpecificationImpl copy = new WindowSpecificationImpl(this.windowDefinition);
        copy.partitionBy.addAll(this.partitionBy);
        copy.orderBy.addAll(this.orderBy);
        copy.frameStart = this.frameStart;
        copy.frameEnd = this.frameEnd;
        copy.frameUnits = this.frameUnits;
        copy.exclude = this.exclude;
        copy.partitionByOne = this.partitionByOne;
        return copy;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x00e4, code lost:            r0 = org.jooq.impl.DSL.field(org.jooq.impl.DSL.select(org.jooq.impl.DSL.one()));        r6 = new org.jooq.impl.SortFieldList();        r6.add((org.jooq.impl.SortFieldList) r0.sortDefault());     */
    /* JADX WARN: Type inference failed for: r0v40, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v59, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v68, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v78, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v86, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v96, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void accept(org.jooq.Context<?> r5) {
        /*
            Method dump skipped, instructions count: 712
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.WindowSpecificationImpl.accept(org.jooq.Context):void");
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    private final void toSQLRows(Context<?> ctx, Integer rows) {
        if (rows.intValue() == Integer.MIN_VALUE) {
            ctx.visit(Keywords.K_UNBOUNDED_PRECEDING);
            return;
        }
        if (rows.intValue() == Integer.MAX_VALUE) {
            ctx.visit(Keywords.K_UNBOUNDED_FOLLOWING);
            return;
        }
        if (rows.intValue() < 0) {
            ctx.sql(-rows.intValue()).sql(' ').visit(Keywords.K_PRECEDING);
        } else if (rows.intValue() > 0) {
            ctx.sql(rows.intValue()).sql(' ').visit(Keywords.K_FOLLOWING);
        } else {
            ctx.visit(Keywords.K_CURRENT_ROW);
        }
    }

    @Override // org.jooq.WindowSpecificationPartitionByStep
    public final WindowSpecificationPartitionByStep partitionBy(GroupField... fields) {
        return partitionBy((Collection<? extends GroupField>) Arrays.asList(fields));
    }

    @Override // org.jooq.WindowSpecificationPartitionByStep
    public final WindowSpecificationPartitionByStep partitionBy(Collection<? extends GroupField> fields) {
        this.partitionBy.addAll(fields);
        return this;
    }

    @Override // org.jooq.WindowSpecificationPartitionByStep
    @Deprecated
    public final WindowSpecificationOrderByStep partitionByOne() {
        this.partitionByOne = true;
        this.partitionBy.add((GroupFieldList) DSL.one());
        return this;
    }

    @Override // org.jooq.WindowSpecificationOrderByStep
    public final WindowSpecificationOrderByStep orderBy(OrderField<?>... fields) {
        return orderBy((Collection<? extends OrderField<?>>) Arrays.asList(fields));
    }

    @Override // org.jooq.WindowSpecificationOrderByStep
    public final WindowSpecificationOrderByStep orderBy(Collection<? extends OrderField<?>> fields) {
        this.orderBy.addAll(Tools.sortFields(fields));
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsUnboundedPreceding() {
        this.frameUnits = QOM.FrameUnits.ROWS;
        this.frameStart = Integer.MIN_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsPreceding(int number) {
        this.frameUnits = QOM.FrameUnits.ROWS;
        this.frameStart = Integer.valueOf(-number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsCurrentRow() {
        this.frameUnits = QOM.FrameUnits.ROWS;
        this.frameStart = 0;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsUnboundedFollowing() {
        this.frameUnits = QOM.FrameUnits.ROWS;
        this.frameStart = Integer.MAX_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rowsFollowing(int number) {
        this.frameUnits = QOM.FrameUnits.ROWS;
        this.frameStart = Integer.valueOf(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenUnboundedPreceding() {
        rowsUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenPreceding(int number) {
        rowsPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenCurrentRow() {
        rowsCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenUnboundedFollowing() {
        rowsUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rowsBetweenFollowing(int number) {
        rowsFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeUnboundedPreceding() {
        this.frameUnits = QOM.FrameUnits.RANGE;
        this.frameStart = Integer.MIN_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangePreceding(int number) {
        this.frameUnits = QOM.FrameUnits.RANGE;
        this.frameStart = Integer.valueOf(-number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeCurrentRow() {
        this.frameUnits = QOM.FrameUnits.RANGE;
        this.frameStart = 0;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeUnboundedFollowing() {
        this.frameUnits = QOM.FrameUnits.RANGE;
        this.frameStart = Integer.MAX_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep rangeFollowing(int number) {
        this.frameUnits = QOM.FrameUnits.RANGE;
        this.frameStart = Integer.valueOf(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenUnboundedPreceding() {
        rangeUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenPreceding(int number) {
        rangePreceding(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenCurrentRow() {
        rangeCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenUnboundedFollowing() {
        rangeUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep rangeBetweenFollowing(int number) {
        rangeFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsUnboundedPreceding() {
        this.frameUnits = QOM.FrameUnits.GROUPS;
        this.frameStart = Integer.MIN_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsPreceding(int number) {
        this.frameUnits = QOM.FrameUnits.GROUPS;
        this.frameStart = Integer.valueOf(-number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsCurrentRow() {
        this.frameUnits = QOM.FrameUnits.GROUPS;
        this.frameStart = 0;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsUnboundedFollowing() {
        this.frameUnits = QOM.FrameUnits.GROUPS;
        this.frameStart = Integer.MAX_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationExcludeStep groupsFollowing(int number) {
        this.frameUnits = QOM.FrameUnits.GROUPS;
        this.frameStart = Integer.valueOf(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenUnboundedPreceding() {
        groupsUnboundedPreceding();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenPreceding(int number) {
        groupsPreceding(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenCurrentRow() {
        groupsCurrentRow();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenUnboundedFollowing() {
        groupsUnboundedFollowing();
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsStep
    public final WindowSpecificationRowsAndStep groupsBetweenFollowing(int number) {
        groupsFollowing(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsAndStep
    public final WindowSpecificationExcludeStep andUnboundedPreceding() {
        this.frameEnd = Integer.MIN_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsAndStep
    public final WindowSpecificationExcludeStep andPreceding(int number) {
        this.frameEnd = Integer.valueOf(-number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsAndStep
    public final WindowSpecificationExcludeStep andCurrentRow() {
        this.frameEnd = 0;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsAndStep
    public final WindowSpecificationExcludeStep andUnboundedFollowing() {
        this.frameEnd = Integer.MAX_VALUE;
        return this;
    }

    @Override // org.jooq.WindowSpecificationRowsAndStep
    public final WindowSpecificationExcludeStep andFollowing(int number) {
        this.frameEnd = Integer.valueOf(number);
        return this;
    }

    @Override // org.jooq.WindowSpecificationExcludeStep
    public final WindowSpecificationFinalStep excludeCurrentRow() {
        this.exclude = QOM.FrameExclude.CURRENT_ROW;
        return this;
    }

    @Override // org.jooq.WindowSpecificationExcludeStep
    public final WindowSpecificationFinalStep excludeGroup() {
        this.exclude = QOM.FrameExclude.GROUP;
        return this;
    }

    @Override // org.jooq.WindowSpecificationExcludeStep
    public final WindowSpecificationFinalStep excludeTies() {
        this.exclude = QOM.FrameExclude.TIES;
        return this;
    }

    @Override // org.jooq.WindowSpecificationExcludeStep
    public final WindowSpecificationFinalStep excludeNoOthers() {
        this.exclude = QOM.FrameExclude.NO_OTHERS;
        return this;
    }

    @Override // org.jooq.WindowSpecification
    public final WindowDefinitionImpl $windowDefinition() {
        return this.windowDefinition;
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.UnmodifiableList<? extends Field<?>> $partitionBy() {
        return QOM.unmodifiable((List) this.partitionBy);
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.UnmodifiableList<? extends SortField<?>> $orderBy() {
        return QOM.unmodifiable((List) this.orderBy);
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.FrameUnits $frameUnits() {
        return this.frameUnits;
    }

    @Override // org.jooq.WindowSpecification
    public final Integer $frameStart() {
        return this.frameStart;
    }

    @Override // org.jooq.WindowSpecification
    public final Integer $frameEnd() {
        return this.frameEnd;
    }

    @Override // org.jooq.WindowSpecification
    public final QOM.FrameExclude $exclude() {
        return this.exclude;
    }
}
