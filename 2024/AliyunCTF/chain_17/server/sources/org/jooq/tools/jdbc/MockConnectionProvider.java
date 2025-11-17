package org.jooq.tools.jdbc;

import java.sql.Connection;
import org.jooq.ConnectionProvider;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockConnectionProvider.class */
public class MockConnectionProvider implements ConnectionProvider {
    private final ConnectionProvider delegate;
    private final MockDataProvider provider;

    public MockConnectionProvider(ConnectionProvider delegate, MockDataProvider provider) {
        this.delegate = delegate;
        this.provider = provider;
    }

    @Override // org.jooq.ConnectionProvider
    public final Connection acquire() {
        return new MockConnectionWrapper(this.delegate.acquire());
    }

    @Override // org.jooq.ConnectionProvider
    public final void release(Connection connection) {
        if (connection instanceof MockConnectionWrapper) {
            MockConnectionWrapper w = (MockConnectionWrapper) connection;
            this.delegate.release(w.connection);
            return;
        }
        throw new IllegalArgumentException("Argument connection must be a MockConnectionWrapper");
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/tools/jdbc/MockConnectionProvider$MockConnectionWrapper.class */
    private class MockConnectionWrapper extends MockConnection {
        final Connection connection;

        public MockConnectionWrapper(Connection connection) {
            super(MockConnectionProvider.this.provider);
            this.connection = connection;
        }
    }
}
