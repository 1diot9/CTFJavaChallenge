package org.springframework.boot.web.servlet;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import java.util.Map;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/WebServletHandler.class */
class WebServletHandler extends ServletComponentHandler {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WebServletHandler() {
        super(WebServlet.class);
    }

    @Override // org.springframework.boot.web.servlet.ServletComponentHandler
    public void doHandle(Map<String, Object> attributes, AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) ServletRegistrationBean.class);
        builder.addPropertyValue("asyncSupported", attributes.get("asyncSupported"));
        builder.addPropertyValue("initParameters", extractInitParameters(attributes));
        builder.addPropertyValue("loadOnStartup", attributes.get("loadOnStartup"));
        String name = determineName(attributes, beanDefinition);
        builder.addPropertyValue("name", name);
        builder.addPropertyValue("servlet", beanDefinition);
        builder.addPropertyValue("urlMappings", extractUrlPatterns(attributes));
        builder.addPropertyValue("multipartConfig", determineMultipartConfig(beanDefinition));
        registry.registerBeanDefinition(name, builder.getBeanDefinition());
    }

    private String determineName(Map<String, Object> attributes, BeanDefinition beanDefinition) {
        return (String) (StringUtils.hasText((String) attributes.get("name")) ? attributes.get("name") : beanDefinition.getBeanClassName());
    }

    private BeanDefinition determineMultipartConfig(AnnotatedBeanDefinition beanDefinition) {
        Map<String, Object> attributes = beanDefinition.getMetadata().getAnnotationAttributes(MultipartConfig.class.getName());
        if (attributes == null) {
            return null;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition((Class<?>) MultipartConfigElement.class);
        builder.addConstructorArgValue(attributes.get("location"));
        builder.addConstructorArgValue(attributes.get("maxFileSize"));
        builder.addConstructorArgValue(attributes.get("maxRequestSize"));
        builder.addConstructorArgValue(attributes.get("fileSizeThreshold"));
        return builder.getBeanDefinition();
    }
}
