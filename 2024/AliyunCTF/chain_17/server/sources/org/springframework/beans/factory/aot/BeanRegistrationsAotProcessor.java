package org.springframework.beans.factory.aot;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.aot.BeanRegistrationsAotContribution;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanRegistrationsAotProcessor.class */
class BeanRegistrationsAotProcessor implements BeanFactoryInitializationAotProcessor {
    BeanRegistrationsAotProcessor() {
    }

    @Override // org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
    @Nullable
    public BeanRegistrationsAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
        BeanDefinitionMethodGeneratorFactory beanDefinitionMethodGeneratorFactory = new BeanDefinitionMethodGeneratorFactory(beanFactory);
        Map<BeanRegistrationKey, BeanRegistrationsAotContribution.Registration> registrations = new LinkedHashMap<>();
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            RegisteredBean registeredBean = RegisteredBean.of(beanFactory, beanName);
            BeanDefinitionMethodGenerator beanDefinitionMethodGenerator = beanDefinitionMethodGeneratorFactory.getBeanDefinitionMethodGenerator(registeredBean);
            if (beanDefinitionMethodGenerator != null) {
                registrations.put(new BeanRegistrationKey(beanName, registeredBean.getBeanClass()), new BeanRegistrationsAotContribution.Registration(beanDefinitionMethodGenerator, beanFactory.getAliases(beanName)));
            }
        }
        if (registrations.isEmpty()) {
            return null;
        }
        return new BeanRegistrationsAotContribution(registrations);
    }
}
