package org.springframework.beans.factory.aot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.aot.AotServices;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/aot/BeanDefinitionMethodGeneratorFactory.class */
class BeanDefinitionMethodGeneratorFactory {
    private static final Log logger = LogFactory.getLog((Class<?>) BeanDefinitionMethodGeneratorFactory.class);
    private final AotServices<BeanRegistrationAotProcessor> aotProcessors;
    private final AotServices<BeanRegistrationExcludeFilter> excludeFilters;

    /* JADX INFO: Access modifiers changed from: package-private */
    public BeanDefinitionMethodGeneratorFactory(ConfigurableListableBeanFactory beanFactory) {
        this(AotServices.factoriesAndBeans(beanFactory));
    }

    BeanDefinitionMethodGeneratorFactory(AotServices.Loader loader) {
        this.aotProcessors = loader.load(BeanRegistrationAotProcessor.class);
        this.excludeFilters = loader.load(BeanRegistrationExcludeFilter.class);
        Iterator<BeanRegistrationExcludeFilter> it = this.excludeFilters.iterator();
        while (it.hasNext()) {
            BeanRegistrationExcludeFilter excludeFilter = it.next();
            if (this.excludeFilters.getSource(excludeFilter) == AotServices.Source.BEAN_FACTORY) {
                Assert.state((excludeFilter instanceof BeanRegistrationAotProcessor) || (excludeFilter instanceof BeanFactoryInitializationAotProcessor), (Supplier<String>) () -> {
                    return "BeanRegistrationExcludeFilter bean of type %s must also implement an AOT processor interface".formatted(excludeFilter.getClass().getName());
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public BeanDefinitionMethodGenerator getBeanDefinitionMethodGenerator(RegisteredBean registeredBean, @Nullable String currentPropertyName) {
        if (isExcluded(registeredBean)) {
            return null;
        }
        List<BeanRegistrationAotContribution> contributions = getAotContributions(registeredBean);
        return new BeanDefinitionMethodGenerator(this, registeredBean, currentPropertyName, contributions);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public BeanDefinitionMethodGenerator getBeanDefinitionMethodGenerator(RegisteredBean registeredBean) {
        return getBeanDefinitionMethodGenerator(registeredBean, null);
    }

    private boolean isExcluded(RegisteredBean registeredBean) {
        if (isImplicitlyExcluded(registeredBean)) {
            return true;
        }
        Iterator<BeanRegistrationExcludeFilter> it = this.excludeFilters.iterator();
        while (it.hasNext()) {
            BeanRegistrationExcludeFilter excludeFilter = it.next();
            if (excludeFilter.isExcludedFromAotProcessing(registeredBean)) {
                logger.trace(LogMessage.format("Excluding registered bean '%s' from bean factory %s due to %s", registeredBean.getBeanName(), ObjectUtils.identityToString(registeredBean.getBeanFactory()), excludeFilter.getClass().getName()));
                return true;
            }
        }
        return false;
    }

    private boolean isImplicitlyExcluded(RegisteredBean registeredBean) {
        Class<?> beanClass = registeredBean.getBeanClass();
        if (BeanFactoryInitializationAotProcessor.class.isAssignableFrom(beanClass)) {
            return true;
        }
        if (BeanRegistrationAotProcessor.class.isAssignableFrom(beanClass)) {
            BeanRegistrationAotProcessor processor = this.aotProcessors.findByBeanName(registeredBean.getBeanName());
            return processor == null || processor.isBeanExcludedFromAotProcessing();
        }
        return false;
    }

    private List<BeanRegistrationAotContribution> getAotContributions(RegisteredBean registeredBean) {
        String beanName = registeredBean.getBeanName();
        List<BeanRegistrationAotContribution> contributions = new ArrayList<>();
        Iterator<BeanRegistrationAotProcessor> it = this.aotProcessors.iterator();
        while (it.hasNext()) {
            BeanRegistrationAotProcessor aotProcessor = it.next();
            BeanRegistrationAotContribution contribution = aotProcessor.processAheadOfTime(registeredBean);
            if (contribution != null) {
                logger.trace(LogMessage.format("Adding bean registration AOT contribution %S from %S to '%S'", contribution.getClass().getName(), aotProcessor.getClass().getName(), beanName));
                contributions.add(contribution);
            }
        }
        return contributions;
    }
}
