package org.h2.table;

import org.h2.command.query.Select;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/ColumnResolver.class */
public interface ColumnResolver {
    Column[] getColumns();

    Column findColumn(String str);

    Value getValue(Column column);

    default String getTableAlias() {
        return null;
    }

    default String getColumnName(Column column) {
        return column.getName();
    }

    default boolean hasDerivedColumnList() {
        return false;
    }

    default Column[] getSystemColumns() {
        return null;
    }

    default Column getRowIdColumn() {
        return null;
    }

    default String getSchemaName() {
        return null;
    }

    default TableFilter getTableFilter() {
        return null;
    }

    default Select getSelect() {
        return null;
    }

    default Expression optimize(ExpressionColumn expressionColumn, Column column) {
        return expressionColumn;
    }
}
