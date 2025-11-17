package cn.hutool.db.ds;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.DbUtil;
import cn.hutool.db.GlobalDbConfig;
import cn.hutool.db.dialect.DriverUtil;
import cn.hutool.setting.Setting;
import java.util.Collection;
import java.util.Map;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/AbstractDSFactory.class */
public abstract class AbstractDSFactory extends DSFactory {
    private static final long serialVersionUID = -6407302276272379881L;
    private final Setting setting;
    private final Map<String, DataSourceWrapper> dsMap;

    protected abstract DataSource createDataSource(String str, String str2, String str3, String str4, Setting setting);

    public AbstractDSFactory(String dataSourceName, Class<? extends DataSource> dataSourceClass, Setting setting) {
        super(dataSourceName);
        Assert.notNull(dataSourceClass);
        setting = null == setting ? GlobalDbConfig.createDbSetting() : setting;
        DbUtil.setShowSqlGlobal(setting);
        this.setting = setting;
        this.dsMap = new SafeConcurrentHashMap();
    }

    public Setting getSetting() {
        return this.setting;
    }

    @Override // cn.hutool.db.ds.DSFactory
    public synchronized DataSource getDataSource(String group) {
        if (group == null) {
            group = "";
        }
        DataSourceWrapper existedDataSource = this.dsMap.get(group);
        if (existedDataSource != null) {
            return existedDataSource;
        }
        DataSourceWrapper ds = createDataSource(group);
        this.dsMap.put(group, ds);
        return ds;
    }

    private DataSourceWrapper createDataSource(String group) {
        if (group == null) {
            group = "";
        }
        Setting config = this.setting.getSetting(group);
        if (MapUtil.isEmpty(config)) {
            throw new DbRuntimeException("No config for group: [{}]", group);
        }
        String url = config.getAndRemoveStr(KEY_ALIAS_URL);
        if (StrUtil.isBlank(url)) {
            throw new DbRuntimeException("No JDBC URL for group: [{}]", group);
        }
        DbUtil.removeShowSqlParams(config);
        String driver = config.getAndRemoveStr(KEY_ALIAS_DRIVER);
        if (StrUtil.isBlank(driver)) {
            driver = DriverUtil.identifyDriver(url);
        }
        String user = config.getAndRemoveStr(KEY_ALIAS_USER);
        String pass = config.getAndRemoveStr(KEY_ALIAS_PASSWORD);
        return DataSourceWrapper.wrap(createDataSource(url, driver, user, pass, config), driver);
    }

    @Override // cn.hutool.db.ds.DSFactory
    public void close(String group) {
        if (group == null) {
            group = "";
        }
        DataSourceWrapper ds = this.dsMap.get(group);
        if (ds != null) {
            ds.close();
            this.dsMap.remove(group);
        }
    }

    @Override // cn.hutool.db.ds.DSFactory
    public void destroy() {
        if (MapUtil.isNotEmpty(this.dsMap)) {
            Collection<DataSourceWrapper> values = this.dsMap.values();
            for (DataSourceWrapper ds : values) {
                ds.close();
            }
            this.dsMap.clear();
        }
    }

    public int hashCode() {
        int result = (31 * 1) + (this.dataSourceName == null ? 0 : this.dataSourceName.hashCode());
        return (31 * result) + (this.setting == null ? 0 : this.setting.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractDSFactory other = (AbstractDSFactory) obj;
        if (this.dataSourceName == null) {
            if (other.dataSourceName != null) {
                return false;
            }
        } else if (!this.dataSourceName.equals(other.dataSourceName)) {
            return false;
        }
        if (this.setting == null) {
            return other.setting == null;
        }
        return this.setting.equals(other.setting);
    }
}
