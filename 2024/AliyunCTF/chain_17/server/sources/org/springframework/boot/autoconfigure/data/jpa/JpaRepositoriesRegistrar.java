package org.springframework.boot.autoconfigure.data.jpa;

import java.lang.annotation.Annotation;
import java.util.Locale;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesRegistrar.class */
class JpaRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {
    private BootstrapMode bootstrapMode = null;

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJpaRepositories.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected Class<?> getConfiguration() {
        return EnableJpaRepositoriesConfiguration.class;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new JpaRepositoryConfigExtension();
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport
    protected BootstrapMode getBootstrapMode() {
        return this.bootstrapMode == null ? BootstrapMode.DEFAULT : this.bootstrapMode;
    }

    @Override // org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport, org.springframework.context.EnvironmentAware
    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        configureBootstrapMode(environment);
    }

    private void configureBootstrapMode(Environment environment) {
        String property = environment.getProperty("spring.data.jpa.repositories.bootstrap-mode");
        if (StringUtils.hasText(property)) {
            this.bootstrapMode = BootstrapMode.valueOf(property.toUpperCase(Locale.ENGLISH));
        }
    }

    @EnableJpaRepositories
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/jpa/JpaRepositoriesRegistrar$EnableJpaRepositoriesConfiguration.class */
    private static final class EnableJpaRepositoriesConfiguration {
        private EnableJpaRepositoriesConfiguration() {
        }
    }
}
