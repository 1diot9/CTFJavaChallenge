package org.springframework.boot.autoconfigure.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.ObjectProvider;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/Dbcp2JdbcConnectionDetailsBeanPostProcessor.class */
class Dbcp2JdbcConnectionDetailsBeanPostProcessor extends JdbcConnectionDetailsBeanPostProcessor<BasicDataSource> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Dbcp2JdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
        super(BasicDataSource.class, connectionDetailsProvider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetailsBeanPostProcessor
    public Object processDataSource(BasicDataSource dataSource, JdbcConnectionDetails connectionDetails) {
        dataSource.setUrl(connectionDetails.getJdbcUrl());
        dataSource.setUsername(connectionDetails.getUsername());
        dataSource.setPassword(connectionDetails.getPassword());
        dataSource.setDriverClassName(connectionDetails.getDriverClassName());
        return dataSource;
    }
}
