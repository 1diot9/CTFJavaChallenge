package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.schema.Domain;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/ColumnTemplate.class */
public interface ColumnTemplate {
    Domain getDomain();

    void setDomain(Domain domain);

    void setDefaultExpression(SessionLocal sessionLocal, Expression expression);

    Expression getDefaultExpression();

    Expression getEffectiveDefaultExpression();

    String getDefaultSQL();

    void setOnUpdateExpression(SessionLocal sessionLocal, Expression expression);

    Expression getOnUpdateExpression();

    Expression getEffectiveOnUpdateExpression();

    String getOnUpdateSQL();

    void prepareExpressions(SessionLocal sessionLocal);
}
