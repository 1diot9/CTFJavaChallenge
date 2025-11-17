package org.h2.constraint;

import java.util.HashSet;
import org.h2.api.ErrorCode;
import org.h2.constraint.Constraint;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/ConstraintCheck.class */
public class ConstraintCheck extends Constraint {
    private TableFilter filter;
    private Expression expr;

    public ConstraintCheck(Schema schema, int i, String str, Table table) {
        super(schema, i, str, table);
    }

    @Override // org.h2.constraint.Constraint
    public Constraint.Type getConstraintType() {
        return Constraint.Type.CHECK;
    }

    public void setTableFilter(TableFilter tableFilter) {
        this.filter = tableFilter;
    }

    public void setExpression(Expression expression) {
        this.expr = expression;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
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
        sb.append(" CHECK");
        this.expr.getEnclosedSQL(sb, 0).append(" NOCHECK");
        return sb.toString();
    }

    private String getShortDescription() {
        StringBuilder append = new StringBuilder().append(getName()).append(": ");
        this.expr.getTraceSQL();
        return append.toString();
    }

    @Override // org.h2.constraint.Constraint
    public String getCreateSQLWithoutIndexes() {
        return getCreateSQL();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQLForCopy(this.table, getSQL(0));
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.table.removeConstraint(this);
        this.database.removeMeta(sessionLocal, getId());
        this.filter = null;
        this.expr = null;
        this.table = null;
        invalidate();
    }

    @Override // org.h2.constraint.Constraint
    public void checkRow(SessionLocal sessionLocal, Table table, Row row, Row row2) {
        Value value;
        if (row2 == null) {
            return;
        }
        try {
            synchronized (this) {
                this.filter.set(row2);
                value = this.expr.getValue(sessionLocal);
            }
            if (value.isFalse()) {
                throw DbException.get(ErrorCode.CHECK_CONSTRAINT_VIOLATED_1, getShortDescription());
            }
        } catch (DbException e) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_INVALID, e, getShortDescription());
        }
    }

    @Override // org.h2.constraint.Constraint
    public boolean usesIndex(Index index) {
        return false;
    }

    @Override // org.h2.constraint.Constraint
    public void setIndexOwner(Index index) {
        throw DbException.getInternalError(toString());
    }

    @Override // org.h2.constraint.Constraint
    public HashSet<Column> getReferencedColumns(Table table) {
        HashSet<Column> hashSet = new HashSet<>();
        this.expr.isEverything(ExpressionVisitor.getColumnsVisitor(hashSet, table));
        return hashSet;
    }

    @Override // org.h2.constraint.Constraint
    public Expression getExpression() {
        return this.expr;
    }

    @Override // org.h2.constraint.Constraint
    public boolean isBefore() {
        return true;
    }

    @Override // org.h2.constraint.Constraint
    public void checkExistingData(SessionLocal sessionLocal) {
        if (sessionLocal.getDatabase().isStarting()) {
            return;
        }
        StringBuilder append = new StringBuilder().append("SELECT NULL FROM ");
        this.filter.getTable().getSQL(append, 0).append(" WHERE NOT ");
        this.expr.getSQL(append, 0, 0);
        if (sessionLocal.prepare(append.toString()).query(1L).next()) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_VIOLATED_1, getName());
        }
    }

    @Override // org.h2.constraint.Constraint
    public void rebuild() {
    }

    @Override // org.h2.constraint.Constraint
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.expr.isEverything(expressionVisitor);
    }
}
