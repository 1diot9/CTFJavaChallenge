package org.springframework.boot.autoconfigure.security.saml2;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;

@EnableConfigurationProperties({Saml2RelyingPartyProperties.class})
@AutoConfiguration(before = {SecurityAutoConfiguration.class})
@ConditionalOnClass({RelyingPartyRegistrationRepository.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({Saml2RelyingPartyRegistrationConfiguration.class, Saml2LoginConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2RelyingPartyAutoConfiguration.class */
public class Saml2RelyingPartyAutoConfiguration {
}
