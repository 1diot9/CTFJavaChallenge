package cn.hutool.db.ds.pooled;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/pooled/PooledDSFactory.class */
public class PooledDSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = 8093886210895248277L;
    public static final String DS_NAME = "Hutool-Pooled-DataSource";

    public PooledDSFactory() {
        this(null);
    }

    public PooledDSFactory(Setting setting) {
        super(DS_NAME, PooledDataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        DbConfig dbConfig = new DbConfig();
        dbConfig.setUrl(jdbcUrl);
        dbConfig.setDriver(driver);
        dbConfig.setUser(user);
        dbConfig.setPass(pass);
        dbConfig.setInitialSize(poolSetting.getInt((Setting) "initialSize", (Integer) 0).intValue());
        dbConfig.setMinIdle(poolSetting.getInt((Setting) "minIdle", (Integer) 0).intValue());
        dbConfig.setMaxActive(poolSetting.getInt((Setting) "maxActive", (Integer) 8).intValue());
        dbConfig.setMaxWait(poolSetting.getLong((Setting) "maxWait", (Long) 6000L).longValue());
        for (String key : KEY_CONN_PROPS) {
            String connValue = poolSetting.get((Object) key);
            if (StrUtil.isNotBlank(connValue)) {
                dbConfig.addConnProps(key, connValue);
            }
        }
        return new PooledDataSource(dbConfig);
    }
}
