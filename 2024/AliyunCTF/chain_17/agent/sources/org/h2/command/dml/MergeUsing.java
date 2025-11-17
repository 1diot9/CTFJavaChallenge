package org.h2.command.dml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.HasSQL;
import org.h2.util.Utils;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/MergeUsing.class */
public final class MergeUsing extends DataChangeStatement {
    TableFilter targetTableFilter;
    TableFilter sourceTableFilter;
    Expression onCondition;
    private ArrayList<When> when;
    private final HashSet<Long> targetRowidsRemembered;

    public MergeUsing(SessionLocal sessionLocal, TableFilter tableFilter) {
        super(sessionLocal);
        this.when = Utils.newSmallArrayList();
        this.targetRowidsRemembered = new HashSet<>();
        this.targetTableFilter = tableFilter;
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public long update(ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
        long j = 0;
        this.targetRowidsRemembered.clear();
        checkRights();
        setCurrentRowNumber(0L);
        this.sourceTableFilter.startQuery(this.session);
        this.sourceTableFilter.reset();
        Table table = this.targetTableFilter.getTable();
        table.fire(this.session, evaluateTriggerMasks(), true);
        table.lock(this.session, 1);
        setCurrentRowNumber(0L);
        long j2 = 0;
        Row row = null;
        Row row2 = null;
        boolean z = table.getRowIdColumn() != null;
        while (this.sourceTableFilter.next()) {
            Row row3 = this.sourceTableFilter.get();
            if (row2 != null) {
                if (row3 != row2) {
                    Row row4 = this.targetTableFilter.get();
                    this.sourceTableFilter.set(row2);
                    this.targetTableFilter.set(table.getNullRow());
                    j += merge(true, resultTarget, resultOption);
                    this.sourceTableFilter.set(row3);
                    this.targetTableFilter.set(row4);
                    j2++;
                }
                row2 = null;
            }
            setCurrentRowNumber(j2 + 1);
            if (!this.targetTableFilter.isNullRow()) {
                Row row5 = this.targetTableFilter.get();
                if (table.isRowLockable()) {
                    Row lockRow = table.lockRow(this.session, row5, -1);
                    if (lockRow == null) {
                        if (row != row3) {
                            row2 = row3;
                        }
                    } else if (!row5.hasSharedData(lockRow)) {
                        row5 = lockRow;
                        this.targetTableFilter.set(row5);
                        if (!this.onCondition.getBooleanValue(this.session)) {
                            if (row != row3) {
                                row2 = row3;
                            }
                        }
                    }
                }
                if (z) {
                    long key = row5.getKey();
                    if (!this.targetRowidsRemembered.add(Long.valueOf(key))) {
                        throw DbException.get(ErrorCode.DUPLICATE_KEY_1, "Merge using ON column expression, duplicate _ROWID_ target record already processed:_ROWID_=" + key + ":in:" + this.targetTableFilter.getTable());
                    }
                }
            }
            j += merge(r0, resultTarget, resultOption);
            j2++;
            row = row3;
        }
        if (row2 != null) {
            this.sourceTableFilter.set(row2);
            this.targetTableFilter.set(table.getNullRow());
            j += merge(true, resultTarget, resultOption);
        }
        this.targetRowidsRemembered.clear();
        table.fire(this.session, evaluateTriggerMasks(), false);
        return j;
    }

    private int merge(boolean z, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
        Expression expression;
        Iterator<When> it = this.when.iterator();
        while (it.hasNext()) {
            When next = it.next();
            if ((next.getClass() == WhenNotMatched.class) == z && ((expression = next.andCondition) == null || expression.getBooleanValue(this.session))) {
                next.merge(this.session, resultTarget, resultOption);
                return 1;
            }
        }
        return 0;
    }

    private int evaluateTriggerMasks() {
        int i = 0;
        Iterator<When> it = this.when.iterator();
        while (it.hasNext()) {
            i |= it.next().evaluateTriggerMasks();
        }
        return i;
    }

    private void checkRights() {
        Iterator<When> it = this.when.iterator();
        while (it.hasNext()) {
            it.next().checkRights();
        }
        this.session.getUser().checkTableRight(this.targetTableFilter.getTable(), 1);
        this.session.getUser().checkTableRight(this.sourceTableFilter.getTable(), 1);
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        StringBuilder sb = new StringBuilder("MERGE INTO ");
        this.targetTableFilter.getPlanSQL(sb, false, i);
        this.sourceTableFilter.getPlanSQL(sb.append('\n').append("USING "), false, i);
        this.onCondition.getSQL(sb.append('\n').append("ON "), i);
        Iterator<When> it = this.when.iterator();
        while (it.hasNext()) {
            it.next().getSQL(sb.append('\n'), i);
        }
        return sb.toString();
    }

    @Override // org.h2.command.dml.DataChangeStatement
    void doPrepare() {
        this.onCondition.addFilterConditions(this.sourceTableFilter);
        this.onCondition.addFilterConditions(this.targetTableFilter);
        this.onCondition.mapColumns(this.sourceTableFilter, 0, 0);
        this.onCondition.mapColumns(this.targetTableFilter, 0, 0);
        this.onCondition = this.onCondition.optimize(this.session);
        this.onCondition.createIndexConditions(this.session, this.targetTableFilter);
        TableFilter[] tableFilterArr = {this.sourceTableFilter, this.targetTableFilter};
        this.sourceTableFilter.addJoin(this.targetTableFilter, true, this.onCondition);
        this.sourceTableFilter.setPlanItem(this.sourceTableFilter.getBestPlanItem(this.session, tableFilterArr, 0, new AllColumnsForPlan(tableFilterArr)));
        this.sourceTableFilter.prepare();
        boolean z = false;
        boolean z2 = false;
        Iterator<When> it = this.when.iterator();
        while (it.hasNext()) {
            When next = it.next();
            if (!next.prepare(this.session)) {
                it.remove();
            } else if (next.getClass() == WhenNotMatched.class) {
                if (z) {
                    it.remove();
                } else if (next.andCondition == null) {
                    z = true;
                }
            } else if (z2) {
                it.remove();
            } else if (next.andCondition == null) {
                z2 = true;
            }
        }
    }

    public void setSourceTableFilter(TableFilter tableFilter) {
        this.sourceTableFilter = tableFilter;
    }

    public TableFilter getSourceTableFilter() {
        return this.sourceTableFilter;
    }

    public void setOnCondition(Expression expression) {
        this.onCondition = expression;
    }

    public Expression getOnCondition() {
        return this.onCondition;
    }

    public ArrayList<When> getWhen() {
        return this.when;
    }

    public void addWhen(When when) {
        this.when.add(when);
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public Table getTable() {
        return this.targetTableFilter.getTable();
    }

    public void setTargetTableFilter(TableFilter tableFilter) {
        this.targetTableFilter = tableFilter;
    }

    public TableFilter getTargetTableFilter() {
        return this.targetTableFilter;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 62;
    }

    @Override // org.h2.command.dml.DataChangeStatement
    public String getStatementName() {
        return "MERGE";
    }

    @Override // org.h2.command.Prepared
    public void collectDependencies(HashSet<DbObject> hashSet) {
        hashSet.add(this.targetTableFilter.getTable());
        hashSet.add(this.sourceTableFilter.getTable());
        ExpressionVisitor dependenciesVisitor = ExpressionVisitor.getDependenciesVisitor(hashSet);
        Iterator<When> it = this.when.iterator();
        while (it.hasNext()) {
            it.next().collectDependencies(dependenciesVisitor);
        }
        this.onCondition.isEverything(dependenciesVisitor);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/MergeUsing$When.class */
    public abstract class When implements HasSQL {
        Expression andCondition;

        abstract void merge(SessionLocal sessionLocal, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption);

        abstract int evaluateTriggerMasks();

        abstract void checkRights();

        When() {
        }

        public void setAndCondition(Expression expression) {
            this.andCondition = expression;
        }

        boolean prepare(SessionLocal sessionLocal) {
            if (this.andCondition != null) {
                this.andCondition.mapColumns(MergeUsing.this.targetTableFilter, 0, 0);
                this.andCondition.mapColumns(MergeUsing.this.sourceTableFilter, 0, 0);
                this.andCondition = this.andCondition.optimize(sessionLocal);
                if (this.andCondition.isConstant()) {
                    if (this.andCondition.getBooleanValue(sessionLocal)) {
                        this.andCondition = null;
                        return true;
                    }
                    return false;
                }
                return true;
            }
            return true;
        }

        void collectDependencies(ExpressionVisitor expressionVisitor) {
            if (this.andCondition != null) {
                this.andCondition.isEverything(expressionVisitor);
            }
        }

        @Override // org.h2.util.HasSQL
        public StringBuilder getSQL(StringBuilder sb, int i) {
            sb.append("WHEN ");
            if (getClass() == WhenNotMatched.class) {
                sb.append("NOT ");
            }
            sb.append("MATCHED");
            if (this.andCondition != null) {
                this.andCondition.getUnenclosedSQL(sb.append(" AND "), i);
            }
            return sb.append(" THEN ");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/MergeUsing$WhenMatchedThenDelete.class */
    public final class WhenMatchedThenDelete extends When {
        public WhenMatchedThenDelete() {
            super();
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void merge(SessionLocal sessionLocal, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
            TableFilter tableFilter = MergeUsing.this.targetTableFilter;
            Table table = tableFilter.getTable();
            Row row = tableFilter.get();
            if (resultOption == DataChangeDeltaTable.ResultOption.OLD) {
                resultTarget.addRow(row.getValueList());
            }
            if (!table.fireRow() || !table.fireBeforeRow(sessionLocal, row, null)) {
                table.removeRow(sessionLocal, row);
                table.fireAfterRow(sessionLocal, row, null, false);
            }
        }

        @Override // org.h2.command.dml.MergeUsing.When
        int evaluateTriggerMasks() {
            return 4;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void checkRights() {
            MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 2);
        }

        @Override // org.h2.command.dml.MergeUsing.When, org.h2.util.HasSQL
        public StringBuilder getSQL(StringBuilder sb, int i) {
            return super.getSQL(sb, i).append("DELETE");
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/MergeUsing$WhenMatchedThenUpdate.class */
    public final class WhenMatchedThenUpdate extends When {
        private SetClauseList setClauseList;

        public WhenMatchedThenUpdate() {
            super();
        }

        public void setSetClauseList(SetClauseList setClauseList) {
            this.setClauseList = setClauseList;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void merge(SessionLocal sessionLocal, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
            TableFilter tableFilter = MergeUsing.this.targetTableFilter;
            Table table = tableFilter.getTable();
            LocalResult forTable = LocalResult.forTable(sessionLocal, table);
            Throwable th = null;
            try {
                try {
                    this.setClauseList.prepareUpdate(table, sessionLocal, resultTarget, resultOption, forTable, tableFilter.get(), false);
                    Update.doUpdate(MergeUsing.this, sessionLocal, table, forTable);
                    if (forTable != null) {
                        if (0 != 0) {
                            try {
                                forTable.close();
                                return;
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                                return;
                            }
                        }
                        forTable.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (forTable != null) {
                    if (th != null) {
                        try {
                            forTable.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        forTable.close();
                    }
                }
                throw th4;
            }
        }

        @Override // org.h2.command.dml.MergeUsing.When
        boolean prepare(SessionLocal sessionLocal) {
            boolean prepare = super.prepare(sessionLocal);
            this.setClauseList.mapAndOptimize(sessionLocal, MergeUsing.this.targetTableFilter, MergeUsing.this.sourceTableFilter);
            return prepare;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        int evaluateTriggerMasks() {
            return 2;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void checkRights() {
            MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 8);
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void collectDependencies(ExpressionVisitor expressionVisitor) {
            super.collectDependencies(expressionVisitor);
            this.setClauseList.isEverything(expressionVisitor);
        }

        @Override // org.h2.command.dml.MergeUsing.When, org.h2.util.HasSQL
        public StringBuilder getSQL(StringBuilder sb, int i) {
            return this.setClauseList.getSQL(super.getSQL(sb, i).append("UPDATE"), i);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/MergeUsing$WhenNotMatched.class */
    public final class WhenNotMatched extends When {
        private Column[] columns;
        private final Boolean overridingSystem;
        private final Expression[] values;

        public WhenNotMatched(Column[] columnArr, Boolean bool, Expression[] expressionArr) {
            super();
            this.columns = columnArr;
            this.overridingSystem = bool;
            this.values = expressionArr;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void merge(SessionLocal sessionLocal, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption) {
            Table table = MergeUsing.this.targetTableFilter.getTable();
            Row templateRow = table.getTemplateRow();
            Expression[] expressionArr = this.values;
            int length = this.columns.length;
            for (int i = 0; i < length; i++) {
                int columnId = this.columns[i].getColumnId();
                Expression expression = expressionArr[i];
                if (expression != ValueExpression.DEFAULT) {
                    try {
                        templateRow.setValue(columnId, expression.getValue(sessionLocal));
                    } catch (DbException e) {
                        e.addSQL("INSERT -- " + Prepared.getSimpleSQL(expressionArr));
                        throw e;
                    }
                }
            }
            table.convertInsertRow(sessionLocal, templateRow, this.overridingSystem);
            if (resultOption == DataChangeDeltaTable.ResultOption.NEW) {
                resultTarget.addRow((Value[]) templateRow.getValueList().clone());
            }
            if (!table.fireBeforeRow(sessionLocal, null, templateRow)) {
                table.addRow(sessionLocal, templateRow);
                DataChangeDeltaTable.collectInsertedFinalRow(sessionLocal, table, resultTarget, resultOption, templateRow);
                table.fireAfterRow(sessionLocal, null, templateRow, false);
                return;
            }
            DataChangeDeltaTable.collectInsertedFinalRow(sessionLocal, table, resultTarget, resultOption, templateRow);
        }

        @Override // org.h2.command.dml.MergeUsing.When
        boolean prepare(SessionLocal sessionLocal) {
            boolean prepare = super.prepare(sessionLocal);
            TableFilter tableFilter = MergeUsing.this.targetTableFilter;
            TableFilter tableFilter2 = MergeUsing.this.sourceTableFilter;
            if (this.columns == null) {
                this.columns = tableFilter.getTable().getColumns();
            }
            if (this.values.length != this.columns.length) {
                throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
            }
            int length = this.values.length;
            for (int i = 0; i < length; i++) {
                Expression expression = this.values[i];
                expression.mapColumns(tableFilter, 0, 0);
                expression.mapColumns(tableFilter2, 0, 0);
                Expression optimize = expression.optimize(sessionLocal);
                if (optimize instanceof Parameter) {
                    ((Parameter) optimize).setColumn(this.columns[i]);
                }
                this.values[i] = optimize;
            }
            return prepare;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        int evaluateTriggerMasks() {
            return 1;
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void checkRights() {
            MergeUsing.this.getSession().getUser().checkTableRight(MergeUsing.this.targetTableFilter.getTable(), 4);
        }

        @Override // org.h2.command.dml.MergeUsing.When
        void collectDependencies(ExpressionVisitor expressionVisitor) {
            super.collectDependencies(expressionVisitor);
            for (Expression expression : this.values) {
                expression.isEverything(expressionVisitor);
            }
        }

        @Override // org.h2.command.dml.MergeUsing.When, org.h2.util.HasSQL
        public StringBuilder getSQL(StringBuilder sb, int i) {
            super.getSQL(sb, i).append("INSERT (");
            Column.writeColumns(sb, this.columns, i).append(")\nVALUES (");
            return Expression.writeExpressions(sb, this.values, i).append(')');
        }
    }
}
