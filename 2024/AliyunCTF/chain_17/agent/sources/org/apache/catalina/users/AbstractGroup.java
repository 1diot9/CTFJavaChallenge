package org.apache.catalina.users;

import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/AbstractGroup.class */
public abstract class AbstractGroup implements Group {
    protected String description = null;
    protected String groupname = null;

    @Override // org.apache.catalina.Group
    public abstract Iterator<Role> getRoles();

    @Override // org.apache.catalina.Group
    public abstract UserDatabase getUserDatabase();

    @Override // org.apache.catalina.Group
    public abstract Iterator<User> getUsers();

    @Override // org.apache.catalina.Group
    public abstract void addRole(Role role);

    @Override // org.apache.catalina.Group
    public abstract boolean isInRole(Role role);

    @Override // org.apache.catalina.Group
    public abstract void removeRole(Role role);

    @Override // org.apache.catalina.Group
    public abstract void removeRoles();

    @Override // org.apache.catalina.Group
    public String getDescription() {
        return this.description;
    }

    @Override // org.apache.catalina.Group
    public void setDescription(String description) {
        this.description = description;
    }

    @Override // org.apache.catalina.Group
    public String getGroupname() {
        return this.groupname;
    }

    @Override // org.apache.catalina.Group
    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    @Override // java.security.Principal
    public String getName() {
        return getGroupname();
    }
}
