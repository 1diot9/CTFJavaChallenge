package org.springframework.boot.autoconfigure.security.oauth2.server.servlet;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/RegisteredClientsConfiguredCondition.class */
class RegisteredClientsConfiguredCondition extends SpringBootCondition {
    private static final Bindable<Map<String, OAuth2AuthorizationServerProperties.Client>> STRING_CLIENT_MAP = Bindable.mapOf(String.class, OAuth2AuthorizationServerProperties.Client.class);

    RegisteredClientsConfiguredCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("OAuth2 Registered Clients Configured Condition", new Object[0]);
        Map<String, OAuth2AuthorizationServerProperties.Client> registrations = getRegistrations(context.getEnvironment());
        if (!registrations.isEmpty()) {
            return ConditionOutcome.match(message.foundExactly("registered clients " + ((String) registrations.values().stream().map((v0) -> {
                return v0.getRegistration();
            }).map((v0) -> {
                return v0.getClientId();
            }).collect(Collectors.joining(", ")))));
        }
        return ConditionOutcome.noMatch(message.notAvailable("registered clients"));
    }

    private Map<String, OAuth2AuthorizationServerProperties.Client> getRegistrations(Environment environment) {
        return (Map) Binder.get(environment).bind("spring.security.oauth2.authorizationserver.client", STRING_CLIENT_MAP).orElse(Collections.emptyMap());
    }
}
