package org.springframework.aop.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/support/AbstractBeanFactoryPointcutAdvisor.class */
public abstract class AbstractBeanFactoryPointcutAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    @Nullable
    private String adviceBeanName;

    @Nullable
    private BeanFactory beanFactory;

    @Nullable
    private volatile transient Advice advice;
    private volatile transient Object adviceMonitor = new Object();

    public void setAdviceBeanName(@Nullable String adviceBeanName) {
        this.adviceBeanName = adviceBeanName;
    }

    @Nullable
    public String getAdviceBeanName() {
        return this.adviceBeanName;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        resetAdviceMonitor();
    }

    private void resetAdviceMonitor() {
        BeanFactory beanFactory = this.beanFactory;
        if (beanFactory instanceof ConfigurableBeanFactory) {
            ConfigurableBeanFactory cbf = (ConfigurableBeanFactory) beanFactory;
            this.adviceMonitor = cbf.getSingletonMutex();
        } else {
            this.adviceMonitor = new Object();
        }
    }

    public void setAdvice(Advice advice) {
        synchronized (this.adviceMonitor) {
            this.advice = advice;
        }
    }

    @Override // org.springframework.aop.Advisor
    public Advice getAdvice() {
        Advice advice;
        Advice advice2 = this.advice;
        if (advice2 != null) {
            return advice2;
        }
        Assert.state(this.adviceBeanName != null, "'adviceBeanName' must be specified");
        Assert.state(this.beanFactory != null, "BeanFactory must be set to resolve 'adviceBeanName'");
        if (this.beanFactory.isSingleton(this.adviceBeanName)) {
            Advice advice3 = (Advice) this.beanFactory.getBean(this.adviceBeanName, Advice.class);
            this.advice = advice3;
            return advice3;
        }
        synchronized (this.adviceMonitor) {
            Advice advice4 = this.advice;
            if (advice4 == null) {
                advice4 = (Advice) this.beanFactory.getBean(this.adviceBeanName, Advice.class);
                this.advice = advice4;
            }
            advice = advice4;
        }
        return advice;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(": advice ");
        if (this.adviceBeanName != null) {
            sb.append("bean '").append(this.adviceBeanName).append('\'');
        } else {
            sb.append(this.advice);
        }
        return sb.toString();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        resetAdviceMonitor();
    }
}
