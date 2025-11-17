package org.springframework.jmx.export;

import javax.management.ObjectName;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/MBeanExporterListener.class */
public interface MBeanExporterListener {
    void mbeanRegistered(ObjectName objectName);

    void mbeanUnregistered(ObjectName objectName);
}
