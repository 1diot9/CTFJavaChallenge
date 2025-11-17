package org.springframework.jmx.export;

import javax.management.ObjectName;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/MBeanExportOperations.class */
public interface MBeanExportOperations {
    ObjectName registerManagedResource(Object managedResource) throws MBeanExportException;

    void registerManagedResource(Object managedResource, ObjectName objectName) throws MBeanExportException;

    void unregisterManagedResource(ObjectName objectName);
}
