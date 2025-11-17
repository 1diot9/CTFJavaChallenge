package org.springframework.boot.autoconfigure.jdbc;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.ObjectProvider;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jdbc/TomcatJdbcConnectionDetailsBeanPostProcessor.class */
class TomcatJdbcConnectionDetailsBeanPostProcessor extends JdbcConnectionDetailsBeanPostProcessor<DataSource> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public TomcatJdbcConnectionDetailsBeanPostProcessor(ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {
        super(DataSource.class, connectionDetailsProvider);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetailsBeanPostProcessor
    public Object processDataSource(DataSource dataSource, JdbcConnectionDetails connectionDetails) {
        dataSource.setUrl(connectionDetails.getJdbcUrl());
        dataSource.setUsername(connectionDetails.getUsername());
        dataSource.setPassword(connectionDetails.getPassword());
        dataSource.setDriverClassName(connectionDetails.getDriverClassName());
        return dataSource;
    }
}
