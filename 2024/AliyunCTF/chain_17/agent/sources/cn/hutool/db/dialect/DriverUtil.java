package cn.hutool.db.dialect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.ds.DataSourceWrapper;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/dialect/DriverUtil.class */
public class DriverUtil {
    public static String identifyDriver(String nameContainsProductInfo) {
        return DialectFactory.identifyDriver(nameContainsProductInfo);
    }

    public static String identifyDriver(DataSource ds) {
        if (ds instanceof DataSourceWrapper) {
            String driver = ((DataSourceWrapper) ds).getDriver();
            if (StrUtil.isNotBlank(driver)) {
                return driver;
            }
        }
        Connection conn = null;
        try {
            try {
                conn = ds.getConnection();
                String driver2 = identifyDriver(conn);
                DbUtil.close(conn);
                return driver2;
            } catch (NullPointerException e) {
                throw new DbRuntimeException("Unexpected NullPointException, maybe [jdbcUrl] or [url] is empty!", e);
            } catch (SQLException e2) {
                throw new DbRuntimeException("Get Connection error !", e2);
            }
        } catch (Throwable th) {
            DbUtil.close(conn);
            throw th;
        }
    }

    public static String identifyDriver(Connection conn) throws DbRuntimeException {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            String driver = identifyDriver(meta.getDatabaseProductName());
            if (StrUtil.isBlank(driver)) {
                driver = identifyDriver(meta.getDriverName());
            }
            return driver;
        } catch (SQLException e) {
            throw new DbRuntimeException("Identify driver error!", e);
        }
    }
}
