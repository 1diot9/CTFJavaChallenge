package org.jooq.tools.jdbc;

import io.r2dbc.spi.ConnectionFactory;
import java.io.Closeable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLXML;
import java.sql.Statement;
import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/JDBCUtils.class */
public class JDBCUtils {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) JDBCUtils.class);

    @NotNull
    public static final SQLDialect dialect(Connection connection) {
        SQLDialect result = SQLDialect.DEFAULT;
        if (connection != null) {
            try {
                DatabaseMetaData m = connection.getMetaData();
                String url = m.getURL();
                int majorVersion = 0;
                int minorVersion = 0;
                String productVersion = "";
                try {
                    majorVersion = m.getDatabaseMajorVersion();
                } catch (SQLException e) {
                }
                try {
                    minorVersion = m.getDatabaseMinorVersion();
                } catch (SQLException e2) {
                }
                try {
                    productVersion = m.getDatabaseProductVersion();
                } catch (SQLException e3) {
                }
                result = dialect(url, majorVersion, minorVersion, productVersion);
            } catch (SQLException e4) {
            }
        }
        if (result == SQLDialect.DEFAULT) {
        }
        return result;
    }

    @NotNull
    public static final SQLDialect dialect(ConnectionFactory connection) {
        SQLDialect result = SQLDialect.DEFAULT;
        if (connection != null) {
            result = dialectFromProductName(connection.getMetadata().getName());
        }
        if (result == SQLDialect.DEFAULT) {
        }
        return result;
    }

    @NotNull
    public static final SQLDialect dialect(io.r2dbc.spi.Connection connection) {
        SQLDialect result = SQLDialect.DEFAULT;
        if (connection != null) {
            result = dialectFromProductName(connection.getMetadata().getDatabaseProductName());
        }
        if (result == SQLDialect.DEFAULT) {
        }
        return result;
    }

    private static SQLDialect dialectFromProductName(String product) {
        String p = product.toLowerCase().replace(" ", "");
        if (p.contains("h2")) {
            return SQLDialect.H2;
        }
        if (p.contains("mariadb")) {
            return SQLDialect.MARIADB;
        }
        if (p.contains("mysql")) {
            return SQLDialect.MYSQL;
        }
        if (p.contains("postgres")) {
            return SQLDialect.POSTGRES;
        }
        return SQLDialect.DEFAULT;
    }

    @NotNull
    private static final SQLDialect dialect(String url, int majorVersion, int minorVersion, String productVersion) {
        SQLDialect family = dialect(url).family();
        if (majorVersion == 0) {
            return family;
        }
        switch (family) {
            case FIREBIRD:
                return firebirdDialect(majorVersion);
            case H2:
                return h2Dialect(majorVersion, minorVersion, productVersion);
            case MARIADB:
                return mariadbDialect(majorVersion, minorVersion);
            case MYSQL:
                return mysqlDialect(majorVersion, minorVersion, productVersion);
            case POSTGRES:
                return postgresDialect(majorVersion, minorVersion);
            default:
                return family;
        }
    }

    private static final SQLDialect postgresDialect(int majorVersion, int minorVersion) {
        return SQLDialect.POSTGRES;
    }

    private static final SQLDialect mariadbDialect(int majorVersion, int minorVersion) {
        return SQLDialect.MARIADB;
    }

    private static final SQLDialect mysqlDialect(int majorVersion, int minorVersion, String productVersion) {
        return SQLDialect.MYSQL;
    }

    private static final SQLDialect firebirdDialect(int majorVersion) {
        return SQLDialect.FIREBIRD;
    }

    private static final SQLDialect h2Dialect(int majorVersion, int minorVersion, String productVersion) {
        return SQLDialect.H2;
    }

    @NotNull
    public static final SQLDialect dialect(String url) {
        if (url == null) {
            return SQLDialect.DEFAULT;
        }
        if (url.contains(":cubrid:")) {
            return SQLDialect.CUBRID;
        }
        if (url.contains(":derby:")) {
            return SQLDialect.DERBY;
        }
        if (url.contains(":duckdb:")) {
            return SQLDialect.DUCKDB;
        }
        if (url.contains(":firebirdsql:")) {
            return SQLDialect.FIREBIRD;
        }
        if (url.contains(":h2:")) {
            return SQLDialect.H2;
        }
        if (url.contains(":hsqldb:")) {
            return SQLDialect.HSQLDB;
        }
        if (url.contains(":ignite:")) {
            return SQLDialect.IGNITE;
        }
        if (url.contains(":mariadb:")) {
            return SQLDialect.MARIADB;
        }
        if (url.contains(":mysql:") || url.contains(":google:")) {
            return SQLDialect.MYSQL;
        }
        if (url.contains(":postgresql:") || url.contains(":pgsql:")) {
            return SQLDialect.POSTGRES;
        }
        if (url.contains(":sqlite:") || url.contains(":sqldroid:")) {
            return SQLDialect.SQLITE;
        }
        if (url.contains(":trino:")) {
            return SQLDialect.TRINO;
        }
        if (url.contains(":yugabytedb:")) {
            return SQLDialect.YUGABYTEDB;
        }
        return SQLDialect.DEFAULT;
    }

    @NotNull
    public static final String driver(SQLDialect dialect) {
        switch (dialect.family()) {
            case FIREBIRD:
                return "org.firebirdsql.jdbc.FBDriver";
            case H2:
                return "org.h2.Driver";
            case MARIADB:
                return "org.mariadb.jdbc.Driver";
            case MYSQL:
                return "com.mysql.cj.jdbc.Driver";
            case POSTGRES:
                return "org.postgresql.Driver";
            case CUBRID:
                return "cubrid.jdbc.driver.CUBRIDDriver";
            case DERBY:
                return "org.apache.derby.jdbc.ClientDriver";
            case DUCKDB:
                return "org.duckdb.DuckDBDriver";
            case HSQLDB:
                return "org.hsqldb.jdbcDriver";
            case IGNITE:
                return "org.apache.ignite.IgniteJdbcThinDriver";
            case SQLITE:
                return "org.sqlite.JDBC";
            case YUGABYTEDB:
                return "com.yugabyte.Driver";
            default:
                return "java.sql.Driver";
        }
    }

    @NotNull
    public static final String driver(String url) {
        return driver(dialect(url).family());
    }

    public static final void safeClose(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignore) {
                log.debug((Object) "Error when closing connection", (Throwable) ignore);
            }
        }
    }

    public static final void safeClose(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ignore) {
                log.debug((Object) "Error when closing statement", (Throwable) ignore);
            }
        }
    }

    public static final void safeClose(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception ignore) {
                log.debug((Object) "Error when closing result set", (Throwable) ignore);
            }
        }
    }

    public static final void safeClose(ResultSet resultSet, PreparedStatement statement) {
        safeClose(resultSet);
        safeClose((Statement) statement);
    }

    public static final void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
                log.debug((Object) "Error when closing closeable", (Throwable) ignore);
            }
        }
    }

    public static final void safeClose(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignore) {
                log.debug((Object) "Error when closing closeable", (Throwable) ignore);
            }
        }
    }

    public static final void safeFree(Blob blob) {
        if (blob != null) {
            try {
                blob.free();
            } catch (AbstractMethodError e) {
            } catch (Exception e2) {
                log.warn((Object) "Error while freeing resource", (Throwable) e2);
            }
        }
    }

    public static final void safeFree(Clob clob) {
        if (clob != null) {
            try {
                clob.free();
            } catch (AbstractMethodError e) {
            } catch (Exception e2) {
                log.warn((Object) "Error while freeing resource", (Throwable) e2);
            }
        }
    }

    public static final void safeFree(SQLXML xml) {
        if (xml != null) {
            try {
                xml.free();
            } catch (AbstractMethodError e) {
            } catch (Exception e2) {
                log.warn((Object) "Error while freeing resource", (Throwable) e2);
            }
        }
    }

    public static final void safeFree(Array array) {
        if (array != null) {
            try {
                array.free();
            } catch (AbstractMethodError e) {
            } catch (Exception e2) {
                log.warn((Object) "Error while freeing resource", (Throwable) e2);
            }
        }
    }

    public static final <T> T wasNull(SQLInput stream, T value) throws SQLException {
        if (value == null || stream.wasNull()) {
            return null;
        }
        return value;
    }

    public static final <T extends Number> T wasNull(SQLInput stream, T value) throws SQLException {
        if (value == null || (value.intValue() == 0 && stream.wasNull())) {
            return null;
        }
        return value;
    }

    public static final Boolean wasNull(SQLInput stream, Boolean value) throws SQLException {
        if (value == null || (!value.booleanValue() && stream.wasNull())) {
            return null;
        }
        return value;
    }

    public static final <T> T wasNull(ResultSet rs, T value) throws SQLException {
        if (value == null || rs.wasNull()) {
            return null;
        }
        return value;
    }

    public static final <T extends Number> T wasNull(ResultSet rs, T value) throws SQLException {
        if (value == null || (value.intValue() == 0 && rs.wasNull())) {
            return null;
        }
        return value;
    }

    public static final Boolean wasNull(ResultSet rs, Boolean value) throws SQLException {
        if (value == null || (!value.booleanValue() && rs.wasNull())) {
            return null;
        }
        return value;
    }

    public static final <T> T wasNull(CallableStatement statement, T value) throws SQLException {
        if (value == null || statement.wasNull()) {
            return null;
        }
        return value;
    }

    public static final <T extends Number> T wasNull(CallableStatement statement, T value) throws SQLException {
        if (value == null || (value.intValue() == 0 && statement.wasNull())) {
            return null;
        }
        return value;
    }

    public static final Boolean wasNull(CallableStatement statement, Boolean value) throws SQLException {
        if (value == null || (!value.booleanValue() && statement.wasNull())) {
            return null;
        }
        return value;
    }

    private JDBCUtils() {
    }
}
