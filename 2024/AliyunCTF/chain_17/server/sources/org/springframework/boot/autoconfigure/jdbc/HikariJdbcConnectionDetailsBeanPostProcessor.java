package org.springframework.boot.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/HikariJdbcConnectionDetailsBeanPostProcessor.class */
class HikariJdbcConnectionDetailsBeanPostProcessor extends JdbcConnectionDetailsBeanPostProcessor<HikariDataSource> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public HikariJdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
        super(HikariDataSource.class, connectionDetailsProvider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetailsBeanPostProcessor
    public Object processDataSource(HikariDataSource dataSource, JdbcConnectionDetails connectionDetails) {
        dataSource.setJdbcUrl(connectionDetails.getJdbcUrl());
        dataSource.setUsername(connectionDetails.getUsername());
        dataSource.setPassword(connectionDetails.getPassword());
        dataSource.setDriverClassName(connectionDetails.getDriverClassName());
        return dataSource;
    }
}
