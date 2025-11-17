package org.h2.expression.function;

import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/SubstringFunction.class */
public final class SubstringFunction extends FunctionN {
    public SubstringFunction() {
        super(new Expression[3]);
    }

    @Override // org.h2.expression.function.FunctionN
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2, Value value3) {
        if (this.type.getValueType() == 6) {
            byte[] bytesNoCopy = value.getBytesNoCopy();
            int length = bytesNoCopy.length;
            int i = value2.getInt();
            if (i == 0) {
                i = 1;
            } else if (i < 0) {
                i = length + i + 1;
            }
            int max = value3 == null ? Math.max(length + 1, i) : i + value3.getInt();
            int max2 = Math.max(i, 1);
            int min = Math.min(max, length + 1);
            if (max2 > length || min <= max2) {
                return ValueVarbinary.EMPTY;
            }
            int i2 = max2 - 1;
            int i3 = min - 1;
            if (i2 == 0 && i3 == bytesNoCopy.length) {
                return value.convertTo(TypeInfo.TYPE_VARBINARY);
            }
            return ValueVarbinary.getNoCopy(Arrays.copyOfRange(bytesNoCopy, i2, i3));
        }
        String string = value.getString();
        int length2 = string.length();
        int i4 = value2.getInt();
        if (i4 == 0) {
            i4 = 1;
        } else if (i4 < 0) {
            i4 = length2 + i4 + 1;
        }
        int max3 = value3 == null ? Math.max(length2 + 1, i4) : i4 + value3.getInt();
        int max4 = Math.max(i4, 1);
        int min2 = Math.min(max3, length2 + 1);
        if (max4 > length2 || min2 <= max4) {
            return sessionLocal.getMode().treatEmptyStringsAsNull ? ValueNull.INSTANCE : ValueVarchar.EMPTY;
        }
        return ValueVarchar.get(string.substring(max4 - 1, min2 - 1), null);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        Value value;
        Value value2;
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        int length = this.args.length;
        if (length < 2 || length > 3) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), "2..3");
        }
        TypeInfo type = this.args[0].getType();
        long precision = type.getPrecision();
        Expression expression = this.args[1];
        if (expression.isConstant() && (value2 = expression.getValue(sessionLocal)) != ValueNull.INSTANCE) {
            precision -= value2.getLong() - 1;
        }
        if (this.args.length == 3) {
            Expression expression2 = this.args[2];
            if (expression2.isConstant() && (value = expression2.getValue(sessionLocal)) != ValueNull.INSTANCE) {
                precision = Math.min(precision, value.getLong());
            }
        }
        this.type = TypeInfo.getTypeInfo(DataType.isBinaryStringType(type.getValueType()) ? 6 : 2, Math.max(0L, precision), 0, null);
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.args[0].getUnenclosedSQL(sb.append(getName()).append('('), i);
        this.args[1].getUnenclosedSQL(sb.append(" FROM "), i);
        if (this.args.length > 2) {
            this.args[2].getUnenclosedSQL(sb.append(" FOR "), i);
        }
        return sb.append(')');
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return "SUBSTRING";
    }
}
