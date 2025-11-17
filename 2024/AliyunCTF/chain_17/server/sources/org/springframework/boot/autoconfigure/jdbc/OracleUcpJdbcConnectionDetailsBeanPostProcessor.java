package org.springframework.boot.autoconfigure.jdbc;

import java.sql.SQLException;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.springframework.beans.factory.ObjectProvider;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/OracleUcpJdbcConnectionDetailsBeanPostProcessor.class */
class OracleUcpJdbcConnectionDetailsBeanPostProcessor extends JdbcConnectionDetailsBeanPostProcessor<PoolDataSourceImpl> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public OracleUcpJdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
        super(PoolDataSourceImpl.class, connectionDetailsProvider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetailsBeanPostProcessor
    public Object processDataSource(PoolDataSourceImpl dataSource, JdbcConnectionDetails connectionDetails) {
        try {
            dataSource.setURL(connectionDetails.getJdbcUrl());
            dataSource.setUser(connectionDetails.getUsername());
            dataSource.setPassword(connectionDetails.getPassword());
            dataSource.setConnectionFactoryClassName(connectionDetails.getDriverClassName());
            return dataSource;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to set URL / user / password of datasource", ex);
        }
    }
}
