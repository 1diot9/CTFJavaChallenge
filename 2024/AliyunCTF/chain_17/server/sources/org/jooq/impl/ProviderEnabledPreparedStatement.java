package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jooq.tools.jdbc.DefaultPreparedStatement;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ProviderEnabledPreparedStatement.class */
final class ProviderEnabledPreparedStatement extends DefaultPreparedStatement {
    private final ProviderEnabledConnection connection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProviderEnabledPreparedStatement(ProviderEnabledConnection connection, PreparedStatement statement) {
        super(statement);
        this.connection = connection;
    }

    @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement, java.lang.AutoCloseable
    public final void close() throws SQLException {
        try {
            getDelegate().close();
        } finally {
            this.connection.close();
        }
    }
}
