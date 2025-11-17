package org.h2.bnf.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.h2.engine.Constants;
import org.h2.engine.Session;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/bnf/context/DbContents.class */
public class DbContents {
    private DbSchema[] schemas;
    private DbSchema defaultSchema;
    private boolean isOracle;
    private boolean isH2;
    private boolean isPostgreSQL;
    private boolean isDerby;
    private boolean isSQLite;
    private boolean isMySQL;
    private boolean isFirebird;
    private boolean isMSSQLServer;
    private boolean isDB2;
    private boolean databaseToUpper;
    private boolean databaseToLower;
    private boolean mayHaveStandardViews = true;

    public DbSchema getDefaultSchema() {
        return this.defaultSchema;
    }

    public boolean isDerby() {
        return this.isDerby;
    }

    public boolean isFirebird() {
        return this.isFirebird;
    }

    public boolean isH2() {
        return this.isH2;
    }

    public boolean isMSSQLServer() {
        return this.isMSSQLServer;
    }

    public boolean isMySQL() {
        return this.isMySQL;
    }

    public boolean isOracle() {
        return this.isOracle;
    }

    public boolean isPostgreSQL() {
        return this.isPostgreSQL;
    }

    public boolean isSQLite() {
        return this.isSQLite;
    }

    public boolean isDB2() {
        return this.isDB2;
    }

    public DbSchema[] getSchemas() {
        return this.schemas;
    }

    public boolean mayHaveStandardViews() {
        return this.mayHaveStandardViews;
    }

    public void setMayHaveStandardViews(boolean z) {
        this.mayHaveStandardViews = z;
    }

    public synchronized void readContents(String str, Connection connection) throws SQLException {
        this.isH2 = str.startsWith(Constants.START_URL);
        this.isDB2 = str.startsWith("jdbc:db2:");
        this.isSQLite = str.startsWith("jdbc:sqlite:");
        this.isOracle = str.startsWith("jdbc:oracle:");
        this.isPostgreSQL = str.startsWith("jdbc:postgresql:") || str.startsWith("jdbc:vertica:");
        this.isMySQL = str.startsWith("jdbc:mysql:");
        this.isDerby = str.startsWith("jdbc:derby:");
        this.isFirebird = str.startsWith("jdbc:firebirdsql:");
        this.isMSSQLServer = str.startsWith("jdbc:sqlserver:");
        if (this.isH2) {
            Session.StaticSettings staticSettings = ((JdbcConnection) connection).getStaticSettings();
            this.databaseToUpper = staticSettings.databaseToUpper;
            this.databaseToLower = staticSettings.databaseToLower;
        } else if (this.isMySQL || this.isPostgreSQL) {
            this.databaseToUpper = false;
            this.databaseToLower = true;
        } else {
            this.databaseToUpper = true;
            this.databaseToLower = false;
        }
        DatabaseMetaData metaData = connection.getMetaData();
        String defaultSchemaName = getDefaultSchemaName(metaData);
        String[] schemaNames = getSchemaNames(metaData);
        this.schemas = new DbSchema[schemaNames.length];
        for (int i = 0; i < schemaNames.length; i++) {
            String str2 = schemaNames[i];
            boolean z = defaultSchemaName == null || defaultSchemaName.equals(str2);
            DbSchema dbSchema = new DbSchema(this, str2, z);
            if (z) {
                this.defaultSchema = dbSchema;
            }
            this.schemas[i] = dbSchema;
            dbSchema.readTables(metaData, new String[]{"TABLE", "SYSTEM TABLE", "VIEW", "SYSTEM VIEW", "TABLE LINK", "SYNONYM", "EXTERNAL"});
            if (!this.isPostgreSQL && !this.isDB2) {
                dbSchema.readProcedures(metaData);
            }
        }
        if (this.defaultSchema == null) {
            String str3 = null;
            for (DbSchema dbSchema2 : this.schemas) {
                if ("dbo".equals(dbSchema2.name)) {
                    this.defaultSchema = dbSchema2;
                    return;
                }
                if (this.defaultSchema == null || str3 == null || dbSchema2.name.length() < str3.length()) {
                    str3 = dbSchema2.name;
                    this.defaultSchema = dbSchema2;
                }
            }
        }
    }

    private String[] getSchemaNames(DatabaseMetaData databaseMetaData) throws SQLException {
        if (this.isMySQL || this.isSQLite) {
            return new String[]{""};
        }
        if (this.isFirebird) {
            return new String[]{null};
        }
        ResultSet schemas = databaseMetaData.getSchemas();
        ArrayList newSmallArrayList = Utils.newSmallArrayList();
        while (schemas.next()) {
            String string = schemas.getString("TABLE_SCHEM");
            String[] strArr = null;
            if (this.isOracle) {
                strArr = new String[]{"CTXSYS", "DIP", "DBSNMP", "DMSYS", "EXFSYS", "FLOWS_020100", "FLOWS_FILES", "MDDATA", "MDSYS", "MGMT_VIEW", "OLAPSYS", "ORDSYS", "ORDPLUGINS", "OUTLN", "SI_INFORMTN_SCHEMA", "SYS", "SYSMAN", "SYSTEM", "TSMSYS", "WMSYS", "XDB"};
            } else if (this.isMSSQLServer) {
                strArr = new String[]{"sys", "db_accessadmin", "db_backupoperator", "db_datareader", "db_datawriter", "db_ddladmin", "db_denydatareader", "db_denydatawriter", "db_owner", "db_securityadmin"};
            } else if (this.isDB2) {
                strArr = new String[]{"NULLID", "SYSFUN", "SYSIBMINTERNAL", "SYSIBMTS", "SYSPROC", "SYSPUBLIC", "SYSCAT", "SYSIBM", "SYSIBMADM", "SYSSTAT", "SYSTOOLS"};
            }
            if (strArr != null) {
                String[] strArr2 = strArr;
                int length = strArr2.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    if (!strArr2[i].equals(string)) {
                        i++;
                    } else {
                        string = null;
                        break;
                    }
                }
            }
            if (string != null) {
                newSmallArrayList.add(string);
            }
        }
        schemas.close();
        return (String[]) newSmallArrayList.toArray(new String[0]);
    }

    private String getDefaultSchemaName(DatabaseMetaData databaseMetaData) {
        if (this.isH2) {
            return databaseMetaData.storesLowerCaseIdentifiers() ? "public" : "PUBLIC";
        }
        if (this.isOracle) {
            return databaseMetaData.getUserName();
        }
        if (this.isPostgreSQL) {
            return "public";
        }
        if (this.isMySQL) {
            return "";
        }
        if (this.isDerby) {
            return StringUtils.toUpperEnglish(databaseMetaData.getUserName());
        }
        if (this.isFirebird) {
            return null;
        }
        return "";
    }

    public String quoteIdentifier(String str) {
        if (str == null) {
            return null;
        }
        if (ParserUtil.isSimpleIdentifier(str, this.databaseToUpper, this.databaseToLower)) {
            return str;
        }
        return StringUtils.quoteIdentifier(str);
    }
}
