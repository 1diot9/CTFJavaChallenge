package org.apache.catalina.users;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/GenericUser.class */
public class GenericUser<UD extends UserDatabase> extends AbstractUser {
    protected final UD database;
    protected final CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();
    protected final CopyOnWriteArrayList<Role> roles = new CopyOnWriteArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenericUser(UD database, String username, String password, String fullName, List<Group> groups, List<Role> roles) {
        this.database = database;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        if (groups != null) {
            this.groups.addAll(groups);
        }
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public Iterator<Group> getGroups() {
        return this.groups.iterator();
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public Iterator<Role> getRoles() {
        return this.roles.iterator();
    }

    @Override // org.apache.catalina.User
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void addGroup(Group group) {
        if (this.groups.addIfAbsent(group)) {
            this.database.modifiedUser(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void addRole(Role role) {
        if (this.roles.addIfAbsent(role)) {
            this.database.modifiedUser(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public boolean isInGroup(Group group) {
        return this.groups.contains(group);
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public boolean isInRole(Role role) {
        return this.roles.contains(role);
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeGroup(Group group) {
        if (this.groups.remove(group)) {
            this.database.modifiedUser(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeGroups() {
        if (!this.groups.isEmpty()) {
            this.groups.clear();
            this.database.modifiedUser(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeRole(Role role) {
        if (this.roles.remove(role)) {
            this.database.modifiedUser(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void removeRoles() {
        if (!this.roles.isEmpty()) {
            this.database.modifiedUser(this);
        }
        this.roles.clear();
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void setFullName(String fullName) {
        this.database.modifiedUser(this);
        super.setFullName(fullName);
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void setPassword(String password) {
        this.database.modifiedUser(this);
        super.setPassword(password);
    }

    @Override // org.apache.catalina.users.AbstractUser, org.apache.catalina.User
    public void setUsername(String username) {
        this.database.modifiedUser(this);
        super.setUsername(username);
    }

    @Override // java.security.Principal
    public boolean equals(Object obj) {
        if (obj instanceof GenericUser) {
            GenericUser<?> user = (GenericUser) obj;
            return user.database == this.database && this.username.equals(user.getUsername());
        }
        return super.equals(obj);
    }

    @Override // java.security.Principal
    public int hashCode() {
        int result = (31 * 1) + (this.database == null ? 0 : this.database.hashCode());
        return (31 * result) + (this.username == null ? 0 : this.username.hashCode());
    }
}
