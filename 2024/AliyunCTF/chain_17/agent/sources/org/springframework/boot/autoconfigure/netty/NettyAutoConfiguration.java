package org.springframework.boot.autoconfigure.netty;

import io.netty.util.NettyRuntime;
import io.netty.util.ResourceLeakDetector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.netty.NettyProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({NettyProperties.class})
@AutoConfiguration
@ConditionalOnClass({NettyRuntime.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/netty/NettyAutoConfiguration.class */
public class NettyAutoConfiguration {
    public NettyAutoConfiguration(NettyProperties properties) {
        if (properties.getLeakDetection() != null) {
            NettyProperties.LeakDetection leakDetection = properties.getLeakDetection();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.valueOf(leakDetection.name()));
        }
    }
}
