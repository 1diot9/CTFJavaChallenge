package org.springframework.boot.jdbc.metadata;

import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.DataSource;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/TomcatDataSourcePoolMetadata.class */
public class TomcatDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<DataSource> {
    public TomcatDataSourcePoolMetadata(DataSource dataSource) {
        super(dataSource);
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getActive() {
        ConnectionPool pool = getDataSource().getPool();
        return Integer.valueOf(pool != null ? pool.getActive() : 0);
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getIdle() {
        return Integer.valueOf(getDataSource().getNumIdle());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMax() {
        return Integer.valueOf(getDataSource().getMaxActive());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMin() {
        return Integer.valueOf(getDataSource().getMinIdle());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public String getValidationQuery() {
        return getDataSource().getValidationQuery();
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Boolean getDefaultAutoCommit() {
        return getDataSource().isDefaultAutoCommit();
    }
}
