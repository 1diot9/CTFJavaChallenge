package org.springframework.boot.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/DataSourceBuilderRuntimeHints.class */
class DataSourceBuilderRuntimeHints implements RuntimeHintsRegistrar {
    private static final List<String> TYPE_NAMES;

    DataSourceBuilderRuntimeHints() {
    }

    static {
        List<String> typeNames = new ArrayList<>();
        typeNames.add("com.mchange.v2.c3p0.ComboPooledDataSource");
        typeNames.add("org.h2.jdbcx.JdbcDataSource");
        typeNames.add("com.zaxxer.hikari.HikariDataSource");
        typeNames.add("org.apache.commons.dbcp2.BasicDataSource");
        typeNames.add("oracle.jdbc.datasource.OracleDataSource");
        typeNames.add("oracle.ucp.jdbc.PoolDataSource");
        typeNames.add("org.postgresql.ds.PGSimpleDataSource");
        typeNames.add("org.springframework.jdbc.datasource.SimpleDriverDataSource");
        typeNames.add("org.apache.tomcat.jdbc.pool.DataSource");
        TYPE_NAMES = Collections.unmodifiableList(typeNames);
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (String typeName : TYPE_NAMES) {
            hints.reflection().registerTypeIfPresent(classLoader, typeName, hint -> {
                hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            });
        }
    }
}
