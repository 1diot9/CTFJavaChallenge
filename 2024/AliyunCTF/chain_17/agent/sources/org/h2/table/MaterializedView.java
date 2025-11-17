package org.h2.table;

import java.util.ArrayList;
import java.util.HashSet;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Query;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/MaterializedView.class */
public class MaterializedView extends Table {
    private Table table;
    private String querySQL;
    private Query query;

    public MaterializedView(Schema schema, int i, String str, Table table, Query query, String str2) {
        super(schema, i, str, false, true);
        this.table = table;
        this.query = query;
        this.querySQL = str2;
    }

    public void replace(Table table, Query query, String str) {
        this.table = table;
        this.query = query;
        this.querySQL = str;
    }

    public Table getUnderlyingTable() {
        return this.table;
    }

    public Query getSelect() {
        return this.query;
    }

    @Override // org.h2.table.Table
    public final void close(SessionLocal sessionLocal) {
        this.table.close(sessionLocal);
    }

    @Override // org.h2.table.Table
    public final Index addIndex(SessionLocal sessionLocal, String str, int i, IndexColumn[] indexColumnArr, int i2, IndexType indexType, boolean z, String str2) {
        return this.table.addIndex(sessionLocal, str, i, indexColumnArr, i2, indexType, z, str2);
    }

    @Override // org.h2.table.Table
    public final boolean isView() {
        return true;
    }

    @Override // org.h2.table.Table
    public final PlanItem getBestPlanItem(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return this.table.getBestPlanItem(sessionLocal, iArr, tableFilterArr, i, sortOrder, allColumnsForPlan);
    }

    @Override // org.h2.table.Table
    public boolean isQueryComparable() {
        return this.table.isQueryComparable();
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
        return this.table.getRowCount(sessionLocal);
    }

    @Override // org.h2.table.Table
    public final boolean canGetRowCount(SessionLocal sessionLocal) {
        return this.table.canGetRowCount(sessionLocal);
    }

    @Override // org.h2.table.Table
    public final long getRowCountApproximation(SessionLocal sessionLocal) {
        return this.table.getRowCountApproximation(sessionLocal);
    }

    @Override // org.h2.table.Table
    public final boolean canReference() {
        return false;
    }

    @Override // org.h2.table.Table
    public final ArrayList<Index> getIndexes() {
        return this.table.getIndexes();
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
        return this.table.isDeterministic();
    }

    @Override // org.h2.table.Table
    public final void addDependencies(HashSet<DbObject> hashSet) {
        this.table.addDependencies(hashSet);
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP MATERIALIZED VIEW IF EXISTS "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        return getCreateSQL(false, true, str);
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQL(false, true);
    }

    public String getCreateSQL(boolean z, boolean z2) {
        return getCreateSQL(z, z2, getSQL(0));
    }

    private String getCreateSQL(boolean z, boolean z2, String str) {
        StringBuilder sb = new StringBuilder("CREATE ");
        if (z) {
            sb.append("OR REPLACE ");
        }
        if (z2) {
            sb.append("FORCE ");
        }
        sb.append("MATERIALIZED VIEW ");
        sb.append(str);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        return sb.append(" AS\n").append(this.querySQL).toString();
    }

    @Override // org.h2.table.Table
    public boolean canDrop() {
        return true;
    }

    @Override // org.h2.table.Table
    public TableType getTableType() {
        return TableType.MATERIALIZED_VIEW;
    }

    @Override // org.h2.table.Table, org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.table.removeChildrenAndResources(sessionLocal);
        this.database.removeMeta(sessionLocal, getId());
        this.querySQL = null;
        invalidate();
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if (isTemporary() && this.querySQL != null) {
            sb.append("(\n");
            return StringUtils.indent(sb, this.querySQL, 4, true).append(')');
        }
        return super.getSQL(sb, i);
    }

    public String getQuerySQL() {
        return this.querySQL;
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return this.table.getMaxDataModificationId();
    }
}
