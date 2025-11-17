package org.h2.expression;

import java.util.HashSet;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ExpressionVisitor.class */
public final class ExpressionVisitor {
    public static final int INDEPENDENT = 0;
    public static final ExpressionVisitor INDEPENDENT_VISITOR;
    public static final int OPTIMIZABLE_AGGREGATE = 1;
    public static final int DETERMINISTIC = 2;
    public static final ExpressionVisitor DETERMINISTIC_VISITOR;
    public static final int EVALUATABLE = 3;
    public static final ExpressionVisitor EVALUATABLE_VISITOR;
    private static final int CACHED = 8;
    private static final ExpressionVisitor[] INDEPENDENT_VISITORS;
    private static final ExpressionVisitor[] EVALUATABLE_VISITORS;
    public static final int SET_MAX_DATA_MODIFICATION_ID = 4;
    public static final int READONLY = 5;
    public static final ExpressionVisitor READONLY_VISITOR;
    public static final int NOT_FROM_RESOLVER = 6;
    public static final int GET_DEPENDENCIES = 7;
    public static final int QUERY_COMPARABLE = 8;
    public static final int GET_COLUMNS1 = 9;
    public static final int GET_COLUMNS2 = 10;
    public static final int DECREMENT_QUERY_LEVEL = 11;
    public static final ExpressionVisitor QUERY_COMPARABLE_VISITOR;
    private final int type;
    private final int queryLevel;
    private final HashSet<?> set;
    private final AllColumnsForPlan columns1;
    private final Table table;
    private final long[] maxDataModificationId;
    private final ColumnResolver resolver;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ExpressionVisitor.class.desiredAssertionStatus();
        INDEPENDENT_VISITOR = new ExpressionVisitor(0);
        DETERMINISTIC_VISITOR = new ExpressionVisitor(2);
        EVALUATABLE_VISITOR = new ExpressionVisitor(3);
        ExpressionVisitor[] expressionVisitorArr = new ExpressionVisitor[8];
        expressionVisitorArr[0] = INDEPENDENT_VISITOR;
        for (int i = 1; i < 8; i++) {
            expressionVisitorArr[i] = new ExpressionVisitor(0, i);
        }
        INDEPENDENT_VISITORS = expressionVisitorArr;
        ExpressionVisitor[] expressionVisitorArr2 = new ExpressionVisitor[8];
        expressionVisitorArr2[0] = EVALUATABLE_VISITOR;
        for (int i2 = 1; i2 < 8; i2++) {
            expressionVisitorArr2[i2] = new ExpressionVisitor(3, i2);
        }
        EVALUATABLE_VISITORS = expressionVisitorArr2;
        READONLY_VISITOR = new ExpressionVisitor(5);
        QUERY_COMPARABLE_VISITOR = new ExpressionVisitor(8);
    }

    private ExpressionVisitor(int i, int i2, HashSet<?> hashSet, AllColumnsForPlan allColumnsForPlan, Table table, ColumnResolver columnResolver, long[] jArr) {
        this.type = i;
        this.queryLevel = i2;
        this.set = hashSet;
        this.columns1 = allColumnsForPlan;
        this.table = table;
        this.resolver = columnResolver;
        this.maxDataModificationId = jArr;
    }

    private ExpressionVisitor(int i) {
        this.type = i;
        this.queryLevel = 0;
        this.set = null;
        this.columns1 = null;
        this.table = null;
        this.resolver = null;
        this.maxDataModificationId = null;
    }

    private ExpressionVisitor(int i, int i2) {
        this.type = i;
        this.queryLevel = i2;
        this.set = null;
        this.columns1 = null;
        this.table = null;
        this.resolver = null;
        this.maxDataModificationId = null;
    }

    public static ExpressionVisitor getDependenciesVisitor(HashSet<DbObject> hashSet) {
        return new ExpressionVisitor(7, 0, hashSet, null, null, null, null);
    }

    public static ExpressionVisitor getOptimizableVisitor(Table table) {
        return new ExpressionVisitor(1, 0, null, null, table, null, null);
    }

    public static ExpressionVisitor getNotFromResolverVisitor(ColumnResolver columnResolver) {
        return new ExpressionVisitor(6, 0, null, null, null, columnResolver, null);
    }

    public static ExpressionVisitor getColumnsVisitor(AllColumnsForPlan allColumnsForPlan) {
        return new ExpressionVisitor(9, 0, null, allColumnsForPlan, null, null, null);
    }

    public static ExpressionVisitor getColumnsVisitor(HashSet<Column> hashSet, Table table) {
        return new ExpressionVisitor(10, 0, hashSet, null, table, null, null);
    }

    public static ExpressionVisitor getMaxModificationIdVisitor() {
        return new ExpressionVisitor(4, 0, null, null, null, null, new long[1]);
    }

    public static ExpressionVisitor getDecrementQueryLevelVisitor(HashSet<ColumnResolver> hashSet, int i) {
        return new ExpressionVisitor(11, i, hashSet, null, null, null, null);
    }

    public void addDependency(DbObject dbObject) {
        this.set.add(dbObject);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addColumn1(Column column) {
        this.columns1.add(column);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addColumn2(Column column) {
        if (this.table == null || this.table == column.getTable()) {
            this.set.add(column);
        }
    }

    public HashSet<DbObject> getDependencies() {
        return this.set;
    }

    public ExpressionVisitor incrementQueryLevel(int i) {
        if (this.type == 0) {
            int i2 = i + this.queryLevel;
            return i2 < 8 ? INDEPENDENT_VISITORS[i2] : new ExpressionVisitor(0, i2);
        }
        if (this.type == 3) {
            int i3 = i + this.queryLevel;
            return i3 < 8 ? EVALUATABLE_VISITORS[i3] : new ExpressionVisitor(3, i3);
        }
        return this;
    }

    public ColumnResolver getResolver() {
        return this.resolver;
    }

    public HashSet<ColumnResolver> getColumnResolvers() {
        return this.set;
    }

    public void addDataModificationId(long j) {
        if (j > this.maxDataModificationId[0]) {
            this.maxDataModificationId[0] = j;
        }
    }

    public long getMaxDataModificationId() {
        return this.maxDataModificationId[0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getQueryLevel() {
        if ($assertionsDisabled || this.type == 0 || this.type == 3 || this.type == 11) {
            return this.queryLevel;
        }
        throw new AssertionError();
    }

    public Table getTable() {
        return this.table;
    }

    public int getType() {
        return this.type;
    }

    public static void allColumnsForTableFilters(TableFilter[] tableFilterArr, AllColumnsForPlan allColumnsForPlan) {
        for (TableFilter tableFilter : tableFilterArr) {
            if (tableFilter.getSelect() != null) {
                tableFilter.getSelect().isEverything(getColumnsVisitor(allColumnsForPlan));
            }
        }
    }
}
