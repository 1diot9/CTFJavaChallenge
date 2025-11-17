package org.springframework.boot.jdbc.metadata;

import javax.sql.DataSource;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/DataSourcePoolMetadataProvider.class */
public interface DataSourcePoolMetadataProvider {
    DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource);
}
