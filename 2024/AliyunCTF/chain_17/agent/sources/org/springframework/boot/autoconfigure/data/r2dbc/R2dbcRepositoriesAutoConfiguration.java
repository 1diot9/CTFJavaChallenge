package org.springframework.boot.autoconfigure.data.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean;
import org.springframework.r2dbc.core.DatabaseClient;

@AutoConfiguration(after = {R2dbcDataAutoConfiguration.class})
@ConditionalOnClass({ConnectionFactory.class, R2dbcRepository.class})
@ConditionalOnMissingBean({R2dbcRepositoryFactoryBean.class})
@ConditionalOnBean({DatabaseClient.class})
@ConditionalOnProperty(prefix = "spring.data.r2dbc.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({R2dbcRepositoriesAutoConfigureRegistrar.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/data/r2dbc/R2dbcRepositoriesAutoConfiguration.class */
public class R2dbcRepositoriesAutoConfiguration {
}
