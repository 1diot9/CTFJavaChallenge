package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONValue;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/CardinalityExpression.class */
public final class CardinalityExpression extends Function1 {
    private final boolean max;

    public CardinalityExpression(Expression expression, boolean z) {
        super(expression);
        this.max = z;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        int length;
        if (this.max) {
            TypeInfo type = this.arg.getType();
            if (type.getValueType() == 40) {
                length = MathUtils.convertLongToInt(type.getPrecision());
            } else {
                throw DbException.getInvalidValueException(BeanDefinitionParserDelegate.ARRAY_ELEMENT, this.arg.getValue(sessionLocal).getTraceSQL());
            }
        } else {
            Value value = this.arg.getValue(sessionLocal);
            if (value == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            switch (value.getValueType()) {
                case 38:
                    JSONValue decomposition = value.convertToAnyJson().getDecomposition();
                    if (decomposition instanceof JSONArray) {
                        length = ((JSONArray) decomposition).length();
                        break;
                    } else {
                        return ValueNull.INSTANCE;
                    }
                case 40:
                    length = ((ValueArray) value).getList().length;
                    break;
                default:
                    throw DbException.getInvalidValueException(BeanDefinitionParserDelegate.ARRAY_ELEMENT, value.getTraceSQL());
            }
        }
        return ValueInteger.get(length);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        this.type = TypeInfo.TYPE_INTEGER;
        if (this.arg.isConstant()) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return this.max ? "ARRAY_MAX_CARDINALITY" : "CARDINALITY";
    }
}
