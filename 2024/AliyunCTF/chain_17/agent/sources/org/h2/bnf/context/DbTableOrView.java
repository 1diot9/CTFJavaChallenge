package org.h2.bnf.context;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/context/DbTableOrView.class */
public class DbTableOrView {
    private final DbSchema schema;
    private final String name;
    private final String quotedName;
    private final boolean isView;
    private DbColumn[] columns;

    public DbTableOrView(DbSchema dbSchema, ResultSet resultSet) throws SQLException {
        this.schema = dbSchema;
        this.name = resultSet.getString("TABLE_NAME");
        this.isView = "VIEW".equals(resultSet.getString("TABLE_TYPE"));
        this.quotedName = dbSchema.getContents().quoteIdentifier(this.name);
    }

    public DbSchema getSchema() {
        return this.schema;
    }

    public DbColumn[] getColumns() {
        return this.columns;
    }

    public String getName() {
        return this.name;
    }

    public boolean isView() {
        return this.isView;
    }

    public String getQuotedName() {
        return this.quotedName;
    }

    public void readColumns(DatabaseMetaData databaseMetaData, PreparedStatement preparedStatement) throws SQLException {
        ResultSet columns;
        if (this.schema.getContents().isH2()) {
            preparedStatement.setString(1, this.schema.name);
            preparedStatement.setString(2, this.name);
            columns = preparedStatement.executeQuery();
        } else {
            columns = databaseMetaData.getColumns(null, this.schema.name, this.name, null);
        }
        ArrayList arrayList = new ArrayList();
        while (columns.next()) {
            arrayList.add(DbColumn.getColumn(this.schema.getContents(), columns));
        }
        columns.close();
        this.columns = (DbColumn[]) arrayList.toArray(new DbColumn[0]);
    }
}
