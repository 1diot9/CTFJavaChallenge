package org.apache.catalina.users;

import org.apache.catalina.Role;
import org.apache.tomcat.util.digester.AbstractObjectCreationFactory;
import org.xml.sax.Attributes;

/* compiled from: MemoryUserDatabase.java */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/MemoryRoleCreationFactory.class */
class MemoryRoleCreationFactory extends AbstractObjectCreationFactory {
    private final MemoryUserDatabase database;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MemoryRoleCreationFactory(MemoryUserDatabase database) {
        this.database = database;
    }

    @Override // org.apache.tomcat.util.digester.AbstractObjectCreationFactory, org.apache.tomcat.util.digester.ObjectCreationFactory
    public Object createObject(Attributes attributes) {
        String rolename = attributes.getValue("rolename");
        if (rolename == null) {
            rolename = attributes.getValue("name");
        }
        String description = attributes.getValue("description");
        Role existingRole = this.database.findRole(rolename);
        if (existingRole == null) {
            return this.database.createRole(rolename, description);
        }
        if (existingRole.getDescription() == null) {
            existingRole.setDescription(description);
        }
        return existingRole;
    }
}
