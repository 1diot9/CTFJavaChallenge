package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.ColumnTemplate;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterDomainExpressions.class */
public class AlterDomainExpressions extends AlterDomain {
    private final int type;
    private Expression expression;

    public AlterDomainExpressions(SessionLocal sessionLocal, Schema schema, int i) {
        super(sessionLocal, schema);
        this.type = i;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override // org.h2.command.ddl.AlterDomain
    long update(Schema schema, Domain domain) {
        switch (this.type) {
            case 94:
                domain.setDefaultExpression(this.session, this.expression);
                break;
            case 95:
                domain.setOnUpdateExpression(this.session, this.expression);
                break;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        if (this.expression != null) {
            forAllDependencies(this.session, domain, this::copyColumn, this::copyDomain, true);
        }
        getDatabase().updateMeta(this.session, domain);
        return 0L;
    }

    private boolean copyColumn(Domain domain, Column column) {
        return copyExpressions(this.session, domain, column);
    }

    private boolean copyDomain(Domain domain, Domain domain2) {
        return copyExpressions(this.session, domain, domain2);
    }

    private boolean copyExpressions(SessionLocal sessionLocal, Domain domain, ColumnTemplate columnTemplate) {
        switch (this.type) {
            case 94:
                Expression defaultExpression = domain.getDefaultExpression();
                if (defaultExpression != null && columnTemplate.getDefaultExpression() == null) {
                    columnTemplate.setDefaultExpression(sessionLocal, defaultExpression);
                    return true;
                }
                return false;
            case 95:
                Expression onUpdateExpression = domain.getOnUpdateExpression();
                if (onUpdateExpression != null && columnTemplate.getOnUpdateExpression() == null) {
                    columnTemplate.setOnUpdateExpression(sessionLocal, onUpdateExpression);
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.type;
    }
}
