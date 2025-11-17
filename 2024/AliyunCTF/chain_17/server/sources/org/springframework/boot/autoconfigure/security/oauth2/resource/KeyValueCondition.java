package org.springframework.boot.autoconfigure.security.oauth2.resource;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/resource/KeyValueCondition.class */
public class KeyValueCondition extends SpringBootCondition {
    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("Public Key Value Condition", new Object[0]);
        Environment environment = context.getEnvironment();
        String publicKeyLocation = environment.getProperty("spring.security.oauth2.resourceserver.jwt.public-key-location");
        if (!StringUtils.hasText(publicKeyLocation)) {
            return ConditionOutcome.noMatch(message.didNotFind("public-key-location property").atAll());
        }
        String issuerUri = environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        String jwkSetUri = environment.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
        return StringUtils.hasText(jwkSetUri) ? ConditionOutcome.noMatch(message.found("jwk-set-uri property").items(jwkSetUri)) : StringUtils.hasText(issuerUri) ? ConditionOutcome.noMatch(message.found("issuer-uri property").items(issuerUri)) : ConditionOutcome.match(message.foundExactly("public key location property"));
    }
}
