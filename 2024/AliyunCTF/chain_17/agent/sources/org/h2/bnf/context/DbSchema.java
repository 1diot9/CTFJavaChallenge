package org.h2.bnf.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import org.h2.engine.SysProperties;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/context/DbSchema.class */
public class DbSchema {
    private static final String COLUMNS_QUERY_H2_197 = "SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2";
    private static final String COLUMNS_QUERY_H2_202 = "SELECT COLUMN_NAME, ORDINAL_POSITION, DATA_TYPE_SQL(?1, ?2, 'TABLE', ORDINAL_POSITION) COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ?1 AND TABLE_NAME = ?2";
    public final String name;
    public final boolean isDefault;
    public final boolean isSystem;
    public final String quotedName;
    private final DbContents contents;
    private DbTableOrView[] tables;
    private DbProcedure[] procedures;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbSchema(DbContents dbContents, String str, boolean z) {
        this.contents = dbContents;
        this.name = str;
        this.quotedName = dbContents.quoteIdentifier(str);
        this.isDefault = z;
        if (str == null) {
            this.isSystem = true;
            return;
        }
        if ("INFORMATION_SCHEMA".equalsIgnoreCase(str)) {
            this.isSystem = true;
            return;
        }
        if (!dbContents.isH2() && StringUtils.toUpperEnglish(str).startsWith("INFO")) {
            this.isSystem = true;
            return;
        }
        if (dbContents.isPostgreSQL() && StringUtils.toUpperEnglish(str).startsWith("PG_")) {
            this.isSystem = true;
        } else if (dbContents.isDerby() && str.startsWith("SYS")) {
            this.isSystem = true;
        } else {
            this.isSystem = false;
        }
    }

    public DbContents getContents() {
        return this.contents;
    }

    public DbTableOrView[] getTables() {
        return this.tables;
    }

    public DbProcedure[] getProcedures() {
        return this.procedures;
    }

    public void readTables(DatabaseMetaData databaseMetaData, String[] strArr) throws SQLException {
        ResultSet tables = databaseMetaData.getTables(null, this.name, null, strArr);
        ArrayList arrayList = new ArrayList();
        while (tables.next()) {
            DbTableOrView dbTableOrView = new DbTableOrView(this, tables);
            if (!this.contents.isOracle() || dbTableOrView.getName().indexOf(36) <= 0) {
                arrayList.add(dbTableOrView);
            }
        }
        tables.close();
        this.tables = (DbTableOrView[]) arrayList.toArray(new DbTableOrView[0]);
        if (this.tables.length < SysProperties.CONSOLE_MAX_TABLES_LIST_COLUMNS) {
            PreparedStatement prepareColumnsQueryH2 = this.contents.isH2() ? prepareColumnsQueryH2(databaseMetaData.getConnection()) : null;
            Throwable th = null;
            try {
                try {
                    for (DbTableOrView dbTableOrView2 : this.tables) {
                        try {
                            dbTableOrView2.readColumns(databaseMetaData, prepareColumnsQueryH2);
                        } catch (SQLException e) {
                        }
                    }
                    if (prepareColumnsQueryH2 != null) {
                        if (0 != 0) {
                            try {
                                prepareColumnsQueryH2.close();
                                return;
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                                return;
                            }
                        }
                        prepareColumnsQueryH2.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (prepareColumnsQueryH2 != null) {
                    if (th != null) {
                        try {
                            prepareColumnsQueryH2.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        prepareColumnsQueryH2.close();
                    }
                }
                throw th4;
            }
        }
    }

    private static PreparedStatement prepareColumnsQueryH2(Connection connection) throws SQLException {
        try {
            return connection.prepareStatement(COLUMNS_QUERY_H2_202);
        } catch (SQLSyntaxErrorException e) {
            return connection.prepareStatement(COLUMNS_QUERY_H2_197);
        }
    }

    public void readProcedures(DatabaseMetaData databaseMetaData) throws SQLException {
        ResultSet procedures = databaseMetaData.getProcedures(null, this.name, null);
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        while (procedures.next()) {
            newSmallArrayList.add(new DbProcedure(this, procedures));
        }
        procedures.close();
        this.procedures = (DbProcedure[]) newSmallArrayList.toArray(new DbProcedure[0]);
        if (this.procedures.length < SysProperties.CONSOLE_MAX_PROCEDURES_LIST_COLUMNS) {
            for (DbProcedure dbProcedure : this.procedures) {
                dbProcedure.readParameters(databaseMetaData);
            }
        }
    }
}
