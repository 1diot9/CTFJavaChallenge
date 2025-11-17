package org.h2.expression.function;

import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.engine.Mode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/ArrayFunction.class */
public final class ArrayFunction extends FunctionN {
    public static final int TRIM_ARRAY = 0;
    public static final int ARRAY_CONTAINS = 1;
    public static final int ARRAY_SLICE = 2;
    private static final String[] NAMES = {"TRIM_ARRAY", "ARRAY_CONTAINS", "ARRAY_SLICE"};
    private final int function;

    public ArrayFunction(Expression expression, Expression expression2, Expression expression3, int i) {
        super(expression3 == null ? new Expression[]{expression, expression2} : new Expression[]{expression, expression2, expression3});
        this.function = i;
    }

    @Override // org.h2.expression.function.FunctionN, org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value;
        Value value2 = this.args[0].getValue(sessionLocal);
        Value value3 = this.args[1].getValue(sessionLocal);
        switch (this.function) {
            case 0:
                if (value3 == ValueNull.INSTANCE) {
                    value2 = ValueNull.INSTANCE;
                    break;
                } else {
                    int i = value3.getInt();
                    if (i < 0) {
                        throw DbException.get(ErrorCode.ARRAY_ELEMENT_ERROR_2, Integer.toString(i), "0..CARDINALITY(array)");
                    }
                    if (value2 != ValueNull.INSTANCE) {
                        ValueArray convertToAnyArray = value2.convertToAnyArray(sessionLocal);
                        Value[] list = convertToAnyArray.getList();
                        int length = list.length;
                        if (i > length) {
                            throw DbException.get(ErrorCode.ARRAY_ELEMENT_ERROR_2, Integer.toString(i), "0.." + length);
                        }
                        if (i == 0) {
                            value2 = convertToAnyArray;
                            break;
                        } else {
                            value2 = ValueArray.get(convertToAnyArray.getComponentType(), (Value[]) Arrays.copyOf(list, length - i), sessionLocal);
                            break;
                        }
                    }
                }
                break;
            case 1:
                int valueType = value2.getValueType();
                if (valueType == 40 || valueType == 41) {
                    Value[] list2 = ((ValueCollectionBase) value2).getList();
                    value2 = ValueBoolean.FALSE;
                    int length2 = list2.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length2) {
                            break;
                        } else if (!sessionLocal.areEqual(list2[i2], value3)) {
                            i2++;
                        } else {
                            value2 = ValueBoolean.TRUE;
                            break;
                        }
                    }
                } else {
                    value2 = ValueNull.INSTANCE;
                    break;
                }
                break;
            case 2:
                if (value2 == ValueNull.INSTANCE || value3 == ValueNull.INSTANCE || (value = this.args[2].getValue(sessionLocal)) == ValueNull.INSTANCE) {
                    value2 = ValueNull.INSTANCE;
                    break;
                } else {
                    ValueArray convertToAnyArray2 = value2.convertToAnyArray(sessionLocal);
                    int i3 = value3.getInt() - 1;
                    int i4 = value.getInt();
                    boolean z = sessionLocal.getMode().getEnum() == Mode.ModeEnum.PostgreSQL;
                    if (i3 > i4) {
                        value2 = z ? ValueArray.get(convertToAnyArray2.getComponentType(), Value.EMPTY_VALUES, sessionLocal) : ValueNull.INSTANCE;
                        break;
                    } else {
                        if (i3 < 0) {
                            if (z) {
                                i3 = 0;
                            } else {
                                value2 = ValueNull.INSTANCE;
                                break;
                            }
                        }
                        if (i4 > convertToAnyArray2.getList().length) {
                            if (z) {
                                i4 = convertToAnyArray2.getList().length;
                            } else {
                                value2 = ValueNull.INSTANCE;
                                break;
                            }
                        }
                        value2 = ValueArray.get(convertToAnyArray2.getComponentType(), (Value[]) Arrays.copyOfRange(convertToAnyArray2.getList(), i3, i4), sessionLocal);
                        break;
                    }
                }
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return value2;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        boolean optimizeArguments = optimizeArguments(sessionLocal, true);
        switch (this.function) {
            case 0:
            case 2:
                Expression expression = this.args[0];
                this.type = expression.getType();
                int valueType = this.type.getValueType();
                if (valueType != 40 && valueType != 0) {
                    throw Store.getInvalidExpressionTypeException(getName() + " array argument", expression);
                }
                break;
            case 1:
                this.type = TypeInfo.TYPE_BOOLEAN;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        if (optimizeArguments) {
            return TypedValueExpression.getTypedIfNull(getValue(sessionLocal), this.type);
        }
        return this;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
