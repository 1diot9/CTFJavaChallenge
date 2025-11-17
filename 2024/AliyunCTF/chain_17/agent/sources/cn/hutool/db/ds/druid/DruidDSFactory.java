package cn.hutool.db.ds.druid;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/druid/DruidDSFactory.class */
public class DruidDSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = 4680621702534433222L;
    public static final String DS_NAME = "Druid";

    public DruidDSFactory() {
        this(null);
    }

    public DruidDSFactory(Setting setting) {
        super(DS_NAME, DruidDataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        DruidDataSource ds = new DruidDataSource();
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
        Props druidProps = new Props();
        poolSetting.forEach((key2, value) -> {
            druidProps.put(StrUtil.addPrefixIfNot(key2, "druid."), value);
        });
        ds.configFromPropety(druidProps);
        if (druidProps.containsKey("druid.connectionErrorRetryAttempts")) {
            ds.setConnectionErrorRetryAttempts(druidProps.getInt("druid.connectionErrorRetryAttempts").intValue());
        }
        if (druidProps.containsKey("druid.timeBetweenConnectErrorMillis")) {
            ds.setTimeBetweenConnectErrorMillis(druidProps.getInt("druid.timeBetweenConnectErrorMillis").intValue());
        }
        if (druidProps.containsKey("druid.breakAfterAcquireFailure")) {
            ds.setBreakAfterAcquireFailure(druidProps.getBool("druid.breakAfterAcquireFailure").booleanValue());
        }
        if (null == ds.getValidationQuery()) {
            ds.setTestOnBorrow(false);
            ds.setTestOnReturn(false);
            ds.setTestWhileIdle(false);
        }
        return ds;
    }
}
