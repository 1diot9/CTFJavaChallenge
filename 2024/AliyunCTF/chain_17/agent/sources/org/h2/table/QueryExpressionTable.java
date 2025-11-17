package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Query;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.index.QueryExpressionIndex;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/QueryExpressionTable.class */
public abstract class QueryExpressionTable extends Table {
    private static final long ROW_COUNT_APPROXIMATION = 100;
    Query viewQuery;
    QueryExpressionIndex index;
    ArrayList<Table> tables;
    private long lastModificationCheck;
    private long maxDataModificationId;

    public abstract Query getTopQuery();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/QueryExpressionTable$CacheKey.class */
    public static final class CacheKey {
        private final int[] masks;
        private final QueryExpressionTable queryExpressionTable;

        CacheKey(int[] iArr, QueryExpressionTable queryExpressionTable) {
            this.masks = iArr;
            this.queryExpressionTable = queryExpressionTable;
        }

        public int hashCode() {
            return (31 * ((31 * 1) + Arrays.hashCode(this.masks))) + this.queryExpressionTable.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            CacheKey cacheKey = (CacheKey) obj;
            if (this.queryExpressionTable != cacheKey.queryExpressionTable) {
                return false;
            }
            return Arrays.equals(this.masks, cacheKey.masks);
        }
    }

    public static List<Column> createQueryColumnTemplateList(String[] strArr, Query query, String[] strArr2) {
        ArrayList arrayList = new ArrayList();
        query.prepare();
        strArr2[0] = StringUtils.cache(query.getPlanSQL(8));
        SessionLocal session = query.getSession();
        ArrayList<Expression> expressions = query.getExpressions();
        int i = 0;
        while (i < expressions.size()) {
            Expression expression = expressions.get(i);
            arrayList.add(new Column((strArr == null || strArr.length <= i) ? expression.getColumnNameForView(session, i) : strArr[i], expression.getType()));
            i++;
        }
        return arrayList;
    }

    static int getMaxParameterIndex(ArrayList<Parameter> arrayList) {
        int i = -1;
        Iterator<Parameter> it = arrayList.iterator();
        while (it.hasNext()) {
            Parameter next = it.next();
            if (next != null) {
                i = Math.max(i, next.getIndex());
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public QueryExpressionTable(Schema schema, int i, String str) {
        super(schema, i, str, false, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Column[] initColumns(SessionLocal sessionLocal, Column[] columnArr, Query query, boolean z) {
        ArrayList<Expression> expressions = query.getExpressions();
        int columnCount = query.getColumnCount();
        ArrayList arrayList = new ArrayList(columnCount);
        for (int i = 0; i < columnCount; i++) {
            Expression expression = expressions.get(i);
            String str = null;
            TypeInfo typeInfo = TypeInfo.TYPE_UNKNOWN;
            if (columnArr != null && columnArr.length > i) {
                str = columnArr[i].getName();
                typeInfo = columnArr[i].getType();
            }
            if (str == null) {
                str = z ? expression.getAlias(sessionLocal, i) : expression.getColumnNameForView(sessionLocal, i);
            }
            if (typeInfo.getValueType() == -1) {
                typeInfo = expression.getType();
            }
            arrayList.add(new Column(str, typeInfo, this, i));
        }
        return (Column[]) arrayList.toArray(new Column[0]);
    }

    public final Query getQuery() {
        return this.viewQuery;
    }

    @Override // org.h2.table.Table
    public final void close(SessionLocal sessionLocal) {
    }

    @Override // org.h2.table.Table
    public final Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2) {
        throw DbException.getUnsupportedException(getClass().getSimpleName() + ".addIndex");
    }

    @Override // org.h2.table.Table
    public final boolean isView() {
        return true;
    }

    @Override // org.h2.table.Table
    public final PlanItem getBestPlanItem(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        CacheKey cacheKey = new CacheKey(iArr, this);
        Map<Object, QueryExpressionIndex> viewIndexCache = sessionLocal.getViewIndexCache(getTableType() == null);
        QueryExpressionIndex queryExpressionIndex = viewIndexCache.get(cacheKey);
        if (queryExpressionIndex == null || queryExpressionIndex.isExpired()) {
            queryExpressionIndex = new QueryExpressionIndex(this, this.index, sessionLocal, iArr, tableFilterArr, i, sortOrder);
            viewIndexCache.put(cacheKey, queryExpressionIndex);
        }
        PlanItem planItem = new PlanItem();
        planItem.cost = queryExpressionIndex.getCost(sessionLocal, iArr, tableFilterArr, i, sortOrder, allColumnsForPlan);
        planItem.setIndex(queryExpressionIndex);
        return planItem;
    }

    @Override // org.h2.table.Table
    public boolean isQueryComparable() {
        Iterator<Table> it = this.tables.iterator();
        while (it.hasNext()) {
            if (!it.next().isQueryComparable()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.table.Table
    public final boolean isInsertable() {
        return false;
    }

    @Override // org.h2.table.Table
    public final void removeRow(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException(getClass().getSimpleName() + ".removeRow");
    }

    @Override // org.h2.table.Table
    public final void addRow(SessionLocal sessionLocal, Row row) {
        throw DbException.getUnsupportedException(getClass().getSimpleName() + ".addRow");
    }

    @Override // org.h2.table.Table
    public final void checkSupportAlter() {
        throw DbException.getUnsupportedException(getClass().getSimpleName() + ".checkSupportAlter");
    }

    @Override // org.h2.table.Table
    public final long truncate(SessionLocal sessionLocal) {
        throw DbException.getUnsupportedException(getClass().getSimpleName() + ".truncate");
    }

    @Override // org.h2.table.Table
    public final long getRowCount(SessionLocal sessionLocal) {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.table.Table
    public final boolean canGetRowCount(SessionLocal sessionLocal) {
        return false;
    }

    @Override // org.h2.table.Table
    public final long getRowCountApproximation(SessionLocal sessionLocal) {
        return 100L;
    }

    public final int getParameterOffset(ArrayList<Parameter> arrayList) {
        Query topQuery = getTopQuery();
        int maxParameterIndex = topQuery == null ? -1 : getMaxParameterIndex(topQuery.getParameters());
        if (arrayList != null) {
            maxParameterIndex = Math.max(maxParameterIndex, getMaxParameterIndex(arrayList));
        }
        return maxParameterIndex + 1;
    }

    @Override // org.h2.table.Table
    public final boolean canReference() {
        return false;
    }

    @Override // org.h2.table.Table
    public final ArrayList<Index> getIndexes() {
        return null;
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        long modificationDataId = this.database.getModificationDataId();
        if (modificationDataId > this.lastModificationCheck && this.maxDataModificationId <= modificationDataId) {
            this.maxDataModificationId = this.viewQuery.getMaxDataModificationId();
            this.lastModificationCheck = modificationDataId;
        }
        return this.maxDataModificationId;
    }

    @Override // org.h2.table.Table
    public final Index getScanIndex(SessionLocal sessionLocal) {
        return getBestPlanItem(sessionLocal, null, null, -1, null, null).getIndex();
    }

    @Override // org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return getBestPlanItem(sessionLocal, iArr, tableFilterArr, i, sortOrder, allColumnsForPlan).getIndex();
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return this.viewQuery.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR);
    }

    @Override // org.h2.table.Table
    public final void addDependencies(HashSet<DbObject> hashSet) {
        super.addDependencies(hashSet);
        if (this.tables != null) {
            Iterator<Table> it = this.tables.iterator();
            while (it.hasNext()) {
                Table next = it.next();
                if (TableType.VIEW != next.getTableType()) {
                    next.addDependencies(hashSet);
                }
            }
        }
    }
}
