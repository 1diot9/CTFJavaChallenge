package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashMap;
import org.h2.expression.ExpressionVisitor;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableFilter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/AllColumnsForPlan.class */
public class AllColumnsForPlan {
    private final TableFilter[] filters;
    private HashMap<Table, ArrayList<Column>> map;

    public AllColumnsForPlan(TableFilter[] tableFilterArr) {
        this.filters = tableFilterArr;
    }

    public void add(Column column) {
        ArrayList<Column> arrayList = this.map.get(column.getTable());
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.map.put(column.getTable(), arrayList);
        }
        if (!arrayList.contains(column)) {
            arrayList.add(column);
        }
    }

    public ArrayList<Column> get(Table table) {
        if (this.map == null) {
            this.map = new HashMap<>();
            ExpressionVisitor.allColumnsForTableFilters(this.filters, this);
        }
        return this.map.get(table);
    }
}
