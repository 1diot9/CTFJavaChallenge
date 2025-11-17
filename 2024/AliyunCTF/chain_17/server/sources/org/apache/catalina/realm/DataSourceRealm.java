package org.apache.catalina.realm;

import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.Context;
import javax.sql.DataSource;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.realm.RealmBase;
import org.apache.naming.ContextBindings;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/DataSourceRealm.class */
public class DataSourceRealm extends RealmBase {
    private String preparedRoles = null;
    private String preparedCredentials = null;
    protected String dataSourceName = null;
    protected boolean localDataSource = false;
    protected String roleNameCol = null;
    protected String userCredCol = null;
    protected String userNameCol = null;
    protected String userRoleTable = null;
    protected String userTable = null;
    private volatile boolean connectionSuccess = true;

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public boolean getLocalDataSource() {
        return this.localDataSource;
    }

    public void setLocalDataSource(boolean localDataSource) {
        this.localDataSource = localDataSource;
    }

    public String getRoleNameCol() {
        return this.roleNameCol;
    }

    public void setRoleNameCol(String roleNameCol) {
        this.roleNameCol = roleNameCol;
    }

    public String getUserCredCol() {
        return this.userCredCol;
    }

    public void setUserCredCol(String userCredCol) {
        this.userCredCol = userCredCol;
    }

    public String getUserNameCol() {
        return this.userNameCol;
    }

    public void setUserNameCol(String userNameCol) {
        this.userNameCol = userNameCol;
    }

    public String getUserRoleTable() {
        return this.userRoleTable;
    }

    public void setUserRoleTable(String userRoleTable) {
        this.userRoleTable = userRoleTable;
    }

    public String getUserTable() {
        return this.userTable;
    }

    public void setUserTable(String userTable) {
        this.userTable = userTable;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public Principal authenticate(String username, String credentials) {
        Connection dbConnection;
        if (username == null || credentials == null || (dbConnection = open()) == null) {
            return null;
        }
        try {
            Principal authenticate = authenticate(dbConnection, username, credentials);
            close(dbConnection);
            return authenticate;
        } catch (Throwable th) {
            close(dbConnection);
            throw th;
        }
    }

    @Override // org.apache.catalina.Realm
    public boolean isAvailable() {
        return this.connectionSuccess;
    }

    protected Principal authenticate(Connection dbConnection, String username, String credentials) {
        if (username == null || credentials == null) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("dataSourceRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        String dbCredentials = getPassword(dbConnection, username);
        if (dbCredentials == null) {
            getCredentialHandler().mutate(credentials);
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("dataSourceRealm.authenticateFailure", username));
                return null;
            }
            return null;
        }
        boolean validated = getCredentialHandler().matches(credentials, dbCredentials);
        if (validated) {
            if (this.containerLog.isTraceEnabled()) {
                this.containerLog.trace(sm.getString("dataSourceRealm.authenticateSuccess", username));
            }
            ArrayList<String> list = getRoles(dbConnection, username);
            return new GenericPrincipal(username, list);
        }
        if (this.containerLog.isTraceEnabled()) {
            this.containerLog.trace(sm.getString("dataSourceRealm.authenticateFailure", username));
            return null;
        }
        return null;
    }

    protected void close(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        } catch (SQLException e) {
            this.containerLog.error(sm.getString("dataSourceRealm.commit"), e);
        }
        try {
            dbConnection.close();
        } catch (SQLException e2) {
            this.containerLog.error(sm.getString("dataSourceRealm.close"), e2);
        }
    }

    protected Connection open() {
        Context context;
        try {
            if (this.localDataSource) {
                Context context2 = ContextBindings.getClassLoader();
                context = (Context) context2.lookup("comp/env");
            } else {
                Server server = getServer();
                if (server == null) {
                    this.connectionSuccess = false;
                    this.containerLog.error(sm.getString("dataSourceRealm.noNamingContext"));
                    return null;
                }
                context = server.getGlobalNamingContext();
            }
            DataSource dataSource = (DataSource) context.lookup(this.dataSourceName);
            Connection connection = dataSource.getConnection();
            this.connectionSuccess = true;
            return connection;
        } catch (Exception e) {
            this.connectionSuccess = false;
            this.containerLog.error(sm.getString("dataSourceRealm.exception"), e);
            return null;
        }
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        Connection dbConnection = open();
        if (dbConnection == null) {
            return null;
        }
        try {
            String password = getPassword(dbConnection, username);
            close(dbConnection);
            return password;
        } catch (Throwable th) {
            close(dbConnection);
            throw th;
        }
    }

    protected String getPassword(Connection dbConnection, String username) {
        String dbCredentials = null;
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(this.preparedCredentials);
            try {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        dbCredentials = rs.getString(1);
                    }
                    String trim = dbCredentials != null ? dbCredentials.trim() : null;
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    return trim;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (SQLException e) {
            this.containerLog.error(sm.getString("dataSourceRealm.getPassword.exception", username), e);
            return null;
        }
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        Connection dbConnection = open();
        if (dbConnection == null) {
            return new GenericPrincipal(username, null);
        }
        try {
            GenericPrincipal genericPrincipal = new GenericPrincipal(username, getRoles(dbConnection, username));
            close(dbConnection);
            return genericPrincipal;
        } catch (Throwable th) {
            close(dbConnection);
            throw th;
        }
    }

    protected ArrayList<String> getRoles(String username) {
        Connection dbConnection = open();
        if (dbConnection == null) {
            return null;
        }
        try {
            ArrayList<String> roles = getRoles(dbConnection, username);
            close(dbConnection);
            return roles;
        } catch (Throwable th) {
            close(dbConnection);
            throw th;
        }
    }

    protected ArrayList<String> getRoles(Connection dbConnection, String username) {
        if (this.allRolesMode != RealmBase.AllRolesMode.STRICT_MODE && !isRoleStoreDefined()) {
            return null;
        }
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(this.preparedRoles);
            try {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                try {
                    ArrayList<String> list = new ArrayList<>();
                    while (rs.next()) {
                        String role = rs.getString(1);
                        if (role != null) {
                            list.add(role.trim());
                        }
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                    return list;
                } catch (Throwable th) {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                    }
                }
                throw th3;
            }
        } catch (SQLException e) {
            this.containerLog.error(sm.getString("dataSourceRealm.getRoles.exception", username), e);
            return null;
        }
    }

    private boolean isRoleStoreDefined() {
        return (this.userRoleTable == null && this.roleNameCol == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        this.preparedRoles = "SELECT " + this.roleNameCol + " FROM " + this.userRoleTable + " WHERE " + this.userNameCol + " = ?";
        this.preparedCredentials = "SELECT " + this.userCredCol + " FROM " + this.userTable + " WHERE " + this.userNameCol + " = ?";
        super.startInternal();
    }
}
