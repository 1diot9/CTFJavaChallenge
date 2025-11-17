package org.h2.expression.condition;

import java.util.Arrays;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/TypePredicate.class */
public final class TypePredicate extends SimplePredicate {
    private final TypeInfo[] typeList;
    private int[] valueTypes;

    public TypePredicate(Expression expression, boolean z, boolean z2, TypeInfo[] typeInfoArr) {
        super(expression, z, z2);
        this.typeList = typeInfoArr;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        return getWhenSQL(this.left.getSQL(sb, i, 0), i);
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        sb.append(" IS");
        if (this.not) {
            sb.append(" NOT");
        }
        sb.append(" OF (");
        for (int i2 = 0; i2 < this.typeList.length; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            this.typeList[i2].getSQL(sb, i);
        }
        return sb.append(')');
    }

    @Override // org.h2.expression.condition.SimplePredicate, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        int length = this.typeList.length;
        this.valueTypes = new int[length];
        for (int i = 0; i < length; i++) {
            this.valueTypes[i] = this.typeList[i].getValueType();
        }
        Arrays.sort(this.valueTypes);
        return super.optimize(sessionLocal);
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get((Arrays.binarySearch(this.valueTypes, value.getValueType()) >= 0) ^ this.not);
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        if (value == ValueNull.INSTANCE) {
            return false;
        }
        return (Arrays.binarySearch(this.valueTypes, value.getValueType()) >= 0) ^ this.not;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new TypePredicate(this.left, !this.not, false, this.typeList);
    }
}
