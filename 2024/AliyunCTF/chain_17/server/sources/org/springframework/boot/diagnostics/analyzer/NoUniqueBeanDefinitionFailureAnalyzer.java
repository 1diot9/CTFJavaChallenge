package org.springframework.boot.diagnostics.analyzer;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/NoUniqueBeanDefinitionFailureAnalyzer.class */
class NoUniqueBeanDefinitionFailureAnalyzer extends AbstractInjectionFailureAnalyzer<NoUniqueBeanDefinitionException> {
    private final ConfigurableBeanFactory beanFactory;

    NoUniqueBeanDefinitionFailureAnalyzer(BeanFactory beanFactory) {
        Assert.isInstanceOf(ConfigurableBeanFactory.class, beanFactory);
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoUniqueBeanDefinitionException cause, String description) {
        String[] beanNames;
        if (description == null || (beanNames = extractBeanNames(cause)) == null) {
            return null;
        }
        StringBuilder message = new StringBuilder();
        message.append(String.format("%s required a single bean, but %d were found:%n", description, Integer.valueOf(beanNames.length)));
        for (String beanName : beanNames) {
            buildMessage(message, beanName);
        }
        MissingParameterNamesFailureAnalyzer.appendPossibility(message);
        return new FailureAnalysis(message.toString(), "Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed" + "%n%n%s".formatted(MissingParameterNamesFailureAnalyzer.ACTION), cause);
    }

    private void buildMessage(StringBuilder message, String beanName) {
        try {
            BeanDefinition definition = this.beanFactory.getMergedBeanDefinition(beanName);
            message.append(getDefinitionDescription(beanName, definition));
        } catch (NoSuchBeanDefinitionException e) {
            message.append(String.format("\t- %s: a programmatically registered singleton%n", beanName));
        }
    }

    private String getDefinitionDescription(String beanName, BeanDefinition definition) {
        if (StringUtils.hasText(definition.getFactoryMethodName())) {
            return String.format("\t- %s: defined by method '%s' in %s%n", beanName, definition.getFactoryMethodName(), getResourceDescription(definition));
        }
        return String.format("\t- %s: defined in %s%n", beanName, getResourceDescription(definition));
    }

    private String getResourceDescription(BeanDefinition definition) {
        String resourceDescription = definition.getResourceDescription();
        return resourceDescription != null ? resourceDescription : "unknown location";
    }

    private String[] extractBeanNames(NoUniqueBeanDefinitionException cause) {
        if (cause.getMessage().contains("but found")) {
            return StringUtils.commaDelimitedListToStringArray(cause.getMessage().substring(cause.getMessage().lastIndexOf(58) + 1).trim());
        }
        return null;
    }
}
