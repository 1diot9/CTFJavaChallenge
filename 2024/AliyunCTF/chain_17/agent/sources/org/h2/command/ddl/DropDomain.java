package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.constraint.ConstraintActionType;
import org.h2.constraint.ConstraintDomain;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.ColumnTemplate;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropDomain.class */
public class DropDomain extends AlterDomain {
    private ConstraintActionType dropAction;

    public DropDomain(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.dropAction = getDatabase().getSettings().dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
    }

    public void setDropAction(ConstraintActionType constraintActionType) {
        this.dropAction = constraintActionType;
    }

    @Override // org.h2.command.ddl.AlterDomain
    long update(Schema schema, Domain domain) {
        forAllDependencies(this.session, domain, this::copyColumn, this::copyDomain, true);
        getDatabase().removeSchemaObject(this.session, domain);
        return 0L;
    }

    private boolean copyColumn(Domain domain, Column column) {
        Table table = column.getTable();
        if (this.dropAction == ConstraintActionType.RESTRICT) {
            throw DbException.get(ErrorCode.CANNOT_DROP_2, this.domainName, table.getCreateSQL());
        }
        String name = column.getName();
        ArrayList<ConstraintDomain> constraints = domain.getConstraints();
        if (constraints != null && !constraints.isEmpty()) {
            Iterator<ConstraintDomain> it = constraints.iterator();
            while (it.hasNext()) {
                Expression checkConstraint = it.next().getCheckConstraint(this.session, name);
                AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, table.getSchema(), 3, false);
                alterTableAddConstraint.setTableName(table.getName());
                alterTableAddConstraint.setCheckExpression(checkConstraint);
                alterTableAddConstraint.update();
            }
        }
        copyExpressions(this.session, domain, column);
        return true;
    }

    private boolean copyDomain(Domain domain, Domain domain2) {
        if (this.dropAction == ConstraintActionType.RESTRICT) {
            throw DbException.get(ErrorCode.CANNOT_DROP_2, this.domainName, domain2.getTraceSQL());
        }
        ArrayList<ConstraintDomain> constraints = domain.getConstraints();
        if (constraints != null && !constraints.isEmpty()) {
            Iterator<ConstraintDomain> it = constraints.iterator();
            while (it.hasNext()) {
                Expression checkConstraint = it.next().getCheckConstraint(this.session, null);
                AlterDomainAddConstraint alterDomainAddConstraint = new AlterDomainAddConstraint(this.session, domain2.getSchema(), false);
                alterDomainAddConstraint.setDomainName(domain2.getName());
                alterDomainAddConstraint.setCheckExpression(checkConstraint);
                alterDomainAddConstraint.update();
            }
        }
        copyExpressions(this.session, domain, domain2);
        return true;
    }

    private static boolean copyExpressions(SessionLocal sessionLocal, Domain domain, ColumnTemplate columnTemplate) {
        columnTemplate.setDomain(domain.getDomain());
        Expression defaultExpression = domain.getDefaultExpression();
        boolean z = false;
        if (defaultExpression != null && columnTemplate.getDefaultExpression() == null) {
            columnTemplate.setDefaultExpression(sessionLocal, defaultExpression);
            z = true;
        }
        Expression onUpdateExpression = domain.getOnUpdateExpression();
        if (onUpdateExpression != null && columnTemplate.getOnUpdateExpression() == null) {
            columnTemplate.setOnUpdateExpression(sessionLocal, onUpdateExpression);
            z = true;
        }
        return z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 47;
    }
}
