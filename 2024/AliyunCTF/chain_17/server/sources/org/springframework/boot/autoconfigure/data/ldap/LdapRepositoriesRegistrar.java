package org.springframework.boot.autoconfigure.data.ldap;

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.data.ldap.repository.config.LdapRepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/ldap/LdapRepositoriesRegistrar.class */
class LdapRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    LdapRepositoriesRegistrar() {
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableLdapRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableLdapRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new LdapRepositoryConfigurationExtension();
    }

    @EnableLdapRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/ldap/LdapRepositoriesRegistrar$EnableLdapRepositoriesConfiguration.class */
    private static final class EnableLdapRepositoriesConfiguration {
        private EnableLdapRepositoriesConfiguration() {
        }
    }
}
