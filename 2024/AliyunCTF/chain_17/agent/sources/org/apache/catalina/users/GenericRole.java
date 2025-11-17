package org.apache.catalina.users;

import org.apache.catalina.UserDatabase;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/GenericRole.class */
public class GenericRole<UD extends UserDatabase> extends AbstractRole {
    protected final UserDatabase database;

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenericRole(UD database, String rolename, String description) {
        this.database = database;
        this.rolename = rolename;
        this.description = description;
    }

    @Override // org.apache.catalina.users.AbstractRole, org.apache.catalina.Role
    public UserDatabase getUserDatabase() {
        return this.database;
    }

    @Override // org.apache.catalina.users.AbstractRole, org.apache.catalina.Role
    public void setDescription(String description) {
        this.database.modifiedRole(this);
        super.setDescription(description);
    }

    @Override // org.apache.catalina.users.AbstractRole, org.apache.catalina.Role
    public void setRolename(String rolename) {
        this.database.modifiedRole(this);
        super.setRolename(rolename);
    }

    @Override // java.security.Principal
    public boolean equals(Object obj) {
        if (obj instanceof GenericRole) {
            GenericRole<?> role = (GenericRole) obj;
            return role.database == this.database && this.rolename.equals(role.getRolename());
        }
        return super.equals(obj);
    }

    @Override // java.security.Principal
    public int hashCode() {
        int result = (31 * 1) + (this.database == null ? 0 : this.database.hashCode());
        return (31 * result) + (this.rolename == null ? 0 : this.rolename.hashCode());
    }
}
