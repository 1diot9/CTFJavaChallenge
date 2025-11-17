package org.apache.catalina;

import javax.management.MBeanRegistration;
import javax.management.ObjectName;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/JmxEnabled.class */
public interface JmxEnabled extends MBeanRegistration {
    String getDomain();

    void setDomain(String str);

    ObjectName getObjectName();
}
