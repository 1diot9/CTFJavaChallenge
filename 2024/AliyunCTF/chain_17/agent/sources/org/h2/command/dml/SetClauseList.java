package org.h2.command.dml;

import java.util.ArrayList;
import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.util.HasSQL;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetClauseList.class */
public final class SetClauseList implements HasSQL {
    private final Table table;
    private final UpdateAction[] actions;
    private boolean onUpdate;

    public SetClauseList(Table table) {
        this.table = table;
        this.actions = new UpdateAction[table.getColumns().length];
    }

    public void addSingle(Column column, Expression[] expressionArr, Expression expression) {
        int columnId = column.getColumnId();
        if (this.actions[columnId] != null) {
            throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, column.getName());
        }
        if (expression != ValueExpression.DEFAULT) {
            this.actions[columnId] = new SetSimple(expressionArr, expression);
            if (expression instanceof Parameter) {
                ((Parameter) expression).setColumn(column);
                return;
            }
            return;
        }
        this.actions[columnId] = UpdateAction.SET_DEFAULT;
    }

    public void addMultiple(ArrayList<Column> arrayList, ArrayList<Expression[]> arrayList2, Expression expression) {
        int size = arrayList.size();
        if (expression instanceof ExpressionList) {
            ExpressionList expressionList = (ExpressionList) expression;
            if (!expressionList.isArray()) {
                if (size != expressionList.getSubexpressionCount()) {
                    throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
                }
                for (int i = 0; i < size; i++) {
                    addSingle(arrayList.get(i), arrayList2.get(i), expressionList.getSubexpression(i));
                }
                return;
            }
        }
        if (size == 1) {
            addSingle(arrayList.get(0), arrayList2.get(0), expression);
            return;
        }
        int[] iArr = new int[size];
        RowExpression rowExpression = new RowExpression(expression, iArr);
        int length = this.table.getColumns().length - 1;
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            int columnId = arrayList.get(i3).getColumnId();
            if (columnId < length) {
                length = columnId;
            }
            if (columnId > i2) {
                i2 = columnId;
            }
        }
        for (int i4 = 0; i4 < size; i4++) {
            Column column = arrayList.get(i4);
            int columnId2 = column.getColumnId();
            iArr[i4] = columnId2;
            if (this.actions[columnId2] != null) {
                throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, column.getName());
            }
            this.actions[columnId2] = new SetMultiple(arrayList2.get(i4), rowExpression, i4, columnId2 == length, columnId2 == i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean prepareUpdate(Table table, SessionLocal sessionLocal, ResultTarget resultTarget, DataChangeDeltaTable.ResultOption resultOption, LocalResult localResult, Row row, boolean z) {
        Value value;
        Column[] columns = table.getColumns();
        int length = columns.length;
        Row templateRow = table.getTemplateRow();
        for (int i = 0; i < length; i++) {
            UpdateAction updateAction = this.actions[i];
            Column column = columns[i];
            Value value2 = row.getValue(i);
            if (updateAction == null || updateAction == UpdateAction.ON_UPDATE) {
                value = column.isGenerated() ? null : value2;
            } else if (updateAction == UpdateAction.SET_DEFAULT) {
                value = !column.isIdentity() ? null : value2;
            } else {
                value = updateAction.update(sessionLocal, value2);
                if (value == ValueNull.INSTANCE && column.isDefaultOnNull()) {
                    value = !column.isIdentity() ? null : value2;
                } else if (column.isGeneratedAlways()) {
                    throw DbException.get(ErrorCode.GENERATED_COLUMN_CANNOT_BE_ASSIGNED_1, column.getSQLWithTable(new StringBuilder(), 3).toString());
                }
            }
            templateRow.setValue(i, value);
        }
        templateRow.setKey(row.getKey());
        table.convertUpdateRow(sessionLocal, templateRow, false);
        boolean z2 = true;
        if (this.onUpdate) {
            if (!row.hasSameValues(templateRow)) {
                for (int i2 = 0; i2 < length; i2++) {
                    if (this.actions[i2] == UpdateAction.ON_UPDATE) {
                        templateRow.setValue(i2, columns[i2].getEffectiveOnUpdateExpression().getValue(sessionLocal));
                    } else if (columns[i2].isGenerated()) {
                        templateRow.setValue(i2, null);
                    }
                }
                table.convertUpdateRow(sessionLocal, templateRow, false);
            } else if (z) {
                z2 = false;
            }
        } else if (z && row.hasSameValues(templateRow)) {
            z2 = false;
        }
        if (resultOption == DataChangeDeltaTable.ResultOption.OLD) {
            resultTarget.addRow(row.getValueList());
        } else if (resultOption == DataChangeDeltaTable.ResultOption.NEW) {
            resultTarget.addRow((Value[]) templateRow.getValueList().clone());
        }
        if (!table.fireRow() || !table.fireBeforeRow(sessionLocal, row, templateRow)) {
            localResult.addRowForTable(row);
            localResult.addRowForTable(templateRow);
        }
        if (resultOption == DataChangeDeltaTable.ResultOption.FINAL) {
            resultTarget.addRow(templateRow.getValueList());
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        for (UpdateAction updateAction : this.actions) {
            if (updateAction != null && !updateAction.isEverything(expressionVisitor)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mapAndOptimize(SessionLocal sessionLocal, ColumnResolver columnResolver, ColumnResolver columnResolver2) {
        Column[] columns = this.table.getColumns();
        boolean z = false;
        for (int i = 0; i < this.actions.length; i++) {
            UpdateAction updateAction = this.actions[i];
            if (updateAction != null) {
                updateAction.mapAndOptimize(sessionLocal, columnResolver, columnResolver2);
            } else if (columns[i].getEffectiveOnUpdateExpression() != null) {
                this.actions[i] = UpdateAction.ON_UPDATE;
                z = true;
            }
        }
        this.onUpdate = z;
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        Column[] columns = this.table.getColumns();
        sb.append("\nSET\n    ");
        boolean z = false;
        for (int i2 = 0; i2 < this.actions.length; i2++) {
            UpdateAction updateAction = this.actions[i2];
            if (updateAction != null && updateAction != UpdateAction.ON_UPDATE) {
                if (updateAction.getClass() == SetMultiple.class) {
                    SetMultiple setMultiple = (SetMultiple) updateAction;
                    if (setMultiple.first) {
                        if (z) {
                            sb.append(",\n    ");
                        }
                        z = true;
                        RowExpression rowExpression = setMultiple.row;
                        sb.append('(');
                        int[] iArr = rowExpression.columns;
                        int length = iArr.length;
                        for (int i3 = 0; i3 < length; i3++) {
                            if (i3 > 0) {
                                sb.append(", ");
                            }
                            columns[iArr[i3]].getSQL(sb, i);
                        }
                        rowExpression.expression.getUnenclosedSQL(sb.append(") = "), i);
                    }
                } else {
                    if (z) {
                        sb.append(",\n    ");
                    }
                    z = true;
                    Column column = columns[i2];
                    if (updateAction != UpdateAction.SET_DEFAULT) {
                        updateAction.getSQL(sb, i, column);
                    } else {
                        column.getSQL(sb, i).append(" = DEFAULT");
                    }
                }
            }
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetClauseList$UpdateAction.class */
    public static class UpdateAction {
        static UpdateAction ON_UPDATE = new UpdateAction();
        static UpdateAction SET_DEFAULT = new UpdateAction();

        UpdateAction() {
        }

        Value update(SessionLocal sessionLocal, Value value) {
            throw DbException.getInternalError();
        }

        boolean isEverything(ExpressionVisitor expressionVisitor) {
            return true;
        }

        void mapAndOptimize(SessionLocal sessionLocal, ColumnResolver columnResolver, ColumnResolver columnResolver2) {
        }

        void getSQL(StringBuilder sb, int i, Column column) {
            throw DbException.getInternalError();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetClauseList$SetAction.class */
    private static abstract class SetAction extends UpdateAction {
        private final Expression[] arrayIndexes;

        abstract Value update(SessionLocal sessionLocal);

        SetAction(Expression[] expressionArr) {
            this.arrayIndexes = expressionArr;
        }

        @Override // org.h2.command.dml.SetClauseList.UpdateAction
        boolean isEverything(ExpressionVisitor expressionVisitor) {
            if (this.arrayIndexes != null) {
                for (Expression expression : this.arrayIndexes) {
                    if (!expression.isEverything(expressionVisitor)) {
                        return false;
                    }
                }
                return true;
            }
            return true;
        }

        @Override // org.h2.command.dml.SetClauseList.UpdateAction
        void mapAndOptimize(SessionLocal sessionLocal, ColumnResolver columnResolver, ColumnResolver columnResolver2) {
            if (this.arrayIndexes != null) {
                int length = this.arrayIndexes.length;
                for (int i = 0; i < length; i++) {
                    Expression expression = this.arrayIndexes[i];
                    expression.mapColumns(columnResolver, 0, 0);
                    if (columnResolver2 != null) {
                        expression.mapColumns(columnResolver2, 0, 0);
                    }
                    this.arrayIndexes[i] = expression.optimize(sessionLocal);
                }
            }
        }

        @Override // org.h2.command.dml.SetClauseList.UpdateAction
        final Value update(SessionLocal sessionLocal, Value value) {
            Value update = update(sessionLocal);
            if (this.arrayIndexes != null) {
                update = updateArray(sessionLocal, value, update, 0);
            }
            return update;
        }

        private Value updateArray(SessionLocal sessionLocal, Value value, Value value2, int i) {
            Value[] valueArr;
            int i2 = i + 1;
            int i3 = this.arrayIndexes[i].getValue(sessionLocal).getInt();
            if (i3 < 0 || i3 > 65536) {
                throw DbException.get(ErrorCode.ARRAY_ELEMENT_ERROR_2, Integer.toString(i3), "1..65536");
            }
            if (value == null) {
                valueArr = new Value[i3];
                for (int i4 = 0; i4 < i3 - 1; i4++) {
                    valueArr[i4] = ValueNull.INSTANCE;
                }
            } else {
                if (value == ValueNull.INSTANCE) {
                    throw DbException.get(ErrorCode.NULL_VALUE_IN_ARRAY_TARGET);
                }
                if (value.getValueType() != 40) {
                    throw DbException.get(ErrorCode.ARRAY_ELEMENT_ERROR_2, value.getType().getTraceSQL(), "ARRAY");
                }
                Value[] list = ((ValueArray) value).getList();
                int length = list.length;
                if (i3 <= length) {
                    valueArr = (Value[]) list.clone();
                } else {
                    valueArr = (Value[]) Arrays.copyOf(list, i3);
                    for (int i5 = length; i5 < i3 - 1; i5++) {
                        valueArr[i5] = ValueNull.INSTANCE;
                    }
                }
            }
            valueArr[i3 - 1] = i2 == this.arrayIndexes.length ? value2 : updateArray(sessionLocal, valueArr[i3 - 1], value2, i2);
            return ValueArray.get(valueArr, sessionLocal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetClauseList$SetSimple.class */
    public static final class SetSimple extends SetAction {
        private Expression expression;

        SetSimple(Expression[] expressionArr, Expression expression) {
            super(expressionArr);
            this.expression = expression;
        }

        @Override // org.h2.command.dml.SetClauseList.SetAction
        Value update(SessionLocal sessionLocal) {
            return this.expression.getValue(sessionLocal);
        }

        @Override // org.h2.command.dml.SetClauseList.SetAction, org.h2.command.dml.SetClauseList.UpdateAction
        boolean isEverything(ExpressionVisitor expressionVisitor) {
            return super.isEverything(expressionVisitor) && this.expression.isEverything(expressionVisitor);
        }

        @Override // org.h2.command.dml.SetClauseList.SetAction, org.h2.command.dml.SetClauseList.UpdateAction
        void mapAndOptimize(SessionLocal sessionLocal, ColumnResolver columnResolver, ColumnResolver columnResolver2) {
            super.mapAndOptimize(sessionLocal, columnResolver, columnResolver2);
            this.expression.mapColumns(columnResolver, 0, 0);
            if (columnResolver2 != null) {
                this.expression.mapColumns(columnResolver2, 0, 0);
            }
            this.expression = this.expression.optimize(sessionLocal);
        }

        @Override // org.h2.command.dml.SetClauseList.UpdateAction
        void getSQL(StringBuilder sb, int i, Column column) {
            this.expression.getUnenclosedSQL(column.getSQL(sb, i).append(" = "), i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetClauseList$RowExpression.class */
    public static final class RowExpression {
        Expression expression;
        final int[] columns;
        Value[] values;

        RowExpression(Expression expression, int[] iArr) {
            this.expression = expression;
            this.columns = iArr;
        }

        boolean isEverything(ExpressionVisitor expressionVisitor) {
            return this.expression.isEverything(expressionVisitor);
        }

        void mapAndOptimize(SessionLocal sessionLocal, ColumnResolver columnResolver, ColumnResolver columnResolver2) {
            this.expression.mapColumns(columnResolver, 0, 0);
            if (columnResolver2 != null) {
                this.expression.mapColumns(columnResolver2, 0, 0);
            }
            this.expression = this.expression.optimize(sessionLocal);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/SetClauseList$SetMultiple.class */
    public static final class SetMultiple extends SetAction {
        final RowExpression row;
        private final int position;
        boolean first;
        private boolean last;

        SetMultiple(Expression[] expressionArr, RowExpression rowExpression, int i, boolean z, boolean z2) {
            super(expressionArr);
            this.row = rowExpression;
            this.position = i;
            this.first = z;
            this.last = z2;
        }

        @Override // org.h2.command.dml.SetClauseList.SetAction
        Value update(SessionLocal sessionLocal) {
            Value[] valueArr;
            if (this.first) {
                Value value = this.row.expression.getValue(sessionLocal);
                if (value == ValueNull.INSTANCE) {
                    throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "NULL to assigned row value");
                }
                RowExpression rowExpression = this.row;
                Value[] list = value.convertToAnyRow().getList();
                valueArr = list;
                rowExpression.values = list;
                if (valueArr.length != this.row.columns.length) {
                    throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
                }
            } else {
                valueArr = this.row.values;
                if (this.last) {
                    this.row.values = null;
                }
            }
            return valueArr[this.position];
        }

        @Override // org.h2.command.dml.SetClauseList.SetAction, org.h2.command.dml.SetClauseList.UpdateAction
        boolean isEverything(ExpressionVisitor expressionVisitor) {
            return super.isEverything(expressionVisitor) && (!this.first || this.row.isEverything(expressionVisitor));
        }

        @Override // org.h2.command.dml.SetClauseList.SetAction, org.h2.command.dml.SetClauseList.UpdateAction
        void mapAndOptimize(SessionLocal sessionLocal, ColumnResolver columnResolver, ColumnResolver columnResolver2) {
            super.mapAndOptimize(sessionLocal, columnResolver, columnResolver2);
            if (this.first) {
                this.row.mapAndOptimize(sessionLocal, columnResolver, columnResolver2);
            }
        }
    }
}
