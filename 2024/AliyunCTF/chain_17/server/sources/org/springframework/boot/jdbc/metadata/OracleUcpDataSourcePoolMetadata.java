package org.springframework.boot.jdbc.metadata;

import java.sql.SQLException;
import oracle.ucp.jdbc.PoolDataSource;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/OracleUcpDataSourcePoolMetadata.class */
public class OracleUcpDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<PoolDataSource> {
    public OracleUcpDataSourcePoolMetadata(PoolDataSource dataSource) {
        super(dataSource);
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getActive() {
        try {
            return Integer.valueOf(getDataSource().getBorrowedConnectionsCount());
        } catch (SQLException e) {
            return null;
        }
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getIdle() {
        try {
            return Integer.valueOf(getDataSource().getAvailableConnectionsCount());
        } catch (SQLException e) {
            return null;
        }
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMax() {
        return Integer.valueOf(getDataSource().getMaxPoolSize());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Integer getMin() {
        return Integer.valueOf(getDataSource().getMinPoolSize());
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public String getValidationQuery() {
        return getDataSource().getSQLForValidateConnection();
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Boolean getDefaultAutoCommit() {
        String autoCommit = getDataSource().getConnectionProperty("autoCommit");
        if (StringUtils.hasText(autoCommit)) {
            return Boolean.valueOf(autoCommit);
        }
        return null;
    }
}
