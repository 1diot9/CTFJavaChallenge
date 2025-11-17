package org.jooq.impl;

import io.r2dbc.spi.ConnectionFactory;
import org.jooq.CloseableDSLContext;
import org.jooq.ConnectionProvider;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultCloseableDSLContext.class */
public class DefaultCloseableDSLContext extends DefaultDSLContext implements CloseableDSLContext {
    public DefaultCloseableDSLContext(ConnectionProvider connectionProvider, SQLDialect dialect, Settings settings) {
        super(connectionProvider, dialect, settings);
    }

    public DefaultCloseableDSLContext(ConnectionProvider connectionProvider, SQLDialect dialect) {
        super(connectionProvider, dialect);
    }

    public DefaultCloseableDSLContext(ConnectionFactory connectionFactory, SQLDialect dialect, Settings settings) {
        super(connectionFactory, dialect, settings);
    }

    public DefaultCloseableDSLContext(ConnectionFactory connectionFactory, SQLDialect dialect) {
        super(connectionFactory, dialect);
    }

    @Override // org.jooq.CloseableDSLContext, java.lang.AutoCloseable
    public void close() {
        ConnectionProvider cp = configuration().connectionProvider();
        ConnectionFactory cf = configuration().connectionFactory();
        if (cp instanceof DefaultCloseableConnectionProvider) {
            DefaultCloseableConnectionProvider dcp = (DefaultCloseableConnectionProvider) cp;
            JDBCUtils.safeClose(dcp.connection);
            dcp.connection = null;
        }
        if (cf instanceof DefaultConnectionFactory) {
            DefaultConnectionFactory dcf = (DefaultConnectionFactory) cf;
            if (dcf.finalize) {
                R2DBC.blockWrappingExceptions(dcf.connection.close());
                dcf.connection = null;
            }
        }
    }
}
