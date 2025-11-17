package org.springframework.aop.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.parsing.AbstractComponentDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/AdvisorComponentDefinition.class */
public class AdvisorComponentDefinition extends AbstractComponentDefinition {
    private final String advisorBeanName;
    private final BeanDefinition advisorDefinition;
    private final String description;
    private final BeanReference[] beanReferences;
    private final BeanDefinition[] beanDefinitions;

    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition) {
        this(advisorBeanName, advisorDefinition, null);
    }

    public AdvisorComponentDefinition(String advisorBeanName, BeanDefinition advisorDefinition, @Nullable BeanDefinition pointcutDefinition) {
        Assert.notNull(advisorBeanName, "'advisorBeanName' must not be null");
        Assert.notNull(advisorDefinition, "'advisorDefinition' must not be null");
        this.advisorBeanName = advisorBeanName;
        this.advisorDefinition = advisorDefinition;
        MutablePropertyValues pvs = advisorDefinition.getPropertyValues();
        BeanReference adviceReference = (BeanReference) pvs.get("adviceBeanName");
        Assert.state(adviceReference != null, "Missing 'adviceBeanName' property");
        if (pointcutDefinition != null) {
            this.beanReferences = new BeanReference[]{adviceReference};
            this.beanDefinitions = new BeanDefinition[]{advisorDefinition, pointcutDefinition};
            this.description = buildDescription(adviceReference, pointcutDefinition);
        } else {
            BeanReference pointcutReference = (BeanReference) pvs.get("pointcut");
            Assert.state(pointcutReference != null, "Missing 'pointcut' property");
            this.beanReferences = new BeanReference[]{adviceReference, pointcutReference};
            this.beanDefinitions = new BeanDefinition[]{advisorDefinition};
            this.description = buildDescription(adviceReference, pointcutReference);
        }
    }

    private String buildDescription(BeanReference adviceReference, BeanDefinition pointcutDefinition) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(expression)=[" + pointcutDefinition.getPropertyValues().get("expression") + "]>";
    }

    private String buildDescription(BeanReference adviceReference, BeanReference pointcutReference) {
        return "Advisor <advice(ref)='" + adviceReference.getBeanName() + "', pointcut(ref)='" + pointcutReference.getBeanName() + "'>";
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getName() {
        return this.advisorBeanName;
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public String getDescription() {
        return this.description;
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    @Override // org.springframework.beans.factory.parsing.AbstractComponentDefinition, org.springframework.beans.factory.parsing.ComponentDefinition
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.advisorDefinition.getSource();
    }
}
