package org.springframework.boot.autoconfigure.web;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/OnEnabledResourceChainCondition.class */
class OnEnabledResourceChainCondition extends SpringBootCondition {
    private static final String WEBJAR_ASSET_LOCATOR = "org.webjars.WebJarAssetLocator";

    OnEnabledResourceChainCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConfigurableEnvironment environment = (ConfigurableEnvironment) context.getEnvironment();
        boolean fixed = getEnabledProperty(environment, "strategy.fixed.", false).booleanValue();
        boolean content = getEnabledProperty(environment, "strategy.content.", false).booleanValue();
        Boolean chain = getEnabledProperty(environment, "", null);
        Boolean match = WebProperties.Resources.Chain.getEnabled(fixed, content, chain);
        ConditionMessage.Builder message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnEnabledResourceChain.class, new Object[0]);
        if (match == null) {
            return ClassUtils.isPresent(WEBJAR_ASSET_LOCATOR, getClass().getClassLoader()) ? ConditionOutcome.match(message.found("class").items(WEBJAR_ASSET_LOCATOR)) : ConditionOutcome.noMatch(message.didNotFind("class").items(WEBJAR_ASSET_LOCATOR));
        }
        if (match.booleanValue()) {
            return ConditionOutcome.match(message.because("enabled"));
        }
        return ConditionOutcome.noMatch(message.because("disabled"));
    }

    private Boolean getEnabledProperty(ConfigurableEnvironment environment, String key, Boolean defaultValue) {
        String name = "spring.web.resources.chain." + key + "enabled";
        return (Boolean) environment.getProperty(name, Boolean.class, defaultValue);
    }
}
