package cn.hutool.db.ds.simple;

import ch.qos.logback.classic.ClassicConstants;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.dialect.DriverUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/ds/simple/SimpleDataSource.class */
public class SimpleDataSource extends AbstractDataSource {
    public static final String DEFAULT_DB_CONFIG_PATH = "config/db.setting";
    private String driver;
    private String url;
    private String user;
    private String pass;
    private Properties connProps;

    public static synchronized SimpleDataSource getDataSource(String group) {
        return new SimpleDataSource(group);
    }

    public static synchronized SimpleDataSource getDataSource() {
        return new SimpleDataSource();
    }

    public SimpleDataSource() {
        this(null);
    }

    public SimpleDataSource(String group) {
        this(null, group);
    }

    public SimpleDataSource(Setting setting, String group) {
        Setting config = (null == setting ? new Setting("config/db.setting") : setting).getSetting(group);
        if (MapUtil.isEmpty(config)) {
            throw new DbRuntimeException("No DataSource config for group: [{}]", group);
        }
        init(config.getAndRemoveStr(DSFactory.KEY_ALIAS_URL), config.getAndRemoveStr(DSFactory.KEY_ALIAS_USER), config.getAndRemoveStr(DSFactory.KEY_ALIAS_PASSWORD), config.getAndRemoveStr(DSFactory.KEY_ALIAS_DRIVER));
        this.connProps = config.getProps("");
    }

    public SimpleDataSource(String url, String user, String pass) {
        init(url, user, pass);
    }

    public SimpleDataSource(String url, String user, String pass, String driver) {
        init(url, user, pass, driver);
    }

    public void init(String url, String user, String pass) {
        init(url, user, pass, null);
    }

    public void init(String url, String user, String pass, String driver) {
        this.driver = StrUtil.isNotBlank(driver) ? driver : DriverUtil.identifyDriver(url);
        try {
            Class.forName(this.driver);
            this.url = url;
            this.user = user;
            this.pass = pass;
        } catch (ClassNotFoundException e) {
            throw new DbRuntimeException(e, "Get jdbc driver [{}] error!", driver);
        }
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Properties getConnProps() {
        return this.connProps;
    }

    public void setConnProps(Properties connProps) {
        this.connProps = connProps;
    }

    public void addConnProps(String key, String value) {
        if (null == this.connProps) {
            this.connProps = new Properties();
        }
        this.connProps.setProperty(key, value);
    }

    @Override // javax.sql.DataSource
    public Connection getConnection() throws SQLException {
        Props info = new Props();
        if (this.user != null) {
            info.setProperty(ClassicConstants.USER_MDC_KEY, this.user);
        }
        if (this.pass != null) {
            info.setProperty("password", this.pass);
        }
        Properties connProps = this.connProps;
        if (MapUtil.isNotEmpty(connProps)) {
            info.putAll(connProps);
        }
        return DriverManager.getConnection(this.url, info);
    }

    @Override // javax.sql.DataSource
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(this.url, username, password);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}
