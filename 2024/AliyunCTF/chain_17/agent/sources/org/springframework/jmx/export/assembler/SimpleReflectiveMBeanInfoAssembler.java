package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jmx/export/assembler/SimpleReflectiveMBeanInfoAssembler.class */
public class SimpleReflectiveMBeanInfoAssembler extends AbstractConfigurableMBeanInfoAssembler {
    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return true;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return true;
    }

    @Override // org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler
    protected boolean includeOperation(Method method, String beanKey) {
        return true;
    }
}
