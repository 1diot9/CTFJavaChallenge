package org.apache.catalina.valves;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.encoder.JsonEncoder;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;
import org.apache.catalina.AccessLog;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.Constants;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.web.servlet.tags.BindTag;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/JDBCAccessLogValve.class */
public final class JDBCAccessLogValve extends ValveBase implements AccessLog {
    boolean useLongContentLength;
    String connectionName;
    String connectionPassword;
    Driver driver;
    private String driverName;
    private String connectionURL;
    private String tableName;
    private String remoteHostField;
    private String userField;
    private String timestampField;
    private String virtualHostField;
    private String methodField;
    private String queryField;
    private String statusField;
    private String bytesField;
    private String refererField;
    private String userAgentField;
    private String pattern;
    private boolean resolveHosts;
    private Connection conn;
    private PreparedStatement ps;
    private long currentTimeMillis;
    boolean requestAttributesEnabled;

    public JDBCAccessLogValve() {
        super(true);
        this.useLongContentLength = false;
        this.connectionName = null;
        this.connectionPassword = null;
        this.driver = null;
        this.requestAttributesEnabled = true;
        this.driverName = null;
        this.connectionURL = null;
        this.tableName = "access";
        this.remoteHostField = "remoteHost";
        this.userField = "userName";
        this.timestampField = JsonEncoder.TIMESTAMP_ATTR_NAME;
        this.virtualHostField = "virtualHost";
        this.methodField = "method";
        this.queryField = "query";
        this.statusField = BindTag.STATUS_VARIABLE_NAME;
        this.bytesField = "bytes";
        this.refererField = "referer";
        this.userAgentField = "userAgent";
        this.pattern = Constants.AccessLog.COMMON_ALIAS;
        this.resolveHosts = false;
        this.conn = null;
        this.ps = null;
        this.currentTimeMillis = new Date().getTime();
    }

    @Override // org.apache.catalina.AccessLog
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    @Override // org.apache.catalina.AccessLog
    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public String getConnectionName() {
        return this.connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getConnectionPassword() {
        return this.connectionPassword;
    }

    public void setConnectionPassword(String connectionPassword) {
        this.connectionPassword = connectionPassword;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRemoteHostField(String remoteHostField) {
        this.remoteHostField = remoteHostField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }

    public void setTimestampField(String timestampField) {
        this.timestampField = timestampField;
    }

    public void setVirtualHostField(String virtualHostField) {
        this.virtualHostField = virtualHostField;
    }

    public void setMethodField(String methodField) {
        this.methodField = methodField;
    }

    public void setQueryField(String queryField) {
        this.queryField = queryField;
    }

    public void setStatusField(String statusField) {
        this.statusField = statusField;
    }

    public void setBytesField(String bytesField) {
        this.bytesField = bytesField;
    }

    public void setRefererField(String refererField) {
        this.refererField = refererField;
    }

    public void setUserAgentField(String userAgentField) {
        this.userAgentField = userAgentField;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setResolveHosts(String resolveHosts) {
        this.resolveHosts = Boolean.parseBoolean(resolveHosts);
    }

    public boolean getUseLongContentLength() {
        return this.useLongContentLength;
    }

    public void setUseLongContentLength(boolean useLongContentLength) {
        this.useLongContentLength = useLongContentLength;
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        getNext().invoke(request, response);
    }

    @Override // org.apache.catalina.AccessLog
    public void log(Request request, Response response, long time) {
        String remoteHost;
        if (!getState().isAvailable()) {
            return;
        }
        if (this.resolveHosts) {
            if (this.requestAttributesEnabled) {
                Object host = request.getAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE);
                if (host == null) {
                    remoteHost = request.getRemoteHost();
                } else {
                    remoteHost = (String) host;
                }
            } else {
                remoteHost = request.getRemoteHost();
            }
        } else if (this.requestAttributesEnabled) {
            Object addr = request.getAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE);
            if (addr == null) {
                remoteHost = request.getRemoteAddr();
            } else {
                remoteHost = (String) addr;
            }
        } else {
            remoteHost = request.getRemoteAddr();
        }
        String user = request.getRemoteUser();
        String query = request.getRequestURI();
        long bytes = response.getBytesWritten(true);
        if (bytes < 0) {
            bytes = 0;
        }
        int status = response.getStatus();
        String virtualHost = "";
        String method = "";
        String referer = "";
        String userAgent = "";
        String logPattern = this.pattern;
        if (logPattern.equals(Constants.AccessLog.COMBINED_ALIAS)) {
            virtualHost = request.getServerName();
            method = request.getMethod();
            referer = request.getHeader("referer");
            userAgent = request.getHeader("user-agent");
        }
        synchronized (this) {
            for (int numberOfTries = 2; numberOfTries > 0; numberOfTries--) {
                try {
                    open();
                    this.ps.setString(1, remoteHost);
                    this.ps.setString(2, user);
                    this.ps.setTimestamp(3, new Timestamp(getCurrentTimeMillis()));
                    this.ps.setString(4, query);
                    this.ps.setInt(5, status);
                    if (this.useLongContentLength) {
                        this.ps.setLong(6, bytes);
                    } else {
                        if (bytes > 2147483647L) {
                            bytes = -1;
                        }
                        this.ps.setInt(6, (int) bytes);
                    }
                    if (logPattern.equals(Constants.AccessLog.COMBINED_ALIAS)) {
                        this.ps.setString(7, virtualHost);
                        this.ps.setString(8, method);
                        this.ps.setString(9, referer);
                        this.ps.setString(10, userAgent);
                    }
                    this.ps.executeUpdate();
                    return;
                } catch (SQLException e) {
                    this.container.getLogger().error(sm.getString("jdbcAccessLogValve.exception"), e);
                    if (this.conn != null) {
                        close();
                    }
                }
            }
        }
    }

    protected void open() throws SQLException {
        if (this.conn != null) {
            return;
        }
        if (this.driver == null) {
            try {
                Class<?> clazz = Class.forName(this.driverName);
                this.driver = (Driver) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                throw new SQLException(e.getMessage(), e);
            }
        }
        Properties props = new Properties();
        if (this.connectionName != null) {
            props.put(ClassicConstants.USER_MDC_KEY, this.connectionName);
        }
        if (this.connectionPassword != null) {
            props.put("password", this.connectionPassword);
        }
        this.conn = this.driver.connect(this.connectionURL, props);
        this.conn.setAutoCommit(true);
        String logPattern = this.pattern;
        if (logPattern.equals(Constants.AccessLog.COMMON_ALIAS)) {
            this.ps = this.conn.prepareStatement("INSERT INTO " + this.tableName + " (" + this.remoteHostField + ", " + this.userField + ", " + this.timestampField + ", " + this.queryField + ", " + this.statusField + ", " + this.bytesField + ") VALUES(?, ?, ?, ?, ?, ?)");
        } else if (logPattern.equals(Constants.AccessLog.COMBINED_ALIAS)) {
            this.ps = this.conn.prepareStatement("INSERT INTO " + this.tableName + " (" + this.remoteHostField + ", " + this.userField + ", " + this.timestampField + ", " + this.queryField + ", " + this.statusField + ", " + this.bytesField + ", " + this.virtualHostField + ", " + this.methodField + ", " + this.refererField + ", " + this.userAgentField + ") VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
    }

    protected void close() {
        if (this.conn == null) {
            return;
        }
        try {
            this.ps.close();
        } catch (Throwable f) {
            ExceptionUtils.handleThrowable(f);
        }
        this.ps = null;
        try {
            this.conn.close();
        } catch (SQLException e) {
            this.container.getLogger().error(sm.getString("jdbcAccessLogValve.close"), e);
        } finally {
            this.conn = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        try {
            open();
            super.startInternal();
        } catch (SQLException e) {
            throw new LifecycleException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        close();
    }

    public long getCurrentTimeMillis() {
        long systime = System.currentTimeMillis();
        if (systime - this.currentTimeMillis > 1000) {
            this.currentTimeMillis = new Date(systime).getTime();
        }
        return this.currentTimeMillis;
    }
}
