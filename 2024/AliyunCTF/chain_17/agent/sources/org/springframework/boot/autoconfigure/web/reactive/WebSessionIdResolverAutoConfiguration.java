package org.springframework.boot.autoconfigure.web.reactive;

import java.util.Objects;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.server.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties({WebFluxProperties.class, ServerProperties.class})
@AutoConfiguration
@ConditionalOnClass({WebSessionManager.class, Mono.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/WebSessionIdResolverAutoConfiguration.class */
public class WebSessionIdResolverAutoConfiguration {
    private final ServerProperties serverProperties;

    public WebSessionIdResolverAutoConfiguration(ServerProperties serverProperties, WebFluxProperties webFluxProperties) {
        this.serverProperties = serverProperties;
    }

    @ConditionalOnMissingBean
    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        String cookieName = this.serverProperties.getReactive().getSession().getCookie().getName();
        if (StringUtils.hasText(cookieName)) {
            resolver.setCookieName(cookieName);
        }
        resolver.addCookieInitializer(this::initializeCookie);
        return resolver;
    }

    private void initializeCookie(ResponseCookie.ResponseCookieBuilder builder) {
        Cookie cookie = this.serverProperties.getReactive().getSession().getCookie();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        Objects.requireNonNull(cookie);
        PropertyMapper.Source from = map.from(cookie::getDomain);
        Objects.requireNonNull(builder);
        from.to(builder::domain);
        Objects.requireNonNull(cookie);
        PropertyMapper.Source from2 = map.from(cookie::getPath);
        Objects.requireNonNull(builder);
        from2.to(builder::path);
        Objects.requireNonNull(cookie);
        PropertyMapper.Source from3 = map.from(cookie::getHttpOnly);
        Objects.requireNonNull(builder);
        from3.to((v1) -> {
            r1.httpOnly(v1);
        });
        Objects.requireNonNull(cookie);
        PropertyMapper.Source from4 = map.from(cookie::getSecure);
        Objects.requireNonNull(builder);
        from4.to((v1) -> {
            r1.secure(v1);
        });
        Objects.requireNonNull(cookie);
        PropertyMapper.Source from5 = map.from(cookie::getMaxAge);
        Objects.requireNonNull(builder);
        from5.to(builder::maxAge);
        PropertyMapper.Source from6 = map.from((PropertyMapper) getSameSite(cookie));
        Objects.requireNonNull(builder);
        from6.to(builder::sameSite);
    }

    private String getSameSite(Cookie properties) {
        Cookie.SameSite sameSite = properties.getSameSite();
        if (sameSite != null) {
            return sameSite.attributeValue();
        }
        return null;
    }
}
