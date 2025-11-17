package org.springframework.boot.jdbc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import javax.sql.DataSource;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DatabaseDriver.class */
public enum DatabaseDriver {
    UNKNOWN(null, null),
    DERBY("Apache Derby", "org.apache.derby.jdbc.EmbeddedDriver", "org.apache.derby.jdbc.EmbeddedXADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1"),
    H2("H2", "org.h2.Driver", "org.h2.jdbcx.JdbcDataSource", "SELECT 1"),
    HSQLDB("HSQL Database Engine", "org.hsqldb.jdbc.JDBCDriver", "org.hsqldb.jdbc.pool.JDBCXADataSource", "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS"),
    SQLITE("SQLite", "org.sqlite.JDBC"),
    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "com.mysql.cj.jdbc.MysqlXADataSource", "/* ping */ SELECT 1"),
    MARIADB("MariaDB", "org.mariadb.jdbc.Driver", "org.mariadb.jdbc.MariaDbDataSource", "SELECT 1"),
    ORACLE("Oracle", "oracle.jdbc.OracleDriver", "oracle.jdbc.xa.client.OracleXADataSource", "SELECT 'Hello' from DUAL"),
    POSTGRESQL("PostgreSQL", "org.postgresql.Driver", "org.postgresql.xa.PGXADataSource", "SELECT 1"),
    REDSHIFT("Redshift", "com.amazon.redshift.jdbc.Driver", null, "SELECT 1"),
    HANA("HDB", "com.sap.db.jdbc.Driver", "com.sap.db.jdbcext.XADataSourceSAP", "SELECT 1 FROM SYS.DUMMY") { // from class: org.springframework.boot.jdbc.DatabaseDriver.1
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected Collection<String> getUrlPrefixes() {
            return Collections.singleton("sap");
        }
    },
    JTDS(null, "net.sourceforge.jtds.jdbc.Driver"),
    SQLSERVER("Microsoft SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "com.microsoft.sqlserver.jdbc.SQLServerXADataSource", "SELECT 1") { // from class: org.springframework.boot.jdbc.DatabaseDriver.2
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected boolean matchProductName(String productName) {
            return super.matchProductName(productName) || "SQL SERVER".equalsIgnoreCase(productName);
        }
    },
    FIREBIRD("Firebird", "org.firebirdsql.jdbc.FBDriver", "org.firebirdsql.ds.FBXADataSource", "SELECT 1 FROM RDB$DATABASE") { // from class: org.springframework.boot.jdbc.DatabaseDriver.3
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected Collection<String> getUrlPrefixes() {
            return Arrays.asList("firebirdsql", "firebird");
        }

        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected boolean matchProductName(String productName) {
            return super.matchProductName(productName) || productName.toLowerCase(Locale.ENGLISH).startsWith("firebird");
        }
    },
    DB2("DB2", "com.ibm.db2.jcc.DB2Driver", "com.ibm.db2.jcc.DB2XADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1") { // from class: org.springframework.boot.jdbc.DatabaseDriver.4
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected boolean matchProductName(String productName) {
            return super.matchProductName(productName) || productName.toLowerCase(Locale.ENGLISH).startsWith("db2/");
        }
    },
    DB2_AS400("DB2 UDB for AS/400", "com.ibm.as400.access.AS400JDBCDriver", "com.ibm.as400.access.AS400JDBCXADataSource", "SELECT 1 FROM SYSIBM.SYSDUMMY1") { // from class: org.springframework.boot.jdbc.DatabaseDriver.5
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        public String getId() {
            return "db2";
        }

        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected Collection<String> getUrlPrefixes() {
            return Collections.singleton("as400");
        }

        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected boolean matchProductName(String productName) {
            return super.matchProductName(productName) || productName.toLowerCase(Locale.ENGLISH).contains("as/400");
        }
    },
    TERADATA("Teradata", "com.teradata.jdbc.TeraDriver"),
    INFORMIX("Informix Dynamic Server", "com.informix.jdbc.IfxDriver", null, "select count(*) from systables") { // from class: org.springframework.boot.jdbc.DatabaseDriver.6
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected Collection<String> getUrlPrefixes() {
            return Arrays.asList("informix-sqli", "informix-direct");
        }
    },
    PHOENIX("Apache Phoenix", "org.apache.phoenix.jdbc.PhoenixDriver", null, "SELECT 1 FROM SYSTEM.CATALOG LIMIT 1"),
    TESTCONTAINERS(null, "org.testcontainers.jdbc.ContainerDatabaseDriver") { // from class: org.springframework.boot.jdbc.DatabaseDriver.7
        @Override // org.springframework.boot.jdbc.DatabaseDriver
        protected Collection<String> getUrlPrefixes() {
            return Collections.singleton("tc");
        }
    };

    private final String productName;
    private final String driverClassName;
    private final String xaDataSourceClassName;
    private final String validationQuery;

    DatabaseDriver(String productName, String driverClassName) {
        this(productName, driverClassName, null);
    }

    DatabaseDriver(String productName, String driverClassName, String xaDataSourceClassName) {
        this(productName, driverClassName, xaDataSourceClassName, null);
    }

    DatabaseDriver(String productName, String driverClassName, String xaDataSourceClassName, String validationQuery) {
        this.productName = productName;
        this.driverClassName = driverClassName;
        this.xaDataSourceClassName = xaDataSourceClassName;
        this.validationQuery = validationQuery;
    }

    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    protected Collection<String> getUrlPrefixes() {
        return Collections.singleton(name().toLowerCase(Locale.ENGLISH));
    }

    protected boolean matchProductName(String productName) {
        return this.productName != null && this.productName.equalsIgnoreCase(productName);
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getXaDataSourceClassName() {
        return this.xaDataSourceClassName;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    public static DatabaseDriver fromJdbcUrl(String url) {
        if (StringUtils.hasLength(url)) {
            Assert.isTrue(url.startsWith("jdbc"), "URL must start with 'jdbc'");
            String urlWithoutPrefix = url.substring("jdbc".length()).toLowerCase(Locale.ENGLISH);
            for (DatabaseDriver driver : values()) {
                for (String urlPrefix : driver.getUrlPrefixes()) {
                    String prefix = ":" + urlPrefix + ":";
                    if (driver != UNKNOWN && urlWithoutPrefix.startsWith(prefix)) {
                        return driver;
                    }
                }
            }
        }
        return UNKNOWN;
    }

    public static DatabaseDriver fromProductName(String productName) {
        if (StringUtils.hasLength(productName)) {
            for (DatabaseDriver candidate : values()) {
                if (candidate.matchProductName(productName)) {
                    return candidate;
                }
            }
        }
        return UNKNOWN;
    }

    @Deprecated(since = "2.7.15", forRemoval = true)
    public static DatabaseDriver fromDataSource(DataSource dataSource) {
        try {
            String productName = JdbcUtils.commonDatabaseName((String) JdbcUtils.extractDatabaseMetaData(dataSource, (v0) -> {
                return v0.getDatabaseProductName();
            }));
            return fromProductName(productName);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
