package org.springframework.boot.jdbc;

import javax.sql.DataSource;
import javax.sql.XADataSource;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/XADataSourceWrapper.class */
public interface XADataSourceWrapper {
    DataSource wrapDataSource(XADataSource dataSource) throws Exception;
}
