package org.springframework.boot.autoconfigure.couchbase;

import com.couchbase.client.java.env.ClusterEnvironment;

@FunctionalInterface
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/ClusterEnvironmentBuilderCustomizer.class */
public interface ClusterEnvironmentBuilderCustomizer {
    void customize(ClusterEnvironment.Builder builder);
}
