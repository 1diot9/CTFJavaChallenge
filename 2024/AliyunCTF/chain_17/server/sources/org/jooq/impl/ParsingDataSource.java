package org.jooq.impl;

import java.sql.Connection;
import java.sql.SQLException;
import org.jooq.Configuration;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingDataSource.class */
final class ParsingDataSource extends AbstractDataSource {
    private final Configuration configuration;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParsingDataSource(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override // javax.sql.DataSource
    public final Connection getConnection() throws SQLException {
        return new ParsingConnection(this.configuration);
    }

    @Override // javax.sql.DataSource
    public final Connection getConnection(String username, String password) throws SQLException {
        return new ParsingConnection(this.configuration);
    }
}
