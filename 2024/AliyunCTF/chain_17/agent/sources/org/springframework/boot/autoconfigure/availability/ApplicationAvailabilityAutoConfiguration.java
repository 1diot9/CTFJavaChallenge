package org.springframework.boot.autoconfigure.availability;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.ApplicationAvailabilityBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/availability/ApplicationAvailabilityAutoConfiguration.class */
public class ApplicationAvailabilityAutoConfiguration {
    @ConditionalOnMissingBean({ApplicationAvailability.class})
    @Bean
    public ApplicationAvailabilityBean applicationAvailability() {
        return new ApplicationAvailabilityBean();
    }
}
