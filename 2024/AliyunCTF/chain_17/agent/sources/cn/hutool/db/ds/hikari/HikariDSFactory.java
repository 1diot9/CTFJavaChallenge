package cn.hutool.db.ds.hikari;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/hikari/HikariDSFactory.class */
public class HikariDSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = -8834744983614749401L;
    public static final String DS_NAME = "HikariCP";

    public HikariDSFactory() {
        this(null);
    }

    public HikariDSFactory(Setting setting) {
        super(DS_NAME, HikariDataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        Props connProps = new Props();
        for (String key : KEY_CONN_PROPS) {
            String connValue = poolSetting.getAndRemoveStr(key);
            if (StrUtil.isNotBlank(connValue)) {
                connProps.setProperty(key, connValue);
            }
        }
        Props config = new Props();
        config.putAll(poolSetting);
        config.put("jdbcUrl", jdbcUrl);
        if (null != driver) {
            config.put("driverClassName", driver);
        }
        if (null != user) {
            config.put("username", user);
        }
        if (null != pass) {
            config.put("password", pass);
        }
        HikariConfig hikariConfig = new HikariConfig(config);
        hikariConfig.setDataSourceProperties(connProps);
        return new HikariDataSource(hikariConfig);
    }
}
