package org.springframework.boot.autoconfigure.security.reactive;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({SecurityProperties.class})
@AutoConfiguration(before = {ReactiveSecurityAutoConfiguration.class}, after = {RSocketMessagingAutoConfiguration.class})
@ConditionalOnClass({ReactiveAuthenticationManager.class})
@ConditionalOnMissingBean(value = {ReactiveAuthenticationManager.class, ReactiveUserDetailsService.class, ReactiveAuthenticationManagerResolver.class}, type = {"org.springframework.security.oauth2.jwt.ReactiveJwtDecoder"})
@Conditional({RSocketEnabledOrReactiveWebApplication.class, MissingAlternativeOrUserPropertiesConfigured.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration.class */
public class ReactiveUserDetailsServiceAutoConfiguration {
    private static final String NOOP_PASSWORD_PREFIX = "{noop}";
    private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$");
    private static final Log logger = LogFactory.getLog((Class<?>) ReactiveUserDetailsServiceAutoConfiguration.class);

    @Bean
    public MapReactiveUserDetailsService reactiveUserDetailsService(SecurityProperties properties, ObjectProvider<PasswordEncoder> passwordEncoder) {
        SecurityProperties.User user = properties.getUser();
        UserDetails userDetails = getUserDetails(user, getOrDeducePassword(user, passwordEncoder.getIfAvailable()));
        return new MapReactiveUserDetailsService(new UserDetails[]{userDetails});
    }

    private UserDetails getUserDetails(SecurityProperties.User user, String password) {
        List<String> roles = user.getRoles();
        return User.withUsername(user.getName()).password(password).roles(StringUtils.toStringArray(roles)).build();
    }

    private String getOrDeducePassword(SecurityProperties.User user, PasswordEncoder encoder) {
        String password = user.getPassword();
        if (user.isPasswordGenerated()) {
            logger.info(String.format("%n%nUsing generated security password: %s%n", user.getPassword()));
        }
        if (encoder != null || PASSWORD_ALGORITHM_PATTERN.matcher(password).matches()) {
            return password;
        }
        return "{noop}" + password;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$RSocketEnabledOrReactiveWebApplication.class */
    static class RSocketEnabledOrReactiveWebApplication extends AnyNestedCondition {
        RSocketEnabledOrReactiveWebApplication() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({RSocketMessageHandler.class})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$RSocketEnabledOrReactiveWebApplication$RSocketSecurityEnabledCondition.class */
        static class RSocketSecurityEnabledCondition {
            RSocketSecurityEnabledCondition() {
            }
        }

        @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$RSocketEnabledOrReactiveWebApplication$ReactiveWebApplicationCondition.class */
        static class ReactiveWebApplicationCondition {
            ReactiveWebApplicationCondition() {
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured.class */
    static final class MissingAlternativeOrUserPropertiesConfigured extends AnyNestedCondition {
        MissingAlternativeOrUserPropertiesConfigured() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnMissingClass({"org.springframework.security.oauth2.client.registration.ClientRegistrationRepository", "org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured$MissingAlternative.class */
        static final class MissingAlternative {
            MissingAlternative() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.security.user", name = {"name"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured$NameConfigured.class */
        static final class NameConfigured {
            NameConfigured() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.security.user", name = {"password"})
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/security/reactive/ReactiveUserDetailsServiceAutoConfiguration$MissingAlternativeOrUserPropertiesConfigured$PasswordConfigured.class */
        static final class PasswordConfigured {
            PasswordConfigured() {
            }
        }
    }
}
