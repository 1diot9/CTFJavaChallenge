package org.springframework.boot.autoconfigure.data.ldap;

import javax.naming.ldap.LdapContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.ldap.repository.support.LdapRepositoryFactoryBean;

@AutoConfiguration
@ConditionalOnClass({LdapContext.class, LdapRepository.class})
@ConditionalOnMissingBean({LdapRepositoryFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.ldap.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({LdapRepositoriesRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/ldap/LdapRepositoriesAutoConfiguration.class */
public class LdapRepositoriesAutoConfiguration {
}
