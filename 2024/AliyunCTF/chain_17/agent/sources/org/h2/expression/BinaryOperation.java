package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.expression.IntervalOperation;
import org.h2.expression.function.DateTimeFunction;
import org.h2.message.DbException;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/BinaryOperation.class */
public class BinaryOperation extends Operation2 {
    private OpType opType;
    private TypeInfo forcedType;
    private boolean convertRight;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/BinaryOperation$OpType.class */
    public enum OpType {
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE
    }

    public BinaryOperation(OpType opType, Expression expression, Expression expression2) {
        super(expression, expression2);
        this.convertRight = true;
        this.opType = opType;
    }

    public void setForcedType(TypeInfo typeInfo) {
        if (this.opType != OpType.MINUS) {
            throw getUnexpectedForcedTypeException();
        }
        this.forcedType = typeInfo;
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.left.getSQL(sb, i, 0).append(' ').append(getOperationToken()).append(' ');
        return this.right.getSQL(sb, i, 0);
    }

    private String getOperationToken() {
        switch (this.opType) {
            case PLUS:
                return Marker.ANY_NON_NULL_MARKER;
            case MINUS:
                return "-";
            case MULTIPLY:
                return "*";
            case DIVIDE:
                return "/";
            default:
                throw DbException.getInternalError("opType=" + this.opType);
        }
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value convertTo = this.left.getValue(sessionLocal).convertTo(this.type, sessionLocal);
        Value value = this.right.getValue(sessionLocal);
        if (this.convertRight) {
            value = value.convertTo(this.type, sessionLocal);
        }
        switch (this.opType) {
            case PLUS:
                if (convertTo == ValueNull.INSTANCE || value == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                return convertTo.add(value);
            case MINUS:
                if (convertTo == ValueNull.INSTANCE || value == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                return convertTo.subtract(value);
            case MULTIPLY:
                if (convertTo == ValueNull.INSTANCE || value == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                return convertTo.multiply(value);
            case DIVIDE:
                if (convertTo == ValueNull.INSTANCE || value == ValueNull.INSTANCE) {
                    return ValueNull.INSTANCE;
                }
                return convertTo.divide(value, this.type);
            default:
                throw DbException.getInternalError("type=" + this.opType);
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        this.right = this.right.optimize(sessionLocal);
        TypeInfo type = this.left.getType();
        TypeInfo type2 = this.right.getType();
        int valueType = type.getValueType();
        int valueType2 = type2.getValueType();
        if ((valueType == 0 && valueType2 == 0) || (valueType == -1 && valueType2 == -1)) {
            if (this.opType == OpType.PLUS && sessionLocal.getDatabase().getMode().allowPlusForStringConcat) {
                return new ConcatenationOperation(this.left, this.right).optimize(sessionLocal);
            }
            this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
        } else {
            if (DataType.isIntervalType(valueType) || DataType.isIntervalType(valueType2)) {
                if (this.forcedType != null) {
                    throw getUnexpectedForcedTypeException();
                }
                return optimizeInterval(valueType, valueType2);
            }
            if (DataType.isDateTimeType(valueType) || DataType.isDateTimeType(valueType2)) {
                return optimizeDateTime(sessionLocal, valueType, valueType2);
            }
            if (this.forcedType != null) {
                throw getUnexpectedForcedTypeException();
            }
            int higherOrder = Value.getHigherOrder(valueType, valueType2);
            if (higherOrder == 13) {
                optimizeNumeric(type, type2);
            } else if (higherOrder == 16) {
                optimizeDecfloat(type, type2);
            } else if (higherOrder == 36) {
                this.type = TypeInfo.TYPE_INTEGER;
            } else {
                if (DataType.isCharacterStringType(higherOrder) && this.opType == OpType.PLUS && sessionLocal.getDatabase().getMode().allowPlusForStringConcat) {
                    return new ConcatenationOperation(this.left, this.right).optimize(sessionLocal);
                }
                this.type = TypeInfo.getTypeInfo(higherOrder);
            }
        }
        if (this.left.isConstant() && this.right.isConstant()) {
            return ValueExpression.get(getValue(sessionLocal));
        }
        return this;
    }

    private void optimizeNumeric(TypeInfo typeInfo, TypeInfo typeInfo2) {
        int i;
        long j;
        TypeInfo numericType = typeInfo.toNumericType();
        TypeInfo numericType2 = typeInfo2.toNumericType();
        long precision = numericType.getPrecision();
        long precision2 = numericType2.getPrecision();
        int scale = numericType.getScale();
        int scale2 = numericType2.getScale();
        switch (this.opType) {
            case PLUS:
            case MINUS:
                if (scale < scale2) {
                    precision += scale2 - scale;
                    i = scale2;
                } else {
                    precision2 += scale - scale2;
                    i = scale;
                }
                j = Math.max(precision, precision2) + 1;
                break;
            case MULTIPLY:
                j = precision + precision2;
                i = scale + scale2;
                break;
            case DIVIDE:
                long j2 = (scale - scale2) + (precision2 * 2);
                if (j2 >= 100000) {
                    i = 100000;
                } else if (j2 <= 0) {
                    i = 0;
                } else {
                    i = (int) j2;
                }
                j = ((precision + scale2) - scale) + i;
                if (j > 100000) {
                    long min = Math.min(j - 100000, i);
                    j -= min;
                    i = (int) (i - min);
                    break;
                }
                break;
            default:
                throw DbException.getInternalError("type=" + this.opType);
        }
        this.type = TypeInfo.getTypeInfo(13, j, i, null);
    }

    private void optimizeDecfloat(TypeInfo typeInfo, TypeInfo typeInfo2) {
        long j;
        TypeInfo decfloatType = typeInfo.toDecfloatType();
        TypeInfo decfloatType2 = typeInfo2.toDecfloatType();
        long precision = decfloatType.getPrecision();
        long precision2 = decfloatType2.getPrecision();
        switch (this.opType) {
            case PLUS:
            case MINUS:
            case DIVIDE:
                j = Math.max(precision, precision2) + 1;
                break;
            case MULTIPLY:
                j = precision + precision2;
                break;
            default:
                throw DbException.getInternalError("type=" + this.opType);
        }
        this.type = TypeInfo.getTypeInfo(16, j, 0, null);
    }

    private Expression optimizeInterval(int i, int i2) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        if (DataType.isIntervalType(i)) {
            z = true;
        } else if (DataType.isNumericType(i)) {
            z2 = true;
        } else if (DataType.isDateTimeType(i)) {
            z3 = true;
        } else {
            throw getUnsupported(i, i2);
        }
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        if (DataType.isIntervalType(i2)) {
            z4 = true;
        } else if (DataType.isNumericType(i2)) {
            z5 = true;
        } else if (DataType.isDateTimeType(i2)) {
            z6 = true;
        } else {
            throw getUnsupported(i, i2);
        }
        switch (this.opType) {
            case PLUS:
                if (z && z4) {
                    if (DataType.isYearMonthIntervalType(i) == DataType.isYearMonthIntervalType(i2)) {
                        return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_PLUS_INTERVAL, this.left, this.right);
                    }
                } else if (z && z6) {
                    if (i2 != 18 || !DataType.isYearMonthIntervalType(i)) {
                        return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL, this.right, this.left);
                    }
                } else if (z3 && z4 && (i != 18 || !DataType.isYearMonthIntervalType(i2))) {
                    return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_PLUS_INTERVAL, this.left, this.right);
                }
                break;
            case MINUS:
                if (z && z4) {
                    if (DataType.isYearMonthIntervalType(i) == DataType.isYearMonthIntervalType(i2)) {
                        return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MINUS_INTERVAL, this.left, this.right);
                    }
                } else if (z3 && z4 && (i != 18 || !DataType.isYearMonthIntervalType(i2))) {
                    return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_INTERVAL, this.left, this.right);
                }
                break;
            case MULTIPLY:
                if (z && z5) {
                    return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC, this.left, this.right);
                }
                if (z2 && z4) {
                    return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_MULTIPLY_NUMERIC, this.right, this.left);
                }
                break;
            case DIVIDE:
                if (z) {
                    if (z5) {
                        return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_DIVIDE_NUMERIC, this.left, this.right);
                    }
                    if (z4 && DataType.isYearMonthIntervalType(i) == DataType.isYearMonthIntervalType(i2)) {
                        return new IntervalOperation(IntervalOperation.IntervalOpType.INTERVAL_DIVIDE_INTERVAL, this.left, this.right);
                    }
                }
                break;
        }
        throw getUnsupported(i, i2);
    }

    private Expression optimizeDateTime(SessionLocal sessionLocal, int i, int i2) {
        switch (this.opType) {
            case PLUS:
                if (DataType.isDateTimeType(i)) {
                    if (DataType.isDateTimeType(i2)) {
                        if (i > i2) {
                            swap();
                        }
                        return new CompatibilityDatePlusTimeOperation(this.right, this.left).optimize(sessionLocal);
                    }
                    swap();
                    i = i2;
                    i2 = i;
                }
                switch (i) {
                    case 11:
                        return new DateTimeFunction(2, 2, this.left, this.right).optimize(sessionLocal);
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        return new DateTimeFunction(2, 5, new BinaryOperation(OpType.MULTIPLY, ValueExpression.get(ValueInteger.get(86400)), this.left), this.right).optimize(sessionLocal);
                }
            case MINUS:
                switch (i) {
                    case 17:
                    case 20:
                    case 21:
                        switch (i2) {
                            case 11:
                                if (this.forcedType != null) {
                                    throw getUnexpectedForcedTypeException();
                                }
                                return new DateTimeFunction(2, 2, new UnaryOperation(this.right), this.left).optimize(sessionLocal);
                            case 13:
                            case 14:
                            case 15:
                            case 16:
                                if (this.forcedType != null) {
                                    throw getUnexpectedForcedTypeException();
                                }
                                return new DateTimeFunction(2, 5, new BinaryOperation(OpType.MULTIPLY, ValueExpression.get(ValueInteger.get(-86400)), this.right), this.left).optimize(sessionLocal);
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                                return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_DATETIME, this.left, this.right, this.forcedType);
                        }
                    case 18:
                    case 19:
                        if (DataType.isDateTimeType(i2)) {
                            return new IntervalOperation(IntervalOperation.IntervalOpType.DATETIME_MINUS_DATETIME, this.left, this.right, this.forcedType);
                        }
                        break;
                }
            case MULTIPLY:
                if (i == 18) {
                    this.type = TypeInfo.TYPE_TIME;
                    this.convertRight = false;
                    return this;
                }
                if (i2 == 18) {
                    swap();
                    this.type = TypeInfo.TYPE_TIME;
                    this.convertRight = false;
                    return this;
                }
                break;
            case DIVIDE:
                if (i == 18) {
                    this.type = TypeInfo.TYPE_TIME;
                    this.convertRight = false;
                    return this;
                }
                break;
        }
        throw getUnsupported(i, i2);
    }

    private DbException getUnsupported(int i, int i2) {
        return DbException.getUnsupportedException(Value.getTypeName(i) + ' ' + getOperationToken() + ' ' + Value.getTypeName(i2));
    }

    private DbException getUnexpectedForcedTypeException() {
        StringBuilder unenclosedSQL = getUnenclosedSQL(new StringBuilder(), 3);
        return DbException.getSyntaxError(IntervalOperation.getForcedTypeSQL(unenclosedSQL.append(' '), this.forcedType).toString(), unenclosedSQL.length(), "");
    }

    private void swap() {
        Expression expression = this.left;
        this.left = this.right;
        this.right = expression;
    }

    public OpType getOperationType() {
        return this.opType;
    }
}
