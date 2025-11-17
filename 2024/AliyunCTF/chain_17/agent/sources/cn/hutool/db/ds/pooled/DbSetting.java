package cn.hutool.db.ds.pooled;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.dialect.DriverUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/pooled/DbSetting.class */
public class DbSetting {
    public static final String DEFAULT_DB_CONFIG_PATH = "config/db.setting";
    private final Setting setting;

    public DbSetting() {
        this(null);
    }

    public DbSetting(Setting setting) {
        if (null == setting) {
            this.setting = new Setting("config/db.setting");
        } else {
            this.setting = setting;
        }
    }

    public DbConfig getDbConfig(String group) {
        Setting config = this.setting.getSetting(group);
        if (MapUtil.isEmpty(config)) {
            throw new DbRuntimeException("No Hutool pool config for group: [{}]", group);
        }
        DbConfig dbConfig = new DbConfig();
        String url = config.getAndRemoveStr(DSFactory.KEY_ALIAS_URL);
        if (StrUtil.isBlank(url)) {
            throw new DbRuntimeException("No JDBC URL for group: [{}]", group);
        }
        dbConfig.setUrl(url);
        String driver = config.getAndRemoveStr(DSFactory.KEY_ALIAS_DRIVER);
        dbConfig.setDriver(StrUtil.isNotBlank(driver) ? driver : DriverUtil.identifyDriver(url));
        dbConfig.setUser(config.getAndRemoveStr(DSFactory.KEY_ALIAS_USER));
        dbConfig.setPass(config.getAndRemoveStr(DSFactory.KEY_ALIAS_PASSWORD));
        dbConfig.setInitialSize(this.setting.getInt("initialSize", group, 0).intValue());
        dbConfig.setMinIdle(this.setting.getInt("minIdle", group, 0).intValue());
        dbConfig.setMaxActive(this.setting.getInt("maxActive", group, 8).intValue());
        dbConfig.setMaxWait(this.setting.getLong("maxWait", group, 6000L).longValue());
        for (String key : DSFactory.KEY_CONN_PROPS) {
            String connValue = config.get((Object) key);
            if (StrUtil.isNotBlank(connValue)) {
                dbConfig.addConnProps(key, connValue);
            }
        }
        return dbConfig;
    }
}
