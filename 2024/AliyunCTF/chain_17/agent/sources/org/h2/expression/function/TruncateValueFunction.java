package org.h2.expression.function;

import java.math.BigDecimal;
import java.math.MathContext;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueNumeric;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/TruncateValueFunction.class */
public final class TruncateValueFunction extends FunctionN {
    public TruncateValueFunction(Expression expression, Expression expression2, Expression expression3) {
        super(new Expression[]{expression, expression2, expression3});
    }

    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        BigDecimal bigDecimal;
        long j = value2.getLong();
        boolean z = value3.getBoolean();
        if (j <= 0) {
            throw DbException.get(ErrorCode.INVALID_VALUE_PRECISION, Long.toString(j), CustomBooleanEditor.VALUE_1, "2147483647");
        }
        TypeInfo type = value.getType();
        int valueType = type.getValueType();
        if (DataType.getDataType(valueType).supportsPrecision) {
            if (j < type.getPrecision()) {
                switch (valueType) {
                    case 13:
                        BigDecimal round = value.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(j)));
                        if (round.scale() < 0) {
                            round = round.setScale(0);
                        }
                        return ValueNumeric.get(round);
                    case 16:
                        return ValueDecfloat.get(value.getBigDecimal().round(new MathContext(MathUtils.convertLongToInt(j))));
                    default:
                        return value.castTo(TypeInfo.getTypeInfo(valueType, j, type.getScale(), type.getExtTypeInfo()), sessionLocal);
                }
            }
        } else if (z) {
            switch (valueType) {
                case 9:
                case 10:
                case 11:
                    bigDecimal = BigDecimal.valueOf(value.getInt());
                    break;
                case 12:
                    bigDecimal = BigDecimal.valueOf(value.getLong());
                    break;
                case 13:
                default:
                    return value;
                case 14:
                case 15:
                    bigDecimal = value.getBigDecimal();
                    break;
            }
            BigDecimal round2 = bigDecimal.round(new MathContext(MathUtils.convertLongToInt(j)));
            if (valueType == 16) {
                return ValueDecfloat.get(round2);
            }
            if (round2.scale() < 0) {
                round2 = round2.setScale(0);
            }
            return ValueNumeric.get(round2).convertTo(valueType);
        }
        return value;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        this.type = this.args[0].getType();
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "TRUNCATE_VALUE";
    }
}
