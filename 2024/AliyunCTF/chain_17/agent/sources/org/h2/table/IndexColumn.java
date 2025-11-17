package org.h2.table;

import org.h2.result.SortOrder;
import org.h2.util.ParserUtil;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/IndexColumn.class */
public class IndexColumn {
    public static final int SQL_NO_ORDER = Integer.MIN_VALUE;
    public final String columnName;
    public Column column;
    public int sortType;

    public static StringBuilder writeColumns(StringBuilder sb, IndexColumn[] indexColumnArr, int i) {
        return writeColumns(sb, indexColumnArr, 0, indexColumnArr.length, i);
    }

    public static StringBuilder writeColumns(StringBuilder sb, IndexColumn[] indexColumnArr, int i, int i2, int i3) {
        for (int i4 = i; i4 < i2; i4++) {
            if (i4 > i) {
                sb.append(", ");
            }
            indexColumnArr[i4].getSQL(sb, i3);
        }
        return sb;
    }

    public static StringBuilder writeColumns(StringBuilder sb, IndexColumn[] indexColumnArr, String str, String str2, int i) {
        int length = indexColumnArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (i2 > 0) {
                sb.append(str);
            }
            indexColumnArr[i2].getSQL(sb, i).append(str2);
        }
        return sb;
    }

    public IndexColumn(String str) {
        this.sortType = 0;
        this.columnName = str;
    }

    public IndexColumn(String str, int i) {
        this.sortType = 0;
        this.columnName = str;
        this.sortType = i;
    }

    public IndexColumn(Column column) {
        this.sortType = 0;
        this.columnName = null;
        this.column = column;
    }

    public StringBuilder getSQL(StringBuilder sb, int i) {
        if (this.column != null) {
            this.column.getSQL(sb, i);
        } else {
            ParserUtil.quoteIdentifier(sb, this.columnName, i);
        }
        if ((i & Integer.MIN_VALUE) == 0) {
            SortOrder.typeToString(sb, this.sortType);
        }
        return sb;
    }

    public static IndexColumn[] wrap(Column[] columnArr) {
        IndexColumn[] indexColumnArr = new IndexColumn[columnArr.length];
        for (int i = 0; i < indexColumnArr.length; i++) {
            indexColumnArr[i] = new IndexColumn(columnArr[i]);
        }
        return indexColumnArr;
    }

    public static void mapColumns(IndexColumn[] indexColumnArr, Table table) {
        for (IndexColumn indexColumn : indexColumnArr) {
            indexColumn.column = table.getColumn(indexColumn.columnName);
        }
    }

    public String toString() {
        return getSQL(new StringBuilder("IndexColumn "), 3).toString();
    }
}
