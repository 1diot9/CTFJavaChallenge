package org.springframework.boot.autoconfigure.jooq;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.SQLDialect;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/SqlDialectLookup.class */
final class SqlDialectLookup {
    private static final Log logger = LogFactory.getLog((Class<?>) SqlDialectLookup.class);

    private SqlDialectLookup() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SQLDialect getDialect(DataSource dataSource) {
        Connection connection;
        if (dataSource != null) {
            try {
                connection = dataSource.getConnection();
            } catch (SQLException ex) {
                logger.warn("Unable to determine dialect from datasource", ex);
                return SQLDialect.DEFAULT;
            }
        } else {
            connection = null;
        }
        Connection connection2 = connection;
        try {
            SQLDialect dialect = JDBCUtils.dialect(connection2);
            if (connection2 != null) {
                connection2.close();
            }
            return dialect;
        } finally {
        }
    }
}
