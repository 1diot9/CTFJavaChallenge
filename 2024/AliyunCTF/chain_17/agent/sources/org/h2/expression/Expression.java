package org.h2.expression;

import java.util.List;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.function.NamedExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.util.HasSQL;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/Expression.class */
public abstract class Expression implements HasSQL, Typed {
    public static final int MAP_INITIAL = 0;
    public static final int MAP_IN_WINDOW = 1;
    public static final int MAP_IN_AGGREGATE = 2;
    public static final int AUTO_PARENTHESES = 0;
    public static final int WITH_PARENTHESES = 1;
    public static final int WITHOUT_PARENTHESES = 2;
    private boolean addedToFilter;

    public abstract Value getValue(SessionLocal sessionLocal);

    public abstract TypeInfo getType();

    public abstract void mapColumns(ColumnResolver columnResolver, int i, int i2);

    public abstract Expression optimize(SessionLocal sessionLocal);

    public abstract void setEvaluatable(TableFilter tableFilter, boolean z);

    public abstract StringBuilder getUnenclosedSQL(StringBuilder sb, int i);

    public abstract void updateAggregate(SessionLocal sessionLocal, int i);

    public abstract boolean isEverything(ExpressionVisitor expressionVisitor);

    public abstract int getCost();

    public static StringBuilder writeExpressions(StringBuilder sb, List<? extends Expression> list, int i) {
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            list.get(i2).getUnenclosedSQL(sb, i);
        }
        return sb;
    }

    public static StringBuilder writeExpressions(StringBuilder sb, Expression[] expressionArr, int i) {
        int length = expressionArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            Expression expression = expressionArr[i2];
            if (expression == null) {
                sb.append("DEFAULT");
            } else {
                expression.getUnenclosedSQL(sb, i);
            }
        }
        return sb;
    }

    public final Expression optimizeCondition(SessionLocal sessionLocal) {
        Expression optimize = optimize(sessionLocal);
        if (optimize.isConstant()) {
            if (optimize.getBooleanValue(sessionLocal)) {
                return null;
            }
            return ValueExpression.FALSE;
        }
        return optimize;
    }

    @Override // org.h2.util.HasSQL
    public final String getSQL(int i) {
        return getSQL(new StringBuilder(), i, 0).toString();
    }

    @Override // org.h2.util.HasSQL
    public final StringBuilder getSQL(StringBuilder sb, int i) {
        return getSQL(sb, i, 0);
    }

    public final String getSQL(int i, int i2) {
        return getSQL(new StringBuilder(), i, i2).toString();
    }

    public final StringBuilder getSQL(StringBuilder sb, int i, int i2) {
        if (i2 == 1 || (i2 != 2 && needParentheses())) {
            return getUnenclosedSQL(sb.append('('), i).append(')');
        }
        return getUnenclosedSQL(sb, i);
    }

    public boolean needParentheses() {
        return false;
    }

    public final StringBuilder getEnclosedSQL(StringBuilder sb, int i) {
        return getUnenclosedSQL(sb.append('('), i).append(')');
    }

    public Expression getNotIfPossible(SessionLocal sessionLocal) {
        return null;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean isNullConstant() {
        return false;
    }

    public boolean isValueSet() {
        return false;
    }

    public boolean isIdentity() {
        return false;
    }

    public boolean getBooleanValue(SessionLocal sessionLocal) {
        return getValue(sessionLocal).isTrue();
    }

    public void createIndexConditions(SessionLocal sessionLocal, TableFilter tableFilter) {
    }

    public String getColumnName(SessionLocal sessionLocal, int i) {
        return getAlias(sessionLocal, i);
    }

    public String getSchemaName() {
        return null;
    }

    public String getTableName() {
        return null;
    }

    public int getNullable() {
        return 2;
    }

    public String getTableAlias() {
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public String getAlias(SessionLocal sessionLocal, int i) {
        switch (sessionLocal.getMode().expressionNames) {
            case C_NUMBER:
                break;
            case EMPTY:
                return "";
            case NUMBER:
                return Integer.toString(i + 1);
            case POSTGRESQL_STYLE:
                if (this instanceof NamedExpression) {
                    return StringUtils.toLowerEnglish(((NamedExpression) this).getName());
                }
                return "?column?";
            default:
                String sql = getSQL(5, 2);
                if (sql.length() <= 256) {
                    return sql;
                }
                break;
        }
        return "C" + (i + 1);
    }

    public String getColumnNameForView(SessionLocal sessionLocal, int i) {
        switch (sessionLocal.getMode().viewExpressionNames) {
            case AS_IS:
            default:
                return getAlias(sessionLocal, i);
            case EXCEPTION:
                throw DbException.get(ErrorCode.COLUMN_ALIAS_IS_NOT_SPECIFIED_1, getTraceSQL());
            case MYSQL_STYLE:
                String sql = getSQL(5, 2);
                if (sql.length() > 64) {
                    sql = "Name_exp_" + (i + 1);
                }
                return sql;
        }
    }

    public Expression getNonAliasExpression() {
        return this;
    }

    public void addFilterConditions(TableFilter tableFilter) {
        if (!this.addedToFilter && isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
            tableFilter.addFilterCondition(this, false);
            this.addedToFilter = true;
        }
    }

    public String toString() {
        return getTraceSQL();
    }

    public int getSubexpressionCount() {
        return 0;
    }

    public Expression getSubexpression(int i) {
        throw new IndexOutOfBoundsException();
    }

    public boolean getWhenValue(SessionLocal sessionLocal, Value value) {
        return sessionLocal.compareWithNull(value, getValue(sessionLocal), true) == 0;
    }

    public StringBuilder getWhenSQL(StringBuilder sb, int i) {
        return getUnenclosedSQL(sb.append(' '), i);
    }

    public boolean isWhenConditionOperand() {
        return false;
    }
}
