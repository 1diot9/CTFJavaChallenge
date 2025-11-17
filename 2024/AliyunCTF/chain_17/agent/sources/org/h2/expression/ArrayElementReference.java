package org.h2.expression;

import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONValue;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ArrayElementReference.class */
public final class ArrayElementReference extends Operation2 {
    public ArrayElementReference(Expression expression, Expression expression2) {
        super(expression, expression2);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.left.getSQL(sb, i, 0).append('[');
        return this.right.getUnenclosedSQL(sb, i).append(']');
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        JSONValue element;
        Value value = this.left.getValue(sessionLocal);
        Value value2 = this.right.getValue(sessionLocal);
        if (value != ValueNull.INSTANCE && value2 != ValueNull.INSTANCE) {
            int i = value2.getInt();
            if (this.left.getType().getValueType() == 40) {
                Value[] list = ((ValueArray) value).getList();
                int length = list.length;
                if (i >= 1 && i <= length) {
                    return list[i - 1];
                }
                throw DbException.get(ErrorCode.ARRAY_ELEMENT_ERROR_2, Integer.toString(i), "1.." + length);
            }
            JSONValue decomposition = value.convertToAnyJson().getDecomposition();
            if ((decomposition instanceof JSONArray) && (element = ((JSONArray) decomposition).getElement(i - 1)) != null) {
                return ValueJson.fromJson(element);
            }
        }
        return ValueNull.INSTANCE;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        TypeInfo type = this.left.getType();
        switch (type.getValueType()) {
            case 0:
                return ValueExpression.NULL;
            case 38:
                this.type = TypeInfo.TYPE_JSON;
                if (this.left.isConstant() && this.right.isConstant()) {
                    return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
                }
                break;
            case 40:
                this.type = (TypeInfo) type.getExtTypeInfo();
                if (this.left.isConstant() && this.right.isConstant()) {
                    return TypedValueExpression.get(getValue(sessionLocal), this.type);
                }
                break;
            default:
                throw Store.getInvalidExpressionTypeException("Array", this.left);
        }
        return this;
    }
}
