package cn.hutool.db.dialect;

import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.dialect.impl.AnsiSqlDialect;
import cn.hutool.db.dialect.impl.H2Dialect;
import cn.hutool.db.dialect.impl.MysqlDialect;
import cn.hutool.db.dialect.impl.OracleDialect;
import cn.hutool.db.dialect.impl.PhoenixDialect;
import cn.hutool.db.dialect.impl.PostgresqlDialect;
import cn.hutool.db.dialect.impl.SqlServer2012Dialect;
import cn.hutool.db.dialect.impl.Sqlite3Dialect;
import cn.hutool.log.StaticLog;
import java.sql.Connection;
import java.util.Map;
import javax.sql.DataSource;
import org.h2.security.auth.impl.JaasCredentialsValidator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/DialectFactory.class */
public class DialectFactory implements DriverNamePool {
    private static final Map<DataSource, Dialect> DIALECT_POOL = new SafeConcurrentHashMap();

    private DialectFactory() {
    }

    public static Dialect newDialect(String driverName) {
        Dialect dialect = internalNewDialect(driverName);
        StaticLog.debug("Use Dialect: [{}].", dialect.getClass().getSimpleName());
        return dialect;
    }

    private static Dialect internalNewDialect(String driverName) {
        if (StrUtil.isNotBlank(driverName)) {
            if (DriverNamePool.DRIVER_MYSQL.equalsIgnoreCase(driverName) || DriverNamePool.DRIVER_MYSQL_V6.equalsIgnoreCase(driverName)) {
                return new MysqlDialect();
            }
            if (DriverNamePool.DRIVER_ORACLE.equalsIgnoreCase(driverName) || DriverNamePool.DRIVER_ORACLE_OLD.equalsIgnoreCase(driverName)) {
                return new OracleDialect();
            }
            if (DriverNamePool.DRIVER_SQLLITE3.equalsIgnoreCase(driverName)) {
                return new Sqlite3Dialect();
            }
            if (DriverNamePool.DRIVER_POSTGRESQL.equalsIgnoreCase(driverName)) {
                return new PostgresqlDialect();
            }
            if (DriverNamePool.DRIVER_H2.equalsIgnoreCase(driverName)) {
                return new H2Dialect();
            }
            if (DriverNamePool.DRIVER_SQLSERVER.equalsIgnoreCase(driverName)) {
                return new SqlServer2012Dialect();
            }
            if (DriverNamePool.DRIVER_PHOENIX.equalsIgnoreCase(driverName)) {
                return new PhoenixDialect();
            }
        }
        return new AnsiSqlDialect();
    }

    public static String identifyDriver(String nameContainsProductInfo) {
        return identifyDriver(nameContainsProductInfo, null);
    }

    public static String identifyDriver(String nameContainsProductInfo, ClassLoader classLoader) {
        if (StrUtil.isBlank(nameContainsProductInfo)) {
            return null;
        }
        String nameContainsProductInfo2 = StrUtil.cleanBlank(nameContainsProductInfo.toLowerCase());
        String name = ReUtil.getGroup1("jdbc:(.*?):", nameContainsProductInfo2);
        if (StrUtil.isNotBlank(name)) {
            nameContainsProductInfo2 = name;
        }
        String driver = null;
        if (nameContainsProductInfo2.contains("mysql") || nameContainsProductInfo2.contains("cobar")) {
            driver = ClassLoaderUtil.isPresent(DriverNamePool.DRIVER_MYSQL_V6, classLoader) ? DriverNamePool.DRIVER_MYSQL_V6 : DriverNamePool.DRIVER_MYSQL;
        } else if (nameContainsProductInfo2.contains("oracle")) {
            driver = ClassLoaderUtil.isPresent(DriverNamePool.DRIVER_ORACLE, classLoader) ? DriverNamePool.DRIVER_ORACLE : DriverNamePool.DRIVER_ORACLE_OLD;
        } else if (nameContainsProductInfo2.contains("postgresql")) {
            driver = DriverNamePool.DRIVER_POSTGRESQL;
        } else if (nameContainsProductInfo2.contains("sqlite")) {
            driver = DriverNamePool.DRIVER_SQLLITE3;
        } else if (nameContainsProductInfo2.contains("sqlserver") || nameContainsProductInfo2.contains("microsoft")) {
            driver = DriverNamePool.DRIVER_SQLSERVER;
        } else if (nameContainsProductInfo2.contains("hive2")) {
            driver = DriverNamePool.DRIVER_HIVE2;
        } else if (nameContainsProductInfo2.contains("hive")) {
            driver = DriverNamePool.DRIVER_HIVE;
        } else if (nameContainsProductInfo2.contains(JaasCredentialsValidator.DEFAULT_APPNAME)) {
            driver = DriverNamePool.DRIVER_H2;
        } else if (nameContainsProductInfo2.contains("derby")) {
            driver = DriverNamePool.DRIVER_DERBY;
        } else if (nameContainsProductInfo2.contains("hsqldb")) {
            driver = DriverNamePool.DRIVER_HSQLDB;
        } else if (nameContainsProductInfo2.contains("dm")) {
            driver = DriverNamePool.DRIVER_DM7;
        } else if (nameContainsProductInfo2.contains("kingbase8")) {
            driver = DriverNamePool.DRIVER_KINGBASE8;
        } else if (nameContainsProductInfo2.contains("ignite")) {
            driver = DriverNamePool.DRIVER_IGNITE_THIN;
        } else if (nameContainsProductInfo2.contains("clickhouse")) {
            driver = DriverNamePool.DRIVER_CLICK_HOUSE;
        } else if (nameContainsProductInfo2.contains("highgo")) {
            driver = DriverNamePool.DRIVER_HIGHGO;
        } else if (nameContainsProductInfo2.contains("db2")) {
            driver = DriverNamePool.DRIVER_DB2;
        } else if (nameContainsProductInfo2.contains("xugu")) {
            driver = DriverNamePool.DRIVER_XUGU;
        } else if (nameContainsProductInfo2.contains("phoenix")) {
            driver = DriverNamePool.DRIVER_PHOENIX;
        } else if (nameContainsProductInfo2.contains("zenith")) {
            driver = DriverNamePool.DRIVER_GAUSS;
        } else if (nameContainsProductInfo2.contains("gbase")) {
            driver = DriverNamePool.DRIVER_GBASE;
        } else if (nameContainsProductInfo2.contains("oscar")) {
            driver = DriverNamePool.DRIVER_OSCAR;
        } else if (nameContainsProductInfo2.contains("sybase")) {
            driver = DriverNamePool.DRIVER_SYBASE;
        } else if (nameContainsProductInfo2.contains("mariadb")) {
            driver = DriverNamePool.DRIVER_MARIADB;
        }
        return driver;
    }

    public static Dialect getDialect(DataSource ds) {
        Dialect dialect = DIALECT_POOL.get(ds);
        if (null == dialect) {
            synchronized (ds) {
                dialect = DIALECT_POOL.computeIfAbsent(ds, DialectFactory::newDialect);
            }
        }
        return dialect;
    }

    public static Dialect newDialect(DataSource ds) {
        return newDialect(DriverUtil.identifyDriver(ds));
    }

    public static Dialect newDialect(Connection conn) {
        return newDialect(DriverUtil.identifyDriver(conn));
    }
}
