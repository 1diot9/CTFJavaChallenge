package org.apache.catalina;

import java.security.Principal;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/Role.class */
public interface Role extends Principal {
    String getDescription();

    void setDescription(String str);

    String getRolename();

    void setRolename(String str);

    UserDatabase getUserDatabase();
}
