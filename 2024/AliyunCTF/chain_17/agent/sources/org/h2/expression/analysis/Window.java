package org.h2.expression.analysis;

import ch.qos.logback.core.joran.JoranConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import org.h2.api.ErrorCode;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/Window.class */
public final class Window {
    private ArrayList<Expression> partitionBy;
    private ArrayList<QueryOrderBy> orderBy;
    private WindowFrame frame;
    private String parent;

    public static void appendOrderBy(StringBuilder sb, ArrayList<QueryOrderBy> arrayList, int i, boolean z) {
        if (arrayList != null && !arrayList.isEmpty()) {
            appendOrderByStart(sb);
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                QueryOrderBy queryOrderBy = arrayList.get(i2);
                if (i2 > 0) {
                    sb.append(", ");
                }
                queryOrderBy.expression.getUnenclosedSQL(sb, i);
                SortOrder.typeToString(sb, queryOrderBy.sortType);
            }
            return;
        }
        if (z) {
            appendOrderByStart(sb);
            sb.append(JoranConstants.NULL);
        }
    }

    private static void appendOrderByStart(StringBuilder sb) {
        if (sb.charAt(sb.length() - 1) != '(') {
            sb.append(' ');
        }
        sb.append("ORDER BY ");
    }

    public Window(String str, ArrayList<Expression> arrayList, ArrayList<QueryOrderBy> arrayList2, WindowFrame windowFrame) {
        this.parent = str;
        this.partitionBy = arrayList;
        this.orderBy = arrayList2;
        this.frame = windowFrame;
    }

    public void mapColumns(ColumnResolver columnResolver, int i) {
        resolveWindows(columnResolver);
        if (this.partitionBy != null) {
            Iterator<Expression> it = this.partitionBy.iterator();
            while (it.hasNext()) {
                it.next().mapColumns(columnResolver, i, 1);
            }
        }
        if (this.orderBy != null) {
            Iterator<QueryOrderBy> it2 = this.orderBy.iterator();
            while (it2.hasNext()) {
                it2.next().expression.mapColumns(columnResolver, i, 1);
            }
        }
        if (this.frame != null) {
            this.frame.mapColumns(columnResolver, i, 1);
        }
    }

    private void resolveWindows(ColumnResolver columnResolver) {
        if (this.parent != null) {
            Select select = columnResolver.getSelect();
            do {
                Window window = select.getWindow(this.parent);
                if (window == null) {
                    select = select.getParentSelect();
                } else {
                    window.resolveWindows(columnResolver);
                    if (this.partitionBy == null) {
                        this.partitionBy = window.partitionBy;
                    }
                    if (this.orderBy == null) {
                        this.orderBy = window.orderBy;
                    }
                    if (this.frame == null) {
                        this.frame = window.frame;
                    }
                    this.parent = null;
                    return;
                }
            } while (select != null);
            throw DbException.get(ErrorCode.WINDOW_NOT_FOUND_1, this.parent);
        }
    }

    public void optimize(SessionLocal sessionLocal) {
        if (this.partitionBy != null) {
            ListIterator<Expression> listIterator = this.partitionBy.listIterator();
            while (listIterator.hasNext()) {
                Expression optimize = listIterator.next().optimize(sessionLocal);
                if (optimize.isConstant()) {
                    listIterator.remove();
                } else {
                    listIterator.set(optimize);
                }
            }
            if (this.partitionBy.isEmpty()) {
                this.partitionBy = null;
            }
        }
        if (this.orderBy != null) {
            Iterator<QueryOrderBy> it = this.orderBy.iterator();
            while (it.hasNext()) {
                QueryOrderBy next = it.next();
                Expression optimize2 = next.expression.optimize(sessionLocal);
                if (optimize2.isConstant()) {
                    it.remove();
                } else {
                    next.expression = optimize2;
                }
            }
            if (this.orderBy.isEmpty()) {
                this.orderBy = null;
            }
        }
        if (this.frame != null) {
            this.frame.optimize(sessionLocal);
        }
    }

    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        if (this.partitionBy != null) {
            Iterator<Expression> it = this.partitionBy.iterator();
            while (it.hasNext()) {
                it.next().setEvaluatable(tableFilter, z);
            }
        }
        if (this.orderBy != null) {
            Iterator<QueryOrderBy> it2 = this.orderBy.iterator();
            while (it2.hasNext()) {
                it2.next().expression.setEvaluatable(tableFilter, z);
            }
        }
    }

    public ArrayList<QueryOrderBy> getOrderBy() {
        return this.orderBy;
    }

    public WindowFrame getWindowFrame() {
        return this.frame;
    }

    public boolean isOrdered() {
        WindowFrameBound following;
        if (this.orderBy != null) {
            return true;
        }
        if (this.frame != null && this.frame.getUnits() == WindowFrameUnits.ROWS) {
            if (this.frame.getStarting().getType() == WindowFrameBoundType.UNBOUNDED_PRECEDING && (following = this.frame.getFollowing()) != null && following.getType() == WindowFrameBoundType.UNBOUNDED_FOLLOWING) {
                return false;
            }
            return true;
        }
        return false;
    }

    public Value getCurrentKey(SessionLocal sessionLocal) {
        if (this.partitionBy == null) {
            return null;
        }
        int size = this.partitionBy.size();
        if (size == 1) {
            return this.partitionBy.get(0).getValue(sessionLocal);
        }
        Value[] valueArr = new Value[size];
        for (int i = 0; i < size; i++) {
            valueArr[i] = this.partitionBy.get(i).getValue(sessionLocal);
        }
        return ValueRow.get(valueArr);
    }

    public StringBuilder getSQL(StringBuilder sb, int i, boolean z) {
        sb.append("OVER (");
        if (this.partitionBy != null) {
            sb.append("PARTITION BY ");
            for (int i2 = 0; i2 < this.partitionBy.size(); i2++) {
                if (i2 > 0) {
                    sb.append(", ");
                }
                this.partitionBy.get(i2).getUnenclosedSQL(sb, i);
            }
        }
        appendOrderBy(sb, this.orderBy, i, z);
        if (this.frame != null) {
            if (sb.charAt(sb.length() - 1) != '(') {
                sb.append(' ');
            }
            this.frame.getSQL(sb, i);
        }
        return sb.append(')');
    }

    public void updateAggregate(SessionLocal sessionLocal, int i) {
        if (this.partitionBy != null) {
            Iterator<Expression> it = this.partitionBy.iterator();
            while (it.hasNext()) {
                it.next().updateAggregate(sessionLocal, i);
            }
        }
        if (this.orderBy != null) {
            Iterator<QueryOrderBy> it2 = this.orderBy.iterator();
            while (it2.hasNext()) {
                it2.next().expression.updateAggregate(sessionLocal, i);
            }
        }
        if (this.frame != null) {
            this.frame.updateAggregate(sessionLocal, i);
        }
    }

    public String toString() {
        return getSQL(new StringBuilder(), 3, false).toString();
    }
}
