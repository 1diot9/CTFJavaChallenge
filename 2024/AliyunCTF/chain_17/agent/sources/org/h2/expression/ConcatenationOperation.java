package org.h2.expression;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.function.CastSpecification;
import org.h2.expression.function.ConcatFunction;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/ConcatenationOperation.class */
public final class ConcatenationOperation extends OperationN {
    public ConcatenationOperation() {
        super(new Expression[4]);
    }

    public ConcatenationOperation(Expression expression, Expression expression2) {
        super(new Expression[]{expression, expression2});
        this.argsCount = 2;
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        int length = this.args.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(" || ");
            }
            this.args[i2].getSQL(sb, i, 0);
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        int length = this.args.length;
        if (length == 2) {
            Value convertTo = this.args[0].getValue(sessionLocal).convertTo(this.type, sessionLocal);
            if (convertTo == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            Value convertTo2 = this.args[1].getValue(sessionLocal).convertTo(this.type, sessionLocal);
            if (convertTo2 == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            return getValue(sessionLocal, convertTo, convertTo2);
        }
        return getValue(sessionLocal, length);
    }

    private Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        int valueType = this.type.getValueType();
        if (valueType == 2) {
            String string = value.getString();
            String string2 = value2.getString();
            return ValueVarchar.get(new StringBuilder(string.length() + string2.length()).append(string).append(string2).toString());
        }
        if (valueType == 6) {
            byte[] bytesNoCopy = value.getBytesNoCopy();
            byte[] bytesNoCopy2 = value2.getBytesNoCopy();
            int length = bytesNoCopy.length;
            int length2 = bytesNoCopy2.length;
            byte[] copyOf = Arrays.copyOf(bytesNoCopy, length + length2);
            System.arraycopy(bytesNoCopy2, 0, copyOf, length, length2);
            return ValueVarbinary.getNoCopy(copyOf);
        }
        Value[] list = ((ValueArray) value).getList();
        Value[] list2 = ((ValueArray) value2).getList();
        int length3 = list.length;
        int length4 = list2.length;
        Value[] valueArr = (Value[]) Arrays.copyOf(list, length3 + length4);
        System.arraycopy(list2, 0, valueArr, length3, length4);
        return ValueArray.get((TypeInfo) this.type.getExtTypeInfo(), valueArr, sessionLocal);
    }

    private Value getValue(SessionLocal sessionLocal, int i) {
        Value[] valueArr = new Value[i];
        for (int i2 = 0; i2 < i; i2++) {
            Value convertTo = this.args[i2].getValue(sessionLocal).convertTo(this.type, sessionLocal);
            if (convertTo == ValueNull.INSTANCE) {
                return ValueNull.INSTANCE;
            }
            valueArr[i2] = convertTo;
        }
        int valueType = this.type.getValueType();
        if (valueType == 2) {
            StringBuilder sb = new StringBuilder();
            for (int i3 = 0; i3 < i; i3++) {
                sb.append(valueArr[i3].getString());
            }
            return ValueVarchar.get(sb.toString(), sessionLocal);
        }
        if (valueType == 6) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                i4 += valueArr[i5].getBytesNoCopy().length;
            }
            byte[] bArr = new byte[i4];
            int i6 = 0;
            for (int i7 = 0; i7 < i; i7++) {
                byte[] bytesNoCopy = valueArr[i7].getBytesNoCopy();
                int length = bytesNoCopy.length;
                System.arraycopy(bytesNoCopy, 0, bArr, i6, length);
                i6 += length;
            }
            return ValueVarbinary.getNoCopy(bArr);
        }
        int i8 = 0;
        for (int i9 = 0; i9 < i; i9++) {
            i8 += ((ValueArray) valueArr[i9]).getList().length;
        }
        Value[] valueArr2 = new Value[i8];
        int i10 = 0;
        for (int i11 = 0; i11 < i; i11++) {
            Value[] list = ((ValueArray) valueArr[i11]).getList();
            int length2 = list.length;
            System.arraycopy(list, 0, valueArr2, i10, length2);
            i10 += length2;
        }
        return ValueArray.get((TypeInfo) this.type.getExtTypeInfo(), valueArr2, sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        determineType(sessionLocal);
        inlineArguments();
        if (this.type.getValueType() == 2 && sessionLocal.getMode().treatEmptyStringsAsNull) {
            return new ConcatFunction(0, this.args).optimize(sessionLocal);
        }
        int length = this.args.length;
        boolean z = true;
        boolean z2 = false;
        for (int i = 0; i < length; i++) {
            if (this.args[i].isConstant()) {
                z2 = true;
            } else {
                z = false;
            }
        }
        if (z) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        if (z2) {
            int i2 = 0;
            int i3 = 0;
            while (i3 < length) {
                Expression expression = this.args[i3];
                if (expression.isConstant()) {
                    Value convertTo = expression.getValue(sessionLocal).convertTo(this.type, sessionLocal);
                    if (convertTo == ValueNull.INSTANCE) {
                        return TypedValueExpression.get(ValueNull.INSTANCE, this.type);
                    }
                    if (isEmpty(convertTo)) {
                        continue;
                        i3++;
                    } else {
                        while (i3 + 1 < length) {
                            Expression expression2 = this.args[i3 + 1];
                            if (!expression2.isConstant()) {
                                break;
                            }
                            Value convertTo2 = expression2.getValue(sessionLocal).convertTo(this.type, sessionLocal);
                            if (convertTo2 == ValueNull.INSTANCE) {
                                return TypedValueExpression.get(ValueNull.INSTANCE, this.type);
                            }
                            if (!isEmpty(convertTo2)) {
                                convertTo = getValue(sessionLocal, convertTo, convertTo2);
                            }
                            i3++;
                        }
                        expression = ValueExpression.get(convertTo);
                    }
                }
                int i4 = i2;
                i2++;
                this.args[i4] = expression;
                i3++;
            }
            if (i2 == 1) {
                Expression expression3 = this.args[0];
                if (TypeInfo.areSameTypes(this.type, expression3.getType())) {
                    return expression3;
                }
                return new CastSpecification(expression3, this.type);
            }
            this.argsCount = i2;
            doneWithParameters();
        }
        return this;
    }

    private void determineType(SessionLocal sessionLocal) {
        int length = this.args.length;
        boolean z = false;
        boolean z2 = true;
        boolean z3 = true;
        for (int i = 0; i < length; i++) {
            Expression optimize = this.args[i].optimize(sessionLocal);
            this.args[i] = optimize;
            int valueType = optimize.getType().getValueType();
            if (valueType == 40) {
                z = true;
                z3 = false;
                z2 = false;
            } else if (valueType != 0) {
                if (DataType.isBinaryStringType(valueType)) {
                    z3 = false;
                } else if (DataType.isCharacterStringType(valueType)) {
                    z2 = false;
                } else {
                    z3 = false;
                    z2 = false;
                }
            }
        }
        if (z) {
            this.type = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getHigherType(this.args).getExtTypeInfo());
            return;
        }
        if (z2) {
            long precision = getPrecision(0);
            for (int i2 = 1; i2 < length; i2++) {
                precision = DataType.addPrecision(precision, getPrecision(i2));
            }
            this.type = TypeInfo.getTypeInfo(6, precision, 0, null);
            return;
        }
        if (z3) {
            long precision2 = getPrecision(0);
            for (int i3 = 1; i3 < length; i3++) {
                precision2 = DataType.addPrecision(precision2, getPrecision(i3));
            }
            this.type = TypeInfo.getTypeInfo(2, precision2, 0, null);
            return;
        }
        this.type = TypeInfo.TYPE_VARCHAR;
    }

    private long getPrecision(int i) {
        TypeInfo type = this.args[i].getType();
        if (type.getValueType() != 0) {
            return type.getPrecision();
        }
        return 0L;
    }

    private void inlineArguments() {
        int valueType = this.type.getValueType();
        int length = this.args.length;
        int i = length;
        for (int i2 = 0; i2 < length; i2++) {
            Expression expression = this.args[i2];
            if ((expression instanceof ConcatenationOperation) && expression.getType().getValueType() == valueType) {
                i += expression.getSubexpressionCount() - 1;
            }
        }
        if (i > length) {
            Expression[] expressionArr = new Expression[i];
            int i3 = 0;
            for (int i4 = 0; i4 < length; i4++) {
                Expression expression2 = this.args[i4];
                if ((expression2 instanceof ConcatenationOperation) && expression2.getType().getValueType() == valueType) {
                    Expression[] expressionArr2 = ((ConcatenationOperation) expression2).args;
                    int length2 = expressionArr2.length;
                    System.arraycopy(expressionArr2, 0, expressionArr, i3, length2);
                    i3 += length2;
                } else {
                    int i5 = i3;
                    i3++;
                    expressionArr[i5] = expression2;
                }
            }
            this.args = expressionArr;
            this.argsCount = i;
        }
    }

    private static boolean isEmpty(Value value) {
        int valueType = value.getValueType();
        if (valueType == 2) {
            return value.getString().isEmpty();
        }
        return valueType == 6 ? value.getBytesNoCopy().length == 0 : ((ValueArray) value).getList().length == 0;
    }
}
