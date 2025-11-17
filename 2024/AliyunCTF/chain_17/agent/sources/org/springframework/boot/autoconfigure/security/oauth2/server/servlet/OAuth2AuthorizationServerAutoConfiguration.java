package org.springframework.boot.autoconfigure.security.oauth2.server.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

@AutoConfiguration(before = {OAuth2ResourceServerAutoConfiguration.class, SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@ConditionalOnClass({OAuth2Authorization.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({OAuth2AuthorizationServerConfiguration.class, OAuth2AuthorizationServerWebSecurityConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/oauth2/server/servlet/OAuth2AuthorizationServerAutoConfiguration.class */
public class OAuth2AuthorizationServerAutoConfiguration {
}
