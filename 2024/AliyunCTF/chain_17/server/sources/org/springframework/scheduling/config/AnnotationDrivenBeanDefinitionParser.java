package org.springframework.scheduling.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scheduling/config/AnnotationDrivenBeanDefinitionParser.class */
public class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {
    private static final String ASYNC_EXECUTION_ASPECT_CLASS_NAME = "org.springframework.scheduling.aspectj.AnnotationAsyncExecutionAspect";

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);
        BeanDefinitionRegistry registry = parserContext.getRegistry();
        String mode = element.getAttribute(AdviceModeImportSelector.DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME);
        if ("aspectj".equals(mode)) {
            registerAsyncExecutionAspect(element, parserContext);
        } else if (registry.containsBeanDefinition(TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            parserContext.getReaderContext().error("Only one AsyncAnnotationBeanPostProcessor may exist within the context.", source);
        } else {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor");
            builder.getRawBeanDefinition().setSource(source);
            String executor = element.getAttribute("executor");
            if (StringUtils.hasText(executor)) {
                builder.addPropertyReference("executor", executor);
            }
            String exceptionHandler = element.getAttribute("exception-handler");
            if (StringUtils.hasText(exceptionHandler)) {
                builder.addPropertyReference("exceptionHandler", exceptionHandler);
            }
            if (Boolean.parseBoolean(element.getAttribute(AopNamespaceUtils.PROXY_TARGET_CLASS_ATTRIBUTE))) {
                builder.addPropertyValue("proxyTargetClass", true);
            }
            registerPostProcessor(parserContext, builder, TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME);
        }
        if (registry.containsBeanDefinition(TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            parserContext.getReaderContext().error("Only one ScheduledAnnotationBeanPostProcessor may exist within the context.", source);
        } else {
            BeanDefinitionBuilder builder2 = BeanDefinitionBuilder.genericBeanDefinition("org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor");
            builder2.getRawBeanDefinition().setSource(source);
            String scheduler = element.getAttribute("scheduler");
            if (StringUtils.hasText(scheduler)) {
                builder2.addPropertyReference("scheduler", scheduler);
            }
            registerPostProcessor(parserContext, builder2, TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME);
        }
        parserContext.popAndRegisterContainingComponent();
        return null;
    }

    private void registerAsyncExecutionAspect(Element element, ParserContext parserContext) {
        if (!parserContext.getRegistry().containsBeanDefinition(TaskManagementConfigUtils.ASYNC_EXECUTION_ASPECT_BEAN_NAME)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ASYNC_EXECUTION_ASPECT_CLASS_NAME);
            builder.setFactoryMethod("aspectOf");
            String executor = element.getAttribute("executor");
            if (StringUtils.hasText(executor)) {
                builder.addPropertyReference("executor", executor);
            }
            String exceptionHandler = element.getAttribute("exception-handler");
            if (StringUtils.hasText(exceptionHandler)) {
                builder.addPropertyReference("exceptionHandler", exceptionHandler);
            }
            parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getBeanDefinition(), TaskManagementConfigUtils.ASYNC_EXECUTION_ASPECT_BEAN_NAME));
        }
    }

    private static void registerPostProcessor(ParserContext parserContext, BeanDefinitionBuilder builder, String beanName) {
        builder.setRole(2);
        parserContext.getRegistry().registerBeanDefinition(beanName, builder.getBeanDefinition());
        BeanDefinitionHolder holder = new BeanDefinitionHolder(builder.getBeanDefinition(), beanName);
        parserContext.registerComponent(new BeanComponentDefinition(holder));
    }
}
