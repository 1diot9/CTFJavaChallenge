package org.springframework.aop.target;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/SimpleBeanTargetSource.class */
public class SimpleBeanTargetSource extends AbstractBeanFactoryBasedTargetSource {
    @Override // org.springframework.aop.TargetSource
    public Object getTarget() throws Exception {
        return getBeanFactory().getBean(getTargetBeanName());
    }
}
