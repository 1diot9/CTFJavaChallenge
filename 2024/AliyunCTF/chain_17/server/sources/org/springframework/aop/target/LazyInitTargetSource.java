package org.springframework.aop.target;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/LazyInitTargetSource.class */
public class LazyInitTargetSource extends AbstractBeanFactoryBasedTargetSource {

    @Nullable
    private Object target;

    @Override // org.springframework.aop.TargetSource
    public synchronized Object getTarget() throws BeansException {
        if (this.target == null) {
            this.target = getBeanFactory().getBean(getTargetBeanName());
            postProcessTargetObject(this.target);
        }
        return this.target;
    }

    protected void postProcessTargetObject(Object targetObject) {
    }
}
