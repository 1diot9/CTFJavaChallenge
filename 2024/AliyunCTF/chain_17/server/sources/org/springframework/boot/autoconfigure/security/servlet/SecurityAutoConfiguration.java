package org.springframework.boot.autoconfigure.security.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityDataConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;

@EnableConfigurationProperties({SecurityProperties.class})
@AutoConfiguration(before = {UserDetailsServiceAutoConfiguration.class})
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class})
@Import({SpringBootWebSecurityConfiguration.class, SecurityDataConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/SecurityAutoConfiguration.class */
public class SecurityAutoConfiguration {
    @ConditionalOnMissingBean({AuthenticationEventPublisher.class})
    @Bean
    public DefaultAuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher publisher) {
        return new DefaultAuthenticationEventPublisher(publisher);
    }
}
