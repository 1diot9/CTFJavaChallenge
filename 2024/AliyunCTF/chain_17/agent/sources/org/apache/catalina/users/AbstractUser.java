package org.apache.catalina.users;

import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/AbstractUser.class */
public abstract class AbstractUser implements User {
    protected String fullName = null;
    protected String password = null;
    protected String username = null;

    @Override // org.apache.catalina.User
    public abstract Iterator<Group> getGroups();

    @Override // org.apache.catalina.User
    public abstract Iterator<Role> getRoles();

    @Override // org.apache.catalina.User
    public abstract void addGroup(Group group);

    @Override // org.apache.catalina.User
    public abstract void addRole(Role role);

    @Override // org.apache.catalina.User
    public abstract boolean isInGroup(Group group);

    @Override // org.apache.catalina.User
    public abstract boolean isInRole(Role role);

    @Override // org.apache.catalina.User
    public abstract void removeGroup(Group group);

    @Override // org.apache.catalina.User
    public abstract void removeGroups();

    @Override // org.apache.catalina.User
    public abstract void removeRole(Role role);

    @Override // org.apache.catalina.User
    public abstract void removeRoles();

    @Override // org.apache.catalina.User
    public String getFullName() {
        return this.fullName;
    }

    @Override // org.apache.catalina.User
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override // org.apache.catalina.User
    public String getPassword() {
        return this.password;
    }

    @Override // org.apache.catalina.User
    public void setPassword(String password) {
        this.password = password;
    }

    @Override // org.apache.catalina.User
    public String getUsername() {
        return this.username;
    }

    @Override // org.apache.catalina.User
    public void setUsername(String username) {
        this.username = username;
    }

    @Override // java.security.Principal
    public String getName() {
        return getUsername();
    }
}
