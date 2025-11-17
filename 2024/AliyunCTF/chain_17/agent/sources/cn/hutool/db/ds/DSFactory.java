package cn.hutool.db.ds;

import ch.qos.logback.classic.ClassicConstants;
import cn.hutool.db.ds.bee.BeeDSFactory;
import cn.hutool.db.ds.c3p0.C3p0DSFactory;
import cn.hutool.db.ds.dbcp.DbcpDSFactory;
import cn.hutool.db.ds.druid.DruidDSFactory;
import cn.hutool.db.ds.hikari.HikariDSFactory;
import cn.hutool.db.ds.pooled.PooledDSFactory;
import cn.hutool.db.ds.tomcat.TomcatDSFactory;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;
import java.io.Closeable;
import java.io.Serializable;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/DSFactory.class */
public abstract class DSFactory implements Closeable, Serializable {
    private static final long serialVersionUID = -8789780234095234765L;
    private static final Log log = LogFactory.get();
    public static final String[] KEY_CONN_PROPS = {"remarks", "useInformationSchema"};
    public static final String[] KEY_ALIAS_URL = {"url", "jdbcUrl"};
    public static final String[] KEY_ALIAS_DRIVER = {"driver", "driverClassName"};
    public static final String[] KEY_ALIAS_USER = {ClassicConstants.USER_MDC_KEY, "username"};
    public static final String[] KEY_ALIAS_PASSWORD = {"pass", "password"};
    protected final String dataSourceName;

    public abstract DataSource getDataSource(String str);

    public abstract void close(String str);

    public abstract void destroy();

    public DSFactory(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public DataSource getDataSource() {
        return getDataSource("");
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        close("");
    }

    public static DataSource get() {
        return get(null);
    }

    public static DataSource get(String group) {
        return GlobalDSFactory.get().getDataSource(group);
    }

    public static DSFactory setCurrentDSFactory(DSFactory dsFactory) {
        return GlobalDSFactory.set(dsFactory);
    }

    public static DSFactory create(Setting setting) {
        DSFactory dsFactory = doCreate(setting);
        log.debug("Use [{}] DataSource As Default", dsFactory.dataSourceName);
        return dsFactory;
    }

    private static DSFactory doCreate(Setting setting) {
        try {
            return new HikariDSFactory(setting);
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            try {
                return new DruidDSFactory(setting);
            } catch (NoClassDefFoundError | NoSuchMethodError e2) {
                try {
                    return new TomcatDSFactory(setting);
                } catch (NoClassDefFoundError | NoSuchMethodError e3) {
                    try {
                        return new BeeDSFactory(setting);
                    } catch (NoClassDefFoundError | NoSuchMethodError e4) {
                        try {
                            return new DbcpDSFactory(setting);
                        } catch (NoClassDefFoundError | NoSuchMethodError e5) {
                            try {
                                return new C3p0DSFactory(setting);
                            } catch (NoClassDefFoundError | NoSuchMethodError e6) {
                                return new PooledDSFactory(setting);
                            }
                        }
                    }
                }
            }
        }
    }
}
