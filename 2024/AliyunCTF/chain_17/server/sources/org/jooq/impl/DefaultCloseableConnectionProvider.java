package org.jooq.impl;

import java.sql.Connection;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultCloseableConnectionProvider.class */
class DefaultCloseableConnectionProvider extends DefaultConnectionProvider {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultCloseableConnectionProvider(Connection connection) {
        super(connection);
    }

    protected void finalize() throws Throwable {
        JDBCUtils.safeClose(this.connection);
        super.finalize();
    }
}
