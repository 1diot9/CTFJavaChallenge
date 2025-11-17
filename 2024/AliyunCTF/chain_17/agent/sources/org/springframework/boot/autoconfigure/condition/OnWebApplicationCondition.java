package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.boot.web.reactive.context.ConfigurableReactiveWebEnvironment;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.WebApplicationContext;

@Order(LoggingApplicationListener.DEFAULT_ORDER)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnWebApplicationCondition.class */
class OnWebApplicationCondition extends FilteringSpringBootCondition {
    private static final String SERVLET_WEB_APPLICATION_CLASS = "org.springframework.web.context.support.GenericWebApplicationContext";
    private static final String REACTIVE_WEB_APPLICATION_CLASS = "org.springframework.web.reactive.HandlerResult";

    OnWebApplicationCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.FilteringSpringBootCondition
    protected ConditionOutcome[] getOutcomes(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        ConditionOutcome[] outcomes = new ConditionOutcome[autoConfigurationClasses.length];
        for (int i = 0; i < outcomes.length; i++) {
            String autoConfigurationClass = autoConfigurationClasses[i];
            if (autoConfigurationClass != null) {
                outcomes[i] = getOutcome(autoConfigurationMetadata.get(autoConfigurationClass, "ConditionalOnWebApplication"));
            }
        }
        return outcomes;
    }

    private ConditionOutcome getOutcome(String type) {
        if (type == null) {
            return null;
        }
        ConditionMessage.Builder message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnWebApplication.class, new Object[0]);
        if (ConditionalOnWebApplication.Type.SERVLET.name().equals(type) && !FilteringSpringBootCondition.ClassNameFilter.isPresent(SERVLET_WEB_APPLICATION_CLASS, getBeanClassLoader())) {
            return ConditionOutcome.noMatch(message.didNotFind("servlet web application classes").atAll());
        }
        if (ConditionalOnWebApplication.Type.REACTIVE.name().equals(type) && !FilteringSpringBootCondition.ClassNameFilter.isPresent(REACTIVE_WEB_APPLICATION_CLASS, getBeanClassLoader())) {
            return ConditionOutcome.noMatch(message.didNotFind("reactive web application classes").atAll());
        }
        if (!FilteringSpringBootCondition.ClassNameFilter.isPresent(SERVLET_WEB_APPLICATION_CLASS, getBeanClassLoader()) && !ClassUtils.isPresent(REACTIVE_WEB_APPLICATION_CLASS, getBeanClassLoader())) {
            return ConditionOutcome.noMatch(message.didNotFind("reactive or servlet web application classes").atAll());
        }
        return null;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean required = metadata.isAnnotated(ConditionalOnWebApplication.class.getName());
        ConditionOutcome outcome = isWebApplication(context, metadata, required);
        if (required && !outcome.isMatch()) {
            return ConditionOutcome.noMatch(outcome.getConditionMessage());
        }
        if (!required && outcome.isMatch()) {
            return ConditionOutcome.noMatch(outcome.getConditionMessage());
        }
        return ConditionOutcome.match(outcome.getConditionMessage());
    }

    private ConditionOutcome isWebApplication(ConditionContext context, AnnotatedTypeMetadata metadata, boolean required) {
        switch (deduceType(metadata)) {
            case SERVLET:
                return isServletWebApplication(context);
            case REACTIVE:
                return isReactiveWebApplication(context);
            default:
                return isAnyWebApplication(context, required);
        }
    }

    private ConditionOutcome isAnyWebApplication(ConditionContext context, boolean required) {
        Object[] objArr = new Object[1];
        objArr[0] = required ? "(required)" : "";
        ConditionMessage.Builder message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnWebApplication.class, objArr);
        ConditionOutcome servletOutcome = isServletWebApplication(context);
        if (servletOutcome.isMatch() && required) {
            return new ConditionOutcome(servletOutcome.isMatch(), message.because(servletOutcome.getMessage()));
        }
        ConditionOutcome reactiveOutcome = isReactiveWebApplication(context);
        if (reactiveOutcome.isMatch() && required) {
            return new ConditionOutcome(reactiveOutcome.isMatch(), message.because(reactiveOutcome.getMessage()));
        }
        return new ConditionOutcome(servletOutcome.isMatch() || reactiveOutcome.isMatch(), message.because(servletOutcome.getMessage()).append("and").append(reactiveOutcome.getMessage()));
    }

    private ConditionOutcome isServletWebApplication(ConditionContext context) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("", new Object[0]);
        if (!FilteringSpringBootCondition.ClassNameFilter.isPresent(SERVLET_WEB_APPLICATION_CLASS, context.getClassLoader())) {
            return ConditionOutcome.noMatch(message.didNotFind("servlet web application classes").atAll());
        }
        if (context.getBeanFactory() != null) {
            String[] scopes = context.getBeanFactory().getRegisteredScopeNames();
            if (ObjectUtils.containsElement(scopes, "session")) {
                return ConditionOutcome.match(message.foundExactly("'session' scope"));
            }
        }
        if (context.getEnvironment() instanceof ConfigurableWebEnvironment) {
            return ConditionOutcome.match(message.foundExactly("ConfigurableWebEnvironment"));
        }
        if (context.getResourceLoader() instanceof WebApplicationContext) {
            return ConditionOutcome.match(message.foundExactly("WebApplicationContext"));
        }
        return ConditionOutcome.noMatch(message.because("not a servlet web application"));
    }

    private ConditionOutcome isReactiveWebApplication(ConditionContext context) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("", new Object[0]);
        if (!FilteringSpringBootCondition.ClassNameFilter.isPresent(REACTIVE_WEB_APPLICATION_CLASS, context.getClassLoader())) {
            return ConditionOutcome.noMatch(message.didNotFind("reactive web application classes").atAll());
        }
        if (context.getEnvironment() instanceof ConfigurableReactiveWebEnvironment) {
            return ConditionOutcome.match(message.foundExactly("ConfigurableReactiveWebEnvironment"));
        }
        if (context.getResourceLoader() instanceof ReactiveWebApplicationContext) {
            return ConditionOutcome.match(message.foundExactly("ReactiveWebApplicationContext"));
        }
        return ConditionOutcome.noMatch(message.because("not a reactive web application"));
    }

    private ConditionalOnWebApplication.Type deduceType(AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnWebApplication.class.getName());
        if (attributes != null) {
            return (ConditionalOnWebApplication.Type) attributes.get("type");
        }
        return ConditionalOnWebApplication.Type.ANY;
    }
}
