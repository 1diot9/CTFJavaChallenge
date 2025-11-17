package org.springframework.boot.autoconfigure.condition;

import jakarta.servlet.ServletContext;
import java.lang.annotation.Annotation;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnWarDeploymentCondition.class */
class OnWarDeploymentCondition extends SpringBootCondition {
    OnWarDeploymentCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean required = metadata.isAnnotated(ConditionalOnWarDeployment.class.getName());
        ResourceLoader resourceLoader = context.getResourceLoader();
        if (resourceLoader instanceof WebApplicationContext) {
            WebApplicationContext applicationContext = (WebApplicationContext) resourceLoader;
            ServletContext servletContext = applicationContext.getServletContext();
            if (servletContext != null) {
                return new ConditionOutcome(required, "Application is deployed as a WAR file.");
            }
        }
        return new ConditionOutcome(!required, ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnWarDeployment.class, new Object[0]).because("the application is not deployed as a WAR file."));
    }
}
