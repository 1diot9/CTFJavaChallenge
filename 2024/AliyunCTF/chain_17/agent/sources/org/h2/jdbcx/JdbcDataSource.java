package org.h2.jdbcx;

import ch.qos.logback.classic.ClassicConstants;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import org.h2.jdbc.JdbcConnection;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbcx/JdbcDataSource.class */
public final class JdbcDataSource extends TraceObject implements XADataSource, DataSource, ConnectionPoolDataSource, Serializable, Referenceable, JdbcDataSourceBackwardsCompat {
    private static final long serialVersionUID = 1288136338451857771L;
    private transient JdbcDataSourceFactory factory;
    private transient PrintWriter logWriter;
    private int loginTimeout;
    private String userName = "";
    private char[] passwordChars = new char[0];
    private String url = "";
    private String description;

    public JdbcDataSource() {
        initFactory();
        setTrace(this.factory.getTrace(), 12, getNextId(12));
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        initFactory();
        objectInputStream.defaultReadObject();
    }

    private void initFactory() {
        this.factory = new JdbcDataSourceFactory();
    }

    @Override // javax.sql.CommonDataSource
    public int getLoginTimeout() {
        debugCodeCall("getLoginTimeout");
        return this.loginTimeout;
    }

    @Override // javax.sql.CommonDataSource
    public void setLoginTimeout(int i) {
        debugCodeCall("setLoginTimeout", i);
        this.loginTimeout = i;
    }

    @Override // javax.sql.CommonDataSource
    public PrintWriter getLogWriter() {
        debugCodeCall("getLogWriter");
        return this.logWriter;
    }

    @Override // javax.sql.CommonDataSource
    public void setLogWriter(PrintWriter printWriter) {
        debugCodeCall("setLogWriter(out)");
        this.logWriter = printWriter;
    }

    @Override // javax.sql.DataSource
    public Connection getConnection() throws SQLException {
        debugCodeCall("getConnection");
        return new JdbcConnection(this.url, null, this.userName, StringUtils.cloneCharArray(this.passwordChars), false);
    }

    @Override // javax.sql.DataSource
    public Connection getConnection(String str, String str2) throws SQLException {
        if (isDebugEnabled()) {
            debugCode("getConnection(" + quote(str) + ", \"\")");
        }
        return new JdbcConnection(this.url, null, str, str2, false);
    }

    public String getURL() {
        debugCodeCall("getURL");
        return this.url;
    }

    public void setURL(String str) {
        debugCodeCall("setURL", str);
        this.url = str;
    }

    public String getUrl() {
        debugCodeCall("getUrl");
        return this.url;
    }

    public void setUrl(String str) {
        debugCodeCall("setUrl", str);
        this.url = str;
    }

    public void setPassword(String str) {
        debugCodeCall("setPassword", "");
        this.passwordChars = str == null ? null : str.toCharArray();
    }

    public void setPasswordChars(char[] cArr) {
        if (isDebugEnabled()) {
            debugCode("setPasswordChars(new char[0])");
        }
        this.passwordChars = cArr;
    }

    private static String convertToString(char[] cArr) {
        if (cArr == null) {
            return null;
        }
        return new String(cArr);
    }

    public String getPassword() {
        debugCodeCall("getPassword");
        return convertToString(this.passwordChars);
    }

    public String getUser() {
        debugCodeCall("getUser");
        return this.userName;
    }

    public void setUser(String str) {
        debugCodeCall("setUser", str);
        this.userName = str;
    }

    public String getDescription() {
        debugCodeCall("getDescription");
        return this.description;
    }

    public void setDescription(String str) {
        debugCodeCall("getDescription", str);
        this.description = str;
    }

    public Reference getReference() {
        debugCodeCall("getReference");
        Reference reference = new Reference(getClass().getName(), JdbcDataSourceFactory.class.getName(), (String) null);
        reference.add(new StringRefAddr("url", this.url));
        reference.add(new StringRefAddr(ClassicConstants.USER_MDC_KEY, this.userName));
        reference.add(new StringRefAddr("password", convertToString(this.passwordChars)));
        reference.add(new StringRefAddr("loginTimeout", Integer.toString(this.loginTimeout)));
        reference.add(new StringRefAddr("description", this.description));
        return reference;
    }

    public XAConnection getXAConnection() throws SQLException {
        debugCodeCall("getXAConnection");
        return new JdbcXAConnection(this.factory, getNextId(13), new JdbcConnection(this.url, null, this.userName, StringUtils.cloneCharArray(this.passwordChars), false));
    }

    public XAConnection getXAConnection(String str, String str2) throws SQLException {
        if (isDebugEnabled()) {
            debugCode("getXAConnection(" + quote(str) + ", \"\")");
        }
        return new JdbcXAConnection(this.factory, getNextId(13), new JdbcConnection(this.url, null, str, str2, false));
    }

    @Override // javax.sql.ConnectionPoolDataSource
    public PooledConnection getPooledConnection() throws SQLException {
        debugCodeCall("getPooledConnection");
        return getXAConnection();
    }

    @Override // javax.sql.ConnectionPoolDataSource
    public PooledConnection getPooledConnection(String str, String str2) throws SQLException {
        if (isDebugEnabled()) {
            debugCode("getPooledConnection(" + quote(str) + ", \"\")");
        }
        return getXAConnection(str, str2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.sql.Wrapper
    public <T> T unwrap(Class<T> cls) throws SQLException {
        try {
            if (isWrapperFor(cls)) {
                return this;
            }
            throw DbException.getInvalidValueException("iface", cls);
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.Wrapper
    public boolean isWrapperFor(Class<?> cls) throws SQLException {
        return cls != null && cls.isAssignableFrom(getClass());
    }

    @Override // javax.sql.CommonDataSource
    public Logger getParentLogger() {
        return null;
    }

    public String toString() {
        return getTraceObjectName() + ": url=" + this.url + " user=" + this.userName;
    }
}
