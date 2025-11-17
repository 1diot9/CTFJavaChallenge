package org.springframework.boot.autoconfigure.reactor.netty;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.reactor.netty")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/reactor/netty/ReactorNettyProperties.class */
public class ReactorNettyProperties {
    private Duration shutdownQuietPeriod;

    public Duration getShutdownQuietPeriod() {
        return this.shutdownQuietPeriod;
    }

    public void setShutdownQuietPeriod(Duration shutdownQuietPeriod) {
        this.shutdownQuietPeriod = shutdownQuietPeriod;
    }
}
