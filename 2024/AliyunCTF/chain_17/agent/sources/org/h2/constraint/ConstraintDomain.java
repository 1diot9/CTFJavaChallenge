package org.h2.constraint;

import java.util.HashSet;
import org.h2.api.ErrorCode;
import org.h2.command.Parser;
import org.h2.command.ddl.AlterDomain;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.constraint.Constraint;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/ConstraintDomain.class */
public class ConstraintDomain extends Constraint {
    private Domain domain;
    private Expression expr;
    private DomainColumnResolver resolver;

    public ConstraintDomain(Schema schema, int i, String str, Domain domain) {
        super(schema, i, str, null);
        this.domain = domain;
        this.resolver = new DomainColumnResolver(domain.getDataType());
    }

    @Override // org.h2.constraint.Constraint
    public Constraint.Type getConstraintType() {
        return Constraint.Type.DOMAIN;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public void setExpression(SessionLocal sessionLocal, Expression expression) {
        expression.mapColumns(this.resolver, 0, 0);
        Expression optimize = expression.optimize(sessionLocal);
        synchronized (this) {
            this.resolver.setValue(ValueNull.INSTANCE);
            optimize.getValue(sessionLocal);
        }
        this.expr = optimize;
    }

    @Override // org.h2.constraint.Constraint
    public String getCreateSQLWithoutIndexes() {
        return getCreateSQL();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("ALTER DOMAIN ");
        this.domain.getSQL(sb, 0).append(" ADD CONSTRAINT ");
        getSQL(sb, 0);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        sb.append(" CHECK");
        this.expr.getEnclosedSQL(sb, 0).append(" NOCHECK");
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.domain.removeConstraint(this);
        this.database.removeMeta(sessionLocal, getId());
        this.domain = null;
        this.expr = null;
        invalidate();
    }

    @Override // org.h2.constraint.Constraint
    public void checkRow(SessionLocal sessionLocal, Table table, Row row, Row row2) {
        throw DbException.getInternalError(toString());
    }

    public void check(SessionLocal sessionLocal, Value value) {
        Value value2;
        synchronized (this) {
            this.resolver.setValue(value);
            value2 = this.expr.getValue(sessionLocal);
        }
        if (value2.isFalse()) {
            throw DbException.get(ErrorCode.CHECK_CONSTRAINT_VIOLATED_1, this.expr.getTraceSQL());
        }
    }

    public Expression getCheckConstraint(SessionLocal sessionLocal, String str) {
        String sql;
        String sql2;
        if (str != null) {
            synchronized (this) {
                try {
                    this.resolver.setColumnName(str);
                    sql2 = this.expr.getSQL(0);
                    this.resolver.resetColumnName();
                } catch (Throwable th) {
                    this.resolver.resetColumnName();
                    throw th;
                }
            }
            return new Parser(sessionLocal).parseExpression(sql2);
        }
        synchronized (this) {
            sql = this.expr.getSQL(0);
        }
        return new Parser(sessionLocal).parseDomainConstraintExpression(sql);
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
        new CheckExistingData(sessionLocal, this.domain);
    }

    @Override // org.h2.constraint.Constraint
    public void rebuild() {
    }

    @Override // org.h2.constraint.Constraint
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.expr.isEverything(expressionVisitor);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/ConstraintDomain$CheckExistingData.class */
    private class CheckExistingData {
        private final SessionLocal session;

        CheckExistingData(SessionLocal sessionLocal, Domain domain) {
            this.session = sessionLocal;
            checkDomain(null, domain);
        }

        private boolean checkColumn(Domain domain, Column column) {
            TableFilter tableFilter = new TableFilter(this.session, column.getTable(), null, true, null, 0, null);
            TableFilter[] tableFilterArr = {tableFilter};
            tableFilter.setPlanItem(tableFilter.getBestPlanItem(this.session, tableFilterArr, 0, new AllColumnsForPlan(tableFilterArr)));
            tableFilter.prepare();
            tableFilter.startQuery(this.session);
            tableFilter.reset();
            while (tableFilter.next()) {
                ConstraintDomain.this.check(this.session, tableFilter.getValue(column));
            }
            return false;
        }

        private boolean checkDomain(Domain domain, Domain domain2) {
            AlterDomain.forAllDependencies(this.session, domain2, this::checkColumn, this::checkDomain, false);
            return false;
        }
    }
}
