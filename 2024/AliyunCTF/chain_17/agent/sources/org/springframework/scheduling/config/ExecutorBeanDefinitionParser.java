package org.springframework.scheduling.config;

import org.apache.coyote.http11.Constants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/ExecutorBeanDefinitionParser.class */
public class ExecutorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        return "org.springframework.scheduling.config.TaskExecutorFactoryBean";
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String keepAliveSeconds = element.getAttribute(Constants.KEEP_ALIVE_HEADER_VALUE_TOKEN);
        if (StringUtils.hasText(keepAliveSeconds)) {
            builder.addPropertyValue("keepAliveSeconds", keepAliveSeconds);
        }
        String queueCapacity = element.getAttribute("queue-capacity");
        if (StringUtils.hasText(queueCapacity)) {
            builder.addPropertyValue("queueCapacity", queueCapacity);
        }
        configureRejectionPolicy(element, builder);
        String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }
    }

    private void configureRejectionPolicy(Element element, BeanDefinitionBuilder builder) {
        String str;
        String rejectionPolicy = element.getAttribute("rejection-policy");
        if (!StringUtils.hasText(rejectionPolicy)) {
            return;
        }
        boolean z = -1;
        switch (rejectionPolicy.hashCode()) {
            case -1905617794:
                if (rejectionPolicy.equals("DISCARD")) {
                    z = 2;
                    break;
                }
                break;
            case -1392022660:
                if (rejectionPolicy.equals("CALLER_RUNS")) {
                    z = true;
                    break;
                }
                break;
            case -1045903136:
                if (rejectionPolicy.equals("DISCARD_OLDEST")) {
                    z = 3;
                    break;
                }
                break;
            case 62073616:
                if (rejectionPolicy.equals("ABORT")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                str = "java.util.concurrent.ThreadPoolExecutor." + "AbortPolicy";
                break;
            case true:
                str = "java.util.concurrent.ThreadPoolExecutor." + "CallerRunsPolicy";
                break;
            case true:
                str = "java.util.concurrent.ThreadPoolExecutor." + "DiscardPolicy";
                break;
            case true:
                str = "java.util.concurrent.ThreadPoolExecutor." + "DiscardOldestPolicy";
                break;
            default:
                str = rejectionPolicy;
                break;
        }
        String policyClassName = str;
        builder.addPropertyValue("rejectedExecutionHandler", new RootBeanDefinition(policyClassName));
    }
}
