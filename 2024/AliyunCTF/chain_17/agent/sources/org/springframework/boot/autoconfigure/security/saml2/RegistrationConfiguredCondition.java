package org.springframework.boot.autoconfigure.security.saml2;

import java.util.Collections;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/RegistrationConfiguredCondition.class */
class RegistrationConfiguredCondition extends SpringBootCondition {
    private static final String PROPERTY = "spring.security.saml2.relyingparty.registration";
    private static final Bindable<Map<String, Saml2RelyingPartyProperties.Registration>> STRING_REGISTRATION_MAP = Bindable.mapOf(String.class, Saml2RelyingPartyProperties.Registration.class);

    RegistrationConfiguredCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("Relying Party Registration Condition", new Object[0]);
        Map<String, Saml2RelyingPartyProperties.Registration> registrations = getRegistrations(context.getEnvironment());
        if (registrations.isEmpty()) {
            return ConditionOutcome.noMatch(message.didNotFind("any registrations").atAll());
        }
        return ConditionOutcome.match(message.found("registration", "registrations").items(registrations.keySet()));
    }

    private Map<String, Saml2RelyingPartyProperties.Registration> getRegistrations(Environment environment) {
        return (Map) Binder.get(environment).bind(PROPERTY, STRING_REGISTRATION_MAP).orElse(Collections.emptyMap());
    }
}
