package org.apache.catalina.users;

import org.apache.catalina.Role;
import org.apache.catalina.UserDatabase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/AbstractRole.class */
public abstract class AbstractRole implements Role {
    protected String description = null;
    protected String rolename = null;

    @Override // org.apache.catalina.Role
    public abstract UserDatabase getUserDatabase();

    @Override // org.apache.catalina.Role
    public String getDescription() {
        return this.description;
    }

    @Override // org.apache.catalina.Role
    public void setDescription(String description) {
        this.description = description;
    }

    @Override // org.apache.catalina.Role
    public String getRolename() {
        return this.rolename;
    }

    @Override // org.apache.catalina.Role
    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    @Override // java.security.Principal
    public String getName() {
        return getRolename();
    }
}
