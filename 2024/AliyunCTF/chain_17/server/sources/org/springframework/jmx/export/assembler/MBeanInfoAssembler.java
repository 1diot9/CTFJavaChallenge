package org.springframework.jmx.export.assembler;

import javax.management.JMException;
import javax.management.modelmbean.ModelMBeanInfo;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/MBeanInfoAssembler.class */
public interface MBeanInfoAssembler {
    ModelMBeanInfo getMBeanInfo(Object managedBean, String beanKey) throws JMException;
}
