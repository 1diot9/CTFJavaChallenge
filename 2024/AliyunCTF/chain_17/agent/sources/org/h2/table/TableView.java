package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.command.ddl.CreateTableData;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Parameter;
import org.h2.index.Index;
import org.h2.index.QueryExpressionIndex;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.SortOrder;
import org.h2.schema.Schema;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableView.class */
public final class TableView extends QueryExpressionTable {
    private String querySQL;
    private Column[] columnTemplates;
    private boolean allowRecursive;
    private DbException createException;
    private ResultInterface recursiveResult;
    private boolean isRecursiveQueryDetected;
    private boolean isTableExpression;

    public TableView(Schema schema, int i, String str, String str2, ArrayList<Parameter> arrayList, Column[] columnArr, SessionLocal sessionLocal, boolean z, boolean z2, boolean z3, boolean z4) {
        super(schema, i, str);
        setTemporary(z4);
        init(str2, arrayList, columnArr, sessionLocal, z, z2, z3);
    }

    public void replace(String str, Column[] columnArr, SessionLocal sessionLocal, boolean z, boolean z2, boolean z3) {
        String str2 = this.querySQL;
        Column[] columnArr2 = this.columnTemplates;
        boolean z4 = this.allowRecursive;
        init(str, null, columnArr, sessionLocal, z, z3, this.isTableExpression);
        DbException recompile = recompile(sessionLocal, z2, true);
        if (recompile != null) {
            init(str2, null, columnArr2, sessionLocal, z4, z3, this.isTableExpression);
            recompile(sessionLocal, true, false);
            throw recompile;
        }
    }

    private synchronized void init(String str, ArrayList<Parameter> arrayList, Column[] columnArr, SessionLocal sessionLocal, boolean z, boolean z2, boolean z3) {
        this.querySQL = str;
        this.columnTemplates = columnArr;
        this.allowRecursive = z;
        this.isRecursiveQueryDetected = false;
        this.isTableExpression = z3;
        this.index = new QueryExpressionIndex(this, str, arrayList, z);
        initColumnsAndTables(sessionLocal, z2);
    }

    private Query compileViewQuery(SessionLocal sessionLocal, String str, boolean z) {
        sessionLocal.setParsingCreateView(true);
        try {
            Prepared prepare = sessionLocal.prepare(str, false, z);
            sessionLocal.setParsingCreateView(false);
            if (!(prepare instanceof Query)) {
                throw DbException.getSyntaxError(str, 0);
            }
            Query query = (Query) prepare;
            if (this.isTableExpression && this.allowRecursive) {
                query.setNeverLazy(true);
            }
            return query;
        } catch (Throwable th) {
            sessionLocal.setParsingCreateView(false);
            throw th;
        }
    }

    public synchronized DbException recompile(SessionLocal sessionLocal, boolean z, boolean z2) {
        try {
            compileViewQuery(sessionLocal, this.querySQL, false);
        } catch (DbException e) {
            if (!z) {
                return e;
            }
        }
        ArrayList arrayList = new ArrayList(getDependentViews());
        initColumnsAndTables(sessionLocal, false);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            DbException recompile = ((TableView) it.next()).recompile(sessionLocal, z, false);
            if (recompile != null && !z) {
                return recompile;
            }
        }
        if (z2) {
            clearIndexCaches(this.database);
        }
        if (z) {
            return null;
        }
        return this.createException;
    }

    private void initColumnsAndTables(SessionLocal sessionLocal, boolean z) {
        Column[] columnArr;
        removeCurrentViewFromOtherTables();
        setTableExpression(this.isTableExpression);
        try {
            Query compileViewQuery = compileViewQuery(sessionLocal, this.querySQL, z);
            this.querySQL = compileViewQuery.getPlanSQL(0);
            this.tables = new ArrayList<>(compileViewQuery.getTables());
            columnArr = initColumns(sessionLocal, this.columnTemplates, compileViewQuery, false);
            this.createException = null;
            this.viewQuery = compileViewQuery;
        } catch (DbException e) {
            if (e.getErrorCode() == 90156) {
                throw e;
            }
            e.addSQL(getCreateSQL());
            this.createException = e;
            if (isRecursiveQueryExceptionDetected(this.createException)) {
                this.isRecursiveQueryDetected = true;
            }
            this.tables = Utils.newSmallArrayList();
            columnArr = new Column[0];
            if (this.allowRecursive && this.columnTemplates != null) {
                columnArr = new Column[this.columnTemplates.length];
                for (int i = 0; i < this.columnTemplates.length; i++) {
                    columnArr[i] = this.columnTemplates[i].getClone();
                }
                this.index.setRecursive(true);
                this.createException = null;
            }
        }
        setColumns(columnArr);
        if (getId() != 0) {
            addDependentViewToTables();
        }
    }

    public boolean isInvalid() {
        return this.createException != null;
    }

    @Override // org.h2.table.QueryExpressionTable
    public Query getTopQuery() {
        return null;
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP VIEW IF EXISTS "), 0).append(" CASCADE").toString();
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
        sb.append("VIEW ");
        if (this.isTableExpression) {
            sb.append("TABLE_EXPRESSION ");
        }
        sb.append(str);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        if (this.columns != null && this.columns.length > 0) {
            sb.append('(');
            Column.writeColumns(sb, this.columns, 0);
            sb.append(')');
        } else if (this.columnTemplates != null) {
            sb.append('(');
            Column.writeColumns(sb, this.columnTemplates, 0);
            sb.append(')');
        }
        return sb.append(" AS\n").append(this.querySQL).toString();
    }

    @Override // org.h2.table.Table
    public boolean canDrop() {
        return true;
    }

    @Override // org.h2.table.Table
    public TableType getTableType() {
        return TableType.VIEW;
    }

    @Override // org.h2.table.Table, org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        removeCurrentViewFromOtherTables();
        super.removeChildrenAndResources(sessionLocal);
        this.querySQL = null;
        this.index = null;
        clearIndexCaches(this.database);
        invalidate();
    }

    public static void clearIndexCaches(Database database) {
        for (SessionLocal sessionLocal : database.getSessions(true)) {
            sessionLocal.clearViewIndexCache();
        }
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

    @Override // org.h2.table.QueryExpressionTable, org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        if (this.createException != null) {
            throw DbException.get(ErrorCode.VIEW_IS_INVALID_2, this.createException, getTraceSQL(), this.createException.getMessage());
        }
        return super.getScanIndex(sessionLocal, iArr, tableFilterArr, i, sortOrder, allColumnsForPlan);
    }

    @Override // org.h2.table.QueryExpressionTable, org.h2.table.Table
    public long getMaxDataModificationId() {
        if (this.createException != null || this.viewQuery == null) {
            return Long.MAX_VALUE;
        }
        return super.getMaxDataModificationId();
    }

    private void removeCurrentViewFromOtherTables() {
        if (this.tables != null) {
            Iterator<Table> it = this.tables.iterator();
            while (it.hasNext()) {
                it.next().removeDependentView(this);
            }
            this.tables.clear();
        }
    }

    private void addDependentViewToTables() {
        Iterator<Table> it = this.tables.iterator();
        while (it.hasNext()) {
            it.next().addDependentView(this);
        }
    }

    public boolean isRecursive() {
        return this.allowRecursive;
    }

    @Override // org.h2.table.QueryExpressionTable, org.h2.table.Table
    public boolean isDeterministic() {
        if (this.allowRecursive || this.viewQuery == null) {
            return false;
        }
        return super.isDeterministic();
    }

    public void setRecursiveResult(ResultInterface resultInterface) {
        if (this.recursiveResult != null) {
            this.recursiveResult.close();
        }
        this.recursiveResult = resultInterface;
    }

    public ResultInterface getRecursiveResult() {
        return this.recursiveResult;
    }

    public boolean isRecursiveQueryDetected() {
        return this.isRecursiveQueryDetected;
    }

    private boolean isRecursiveQueryExceptionDetected(DbException dbException) {
        if (dbException == null) {
            return false;
        }
        int errorCode = dbException.getErrorCode();
        if (errorCode != 42102 && errorCode != 42104 && errorCode != 42103) {
            return false;
        }
        return dbException.getMessage().contains("\"" + getName() + "\"");
    }

    public List<Table> getTables() {
        return this.tables;
    }

    public static TableView createTableViewMaybeRecursive(Schema schema, int i, String str, String str2, ArrayList<Parameter> arrayList, Column[] columnArr, SessionLocal sessionLocal, boolean z, boolean z2, boolean z3, Database database) {
        Table createShadowTableForRecursiveTableExpression = createShadowTableForRecursiveTableExpression(z3, sessionLocal, str, schema, Arrays.asList(columnArr), database);
        String[] strArr = new String[1];
        ArrayList arrayList2 = new ArrayList();
        for (Column column : columnArr) {
            arrayList2.add(column.getName());
        }
        try {
            Prepared prepare = sessionLocal.prepare(str2, false, false);
            if (!z3) {
                prepare.setSession(sessionLocal);
            }
            List<Column> createQueryColumnTemplateList = createQueryColumnTemplateList((String[]) arrayList2.toArray(new String[1]), (Query) prepare, strArr);
            destroyShadowTableForRecursiveExpression(z3, sessionLocal, createShadowTableForRecursiveTableExpression);
            TableView tableView = new TableView(schema, i, str, str2, arrayList, (Column[]) createQueryColumnTemplateList.toArray(columnArr), sessionLocal, true, z, z2, z3);
            if (!tableView.isRecursiveQueryDetected()) {
                if (!z3) {
                    database.addSchemaObject(sessionLocal, tableView);
                    tableView.lock(sessionLocal, 2);
                    sessionLocal.getDatabase().removeSchemaObject(sessionLocal, tableView);
                    tableView.removeChildrenAndResources(sessionLocal);
                } else {
                    sessionLocal.removeLocalTempTable(tableView);
                }
                tableView = new TableView(schema, i, str, str2, arrayList, columnArr, sessionLocal, false, z, z2, z3);
            }
            return tableView;
        } catch (Throwable th) {
            destroyShadowTableForRecursiveExpression(z3, sessionLocal, createShadowTableForRecursiveTableExpression);
            throw th;
        }
    }

    public static Table createShadowTableForRecursiveTableExpression(boolean z, SessionLocal sessionLocal, String str, Schema schema, List<Column> list, Database database) {
        CreateTableData createTableData = new CreateTableData();
        createTableData.id = database.allocateObjectId();
        createTableData.columns = new ArrayList<>(list);
        createTableData.tableName = str;
        createTableData.temporary = z;
        createTableData.persistData = true;
        createTableData.persistIndexes = !z;
        createTableData.session = sessionLocal;
        Table createTable = schema.createTable(createTableData);
        if (!z) {
            database.unlockMeta(sessionLocal);
            synchronized (sessionLocal) {
                database.addSchemaObject(sessionLocal, createTable);
            }
        } else {
            sessionLocal.addLocalTempTable(createTable);
        }
        return createTable;
    }

    public static void destroyShadowTableForRecursiveExpression(boolean z, SessionLocal sessionLocal, Table table) {
        if (table != null) {
            if (!z) {
                table.lock(sessionLocal, 2);
                sessionLocal.getDatabase().removeSchemaObject(sessionLocal, table);
            } else {
                sessionLocal.removeLocalTempTable(table);
            }
            sessionLocal.getDatabase().unlockMeta(sessionLocal);
        }
    }
}
