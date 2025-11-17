package org.springframework.boot.autoconfigure.graphql;

import org.springframework.graphql.execution.GraphQlSource;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/GraphQlSourceBuilderCustomizer.class */
public interface GraphQlSourceBuilderCustomizer {
    void customize(GraphQlSource.SchemaResourceBuilder builder);
}
