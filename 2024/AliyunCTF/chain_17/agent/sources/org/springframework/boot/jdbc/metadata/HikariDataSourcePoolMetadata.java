package org.springframework.boot.jdbc.metadata;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.springframework.beans.DirectFieldAccessor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/HikariDataSourcePoolMetadata.class */
public class HikariDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<HikariDataSource> {
    public HikariDataSourcePoolMetadata(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getActive() {
        try {
            return Integer.valueOf(getHikariPool().getActiveConnections());
        } catch (Exception e) {
            return null;
        }
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getIdle() {
        try {
            return Integer.valueOf(getHikariPool().getIdleConnections());
        } catch (Exception e) {
            return null;
        }
    }

    private HikariPool getHikariPool() {
        return (HikariPool) new DirectFieldAccessor(getDataSource()).getPropertyValue("pool");
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMax() {
        return Integer.valueOf(getDataSource().getMaximumPoolSize());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMin() {
        return Integer.valueOf(getDataSource().getMinimumIdle());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public String getValidationQuery() {
        return getDataSource().getConnectionTestQuery();
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Boolean getDefaultAutoCommit() {
        return Boolean.valueOf(getDataSource().isAutoCommit());
    }
}
