package org.apache.catalina.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/MemoryUserDatabase.class */
public class MemoryUserDatabase implements UserDatabase {
    private static final Log log = LogFactory.getLog((Class<?>) MemoryUserDatabase.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) MemoryUserDatabase.class);
    protected final Map<String, Group> groups;
    protected final String id;
    protected String pathname;
    protected String pathnameOld;
    protected String pathnameNew;
    protected boolean readonly;
    protected final Map<String, Role> roles;
    protected final Map<String, User> users;
    private final ReentrantReadWriteLock dbLock;
    private final Lock readLock;
    private final Lock writeLock;
    private volatile long lastModified;
    private boolean watchSource;

    public MemoryUserDatabase() {
        this(null);
    }

    public MemoryUserDatabase(String id) {
        this.groups = new ConcurrentHashMap();
        this.pathname = "conf/tomcat-users.xml";
        this.pathnameOld = this.pathname + ".old";
        this.pathnameNew = this.pathname + ".new";
        this.readonly = true;
        this.roles = new ConcurrentHashMap();
        this.users = new ConcurrentHashMap();
        this.dbLock = new ReentrantReadWriteLock();
        this.readLock = this.dbLock.readLock();
        this.writeLock = this.dbLock.writeLock();
        this.lastModified = 0L;
        this.watchSource = true;
        this.id = id;
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<Group> getGroups() {
        this.readLock.lock();
        try {
            return new ArrayList(this.groups.values()).iterator();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public String getId() {
        return this.id;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
        this.pathnameOld = pathname + ".old";
        this.pathnameNew = pathname + ".new";
    }

    public boolean getReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean getWatchSource() {
        return this.watchSource;
    }

    public void setWatchSource(boolean watchSource) {
        this.watchSource = watchSource;
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<Role> getRoles() {
        this.readLock.lock();
        try {
            return new ArrayList(this.roles.values()).iterator();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Iterator<User> getUsers() {
        this.readLock.lock();
        try {
            return new ArrayList(this.users.values()).iterator();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void close() throws Exception {
        this.writeLock.lock();
        try {
            save();
            this.users.clear();
            this.groups.clear();
            this.roles.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Group createGroup(String groupname, String description) {
        if (groupname == null || groupname.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullGroup");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        Group group = new GenericGroup(this, groupname, description, null);
        this.readLock.lock();
        try {
            this.groups.put(group.getGroupname(), group);
            this.readLock.unlock();
            return group;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Role createRole(String rolename, String description) {
        if (rolename == null || rolename.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullRole");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        Role role = new GenericRole(this, rolename, description);
        this.readLock.lock();
        try {
            this.roles.put(role.getRolename(), role);
            this.readLock.unlock();
            return role;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public User createUser(String username, String password, String fullName) {
        if (username == null || username.length() == 0) {
            String msg = sm.getString("memoryUserDatabase.nullUser");
            log.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        User user = new GenericUser(this, username, password, fullName, null, null);
        this.readLock.lock();
        try {
            this.users.put(user.getUsername(), user);
            this.readLock.unlock();
            return user;
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Group findGroup(String groupname) {
        this.readLock.lock();
        try {
            return this.groups.get(groupname);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public Role findRole(String rolename) {
        this.readLock.lock();
        try {
            return this.roles.get(rolename);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public User findUser(String username) {
        this.readLock.lock();
        try {
            return this.users.get(username);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void open() throws Exception {
        ConfigurationSource.Resource resource;
        this.writeLock.lock();
        try {
            this.users.clear();
            this.groups.clear();
            this.roles.clear();
            String pathName = getPathname();
            try {
                try {
                    resource = ConfigFileLoader.getSource().getResource(pathName);
                } catch (IOException e) {
                    log.error(sm.getString("memoryUserDatabase.fileNotFound", pathName));
                }
                try {
                    this.lastModified = resource.getLastModified();
                    Digester digester = new Digester();
                    try {
                        digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
                    } catch (Exception e2) {
                        log.warn(sm.getString("memoryUserDatabase.xmlFeatureEncoding"), e2);
                    }
                    digester.addFactoryCreate("tomcat-users/group", new MemoryGroupCreationFactory(this), true);
                    digester.addFactoryCreate("tomcat-users/role", new MemoryRoleCreationFactory(this), true);
                    digester.addFactoryCreate("tomcat-users/user", new MemoryUserCreationFactory(this), true);
                    digester.parse(resource.getInputStream());
                    if (resource != null) {
                        resource.close();
                    }
                } catch (Throwable th) {
                    if (resource != null) {
                        try {
                            resource.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Exception e3) {
                this.users.clear();
                this.groups.clear();
                this.roles.clear();
                throw e3;
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeGroup(Group group) {
        this.readLock.lock();
        try {
            Iterator<User> users = getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeGroup(group);
            }
            this.groups.remove(group.getGroupname());
            this.readLock.unlock();
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeRole(Role role) {
        this.readLock.lock();
        try {
            Iterator<Group> groups = getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                group.removeRole(role);
            }
            Iterator<User> users = getUsers();
            while (users.hasNext()) {
                User user = users.next();
                user.removeRole(role);
            }
            this.roles.remove(role.getRolename());
            this.readLock.unlock();
        } catch (Throwable th) {
            this.readLock.unlock();
            throw th;
        }
    }

    @Override // org.apache.catalina.UserDatabase
    public void removeUser(User user) {
        this.readLock.lock();
        try {
            this.users.remove(user.getUsername());
        } finally {
            this.readLock.unlock();
        }
    }

    public boolean isWritable() {
        File file = new File(this.pathname);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("catalina.base"), this.pathname);
        }
        File dir = file.getParentFile();
        return dir.exists() && dir.isDirectory() && dir.canWrite();
    }

    @Override // org.apache.catalina.UserDatabase
    public void save() throws Exception {
        if (getReadonly()) {
            log.error(sm.getString("memoryUserDatabase.readOnly"));
            return;
        }
        if (!isWritable()) {
            log.warn(sm.getString("memoryUserDatabase.notPersistable"));
            return;
        }
        File fileNew = new File(this.pathnameNew);
        if (!fileNew.isAbsolute()) {
            fileNew = new File(System.getProperty("catalina.base"), this.pathnameNew);
        }
        this.writeLock.lock();
        try {
            try {
                FileOutputStream fos = new FileOutputStream(fileNew);
                try {
                    OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                    try {
                        PrintWriter writer = new PrintWriter(osw);
                        try {
                            writer.println("<?xml version='1.0' encoding='utf-8'?>");
                            writer.println("<tomcat-users xmlns=\"http://tomcat.apache.org/xml\"");
                            writer.print("              ");
                            writer.println("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
                            writer.print("              ");
                            writer.println("xsi:schemaLocation=\"http://tomcat.apache.org/xml tomcat-users.xsd\"");
                            writer.println("              version=\"1.0\">");
                            Iterator<?> values = getRoles();
                            while (values.hasNext()) {
                                Role role = values.next();
                                writer.print("  <role rolename=\"");
                                writer.print(Escape.xml(role.getRolename()));
                                writer.print("\"");
                                if (null != role.getDescription()) {
                                    writer.print(" description=\"");
                                    writer.print(Escape.xml(role.getDescription()));
                                    writer.print("\"");
                                }
                                writer.println("/>");
                            }
                            Iterator<?> values2 = getGroups();
                            while (values2.hasNext()) {
                                Group group = values2.next();
                                writer.print("  <group groupname=\"");
                                writer.print(Escape.xml(group.getName()));
                                writer.print("\"");
                                if (null != group.getDescription()) {
                                    writer.print(" description=\"");
                                    writer.print(Escape.xml(group.getDescription()));
                                    writer.print("\"");
                                }
                                writer.print(" roles=\"");
                                Iterator<Role> roles = group.getRoles();
                                while (roles.hasNext()) {
                                    Role role2 = roles.next();
                                    writer.print(Escape.xml(role2.getRolename()));
                                    if (roles.hasNext()) {
                                        writer.print(',');
                                    }
                                }
                                writer.println("\"/>");
                            }
                            Iterator<?> values3 = getUsers();
                            while (values3.hasNext()) {
                                User user = values3.next();
                                writer.print("  <user username=\"");
                                writer.print(Escape.xml(user.getUsername()));
                                writer.print("\" password=\"");
                                writer.print(Escape.xml(user.getPassword()));
                                writer.print("\"");
                                if (null != user.getFullName()) {
                                    writer.print(" fullName=\"");
                                    writer.print(Escape.xml(user.getFullName()));
                                    writer.print("\"");
                                }
                                writer.print(" groups=\"");
                                Iterator<Group> groups = user.getGroups();
                                while (groups.hasNext()) {
                                    writer.print(Escape.xml(groups.next().getGroupname()));
                                    if (groups.hasNext()) {
                                        writer.print(',');
                                    }
                                }
                                writer.print("\" roles=\"");
                                Iterator<Role> roles2 = user.getRoles();
                                while (roles2.hasNext()) {
                                    Role role3 = roles2.next();
                                    writer.print(Escape.xml(role3.getRolename()));
                                    if (roles2.hasNext()) {
                                        writer.print(',');
                                    }
                                }
                                writer.print("\"/>");
                            }
                            writer.println("</tomcat-users>");
                            if (writer.checkError()) {
                                throw new IOException(sm.getString("memoryUserDatabase.writeException", fileNew.getAbsolutePath()));
                            }
                            writer.close();
                            osw.close();
                            fos.close();
                            this.lastModified = fileNew.lastModified();
                            this.writeLock.unlock();
                            File fileOld = new File(this.pathnameOld);
                            if (!fileOld.isAbsolute()) {
                                fileOld = new File(System.getProperty("catalina.base"), this.pathnameOld);
                            }
                            if (fileOld.exists() && !fileOld.delete()) {
                                throw new IOException(sm.getString("memoryUserDatabase.fileDelete", fileOld));
                            }
                            File fileOrig = new File(this.pathname);
                            if (!fileOrig.isAbsolute()) {
                                fileOrig = new File(System.getProperty("catalina.base"), this.pathname);
                            }
                            if (fileOrig.exists() && !fileOrig.renameTo(fileOld)) {
                                throw new IOException(sm.getString("memoryUserDatabase.renameOld", fileOld.getAbsolutePath()));
                            }
                            if (fileNew.renameTo(fileOrig)) {
                                if (fileOld.exists() && !fileOld.delete()) {
                                    throw new IOException(sm.getString("memoryUserDatabase.fileDelete", fileOld));
                                }
                            } else {
                                if (fileOld.exists() && !fileOld.renameTo(fileOrig)) {
                                    log.warn(sm.getString("memoryUserDatabase.restoreOrig", fileOld));
                                }
                                throw new IOException(sm.getString("memoryUserDatabase.renameNew", fileOrig.getAbsolutePath()));
                            }
                        } catch (Throwable th) {
                            try {
                                writer.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        try {
                            osw.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                        throw th3;
                    }
                } catch (Throwable th5) {
                    try {
                        fos.close();
                    } catch (Throwable th6) {
                        th5.addSuppressed(th6);
                    }
                    throw th5;
                }
            } catch (IOException e) {
                if (fileNew.exists() && !fileNew.delete()) {
                    log.warn(sm.getString("memoryUserDatabase.fileDelete", fileNew));
                }
                throw e;
            }
        } catch (Throwable th7) {
            this.writeLock.unlock();
            throw th7;
        }
    }

    /* JADX WARN: Finally extract failed */
    @Override // org.apache.catalina.UserDatabase
    public void backgroundProcess() {
        if (this.watchSource) {
            URI uri = ConfigFileLoader.getSource().getURI(getPathname());
            URLConnection uConn = null;
            try {
                try {
                    URL url = uri.toURL();
                    URLConnection uConn2 = url.openConnection();
                    if (this.lastModified != uConn2.getLastModified()) {
                        this.writeLock.lock();
                        try {
                            long detectedLastModified = uConn2.getLastModified();
                            if (this.lastModified != detectedLastModified && detectedLastModified + ExponentialBackOff.DEFAULT_INITIAL_INTERVAL < System.currentTimeMillis()) {
                                log.info(sm.getString("memoryUserDatabase.reload", this.id, uri));
                                open();
                            }
                            this.writeLock.unlock();
                        } catch (Throwable th) {
                            this.writeLock.unlock();
                            throw th;
                        }
                    }
                    if (uConn2 != null) {
                        try {
                            uConn2.getInputStream().close();
                        } catch (FileNotFoundException e) {
                            this.lastModified = 0L;
                        } catch (IOException ioe) {
                            log.warn(sm.getString("memoryUserDatabase.fileClose", this.pathname), ioe);
                        }
                    }
                } catch (Exception ioe2) {
                    log.error(sm.getString("memoryUserDatabase.reloadError", this.id, uri), ioe2);
                    if (0 != 0) {
                        try {
                            uConn.getInputStream().close();
                        } catch (FileNotFoundException e2) {
                            this.lastModified = 0L;
                        } catch (IOException ioe3) {
                            log.warn(sm.getString("memoryUserDatabase.fileClose", this.pathname), ioe3);
                        }
                    }
                }
            } catch (Throwable th2) {
                if (0 != 0) {
                    try {
                        uConn.getInputStream().close();
                    } catch (FileNotFoundException e3) {
                        this.lastModified = 0L;
                    } catch (IOException ioe4) {
                        log.warn(sm.getString("memoryUserDatabase.fileClose", this.pathname), ioe4);
                    }
                }
                throw th2;
            }
        }
    }

    public String toString() {
        return "MemoryUserDatabase[id=" + this.id + ",pathname=" + this.pathname + ",groupCount=" + this.groups.size() + ",roleCount=" + this.roles.size() + ",userCount=" + this.users.size() + ']';
    }
}
