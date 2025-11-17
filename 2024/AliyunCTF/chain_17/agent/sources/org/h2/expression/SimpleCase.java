package org.h2.expression;

import org.h2.engine.SessionLocal;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/SimpleCase.class */
public final class SimpleCase extends Expression {
    private Expression operand;
    private SimpleWhen when;
    private Expression elseResult;
    private TypeInfo type;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/SimpleCase$SimpleWhen.class */
    public static final class SimpleWhen {
        Expression[] operands;
        Expression result;
        SimpleWhen next;

        public SimpleWhen(Expression expression, Expression expression2) {
            this(new Expression[]{expression}, expression2);
        }

        public SimpleWhen(Expression[] expressionArr, Expression expression) {
            this.operands = expressionArr;
            this.result = expression;
        }

        public void setWhen(SimpleWhen simpleWhen) {
            this.next = simpleWhen;
        }
    }

    public SimpleCase(Expression expression, SimpleWhen simpleWhen, Expression expression2) {
        this.operand = expression;
        this.when = simpleWhen;
        this.elseResult = expression2;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.operand.getValue(sessionLocal);
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 != null) {
                for (Expression expression : simpleWhen2.operands) {
                    if (expression.getWhenValue(sessionLocal, value)) {
                        return simpleWhen2.result.getValue(sessionLocal).convertTo(this.type, sessionLocal);
                    }
                }
                simpleWhen = simpleWhen2.next;
            } else {
                if (this.elseResult != null) {
                    return this.elseResult.getValue(sessionLocal).convertTo(this.type, sessionLocal);
                }
                return ValueNull.INSTANCE;
            }
        }
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        TypeInfo typeInfo = TypeInfo.TYPE_UNKNOWN;
        this.operand = this.operand.optimize(sessionLocal);
        boolean isConstant = this.operand.isConstant();
        Value value = null;
        if (isConstant) {
            value = this.operand.getValue(sessionLocal);
        }
        TypeInfo type = this.operand.getType();
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 != null) {
                Expression[] expressionArr = simpleWhen2.operands;
                for (int i = 0; i < expressionArr.length; i++) {
                    Expression optimize = expressionArr[i].optimize(sessionLocal);
                    if (!optimize.isWhenConditionOperand()) {
                        TypeInfo.checkComparable(type, optimize.getType());
                    }
                    if (isConstant) {
                        if (optimize.isConstant()) {
                            if (optimize.getWhenValue(sessionLocal, value)) {
                                return simpleWhen2.result.optimize(sessionLocal);
                            }
                        } else {
                            isConstant = false;
                        }
                    }
                    expressionArr[i] = optimize;
                }
                simpleWhen2.result = simpleWhen2.result.optimize(sessionLocal);
                typeInfo = combineTypes(typeInfo, simpleWhen2.result);
                simpleWhen = simpleWhen2.next;
            } else {
                if (this.elseResult != null) {
                    this.elseResult = this.elseResult.optimize(sessionLocal);
                    if (isConstant) {
                        return this.elseResult;
                    }
                    typeInfo = combineTypes(typeInfo, this.elseResult);
                } else if (isConstant) {
                    return ValueExpression.NULL;
                }
                if (typeInfo.getValueType() == -1) {
                    typeInfo = TypeInfo.TYPE_VARCHAR;
                }
                this.type = typeInfo;
                return this;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TypeInfo combineTypes(TypeInfo typeInfo, Expression expression) {
        TypeInfo type;
        int valueType;
        if (!expression.isNullConstant() && (valueType = (type = expression.getType()).getValueType()) != -1 && valueType != 0) {
            typeInfo = TypeInfo.getHigherType(typeInfo, type);
        }
        return typeInfo;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        this.operand.getUnenclosedSQL(sb.append("CASE "), i);
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 == null) {
                break;
            }
            sb.append(" WHEN");
            Expression[] expressionArr = simpleWhen2.operands;
            int length = expressionArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                if (i2 > 0) {
                    sb.append(',');
                }
                expressionArr[i2].getWhenSQL(sb, i);
            }
            simpleWhen2.result.getUnenclosedSQL(sb.append(" THEN "), i);
            simpleWhen = simpleWhen2.next;
        }
        if (this.elseResult != null) {
            this.elseResult.getUnenclosedSQL(sb.append(" ELSE "), i);
        }
        return sb.append(" END");
    }

    @Override // org.h2.expression.Expression, org.h2.value.Typed
    public TypeInfo getType() {
        return this.type;
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.operand.mapColumns(columnResolver, i, i2);
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 == null) {
                break;
            }
            for (Expression expression : simpleWhen2.operands) {
                expression.mapColumns(columnResolver, i, i2);
            }
            simpleWhen2.result.mapColumns(columnResolver, i, i2);
            simpleWhen = simpleWhen2.next;
        }
        if (this.elseResult != null) {
            this.elseResult.mapColumns(columnResolver, i, i2);
        }
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.operand.setEvaluatable(tableFilter, z);
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 == null) {
                break;
            }
            for (Expression expression : simpleWhen2.operands) {
                expression.setEvaluatable(tableFilter, z);
            }
            simpleWhen2.result.setEvaluatable(tableFilter, z);
            simpleWhen = simpleWhen2.next;
        }
        if (this.elseResult != null) {
            this.elseResult.setEvaluatable(tableFilter, z);
        }
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.operand.updateAggregate(sessionLocal, i);
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 == null) {
                break;
            }
            for (Expression expression : simpleWhen2.operands) {
                expression.updateAggregate(sessionLocal, i);
            }
            simpleWhen2.result.updateAggregate(sessionLocal, i);
            simpleWhen = simpleWhen2.next;
        }
        if (this.elseResult != null) {
            this.elseResult.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!this.operand.isEverything(expressionVisitor)) {
            return false;
        }
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 != null) {
                for (Expression expression : simpleWhen2.operands) {
                    if (!expression.isEverything(expressionVisitor)) {
                        return false;
                    }
                }
                if (simpleWhen2.result.isEverything(expressionVisitor)) {
                    simpleWhen = simpleWhen2.next;
                } else {
                    return false;
                }
            } else {
                if (this.elseResult != null && !this.elseResult.isEverything(expressionVisitor)) {
                    return false;
                }
                return true;
            }
        }
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 0;
        int cost = 1 + this.operand.getCost();
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 == null) {
                break;
            }
            for (Expression expression : simpleWhen2.operands) {
                cost += expression.getCost();
            }
            i = Math.max(i, simpleWhen2.result.getCost());
            simpleWhen = simpleWhen2.next;
        }
        if (this.elseResult != null) {
            i = Math.max(i, this.elseResult.getCost());
        }
        return cost + i;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        int i = 1;
        SimpleWhen simpleWhen = this.when;
        while (true) {
            SimpleWhen simpleWhen2 = simpleWhen;
            if (simpleWhen2 == null) {
                break;
            }
            i += simpleWhen2.operands.length + 1;
            simpleWhen = simpleWhen2.next;
        }
        if (this.elseResult != null) {
            i++;
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i >= 0) {
            if (i == 0) {
                return this.operand;
            }
            int i2 = 1;
            SimpleWhen simpleWhen = this.when;
            while (true) {
                SimpleWhen simpleWhen2 = simpleWhen;
                if (simpleWhen2 != null) {
                    Expression[] expressionArr = simpleWhen2.operands;
                    int length = expressionArr.length;
                    int i3 = i - i2;
                    if (i3 < length) {
                        return expressionArr[i3];
                    }
                    int i4 = i2 + length;
                    i2 = i4 + 1;
                    if (i != i4) {
                        simpleWhen = simpleWhen2.next;
                    } else {
                        return simpleWhen2.result;
                    }
                } else if (this.elseResult != null && i == i2) {
                    return this.elseResult;
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
