package org.springframework.boot.autoconfigure.security.servlet;

import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.StringUtils;

@AutoConfiguration
@ConditionalOnClass({AuthenticationManager.class})
@ConditionalOnMissingBean(value = {AuthenticationManager.class, AuthenticationProvider.class, UserDetailsService.class, AuthenticationManagerResolver.class}, type = {"org.springframework.security.oauth2.jwt.JwtDecoder"})
@Conditional({MissingAlternativeOrUserPropertiesConfigured.class})
@ConditionalOnBean({ObjectPostProcessor.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/UserDetailsServiceAutoConfiguration.class */
public class UserDetailsServiceAutoConfiguration {
    private static final String NOOP_PASSWORD_PREFIX = "{noop}";
    private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$");
    private static final Log logger = LogFactory.getLog((Class<?>) UserDetailsServiceAutoConfiguration.class);

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(SecurityProperties properties, ObjectProvider<PasswordEncoder> passwordEncoder) {
        SecurityProperties.User user = properties.getUser();
        List<String> roles = user.getRoles();
        return new InMemoryUserDetailsManager(new UserDetails[]{User.withUsername(user.getName()).password(getOrDeducePassword(user, passwordEncoder.getIfAvailable())).roles(StringUtils.toStringArray(roles)).build()});
    }

    private String getOrDeducePassword(SecurityProperties.User user, PasswordEncoder encoder) {
        String password = user.getPassword();
        if (user.isPasswordGenerated()) {
            logger.warn(String.format("%n%nUsing generated security password: %s%n%nThis generated password is for development use only. Your security configuration must be updated before running your application in production.%n", user.getPassword()));
        }
        if (encoder != null || PASSWORD_ALGORITHM_PATTERN.matcher(password).matches()) {
            return password;
        }
        return "{noop}" + password;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/UserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured.class */
    static final class MissingAlternativeOrUserPropertiesConfigured extends AnyNestedCondition {
        MissingAlternativeOrUserPropertiesConfigured() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnMissingClass({"org.springframework.security.oauth2.client.registration.ClientRegistrationRepository", "org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector", "org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/UserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured$MissingAlternative.class */
        static final class MissingAlternative {
            MissingAlternative() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.security.user", name = {"name"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/UserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured$NameConfigured.class */
        static final class NameConfigured {
            NameConfigured() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.security.user", name = {"password"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/servlet/UserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured$PasswordConfigured.class */
        static final class PasswordConfigured {
            PasswordConfigured() {
            }
        }
    }
}
