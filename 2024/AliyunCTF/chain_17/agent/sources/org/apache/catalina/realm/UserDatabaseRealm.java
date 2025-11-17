package org.apache.catalina.realm;

import java.io.ObjectStreamException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.naming.Context;
import org.apache.catalina.Group;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Role;
import org.apache.catalina.Server;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.naming.ContextBindings;
import org.apache.tomcat.util.ExceptionUtils;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/UserDatabaseRealm.class */
public class UserDatabaseRealm extends RealmBase {
    protected volatile UserDatabase database = null;
    private final Object databaseLock = new Object();
    protected String resourceName = "UserDatabase";
    private boolean localJndiResource = false;
    private boolean useStaticPrincipal = false;

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean getUseStaticPrincipal() {
        return this.useStaticPrincipal;
    }

    public void setUseStaticPrincipal(boolean useStaticPrincipal) {
        this.useStaticPrincipal = useStaticPrincipal;
    }

    public boolean getLocalJndiResource() {
        return this.localJndiResource;
    }

    public void setLocalJndiResource(boolean localJndiResource) {
        this.localJndiResource = localJndiResource;
    }

    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.Realm
    public void backgroundProcess() {
        UserDatabase database = getUserDatabase();
        if (database != null) {
            database.backgroundProcess();
        }
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        User user;
        UserDatabase database = getUserDatabase();
        if (database == null || (user = database.findUser(username)) == null) {
            return null;
        }
        return user.getPassword();
    }

    public static String[] getRoles(User user) {
        Set<String> roles = new HashSet<>();
        Iterator<Role> uroles = user.getRoles();
        while (uroles.hasNext()) {
            Role role = uroles.next();
            roles.add(role.getName());
        }
        Iterator<Group> groups = user.getGroups();
        while (groups.hasNext()) {
            Group group = groups.next();
            Iterator<Role> uroles2 = group.getRoles();
            while (uroles2.hasNext()) {
                Role role2 = uroles2.next();
                roles.add(role2.getName());
            }
        }
        return (String[]) roles.toArray(new String[0]);
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        User user;
        UserDatabase database = getUserDatabase();
        if (database == null || (user = database.findUser(username)) == null) {
            return null;
        }
        if (this.useStaticPrincipal) {
            return new GenericPrincipal(username, Arrays.asList(getRoles(user)));
        }
        return new UserDatabasePrincipal(user, database);
    }

    private UserDatabase getUserDatabase() {
        Context context;
        if (this.database == null) {
            synchronized (this.databaseLock) {
                if (this.database == null) {
                    try {
                        if (this.localJndiResource) {
                            Context context2 = ContextBindings.getClassLoader();
                            context = (Context) context2.lookup("comp/env");
                        } else {
                            Server server = getServer();
                            if (server == null) {
                                this.containerLog.error(sm.getString("userDatabaseRealm.noNamingContext"));
                                return null;
                            }
                            context = getServer().getGlobalNamingContext();
                        }
                        this.database = (UserDatabase) context.lookup(this.resourceName);
                    } catch (Throwable e) {
                        ExceptionUtils.handleThrowable(e);
                        if (this.containerLog != null) {
                            this.containerLog.error(sm.getString("userDatabaseRealm.lookup", this.resourceName), e);
                        }
                        this.database = null;
                    }
                }
            }
        }
        return this.database;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void startInternal() throws LifecycleException {
        if (!this.localJndiResource) {
            UserDatabase database = getUserDatabase();
            if (database == null) {
                throw new LifecycleException(sm.getString("userDatabaseRealm.noDatabase", this.resourceName));
            }
        }
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.realm.RealmBase, org.apache.catalina.util.LifecycleBase
    public void stopInternal() throws LifecycleException {
        super.stopInternal();
        this.database = null;
    }

    @Override // org.apache.catalina.Realm
    public boolean isAvailable() {
        if (this.database == null) {
            return false;
        }
        return this.database.isAvailable();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/UserDatabaseRealm$UserDatabasePrincipal.class */
    public static final class UserDatabasePrincipal extends GenericPrincipal {
        private static final long serialVersionUID = 1;
        private final transient UserDatabase database;

        public UserDatabasePrincipal(User user, UserDatabase database) {
            super(user.getName());
            this.database = database;
        }

        @Override // org.apache.catalina.realm.GenericPrincipal
        public String[] getRoles() {
            if (this.database == null) {
                return new String[0];
            }
            User user = this.database.findUser(this.name);
            if (user == null) {
                return new String[0];
            }
            Set<String> roles = new HashSet<>();
            Iterator<Role> uroles = user.getRoles();
            while (uroles.hasNext()) {
                Role role = uroles.next();
                roles.add(role.getName());
            }
            Iterator<Group> groups = user.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                Iterator<Role> uroles2 = group.getRoles();
                while (uroles2.hasNext()) {
                    Role role2 = uroles2.next();
                    roles.add(role2.getName());
                }
            }
            return (String[]) roles.toArray(new String[0]);
        }

        @Override // org.apache.catalina.realm.GenericPrincipal
        public boolean hasRole(String role) {
            User user;
            if ("*".equals(role)) {
                return true;
            }
            if (role == null) {
                return false;
            }
            if (this.database == null) {
                return super.hasRole(role);
            }
            Role dbrole = this.database.findRole(role);
            if (dbrole == null || (user = this.database.findUser(this.name)) == null) {
                return false;
            }
            if (user.isInRole(dbrole)) {
                return true;
            }
            Iterator<Group> groups = user.getGroups();
            while (groups.hasNext()) {
                Group group = groups.next();
                if (group.isInRole(dbrole)) {
                    return true;
                }
            }
            return false;
        }

        private Object writeReplace() throws ObjectStreamException {
            return new GenericPrincipal(getName(), Arrays.asList(getRoles()));
        }
    }
}
