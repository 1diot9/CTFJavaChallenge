package org.springframework.context.weaving;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/weaving/LoadTimeWeaverAwareProcessor.class */
public class LoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {

    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    @Nullable
    private BeanFactory beanFactory;

    public LoadTimeWeaverAwareProcessor() {
    }

    public LoadTimeWeaverAwareProcessor(@Nullable LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    public LoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof LoadTimeWeaverAware) {
            LoadTimeWeaverAware loadTimeWeaverAware = (LoadTimeWeaverAware) bean;
            LoadTimeWeaver ltw = this.loadTimeWeaver;
            if (ltw == null) {
                Assert.state(this.beanFactory != null, "BeanFactory required if no LoadTimeWeaver explicitly specified");
                ltw = (LoadTimeWeaver) this.beanFactory.getBean(ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME, LoadTimeWeaver.class);
            }
            loadTimeWeaverAware.setLoadTimeWeaver(ltw);
        }
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String name) {
        return bean;
    }
}
