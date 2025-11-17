package org.h2.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.h2.command.query.QueryOrderBy;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.mode.DefaultNullOrdering;
import org.h2.table.Column;
import org.h2.table.TableFilter;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/result/SortOrder.class */
public final class SortOrder implements Comparator<Value[]> {
    public static final int ASCENDING = 0;
    public static final int DESCENDING = 1;
    public static final int NULLS_FIRST = 2;
    public static final int NULLS_LAST = 4;
    private final SessionLocal session;
    private final int[] queryColumnIndexes;
    private final int[] sortTypes;
    private final ArrayList<QueryOrderBy> orderList;

    public SortOrder(SessionLocal sessionLocal, int[] iArr) {
        this(sessionLocal, iArr, new int[iArr.length], null);
    }

    public SortOrder(SessionLocal sessionLocal, int[] iArr, int[] iArr2, ArrayList<QueryOrderBy> arrayList) {
        this.session = sessionLocal;
        this.queryColumnIndexes = iArr;
        this.sortTypes = iArr2;
        this.orderList = arrayList;
    }

    public StringBuilder getSQL(StringBuilder sb, Expression[] expressionArr, int i, int i2) {
        int i3 = 0;
        for (int i4 : this.queryColumnIndexes) {
            if (i3 > 0) {
                sb.append(", ");
            }
            if (i4 < i) {
                sb.append(i4 + 1);
            } else {
                expressionArr[i4].getUnenclosedSQL(sb, i2);
            }
            int i5 = i3;
            i3++;
            typeToString(sb, this.sortTypes[i5]);
        }
        return sb;
    }

    public static void typeToString(StringBuilder sb, int i) {
        if ((i & 1) != 0) {
            sb.append(" DESC");
        }
        if ((i & 2) != 0) {
            sb.append(" NULLS FIRST");
        } else if ((i & 4) != 0) {
            sb.append(" NULLS LAST");
        }
    }

    @Override // java.util.Comparator
    public int compare(Value[] valueArr, Value[] valueArr2) {
        int length = this.queryColumnIndexes.length;
        for (int i = 0; i < length; i++) {
            int i2 = this.queryColumnIndexes[i];
            int i3 = this.sortTypes[i];
            Value value = valueArr[i2];
            Value value2 = valueArr2[i2];
            boolean z = value == ValueNull.INSTANCE;
            boolean z2 = value2 == ValueNull.INSTANCE;
            if (z || z2) {
                if (z != z2) {
                    return this.session.getDatabase().getDefaultNullOrdering().compareNull(z, i3);
                }
            } else {
                int compare = this.session.compare(value, value2);
                if (compare != 0) {
                    return (i3 & 1) == 0 ? compare : -compare;
                }
            }
        }
        return 0;
    }

    public void sort(ArrayList<Value[]> arrayList) {
        arrayList.sort(this);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void sort(ArrayList<Value[]> arrayList, int i, int i2) {
        if (i2 == 1 && i == 0) {
            arrayList.set(0, Collections.min(arrayList, this));
            return;
        }
        Value[][] valueArr = (Value[][]) arrayList.toArray(new Value[0]);
        Utils.sortTopN(valueArr, i, i2, this);
        for (int i3 = i; i3 < i2; i3++) {
            arrayList.set(i3, valueArr[i3]);
        }
    }

    public int[] getQueryColumnIndexes() {
        return this.queryColumnIndexes;
    }

    public Column getColumn(int i, TableFilter tableFilter) {
        Expression expression;
        if (this.orderList == null || (expression = this.orderList.get(i).expression) == null) {
            return null;
        }
        Expression nonAliasExpression = expression.getNonAliasExpression();
        if (nonAliasExpression.isConstant() || !(nonAliasExpression instanceof ExpressionColumn)) {
            return null;
        }
        ExpressionColumn expressionColumn = (ExpressionColumn) nonAliasExpression;
        if (expressionColumn.getTableFilter() != tableFilter) {
            return null;
        }
        return expressionColumn.getColumn();
    }

    public int[] getSortTypes() {
        return this.sortTypes;
    }

    public ArrayList<QueryOrderBy> getOrderList() {
        return this.orderList;
    }

    public int[] getSortTypesWithNullOrdering() {
        return addNullOrdering(this.session.getDatabase(), (int[]) this.sortTypes.clone());
    }

    public static int[] addNullOrdering(Database database, int[] iArr) {
        DefaultNullOrdering defaultNullOrdering = database.getDefaultNullOrdering();
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            iArr[i] = defaultNullOrdering.addExplicitNullOrdering(iArr[i]);
        }
        return iArr;
    }

    public Comparator<Value> getRowValueComparator() {
        return (value, value2) -> {
            return compare(((ValueRow) value).getList(), ((ValueRow) value2).getList());
        };
    }
}
