package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/naming/ObjectNamingStrategy.class */
public interface ObjectNamingStrategy {
    ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException;
}
