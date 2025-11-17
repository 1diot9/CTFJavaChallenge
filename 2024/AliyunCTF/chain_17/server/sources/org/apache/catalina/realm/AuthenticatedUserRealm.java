package org.apache.catalina.realm;

import java.security.Principal;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/realm/AuthenticatedUserRealm.class */
public class AuthenticatedUserRealm extends RealmBase {
    @Override // org.apache.catalina.realm.RealmBase
    protected String getPassword(String username) {
        return null;
    }

    @Override // org.apache.catalina.realm.RealmBase
    protected Principal getPrincipal(String username) {
        return new GenericPrincipal(username);
    }
}
