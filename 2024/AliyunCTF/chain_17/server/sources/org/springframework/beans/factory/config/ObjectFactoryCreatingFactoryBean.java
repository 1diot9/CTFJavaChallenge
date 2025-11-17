package org.springframework.beans.factory.config;

import java.io.Serializable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ObjectFactoryCreatingFactoryBean.class */
public class ObjectFactoryCreatingFactoryBean extends AbstractFactoryBean<ObjectFactory<Object>> {

    @Nullable
    private String targetBeanName;

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.targetBeanName, "Property 'targetBeanName' is required");
        super.afterPropertiesSet();
    }

    @Override // org.springframework.beans.factory.config.AbstractFactoryBean, org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return ObjectFactory.class;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.config.AbstractFactoryBean
    public ObjectFactory<Object> createInstance() {
        BeanFactory beanFactory = getBeanFactory();
        Assert.state(beanFactory != null, "No BeanFactory available");
        Assert.state(this.targetBeanName != null, "No target bean name specified");
        return new TargetBeanObjectFactory(beanFactory, this.targetBeanName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/config/ObjectFactoryCreatingFactoryBean$TargetBeanObjectFactory.class */
    public static class TargetBeanObjectFactory implements ObjectFactory<Object>, Serializable {
        private final BeanFactory beanFactory;
        private final String targetBeanName;

        public TargetBeanObjectFactory(BeanFactory beanFactory, String targetBeanName) {
            this.beanFactory = beanFactory;
            this.targetBeanName = targetBeanName;
        }

        @Override // org.springframework.beans.factory.ObjectFactory
        public Object getObject() throws BeansException {
            return this.beanFactory.getBean(this.targetBeanName);
        }
    }
}
