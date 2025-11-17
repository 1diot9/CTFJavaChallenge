package org.springframework.aop.aspectj.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.reflect.PerClauseKind;
import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/aspectj/annotation/BeanFactoryAspectJAdvisorsBuilder.class */
public class BeanFactoryAspectJAdvisorsBuilder {
    private final ListableBeanFactory beanFactory;
    private final AspectJAdvisorFactory advisorFactory;

    @Nullable
    private volatile List<String> aspectBeanNames;
    private final Map<String, List<Advisor>> advisorsCache;
    private final Map<String, MetadataAwareAspectInstanceFactory> aspectFactoryCache;

    public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory) {
        this(beanFactory, new ReflectiveAspectJAdvisorFactory(beanFactory));
    }

    public BeanFactoryAspectJAdvisorsBuilder(ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
        this.advisorsCache = new ConcurrentHashMap();
        this.aspectFactoryCache = new ConcurrentHashMap();
        Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
        Assert.notNull(advisorFactory, "AspectJAdvisorFactory must not be null");
        this.beanFactory = beanFactory;
        this.advisorFactory = advisorFactory;
    }

    public List<Advisor> buildAspectJAdvisors() {
        Class<?> beanType;
        List<String> aspectNames = this.aspectBeanNames;
        if (aspectNames == null) {
            synchronized (this) {
                aspectNames = this.aspectBeanNames;
                if (aspectNames == null) {
                    List<Advisor> advisors = new ArrayList<>();
                    List<String> aspectNames2 = new ArrayList<>();
                    String[] beanNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, (Class<?>) Object.class, true, false);
                    for (String beanName : beanNames) {
                        if (isEligibleBean(beanName) && (beanType = this.beanFactory.getType(beanName, false)) != null && this.advisorFactory.isAspect(beanType)) {
                            aspectNames2.add(beanName);
                            AspectMetadata amd = new AspectMetadata(beanType, beanName);
                            if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                                MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                                List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
                                if (this.beanFactory.isSingleton(beanName)) {
                                    this.advisorsCache.put(beanName, classAdvisors);
                                } else {
                                    this.aspectFactoryCache.put(beanName, factory);
                                }
                                advisors.addAll(classAdvisors);
                            } else {
                                if (this.beanFactory.isSingleton(beanName)) {
                                    throw new IllegalArgumentException("Bean with name '" + beanName + "' is a singleton, but aspect instantiation model is not singleton");
                                }
                                MetadataAwareAspectInstanceFactory factory2 = new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                                this.aspectFactoryCache.put(beanName, factory2);
                                advisors.addAll(this.advisorFactory.getAdvisors(factory2));
                            }
                        }
                    }
                    this.aspectBeanNames = aspectNames2;
                    return advisors;
                }
            }
        }
        if (aspectNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<Advisor> advisors2 = new ArrayList<>();
        for (String aspectName : aspectNames) {
            List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
            if (cachedAdvisors != null) {
                advisors2.addAll(cachedAdvisors);
            } else {
                advisors2.addAll(this.advisorFactory.getAdvisors(this.aspectFactoryCache.get(aspectName)));
            }
        }
        return advisors2;
    }

    protected boolean isEligibleBean(String beanName) {
        return true;
    }
}
