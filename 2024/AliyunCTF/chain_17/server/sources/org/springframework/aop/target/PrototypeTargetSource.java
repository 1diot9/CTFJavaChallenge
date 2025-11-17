package org.springframework.aop.target;

import org.springframework.beans.BeansException;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/PrototypeTargetSource.class */
public class PrototypeTargetSource extends AbstractPrototypeBasedTargetSource {
    @Override // org.springframework.aop.TargetSource
    public Object getTarget() throws BeansException {
        return newPrototypeInstance();
    }

    @Override // org.springframework.aop.TargetSource
    public void releaseTarget(Object target) {
        destroyPrototypeInstance(target);
    }

    @Override // org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource
    public String toString() {
        return "PrototypeTargetSource for target bean with name '" + getTargetBeanName() + "'";
    }
}
