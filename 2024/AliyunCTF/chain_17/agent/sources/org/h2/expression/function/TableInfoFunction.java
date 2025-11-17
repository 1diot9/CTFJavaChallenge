package org.h2.expression.function;

import java.util.ArrayList;
import org.h2.command.Parser;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVSpatialIndex;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/function/TableInfoFunction.class */
public final class TableInfoFunction extends Function1_2 {
    public static final int DISK_SPACE_USED = 0;
    public static final int ESTIMATED_ENVELOPE = 1;
    private static final String[] NAMES = {"DISK_SPACE_USED", "ESTIMATED_ENVELOPE"};
    private final int function;

    public TableInfoFunction(Expression expression, Expression expression2, int i) {
        super(expression, expression2);
        this.function = i;
    }

    @Override // org.h2.expression.function.Function1_2
    public Value getValue(SessionLocal sessionLocal, Value value, Value value2) {
        Value value3;
        Table parseTableName = new Parser(sessionLocal).parseTableName(value.getString());
        switch (this.function) {
            case 0:
                value3 = ValueBigint.get(parseTableName.getDiskSpaceUsed());
                break;
            case 1:
                Column column = parseTableName.getColumn(value2.getString());
                ArrayList<Index> indexes = parseTableName.getIndexes();
                if (indexes != null) {
                    int size = indexes.size();
                    for (int i = 1; i < size; i++) {
                        Index index = indexes.get(i);
                        if ((index instanceof MVSpatialIndex) && index.isFirstColumn(column)) {
                            value3 = ((MVSpatialIndex) index).getEstimatedBounds(sessionLocal);
                            break;
                        }
                    }
                }
                value3 = ValueNull.INSTANCE;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return value3;
    }

    @Override // org.h2.expression.Expression
    public Expression optimize(SessionLocal sessionLocal) {
        this.left = this.left.optimize(sessionLocal);
        if (this.right != null) {
            this.right = this.right.optimize(sessionLocal);
        }
        switch (this.function) {
            case 0:
                this.type = TypeInfo.TYPE_BIGINT;
                break;
            case 1:
                this.type = TypeInfo.TYPE_GEOMETRY;
                break;
            default:
                throw DbException.getInternalError("function=" + this.function);
        }
        return this;
    }

    @Override // org.h2.expression.Operation1_2, org.h2.expression.Expression
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        switch (expressionVisitor.getType()) {
            case 2:
                return false;
            default:
                return super.isEverything(expressionVisitor);
        }
    }

    @Override // org.h2.expression.function.NamedExpression
    public String getName() {
        return NAMES[this.function];
    }
}
