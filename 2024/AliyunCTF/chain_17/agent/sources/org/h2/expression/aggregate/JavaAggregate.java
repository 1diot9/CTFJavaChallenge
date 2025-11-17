package org.h2.expression.aggregate;

import java.sql.SQLException;
import org.h2.command.query.Select;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.aggregate.AggregateDataCollecting;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.schema.UserAggregate;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/JavaAggregate.class */
public class JavaAggregate extends AbstractAggregate {
    private final UserAggregate userAggregate;
    private int[] argTypes;
    private int dataType;
    private JdbcConnection userConnection;

    public JavaAggregate(UserAggregate userAggregate, Expression[] expressionArr, Select select, boolean z) {
        super(select, expressionArr, z);
        this.userAggregate = userAggregate;
    }

    @Override // org.h2.expression.Expression
    public int getCost() {
        int i = 5;
        for (Expression expression : this.args) {
            i += expression.getCost();
        }
        if (this.filterCondition != null) {
            i += this.filterCondition.getCost();
        }
        return i;
    }

    @Override // org.h2.expression.Expression
    public StringBuilder getUnenclosedSQL(StringBuilder sb, int i) {
        ParserUtil.quoteIdentifier(sb, this.userAggregate.getName(), i).append('(');
        writeExpressions(sb, this.args, i).append(')');
        return appendTailConditions(sb, i, false);
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        if (!super.isEverything(expressionVisitor)) {
            return false;
        }
        switch (expressionVisitor.getType()) {
            case 1:
            case 2:
                return false;
            case 7:
                expressionVisitor.addDependency(this.userAggregate);
                break;
        }
        for (Expression expression : this.args) {
            if (expression != null && !expression.isEverything(expressionVisitor)) {
                return false;
            }
        }
        return this.filterCondition == null || this.filterCondition.isEverything(expressionVisitor);
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate, org.h2.expression.analysis.DataAnalysisOperation, org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        super.optimize(sessionLocal);
        this.userConnection = sessionLocal.createConnection(false);
        int length = this.args.length;
        this.argTypes = new int[length];
        for (int i = 0; i < length; i++) {
            this.argTypes[i] = this.args[i].getType().getValueType();
        }
        try {
            this.dataType = getInstance().getInternalType(this.argTypes);
            this.type = TypeInfo.getTypeInfo(this.dataType);
            return this;
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    private org.h2.api.Aggregate getInstance() {
        org.h2.api.Aggregate userAggregate = this.userAggregate.getInstance();
        try {
            userAggregate.init(this.userConnection);
            return userAggregate;
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public Value getAggregatedValue(SessionLocal sessionLocal, Object obj) {
        org.h2.api.Aggregate aggregate;
        try {
            if (this.distinct) {
                aggregate = getInstance();
                AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting) obj;
                if (aggregateDataCollecting != null) {
                    for (Value value : aggregateDataCollecting.values) {
                        if (this.args.length == 1) {
                            aggregate.add(ValueToObjectConverter.valueToDefaultObject(value, this.userConnection, false));
                        } else {
                            Value[] list = ((ValueRow) value).getList();
                            Object[] objArr = new Object[this.args.length];
                            int length = this.args.length;
                            for (int i = 0; i < length; i++) {
                                objArr[i] = ValueToObjectConverter.valueToDefaultObject(list[i], this.userConnection, false);
                            }
                            aggregate.add(objArr);
                        }
                    }
                }
            } else {
                aggregate = (org.h2.api.Aggregate) obj;
                if (aggregate == null) {
                    aggregate = getInstance();
                }
            }
            Object result = aggregate.getResult();
            if (result == null) {
                return ValueNull.INSTANCE;
            }
            return ValueToObjectConverter.objectToValue(sessionLocal, result, this.dataType);
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate
    protected void updateAggregate(SessionLocal sessionLocal, Object obj) {
        updateData(sessionLocal, obj, null);
    }

    private void updateData(SessionLocal sessionLocal, Object obj, Value[] valueArr) {
        try {
            if (this.distinct) {
                AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting) obj;
                Value[] valueArr2 = new Value[this.args.length];
                Value value = null;
                int length = this.args.length;
                for (int i = 0; i < length; i++) {
                    value = valueArr == null ? this.args[i].getValue(sessionLocal) : valueArr[i];
                    valueArr2[i] = value;
                }
                aggregateDataCollecting.add(sessionLocal, this.args.length == 1 ? value : ValueRow.get(valueArr2));
            } else {
                org.h2.api.Aggregate aggregate = (org.h2.api.Aggregate) obj;
                Object[] objArr = new Object[this.args.length];
                Object obj2 = null;
                int length2 = this.args.length;
                for (int i2 = 0; i2 < length2; i2++) {
                    obj2 = ValueToObjectConverter.valueToDefaultObject(valueArr == null ? this.args[i2].getValue(sessionLocal) : valueArr[i2], this.userConnection, false);
                    objArr[i2] = obj2;
                }
                aggregate.add(this.args.length == 1 ? obj2 : objArr);
            }
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.aggregate.AbstractAggregate, org.h2.expression.analysis.DataAnalysisOperation
    public void updateGroupAggregates(SessionLocal sessionLocal, int i) {
        super.updateGroupAggregates(sessionLocal, i);
        for (Expression expression : this.args) {
            expression.updateAggregate(sessionLocal, i);
        }
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected int getNumExpressions() {
        int length = this.args.length;
        if (this.filterCondition != null) {
            length++;
        }
        return length;
    }

    @Override // org.h2.expression.analysis.DataAnalysisOperation
    protected void rememberExpressions(SessionLocal sessionLocal, Value[] valueArr) {
        int length = this.args.length;
        for (int i = 0; i < length; i++) {
            valueArr[i] = this.args[i].getValue(sessionLocal);
        }
        if (this.filterCondition != null) {
            valueArr[length] = ValueBoolean.get(this.filterCondition.getBooleanValue(sessionLocal));
        }
    }

    @Override // org.h2.expression.aggregate.AbstractAggregate
    protected void updateFromExpressions(SessionLocal sessionLocal, Object obj, Value[] valueArr) {
        if (this.filterCondition == null || valueArr[getNumExpressions() - 1].isTrue()) {
            updateData(sessionLocal, obj, valueArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.expression.analysis.DataAnalysisOperation
    public Object createAggregateData() {
        return this.distinct ? new AggregateDataCollecting(true, false, AggregateDataCollecting.NullCollectionMode.IGNORED) : getInstance();
    }
}
