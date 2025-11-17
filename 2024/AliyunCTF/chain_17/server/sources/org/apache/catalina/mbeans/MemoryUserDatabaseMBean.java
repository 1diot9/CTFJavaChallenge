package org.apache.catalina.mbeans;

import org.apache.tomcat.util.modeler.ManagedBean;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/mbeans/MemoryUserDatabaseMBean.class */
public class MemoryUserDatabaseMBean extends SparseUserDatabaseMBean {
    protected final ManagedBean managed = this.registry.findManagedBean("MemoryUserDatabase");
}
