package cn.hutool.db.ds.dbcp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/dbcp/DbcpDSFactory.class */
public class DbcpDSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = -9133501414334104548L;
    public static final String DS_NAME = "commons-dbcp2";

    public DbcpDSFactory() {
        this(null);
    }

    public DbcpDSFactory(Setting setting) {
        super(DS_NAME, BasicDataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(jdbcUrl);
        ds.setDriverClassName(driver);
        ds.setUsername(user);
        ds.setPassword(pass);
        for (String key : KEY_CONN_PROPS) {
            String connValue = poolSetting.getAndRemoveStr(key);
            if (StrUtil.isNotBlank(connValue)) {
                ds.addConnectionProperty(key, connValue);
            }
        }
        poolSetting.toBean((Setting) ds);
        return ds;
    }
}
