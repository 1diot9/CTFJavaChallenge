package org.springframework.boot.autoconfigure.security.oauth2.resource;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/IssuerUriCondition.class */
public class IssuerUriCondition extends SpringBootCondition {
    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("OpenID Connect Issuer URI Condition", new Object[0]);
        Environment environment = context.getEnvironment();
        String issuerUri = environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        String jwkSetUri = environment.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
        if (StringUtils.hasText(issuerUri)) {
            return StringUtils.hasText(jwkSetUri) ? ConditionOutcome.noMatch(message.found("jwk-set-uri property").items(jwkSetUri)) : ConditionOutcome.match(message.foundExactly("issuer-uri property"));
        }
        return ConditionOutcome.noMatch(message.didNotFind("issuer-uri property").atAll());
    }
}
