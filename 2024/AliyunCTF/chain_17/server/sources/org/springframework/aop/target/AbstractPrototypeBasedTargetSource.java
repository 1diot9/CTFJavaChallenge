package org.springframework.aop.target;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/target/AbstractPrototypeBasedTargetSource.class */
public abstract class AbstractPrototypeBasedTargetSource extends AbstractBeanFactoryBasedTargetSource {
    @Override // org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource, org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        if (!beanFactory.isPrototype(getTargetBeanName())) {
            throw new BeanDefinitionStoreException("Cannot use prototype-based TargetSource against non-prototype bean with name '" + getTargetBeanName() + "': instances would not be independent");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object newPrototypeInstance() throws BeansException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating new instance of bean '" + getTargetBeanName() + "'");
        }
        return getBeanFactory().getBean(getTargetBeanName());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void destroyPrototypeInstance(Object target) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Destroying instance of bean '" + getTargetBeanName() + "'");
        }
        BeanFactory beanFactory = getBeanFactory();
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            cbf.destroyBean(getTargetBeanName(), target);
        } else if (target instanceof DisposableBean) {
            DisposableBean disposableBean = (DisposableBean) target;
            try {
                disposableBean.destroy();
            } catch (Throwable ex) {
                this.logger.warn("Destroy method on bean with name '" + getTargetBeanName() + "' threw an exception", ex);
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("A prototype-based TargetSource itself is not deserializable - just a disconnected SingletonTargetSource or EmptyTargetSource is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Disconnecting TargetSource [" + this + "]");
        }
        try {
            Object target = getTarget();
            return target != null ? new SingletonTargetSource(target) : EmptyTargetSource.forClass(getTargetClass());
        } catch (Exception ex) {
            String msg = "Cannot get target for disconnecting TargetSource [" + this + "]";
            this.logger.error(msg, ex);
            throw new NotSerializableException(msg + ": " + ex);
        }
    }
}
