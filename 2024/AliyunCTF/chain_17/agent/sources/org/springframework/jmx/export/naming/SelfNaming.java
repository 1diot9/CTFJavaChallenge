package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/naming/SelfNaming.class */
public interface SelfNaming {
    ObjectName getObjectName() throws MalformedObjectNameException;
}
