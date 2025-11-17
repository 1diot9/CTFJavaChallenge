package org.h2.command.dml;

import java.util.HashSet;
import org.h2.command.Prepared;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.result.LocalResult;
import org.h2.result.Row;
import org.h2.table.Table;
import org.h2.table.TableFilter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/Update.class */
public final class Update extends FilteredDataChangeStatement {
    private SetClauseList setClauseList;
    private Insert onDuplicateKeyInsert;

    @Override // org.h2.command.dml.FilteredDataChangeStatement
    public /* bridge */ /* synthetic */ void setFetch(Expression expression) {
        super.setFetch(expression);
    }

    public Update(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setSetClauseList(SetClauseList setClauseList) {
        this.setClauseList = setClauseList;
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x007b, code lost:            if (1 < 0) goto L9;     */
    @Override // org.h2.command.dml.DataChangeStatement
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public long update(org.h2.result.ResultTarget r10, org.h2.table.DataChangeDeltaTable.ResultOption r11) {
        /*
            Method dump skipped, instructions count: 383
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.dml.Update.update(org.h2.result.ResultTarget, org.h2.table.DataChangeDeltaTable$ResultOption):long");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void doUpdate(Prepared prepared, SessionLocal sessionLocal, Table table, LocalResult localResult) {
        localResult.done();
        table.updateRows(prepared, sessionLocal, localResult);
        if (table.fireRow()) {
            localResult.reset();
            while (localResult.next()) {
                Row currentRowForTable = localResult.currentRowForTable();
                localResult.next();
                table.fireAfterRow(sessionLocal, currentRowForTable, localResult.currentRowForTable(), false);
            }
        }
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        StringBuilder sb = new StringBuilder("UPDATE ");
        this.targetTableFilter.getPlanSQL(sb, false, i);
        this.setClauseList.getSQL(sb, i);
        appendFilterCondition(sb, i);
        return sb.toString();
    }

    @Override // org.h2.command.dml.DataChangeStatement
    void doPrepare() {
        if (this.condition != null) {
            this.condition.mapColumns(this.targetTableFilter, 0, 0);
            this.condition = this.condition.optimizeCondition(this.session);
            if (this.condition != null) {
                this.condition.createIndexConditions(this.session, this.targetTableFilter);
            }
        }
        this.setClauseList.mapAndOptimize(this.session, this.targetTableFilter, null);
        TableFilter[] tableFilterArr = {this.targetTableFilter};
        this.targetTableFilter.setPlanItem(this.targetTableFilter.getBestPlanItem(this.session, tableFilterArr, 0, new AllColumnsForPlan(tableFilterArr)));
        this.targetTableFilter.prepare();
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 68;
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public String getStatementName() {
        return "UPDATE";
    }

    @Override // org.h2.command.Prepared
    public void collectDependencies(HashSet<DbObject> hashSet) {
        ExpressionVisitor dependenciesVisitor = ExpressionVisitor.getDependenciesVisitor(hashSet);
        if (this.condition != null) {
            this.condition.isEverything(dependenciesVisitor);
        }
        this.setClauseList.isEverything(dependenciesVisitor);
    }

    public Insert getOnDuplicateKeyInsert() {
        return this.onDuplicateKeyInsert;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnDuplicateKeyInsert(Insert insert) {
        this.onDuplicateKeyInsert = insert;
    }
}
