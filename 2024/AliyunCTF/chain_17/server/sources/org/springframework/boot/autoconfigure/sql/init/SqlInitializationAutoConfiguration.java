package org.springframework.boot.autoconfigure.sql.init;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({SqlInitializationProperties.class})
@AutoConfiguration
@ConditionalOnProperty(prefix = "spring.sql.init", name = {"enabled"}, matchIfMissing = true)
@Conditional({SqlInitializationModeCondition.class})
@Import({DatabaseInitializationDependencyConfigurer.class, R2dbcInitializationConfiguration.class, DataSourceInitializationConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlInitializationAutoConfiguration.class */
public class SqlInitializationAutoConfiguration {

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlInitializationAutoConfiguration$SqlInitializationModeCondition.class */
    static class SqlInitializationModeCondition extends NoneNestedConditions {
        SqlInitializationModeCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.sql.init", name = {AdviceModeImportSelector.DEFAULT_ADVICE_MODE_ATTRIBUTE_NAME}, havingValue = "never")
        /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/sql/init/SqlInitializationAutoConfiguration$SqlInitializationModeCondition$ModeIsNever.class */
        static class ModeIsNever {
            ModeIsNever() {
            }
        }
    }
}
