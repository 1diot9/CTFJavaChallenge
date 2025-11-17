package org.apache.catalina.users;

import org.apache.catalina.UserDatabase;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/users/SparseUserDatabase.class */
public abstract class SparseUserDatabase implements UserDatabase {
    @Override // org.apache.catalina.UserDatabase
    public boolean isSparse() {
        return true;
    }
}
