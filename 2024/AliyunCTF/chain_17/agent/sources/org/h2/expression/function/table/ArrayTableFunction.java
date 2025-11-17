package org.h2.expression.function.table;

import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONValue;
import org.h2.value.Value;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueInteger;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/table/ArrayTableFunction.class */
public final class ArrayTableFunction extends TableFunction {
    public static final int UNNEST = 0;
    public static final int TABLE = 1;
    public static final int TABLE_DISTINCT = 2;
    private Column[] columns;
    private static final String[] NAMES = {"UNNEST", "TABLE", "TABLE_DISTINCT"};
    private final int function;

    public ArrayTableFunction(int i) {
        super(new Expression[1]);
        this.function = i;
    }

    @Override // org.h2.expression.function.table.TableFunction
    public ResultInterface getValue(SessionLocal sessionLocal) {
        return getTable(sessionLocal, false);
    }

    @Override // org.h2.expression.function.table.TableFunction
    public void optimize(SessionLocal sessionLocal) {
        super.optimize(sessionLocal);
        if (this.args.length < 1) {
            throw DbException.get(ErrorCode.INVALID_PARAMETER_COUNT_2, getName(), ">0");
        }
    }

    @Override // org.h2.expression.function.table.TableFunction, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if (this.function == 0) {
            super.getSQL(sb, i);
            if (this.args.length < this.columns.length) {
                sb.append(" WITH ORDINALITY");
            }
        } else {
            sb.append(getName()).append('(');
            for (int i2 = 0; i2 < this.args.length; i2++) {
                if (i2 > 0) {
                    sb.append(", ");
                }
                sb.append(this.columns[i2].getCreateSQL()).append('=');
                this.args[i2].getUnenclosedSQL(sb, i);
            }
            sb.append(')');
        }
        return sb;
    }

    @Override // org.h2.expression.function.table.TableFunction
    public ResultInterface getValueTemplate(SessionLocal sessionLocal) {
        return getTable(sessionLocal, true);
    }

    public void setColumns(ArrayList<Column> arrayList) {
        this.columns = (Column[]) arrayList.toArray(new Column[0]);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v22, types: [org.h2.value.Value[]] */
    /* JADX WARN: Type inference failed for: r0v36 */
    /* JADX WARN: Type inference failed for: r0v43 */
    private ResultInterface getTable(SessionLocal sessionLocal, boolean z) {
        Value value;
        Value[] valueArr;
        int length = this.columns.length;
        Expression[] expressionArr = new Expression[length];
        Database database = sessionLocal.getDatabase();
        for (int i = 0; i < length; i++) {
            expressionArr[i] = new ExpressionColumn(database, this.columns[i]);
        }
        LocalResult localResult = new LocalResult(sessionLocal, expressionArr, length, length);
        if (!z && this.function == 2) {
            localResult.setDistinct();
        }
        if (!z) {
            int i2 = length;
            boolean z2 = this.function == 0;
            boolean z3 = false;
            if (z2) {
                i2 = this.args.length;
                if (i2 < length) {
                    z3 = true;
                }
            }
            ?? r0 = new Value[i2];
            int i3 = 0;
            for (int i4 = 0; i4 < i2; i4++) {
                Value value2 = this.args[i4].getValue(sessionLocal);
                if (value2 == ValueNull.INSTANCE) {
                    r0[i4] = Value.EMPTY_VALUES;
                } else {
                    switch (value2.getValueType()) {
                        case 38:
                            JSONValue decomposition = value2.convertToAnyJson().getDecomposition();
                            if (decomposition instanceof JSONArray) {
                                valueArr = (Value[]) ((JSONArray) decomposition).getArray(Value.class, ValueJson::fromJson);
                                break;
                            } else {
                                valueArr = Value.EMPTY_VALUES;
                                break;
                            }
                        case 39:
                        default:
                            valueArr = new Value[]{value2};
                            break;
                        case 40:
                        case 41:
                            valueArr = ((ValueCollectionBase) value2).getList();
                            break;
                    }
                    r0[i4] = valueArr;
                    i3 = Math.max(i3, valueArr.length);
                }
            }
            for (int i5 = 0; i5 < i3; i5++) {
                Value[] valueArr2 = new Value[length];
                for (int i6 = 0; i6 < i2; i6++) {
                    ?? r02 = r0[i6];
                    if (r02.length <= i5) {
                        value = ValueNull.INSTANCE;
                    } else {
                        Column column = this.columns[i6];
                        value = r02[i5];
                        if (!z2) {
                            value = value.convertForAssignTo(column.getType(), sessionLocal, column);
                        }
                    }
                    valueArr2[i6] = value;
                }
                if (z3) {
                    valueArr2[i2] = ValueInteger.get(i5 + 1);
                }
                localResult.addRow(valueArr2);
            }
        }
        localResult.done();
        return localResult;
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }

    @Override // org.h2.expression.function.table.TableFunction
    public boolean isDeterministic() {
        return true;
    }

    public int getFunctionType() {
        return this.function;
    }
}
