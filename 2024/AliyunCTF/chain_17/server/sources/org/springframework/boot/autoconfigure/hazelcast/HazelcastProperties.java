package org.springframework.boot.autoconfigure.hazelcast;

import java.util.function.Supplier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "spring.hazelcast")
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/hazelcast/HazelcastProperties.class */
public class HazelcastProperties {
    private Resource config;

    public Resource getConfig() {
        return this.config;
    }

    public void setConfig(Resource config) {
        this.config = config;
    }

    public Resource resolveConfigLocation() {
        if (this.config == null) {
            return null;
        }
        Assert.isTrue(this.config.exists(), (Supplier<String>) () -> {
            return "Hazelcast configuration does not exist '" + this.config.getDescription() + "'";
        });
        return this.config;
    }
}
