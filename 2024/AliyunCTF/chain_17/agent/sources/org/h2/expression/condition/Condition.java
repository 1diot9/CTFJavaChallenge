package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.function.CastSpecification;
import org.h2.value.TypeInfo;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/Condition.class */
public abstract class Condition extends Expression {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static Expression castToBoolean(SessionLocal sessionLocal, Expression expression) {
        if (expression.getType().getValueType() == 8) {
            return expression;
        }
        return new CastSpecification(expression, TypeInfo.TYPE_BOOLEAN);
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return TypeInfo.TYPE_BOOLEAN;
    }
}
