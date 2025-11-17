package cn.hutool.db.ds.c3p0;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.ds.AbstractDSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/c3p0/C3p0DSFactory.class */
public class C3p0DSFactory extends AbstractDSFactory {
    private static final long serialVersionUID = -6090788225842047281L;
    public static final String DS_NAME = "C3P0";

    public C3p0DSFactory() {
        this(null);
    }

    public C3p0DSFactory(Setting setting) {
        super(DS_NAME, ComboPooledDataSource.class, setting);
    }

    @Override // cn.hutool.db.ds.AbstractDSFactory
    protected DataSource createDataSource(String jdbcUrl, String driver, String user, String pass, Setting poolSetting) {
        ComboPooledDataSource ds = new ComboPooledDataSource();
        Props connProps = new Props();
        for (String key : KEY_CONN_PROPS) {
            String connValue = poolSetting.getAndRemoveStr(key);
            if (StrUtil.isNotBlank(connValue)) {
                connProps.setProperty(key, connValue);
            }
        }
        if (MapUtil.isNotEmpty(connProps)) {
            ds.setProperties(connProps);
        }
        ds.setJdbcUrl(jdbcUrl);
        try {
            ds.setDriverClass(driver);
            ds.setUser(user);
            ds.setPassword(pass);
            poolSetting.toBean((Setting) ds);
            return ds;
        } catch (PropertyVetoException e) {
            throw new DbRuntimeException((Throwable) e);
        }
    }
}
