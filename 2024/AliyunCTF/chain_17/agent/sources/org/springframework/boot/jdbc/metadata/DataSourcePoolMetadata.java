package org.springframework.boot.jdbc.metadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/DataSourcePoolMetadata.class */
public interface DataSourcePoolMetadata {
    Float getUsage();

    Integer getActive();

    Integer getMax();

    Integer getMin();

    String getValidationQuery();

    Boolean getDefaultAutoCommit();

    default Integer getIdle() {
        return null;
    }
}
