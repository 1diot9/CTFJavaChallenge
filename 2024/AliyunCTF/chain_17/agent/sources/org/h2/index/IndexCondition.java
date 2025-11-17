package org.h2.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.table.TableType;
import org.h2.value.Value;
import org.h2.value.ValueArray;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/IndexCondition.class */
public class IndexCondition {
    public static final int EQUALITY = 1;
    public static final int START = 2;
    public static final int END = 4;
    public static final int RANGE = 6;
    public static final int ALWAYS_FALSE = 8;
    public static final int SPATIAL_INTERSECTS = 16;
    private final Column column;
    private final int compareType;
    private final Expression expression;
    private List<Expression> expressionList;
    private Query expressionQuery;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !IndexCondition.class.desiredAssertionStatus();
    }

    private IndexCondition(int i, ExpressionColumn expressionColumn, Expression expression) {
        this.compareType = i;
        this.column = expressionColumn == null ? null : expressionColumn.getColumn();
        this.expression = expression;
    }

    public static IndexCondition get(int i, ExpressionColumn expressionColumn, Expression expression) {
        return new IndexCondition(i, expressionColumn, expression);
    }

    public static IndexCondition getInList(ExpressionColumn expressionColumn, List<Expression> list) {
        IndexCondition indexCondition = new IndexCondition(10, expressionColumn, null);
        indexCondition.expressionList = list;
        return indexCondition;
    }

    public static IndexCondition getInArray(ExpressionColumn expressionColumn, Expression expression) {
        return new IndexCondition(11, expressionColumn, expression);
    }

    public static IndexCondition getInQuery(ExpressionColumn expressionColumn, Query query) {
        if (!$assertionsDisabled && !query.isRandomAccessResult()) {
            throw new AssertionError();
        }
        IndexCondition indexCondition = new IndexCondition(12, expressionColumn, null);
        indexCondition.expressionQuery = query;
        return indexCondition;
    }

    public Value getCurrentValue(SessionLocal sessionLocal) {
        return this.expression.getValue(sessionLocal);
    }

    public Value[] getCurrentValueList(SessionLocal sessionLocal) {
        TreeSet treeSet = new TreeSet(sessionLocal.getDatabase().getCompareMode());
        if (this.compareType == 10) {
            Iterator<Expression> it = this.expressionList.iterator();
            while (it.hasNext()) {
                treeSet.add(this.column.convert(sessionLocal, it.next().getValue(sessionLocal)));
            }
        } else if (this.compareType == 11) {
            Value value = this.expression.getValue(sessionLocal);
            if (value instanceof ValueArray) {
                for (Value value2 : ((ValueArray) value).getList()) {
                    treeSet.add(value2);
                }
            }
        } else {
            throw DbException.getInternalError("compareType = " + this.compareType);
        }
        Value[] valueArr = (Value[]) treeSet.toArray(new Value[treeSet.size()]);
        Arrays.sort(valueArr, sessionLocal.getDatabase().getCompareMode());
        return valueArr;
    }

    public ResultInterface getCurrentResult() {
        return this.expressionQuery.query(0L);
    }

    public String getSQL(int i) {
        if (this.compareType == 9) {
            return "FALSE";
        }
        StringBuilder sb = new StringBuilder();
        this.column.getSQL(sb, i);
        switch (this.compareType) {
            case 0:
                sb.append(" = ");
                break;
            case 1:
            case 7:
            case 9:
            default:
                throw DbException.getInternalError("type=" + this.compareType);
            case 2:
                sb.append(" < ");
                break;
            case 3:
                sb.append(" > ");
                break;
            case 4:
                sb.append(" <= ");
                break;
            case 5:
                sb.append(" >= ");
                break;
            case 6:
                sb.append((this.expression.isNullConstant() || (this.column.getType().getValueType() == 8 && this.expression.isConstant())) ? " IS " : " IS NOT DISTINCT FROM ");
                break;
            case 8:
                sb.append(" && ");
                break;
            case 10:
                Expression.writeExpressions(sb.append(" IN("), this.expressionList, i).append(')');
                break;
            case 11:
                return this.expression.getSQL(sb.append(" = ANY("), i, 0).append(')').toString();
            case 12:
                sb.append(" IN(");
                sb.append(this.expressionQuery.getPlanSQL(i));
                sb.append(')');
                break;
        }
        if (this.expression != null) {
            this.expression.getSQL(sb, i, 0);
        }
        return sb.toString();
    }

    public int getMask(ArrayList<IndexCondition> arrayList) {
        switch (this.compareType) {
            case 0:
            case 6:
                return 1;
            case 1:
            case 7:
            default:
                throw DbException.getInternalError("type=" + this.compareType);
            case 2:
            case 4:
                return 4;
            case 3:
            case 5:
                return 2;
            case 8:
                return 16;
            case 9:
                return 8;
            case 10:
            case 11:
            case 12:
                if (arrayList.size() > 1 && TableType.TABLE != this.column.getTable().getTableType()) {
                    return 0;
                }
                return 1;
        }
    }

    public boolean isAlwaysFalse() {
        return this.compareType == 9;
    }

    public boolean isStart() {
        switch (this.compareType) {
            case 0:
            case 3:
            case 5:
            case 6:
                return true;
            case 1:
            case 2:
            case 4:
            default:
                return false;
        }
    }

    public boolean isEnd() {
        switch (this.compareType) {
            case 0:
            case 2:
            case 4:
            case 6:
                return true;
            case 1:
            case 3:
            case 5:
            default:
                return false;
        }
    }

    public boolean isSpatialIntersects() {
        switch (this.compareType) {
            case 8:
                return true;
            default:
                return false;
        }
    }

    public int getCompareType() {
        return this.compareType;
    }

    public Column getColumn() {
        return this.column;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public List<Expression> getExpressionList() {
        return this.expressionList;
    }

    public Query getExpressionQuery() {
        return this.expressionQuery;
    }

    public boolean isEvaluatable() {
        if (this.expression != null) {
            return this.expression.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
        }
        if (this.expressionList != null) {
            Iterator<Expression> it = this.expressionList.iterator();
            while (it.hasNext()) {
                if (!it.next().isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
                    return false;
                }
            }
            return true;
        }
        return this.expressionQuery.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
    }

    public String toString() {
        return compareTypeToString(new StringBuilder("column=").append(this.column).append(", compareType="), this.compareType).append(", expression=").append(this.expression).append(", expressionList=").append(this.expressionList).append(", expressionQuery=").append(this.expressionQuery).toString();
    }

    private static StringBuilder compareTypeToString(StringBuilder sb, int i) {
        boolean z = false;
        if ((i & 1) == 1) {
            z = true;
            sb.append("EQUALITY");
        }
        if ((i & 2) == 2) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            sb.append("START");
        }
        if ((i & 4) == 4) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            sb.append("END");
        }
        if ((i & 8) == 8) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            sb.append("ALWAYS_FALSE");
        }
        if ((i & 16) == 16) {
            if (z) {
                sb.append(", ");
            }
            sb.append("SPATIAL_INTERSECTS");
        }
        return sb;
    }
}
