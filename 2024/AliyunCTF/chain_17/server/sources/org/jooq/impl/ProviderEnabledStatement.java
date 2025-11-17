package org.jooq.impl;

import java.sql.SQLException;
import java.sql.Statement;
import org.jooq.tools.jdbc.DefaultStatement;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ProviderEnabledStatement.class */
final class ProviderEnabledStatement extends DefaultStatement {
    private final ProviderEnabledConnection connection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProviderEnabledStatement(ProviderEnabledConnection connection, Statement statement) {
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
