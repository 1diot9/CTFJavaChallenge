package org.springframework.boot.autoconfigure.ldap;

import java.util.Collections;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DirContextAuthenticationStrategy;
import org.springframework.ldap.core.support.LdapContextSource;

@EnableConfigurationProperties({LdapProperties.class})
@AutoConfiguration
@ConditionalOnClass({ContextSource.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ldap/LdapAutoConfiguration.class */
public class LdapAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public LdapContextSource ldapContextSource(LdapProperties properties, Environment environment, ObjectProvider<DirContextAuthenticationStrategy> dirContextAuthenticationStrategy) {
        LdapContextSource source = new LdapContextSource();
        Objects.requireNonNull(source);
        dirContextAuthenticationStrategy.ifUnique(source::setAuthenticationStrategy);
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = propertyMapper.from((PropertyMapper) properties.getUsername());
        Objects.requireNonNull(source);
        from.to(source::setUserDn);
        PropertyMapper.Source from2 = propertyMapper.from((PropertyMapper) properties.getPassword());
        Objects.requireNonNull(source);
        from2.to(source::setPassword);
        PropertyMapper.Source from3 = propertyMapper.from((PropertyMapper) properties.getAnonymousReadOnly());
        Objects.requireNonNull(source);
        from3.to((v1) -> {
            r1.setAnonymousReadOnly(v1);
        });
        PropertyMapper.Source from4 = propertyMapper.from((PropertyMapper) properties.getBase());
        Objects.requireNonNull(source);
        from4.to(source::setBase);
        PropertyMapper.Source from5 = propertyMapper.from((PropertyMapper) properties.determineUrls(environment));
        Objects.requireNonNull(source);
        from5.to(source::setUrls);
        propertyMapper.from((PropertyMapper) properties.getBaseEnvironment()).to(baseEnvironment -> {
            source.setBaseEnvironmentProperties(Collections.unmodifiableMap(baseEnvironment));
        });
        return source;
    }

    @ConditionalOnMissingBean({LdapOperations.class})
    @Bean
    public LdapTemplate ldapTemplate(LdapProperties properties, ContextSource contextSource) {
        LdapProperties.Template template = properties.getTemplate();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        PropertyMapper.Source from = propertyMapper.from((PropertyMapper) Boolean.valueOf(template.isIgnorePartialResultException()));
        Objects.requireNonNull(ldapTemplate);
        from.to((v1) -> {
            r1.setIgnorePartialResultException(v1);
        });
        PropertyMapper.Source from2 = propertyMapper.from((PropertyMapper) Boolean.valueOf(template.isIgnoreNameNotFoundException()));
        Objects.requireNonNull(ldapTemplate);
        from2.to((v1) -> {
            r1.setIgnoreNameNotFoundException(v1);
        });
        PropertyMapper.Source from3 = propertyMapper.from((PropertyMapper) Boolean.valueOf(template.isIgnoreSizeLimitExceededException()));
        Objects.requireNonNull(ldapTemplate);
        from3.to((v1) -> {
            r1.setIgnoreSizeLimitExceededException(v1);
        });
        return ldapTemplate;
    }
}
