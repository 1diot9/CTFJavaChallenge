package org.h2.schema;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.table.ColumnTemplate;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/Domain.class */
public final class Domain extends SchemaObject implements ColumnTemplate {
    private TypeInfo type;
    private Domain domain;
    private Expression defaultExpression;
    private Expression onUpdateExpression;
    private ArrayList<ConstraintDomain> constraints;

    public Domain(Schema schema, int i, String str) {
        super(schema, i, str, 8);
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        return getSQL(new StringBuilder("DROP DOMAIN IF EXISTS "), 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder append = getSQL(new StringBuilder("CREATE DOMAIN "), 0).append(" AS ");
        if (this.domain != null) {
            this.domain.getSQL(append, 0);
        } else {
            this.type.getSQL(append, 0);
        }
        if (this.defaultExpression != null) {
            this.defaultExpression.getUnenclosedSQL(append.append(" DEFAULT "), 0);
        }
        if (this.onUpdateExpression != null) {
            this.onUpdateExpression.getUnenclosedSQL(append.append(" ON UPDATE "), 0);
        }
        return append.toString();
    }

    public void setDataType(TypeInfo typeInfo) {
        this.type = typeInfo;
    }

    public TypeInfo getDataType() {
        return this.type;
    }

    @Override // org.h2.table.ColumnTemplate
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @Override // org.h2.table.ColumnTemplate
    public Domain getDomain() {
        return this.domain;
    }

    @Override // org.h2.table.ColumnTemplate
    public void setDefaultExpression(SessionLocal sessionLocal, Expression expression) {
        if (expression != null) {
            expression = expression.optimize(sessionLocal);
            if (expression.isConstant()) {
                expression = ValueExpression.get(expression.getValue(sessionLocal));
            }
        }
        this.defaultExpression = expression;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getDefaultExpression() {
        return this.defaultExpression;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getEffectiveDefaultExpression() {
        if (this.defaultExpression != null) {
            return this.defaultExpression;
        }
        if (this.domain != null) {
            return this.domain.getEffectiveDefaultExpression();
        }
        return null;
    }

    @Override // org.h2.table.ColumnTemplate
    public String getDefaultSQL() {
        if (this.defaultExpression == null) {
            return null;
        }
        return this.defaultExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
    }

    @Override // org.h2.table.ColumnTemplate
    public void setOnUpdateExpression(SessionLocal sessionLocal, Expression expression) {
        if (expression != null) {
            expression = expression.optimize(sessionLocal);
            if (expression.isConstant()) {
                expression = ValueExpression.get(expression.getValue(sessionLocal));
            }
        }
        this.onUpdateExpression = expression;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getOnUpdateExpression() {
        return this.onUpdateExpression;
    }

    @Override // org.h2.table.ColumnTemplate
    public Expression getEffectiveOnUpdateExpression() {
        if (this.onUpdateExpression != null) {
            return this.onUpdateExpression;
        }
        if (this.domain != null) {
            return this.domain.getEffectiveOnUpdateExpression();
        }
        return null;
    }

    @Override // org.h2.table.ColumnTemplate
    public String getOnUpdateSQL() {
        if (this.onUpdateExpression == null) {
            return null;
        }
        return this.onUpdateExpression.getUnenclosedSQL(new StringBuilder(), 0).toString();
    }

    @Override // org.h2.table.ColumnTemplate
    public void prepareExpressions(SessionLocal sessionLocal) {
        if (this.defaultExpression != null) {
            this.defaultExpression = this.defaultExpression.optimize(sessionLocal);
        }
        if (this.onUpdateExpression != null) {
            this.onUpdateExpression = this.onUpdateExpression.optimize(sessionLocal);
        }
        if (this.domain != null) {
            this.domain.prepareExpressions(sessionLocal);
        }
    }

    public void addConstraint(ConstraintDomain constraintDomain) {
        if (this.constraints == null) {
            this.constraints = Utils.newSmallArrayList();
        }
        if (!this.constraints.contains(constraintDomain)) {
            this.constraints.add(constraintDomain);
        }
    }

    public ArrayList<ConstraintDomain> getConstraints() {
        return this.constraints;
    }

    public void removeConstraint(Constraint constraint) {
        if (this.constraints != null) {
            this.constraints.remove(constraint);
        }
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 12;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        if (this.constraints != null && !this.constraints.isEmpty()) {
            for (ConstraintDomain constraintDomain : (ConstraintDomain[]) this.constraints.toArray(new ConstraintDomain[0])) {
                this.database.removeSchemaObject(sessionLocal, constraintDomain);
            }
            this.constraints = null;
        }
        this.database.removeMeta(sessionLocal, getId());
    }

    public void checkConstraints(SessionLocal sessionLocal, Value value) {
        if (this.constraints != null) {
            Iterator<ConstraintDomain> it = this.constraints.iterator();
            while (it.hasNext()) {
                it.next().check(sessionLocal, value);
            }
        }
        if (this.domain != null) {
            this.domain.checkConstraints(sessionLocal, value);
        }
    }
}
