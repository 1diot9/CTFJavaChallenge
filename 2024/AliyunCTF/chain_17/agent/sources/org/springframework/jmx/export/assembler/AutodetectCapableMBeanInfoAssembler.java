package org.springframework.jmx.export.assembler;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/AutodetectCapableMBeanInfoAssembler.class */
public interface AutodetectCapableMBeanInfoAssembler extends MBeanInfoAssembler {
    boolean includeBean(Class<?> beanClass, String beanName);
}
