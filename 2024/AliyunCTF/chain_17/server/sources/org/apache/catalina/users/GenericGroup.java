package org.apache.catalina.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/GenericGroup.class */
public class GenericGroup<UD extends UserDatabase> extends AbstractGroup {
    protected final UD database;
    protected final CopyOnWriteArrayList<Role> roles = new CopyOnWriteArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenericGroup(UD database, String groupname, String description, List<Role> roles) {
        this.database = database;
        this.groupname = groupname;
        this.description = description;
        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public Iterator<Role> getRoles() {
        return this.roles.iterator();
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public Iterator<User> getUsers() {
        List<User> results = new ArrayList<>();
        Iterator<User> users = this.database.getUsers();
        while (users.hasNext()) {
            User user = users.next();
            if (user.isInGroup(this)) {
                results.add(user);
            }
        }
        return results.iterator();
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public void addRole(Role role) {
        if (this.roles.addIfAbsent(role)) {
            this.database.modifiedGroup(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public boolean isInRole(Role role) {
        return this.roles.contains(role);
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public void removeRole(Role role) {
        if (this.roles.remove(role)) {
            this.database.modifiedGroup(this);
        }
    }

    @Override // org.apache.catalina.users.AbstractGroup, org.apache.catalina.Group
    public void removeRoles() {
        if (!this.roles.isEmpty()) {
            this.roles.clear();
            this.database.modifiedGroup(this);
        }
    }

    @Override // java.security.Principal
    public boolean equals(Object obj) {
        if (obj instanceof GenericGroup) {
            GenericGroup<?> group = (GenericGroup) obj;
            return group.database == this.database && this.groupname.equals(group.getGroupname());
        }
        return super.equals(obj);
    }

    @Override // java.security.Principal
    public int hashCode() {
        int result = (31 * 1) + (this.database == null ? 0 : this.database.hashCode());
        return (31 * result) + (this.groupname == null ? 0 : this.groupname.hashCode());
    }
}
