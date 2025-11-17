package org.h2.bnf.context;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/context/DbProcedure.class */
public class DbProcedure {
    private final DbSchema schema;
    private final String name;
    private final String quotedName;
    private final boolean returnsResult;
    private DbColumn[] parameters;

    public DbProcedure(DbSchema dbSchema, ResultSet resultSet) throws SQLException {
        this.schema = dbSchema;
        this.name = resultSet.getString("PROCEDURE_NAME");
        this.returnsResult = resultSet.getShort("PROCEDURE_TYPE") == 2;
        this.quotedName = dbSchema.getContents().quoteIdentifier(this.name);
    }

    public DbSchema getSchema() {
        return this.schema;
    }

    public DbColumn[] getParameters() {
        return this.parameters;
    }

    public String getName() {
        return this.name;
    }

    public String getQuotedName() {
        return this.quotedName;
    }

    public boolean isReturnsResult() {
        return this.returnsResult;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readParameters(DatabaseMetaData databaseMetaData) throws SQLException {
        ResultSet procedureColumns = databaseMetaData.getProcedureColumns(null, this.schema.name, this.name, null);
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        while (procedureColumns.next()) {
            DbColumn procedureColumn = DbColumn.getProcedureColumn(this.schema.getContents(), procedureColumns);
            if (procedureColumn.getPosition() > 0) {
                newSmallArrayList.add(procedureColumn);
            }
        }
        procedureColumns.close();
        this.parameters = new DbColumn[newSmallArrayList.size()];
        for (int i = 0; i < this.parameters.length; i++) {
            DbColumn dbColumn = (DbColumn) newSmallArrayList.get(i);
            if (dbColumn.getPosition() > 0 && dbColumn.getPosition() <= this.parameters.length) {
                this.parameters[dbColumn.getPosition() - 1] = dbColumn;
            }
        }
    }
}
