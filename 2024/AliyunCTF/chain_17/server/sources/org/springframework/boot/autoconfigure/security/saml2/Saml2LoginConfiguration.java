package org.springframework.boot.autoconfigure.security.saml2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnDefaultWebSecurity
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({RelyingPartyRegistrationRepository.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/saml2/Saml2LoginConfiguration.class */
class Saml2LoginConfiguration {
    Saml2LoginConfiguration() {
    }

    @Bean
    SecurityFilterChain samlSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> {
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl) requests.anyRequest()).authenticated();
        });
        http.saml2Login(Customizer.withDefaults());
        http.saml2Logout(Customizer.withDefaults());
        return (SecurityFilterChain) http.build();
    }
}
