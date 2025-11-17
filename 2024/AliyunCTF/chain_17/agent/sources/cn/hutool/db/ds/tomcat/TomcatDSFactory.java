package cn.hutool.db.ds.tomcat;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/tomcat/TomcatDSFactory.class */
public class TomcatDSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = 4925514193275150156L;
    public static final String DS_NAME = "Tomcat-Jdbc-Pool";

    public TomcatDSFactory() {
        this(null);
    }

    public TomcatDSFactory(Setting setting) {
        super(DS_NAME, DataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected javax.sql.DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        PoolProperties poolProps = new PoolProperties();
        poolProps.setUrl(jdbcUrl);
        poolProps.setDriverClassName(driver);
        poolProps.setUsername(user);
        poolProps.setPassword(pass);
        Props connProps = new Props();
        for (String key : KEY_CONN_PROPS) {
            String connValue = poolSetting.getAndRemoveStr(key);
            if (StrUtil.isNotBlank(connValue)) {
                connProps.setProperty(key, connValue);
            }
        }
        poolProps.setDbProperties(connProps);
        poolSetting.toBean((Setting) poolProps);
        return new DataSource(poolProps);
    }
}
