package org.springframework.boot.autoconfigure.netty;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.netty")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/netty/NettyProperties.class */
public class NettyProperties {
    private LeakDetection leakDetection;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/netty/NettyProperties$LeakDetection.class */
    public enum LeakDetection {
        DISABLED,
        SIMPLE,
        ADVANCED,
        PARANOID
    }

    public LeakDetection getLeakDetection() {
        return this.leakDetection;
    }

    public void setLeakDetection(LeakDetection leakDetection) {
        this.leakDetection = leakDetection;
    }
}
