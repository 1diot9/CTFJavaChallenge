package org.springframework.aop.framework.autoproxy;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/framework/autoproxy/BeanFactoryAdvisorRetrievalHelper.class */
public class BeanFactoryAdvisorRetrievalHelper {
    private static final Log logger = LogFactory.getLog((Class<?>) BeanFactoryAdvisorRetrievalHelper.class);
    private final ConfigurableListableBeanFactory beanFactory;

    @Nullable
    private volatile String[] cachedAdvisorBeanNames;

    public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public List<Advisor> findAdvisorBeans() {
        String[] advisorNames = this.cachedAdvisorBeanNames;
        if (advisorNames == null) {
            advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory) this.beanFactory, (Class<?>) Advisor.class, true, false);
            this.cachedAdvisorBeanNames = advisorNames;
        }
        if (advisorNames.length == 0) {
            return new ArrayList();
        }
        List<Advisor> advisors = new ArrayList<>();
        for (String name : advisorNames) {
            if (isEligibleBean(name)) {
                if (this.beanFactory.isCurrentlyInCreation(name)) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Skipping currently created advisor '" + name + "'");
                    }
                } else {
                    try {
                        advisors.add((Advisor) this.beanFactory.getBean(name, Advisor.class));
                    } catch (BeanCreationException ex) {
                        Throwable rootCause = ex.getMostSpecificCause();
                        if (rootCause instanceof BeanCurrentlyInCreationException) {
                            BeanCurrentlyInCreationException bce = (BeanCurrentlyInCreationException) rootCause;
                            String bceBeanName = bce.getBeanName();
                            if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
                                if (logger.isTraceEnabled()) {
                                    logger.trace("Skipping advisor '" + name + "' with dependency on currently created bean: " + ex.getMessage());
                                }
                            }
                        }
                        throw ex;
                    }
                }
            }
        }
        return advisors;
    }

    protected boolean isEligibleBean(String beanName) {
        return true;
    }
}
