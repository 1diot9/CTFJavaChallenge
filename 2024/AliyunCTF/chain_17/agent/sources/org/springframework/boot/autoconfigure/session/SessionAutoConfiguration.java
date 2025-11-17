package org.springframework.boot.autoconfigure.session;

import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebSessionIdResolverAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@EnableConfigurationProperties({ServerProperties.class, SessionProperties.class, WebFluxProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class, HazelcastAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class, RedisAutoConfiguration.class, RedisReactiveAutoConfiguration.class, WebSessionIdResolverAutoConfiguration.class}, before = {HttpHandlerAutoConfiguration.class, WebFluxAutoConfiguration.class})
@ConditionalOnClass({Session.class})
@ConditionalOnWebApplication
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration.class */
public class SessionAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Import({SessionRepositoryFilterConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration.class */
    static class ServletSessionConfiguration {
        ServletSessionConfiguration() {
        }

        @Conditional({DefaultCookieSerializerCondition.class})
        @Bean
        DefaultCookieSerializer cookieSerializer(ServerProperties serverProperties, ObjectProvider<DefaultCookieSerializerCustomizer> cookieSerializerCustomizers) {
            Cookie cookie = serverProperties.getServlet().getSession().getCookie();
            DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            Objects.requireNonNull(cookie);
            PropertyMapper.Source from = map.from(cookie::getName);
            Objects.requireNonNull(cookieSerializer);
            from.to(cookieSerializer::setCookieName);
            Objects.requireNonNull(cookie);
            PropertyMapper.Source from2 = map.from(cookie::getDomain);
            Objects.requireNonNull(cookieSerializer);
            from2.to(cookieSerializer::setDomainName);
            Objects.requireNonNull(cookie);
            PropertyMapper.Source from3 = map.from(cookie::getPath);
            Objects.requireNonNull(cookieSerializer);
            from3.to(cookieSerializer::setCookiePath);
            Objects.requireNonNull(cookie);
            PropertyMapper.Source from4 = map.from(cookie::getHttpOnly);
            Objects.requireNonNull(cookieSerializer);
            from4.to((v1) -> {
                r1.setUseHttpOnlyCookie(v1);
            });
            Objects.requireNonNull(cookie);
            PropertyMapper.Source from5 = map.from(cookie::getSecure);
            Objects.requireNonNull(cookieSerializer);
            from5.to((v1) -> {
                r1.setUseSecureCookie(v1);
            });
            Objects.requireNonNull(cookie);
            PropertyMapper.Source<Integer> asInt = map.from(cookie::getMaxAge).asInt((v0) -> {
                return v0.getSeconds();
            });
            Objects.requireNonNull(cookieSerializer);
            asInt.to((v1) -> {
                r1.setCookieMaxAge(v1);
            });
            Objects.requireNonNull(cookie);
            PropertyMapper.Source as = map.from(cookie::getSameSite).as((v0) -> {
                return v0.attributeValue();
            });
            Objects.requireNonNull(cookieSerializer);
            as.to(cookieSerializer::setSameSite);
            cookieSerializerCustomizers.orderedStream().forEach(customizer -> {
                customizer.customize(cookieSerializer);
            });
            return cookieSerializer;
        }

        @Configuration(proxyBeanMethods = false)
        @ConditionalOnClass({RememberMeServices.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration$RememberMeServicesConfiguration.class */
        static class RememberMeServicesConfiguration {
            RememberMeServicesConfiguration() {
            }

            @Bean
            DefaultCookieSerializerCustomizer rememberMeServicesCookieSerializerCustomizer() {
                return cookieSerializer -> {
                    cookieSerializer.setRememberMeRequestAttribute(SpringSessionRememberMeServices.REMEMBER_ME_LOGIN_ATTR);
                };
            }
        }

        @ConditionalOnMissingBean({SessionRepository.class})
        @Configuration(proxyBeanMethods = false)
        @Import({RedisSessionConfiguration.class, JdbcSessionConfiguration.class, HazelcastSessionConfiguration.class, MongoSessionConfiguration.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ServletSessionConfiguration$ServletSessionRepositoryConfiguration.class */
        static class ServletSessionRepositoryConfiguration {
            ServletSessionRepositoryConfiguration() {
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean({ReactiveSessionRepository.class})
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Import({RedisReactiveSessionConfiguration.class, MongoReactiveSessionConfiguration.class})
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$ReactiveSessionConfiguration.class */
    static class ReactiveSessionConfiguration {
        ReactiveSessionConfiguration() {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$DefaultCookieSerializerCondition.class */
    static class DefaultCookieSerializerCondition extends AnyNestedCondition {
        DefaultCookieSerializerCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnMissingBean({HttpSessionIdResolver.class, CookieSerializer.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$DefaultCookieSerializerCondition$NoComponentsAvailable.class */
        static class NoComponentsAvailable {
            NoComponentsAvailable() {
            }
        }

        @ConditionalOnMissingBean({CookieSerializer.class})
        @ConditionalOnBean({CookieHttpSessionIdResolver.class})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/session/SessionAutoConfiguration$DefaultCookieSerializerCondition$CookieHttpSessionIdResolverAvailable.class */
        static class CookieHttpSessionIdResolverAvailable {
            CookieHttpSessionIdResolverAvailable() {
            }
        }
    }
}
