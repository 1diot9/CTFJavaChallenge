package org.apache.catalina.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sql.DataSource;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/DataSourceUserDatabase.class */
public class DataSourceUserDatabase extends SparseUserDatabase {
    private static final Log log = LogFactory.getLog((Class<?>) DataSourceUserDatabase.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) DataSourceUserDatabase.class);
    protected final DataSource dataSource;
    protected final String id;
    protected final ConcurrentHashMap<String, User> createdUsers = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, User> modifiedUsers = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, User> removedUsers = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Group> createdGroups = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Group> modifiedGroups = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Group> removedGroups = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Role> createdRoles = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Role> modifiedRoles = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Role> removedRoles = new ConcurrentHashMap<>();
    private String preparedAllUsers = null;
    private String preparedAllGroups = null;
    private String preparedAllRoles = null;
    private String preparedGroup = null;
    private String preparedRole = null;
    private String preparedUserRoles = null;
    private String preparedUser = null;
    private String preparedUserGroups = null;
    private String preparedGroupRoles = null;
    protected String dataSourceName = null;
    protected String roleNameCol = null;
    protected String roleAndGroupDescriptionCol = null;
    protected String groupNameCol = null;
    protected String userCredCol = null;
    protected String userFullNameCol = null;
    protected String userNameCol = null;
    protected String userRoleTable = null;
    protected String userGroupTable = null;
    protected String groupRoleTable = null;
    protected String userTable = null;
    protected String groupTable = null;
    protected String roleTable = null;
    private volatile boolean connectionSuccess = true;
    protected boolean readonly = true;
    private final ReentrantReadWriteLock dbLock = new ReentrantReadWriteLock();
    private final Lock dbReadLock = this.dbLock.readLock();
    private final Lock dbWriteLock = this.dbLock.writeLock();
    private final ReentrantReadWriteLock groupsLock = new ReentrantReadWriteLock();
    private final Lock groupsReadLock = this.groupsLock.readLock();
    private final Lock groupsWriteLock = this.groupsLock.writeLock();
    private final ReentrantReadWriteLock usersLock = new ReentrantReadWriteLock();
    private final Lock usersReadLock = this.usersLock.readLock();
    private final Lock usersWriteLock = this.usersLock.writeLock();
    private final ReentrantReadWriteLock rolesLock = new ReentrantReadWriteLock();
    private final Lock rolesReadLock = this.rolesLock.readLock();
    private final Lock rolesWriteLock = this.rolesLock.writeLock();

    public DataSourceUserDatabase(DataSource dataSource, String id) {
        this.dataSource = dataSource;
        this.id = id;
    }

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
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

    public String getRoleAndGroupDescriptionCol() {
        return this.roleAndGroupDescriptionCol;
    }

    public void setRoleAndGroupDescriptionCol(String roleAndGroupDescriptionCol) {
        this.roleAndGroupDescriptionCol = roleAndGroupDescriptionCol;
    }

    public String getGroupNameCol() {
        return this.groupNameCol;
    }

    public void setGroupNameCol(String groupNameCol) {
        this.groupNameCol = groupNameCol;
    }

    public String getUserFullNameCol() {
        return this.userFullNameCol;
    }

    public void setUserFullNameCol(String userFullNameCol) {
        this.userFullNameCol = userFullNameCol;
    }

    public String getUserGroupTable() {
        return this.userGroupTable;
    }

    public void setUserGroupTable(String userGroupTable) {
        this.userGroupTable = userGroupTable;
    }

    public String getGroupRoleTable() {
        return this.groupRoleTable;
    }

    public void setGroupRoleTable(String groupRoleTable) {
        this.groupRoleTable = groupRoleTable;
    }

    public String getGroupTable() {
        return this.groupTable;
    }

    public void setGroupTable(String groupTable) {
        this.groupTable = groupTable;
    }

    public String getRoleTable() {
        return this.roleTable;
    }

    public void setRoleTable(String roleTable) {
        this.roleTable = roleTable;
    }

    public boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override // org.apache.catalina.UserDatabase
    public String getId() {
        return this.id;
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<Group> getGroups() {
        Group group;
        this.dbReadLock.lock();
        try {
            this.groupsReadLock.lock();
            try {
                HashMap<String, Group> groups = new HashMap<>();
                groups.putAll(this.createdGroups);
                groups.putAll(this.modifiedGroups);
                try {
                    Connection dbConnection = openConnection();
                    if (dbConnection != null) {
                        try {
                            if (this.preparedAllGroups != null) {
                                PreparedStatement stmt = dbConnection.prepareStatement(this.preparedAllGroups);
                                try {
                                    ResultSet rs = stmt.executeQuery();
                                    while (rs.next()) {
                                        try {
                                            String groupName = rs.getString(1);
                                            if (groupName != null && !groups.containsKey(groupName) && !this.removedGroups.containsKey(groupName) && (group = findGroupInternal(dbConnection, groupName)) != null) {
                                                groups.put(groupName, group);
                                            }
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
                                    }
                                    if (rs != null) {
                                        rs.close();
                                    }
                                    if (stmt != null) {
                                        stmt.close();
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
                            }
                        } catch (Throwable th5) {
                            if (dbConnection != null) {
                                try {
                                    dbConnection.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                            }
                            throw th5;
                        }
                    }
                    if (dbConnection != null) {
                        dbConnection.close();
                    }
                } catch (SQLException e) {
                    log.error(sm.getString("dataSourceUserDatabase.exception"), e);
                }
                Iterator<Group> it = groups.values().iterator();
                this.groupsReadLock.unlock();
                this.dbReadLock.unlock();
                return it;
            } catch (Throwable th7) {
                this.groupsReadLock.unlock();
                throw th7;
            }
        } catch (Throwable th8) {
            this.dbReadLock.unlock();
            throw th8;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<Role> getRoles() {
        Role role;
        this.dbReadLock.lock();
        try {
            this.rolesReadLock.lock();
            try {
                HashMap<String, Role> roles = new HashMap<>();
                roles.putAll(this.createdRoles);
                roles.putAll(this.modifiedRoles);
                try {
                    Connection dbConnection = openConnection();
                    if (dbConnection != null) {
                        try {
                            if (this.preparedAllRoles != null) {
                                PreparedStatement stmt = dbConnection.prepareStatement(this.preparedAllRoles);
                                try {
                                    ResultSet rs = stmt.executeQuery();
                                    while (rs.next()) {
                                        try {
                                            String roleName = rs.getString(1);
                                            if (roleName != null && !roles.containsKey(roleName) && !this.removedRoles.containsKey(roleName) && (role = findRoleInternal(dbConnection, roleName)) != null) {
                                                roles.put(roleName, role);
                                            }
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
                                    }
                                    if (rs != null) {
                                        rs.close();
                                    }
                                    if (stmt != null) {
                                        stmt.close();
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
                            }
                        } catch (Throwable th5) {
                            if (dbConnection != null) {
                                try {
                                    dbConnection.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                            }
                            throw th5;
                        }
                    }
                    if (dbConnection != null) {
                        dbConnection.close();
                    }
                } catch (SQLException e) {
                    log.error(sm.getString("dataSourceUserDatabase.exception"), e);
                }
                Iterator<Role> it = roles.values().iterator();
                this.rolesReadLock.unlock();
                this.dbReadLock.unlock();
                return it;
            } catch (Throwable th7) {
                this.rolesReadLock.unlock();
                throw th7;
            }
        } catch (Throwable th8) {
            this.dbReadLock.unlock();
            throw th8;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.UserDatabase
    public Iterator<User> getUsers() {
        User user;
        this.dbReadLock.lock();
        try {
            this.usersReadLock.lock();
            try {
                HashMap<String, User> users = new HashMap<>();
                users.putAll(this.createdUsers);
                users.putAll(this.modifiedUsers);
                Connection dbConnection = openConnection();
                try {
                    if (dbConnection != null) {
                        try {
                            PreparedStatement stmt = dbConnection.prepareStatement(this.preparedAllUsers);
                            try {
                                ResultSet rs = stmt.executeQuery();
                                while (rs.next()) {
                                    try {
                                        String userName = rs.getString(1);
                                        if (userName != null && !users.containsKey(userName) && !this.removedUsers.containsKey(userName) && (user = findUserInternal(dbConnection, userName)) != null) {
                                            users.put(userName, user);
                                        }
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
                                }
                                if (rs != null) {
                                    rs.close();
                                }
                                if (stmt != null) {
                                    stmt.close();
                                }
                                closeConnection(dbConnection);
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
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e);
                            closeConnection(dbConnection);
                        }
                    }
                    Iterator<User> it = users.values().iterator();
                    this.usersReadLock.unlock();
                    this.dbReadLock.unlock();
                    return it;
                } catch (Throwable th5) {
                    closeConnection(dbConnection);
                    throw th5;
                }
            } catch (Throwable th6) {
                this.usersReadLock.unlock();
                throw th6;
            }
        } catch (Throwable th7) {
            this.dbReadLock.unlock();
            throw th7;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void close() throws Exception {
    }

    @Override // org.apache.catalina.UserDatabase
    public Group createGroup(String groupname, String description) {
        this.dbReadLock.lock();
        try {
            this.groupsWriteLock.lock();
            try {
                Group group = new GenericGroup(this, groupname, description, null);
                this.createdGroups.put(groupname, group);
                this.modifiedGroups.remove(groupname);
                this.groupsWriteLock.unlock();
                this.dbReadLock.unlock();
                return group;
            } catch (Throwable th) {
                this.groupsWriteLock.unlock();
                throw th;
            }
        } catch (Throwable th2) {
            this.dbReadLock.unlock();
            throw th2;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Role createRole(String rolename, String description) {
        this.dbReadLock.lock();
        try {
            this.rolesWriteLock.lock();
            try {
                Role role = new GenericRole(this, rolename, description);
                this.createdRoles.put(rolename, role);
                this.modifiedRoles.remove(rolename);
                this.rolesWriteLock.unlock();
                this.dbReadLock.unlock();
                return role;
            } catch (Throwable th) {
                this.rolesWriteLock.unlock();
                throw th;
            }
        } catch (Throwable th2) {
            this.dbReadLock.unlock();
            throw th2;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public User createUser(String username, String password, String fullName) {
        this.dbReadLock.lock();
        try {
            this.usersWriteLock.lock();
            try {
                User user = new GenericUser(this, username, password, fullName, null, null);
                this.createdUsers.put(username, user);
                this.modifiedUsers.remove(username);
                this.usersWriteLock.unlock();
                this.dbReadLock.unlock();
                return user;
            } catch (Throwable th) {
                this.usersWriteLock.unlock();
                throw th;
            }
        } catch (Throwable th2) {
            this.dbReadLock.unlock();
            throw th2;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Group findGroup(String groupname) {
        this.dbReadLock.lock();
        try {
            this.groupsReadLock.lock();
            try {
                Group group = this.createdGroups.get(groupname);
                if (group != null) {
                    this.dbReadLock.unlock();
                    return group;
                }
                Group group2 = this.modifiedGroups.get(groupname);
                if (group2 != null) {
                    this.groupsReadLock.unlock();
                    this.dbReadLock.unlock();
                    return group2;
                }
                if (this.removedGroups.get(groupname) != null) {
                    this.groupsReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                if (!isGroupStoreDefined()) {
                    this.groupsReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                Connection dbConnection = openConnection();
                if (dbConnection == null) {
                    this.groupsReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                try {
                    Group findGroupInternal = findGroupInternal(dbConnection, groupname);
                    closeConnection(dbConnection);
                    this.groupsReadLock.unlock();
                    this.dbReadLock.unlock();
                    return findGroupInternal;
                } catch (Throwable th) {
                    closeConnection(dbConnection);
                    throw th;
                }
            } finally {
                this.groupsReadLock.unlock();
            }
        } catch (Throwable th2) {
            this.dbReadLock.unlock();
            throw th2;
        }
    }

    public Group findGroupInternal(Connection dbConnection, String groupName) {
        Role groupRole;
        Group group = null;
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(this.preparedGroup);
            try {
                stmt.setString(1, groupName);
                ResultSet rs2 = stmt.executeQuery();
                try {
                    if (rs2.next() && rs2.getString(1) != null) {
                        String description = this.roleAndGroupDescriptionCol != null ? rs2.getString(2) : null;
                        ArrayList<Role> groupRoles = new ArrayList<>();
                        if (groupName != null) {
                            groupName = groupName.trim();
                            try {
                                PreparedStatement stmt2 = dbConnection.prepareStatement(this.preparedGroupRoles);
                                try {
                                    stmt2.setString(1, groupName);
                                    rs2 = stmt2.executeQuery();
                                    while (rs2.next()) {
                                        try {
                                            String roleName = rs2.getString(1);
                                            if (roleName != null && (groupRole = findRoleInternal(dbConnection, roleName)) != null) {
                                                groupRoles.add(groupRole);
                                            }
                                        } finally {
                                        }
                                    }
                                    if (rs2 != null) {
                                        rs2.close();
                                    }
                                    if (stmt2 != null) {
                                        stmt2.close();
                                    }
                                } catch (Throwable th) {
                                    if (stmt2 != null) {
                                        try {
                                            stmt2.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    }
                                    throw th;
                                }
                            } catch (SQLException e) {
                                log.error(sm.getString("dataSourceUserDatabase.exception"), e);
                            }
                        }
                        group = new GenericGroup(this, groupName, description, groupRoles);
                    }
                    if (rs2 != null) {
                        rs2.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                } finally {
                }
            } finally {
            }
        } catch (SQLException e2) {
            log.error(sm.getString("dataSourceUserDatabase.exception"), e2);
        }
        return group;
    }

    @Override // org.apache.catalina.UserDatabase
    public Role findRole(String rolename) {
        this.dbReadLock.lock();
        try {
            this.rolesReadLock.lock();
            try {
                Role role = this.createdRoles.get(rolename);
                if (role != null) {
                    this.dbReadLock.unlock();
                    return role;
                }
                Role role2 = this.modifiedRoles.get(rolename);
                if (role2 != null) {
                    this.rolesReadLock.unlock();
                    this.dbReadLock.unlock();
                    return role2;
                }
                if (this.removedRoles.get(rolename) != null) {
                    this.rolesReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                if (this.userRoleTable == null || this.roleNameCol == null) {
                    this.rolesReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                Connection dbConnection = openConnection();
                if (dbConnection == null) {
                    this.rolesReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                try {
                    Role findRoleInternal = findRoleInternal(dbConnection, rolename);
                    closeConnection(dbConnection);
                    this.rolesReadLock.unlock();
                    this.dbReadLock.unlock();
                    return findRoleInternal;
                } catch (Throwable th) {
                    closeConnection(dbConnection);
                    throw th;
                }
            } finally {
                this.rolesReadLock.unlock();
            }
        } catch (Throwable th2) {
            this.dbReadLock.unlock();
            throw th2;
        }
    }

    public Role findRoleInternal(Connection dbConnection, String roleName) {
        Role role = null;
        try {
            PreparedStatement stmt = dbConnection.prepareStatement(this.preparedRole);
            try {
                stmt.setString(1, roleName);
                ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next() && rs.getString(1) != null) {
                        String description = this.roleAndGroupDescriptionCol != null ? rs.getString(2) : null;
                        role = new GenericRole(this, roleName, description);
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
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
            } finally {
            }
        } catch (SQLException e) {
            log.error(sm.getString("dataSourceUserDatabase.exception"), e);
        }
        return role;
    }

    @Override // org.apache.catalina.UserDatabase
    public User findUser(String username) {
        this.dbReadLock.lock();
        try {
            this.usersReadLock.lock();
            try {
                User user = this.createdUsers.get(username);
                if (user != null) {
                    this.dbReadLock.unlock();
                    return user;
                }
                User user2 = this.modifiedUsers.get(username);
                if (user2 != null) {
                    this.usersReadLock.unlock();
                    this.dbReadLock.unlock();
                    return user2;
                }
                if (this.removedUsers.get(username) != null) {
                    this.usersReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                Connection dbConnection = openConnection();
                if (dbConnection == null) {
                    this.usersReadLock.unlock();
                    this.dbReadLock.unlock();
                    return null;
                }
                try {
                    User findUserInternal = findUserInternal(dbConnection, username);
                    closeConnection(dbConnection);
                    this.usersReadLock.unlock();
                    this.dbReadLock.unlock();
                    return findUserInternal;
                } catch (Throwable th) {
                    closeConnection(dbConnection);
                    throw th;
                }
            } finally {
                this.usersReadLock.unlock();
            }
        } catch (Throwable th2) {
            this.dbReadLock.unlock();
            throw th2;
        }
    }

    public User findUserInternal(Connection dbConnection, String userName) {
        ResultSet rs;
        Group group;
        PreparedStatement stmt;
        Role role;
        String dbCredentials = null;
        String fullName = null;
        try {
            stmt = dbConnection.prepareStatement(this.preparedUser);
            try {
                stmt.setString(1, userName);
                rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        dbCredentials = rs.getString(1);
                        if (this.userFullNameCol != null) {
                            fullName = rs.getString(2);
                        }
                    }
                    dbCredentials = dbCredentials != null ? dbCredentials.trim() : null;
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                } finally {
                }
            } finally {
            }
        } catch (SQLException e) {
            log.error(sm.getString("dataSourceUserDatabase.exception"), e);
        }
        ArrayList<Group> groups = new ArrayList<>();
        if (isGroupStoreDefined()) {
            try {
                PreparedStatement stmt2 = dbConnection.prepareStatement(this.preparedUserGroups);
                try {
                    stmt2.setString(1, userName);
                    rs = stmt2.executeQuery();
                    while (rs.next()) {
                        try {
                            String groupName = rs.getString(1);
                            if (groupName != null && (group = findGroupInternal(dbConnection, groupName)) != null) {
                                groups.add(group);
                            }
                        } finally {
                        }
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (stmt2 != null) {
                        stmt2.close();
                    }
                } finally {
                    if (stmt2 != null) {
                        try {
                            stmt2.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                    }
                }
            } catch (SQLException e2) {
                log.error(sm.getString("dataSourceUserDatabase.exception"), e2);
            }
        }
        ArrayList<Role> roles = new ArrayList<>();
        if (this.userRoleTable != null && this.roleNameCol != null) {
            try {
                stmt = dbConnection.prepareStatement(this.preparedUserRoles);
                try {
                    stmt.setString(1, userName);
                    ResultSet rs2 = stmt.executeQuery();
                    while (rs2.next()) {
                        try {
                            String roleName = rs2.getString(1);
                            if (roleName != null && (role = findRoleInternal(dbConnection, roleName)) != null) {
                                roles.add(role);
                            }
                        } finally {
                            if (rs2 != null) {
                                try {
                                    rs2.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                        }
                    }
                    if (rs2 != null) {
                        rs2.close();
                    }
                    if (stmt != null) {
                        stmt.close();
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    }
                }
            } catch (SQLException e3) {
                log.error(sm.getString("dataSourceUserDatabase.exception"), e3);
            }
        }
        User user = new GenericUser(this, userName, dbCredentials, fullName, groups, roles);
        return user;
    }

    @Override // org.apache.catalina.UserDatabase
    public void modifiedGroup(Group group) {
        this.dbReadLock.lock();
        try {
            this.groupsWriteLock.lock();
            try {
                String name = group.getName();
                if (!this.createdGroups.containsKey(name) && !this.removedGroups.containsKey(name)) {
                    this.modifiedGroups.put(name, group);
                }
                this.groupsWriteLock.unlock();
            } catch (Throwable th) {
                this.groupsWriteLock.unlock();
                throw th;
            }
        } finally {
            this.dbReadLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void modifiedRole(Role role) {
        this.dbReadLock.lock();
        try {
            this.rolesWriteLock.lock();
            try {
                String name = role.getName();
                if (!this.createdRoles.containsKey(name) && !this.removedRoles.containsKey(name)) {
                    this.modifiedRoles.put(name, role);
                }
                this.rolesWriteLock.unlock();
            } catch (Throwable th) {
                this.rolesWriteLock.unlock();
                throw th;
            }
        } finally {
            this.dbReadLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void modifiedUser(User user) {
        this.dbReadLock.lock();
        try {
            this.usersWriteLock.lock();
            try {
                String name = user.getName();
                if (!this.createdUsers.containsKey(name) && !this.removedUsers.containsKey(name)) {
                    this.modifiedUsers.put(name, user);
                }
                this.usersWriteLock.unlock();
            } catch (Throwable th) {
                this.usersWriteLock.unlock();
                throw th;
            }
        } finally {
            this.dbReadLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void open() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("DataSource UserDatabase features: User<->Role [" + Boolean.toString((this.userRoleTable == null || this.roleNameCol == null) ? false : true) + "], Roles [" + Boolean.toString(isRoleStoreDefined()) + "], Groups [" + Boolean.toString(isRoleStoreDefined()) + "]");
        }
        this.dbWriteLock.lock();
        try {
            StringBuilder temp = new StringBuilder("SELECT ");
            temp.append(this.userCredCol);
            if (this.userFullNameCol != null) {
                temp.append(',').append(this.userFullNameCol);
            }
            temp.append(" FROM ");
            temp.append(this.userTable);
            temp.append(" WHERE ");
            temp.append(this.userNameCol);
            temp.append(" = ?");
            this.preparedUser = temp.toString();
            this.preparedAllUsers = "SELECT " + this.userNameCol + " FROM " + this.userTable;
            this.preparedUserRoles = "SELECT " + this.roleNameCol + " FROM " + this.userRoleTable + " WHERE " + this.userNameCol + " = ?";
            if (isGroupStoreDefined()) {
                this.preparedUserGroups = "SELECT " + this.groupNameCol + " FROM " + this.userGroupTable + " WHERE " + this.userNameCol + " = ?";
                this.preparedGroupRoles = "SELECT " + this.roleNameCol + " FROM " + this.groupRoleTable + " WHERE " + this.groupNameCol + " = ?";
                StringBuilder temp2 = new StringBuilder("SELECT ");
                temp2.append(this.groupNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp2.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp2.append(" FROM ");
                temp2.append(this.groupTable);
                temp2.append(" WHERE ");
                temp2.append(this.groupNameCol);
                temp2.append(" = ?");
                this.preparedGroup = temp2.toString();
                this.preparedAllGroups = "SELECT " + this.groupNameCol + " FROM " + this.groupTable;
            }
            if (isRoleStoreDefined()) {
                StringBuilder temp3 = new StringBuilder("SELECT ");
                temp3.append(this.roleNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp3.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp3.append(" FROM ");
                temp3.append(this.roleTable);
                temp3.append(" WHERE ");
                temp3.append(this.roleNameCol);
                temp3.append(" = ?");
                this.preparedRole = temp3.toString();
                this.preparedAllRoles = "SELECT " + this.roleNameCol + " FROM " + this.roleTable;
            } else if (this.userRoleTable != null && this.roleNameCol != null) {
                this.preparedRole = "SELECT " + this.roleNameCol + " FROM " + this.userRoleTable + " WHERE " + this.roleNameCol + " = ?";
            }
        } finally {
            this.dbWriteLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeGroup(Group group) {
        this.dbReadLock.lock();
        try {
            this.groupsWriteLock.lock();
            try {
                String name = group.getName();
                this.createdGroups.remove(name);
                this.modifiedGroups.remove(name);
                this.removedGroups.put(name, group);
                this.groupsWriteLock.unlock();
            } catch (Throwable th) {
                this.groupsWriteLock.unlock();
                throw th;
            }
        } finally {
            this.dbReadLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeRole(Role role) {
        this.dbReadLock.lock();
        try {
            this.rolesWriteLock.lock();
            try {
                String name = role.getName();
                this.createdRoles.remove(name);
                this.modifiedRoles.remove(name);
                this.removedRoles.put(name, role);
                this.rolesWriteLock.unlock();
            } catch (Throwable th) {
                this.rolesWriteLock.unlock();
                throw th;
            }
        } finally {
            this.dbReadLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeUser(User user) {
        this.dbReadLock.lock();
        try {
            this.usersWriteLock.lock();
            try {
                String name = user.getName();
                this.createdUsers.remove(name);
                this.modifiedUsers.remove(name);
                this.removedUsers.put(name, user);
                this.usersWriteLock.unlock();
            } catch (Throwable th) {
                this.usersWriteLock.unlock();
                throw th;
            }
        } finally {
            this.dbReadLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void save() throws Exception {
        Connection dbConnection;
        if (this.readonly || (dbConnection = openConnection()) == null) {
            return;
        }
        this.dbWriteLock.lock();
        try {
            try {
                saveInternal(dbConnection);
                closeConnection(dbConnection);
            } catch (Throwable th) {
                closeConnection(dbConnection);
                throw th;
            }
        } finally {
            this.dbWriteLock.unlock();
        }
    }

    protected void saveInternal(Connection dbConnection) {
        PreparedStatement stmt;
        PreparedStatement stmt2;
        PreparedStatement stmt3;
        PreparedStatement stmt4;
        PreparedStatement stmt5;
        PreparedStatement stmt6;
        PreparedStatement stmt7;
        PreparedStatement stmt8;
        PreparedStatement stmt9;
        StringBuilder temp = null;
        StringBuilder tempRelationDelete = null;
        if (isRoleStoreDefined()) {
            if (!this.removedRoles.isEmpty()) {
                temp = new StringBuilder("DELETE FROM ");
                temp.append(this.roleTable);
                temp.append(" WHERE ").append(this.roleNameCol);
                temp.append(" = ?");
                if (this.groupRoleTable != null) {
                    tempRelationDelete = new StringBuilder("DELETE FROM ");
                    tempRelationDelete.append(this.groupRoleTable);
                    tempRelationDelete.append(" WHERE ");
                    tempRelationDelete.append(this.roleNameCol);
                    tempRelationDelete.append(" = ?");
                }
                for (Role role : this.removedRoles.values()) {
                    if (tempRelationDelete != null) {
                        try {
                            PreparedStatement stmt10 = dbConnection.prepareStatement(tempRelationDelete.toString());
                            try {
                                stmt10.setString(1, role.getRolename());
                                stmt10.executeUpdate();
                                if (stmt10 != null) {
                                    stmt10.close();
                                }
                            } catch (Throwable th) {
                                if (stmt10 != null) {
                                    try {
                                        stmt10.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                }
                                throw th;
                                break;
                            }
                        } catch (SQLException e) {
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e);
                        }
                    }
                    try {
                        PreparedStatement stmt11 = dbConnection.prepareStatement("DELETE FROM " + this.userRoleTable + " WHERE " + this.roleNameCol + " = ?");
                        try {
                            stmt11.setString(1, role.getRolename());
                            stmt11.executeUpdate();
                            if (stmt11 != null) {
                                stmt11.close();
                            }
                        } catch (Throwable th3) {
                            if (stmt11 != null) {
                                try {
                                    stmt11.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            }
                            throw th3;
                            break;
                        }
                    } catch (SQLException e2) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e2);
                    }
                    try {
                        PreparedStatement stmt12 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt12.setString(1, role.getRolename());
                            stmt12.executeUpdate();
                            if (stmt12 != null) {
                                stmt12.close();
                            }
                        } catch (Throwable th5) {
                            if (stmt12 != null) {
                                try {
                                    stmt12.close();
                                } catch (Throwable th6) {
                                    th5.addSuppressed(th6);
                                }
                            }
                            throw th5;
                            break;
                        }
                    } catch (SQLException e3) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e3);
                    }
                }
                this.removedRoles.clear();
            }
            if (!this.createdRoles.isEmpty()) {
                temp = new StringBuilder("INSERT INTO ");
                temp.append(this.roleTable);
                temp.append('(').append(this.roleNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp.append(") VALUES (?");
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(", ?");
                }
                temp.append(')');
                for (Role role2 : this.createdRoles.values()) {
                    try {
                        stmt9 = dbConnection.prepareStatement(temp.toString());
                    } catch (SQLException e4) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e4);
                    }
                    try {
                        stmt9.setString(1, role2.getRolename());
                        if (this.roleAndGroupDescriptionCol != null) {
                            stmt9.setString(2, role2.getDescription());
                        }
                        stmt9.executeUpdate();
                        if (stmt9 != null) {
                            stmt9.close();
                        }
                    } catch (Throwable th7) {
                        if (stmt9 != null) {
                            try {
                                stmt9.close();
                            } catch (Throwable th8) {
                                th7.addSuppressed(th8);
                            }
                        }
                        throw th7;
                        break;
                    }
                }
                this.createdRoles.clear();
            }
            if (!this.modifiedRoles.isEmpty() && this.roleAndGroupDescriptionCol != null) {
                temp = new StringBuilder("UPDATE ");
                temp.append(this.roleTable);
                temp.append(" SET ").append(this.roleAndGroupDescriptionCol);
                temp.append(" = ? WHERE ").append(this.roleNameCol);
                temp.append(" = ?");
                for (Role role3 : this.modifiedRoles.values()) {
                    try {
                        PreparedStatement stmt13 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt13.setString(1, role3.getDescription());
                            stmt13.setString(2, role3.getRolename());
                            stmt13.executeUpdate();
                            if (stmt13 != null) {
                                stmt13.close();
                            }
                        } catch (Throwable th9) {
                            if (stmt13 != null) {
                                try {
                                    stmt13.close();
                                } catch (Throwable th10) {
                                    th9.addSuppressed(th10);
                                }
                            }
                            throw th9;
                            break;
                        }
                    } catch (SQLException e5) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e5);
                    }
                }
                this.modifiedRoles.clear();
            }
        } else if (this.userRoleTable != null && this.roleNameCol != null) {
            for (Role role4 : this.removedRoles.values()) {
                try {
                    stmt = dbConnection.prepareStatement("DELETE FROM " + this.userRoleTable + " WHERE " + this.roleNameCol + " = ?");
                } catch (SQLException e6) {
                    log.error(sm.getString("dataSourceUserDatabase.exception"), e6);
                }
                try {
                    stmt.setString(1, role4.getRolename());
                    stmt.executeUpdate();
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (Throwable th11) {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Throwable th12) {
                            th11.addSuppressed(th12);
                        }
                    }
                    throw th11;
                    break;
                }
            }
            this.removedRoles.clear();
        }
        if (isGroupStoreDefined()) {
            StringBuilder tempRelation = new StringBuilder("INSERT INTO ");
            tempRelation.append(this.groupRoleTable);
            tempRelation.append('(').append(this.groupNameCol).append(", ");
            tempRelation.append(this.roleNameCol);
            tempRelation.append(") VALUES (?, ?)");
            String groupRoleRelation = tempRelation.toString();
            String groupRoleRelationDelete = "DELETE FROM " + this.groupRoleTable + " WHERE " + this.groupNameCol + " = ?";
            if (!this.removedGroups.isEmpty()) {
                temp = new StringBuilder("DELETE FROM ");
                temp.append(this.groupTable);
                temp.append(" WHERE ").append(this.groupNameCol);
                temp.append(" = ?");
                for (Group group : this.removedGroups.values()) {
                    try {
                        stmt8 = dbConnection.prepareStatement(groupRoleRelationDelete);
                    } catch (SQLException e7) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e7);
                    }
                    try {
                        stmt8.setString(1, group.getGroupname());
                        stmt8.executeUpdate();
                        if (stmt8 != null) {
                            stmt8.close();
                        }
                        try {
                            stmt7 = dbConnection.prepareStatement("DELETE FROM " + this.userGroupTable + " WHERE " + this.groupNameCol + " = ?");
                        } catch (SQLException e8) {
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e8);
                        }
                        try {
                            stmt7.setString(1, group.getGroupname());
                            stmt7.executeUpdate();
                            if (stmt7 != null) {
                                stmt7.close();
                            }
                            try {
                                PreparedStatement stmt14 = dbConnection.prepareStatement(temp.toString());
                                try {
                                    stmt14.setString(1, group.getGroupname());
                                    stmt14.executeUpdate();
                                    if (stmt14 != null) {
                                        stmt14.close();
                                    }
                                } catch (Throwable th13) {
                                    if (stmt14 != null) {
                                        try {
                                            stmt14.close();
                                        } catch (Throwable th14) {
                                            th13.addSuppressed(th14);
                                        }
                                    }
                                    throw th13;
                                    break;
                                }
                            } catch (SQLException e9) {
                                log.error(sm.getString("dataSourceUserDatabase.exception"), e9);
                            }
                        } catch (Throwable th15) {
                            if (stmt7 != null) {
                                try {
                                    stmt7.close();
                                } catch (Throwable th16) {
                                    th15.addSuppressed(th16);
                                }
                            }
                            throw th15;
                            break;
                        }
                    } catch (Throwable th17) {
                        if (stmt8 != null) {
                            try {
                                stmt8.close();
                            } catch (Throwable th18) {
                                th17.addSuppressed(th18);
                            }
                        }
                        throw th17;
                        break;
                    }
                }
                this.removedGroups.clear();
            }
            if (!this.createdGroups.isEmpty()) {
                temp = new StringBuilder("INSERT INTO ");
                temp.append(this.groupTable);
                temp.append('(').append(this.groupNameCol);
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(',').append(this.roleAndGroupDescriptionCol);
                }
                temp.append(") VALUES (?");
                if (this.roleAndGroupDescriptionCol != null) {
                    temp.append(", ?");
                }
                temp.append(')');
                loop4: for (Group group2 : this.createdGroups.values()) {
                    try {
                        PreparedStatement stmt15 = dbConnection.prepareStatement(temp.toString());
                        try {
                            stmt15.setString(1, group2.getGroupname());
                            if (this.roleAndGroupDescriptionCol != null) {
                                stmt15.setString(2, group2.getDescription());
                            }
                            stmt15.executeUpdate();
                            if (stmt15 != null) {
                                stmt15.close();
                            }
                        } catch (Throwable th19) {
                            if (stmt15 != null) {
                                try {
                                    stmt15.close();
                                } catch (Throwable th20) {
                                    th19.addSuppressed(th20);
                                }
                            }
                            throw th19;
                            break;
                        }
                    } catch (SQLException e10) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e10);
                    }
                    Iterator<Role> roles = group2.getRoles();
                    while (roles.hasNext()) {
                        Role role5 = roles.next();
                        try {
                            stmt6 = dbConnection.prepareStatement(groupRoleRelation);
                        } catch (SQLException e11) {
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e11);
                        }
                        try {
                            stmt6.setString(1, group2.getGroupname());
                            stmt6.setString(2, role5.getRolename());
                            stmt6.executeUpdate();
                            if (stmt6 != null) {
                                stmt6.close();
                            }
                        } catch (Throwable th21) {
                            if (stmt6 != null) {
                                try {
                                    stmt6.close();
                                } catch (Throwable th22) {
                                    th21.addSuppressed(th22);
                                }
                            }
                            throw th21;
                            break loop4;
                        }
                    }
                }
                this.createdGroups.clear();
            }
            if (!this.modifiedGroups.isEmpty()) {
                if (this.roleAndGroupDescriptionCol != null) {
                    temp = new StringBuilder("UPDATE ");
                    temp.append(this.groupTable);
                    temp.append(" SET ").append(this.roleAndGroupDescriptionCol);
                    temp.append(" = ? WHERE ").append(this.groupNameCol);
                    temp.append(" = ?");
                }
                loop6: for (Group group3 : this.modifiedGroups.values()) {
                    if (temp != null) {
                        try {
                            PreparedStatement stmt16 = dbConnection.prepareStatement(temp.toString());
                            try {
                                stmt16.setString(1, group3.getDescription());
                                stmt16.setString(2, group3.getGroupname());
                                stmt16.executeUpdate();
                                if (stmt16 != null) {
                                    stmt16.close();
                                }
                            } catch (Throwable th23) {
                                if (stmt16 != null) {
                                    try {
                                        stmt16.close();
                                    } catch (Throwable th24) {
                                        th23.addSuppressed(th24);
                                    }
                                }
                                throw th23;
                                break;
                            }
                        } catch (SQLException e12) {
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e12);
                        }
                    }
                    try {
                        stmt5 = dbConnection.prepareStatement(groupRoleRelationDelete);
                    } catch (SQLException e13) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e13);
                    }
                    try {
                        stmt5.setString(1, group3.getGroupname());
                        stmt5.executeUpdate();
                        if (stmt5 != null) {
                            stmt5.close();
                        }
                        Iterator<Role> roles2 = group3.getRoles();
                        while (roles2.hasNext()) {
                            Role role6 = roles2.next();
                            try {
                                PreparedStatement stmt17 = dbConnection.prepareStatement(groupRoleRelation);
                                try {
                                    stmt17.setString(1, group3.getGroupname());
                                    stmt17.setString(2, role6.getRolename());
                                    stmt17.executeUpdate();
                                    if (stmt17 != null) {
                                        stmt17.close();
                                    }
                                } catch (Throwable th25) {
                                    if (stmt17 != null) {
                                        try {
                                            stmt17.close();
                                        } catch (Throwable th26) {
                                            th25.addSuppressed(th26);
                                        }
                                    }
                                    throw th25;
                                    break loop6;
                                }
                            } catch (SQLException e14) {
                                log.error(sm.getString("dataSourceUserDatabase.exception"), e14);
                            }
                        }
                    } catch (Throwable th27) {
                        if (stmt5 != null) {
                            try {
                                stmt5.close();
                            } catch (Throwable th28) {
                                th27.addSuppressed(th28);
                            }
                        }
                        throw th27;
                        break;
                    }
                }
                this.modifiedGroups.clear();
            }
        }
        String userRoleRelation = null;
        String userRoleRelationDelete = null;
        if (this.userRoleTable != null && this.roleNameCol != null) {
            StringBuilder tempRelation2 = new StringBuilder("INSERT INTO ");
            tempRelation2.append(this.userRoleTable);
            tempRelation2.append('(').append(this.userNameCol).append(", ");
            tempRelation2.append(this.roleNameCol);
            tempRelation2.append(") VALUES (?, ?)");
            userRoleRelation = tempRelation2.toString();
            userRoleRelationDelete = "DELETE FROM " + this.userRoleTable + " WHERE " + this.userNameCol + " = ?";
        }
        String userGroupRelation = null;
        String userGroupRelationDelete = null;
        if (isGroupStoreDefined()) {
            StringBuilder tempRelation3 = new StringBuilder("INSERT INTO ");
            tempRelation3.append(this.userGroupTable);
            tempRelation3.append('(').append(this.userNameCol).append(", ");
            tempRelation3.append(this.groupNameCol);
            tempRelation3.append(") VALUES (?, ?)");
            userGroupRelation = tempRelation3.toString();
            userGroupRelationDelete = "DELETE FROM " + this.userGroupTable + " WHERE " + this.userNameCol + " = ?";
        }
        if (!this.removedUsers.isEmpty()) {
            StringBuilder temp2 = new StringBuilder("DELETE FROM ");
            temp2.append(this.userTable);
            temp2.append(" WHERE ").append(this.userNameCol);
            temp2.append(" = ?");
            for (User user : this.removedUsers.values()) {
                if (userRoleRelationDelete != null) {
                    try {
                        PreparedStatement stmt18 = dbConnection.prepareStatement(userRoleRelationDelete);
                        try {
                            stmt18.setString(1, user.getUsername());
                            stmt18.executeUpdate();
                            if (stmt18 != null) {
                                stmt18.close();
                            }
                        } catch (Throwable th29) {
                            if (stmt18 != null) {
                                try {
                                    stmt18.close();
                                } catch (Throwable th30) {
                                    th29.addSuppressed(th30);
                                }
                            }
                            throw th29;
                            break;
                        }
                    } catch (SQLException e15) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e15);
                    }
                }
                if (userGroupRelationDelete != null) {
                    try {
                        PreparedStatement stmt19 = dbConnection.prepareStatement(userGroupRelationDelete);
                        try {
                            stmt19.setString(1, user.getUsername());
                            stmt19.executeUpdate();
                            if (stmt19 != null) {
                                stmt19.close();
                            }
                        } catch (Throwable th31) {
                            if (stmt19 != null) {
                                try {
                                    stmt19.close();
                                } catch (Throwable th32) {
                                    th31.addSuppressed(th32);
                                }
                            }
                            throw th31;
                            break;
                        }
                    } catch (SQLException e16) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e16);
                    }
                }
                try {
                    stmt4 = dbConnection.prepareStatement(temp2.toString());
                } catch (SQLException e17) {
                    log.error(sm.getString("dataSourceUserDatabase.exception"), e17);
                }
                try {
                    stmt4.setString(1, user.getUsername());
                    stmt4.executeUpdate();
                    if (stmt4 != null) {
                        stmt4.close();
                    }
                } catch (Throwable th33) {
                    if (stmt4 != null) {
                        try {
                            stmt4.close();
                        } catch (Throwable th34) {
                            th33.addSuppressed(th34);
                        }
                    }
                    throw th33;
                    break;
                }
            }
            this.removedUsers.clear();
        }
        if (!this.createdUsers.isEmpty()) {
            StringBuilder temp3 = new StringBuilder("INSERT INTO ");
            temp3.append(this.userTable);
            temp3.append('(').append(this.userNameCol);
            temp3.append(", ").append(this.userCredCol);
            if (this.userFullNameCol != null) {
                temp3.append(',').append(this.userFullNameCol);
            }
            temp3.append(") VALUES (?, ?");
            if (this.userFullNameCol != null) {
                temp3.append(", ?");
            }
            temp3.append(')');
            loop9: for (User user2 : this.createdUsers.values()) {
                try {
                    stmt3 = dbConnection.prepareStatement(temp3.toString());
                } catch (SQLException e18) {
                    log.error(sm.getString("dataSourceUserDatabase.exception"), e18);
                }
                try {
                    stmt3.setString(1, user2.getUsername());
                    stmt3.setString(2, user2.getPassword());
                    if (this.userFullNameCol != null) {
                        stmt3.setString(3, user2.getFullName());
                    }
                    stmt3.executeUpdate();
                    if (stmt3 != null) {
                        stmt3.close();
                    }
                    if (userRoleRelation != null) {
                        Iterator<Role> roles3 = user2.getRoles();
                        while (roles3.hasNext()) {
                            Role role7 = roles3.next();
                            try {
                                stmt2 = dbConnection.prepareStatement(userRoleRelation);
                            } catch (SQLException e19) {
                                log.error(sm.getString("dataSourceUserDatabase.exception"), e19);
                            }
                            try {
                                stmt2.setString(1, user2.getUsername());
                                stmt2.setString(2, role7.getRolename());
                                stmt2.executeUpdate();
                                if (stmt2 != null) {
                                    stmt2.close();
                                }
                            } catch (Throwable th35) {
                                if (stmt2 != null) {
                                    try {
                                        stmt2.close();
                                    } catch (Throwable th36) {
                                        th35.addSuppressed(th36);
                                    }
                                }
                                throw th35;
                                break loop9;
                            }
                        }
                    }
                    if (userGroupRelation != null) {
                        Iterator<Group> groups = user2.getGroups();
                        while (groups.hasNext()) {
                            Group group4 = groups.next();
                            try {
                                PreparedStatement stmt20 = dbConnection.prepareStatement(userGroupRelation);
                                try {
                                    stmt20.setString(1, user2.getUsername());
                                    stmt20.setString(2, group4.getGroupname());
                                    stmt20.executeUpdate();
                                    if (stmt20 != null) {
                                        stmt20.close();
                                    }
                                } catch (Throwable th37) {
                                    if (stmt20 != null) {
                                        try {
                                            stmt20.close();
                                        } catch (Throwable th38) {
                                            th37.addSuppressed(th38);
                                        }
                                    }
                                    throw th37;
                                    break loop9;
                                }
                            } catch (SQLException e20) {
                                log.error(sm.getString("dataSourceUserDatabase.exception"), e20);
                            }
                        }
                    }
                } catch (Throwable th39) {
                    if (stmt3 != null) {
                        try {
                            stmt3.close();
                        } catch (Throwable th40) {
                            th39.addSuppressed(th40);
                        }
                    }
                    throw th39;
                    break;
                }
            }
            this.createdUsers.clear();
        }
        if (!this.modifiedUsers.isEmpty()) {
            StringBuilder temp4 = new StringBuilder("UPDATE ");
            temp4.append(this.userTable);
            temp4.append(" SET ").append(this.userCredCol);
            temp4.append(" = ?");
            if (this.userFullNameCol != null) {
                temp4.append(", ").append(this.userFullNameCol).append(" = ?");
            }
            temp4.append(" WHERE ").append(this.userNameCol);
            temp4.append(" = ?");
            loop12: for (User user3 : this.modifiedUsers.values()) {
                try {
                    PreparedStatement stmt21 = dbConnection.prepareStatement(temp4.toString());
                    try {
                        stmt21.setString(1, user3.getPassword());
                        if (this.userFullNameCol != null) {
                            stmt21.setString(2, user3.getFullName());
                            stmt21.setString(3, user3.getUsername());
                        } else {
                            stmt21.setString(2, user3.getUsername());
                        }
                        stmt21.executeUpdate();
                        if (stmt21 != null) {
                            stmt21.close();
                        }
                    } catch (Throwable th41) {
                        if (stmt21 != null) {
                            try {
                                stmt21.close();
                            } catch (Throwable th42) {
                                th41.addSuppressed(th42);
                            }
                        }
                        throw th41;
                        break;
                    }
                } catch (SQLException e21) {
                    log.error(sm.getString("dataSourceUserDatabase.exception"), e21);
                }
                if (userRoleRelationDelete != null) {
                    try {
                        PreparedStatement stmt22 = dbConnection.prepareStatement(userRoleRelationDelete);
                        try {
                            stmt22.setString(1, user3.getUsername());
                            stmt22.executeUpdate();
                            if (stmt22 != null) {
                                stmt22.close();
                            }
                        } catch (Throwable th43) {
                            if (stmt22 != null) {
                                try {
                                    stmt22.close();
                                } catch (Throwable th44) {
                                    th43.addSuppressed(th44);
                                }
                            }
                            throw th43;
                            break;
                        }
                    } catch (SQLException e22) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e22);
                    }
                }
                if (userGroupRelationDelete != null) {
                    try {
                        PreparedStatement stmt23 = dbConnection.prepareStatement(userGroupRelationDelete);
                        try {
                            stmt23.setString(1, user3.getUsername());
                            stmt23.executeUpdate();
                            if (stmt23 != null) {
                                stmt23.close();
                            }
                        } catch (Throwable th45) {
                            if (stmt23 != null) {
                                try {
                                    stmt23.close();
                                } catch (Throwable th46) {
                                    th45.addSuppressed(th46);
                                }
                            }
                            throw th45;
                            break;
                        }
                    } catch (SQLException e23) {
                        log.error(sm.getString("dataSourceUserDatabase.exception"), e23);
                    }
                }
                if (userRoleRelation != null) {
                    Iterator<Role> roles4 = user3.getRoles();
                    while (roles4.hasNext()) {
                        Role role8 = roles4.next();
                        try {
                            PreparedStatement stmt24 = dbConnection.prepareStatement(userRoleRelation);
                            try {
                                stmt24.setString(1, user3.getUsername());
                                stmt24.setString(2, role8.getRolename());
                                stmt24.executeUpdate();
                                if (stmt24 != null) {
                                    stmt24.close();
                                }
                            } catch (Throwable th47) {
                                if (stmt24 != null) {
                                    try {
                                        stmt24.close();
                                    } catch (Throwable th48) {
                                        th47.addSuppressed(th48);
                                    }
                                }
                                throw th47;
                                break loop12;
                            }
                        } catch (SQLException e24) {
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e24);
                        }
                    }
                }
                if (userGroupRelation != null) {
                    Iterator<Group> groups2 = user3.getGroups();
                    while (groups2.hasNext()) {
                        Group group5 = groups2.next();
                        try {
                            PreparedStatement stmt25 = dbConnection.prepareStatement(userGroupRelation);
                            try {
                                stmt25.setString(1, user3.getUsername());
                                stmt25.setString(2, group5.getGroupname());
                                stmt25.executeUpdate();
                                if (stmt25 != null) {
                                    stmt25.close();
                                }
                            } catch (Throwable th49) {
                                if (stmt25 != null) {
                                    try {
                                        stmt25.close();
                                    } catch (Throwable th50) {
                                        th49.addSuppressed(th50);
                                    }
                                }
                                throw th49;
                                break loop12;
                            }
                        } catch (SQLException e25) {
                            log.error(sm.getString("dataSourceUserDatabase.exception"), e25);
                        }
                    }
                }
            }
            this.modifiedGroups.clear();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public boolean isAvailable() {
        return this.connectionSuccess;
    }

    protected boolean isGroupStoreDefined() {
        return (this.groupTable == null || this.userGroupTable == null || this.groupNameCol == null || this.groupRoleTable == null || !isRoleStoreDefined()) ? false : true;
    }

    protected boolean isRoleStoreDefined() {
        return (this.roleTable == null || this.userRoleTable == null || this.roleNameCol == null) ? false : true;
    }

    protected Connection openConnection() {
        if (this.dataSource == null) {
            return null;
        }
        try {
            Connection connection = this.dataSource.getConnection();
            this.connectionSuccess = true;
            return connection;
        } catch (Exception e) {
            this.connectionSuccess = false;
            log.error(sm.getString("dataSourceUserDatabase.exception"), e);
            return null;
        }
    }

    protected void closeConnection(Connection dbConnection) {
        if (dbConnection == null) {
            return;
        }
        try {
            if (!dbConnection.getAutoCommit()) {
                dbConnection.commit();
            }
        } catch (SQLException e) {
            log.error(sm.getString("dataSourceUserDatabase.exception"), e);
        }
        try {
            dbConnection.close();
        } catch (SQLException e2) {
            log.error(sm.getString("dataSourceUserDatabase.exception"), e2);
        }
    }
}
