package org.h2.expression;

import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.util.ParserUtil;
import org.h2.util.json.JSONObject;
import org.h2.util.json.JSONValue;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/FieldReference.class */
public final class FieldReference extends Operation1 {
    private final String fieldName;
    private int ordinal;

    public FieldReference(Expression expression, String str) {
        super(expression);
        this.fieldName = str;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return ParserUtil.quoteIdentifier(this.arg.getEnclosedSQL(sb, i).append('.'), this.fieldName, i);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        JSONValue first;
        Value value = this.arg.getValue(sessionLocal);
        if (value != ValueNull.INSTANCE) {
            if (this.ordinal >= 0) {
                return ((ValueRow) value).getList()[this.ordinal];
            }
            JSONValue decomposition = value.convertToAnyJson().getDecomposition();
            if ((decomposition instanceof JSONObject) && (first = ((JSONObject) decomposition).getFirst(this.fieldName)) != null) {
                return ValueJson.fromJson(first);
            }
        }
        return ValueNull.INSTANCE;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.arg = this.arg.optimize(sessionLocal);
        TypeInfo type = this.arg.getType();
        switch (type.getValueType()) {
            case 38:
                this.type = TypeInfo.TYPE_JSON;
                this.ordinal = -1;
                break;
            case 41:
                int i = 0;
                for (Map.Entry<String, TypeInfo> entry : ((ExtTypeInfoRow) type.getExtTypeInfo()).getFields()) {
                    if (this.fieldName.equals(entry.getKey())) {
                        type = entry.getValue();
                        this.type = type;
                        this.ordinal = i;
                        break;
                    } else {
                        i++;
                    }
                }
                throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, this.fieldName);
            default:
                throw Store.getInvalidExpressionTypeException("JSON | ROW", this.arg);
        }
        if (this.arg.isConstant()) {
            return TypedValueExpression.get(getValue(sessionLocal), type);
        }
        return this;
    }
}
