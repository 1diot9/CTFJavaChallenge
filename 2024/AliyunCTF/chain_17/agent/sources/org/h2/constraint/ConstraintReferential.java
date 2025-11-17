package org.h2.constraint;

import cn.hutool.core.text.CharSequenceUtil;
import java.util.ArrayList;
import java.util.HashSet;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.constraint.Constraint;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/ConstraintReferential.class */
public class ConstraintReferential extends Constraint {
    private IndexColumn[] columns;
    private IndexColumn[] refColumns;
    private ConstraintActionType deleteAction;
    private ConstraintActionType updateAction;
    private Table refTable;
    private Index index;
    private ConstraintUnique refConstraint;
    private boolean indexOwner;
    private String deleteSQL;
    private String updateSQL;
    private boolean skipOwnTable;

    public ConstraintReferential(Schema schema, int i, String str, Table table) {
        super(schema, i, str, table);
        this.deleteAction = ConstraintActionType.RESTRICT;
        this.updateAction = ConstraintActionType.RESTRICT;
    }

    @Override // org.h2.constraint.Constraint
    public Constraint.Type getConstraintType() {
        return Constraint.Type.REFERENTIAL;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        return getCreateSQLForCopy(table, this.refTable, str, true);
    }

    public String getCreateSQLForCopy(Table table, Table table2, String str, boolean z) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        table.getSQL(sb, 0).append(" ADD CONSTRAINT ");
        if (table.isHidden()) {
            sb.append("IF NOT EXISTS ");
        }
        sb.append(str);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        IndexColumn[] indexColumnArr = this.columns;
        IndexColumn[] indexColumnArr2 = this.refColumns;
        sb.append(" FOREIGN KEY(");
        IndexColumn.writeColumns(sb, indexColumnArr, 0);
        sb.append(')');
        if (z && this.indexOwner && table == this.table) {
            sb.append(" INDEX ");
            this.index.getSQL(sb, 0);
        }
        sb.append(" REFERENCES ");
        if (this.table == this.refTable) {
            table.getSQL(sb, 0);
        } else {
            table2.getSQL(sb, 0);
        }
        sb.append('(');
        IndexColumn.writeColumns(sb, indexColumnArr2, 0);
        sb.append(')');
        if (this.deleteAction != ConstraintActionType.RESTRICT) {
            sb.append(" ON DELETE ").append(this.deleteAction.getSqlName());
        }
        if (this.updateAction != ConstraintActionType.RESTRICT) {
            sb.append(" ON UPDATE ").append(this.updateAction.getSqlName());
        }
        return sb.append(" NOCHECK").toString();
    }

    private String getShortDescription(Index index, SearchRow searchRow) {
        StringBuilder append = new StringBuilder(getName()).append(": ");
        this.table.getSQL(append, 3).append(" FOREIGN KEY(");
        IndexColumn.writeColumns(append, this.columns, 3);
        append.append(") REFERENCES ");
        this.refTable.getSQL(append, 3).append('(');
        IndexColumn.writeColumns(append, this.refColumns, 3);
        append.append(')');
        if (index != null && searchRow != null) {
            append.append(" (");
            Column[] columns = index.getColumns();
            int min = Math.min(this.columns.length, columns.length);
            for (int i = 0; i < min; i++) {
                Value value = searchRow.getValue(columns[i].getColumnId());
                if (i > 0) {
                    append.append(", ");
                }
                append.append(value == null ? "" : value.toString());
            }
            append.append(')');
        }
        return append.toString();
    }

    @Override // org.h2.constraint.Constraint
    public String getCreateSQLWithoutIndexes() {
        return getCreateSQLForCopy(this.table, this.refTable, getSQL(0), false);
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQLForCopy(this.table, getSQL(0));
    }

    public void setColumns(IndexColumn[] indexColumnArr) {
        this.columns = indexColumnArr;
    }

    public IndexColumn[] getColumns() {
        return this.columns;
    }

    @Override // org.h2.constraint.Constraint
    public HashSet<Column> getReferencedColumns(Table table) {
        HashSet<Column> hashSet = new HashSet<>();
        if (table == this.table) {
            for (IndexColumn indexColumn : this.columns) {
                hashSet.add(indexColumn.column);
            }
        } else if (table == this.refTable) {
            for (IndexColumn indexColumn2 : this.refColumns) {
                hashSet.add(indexColumn2.column);
            }
        }
        return hashSet;
    }

    public void setRefColumns(IndexColumn[] indexColumnArr) {
        this.refColumns = indexColumnArr;
    }

    public IndexColumn[] getRefColumns() {
        return this.refColumns;
    }

    public void setRefTable(Table table) {
        this.refTable = table;
        if (table.isTemporary()) {
            setTemporary(true);
        }
    }

    public void setIndex(Index index, boolean z) {
        this.index = index;
        this.indexOwner = z;
    }

    public void setRefConstraint(ConstraintUnique constraintUnique) {
        this.refConstraint = constraintUnique;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.table.removeConstraint(this);
        this.refTable.removeConstraint(this);
        if (this.indexOwner) {
            this.table.removeIndexOrTransferOwnership(sessionLocal, this.index);
        }
        this.database.removeMeta(sessionLocal, getId());
        this.refTable = null;
        this.index = null;
        this.refConstraint = null;
        this.columns = null;
        this.refColumns = null;
        this.deleteSQL = null;
        this.updateSQL = null;
        this.table = null;
        invalidate();
    }

    @Override // org.h2.constraint.Constraint
    public void checkRow(SessionLocal sessionLocal, Table table, Row row, Row row2) {
        if (!this.database.getReferentialIntegrity() || !this.table.getCheckForeignKeyConstraints() || !this.refTable.getCheckForeignKeyConstraints()) {
            return;
        }
        if (table == this.table && !this.skipOwnTable) {
            checkRowOwnTable(sessionLocal, row, row2);
        }
        if (table == this.refTable) {
            checkRowRefTable(sessionLocal, row, row2);
        }
    }

    private void checkRowOwnTable(SessionLocal sessionLocal, Row row, Row row2) {
        if (row2 == null) {
            return;
        }
        boolean z = row != null;
        for (IndexColumn indexColumn : this.columns) {
            int columnId = indexColumn.column.getColumnId();
            Value value = row2.getValue(columnId);
            if (value == ValueNull.INSTANCE) {
                return;
            }
            if (z && !sessionLocal.areEqual(value, row.getValue(columnId))) {
                z = false;
            }
        }
        if (z) {
            return;
        }
        if (this.refTable == this.table) {
            boolean z2 = true;
            int i = 0;
            int length = this.columns.length;
            while (true) {
                if (i >= length) {
                    break;
                }
                if (sessionLocal.areEqual(row2.getValue(this.refColumns[i].column.getColumnId()), row2.getValue(this.columns[i].column.getColumnId()))) {
                    i++;
                } else {
                    z2 = false;
                    break;
                }
            }
            if (z2) {
                return;
            }
        }
        Row templateRow = this.refTable.getTemplateRow();
        int length2 = this.columns.length;
        for (int i2 = 0; i2 < length2; i2++) {
            Value value2 = row2.getValue(this.columns[i2].column.getColumnId());
            Column column = this.refColumns[i2].column;
            templateRow.setValue(column.getColumnId(), column.convert(sessionLocal, value2));
        }
        Index index = this.refConstraint.getIndex();
        if (!existsRow(sessionLocal, index, templateRow, null)) {
            throw DbException.get(ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1, getShortDescription(index, templateRow));
        }
    }

    private boolean existsRow(SessionLocal sessionLocal, Index index, SearchRow searchRow, Row row) {
        Table table = index.getTable();
        table.lock(sessionLocal, 0);
        Cursor find = index.find(sessionLocal, searchRow, searchRow);
        while (find.next()) {
            SearchRow searchRow2 = find.getSearchRow();
            if (row == null || searchRow2.getKey() != row.getKey()) {
                Column[] columns = index.getColumns();
                boolean z = true;
                int min = Math.min(this.columns.length, columns.length);
                int i = 0;
                while (true) {
                    if (i >= min) {
                        break;
                    }
                    int columnId = columns[i].getColumnId();
                    if (table.compareValues(sessionLocal, searchRow.getValue(columnId), searchRow2.getValue(columnId)) == 0) {
                        i++;
                    } else {
                        z = false;
                        break;
                    }
                }
                if (z) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isEqual(Row row, Row row2) {
        return this.refConstraint.getIndex().compareRows(row, row2) == 0;
    }

    private void checkRow(SessionLocal sessionLocal, Row row) {
        SearchRow createRow = this.table.getRowFactory().createRow();
        int length = this.columns.length;
        for (int i = 0; i < length; i++) {
            int columnId = this.refColumns[i].column.getColumnId();
            Column column = this.columns[i].column;
            Value convert = column.convert(sessionLocal, row.getValue(columnId));
            if (convert == ValueNull.INSTANCE) {
                return;
            }
            createRow.setValue(column.getColumnId(), convert);
        }
        if (existsRow(sessionLocal, this.index, createRow, this.refTable == this.table ? row : null)) {
            throw DbException.get(ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_CHILD_EXISTS_1, getShortDescription(this.index, createRow));
        }
    }

    private void checkRowRefTable(SessionLocal sessionLocal, Row row, Row row2) {
        if (row == null) {
            return;
        }
        if (row2 != null && isEqual(row, row2)) {
            return;
        }
        if (row2 == null) {
            if (this.deleteAction == ConstraintActionType.RESTRICT) {
                checkRow(sessionLocal, row);
                return;
            }
            int length = this.deleteAction == ConstraintActionType.CASCADE ? 0 : this.columns.length;
            Prepared delete = getDelete(sessionLocal);
            setWhere(delete, length, row);
            updateWithSkipCheck(delete);
            return;
        }
        if (this.updateAction == ConstraintActionType.RESTRICT) {
            checkRow(sessionLocal, row);
            return;
        }
        Prepared update = getUpdate(sessionLocal);
        if (this.updateAction == ConstraintActionType.CASCADE) {
            ArrayList<Parameter> parameters = update.getParameters();
            int length2 = this.columns.length;
            for (int i = 0; i < length2; i++) {
                parameters.get(i).setValue(row2.getValue(this.refColumns[i].column.getColumnId()));
            }
        }
        setWhere(update, this.columns.length, row);
        updateWithSkipCheck(update);
    }

    private void updateWithSkipCheck(Prepared prepared) {
        try {
            this.skipOwnTable = true;
            prepared.update();
        } finally {
            this.skipOwnTable = false;
        }
    }

    private void setWhere(Prepared prepared, int i, Row row) {
        int length = this.refColumns.length;
        for (int i2 = 0; i2 < length; i2++) {
            prepared.getParameters().get(i + i2).setValue(row.getValue(this.refColumns[i2].column.getColumnId()));
        }
    }

    public ConstraintActionType getDeleteAction() {
        return this.deleteAction;
    }

    public void setDeleteAction(ConstraintActionType constraintActionType) {
        if (constraintActionType == this.deleteAction && this.deleteSQL == null) {
            return;
        }
        if (this.deleteAction != ConstraintActionType.RESTRICT) {
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, "ON DELETE");
        }
        this.deleteAction = constraintActionType;
        buildDeleteSQL();
    }

    public void updateOnTableColumnRename() {
        if (this.deleteAction != null) {
            this.deleteSQL = null;
            buildDeleteSQL();
        }
        if (this.updateAction != null) {
            this.updateSQL = null;
            buildUpdateSQL();
        }
    }

    private void buildDeleteSQL() {
        if (this.deleteAction == ConstraintActionType.RESTRICT) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (this.deleteAction == ConstraintActionType.CASCADE) {
            sb.append("DELETE FROM ");
            this.table.getSQL(sb, 0);
        } else {
            appendUpdate(sb);
        }
        appendWhere(sb);
        this.deleteSQL = sb.toString();
    }

    private Prepared getUpdate(SessionLocal sessionLocal) {
        return prepare(sessionLocal, this.updateSQL, this.updateAction);
    }

    private Prepared getDelete(SessionLocal sessionLocal) {
        return prepare(sessionLocal, this.deleteSQL, this.deleteAction);
    }

    public ConstraintActionType getUpdateAction() {
        return this.updateAction;
    }

    public void setUpdateAction(ConstraintActionType constraintActionType) {
        if (constraintActionType == this.updateAction && this.updateSQL == null) {
            return;
        }
        if (this.updateAction != ConstraintActionType.RESTRICT) {
            throw DbException.get(ErrorCode.CONSTRAINT_ALREADY_EXISTS_1, "ON UPDATE");
        }
        this.updateAction = constraintActionType;
        buildUpdateSQL();
    }

    private void buildUpdateSQL() {
        if (this.updateAction == ConstraintActionType.RESTRICT) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        appendUpdate(sb);
        appendWhere(sb);
        this.updateSQL = sb.toString();
    }

    @Override // org.h2.constraint.Constraint
    public void rebuild() {
        buildUpdateSQL();
        buildDeleteSQL();
    }

    private Prepared prepare(SessionLocal sessionLocal, String str, ConstraintActionType constraintActionType) {
        Value value;
        Prepared prepare = sessionLocal.prepare(str);
        if (constraintActionType != ConstraintActionType.CASCADE) {
            ArrayList<Parameter> parameters = prepare.getParameters();
            int length = this.columns.length;
            for (int i = 0; i < length; i++) {
                Column column = this.columns[i].column;
                Parameter parameter = parameters.get(i);
                if (constraintActionType == ConstraintActionType.SET_NULL) {
                    value = ValueNull.INSTANCE;
                } else {
                    Expression effectiveDefaultExpression = column.getEffectiveDefaultExpression();
                    if (effectiveDefaultExpression == null) {
                        throw DbException.get(ErrorCode.NO_DEFAULT_SET_1, column.getName());
                    }
                    value = effectiveDefaultExpression.getValue(sessionLocal);
                }
                parameter.setValue(value);
            }
        }
        return prepare;
    }

    private void appendUpdate(StringBuilder sb) {
        sb.append("UPDATE ");
        this.table.getSQL(sb, 0).append(" SET ");
        IndexColumn.writeColumns(sb, this.columns, ", ", "=?", Integer.MIN_VALUE);
    }

    private void appendWhere(StringBuilder sb) {
        sb.append(" WHERE ");
        IndexColumn.writeColumns(sb, this.columns, " AND ", "=?", Integer.MIN_VALUE);
    }

    @Override // org.h2.constraint.Constraint
    public Table getRefTable() {
        return this.refTable;
    }

    @Override // org.h2.constraint.Constraint
    public boolean usesIndex(Index index) {
        return index == this.index;
    }

    @Override // org.h2.constraint.Constraint
    public void setIndexOwner(Index index) {
        if (this.index == index) {
            this.indexOwner = true;
            return;
        }
        throw DbException.getInternalError(index + CharSequenceUtil.SPACE + toString());
    }

    @Override // org.h2.constraint.Constraint
    public boolean isBefore() {
        return false;
    }

    @Override // org.h2.constraint.Constraint
    public void checkExistingData(SessionLocal sessionLocal) {
        if (sessionLocal.getDatabase().isStarting()) {
            return;
        }
        StringBuilder sb = new StringBuilder("SELECT 1 FROM (SELECT ");
        IndexColumn.writeColumns(sb, this.columns, Integer.MIN_VALUE);
        sb.append(" FROM ");
        this.table.getSQL(sb, 0).append(" WHERE ");
        IndexColumn.writeColumns(sb, this.columns, " AND ", " IS NOT NULL ", Integer.MIN_VALUE);
        sb.append(" ORDER BY ");
        IndexColumn.writeColumns(sb, this.columns, 0);
        sb.append(") C WHERE NOT EXISTS(SELECT 1 FROM ");
        this.refTable.getSQL(sb, 0).append(" P WHERE ");
        int length = this.columns.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(" AND ");
            }
            sb.append("C.");
            this.columns[i].column.getSQL(sb, 0).append('=').append("P.");
            this.refColumns[i].column.getSQL(sb, 0);
        }
        sb.append(')');
        sessionLocal.startStatementWithinTransaction(null);
        try {
            if (sessionLocal.prepare(sb.toString()).query(1L).next()) {
                throw DbException.get(ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1, getShortDescription(null, null));
            }
        } finally {
            sessionLocal.endStatement();
        }
    }

    @Override // org.h2.constraint.Constraint
    public Index getIndex() {
        return this.index;
    }

    @Override // org.h2.constraint.Constraint
    public ConstraintUnique getReferencedConstraint() {
        return this.refConstraint;
    }
}
