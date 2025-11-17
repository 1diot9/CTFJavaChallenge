package org.apache.catalina.session;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/session/PersistentManager.class */
public final class PersistentManager extends PersistentManagerBase {
    private static final String name = "PersistentManager";

    @Override // org.apache.catalina.session.PersistentManagerBase, org.apache.catalina.session.ManagerBase
    public String getName() {
        return name;
    }
}
