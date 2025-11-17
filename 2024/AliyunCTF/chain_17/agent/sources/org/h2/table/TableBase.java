package org.h2.table;

import java.util.Collections;
import java.util.List;
import org.h2.command.ddl.CreateTableData;
import org.h2.engine.Database;
import org.h2.index.IndexType;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableBase.class */
public abstract class TableBase extends Table {
    private final String tableEngine;
    private final List<String> tableEngineParams;
    private final boolean globalTemporary;

    public static int getMainIndexColumn(IndexType indexType, IndexColumn[] indexColumnArr) {
        if (!indexType.isPrimaryKey() || indexColumnArr.length != 1) {
            return -1;
        }
        IndexColumn indexColumn = indexColumnArr[0];
        if ((indexColumn.sortType & 1) != 0) {
            return -1;
        }
        switch (indexColumn.column.getType().getValueType()) {
            case 9:
            case 10:
            case 11:
            case 12:
                return indexColumn.column.getColumnId();
            default:
                return -1;
        }
    }

    public TableBase(CreateTableData createTableData) {
        super(createTableData.schema, createTableData.id, createTableData.tableName, createTableData.persistIndexes, createTableData.persistData);
        this.tableEngine = createTableData.tableEngine;
        this.globalTemporary = createTableData.globalTemporary;
        if (createTableData.tableEngineParams != null) {
            this.tableEngineParams = createTableData.tableEngineParams;
        } else {
            this.tableEngineParams = Collections.emptyList();
        }
        setTemporary(createTableData.temporary);
        setColumns((Column[]) createTableData.columns.toArray(new Column[0]));
    }

    @Override // org.h2.engine.DbObject
    public String getDropSQL() {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ");
        getSQL(sb, 0).append(" CASCADE");
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForMeta() {
        return getCreateSQL(true);
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQL(false);
    }

    private String getCreateSQL(boolean z) {
        String str;
        Database database = getDatabase();
        if (database == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder("CREATE ");
        if (isTemporary()) {
            if (isGlobalTemporary()) {
                sb.append("GLOBAL ");
            } else {
                sb.append("LOCAL ");
            }
            sb.append("TEMPORARY ");
        } else if (isPersistIndexes()) {
            sb.append("CACHED ");
        } else {
            sb.append("MEMORY ");
        }
        sb.append("TABLE ");
        if (this.isHidden) {
            sb.append("IF NOT EXISTS ");
        }
        getSQL(sb, 0);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        sb.append("(\n    ");
        int length = this.columns.length;
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",\n    ");
            }
            sb.append(this.columns[i].getCreateSQL(z));
        }
        sb.append("\n)");
        if (this.tableEngine != null && ((str = database.getSettings().defaultTableEngine) == null || !this.tableEngine.endsWith(str))) {
            sb.append("\nENGINE ");
            StringUtils.quoteIdentifier(sb, this.tableEngine);
        }
        if (!this.tableEngineParams.isEmpty()) {
            sb.append("\nWITH ");
            int size = this.tableEngineParams.size();
            for (int i2 = 0; i2 < size; i2++) {
                if (i2 > 0) {
                    sb.append(", ");
                }
                StringUtils.quoteIdentifier(sb, this.tableEngineParams.get(i2));
            }
        }
        if (!isPersistIndexes() && !isPersistData()) {
            sb.append("\nNOT PERSISTENT");
        }
        if (this.isHidden) {
            sb.append("\nHIDDEN");
        }
        return sb.toString();
    }

    @Override // org.h2.table.Table
    public boolean isGlobalTemporary() {
        return this.globalTemporary;
    }
}
