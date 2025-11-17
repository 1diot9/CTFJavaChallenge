package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.json.JSONItemType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/condition/IsJsonPredicate.class */
public final class IsJsonPredicate extends Condition {
    private Expression left;
    private final boolean not;
    private final boolean whenOperand;
    private final boolean withUniqueKeys;
    private final JSONItemType itemType;

    @Override // org.h2.expression.condition.Condition, org.h2.expression.Expression, org.h2.value.Typed
    public /* bridge */ /* synthetic */ TypeInfo getType() {
        return super.getType();
    }

    public IsJsonPredicate(Expression expression, boolean z, boolean z2, boolean z3, JSONItemType jSONItemType) {
        this.left = expression;
        this.whenOperand = z2;
        this.not = z;
        this.withUniqueKeys = z3;
        this.itemType = jSONItemType;
    }

    @Override // org.h2.expression.Expression
    public boolean needParentheses() {
        return true;
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
        sb.append(" JSON");
        switch (this.itemType) {
            case VALUE:
                break;
            case ARRAY:
                sb.append(" ARRAY");
                break;
            case OBJECT:
                sb.append(" OBJECT");
                break;
            case SCALAR:
                sb.append(" SCALAR");
                break;
            default:
                throw DbException.getInternalError("itemType=" + this.itemType);
        }
        if (this.withUniqueKeys) {
            sb.append(" WITH UNIQUE KEYS");
        }
        return sb;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (!this.whenOperand && this.left.isConstant()) {
            return ValueExpression.getBoolean(getValue(sessionLocal));
        }
        return this;
    }

    @Override // org.h2.expression.Expression
    public Value getValue(SessionLocal sessionLocal) {
        Value value = this.left.getValue(sessionLocal);
        if (value == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
        }
        return ValueBoolean.get(getValue(value));
    }

    @Override // org.h2.expression.Expression
    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        if (!this.whenOperand) {
            return super.getWhenValue(sessionLocal, value);
        }
        if (value == ValueNull.INSTANCE) {
            return false;
        }
        return getValue(value);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00d7  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00e1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean getValue(org.h2.value.Value r5) {
        /*
            r4 = this;
            r0 = r5
            int r0 = r0.getValueType()
            switch(r0) {
                case 1: goto Lcb;
                case 2: goto Lcb;
                case 3: goto Lcb;
                case 4: goto Lcb;
                case 5: goto L50;
                case 6: goto L50;
                case 7: goto L50;
                case 38: goto L92;
                default: goto L10d;
            }
        L50:
            r0 = r5
            byte[] r0 = r0.getBytesNoCopy()
            r7 = r0
            r0 = r4
            boolean r0 = r0.withUniqueKeys
            if (r0 == 0) goto L66
            org.h2.util.json.JSONValidationTargetWithUniqueKeys r0 = new org.h2.util.json.JSONValidationTargetWithUniqueKeys
            r1 = r0
            r1.<init>()
            goto L6d
        L66:
            org.h2.util.json.JSONValidationTargetWithoutUniqueKeys r0 = new org.h2.util.json.JSONValidationTargetWithoutUniqueKeys
            r1 = r0
            r1.<init>()
        L6d:
            r8 = r0
            r0 = r4
            org.h2.util.json.JSONItemType r0 = r0.itemType     // Catch: java.lang.RuntimeException -> L88
            r1 = r7
            r2 = r8
            java.lang.Object r1 = org.h2.util.json.JSONBytesSource.parse(r1, r2)     // Catch: java.lang.RuntimeException -> L88
            org.h2.util.json.JSONItemType r1 = (org.h2.util.json.JSONItemType) r1     // Catch: java.lang.RuntimeException -> L88
            boolean r0 = r0.includes(r1)     // Catch: java.lang.RuntimeException -> L88
            r1 = r4
            boolean r1 = r1.not     // Catch: java.lang.RuntimeException -> L88
            r0 = r0 ^ r1
            r6 = r0
            goto L112
        L88:
            r9 = move-exception
            r0 = r4
            boolean r0 = r0.not
            r6 = r0
            goto L112
        L92:
            r0 = r5
            org.h2.value.ValueJson r0 = (org.h2.value.ValueJson) r0
            org.h2.util.json.JSONItemType r0 = r0.getItemType()
            r7 = r0
            r0 = r4
            org.h2.util.json.JSONItemType r0 = r0.itemType
            r1 = r7
            boolean r0 = r0.includes(r1)
            if (r0 != 0) goto Lad
            r0 = r4
            boolean r0 = r0.not
            r6 = r0
            goto L112
        Lad:
            r0 = r4
            boolean r0 = r0.withUniqueKeys
            if (r0 == 0) goto Lbb
            r0 = r7
            org.h2.util.json.JSONItemType r1 = org.h2.util.json.JSONItemType.SCALAR
            if (r0 != r1) goto Lcb
        Lbb:
            r0 = r4
            boolean r0 = r0.not
            if (r0 != 0) goto Lc6
            r0 = 1
            goto Lc7
        Lc6:
            r0 = 0
        Lc7:
            r6 = r0
            goto L112
        Lcb:
            r0 = r5
            java.lang.String r0 = r0.getString()
            r7 = r0
            r0 = r4
            boolean r0 = r0.withUniqueKeys
            if (r0 == 0) goto Le1
            org.h2.util.json.JSONValidationTargetWithUniqueKeys r0 = new org.h2.util.json.JSONValidationTargetWithUniqueKeys
            r1 = r0
            r1.<init>()
            goto Le8
        Le1:
            org.h2.util.json.JSONValidationTargetWithoutUniqueKeys r0 = new org.h2.util.json.JSONValidationTargetWithoutUniqueKeys
            r1 = r0
            r1.<init>()
        Le8:
            r8 = r0
            r0 = r4
            org.h2.util.json.JSONItemType r0 = r0.itemType     // Catch: java.lang.RuntimeException -> L103
            r1 = r7
            r2 = r8
            java.lang.Object r1 = org.h2.util.json.JSONStringSource.parse(r1, r2)     // Catch: java.lang.RuntimeException -> L103
            org.h2.util.json.JSONItemType r1 = (org.h2.util.json.JSONItemType) r1     // Catch: java.lang.RuntimeException -> L103
            boolean r0 = r0.includes(r1)     // Catch: java.lang.RuntimeException -> L103
            r1 = r4
            boolean r1 = r1.not     // Catch: java.lang.RuntimeException -> L103
            r0 = r0 ^ r1
            r6 = r0
            goto L112
        L103:
            r9 = move-exception
            r0 = r4
            boolean r0 = r0.not
            r6 = r0
            goto L112
        L10d:
            r0 = r4
            boolean r0 = r0.not
            r6 = r0
        L112:
            r0 = r6
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.expression.condition.IsJsonPredicate.getValue(org.h2.value.Value):boolean");
    }

    @Override // org.h2.expression.Expression
    public boolean isWhenConditionOperand() {
        return this.whenOperand;
    }

    @Override // org.h2.expression.Expression
    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        if (this.whenOperand) {
            return null;
        }
        return new IsJsonPredicate(this.left, !this.not, false, this.withUniqueKeys, this.itemType);
    }

    @Override // org.h2.expression.Expression
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.expression.Expression
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.expression.Expression
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.left.mapColumns(columnResolver, i, i2);
    }

    @Override // org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i;
        int cost = this.left.getCost();
        if (this.left.getType().getValueType() == 38 && (!this.withUniqueKeys || this.itemType == JSONItemType.SCALAR)) {
            i = cost + 1;
        } else {
            i = cost + 10;
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public int getSubexpressionCount() {
        return 1;
    }

    @Override // org.h2.expression.Expression
    public Expression getSubexpression(int i) {
        if (i == 0) {
            return this.left;
        }
        throw new IndexOutOfBoundsException();
    }
}
