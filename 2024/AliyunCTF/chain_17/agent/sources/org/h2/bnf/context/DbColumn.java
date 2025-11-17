package org.h2.bnf.context;

import java.sql.ResultSet;
import java.sql.SQLException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/context/DbColumn.class */
public class DbColumn {
    private final String name;
    private final String quotedName;
    private final String dataType;
    private final int position;

    private DbColumn(DbContents dbContents, ResultSet resultSet, boolean z) throws SQLException {
        String str;
        String str2;
        this.name = resultSet.getString("COLUMN_NAME");
        this.quotedName = dbContents.quoteIdentifier(this.name);
        this.position = resultSet.getInt("ORDINAL_POSITION");
        if (dbContents.isH2() && !z) {
            this.dataType = resultSet.getString("COLUMN_TYPE");
            return;
        }
        String string = resultSet.getString("TYPE_NAME");
        if (z) {
            str = "PRECISION";
            str2 = "SCALE";
        } else {
            str = "COLUMN_SIZE";
            str2 = "DECIMAL_DIGITS";
        }
        int i = resultSet.getInt(str);
        if (i > 0 && !dbContents.isSQLite()) {
            int i2 = resultSet.getInt(str2);
            string = i2 > 0 ? string + '(' + i + ", " + i2 + ')' : string + '(' + i + ')';
        }
        this.dataType = resultSet.getInt("NULLABLE") == 0 ? string + " NOT NULL" : string;
    }

    public static DbColumn getProcedureColumn(DbContents dbContents, ResultSet resultSet) throws SQLException {
        return new DbColumn(dbContents, resultSet, true);
    }

    public static DbColumn getColumn(DbContents dbContents, ResultSet resultSet) throws SQLException {
        return new DbColumn(dbContents, resultSet, false);
    }

    public String getDataType() {
        return this.dataType;
    }

    public String getName() {
        return this.name;
    }

    public String getQuotedName() {
        return this.quotedName;
    }

    public int getPosition() {
        return this.position;
    }
}
