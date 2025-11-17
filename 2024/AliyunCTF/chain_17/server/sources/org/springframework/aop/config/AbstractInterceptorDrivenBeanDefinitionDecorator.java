package org.springframework.aop.config;

import java.util.List;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.w3c.dom.Node;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/AbstractInterceptorDrivenBeanDefinitionDecorator.class */
public abstract class AbstractInterceptorDrivenBeanDefinitionDecorator implements BeanDefinitionDecorator {
    protected abstract BeanDefinition createInterceptorDefinition(Node node);

    @Override // org.springframework.beans.factory.xml.BeanDefinitionDecorator
    public final BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definitionHolder, ParserContext parserContext) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String existingBeanName = definitionHolder.getBeanName();
        BeanDefinition targetDefinition = definitionHolder.getBeanDefinition();
        BeanDefinitionHolder targetHolder = new BeanDefinitionHolder(targetDefinition, existingBeanName + ".TARGET");
        BeanDefinition interceptorDefinition = createInterceptorDefinition(node);
        String interceptorName = existingBeanName + "." + getInterceptorNameSuffix(interceptorDefinition);
        BeanDefinitionReaderUtils.registerBeanDefinition(new BeanDefinitionHolder(interceptorDefinition, interceptorName), registry);
        BeanDefinitionHolder result = definitionHolder;
        if (!isProxyFactoryBeanDefinition(targetDefinition)) {
            RootBeanDefinition proxyDefinition = new RootBeanDefinition();
            proxyDefinition.setBeanClass(ProxyFactoryBean.class);
            proxyDefinition.setScope(targetDefinition.getScope());
            proxyDefinition.setLazyInit(targetDefinition.isLazyInit());
            proxyDefinition.setDecoratedDefinition(targetHolder);
            proxyDefinition.getPropertyValues().add(DataBinder.DEFAULT_OBJECT_NAME, targetHolder);
            proxyDefinition.getPropertyValues().add("interceptorNames", new ManagedList());
            proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
            proxyDefinition.setPrimary(targetDefinition.isPrimary());
            if (targetDefinition instanceof AbstractBeanDefinition) {
                AbstractBeanDefinition abd = (AbstractBeanDefinition) targetDefinition;
                proxyDefinition.copyQualifiersFrom(abd);
            }
            result = new BeanDefinitionHolder(proxyDefinition, existingBeanName);
        }
        addInterceptorNameToList(interceptorName, result.getBeanDefinition());
        return result;
    }

    private void addInterceptorNameToList(String interceptorName, BeanDefinition beanDefinition) {
        List<String> list = (List) beanDefinition.getPropertyValues().get("interceptorNames");
        Assert.state(list != null, "Missing 'interceptorNames' property");
        list.add(interceptorName);
    }

    private boolean isProxyFactoryBeanDefinition(BeanDefinition existingDefinition) {
        return ProxyFactoryBean.class.getName().equals(existingDefinition.getBeanClassName());
    }

    protected String getInterceptorNameSuffix(BeanDefinition interceptorDefinition) {
        String beanClassName = interceptorDefinition.getBeanClassName();
        return StringUtils.hasLength(beanClassName) ? StringUtils.uncapitalize(ClassUtils.getShortName(beanClassName)) : "";
    }
}
