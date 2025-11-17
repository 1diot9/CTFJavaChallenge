package org.h2.expression;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ExpressionWithVariableParameters.class */
public interface ExpressionWithVariableParameters {
    void addParameter(Expression expression);

    void doneWithParameters() throws DbException;
}
