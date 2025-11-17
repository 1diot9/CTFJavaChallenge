package cn.hutool.db.ds.bee;

import cn.beecp.BeeDataSource;
import cn.beecp.BeeDataSourceConfig;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/bee/BeeDSFactory.class */
public class BeeDSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = 1;
    public static final String DS_NAME = "BeeCP";

    public BeeDSFactory() {
        this(null);
    }

    public BeeDSFactory(Setting setting) {
        super(DS_NAME, BeeDataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        BeeDataSourceConfig beeConfig = new BeeDataSourceConfig(driver, jdbcUrl, user, pass);
        poolSetting.toBean((Setting) beeConfig);
        for (String key : KEY_CONN_PROPS) {
            String connValue = poolSetting.getAndRemoveStr(key);
            if (StrUtil.isNotBlank(connValue)) {
                beeConfig.addConnectProperty(key, connValue);
            }
        }
        return new BeeDataSource(beeConfig);
    }
}
